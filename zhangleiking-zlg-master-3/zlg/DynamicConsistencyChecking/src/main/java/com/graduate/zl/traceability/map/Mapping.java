package com.graduate.zl.traceability.map;

import com.graduate.zl.sd2Lts.common.TransformConstant;
import com.graduate.zl.traceability.callGraph.handle.CallDistance;
import com.graduate.zl.traceability.common.LocConfConstant;
import com.graduate.zl.traceability.ir.InformationRetrieval;
import com.graduate.zl.traceability.ir.ModelInfo;
import lombok.Getter;

import java.io.*;
import java.util.*;

/**
 * 映射
 */
public class Mapping {

    private Map<String, String> locConf;

    private Map<String, String> transConf;

    private String locationResultFilePath;

    private String mappingResultFilePath;

    private InformationRetrieval ir;

    private CallDistance cd;

    @Getter
    private List<String> modelObjectList;

    @Getter
    //模型中元素发起的消息
    private Map<String, List<String>> modelObjRelatedMsg;

    @Getter
    //定位的结果
    private Set<String> locationResult;

    @Getter
    //映射的结果
    private Map<String, List<String>> mappingResult;

    private void init() {
        this.locConf = LocConfConstant.getLocConf();
        this.transConf = TransformConstant.getTransformConf();
        int proCase = Integer.parseInt(this.locConf.get("proCase"));
        if(proCase == 1) {
            this.locationResultFilePath = this.locConf.get("locationResultFilePath")+this.locConf.get("locationResultFileNameOfATM");
            this.mappingResultFilePath = this.locConf.get("mappingResultFilePath")+this.locConf.get("mappingResultFileNameOfATM");
        } else if(proCase == 2) {
            this.locationResultFilePath = this.locConf.get("locationResultFilePath") + this.locConf.get("locationResultFileNameOfOMH");
            this.mappingResultFilePath = this.locConf.get("mappingResultFilePath")+this.locConf.get("mappingResultFileNameOfOMH");
        }
        this.ir = InformationRetrieval.getInstance();
        this.cd = CallDistance.getInstance();
        this.modelObjectList = this.ir.getModelInfo().getObjectNameList();
        this.mappingResult = new HashMap<>();
        this.locationResult = new HashSet<>();
        setLocationResult();
        this.modelObjRelatedMsg = this.ir.getModelObjRelatedMsg();
    }

    private void setLocationResult() {
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(this.locationResultFilePath);
            br = new BufferedReader(fr);
            String content;
            while((content = br.readLine()) != null) {
                this.locationResult.add(content);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Mapping() {
        init();
        process();
    }

    public void process() {
        mappingHandle();
        recordMappingResult();
    }

    private void mappingHandle() {
        //先添加信息检索的结果，依靠model对象名和消息名
//        for(String objName : this.ir.getModelObjRelatedPackage().keySet()) {
//            List<String> relatedPackageList = this.ir.getModelObjRelatedPackage().get(objName);
//            if(relatedPackageList != null) {
//                int maxMatch = Integer.MIN_VALUE;
//                String bestMatch = null;
//                for(String relatedPackage : relatedPackageList) {
//                    int tmp = CommonFunc.longestCommonSubstring(relatedPackage.replace(".", ""), objName.replace(" ", ""));
//                    if(tmp > maxMatch) {
//                        maxMatch = tmp;
//                        bestMatch = relatedPackage;
//                    }
//                }
//                if(bestMatch != null) {
//                    if(!this.mappingResult.containsKey(objName)) {
//                        this.mappingResult.put(objName, new ArrayList<>());
//                    }
//                    List<String> clazzes = this.ir.getCodeInfo().getPackageMapClazzs().get(bestMatch);
//                    if(clazzes != null) {
//                        for(String clazz : clazzes) {
//                            String tmp = bestMatch+"."+clazz;
//                            // mapping的结果需要基于locate的结果
//                            if(this.locationResult.contains(tmp)) {
//                                this.mappingResult.get(objName).add(tmp);
//                            }
//                        }
//                    }
//                }
//            }
//        }
        for(String objName : this.ir.getModelObjRelatedClass().keySet()) {
            List<String> relatedClassList = this.ir.getModelObjRelatedClass().get(objName);
            for(String clazz : relatedClassList) {
                if(!this.getMappingResult().containsKey(objName)) {
                    this.getMappingResult().put(objName, new ArrayList<>());
                }
                if(!this.getMappingResult().get(objName).contains(clazz)) {
                    if(this.locationResult.contains(clazz)) {
                        this.getMappingResult().get(objName).add(clazz);
                        System.out.println("1: "+objName+", "+clazz);
                    }
                }
            }
        }

        for(String objName : this.ir.getModelObjRelatedMethod().keySet()) {
            List<String> relatedMethodList = this.ir.getModelObjRelatedMethod().get(objName);
            for(String fullMethod : relatedMethodList) {
                String clazz = fullMethod.split(":")[0];
                if(!this.getMappingResult().containsKey(objName)) {
                    this.getMappingResult().put(objName, new ArrayList<>());
                }
                if(!this.getMappingResult().get(objName).contains(clazz)) {
                    if(this.locationResult.contains(clazz)) {
                        this.getMappingResult().get(objName).add(clazz);
                        System.out.println("2: "+objName+", "+clazz);
                    }
                }
            }
        }

        for(String objName : ModelInfo.getInstance().getObjectNameList()) {
            List<String> modelRelatedMsg = this.modelObjRelatedMsg.get(objName);
            for(String msgName : modelRelatedMsg) {
                List<String> relatedMethodList = this.ir.getModelMsgRelatedMethod().get(msgName);
                if(relatedMethodList != null) {
                    for(String fullMethod : relatedMethodList) {
                        String clazz = fullMethod.split(":")[0];
                        if(!this.getMappingResult().containsKey(objName)) {
                            this.getMappingResult().put(objName, new ArrayList<>());
                        }
                        if(!this.getMappingResult().get(objName).contains(clazz)) {
                            if(this.locationResult.contains(clazz)) {
                                this.getMappingResult().get(objName).add(clazz);
                                System.out.println("3: "+objName+", "+clazz);
                            }
                        }
                    }
                }
            }
        }

        //利用call graph进行补充
        for(String objName : this.getMappingResult().keySet()) {
            List<String> relatedClasses = this.getMappingResult().get(objName);
            List<String> relatedClassesCopy = new ArrayList<>(relatedClasses);
            for(String relatedClass : relatedClassesCopy) {
                if(this.ir.getCodeInfo().getClazzMapMethods().containsKey(relatedClass)) {
                    List<String> methodsOfClass = this.ir.getCodeInfo().getClazzMapMethods().get(relatedClass); // 此处的方法名不包括类名，因此下面要用className与MethodName拼凑
                    for(String methodName : methodsOfClass) {
                        List<String> cdRelatedMethods = this.cd.getRelatedMethodsForMapping(relatedClass+":"+methodName);
                        if(cdRelatedMethods == null)
                            continue;
                        for(String relatedMethod : cdRelatedMethods) {
                            if(!this.getMappingResult().containsKey(objName)) {
                                this.getMappingResult().put(objName, new ArrayList<>());
                            }
                            String clazz = relatedMethod.split(":")[0];
                            if(!this.getMappingResult().get(objName).contains(clazz)) {
                                if(this.locationResult.contains(clazz)) {
                                    this.getMappingResult().get(objName).add(clazz);
                                }
                            }
                        }
                    }
                }
            }
        } //call graph

    }

    private void recordMappingResult() {
        File mappingFile = new File(this.mappingResultFilePath);
        FileWriter writer = null;
        BufferedWriter br = null;
        try {
            if(!mappingFile.exists()) {
                mappingFile.createNewFile();
            }
            writer = new FileWriter(mappingFile);
            br = new BufferedWriter(writer);
            for(String objName : this.getMappingResult().keySet()) {
                br.write("<"+objName+">"+"\r\n");
                List<String> relatedClasses = this.getMappingResult().get(objName);
                for(String clazz : relatedClasses) {
                    br.write(clazz+"\r\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("The result of mapping is done!");
    }


    public static void main(String[] args) {
        Mapping mapping = new Mapping();

        for (String objName : mapping.getMappingResult().keySet()) {
            System.out.println("modelObject: " + objName);
            for(String clazz : mapping.getMappingResult().get(objName)) {
                System.out.println(clazz);
            }
        }

        for(String objName : mapping.getLocationResult()) {
            System.out.println("objName: "+objName);
        }
    }
}
