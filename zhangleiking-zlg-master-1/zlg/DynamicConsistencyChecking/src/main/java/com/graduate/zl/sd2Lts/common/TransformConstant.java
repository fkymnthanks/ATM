package com.graduate.zl.sd2Lts.common;

import com.graduate.zl.common.util.PropertiesAccess;

import java.util.Map;

/**
 * 使用单例模式来保存config.properties配置信息
 */
public class TransformConstant {
    private static class Instance{
        private static final Map<String, String> transformConf = PropertiesAccess.getAllProperties("transform.properties");
    }

    public static Map<String, String> getTransformConf() {
        return Instance.transformConf;
    }
}
