/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.microgrids.application.exceptionhandling;

import com.alliander.osgp.adapter.ws.schema.microgrids.common.FunctionalFault;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

public class FunctionalExceptionConverter extends CustomConverter<FunctionalException, FunctionalFault> {

    @Override
    public FunctionalFault convert(final FunctionalException source,
            final Type<? extends FunctionalFault> destinationType) {
        if (source == null) {
            return null;
        }
        final FunctionalFault destination = new FunctionalFault();
        destination.setCode(source.getCode());
        destination.setComponent(source.getComponentType().name());
        destination.setMessage(source.getMessage());
        if (source.getCause() != null) {
            destination.setInnerException(source.getCause().getClass().getName());
            destination.setInnerMessage(source.getCause().getMessage());
        }

        return destination;
    }
}
