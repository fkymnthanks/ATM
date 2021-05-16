package com.graduate.zl.common.model.Lts;

import com.graduate.zl.common.util.RandomId;
import lombok.Getter;
import lombok.Setter;

public class LTransition {

    @Getter @Setter
    private String tid;

    @Getter @Setter
    private LTransitionLabel transLabel;

    @Getter @Setter
    private long timestamp;

    public LTransition() {
        this(null);
    }

    public LTransition(LTransitionLabel tLabel) {
        this(RandomId.getRandomId(), tLabel, System.currentTimeMillis());
    }

    public LTransition(LTransitionLabel tLabel, long timestamp) {
        this(RandomId.getRandomId(), tLabel, timestamp);
    }

    public LTransition(String tid, LTransitionLabel tLabel, long timestamp) {
        this.tid = tid;
        this.transLabel = tLabel;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "LTransition{" +
                "tid='" + tid + '\'' +
                ", transLabel=" + transLabel +
                ", timestamp=" + timestamp +
                '}';
    }
}
