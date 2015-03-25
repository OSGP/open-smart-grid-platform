package com.alliander.osgp.adapter.ws.core.application.exceptionhandling;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.adapter.ws.schema.core.common.TechnicalFault;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

public class TechnicalExceptionConverter extends CustomConverter<TechnicalException, TechnicalFault> {

    @Override
    public TechnicalFault convert(final TechnicalException source, final Type<? extends TechnicalFault> destinationType) {
        if (source == null) {
            return null;
        }
        final TechnicalFault destination = new TechnicalFault();
        destination.setComponent(source.getComponentType().name());
        destination.setMessage(source.getMessage());
        destination.setInnerException(source.getCause().getClass().getName());
        destination.setInnerMessage(source.getCause().getMessage());

        return destination;
    }

}
