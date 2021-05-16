package com.graduate.zl.log2Lts.transform;

import com.graduate.zl.common.model.Lts.LNode;
import com.graduate.zl.common.model.Lts.LTS;
import com.graduate.zl.common.model.Lts.LTransition;
import com.graduate.zl.common.model.Lts.LTransitionLabel;
import com.graduate.zl.common.util.LtsUtil;
import com.graduate.zl.sd2Lts.common.TransformConstant;
import com.graduate.zl.traceability.common.LocConfConstant;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于Log构建LTS
 */
public class BuildLtsFromLog {

    private String logFilePath = null;

    private LNode root = null;

    private AtomicInteger count = new AtomicInteger(0);

    private Map<String, String> transConf;

    private Map<String, String> locConf;

    private void init() {
        this.transConf = TransformConstant.getTransformConf();
        this.locConf = LocConfConstant.getLocConf();
        int proCase = Integer.parseInt(this.locConf.get("proCase"));
        if(proCase == 1) {
            this.logFilePath = this.transConf.get("ATMLogFullPath") + this.transConf.get("handledLogName");
        } else if(proCase == 2){
            this.logFilePath = this.transConf.get("OMHLogFullPath") + this.transConf.get("handledLogName");
        }

    }

    public BuildLtsFromLog() {
        init();
    }

    private void buildLtsFromLog() {
        LNode preNode = this.root;
        String preTransitionName = null;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(this.logFilePath);
            br = new BufferedReader(fr);
            String logContent;
            while((logContent = br.readLine()) != null) {
                String[] parts = parseLogLine(logContent);
                String className = parts[1].split("-")[0];
                String methodName = parts[1].split("-")[1];
                LNode node = new LNode(count.getAndIncrement(), className.substring(1, className.length()-1)); //以Class名为LNode名称
                if(this.root == null) {
                    this.root = node;
                    preNode = this.root;
                    preTransitionName = methodName.substring(1, methodName.length()-1);
                } else {
                    preNode.getNext().put(node, new LTransition(new LTransitionLabel(preTransitionName, null, null, false), Timestamp.valueOf(parts[0]).getTime()));
                    preNode = node;
                    preTransitionName = methodName.substring(1, methodName.length()-1);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析每行log的内容
     * @param str
     * @return
     */
    private String[] parseLogLine(String str) {
        String[] parts = str.split(";");
        String[] ret = new String[5];
        for(int i=0; i<4; i++) {
            if(i>1)
                ret[i] = parts[i].split(":")[1].trim();
            else
                ret[i] = parts[i];
        }
        return ret;
    }

    /**
     * 获取基于Log进行转换后的LTS
     * @return
     */
    public LTS getLTS() {
        LTS ret = new LTS();
        buildLtsFromLog();
        ret.buildLts(this.root);
        return ret;
    }

    public static void main(String[] args) {
        BuildLtsFromLog bt = new BuildLtsFromLog();
        LtsUtil.printAllPath(LtsUtil.getAllPath(bt.getLTS().getStart()));
    }
}
