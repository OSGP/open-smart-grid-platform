package com.alliander.osgp.adapter.ws.microgrids.exceptions;

public class WebServiceException extends Exception {
    
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 1L;

    public WebServiceException() {
        super();
    }

    public WebServiceException(final String message) {
        super(message);
    }

    public WebServiceException(final String message, final Throwable t) {
        super(message, t);
    }

}
