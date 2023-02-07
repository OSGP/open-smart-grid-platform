/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SmartMeteringDeviceDtoBuilder;

class DeviceConverterTest {
  private final DeviceConverter converter = new DeviceConverter();

  @Test
  void convertsSmartMeteringDtoToDlmsDevice() {
    final SmartMeteringDeviceDto dto = new SmartMeteringDeviceDtoBuilder().build();
    final DlmsDevice result = this.converter.convertTo(dto, null, null);

    final DlmsDevice expected = this.converted(dto);

    Assertions.assertThat(result)
        .isEqualToIgnoringGivenFields(expected, "creationTime", "modificationTime", "version");
  }

  private DlmsDevice converted(final SmartMeteringDeviceDto dto) {
    final DlmsDevice dlmsDevice = new DlmsDevice();
    dlmsDevice.setDeviceIdentification(dto.getDeviceIdentification());
    dlmsDevice.setCommunicationMethod(dto.getCommunicationMethod());
    dlmsDevice.setCommunicationProvider(dto.getCommunicationProvider());
    dlmsDevice.setIccId(dto.getIccId());
    dlmsDevice.setHls3Active(dto.isHls3Active());
    dlmsDevice.setHls4Active(dto.isHls4Active());
    dlmsDevice.setHls5Active(dto.isHls5Active());
    dlmsDevice.setMbusIdentificationNumber(dto.getMbusIdentificationNumber());
    dlmsDevice.setMbusManufacturerIdentification(dto.getMbusManufacturerIdentification());
    dlmsDevice.setProtocol(dto.getProtocolName(), dto.getProtocolVersion());
    dlmsDevice.setPolyphase(dto.isPolyphase());
    dlmsDevice.setPort(dto.getPort());
    dlmsDevice.setChallengeLength(dto.getChallengeLength());
    dlmsDevice.setIpAddressIsStatic(dto.isIpAddressIsStatic());
    dlmsDevice.setWithListSupported(dto.isWithListSupported());
    dlmsDevice.setSelectiveAccessSupported(dto.isSelectiveAccessSupported());
    return dlmsDevice;
  }
}
