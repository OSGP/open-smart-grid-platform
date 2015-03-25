package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;

public class PsldData implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 5384893430194329868L;

    private int totalLightingHours;

    public PsldData(final int totalLightingHours) {
        this.totalLightingHours = totalLightingHours;
    }

    public int getTotalLightingHours() {
        return this.totalLightingHours;
    }
}
