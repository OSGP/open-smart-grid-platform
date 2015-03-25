package com.alliander.osgp.shared.exceptionhandling;

public class FunctionalException extends OsgpException {

    /**
     *
     */
    private static final long serialVersionUID = 2879663396838174171L;

    private final FunctionalExceptionType exceptionType;

    public FunctionalException(final FunctionalExceptionType exceptionType, final ComponentType componentType) {
        this(exceptionType, componentType, null);
    }

    public FunctionalException(final FunctionalExceptionType exceptionType, final ComponentType componentType, final Throwable cause) {
        super(componentType, exceptionType.getMessage(), cause);
        this.exceptionType = exceptionType;
    }

    public Integer getCode() {
        return this.exceptionType == null ? null : this.exceptionType.getCode();
    }

    public FunctionalExceptionType getExceptionType() {
        return this.exceptionType;
    }
}
