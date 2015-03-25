package com.alliander.osgp.adapter.protocol.oslp.exceptions;

public class NoDeviceResponseException extends Exception {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 5129388216260738123L;

    private static final String MESSAGE = "No response from device";

    public NoDeviceResponseException() {
        super(MESSAGE);
    }
}
