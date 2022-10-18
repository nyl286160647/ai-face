package com.ai.aiface.detection;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.DetectedObjects.DetectedObject;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point2f;
import org.bytedeco.opencv.opencv_core.Size;
import org.springframework.beans.factory.annotation.Value;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 * @title: RetinaFaceDetection 人脸检测模型
 * @Author nyl
 * @Date: 2022/10/18 11:56
 * @Version 1.0
 */
public class RetinaFaceDetection {

    private static ZooModel<Image, DetectedObjects> model = null;

    private static String model_path = "models/retinaface.zip";

    private static double confThresh = 0.85f;

    static {
        /**
         * 初始化人脸检测模型
         */
        System.out.println("模型文件路径："+model_path);
        double nmsThresh = 0.45f;
        double[] variance = {0.1f, 0.2f};
        int topK = 5000;
        int[][] scales = {{16, 32}, {64, 128}, {256, 512}};
        int[] steps = {8, 16, 32};
        FaceDetectionTranslator translator =
                new FaceDetectionTranslator(confThresh, nmsThresh, variance, topK, scales, steps);

        Criteria<Image, DetectedObjects> criteria =
                Criteria.builder()
                        .setTypes(Image.class, DetectedObjects.class)
                        .optModelUrls(model_path)
                        // Load model from local file, e.g:
                        .optModelName("retinaface") // specify model file prefix
                        .optTranslator(translator)
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
     * 人脸检测-采集人脸
     * @param mat_img
     * @return 返回人脸矩形，高宽等比例的截图
     */
    public static List<Mat> faceDetection(Mat mat_img) {
        // 转换成BufferedImage
        BufferedImage bimg = Java2DFrameUtils.toBufferedImage(mat_img);
        // 转换Image
        Image djl_img = ImageFactory.getInstance().fromImage(bimg);

        List<Mat> faces = new ArrayList();
        Predictor<Image, DetectedObjects> predictor = model.newPredictor();
        try {
            DetectedObjects detection = predictor.predict(djl_img);

            for(int i = 0; i < detection.getNumberOfObjects(); i++) {
                DetectedObject box = detection.item(i);
                double similar = box.getProbability();
                double x = djl_img.getWidth() * box.getBoundingBox().getBounds().getX();
                double y = djl_img.getHeight() * box.getBoundingBox().getBounds().getY();
                double w = djl_img.getWidth() * box.getBoundingBox().getBounds().getWidth();
                double h = djl_img.getHeight() * box.getBoundingBox().getBounds().getHeight();

                if(w != h) {
                    //需要做等比例
                    if(h > w) {
                        double xd = (h - w) / 2;
                        w = h;
                        x = x - xd;
                    }else {
                        double yd = w - h;
                        h = w;
                        y = y - yd;
                    }
                }

                //截取人脸区域
                if(mat_img != null) {
                    //mat上截取人脸
                    Mat face = new Mat();
                    double size = w>h?w:h;
                    getRectSubPix(mat_img, new Size((int)size, (int)size), new Point2f((int) (x + w/2), (int) (y + h/2)), face);
                    faces.add(face);
                }

            }

        } catch (TranslateException e) {
            e.printStackTrace();
        }

        //关闭预测者
        predictor.close();

        return faces;
    }
}
