package com.alliander.osgp.domain.core.valueobjects;

/**
 * An Enum used to filter on Device.isActivated.
 */
public enum DeviceActivatedFilterType {
    BOTH(null),
    ACTIVE(true),
    INACTIVE(false);

    private Boolean value;

    private DeviceActivatedFilterType(final Boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return this.value;
    }
}
