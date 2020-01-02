/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import org.apache.commons.codec.binary.Hex;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SmartMeteringDeviceDtoBuilder;

public class DeviceConverterTest {
    private final DeviceConverter converter = new DeviceConverter();

    @Test
    public void convertsSmartMeteringDtoToDlmsDevice() {
        final SmartMeteringDeviceDto dto = new SmartMeteringDeviceDtoBuilder().build();
        final DlmsDevice result = this.converter.convertTo(dto, null, null);

        final DlmsDevice expected = this.converted(dto);

        Assertions.assertThat(result)
                .isEqualToIgnoringGivenFields(expected, "creationTime", "modificationTime", "version");
        Assertions.assertThat(result.getSecurityKeys())
                .usingElementComparatorIgnoringFields("creationTime", "modificationTime", "version")
                .isEqualTo(expected.getSecurityKeys());
    }

    private DlmsDevice converted(final SmartMeteringDeviceDto dto) {
        final DlmsDevice dlmsDevice = new DlmsDevice();
        dlmsDevice.setDeviceIdentification(dto.getDeviceIdentification());
        dlmsDevice.setCommunicationMethod(dto.getCommunicationMethod());
        dlmsDevice.setCommunicationProvider(dto.getCommunicationProvider());
        dlmsDevice.setIccId(dto.getICCId());
        dlmsDevice.setHls3Active(dto.isHLS3Active());
        dlmsDevice.setHls4Active(dto.isHLS4Active());
        dlmsDevice.setHls5Active(dto.isHLS5Active());
        dlmsDevice.setMbusIdentificationNumber(dto.getMbusIdentificationNumber());
        dlmsDevice.setMbusManufacturerIdentification(dto.getMbusManufacturerIdentification());
        dlmsDevice.setProtocol(dto.getProtocolName(), dto.getProtocolVersion());

        dlmsDevice.addSecurityKey(new SecurityKey(dlmsDevice, SecurityKeyType.E_METER_MASTER,
                Hex.encodeHexString(dto.getMasterKey()), dto.getDeliveryDate(), null));
        dlmsDevice.addSecurityKey(new SecurityKey(dlmsDevice, SecurityKeyType.E_METER_AUTHENTICATION,
                Hex.encodeHexString(dto.getAuthenticationKey()), dto.getDeliveryDate(), null));
        dlmsDevice.addSecurityKey(new SecurityKey(dlmsDevice, SecurityKeyType.E_METER_ENCRYPTION,
                Hex.encodeHexString(dto.getGlobalEncryptionUnicastKey()), dto.getDeliveryDate(), null));
        dlmsDevice.addSecurityKey(new SecurityKey(dlmsDevice, SecurityKeyType.G_METER_MASTER,
                Hex.encodeHexString(dto.getMbusDefaultKey()), dto.getDeliveryDate(), null));
        return dlmsDevice;
    }
}
