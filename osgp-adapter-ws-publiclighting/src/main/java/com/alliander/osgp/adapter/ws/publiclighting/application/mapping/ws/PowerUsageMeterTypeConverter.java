package com.alliander.osgp.adapter.ws.publiclighting.application.mapping.ws;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.MeterType;

public class PowerUsageMeterTypeConverter extends
        CustomConverter<MeterType, com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.MeterType> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.MeterType convert(
            final MeterType source,
            final Type<? extends com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.MeterType> destinationType) {
        // The enum values of the two types do not match (e.g. P1 -> P_1 ). The
        // JAXB MeterType
        // Ordinal value matches our domain values however.
        return com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.MeterType.values()[source.ordinal()];
    }
}