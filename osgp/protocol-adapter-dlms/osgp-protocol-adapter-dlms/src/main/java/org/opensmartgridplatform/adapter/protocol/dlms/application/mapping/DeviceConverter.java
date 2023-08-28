// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;

public class DeviceConverter extends BidirectionalConverter<SmartMeteringDeviceDto, DlmsDevice> {

  @Override
  public DlmsDevice convertTo(
      final SmartMeteringDeviceDto source,
      final Type<DlmsDevice> destinationType,
      final MappingContext context) {
    final DlmsDevice dlmsDevice = new DlmsDevice();
    dlmsDevice.setDeviceIdentification(source.getDeviceIdentification());
    dlmsDevice.setCommunicationMethod(source.getCommunicationMethod());
    dlmsDevice.setCommunicationProvider(source.getCommunicationProvider());
    dlmsDevice.setIccId(source.getIccId());
    dlmsDevice.setLls1Active(source.isLls1Active());
    dlmsDevice.setHls3Active(source.isHls3Active());
    dlmsDevice.setHls4Active(source.isHls4Active());
    dlmsDevice.setHls5Active(source.isHls5Active());
    dlmsDevice.setMbusIdentificationNumber(source.getMbusIdentificationNumber());
    dlmsDevice.setMbusManufacturerIdentification(source.getMbusManufacturerIdentification());
    dlmsDevice.setProtocol(source.getProtocolName(), source.getProtocolVersion());
    dlmsDevice.setTimezone(source.getTimezone());
    dlmsDevice.setIpAddressIsStatic(source.isIpAddressIsStatic());
    dlmsDevice.setWithListSupported(source.isWithListSupported());
    dlmsDevice.setSelectiveAccessSupported(source.isSelectiveAccessSupported());
    dlmsDevice.setPolyphase(source.isPolyphase());
    if (source.getPort() != null) {
      dlmsDevice.setPort(source.getPort());
    }
    if (source.getChallengeLength() != null && source.getChallengeLength() > 0) {
      dlmsDevice.setChallengeLength(source.getChallengeLength());
    }

    return dlmsDevice;
  }

  @Override
  public SmartMeteringDeviceDto convertFrom(
      final DlmsDevice source,
      final Type<SmartMeteringDeviceDto> destinationType,
      final MappingContext context) {
    throw new UnsupportedOperationException(
        "convertFrom of class DeviceConverter is not implemented.");
  }
}
