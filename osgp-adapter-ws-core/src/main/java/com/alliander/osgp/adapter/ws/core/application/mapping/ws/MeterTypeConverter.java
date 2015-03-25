package com.alliander.osgp.adapter.ws.core.application.mapping.ws;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.MeterType;

public class MeterTypeConverter extends
        BidirectionalConverter<com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.MeterType, MeterType> {

    @Override
    public MeterType convertTo(
            final com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.MeterType source,
            final Type<MeterType> destinationType) {
        // The enum values of the two types do not match (e.g. P_1 -> P1). The
        // JAXB MeterType
        // String value matches our domain values however.
        return MeterType.valueOf(source.value());
    }

    @Override
    public com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.MeterType convertFrom(
            final MeterType source,
            final Type<com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.MeterType> destinationType) {
        // The enum values of the two types do not match (e.g. P_1 -> P1). The
        // JAXB MeterType
        // String value matches our domain values however.
        return com.alliander.osgp.adapter.ws.schema.core.configurationmanagement.MeterType.fromValue(source.toString());
    }
}
