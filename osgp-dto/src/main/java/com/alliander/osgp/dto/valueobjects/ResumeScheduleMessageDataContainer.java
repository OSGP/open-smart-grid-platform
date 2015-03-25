package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;

public class ResumeScheduleMessageDataContainer implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 4989992501170383172L;

    Integer index;
    boolean isImmediate;

    public ResumeScheduleMessageDataContainer(final Integer index, final boolean isImmediate) {
        this.index = index;
        this.isImmediate = isImmediate;
    }

    public Integer getIndex() {
        return this.index;
    }

    public boolean isImmediate() {
        return this.isImmediate;
    }
}
