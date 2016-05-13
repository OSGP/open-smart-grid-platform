/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ObisCodeValues;

public class ObisCodeValuesConverter extends
BidirectionalConverter<ObisCodeValues, com.alliander.osgp.domain.core.valueobjects.smartmetering.ObisCodeValues> {

    
    @Override
    public com.alliander.osgp.domain.core.valueobjects.smartmetering.ObisCodeValues convertTo(ObisCodeValues source,
            Type<com.alliander.osgp.domain.core.valueobjects.smartmetering.ObisCodeValues> destinationType) {

        return new com.alliander.osgp.domain.core.valueobjects.smartmetering.ObisCodeValues((byte) source.getA(),
                (byte) source.getB(), (byte) source.getC(), (byte) source.getD(), (byte) source.getE(),
                (byte) source.getF());
    }

    @Override
    public ObisCodeValues convertFrom(com.alliander.osgp.domain.core.valueobjects.smartmetering.ObisCodeValues source,
            Type<ObisCodeValues> destinationType) {
        
        final ObisCodeValues result = new ObisCodeValues();
        result.setA((short)(source.getA() & 0xFF));
        result.setB((short)(source.getB() & 0xFF));
        result.setC((short)(source.getC() & 0xFF));
        result.setD((short)(source.getD() & 0xFF));
        result.setE((short)(source.getE() & 0xFF));
        result.setF((short)(source.getF() & 0xFF));
        return result;
    }

}
