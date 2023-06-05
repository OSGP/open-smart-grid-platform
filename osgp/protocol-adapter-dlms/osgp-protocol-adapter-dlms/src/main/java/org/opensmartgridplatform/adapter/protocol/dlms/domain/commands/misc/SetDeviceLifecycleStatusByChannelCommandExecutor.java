// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus.GetMBusDeviceOnChannelCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMBusDeviceOnChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetDeviceLifecycleStatusByChannelCommandExecutor
    extends AbstractCommandExecutor<
        SetDeviceLifecycleStatusByChannelRequestDataDto,
        SetDeviceLifecycleStatusByChannelResponseDto> {

  @Autowired private GetMBusDeviceOnChannelCommandExecutor getMBusDeviceOnChannelCommandExecutor;

  @Autowired private DlmsDeviceRepository dlmsDeviceRepository;

  public SetDeviceLifecycleStatusByChannelCommandExecutor() {
    super(SetDeviceLifecycleStatusByChannelRequestDataDto.class);
  }

  @Override
  public SetDeviceLifecycleStatusByChannelResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice gatewayDevice,
      final SetDeviceLifecycleStatusByChannelRequestDataDto request,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    final GetMBusDeviceOnChannelRequestDataDto mbusDeviceOnChannelRequest =
        new GetMBusDeviceOnChannelRequestDataDto(
            gatewayDevice.getDeviceIdentification(), request.getChannel());
    final ChannelElementValuesDto channelElementValues =
        this.getMBusDeviceOnChannelCommandExecutor.execute(
            conn, gatewayDevice, mbusDeviceOnChannelRequest, messageMetadata);

    if (!channelElementValues.hasChannel()
        || !channelElementValues.hasDeviceTypeIdentification()
        || !channelElementValues.hasManufacturerIdentification()) {
      throw new FunctionalException(
          FunctionalExceptionType.NO_DEVICE_FOUND_ON_CHANNEL, ComponentType.PROTOCOL_DLMS);
    }

    final DlmsDevice mbusDevice =
        this.dlmsDeviceRepository.findByMbusIdentificationNumberAndMbusManufacturerIdentification(
            channelElementValues.getIdentificationNumber(),
            channelElementValues.getManufacturerIdentification());

    if (mbusDevice == null) {
      throw new FunctionalException(
          FunctionalExceptionType.NO_MATCHING_MBUS_DEVICE_FOUND, ComponentType.PROTOCOL_DLMS);
    }

    return new SetDeviceLifecycleStatusByChannelResponseDto(
        gatewayDevice.getDeviceIdentification(),
        request.getChannel(),
        mbusDevice.getDeviceIdentification(),
        request.getDeviceLifecycleStatus());
  }
}
