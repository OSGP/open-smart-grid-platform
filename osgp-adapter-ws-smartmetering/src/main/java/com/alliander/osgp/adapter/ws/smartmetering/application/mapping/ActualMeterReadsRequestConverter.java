package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsRequest;

public class ActualMeterReadsRequestConverter
        extends
        BidirectionalConverter<ActualMeterReadsRequest, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest convertTo(
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsRequest source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest> destinationType) {

        final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest destination = new com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest();
        destination.setDeviceIdentification(source.getDeviceIdentification());

        return destination;
    }

    @Override
    public ActualMeterReadsRequest convertFrom(
            final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest source,
            final Type<com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsRequest> destinationType) {

        return new ActualMeterReadsRequest(source.getDeviceIdentification());
    }

}
