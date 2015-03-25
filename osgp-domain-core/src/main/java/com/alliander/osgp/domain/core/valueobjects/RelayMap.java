package com.alliander.osgp.domain.core.valueobjects;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class RelayMap implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = -8997468148053647259L;

    @NotNull
    @Min(1)
    @Max(6)
    private final Integer index;

    @NotNull
    @Min(1)
    @Max(255)
    private final Integer address;

    @NotNull
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
