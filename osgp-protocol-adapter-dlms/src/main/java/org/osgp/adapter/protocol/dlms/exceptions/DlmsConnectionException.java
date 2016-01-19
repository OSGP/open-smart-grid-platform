package org.osgp.adapter.protocol.dlms.exceptions;

public class DlmsConnectionException extends ProtocolAdapterException {

    private static final long serialVersionUID = 3407616419178204187L;

    public DlmsConnectionException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public DlmsConnectionException(final String message) {
        super(message);
    }
}
