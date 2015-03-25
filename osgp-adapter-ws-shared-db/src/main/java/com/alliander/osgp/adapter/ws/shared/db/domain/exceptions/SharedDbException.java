package com.alliander.osgp.adapter.ws.shared.db.domain.exceptions;

public class SharedDbException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -6074924962793671015L;

    public SharedDbException(final String message) {
        super(message);
    }

    public SharedDbException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

}
