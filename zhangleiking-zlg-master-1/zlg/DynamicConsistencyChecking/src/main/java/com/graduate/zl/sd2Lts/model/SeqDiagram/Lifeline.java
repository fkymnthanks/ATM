package com.graduate.zl.sd2Lts.model.SeqDiagram;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Vincent on 2018/7/30.
 */
public class Lifeline {

    @Getter @Setter
    private String id;

    @Getter @Setter
    private String name;

    public Lifeline(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
