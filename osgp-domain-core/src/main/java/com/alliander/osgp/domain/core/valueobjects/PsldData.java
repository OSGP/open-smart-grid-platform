package com.alliander.osgp.domain.core.valueobjects;

public class PsldData implements java.io.Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -2896680173172398109L;

    private int totalLightingHours;

    public PsldData(final int totalLightingHours) {
        this.totalLightingHours = totalLightingHours;
    }

    public int getTotalLightingHours() {
        return this.totalLightingHours;
    }
}
