package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;

public class RelayData implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -6823423138978797700L;

    private int index;
    private int totalLightingMinutes;

    public RelayData(final int index, final int totalLightingMinutes) {
        this.index = index;
        this.totalLightingMinutes = totalLightingMinutes;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(final int index) {
        this.index = index;
    }

    public int getTotalLightingMinutes() {
        return this.totalLightingMinutes;
    }

    public void setTotalLightingMinutes(final int totalLightingMinutes) {
        this.totalLightingMinutes = totalLightingMinutes;
    }
}
