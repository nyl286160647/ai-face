package com.ai.aiface.train;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import com.ai.aiface.feature.FeatureExtraction;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @title: FaceClassificationTrain 人脸分类训练得出 FaceNet 模型网络
 * @Author nyl
 * @Date: 2022/10/18 16:10
 * @Version 1.0
 */
public class FaceClassificationTrain {
    /**
     * 训练
     * @param model 可以在此模型文件基础上迭代训练
     * @param trainPath 人脸训练数据根文件夹
     * @param faceNetPath 训练完成后的face net存储文件路径
     */
    public synchronized static void training(String model, String trainPath, String faceNetPath,String userid) {
        FaceNet faceNet = null;
        if(model == null) {
            faceNet = new FaceNet();
        }else {
            faceNet = FaceNet.load(model);
        }
        //分类训练
        dataSetByUserid(trainPath, faceNet,userid);
        //保存网络模型
        faceNet.saveNet(faceNetPath);
    }

    /**
     * 用face_feature模型对人脸图片分类训练 对单个用户（分类训练模型）
     * @param trainPath 训练的图片路径
     * @param faceNet 加载训练模型
     */
    private static void dataSetByUserid(String trainPath, FaceNet faceNet,String userid) {
        File data = new File(trainPath);
        if(data.isDirectory()) {
            String labName = data.getName();
            System.out.println("labName=" + labName );
            loadData(faceNet, data, labName);
        }else {
            new TrainFaceClassificationException("Path:" + trainPath + " is not data directory.");
        }
    }

    /**
     * 用face_feature模型对人脸图片分类训练 所有用户全部训练
     * @param trainPath 训练的图片路径
     * @param faceNet 加载训练模型
     */
    private static void dataSet(String trainPath, FaceNet faceNet) {
        int i = 0;
        File data = new File(trainPath);
        if(data.isDirectory()) {
            File[] labs = data.listFiles();
            if(labs != null && labs.length > 0) {
                for(File lab:labs) {
                    if(lab.isDirectory()) {
                        i++;
                        String labName = lab.getName();
                        System.out.println("labName=" + labName + "  i=" + i);
                        loadData(faceNet, lab, labName);
                    }
                }
            }else {
                new TrainFaceClassificationException("Path:" + trainPath + " is not empty.");
            }
        }else {
            new TrainFaceClassificationException("Path:" + trainPath + " is not data directory.");
        }
    }

    /**
     * 提取每个文件的特征值加入训练文件
     * @param faceNet
     * @param lab
     * @param labName
     */
    private static void loadData(FaceNet faceNet, File lab, String labName) {
        File[] faces = lab.listFiles();
        if(faces != null && faces.length > 0) {
            for(File faceF:faces) {
                Path facePath = Paths.get(faceF.getAbsolutePath());
                try {
                    Image img = ImageFactory.getInstance().fromFile(facePath);
                    float[] feature = FeatureExtraction.faceFeatureExtraction(img);
                    faceNet.addFeature(labName, feature);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
