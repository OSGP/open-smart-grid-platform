package com.alliander.osgp.shared.exceptionhandling;

public class TechnicalException extends OsgpException {

    /**
     * 
     */
    private static final long serialVersionUID = 215662983108393459L;

    public TechnicalException(final ComponentType componentType, final Throwable cause) {
        this(componentType, null, cause);
    }

    public TechnicalException(final ComponentType componentType, final String message, final Throwable cause) {
        super(componentType, message, cause);
    }
}
