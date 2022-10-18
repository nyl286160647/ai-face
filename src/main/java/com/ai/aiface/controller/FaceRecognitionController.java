package com.ai.aiface.controller;

import com.ai.aiface.recognition.FaceRecognition;
import com.ai.aiface.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @title: FaceRecognition ai-face 人脸采集、模型训练、人脸比对
 * @Author nyl
 * @Date: 2022/10/18 18:15
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class FaceRecognitionController {

    /**
     * 人脸采集
     * @param img base64
     * @param userId 用户ID
     * @return
     */
    @RequestMapping(value = "UserImgSave",method = RequestMethod.POST)
    public Result UserImgSave(@RequestParam("img") String img, @RequestParam("userId") String userId){
        log.info("img:"+img +",userid:"+userId);
        FaceRecognition faceRecognition = new FaceRecognition();
        return faceRecognition.UserImgSave(img,userId);
    }

    /**
     * 模型训练
     * @param userId 用户ID
     * @return
     */
    @RequestMapping(value = "TrainFaceNet",method = RequestMethod.GET)
    public Result TrainFaceNet(String userId){
        FaceRecognition faceRecognition = new FaceRecognition();
        return faceRecognition.TrainFaceNet(null,userId);
    }

    /**
     * 人脸比对
     * @param img base64
     * @return
     */
    @RequestMapping(value = "Recognition",method = RequestMethod.POST)
    public Result Recognition(@RequestParam("img") String img) {
        String libPath = System.getenv("PYTORCH_LIBRARY_PATH");
        System.out.println(libPath);
        FaceRecognition faceRecognition = new FaceRecognition();
        return faceRecognition.Recognition(img);
    }

}
