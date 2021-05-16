package com.graduate.zl.common.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

/**
 * 配置信息获取工具类
 */
public class PropertiesAccess {

    public static HashMap<String, String> getAllProperties(String filePath){
        String realPath = PropertiesAccess.class.getClassLoader().getResource(filePath).getPath();
        HashMap<String, String> ret = new HashMap<>();
        Properties properties = new Properties();
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(realPath));
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Enumeration en = properties.propertyNames();
        while(en.hasMoreElements()) {
            String key = (String) en.nextElement();
            String value = properties.getProperty(key);
            ret.put(key, value);
        }
        return ret;
    }
}
