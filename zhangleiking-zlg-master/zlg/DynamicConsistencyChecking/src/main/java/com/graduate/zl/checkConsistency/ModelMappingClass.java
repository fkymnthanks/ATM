package com.graduate.zl.checkConsistency;

import com.graduate.zl.traceability.common.LocConfConstant;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取模型元素与代码类之间的映射关系
 */
public class ModelMappingClass {

    private static String mappingFileFullPath;

    private static Map<String, String> locConf;

    static {
        locConf = LocConfConstant.getLocConf();
        int proCase = Integer.parseInt(locConf.get("proCase"));
        if(proCase == 1) {
            mappingFileFullPath = locConf.get("mappingResultFilePath")+ locConf.get("mappingResultFileNameOfATM");
        } else if(proCase == 2) {
            mappingFileFullPath = locConf.get("mappingResultFilePath")+ locConf.get("mappingResultFileNameOfOMH");
        }
    }

    /**
     * 从文件里去除元素模型与类的映射关系，存于map中
     * @return
     */
    public static Map<String, List<String>> getModelMappingClass() {
        Map<String, List<String>> ret = new HashMap<>();
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(mappingFileFullPath);
            br = new BufferedReader(fr);
            String logContent;
            String target = null;
            while ((logContent = br.readLine()) != null) {
                if(logContent.contains("<") && logContent.contains(">")) {
                    target = logContent.substring(1, logContent.length()-1);
                }else {
                   if(!ret.containsKey(target)) {
                       ret.put(target, new ArrayList<>());
                   }
                   ret.get(target).add(logContent);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void main(String[] args) {
        Map<String, List<String>> ret = getModelMappingClass();
        for(String objName : ret.keySet()) {
            System.out.println("objName: "+objName);
            List<String> classNames = ret.get(objName);
            for(String className : classNames) {
                System.out.println(className);
            }
        }
    }
}
