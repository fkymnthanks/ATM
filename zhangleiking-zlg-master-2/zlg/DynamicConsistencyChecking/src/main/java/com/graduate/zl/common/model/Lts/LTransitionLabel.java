package com.graduate.zl.common.model.Lts;

import lombok.Getter;
import lombok.Setter;

public class LTransitionLabel {

    @Getter @Setter
    //转移动作名称
    private String name;

    @Getter @Setter
    //转移动作类型（msg表示常规消息类型，空或者cf类型则为特殊类型）
    private String type;

    @Getter @Setter
    //若转移动作类型为cf类型，则用condition保存cf的条件
    private String condition;

    @Getter @Setter
    //特殊过渡标识，用于CF开始节点和START节点的transition
    private boolean isSpecTrans;

//    @Getter @Setter
//    //用于log构建LTS时使用，标记调用方法所属的类
//    private String ownedClassName;

    public LTransitionLabel () {
        this(null);
    }

    public LTransitionLabel(String name) {
        this(name, null);
    }

    public LTransitionLabel(String name, String type) {
        this(name, type, null, false);
    }

    public LTransitionLabel(String name, String type, String condition, boolean isSpecTrans) {
        this.name = name;
        this.type = type;
        this.condition = condition;
        this.isSpecTrans = isSpecTrans;
    }

    @Override
    public String toString() {
        return "LTransitionLabel{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", condition='" + condition + '\'' +
                ", isSpecTrans=" + isSpecTrans +
                '}';
    }
}
