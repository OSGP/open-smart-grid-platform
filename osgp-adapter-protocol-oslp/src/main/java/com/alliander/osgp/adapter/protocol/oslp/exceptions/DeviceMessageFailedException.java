package com.alliander.osgp.adapter.protocol.oslp.exceptions;

public class DeviceMessageFailedException extends Exception {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 5129388216260738123L;

    private static final String MESSAGE = "Device Message Failed";

    public DeviceMessageFailedException() {
        super(MESSAGE);
    }
}
