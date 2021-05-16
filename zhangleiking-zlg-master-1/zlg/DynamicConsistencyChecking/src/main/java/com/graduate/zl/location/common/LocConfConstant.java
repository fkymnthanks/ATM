package com.graduate.zl.location.common;

import com.graduate.zl.common.util.PropertiesAccess;

import java.util.Map;

/**
 * 使用单例模式来保存配置信息
 */
public class LocConfConstant {

    private static class Instance{
        private static final Map<String, String> locConf = PropertiesAccess.getAllProperties("location.properties");
    }

    public static Map<String, String> getLocConf() {
        return Instance.locConf;
    }
}
