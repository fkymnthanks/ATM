package com.graduate.zl.traceability.location;

import com.graduate.zl.traceability.common.LocConfConstant;
import com.graduate.zl.traceability.ir.InformationRetrieval;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * 定位
 */
public class Locate {

    private InformationRetrieval ir;

    private Map<String, String> locConf;

    private String locationResultFilePath;

    private void init() {
        this.locConf = LocConfConstant.getLocConf();
        int proCase = Integer.parseInt(this.locConf.get("proCase"));
        if(proCase == 1) {
            this.locationResultFilePath = this.locConf.get("locationResultFilePath")+this.locConf.get("locationResultFileNameOfATM");
        } else if(proCase == 2) {
            this.locationResultFilePath = this.locConf.get("locationResultFilePath") + this.locConf.get("locationResultFileNameOfOMH");
        }
        this.ir = InformationRetrieval.getInstance();
    }

    public Locate() {
        init();
        process();
    }

    public void process() {
        recordLocationResult();
    }

    /**
     * 记录定位的结果到文件中
     */
    public void recordLocationResult() {
        File locationFile = new File(this.locationResultFilePath);
        FileWriter writer = null;
        BufferedWriter br = null;
        try {
            if(!locationFile.exists()) {
                locationFile.createNewFile();
            }
            writer = new FileWriter(locationFile);
            br = new BufferedWriter(writer);
            for(String content : this.ir.getResultClass()) {
                br.write(content+"\r\n");
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
        System.out.println("The result of location is done!");
    }

    public static void main(String[] args) {
        Locate locate = new Locate();
    }
}
