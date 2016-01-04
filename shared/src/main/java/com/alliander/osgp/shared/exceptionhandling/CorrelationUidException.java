package com.alliander.osgp.shared.exceptionhandling;

public abstract class CorrelationUidException extends FunctionalException {

    public CorrelationUidException(final FunctionalExceptionType exceptionType, final ComponentType componentType,
            final Throwable cause) {
        super(exceptionType, componentType, cause);
    }

    public CorrelationUidException(final FunctionalExceptionType exceptionType, final ComponentType componentType) {
        super(exceptionType, componentType);
    }

}
