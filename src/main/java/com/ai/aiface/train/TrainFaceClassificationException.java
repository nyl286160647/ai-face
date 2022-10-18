package com.ai.aiface.train;

/**
 * @title: TrainFaceClassificationException 人脸模型训练异常
 * @Author nyl
 * @Date: 2022/10/18 16:16
 * @Version 1.0
 */
public class TrainFaceClassificationException extends RuntimeException {

    public TrainFaceClassificationException() {}

    public TrainFaceClassificationException(String msg) {
        super(msg);
    }
}
