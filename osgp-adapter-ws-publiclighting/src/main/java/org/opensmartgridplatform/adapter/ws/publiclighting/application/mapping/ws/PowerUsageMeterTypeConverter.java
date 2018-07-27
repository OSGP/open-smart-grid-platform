/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.ws;

import org.opensmartgridplatform.domain.core.valueobjects.MeterType;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class PowerUsageMeterTypeConverter extends
        CustomConverter<MeterType, org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.MeterType> {

    @Override
    public org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.MeterType convert(
            final MeterType source,
            final Type<? extends org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.MeterType> destinationType,
            final MappingContext context) {
        // The enum values of the two types do not match (e.g. P1 -> P_1 ). The
        // JAXB MeterType
        // Ordinal value matches our domain values however.
        return org.opensmartgridplatform.adapter.ws.schema.publiclighting.devicemonitoring.MeterType.values()[source
                .ordinal()];
    }
}