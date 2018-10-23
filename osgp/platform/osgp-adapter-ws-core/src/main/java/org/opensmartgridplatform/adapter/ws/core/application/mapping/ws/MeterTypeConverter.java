/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.mapping.ws;

import org.opensmartgridplatform.domain.core.valueobjects.MeterType;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class MeterTypeConverter extends
        BidirectionalConverter<org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.MeterType, MeterType> {

    @Override
    public MeterType convertTo(final org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.MeterType source,
            final Type<MeterType> destinationType, final MappingContext context) {
        // The enum values of the two types do not match (e.g. P_1 -> P1). The
        // JAXB MeterType
        // String value matches our domain values however.
        return MeterType.valueOf(source.value());
    }

    @Override
    public org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.MeterType convertFrom(
            final MeterType source,
            final Type<org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.MeterType> destinationType,
            final MappingContext context) {
        // The enum values of the two types do not match (e.g. P_1 -> P1). The
        // JAXB MeterType
        // String value matches our domain values however.
        return org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.MeterType.fromValue(source.toString());
    }
}
