package com.alliander.osgp.domain.core.valueobjects;

import java.io.Serializable;

public class RelayMatrix implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 1679416098090362861L;

    private String masterRelayIndex;

    private boolean masterRelayOn;

    private String indicesOfControlledRelaysOn;

    private String indicesOfControlledRelaysOff;

    public RelayMatrix(final String masterRelayIndex, final boolean masterRelayOn) {
        this.masterRelayIndex = masterRelayIndex;
        this.masterRelayOn = masterRelayOn;
    }

    public String getIndicesOfControlledRelaysOn() {
        return this.indicesOfControlledRelaysOn;
    }

    public void setIndicesOfControlledRelaysOn(final String indicesOfControlledRelaysOn) {
        this.indicesOfControlledRelaysOn = indicesOfControlledRelaysOn;
    }

    public String getIndicesOfControlledRelaysOff() {
        return this.indicesOfControlledRelaysOff;
    }

    public void setIndicesOfControlledRelaysOff(final String indicesOfControlledRelaysOff) {
        this.indicesOfControlledRelaysOff = indicesOfControlledRelaysOff;
    }

    public String getMasterRelayIndex() {
        return this.masterRelayIndex;
    }

    public boolean isMasterRelayOn() {
        return this.masterRelayOn;
    }
}
