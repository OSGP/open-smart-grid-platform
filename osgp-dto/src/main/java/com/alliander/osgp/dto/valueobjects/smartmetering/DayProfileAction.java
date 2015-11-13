/**
 * Copyright 2015 Smart Society Services B.V.
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;

public class DayProfileAction implements Comparable<DayProfileAction>, Serializable {

    private static final long serialVersionUID = 3913348299915167189L;

    private String scriptLogicalName;

    private Integer scriptSelector;

    private Date startTime;

    public DayProfileAction(final String scriptLogicalName, final Integer scriptSelector, final Date startTime) {
        super();
        this.scriptLogicalName = scriptLogicalName;
        this.scriptSelector = scriptSelector;
        this.startTime = startTime;
    }

    public String getScriptLogicalName() {
        return this.scriptLogicalName;
    }

    public Integer getScriptSelector() {
        return this.scriptSelector;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    @Override
    public String toString() {
        return "DayProfileAction [scriptLogicalName=" + this.scriptLogicalName + ", scriptSelector="
                + this.scriptSelector + ", startTime=" + this.startTime + "]";
    }

    @Override
    public int compareTo(final DayProfileAction other) {
        return other.scriptLogicalName.compareTo(this.scriptLogicalName);
    }
}
