package com.graduate.zl.sd2Lts.model.SeqDiagram;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Vincent on 2018/7/30.
 */
public class OccurrenceSpecificationFragment {

    @Getter @Setter
    private String id;

    @Getter @Setter
    private String coveredId;

    @Getter @Setter
    private String belongInteractionOperandId;

    @Getter @Setter
    private boolean isBelongToCF;

    public OccurrenceSpecificationFragment(String id, String coveredId) {
        this.id = id;
        this.coveredId = coveredId;
        this.belongInteractionOperandId = null;
        this.isBelongToCF = false;
    }

    public OccurrenceSpecificationFragment(String id, String coveredId, String belongInteractionOperandId, boolean isBelongToCF) {
        this.id = id;
        this.coveredId = coveredId;
        this.belongInteractionOperandId = belongInteractionOperandId;
        this.isBelongToCF = isBelongToCF;
    }
}
