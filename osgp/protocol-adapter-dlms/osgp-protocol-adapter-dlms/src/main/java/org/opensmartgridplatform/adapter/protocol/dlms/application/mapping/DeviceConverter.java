/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;

public class DeviceConverter extends BidirectionalConverter<SmartMeteringDeviceDto, DlmsDevice> {

    @Override
    public DlmsDevice convertTo(final SmartMeteringDeviceDto source, final Type<DlmsDevice> destinationType,
            final MappingContext context) {
        final DlmsDevice dlmsDevice = new DlmsDevice();
        dlmsDevice.setDeviceIdentification(source.getDeviceIdentification());
        dlmsDevice.setCommunicationMethod(source.getCommunicationMethod());
        dlmsDevice.setCommunicationProvider(source.getCommunicationProvider());
        dlmsDevice.setIccId(source.getICCId());
        dlmsDevice.setHls3Active(source.isHLS3Active());
        dlmsDevice.setHls4Active(source.isHLS4Active());
        dlmsDevice.setHls5Active(source.isHLS5Active());
        dlmsDevice.setMbusIdentificationNumber(source.getMbusIdentificationNumber());
        dlmsDevice.setMbusManufacturerIdentification(source.getMbusManufacturerIdentification());
        dlmsDevice.setProtocol(source.getProtocolName(), source.getProtocolVersion());

        return dlmsDevice;
    }

    @Override
    public SmartMeteringDeviceDto convertFrom(final DlmsDevice source,
            final Type<SmartMeteringDeviceDto> destinationType, final MappingContext context) {
        throw new UnsupportedOperationException("convertFrom of class DeviceConverter is not implemented.");
    }

}
