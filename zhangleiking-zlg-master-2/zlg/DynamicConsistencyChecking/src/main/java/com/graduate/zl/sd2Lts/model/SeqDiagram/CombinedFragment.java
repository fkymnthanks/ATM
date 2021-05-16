package com.graduate.zl.sd2Lts.model.SeqDiagram;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Vincent on 2018/7/30.
 */
public class CombinedFragment {

    @Getter @Setter
    private String id;

    @Getter @Setter
    private String type;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private List<InteractionOperand> operandList;

}
