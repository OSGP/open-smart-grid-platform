package com.alliander.osgp.adapter.ws.microgrids.exceptions;


public class WebServiceSecurityException extends WebServiceException {
    
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 3924185256176233897L;

    public WebServiceSecurityException() {
        super();
    }

    public WebServiceSecurityException(final String message, final Throwable t) {
        super(message, t);
    }

    public WebServiceSecurityException(final String message) {
        super(message);
    }

}
