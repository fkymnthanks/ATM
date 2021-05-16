package com.graduate.zl.sd2Lts.model.SeqDiagram;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Vincent on 2018/7/30.
 */
public class InteractionOperand {

    @Getter @Setter
    private String id;

    @Getter @Setter
    private Guard guard;

    @Getter @Setter
    private List<OccurrenceSpecificationFragment> osFragments;

    @Getter @Setter
    private String belongCombinedFragmentId;

    public InteractionOperand() {}

    public InteractionOperand(String id, Guard guard, List<OccurrenceSpecificationFragment> osFragments) {
        this.id = id;
        this.guard = guard;
        this.osFragments = osFragments;
        this.belongCombinedFragmentId = null;
    }

    public InteractionOperand(String id, Guard guard, String belongCombinedFragmentId, List<OccurrenceSpecificationFragment> osFragments) {
        this.id = id;
        this.guard = guard;
        this.belongCombinedFragmentId = belongCombinedFragmentId;
        this.osFragments = osFragments;
    }

}

