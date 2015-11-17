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
        this.startTime = new Date(startTime.getTime());
    }

    public String getScriptLogicalName() {
        return this.scriptLogicalName;
    }

    public Integer getScriptSelector() {
        return this.scriptSelector;
    }

    public Date getStartTime() {
        return new Date(this.startTime.getTime());
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.scriptLogicalName == null) ? 0 : this.scriptLogicalName.hashCode());
        result = prime * result + ((this.scriptSelector == null) ? 0 : this.scriptSelector.hashCode());
        result = prime * result + ((this.startTime == null) ? 0 : this.startTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final DayProfileAction other = (DayProfileAction) obj;
        if (this.scriptLogicalName == null) {
            if (other.scriptLogicalName != null) {
                return false;
            }
        } else if (!this.scriptLogicalName.equals(other.scriptLogicalName)) {
            return false;
        }
        if (this.scriptSelector == null) {
            if (other.scriptSelector != null) {
                return false;
            }
        } else if (!this.scriptSelector.equals(other.scriptSelector)) {
            return false;
        }
        if (this.startTime == null) {
            if (other.startTime != null) {
                return false;
            }
        } else if (!this.startTime.equals(other.startTime)) {
            return false;
        }
        return true;
    }
}
