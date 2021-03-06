package com.graduate.zl.traceability;

import com.graduate.zl.traceability.callGraph.codeParse.CallGraphMainEntry;
import com.graduate.zl.traceability.common.LocConfConstant;
import com.graduate.zl.traceability.location.Locate;
import com.graduate.zl.traceability.map.Mapping;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

/**
 * 跟踪过程
 */
public class TracebilityMain {

    private CallGraphMainEntry callGraphMainEntry;

    private Map<String, String> locConf;

    private String methodCallFilePath;

    private Locate locate;

    private Mapping mapping;

    private void init() {
        this.locConf = LocConfConstant.getLocConf();
        this.methodCallFilePath = this.locConf.get("methodCallFilePath") + this.locConf.get("methodCallFileName");
        clearLogFile();
        this.callGraphMainEntry = new CallGraphMainEntry();
        locate = new Locate();
        System.out.println("请先人工检测定位的结果，修改后按enter键继续....");
        new Scanner(System.in).nextLine();
        mapping = new Mapping();
    }

    public TracebilityMain() {
        init();
    }

    private void clearLogFile() {
        File methodCallLogFile = new File(this.methodCallFilePath);
        try {
            if(!methodCallLogFile.exists()) {
                methodCallLogFile.createNewFile();
            }
            FileWriter writer = new FileWriter(methodCallLogFile);
            writer.write("");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TracebilityMain tracebilityMain = new TracebilityMain();
    }
}
