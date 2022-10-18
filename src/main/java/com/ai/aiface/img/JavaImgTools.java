package com.ai.aiface.img;

import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;

/**
 * @title: JavaImgTools 图片转换工具类
 * @Author nyl
 * @Date: 2022/10/18 13:44
 * @Version 1.0
 */
public class JavaImgTools {

    private static String type = "png";

    /**
     * Mat图片转换base64
     * @param src
     * @return
     */
    public static String matToBase64(Mat src) {
        ByteBuffer bu = ByteBuffer.allocate(1024*1024);
        opencv_imgcodecs.imencode(".png", src, bu);
        byte[] data = bu.array();
        bu.clear();

        try {
            String png_base64 = Base64.getEncoder().encodeToString(data);//转换成base64串
            return TollUtil.imgsBase(type) + png_base64.trim();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 将base64图片 转换成mat
     * @param baseImg
     * @return
     */
    public static Mat base64ImgToMat(String baseImg) {
        ByteArrayInputStream in = null;
        try {
            byte[] data = Base64.getDecoder().decode(baseImg.substring(baseImg.lastIndexOf(",") + 1));

            //byte[] to mat
            Mat src = new Mat(data);
            Mat m = opencv_imgcodecs.imdecode(src, opencv_imgcodecs.IMREAD_UNCHANGED);//CV_LOAD_IMAGE_UNCHANGED
            src.close();

            if(m != null && !m.empty()) {
                //转换成3通道RBG
                opencv_imgproc.cvtColor(m, m, opencv_imgproc.COLOR_BGRA2BGR);

                return m;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        return null;
    }
}
