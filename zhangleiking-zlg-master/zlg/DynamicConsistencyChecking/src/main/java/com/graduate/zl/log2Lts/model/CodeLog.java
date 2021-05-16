package com.graduate.zl.log2Lts.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Log实体模型
 */
public class CodeLog {

    @Getter @Setter
    private String time;

    @Getter @Setter
    private String className;

    @Getter @Setter
    private String methodName;

    @Getter @Setter
    private String randomID;

    @Getter @Setter
    private String tag;

    public CodeLog() {

    }

    public CodeLog(String time, String className, String methodName, String randomID, String tag) {
        this.time = time;
        this.className = className;
        this.methodName = methodName;
        this.randomID = randomID;
        this.tag = tag;
    }
}
