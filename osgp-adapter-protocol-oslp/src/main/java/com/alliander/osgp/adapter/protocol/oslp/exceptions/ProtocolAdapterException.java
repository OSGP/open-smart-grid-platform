package com.alliander.osgp.adapter.protocol.oslp.exceptions;

public class ProtocolAdapterException extends Exception {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 916943696172403469L;

    public ProtocolAdapterException(final String message) {
        super(message);
    }

    public ProtocolAdapterException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
