/**
 * Copyright 2015 Smart Society Services B.V.
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Collection;

public class DayProfile implements Comparable<DayProfile>, Serializable {

    private static final long serialVersionUID = 3513563551917685789L;

    private Integer day_id;

    private Collection<DayProfileAction> dayProfileActionCollection;

    public DayProfile() {

    }

    public DayProfile(final Integer day_id, final Collection<DayProfileAction> dayProfileActionCollection) {
        super();
        this.day_id = day_id;
        this.dayProfileActionCollection = dayProfileActionCollection;
    }

    public Integer getDayId() {
        return this.day_id;
    }

    public void setDayId(final Integer day_id) {
        this.day_id = day_id;
    }

    public Collection<DayProfileAction> getDayProfileActionCollection() {
        return this.dayProfileActionCollection;
    }

    public void setDayProfileActionCollection(final Collection<DayProfileAction> dayProfileActionCollection) {
        this.dayProfileActionCollection = dayProfileActionCollection;
    }

    @Override
    public String toString() {
        return "DayProfile [day_id=" + this.day_id + ", dayProfileActionCollection=" + this.dayProfileActionCollection
                + "]";
    }

    @Override
    public int compareTo(final DayProfile o) {
        // TODO Auto-generated method stub
        return 0;
    }

}
