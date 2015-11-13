/**
 * Copyright 2015 Smart Society Services B.V.
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Collection;

public class DayProfile implements Comparable<DayProfile>, Serializable {

    private static final long serialVersionUID = 3513563551917685789L;

    private Integer dayId;

    private Collection<DayProfileAction> dayProfileActionCollection;

    public DayProfile(final Integer dayId, final Collection<DayProfileAction> dayProfileActionCollection) {
        super();
        this.dayId = dayId;
        this.dayProfileActionCollection = dayProfileActionCollection;
    }

    public Integer getDayId() {
        return this.dayId;
    }

    public Collection<DayProfileAction> getDayProfileActionCollection() {
        return this.dayProfileActionCollection;
    }

    @Override
    public String toString() {
        return "DayProfile [dayId=" + this.dayId + ", dayProfileActionCollection=" + this.dayProfileActionCollection
                + "]";
    }

    @Override
    public int compareTo(final DayProfile o) {
        return o.dayId.compareTo(this.dayId);
    }
}
