package com.ai.aiface.feature;

import ai.djl.MalformedModelException;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.beans.factory.annotation.Value;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @title: FeatureExtraction 人脸特征值提取
 * @Author nyl
 * @Date: 2022/10/18 16:21
 * @Version 1.0
 */
public class FeatureExtraction {

    private static ZooModel<Image, float[]> model = null;

    private static String model_path = "models/face_feature.zip";

    private FeatureExtraction() {}


    static {
        /**
         * 初始化模型
         */
        Criteria<Image, float[]> criteria =
                Criteria.builder()
                        .setTypes(Image.class, float[].class)
                        .optModelUrls(model_path)
                        .optModelName("face_feature") // specify model file prefix
                        .optTranslator(new FaceFeatureTranslator())
                        .optProgress(new ProgressBar())
                        .optEngine("PyTorch") // Use PyTorch engine
                        .build();
        try {
            model = ModelZoo.loadModel(criteria);
        } catch (ModelNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedModelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 人脸特征提取(IMAGE)
     * @param img 人脸区域图,建议w h 等比例的灰度图
     * @return 长度为512的特征一维数组
     * @throws IOException
     * @throws ModelException
     * @throws TranslateException
     */
    public static float[] faceFeatureExtraction(Image img) {
        Predictor<Image, float[]> predictor = model.newPredictor();
        float[] faceFeature = null;
        try {
            faceFeature = predictor.predict(img);
        } catch (TranslateException e) {
            e.printStackTrace();
        }finally {
            //关闭预测者
            predictor.close();
        }

        return faceFeature;
    }

    /**
     * 人脸特征提取（MAT）
     * @param img 人脸区域图,建议w h 等比例的灰度图
     * @return 长度为512的特征一维数组
     * @throws IOException
     * @throws ModelException
     * @throws TranslateException
     */


    public static float[] faceFeatureExtraction(Mat img) {
        // 转换成BufferedImage
        OpenCVFrameConverter.ToMat matConv = new OpenCVFrameConverter.ToMat();
        Java2DFrameConverter biConv = new Java2DFrameConverter();
        Frame frame = matConv.convert(img);
        BufferedImage bimg = biConv.getBufferedImage(frame);

        // 释放
        frame.close();
        biConv.close();
        matConv.close();


        // 转换Image
        Image djl_img = ImageFactory.getInstance().fromImage(bimg);

        Predictor<Image, float[]> predictor = model.newPredictor();
        float[] faceFeature = null;
        try {
            faceFeature = predictor.predict(djl_img);
        } catch (TranslateException e) {
            e.printStackTrace();
        }finally {
            //关闭预测者
            predictor.close();
        }

        return faceFeature;
    }
}
