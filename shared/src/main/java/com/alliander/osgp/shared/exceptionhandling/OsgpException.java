package com.alliander.osgp.shared.exceptionhandling;

public class OsgpException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 3985910152334024442L;

    protected ComponentType componentType;

    public OsgpException(final ComponentType componentType, final String message, final Throwable cause) {
        super(message, cause);
        this.componentType = componentType;
    }

    public ComponentType getComponentType() {
        return this.componentType;
    }

}
