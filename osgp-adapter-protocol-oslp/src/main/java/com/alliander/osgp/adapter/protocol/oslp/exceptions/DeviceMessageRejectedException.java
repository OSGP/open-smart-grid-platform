package com.alliander.osgp.adapter.protocol.oslp.exceptions;

public class DeviceMessageRejectedException extends Exception {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -4616993212851435349L;

    private static final String MESSAGE = "Device Message Rejected";

    public DeviceMessageRejectedException() {
        super(MESSAGE);
    }
}
