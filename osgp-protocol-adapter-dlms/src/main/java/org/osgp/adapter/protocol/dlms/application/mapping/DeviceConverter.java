/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;

import org.apache.commons.codec.binary.Hex;
import com.alliander.osgp.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;

public class DeviceConverter extends BidirectionalConverter<SmartMeteringDeviceDto, DlmsDevice> {

    @Override
    public DlmsDevice convertTo(final SmartMeteringDeviceDto source, final Type<DlmsDevice> destinationType) {
        final DlmsDevice dlmsDevice = new DlmsDevice();
        dlmsDevice.setDeviceIdentification(source.getDeviceIdentification());
        dlmsDevice.setCommunicationMethod(source.getCommunicationMethod());
        dlmsDevice.setCommunicationProvider(source.getCommunicationProvider());
        dlmsDevice.setIccId(source.getICCId());
        dlmsDevice.setHls3Active(source.isHLS3Active());
        dlmsDevice.setHls4Active(source.isHLS4Active());
        dlmsDevice.setHls5Active(source.isHLS5Active());

        if (source.getMasterKey() != null) {
            dlmsDevice.addSecurityKey(new SecurityKey(dlmsDevice, SecurityKeyType.E_METER_MASTER,
                    Hex.encodeHexString(source.getMasterKey()), source.getDeliveryDate(), null));
        }

        if (source.getAuthenticationKey() != null) {
            dlmsDevice.addSecurityKey(new SecurityKey(dlmsDevice, SecurityKeyType.E_METER_AUTHENTICATION, Hex.encodeHexString(source
                    .getAuthenticationKey()), source.getDeliveryDate(), null));
        }

        if (source.getGlobalEncryptionUnicastKey() != null) {
            dlmsDevice.addSecurityKey(new SecurityKey(dlmsDevice, SecurityKeyType.E_METER_ENCRYPTION, Hex.encodeHexString(source
                    .getGlobalEncryptionUnicastKey()), source.getDeliveryDate(), null));
        }

        return dlmsDevice;
    }

    @Override
    public SmartMeteringDeviceDto convertFrom(final DlmsDevice source, final Type<SmartMeteringDeviceDto> destinationType) {
        throw new UnsupportedOperationException("convertFrom of class DeviceConverter is not implemented.");
    }

}
