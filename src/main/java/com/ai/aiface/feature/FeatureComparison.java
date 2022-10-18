package com.ai.aiface.feature;

/**
 * @title: FeatureComparison 欧几里得算法计算相似度
 * @Author nyl
 * @Date: 2022/10/18 16:19
 * @Version 1.0
 */
public class FeatureComparison {
    private FeatureComparison() {}

    /**
     * cosin相似度比较(cpu版本)
     * @param feature1
     * @param feature2
     * @return 返回相似度比例
     */
    public static float calculSimilar(float[] feature1, float[] feature2) {
        float ret = 0.0f;
        float mod1 = 0.0f;
        float mod2 = 0.0f;
        int length = feature1.length;
        for (int i = 0; i < length; ++i) {
            ret += feature1[i] * feature2[i];
            mod1 += feature1[i] * feature1[i];
            mod2 += feature2[i] * feature2[i];
        }
        return (float) ((ret / Math.sqrt(mod1) / Math.sqrt(mod2) + 1) / 2.0f);
    }
}
