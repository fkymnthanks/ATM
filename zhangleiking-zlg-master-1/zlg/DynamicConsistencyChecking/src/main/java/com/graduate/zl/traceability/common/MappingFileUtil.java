package com.graduate.zl.traceability.common;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提取mapping结果
 */
public class MappingFileUtil {

    private static Map<String, String> locConf = LocConfConstant.getLocConf();

    public static Map<String, List<String>> getMapping() {
        Map<String, List<String>> ret = new HashMap<>();
        String path = locConf.get("mappingResultFilePath")+locConf.get("mappingResultFileName");
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            String content;
            String objName = null;
            while((content = br.readLine()) != null) {
                if(content.contains("<") && content.contains(">")) {
                    objName = content.substring(1, content.length()-1);
                    if(!ret.containsKey(objName)) {
                        ret.put(objName, new ArrayList<>());
                    }
                }else {
                    ret.get(objName).add(content);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void main(String[] args) {
        Map<String, List<String>> mm = getMapping();
        for(String objName : mm.keySet()) {
            System.out.println("objName: "+objName);
            for(String clazz : mm.get(objName)) {
                System.out.println(clazz);
            }
        }
    }
}
