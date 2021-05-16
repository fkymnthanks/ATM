package com.graduate.zl.common.model.LtsPath;

import com.graduate.zl.common.model.Lts.LNode;
import com.graduate.zl.common.model.Lts.LTransition;
import lombok.Getter;
import lombok.Setter;

/**
 * 用于获取Lts所有路径节点用
 */
public class LTSNodePath {
    @Getter @Setter
    private LNode node;

    @Getter @Setter
    private LTransition transition;

    public LTSNodePath(LNode node, LTransition transition) {
        this.node = node;
        this.transition = transition;
    }

    @Override
    public String toString() {
        return "LTSNodePath{" +
                "node=" + node +
                ", transition=" + transition +
                '}';
    }
}
