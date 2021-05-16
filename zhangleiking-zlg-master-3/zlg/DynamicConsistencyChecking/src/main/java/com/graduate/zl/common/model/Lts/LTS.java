package com.graduate.zl.common.model.Lts;

import com.graduate.zl.sd2Lts.common.Constants;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * LTS模型数据结构
 */
public class LTS {

    // LTS起始节点
    @Getter @Setter
    private LNode start;

    public LTS() {

    }

    public LNode buildLts(LNode node) {
        this.start = node;
        return this.start;
    }
}
