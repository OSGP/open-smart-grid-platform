/**
 * Copyright 2015 Smart Society Services B.V.
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class DayProfile implements Comparable<DayProfile>, Serializable {

    private static final long serialVersionUID = 3513563551917685789L;

    private Integer dayId;

    private List<DayProfileAction> dayProfileActionList;

    public DayProfile(final Integer dayId, final List<DayProfileAction> dayProfileActionList) {
        super();
        this.dayId = dayId;
        this.dayProfileActionList = dayProfileActionList;
    }

    public Integer getDayId() {
        return this.dayId;
    }

    public List<DayProfileAction> getDayProfileActionList() {
        return this.dayProfileActionList;
    }

    @Override
    public String toString() {
        return "DayProfile [dayId=" + this.dayId + ", dayProfileActionList=" + this.dayProfileActionList + "]";
    }

    @Override
    public int compareTo(final DayProfile other) {
        return other.dayId.compareTo(this.dayId);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.dayId == null) ? 0 : this.dayId.hashCode());
        result = prime * result + ((this.dayProfileActionList == null) ? 0 : this.dayProfileActionList.hashCode());
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
        final DayProfile other = (DayProfile) obj;
        if (this.dayId == null) {
            if (other.dayId != null) {
                return false;
            }
        } else if (!this.dayId.equals(other.dayId)) {
            return false;
        }
        if (this.dayProfileActionList == null) {
            if (other.dayProfileActionList != null) {
                return false;
            }
        } else if (!this.dayProfileActionList.equals(other.dayProfileActionList)) {
            return false;
        }
        return true;
    }
}
