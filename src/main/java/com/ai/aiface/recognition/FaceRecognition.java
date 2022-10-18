package com.ai.aiface.recognition;

import com.ai.aiface.detection.RetinaFaceDetection;
import com.ai.aiface.train.FaceClassificationTrain;
import com.ai.aiface.train.FaceNet;
import com.ai.aiface.feature.FeatureExtraction;
import com.ai.aiface.img.JavaImgTools;
import com.ai.aiface.util.Result;
import com.alibaba.fastjson.JSONObject;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.File;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

/**
 * @title: FaceAcquisition 人脸采集 - 模型训练 - 人脸识别
 * @Author nyl
 * @Date: 2022/10/18 12:12
 * @Version 1.0
 */
public class FaceRecognition {

    private String path="D:\\img\\faces";

    private String netPath = "models/face_net.xm";

    /**
     * 将采集的图片保存到 img/face/userId/下面
     * @param img base64 图片
     * @param userId 用户ID作为文件夹名称 保持唯一
     */
    public Result UserImgSave(String img,String userId){
        // 拼接保存文件路径及使用时间戳作为文件名称
        String savePath = path + "\\" + userId + "\\" + System.currentTimeMillis() + ".png";
        String filePath = path + "\\" + userId;
        //创建用户文件夹
        File file = new File(filePath);
        if (!file.exists()){
            file.mkdirs();
        }
        System.out.println("保存路径："+savePath);
        // 将base64转换为mat
        Mat mat = JavaImgTools.base64ImgToMat(img);
        // 人脸定位
        List<Mat> faces = RetinaFaceDetection.faceDetection(mat);
        // 保存人脸图片至指定文件路径 说明: 限制至单个人脸
        if(faces.size()==1){
            for(Mat face:faces) {
                // 输出图片
                imwrite(savePath, face);
                return Result.success("采集正常");
            }
        }else{
            System.out.println("检测的人脸数量异常");
            return Result.fail5("检测的人脸数量异常");
        }
        return Result.fail5("采集人脸图片异常");
    }

    /**
     * 人脸收集图片后对图片进行训练
     * @param model 训练模型
     * @param userId 用ID的文件夹
     */
    public Result TrainFaceNet(String model,String userId){
        String faceNetPath = netPath;
        String trainPath = path + "\\" + userId;
        System.out.println("faceNetPath="+faceNetPath+",trainPath="+trainPath);
        //开始训练
        System.out.println("***** 开始训练 ***** ");
        FaceClassificationTrain.training(null , trainPath, faceNetPath,userId);
        System.out.println("***** 训练完成 ***** ");
        return Result.success("已生成人脸模型");

    }

    /**
     * 人脸识别比对  设置相似度为 0.85以上为识别成功
     * @param img base64图片
     */
    public Result Recognition(String img){
        FaceNet faceNet = FaceNet.load(netPath);
        // 将base64转换为mat
        Mat mat = JavaImgTools.base64ImgToMat(img);
        // 人脸定位
        List<Mat> faces = RetinaFaceDetection.faceDetection(mat);
        //特征提取
        if(faces != null && faces.size() > 0) {
            for(Mat face:faces) {
                float[] f = FeatureExtraction.faceFeatureExtraction(face);
                if(f != null) {
                    //lab推理
                    FaceNet.MaxLab maxLab = faceNet.predict(f);
                    System.out.println("lab=" + maxLab.maxLab + "  相似度=" + maxLab.maxSimilar);
                    JSONObject jsonObject = new JSONObject();
                    if(maxLab.maxSimilar < 0.85){
                        return Result.fail4("人脸识别失败。如您已采集过人脸，请调整摄像头后重试;如未采集人脸信息，请采集后在识别");
                    }
                    jsonObject.put("userid",maxLab.maxLab);
                    jsonObject.put("similar",maxLab.maxSimilar * 100 + "%");
                    return Result.success(jsonObject);
                }
            }
        }
        return Result.fail5("人脸识别失败");
    }

}
