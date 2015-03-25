package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;

public class RelayMap implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -8744650092009418556L;

    private final Integer index;
    private final Integer address;
    private RelayType relayType;

    public RelayMap(final Integer index, final Integer address, final RelayType relayType) {
        this.index = index;
        this.address = address;
        this.relayType = relayType;
    }

    public Integer getIndex() {
        return this.index;
    }

    public Integer getAddress() {
        return this.address;
    }

    public RelayType getRelayType() {
        return this.relayType;
    }

    public void changeRelayType(final RelayType relayType) {
        this.relayType = relayType;
    }
}
