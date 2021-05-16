package com.graduate.zl.traceability.callGraph.handle;

import com.graduate.zl.traceability.common.LocConfConstant;
import com.graduate.zl.traceability.ir.CodeInfo;
import lombok.Getter;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 预处理
 */
public class PreHandleCG {

    private Map<String, String> locConf;

    private String methodCallFilePath;

    @Getter
    private Map<String, List<String>> methodCallMap;

    @Getter
    private Map<String, Integer> methodCallNodes;

    private AtomicInteger count = new AtomicInteger(1);

    private CodeInfo codeInfo;

    private Set<String> checkedClasses;

    private void init() {
        this.codeInfo = CodeInfo.getInstance();
        this.locConf = LocConfConstant.getLocConf();
        int proCase = Integer.parseInt(this.locConf.get("proCase"));
        if(proCase == 1) {
            this.methodCallFilePath = this.locConf.get("methodCallFilePath") + this.locConf.get("methodCallFileNameOfATM");
        } else if(proCase == 2) {
            this.methodCallFilePath = this.locConf.get("methodCallFilePath") + this.locConf.get("methodCallFileNameOfOMH");
        }
        this.methodCallMap = new HashMap<>();
        this.methodCallNodes = new HashMap<>();
        this.checkedClasses = new HashSet<>();
        setCheckedClasses();
    }

    private void setCheckedClasses() {
        for(String packageName : this.codeInfo.getPackageMapClazzs().keySet()) {
            for(String className : this.codeInfo.getPackageMapClazzs().get(packageName)) {
                this.checkedClasses.add(packageName+"."+className);
            }
        }
        for(String className : this.codeInfo.getClazzMapInnerClass().keySet()) {
            for(String innerClassName : this.codeInfo.getClazzMapInnerClass().get(className)) {
                this.checkedClasses.add(innerClassName);
            }
        }
    }

    public PreHandleCG() {
        init();
        preHandleCG();
    }

    /**
     * 预处理Call Graph信息，过滤依赖包的方法调用
     */
    public void preHandleCG() {
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(this.methodCallFilePath);
            br = new BufferedReader(fr);
            String logContent;
            while ((logContent = br.readLine()) != null) {

                String methodCaller = logContent.split("CALL")[0].trim();
                String methodCallee = logContent.split("CALL")[1].trim();
                if(this.checkedClasses.contains(methodCallee.split(":")[0]) && this.checkedClasses.contains(methodCaller.split(":")[0])) {
                    if(!this.methodCallMap.containsKey(methodCaller)) {
                        this.methodCallMap.put(methodCaller, new ArrayList<>());
                    }
                    if(!this.methodCallMap.get(methodCaller).contains(methodCallee)) {
                        this.methodCallMap.get(methodCaller).add(methodCallee);
                    }
                    if(!this.methodCallNodes.containsKey(methodCaller)) {
                        this.methodCallNodes.put(methodCaller, count.getAndIncrement());
                    }
                    if(!this.methodCallNodes.containsKey(methodCallee)) {
                        this.methodCallNodes.put(methodCallee, count.getAndIncrement());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PreHandleCG ph = new PreHandleCG();
        for(String key : ph.checkedClasses) {
            System.out.println(key);
        }
        for(String key : ph.getMethodCallNodes().keySet()) {
            System.out.println(key+": "+ph.getMethodCallNodes().get(key));
        }
    }
}