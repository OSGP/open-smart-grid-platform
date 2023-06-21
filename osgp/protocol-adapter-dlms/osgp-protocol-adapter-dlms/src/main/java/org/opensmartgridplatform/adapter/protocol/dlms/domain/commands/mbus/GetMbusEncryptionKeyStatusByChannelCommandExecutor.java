// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EncryptionKeyStatusTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMBusDeviceOnChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetMbusEncryptionKeyStatusByChannelCommandExecutor
    extends AbstractCommandExecutor<
        GetMbusEncryptionKeyStatusByChannelRequestDataDto,
        GetMbusEncryptionKeyStatusByChannelResponseDto> {

  @Autowired
  private GetMbusEncryptionKeyStatusCommandExecutor getMbusEncryptionKeyStatusCommandExecutor;

  @Autowired private GetMBusDeviceOnChannelCommandExecutor getMBusDeviceOnChannelCommandExecutor;

  public GetMbusEncryptionKeyStatusByChannelCommandExecutor() {
    super(GetMbusEncryptionKeyStatusByChannelRequestDataDto.class);
  }

  @Override
  public GetMbusEncryptionKeyStatusByChannelResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final GetMbusEncryptionKeyStatusByChannelRequestDataDto request,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    final GetMBusDeviceOnChannelRequestDataDto mbusDeviceOnChannelRequest =
        new GetMBusDeviceOnChannelRequestDataDto(
            device.getDeviceIdentification(), request.getChannel());
    final ChannelElementValuesDto channelElementValues =
        this.getMBusDeviceOnChannelCommandExecutor.execute(
            conn, device, mbusDeviceOnChannelRequest, messageMetadata);

    if (!channelElementValues.hasChannel()
        || !channelElementValues.hasDeviceTypeIdentification()
        || !channelElementValues.hasManufacturerIdentification()) {
      throw new FunctionalException(
          FunctionalExceptionType.NO_DEVICE_FOUND_ON_CHANNEL, ComponentType.DOMAIN_SMART_METERING);
    }

    final EncryptionKeyStatusTypeDto encryptionKeyStatusType =
        this.getMbusEncryptionKeyStatusCommandExecutor.getEncryptionKeyStatusTypeDto(
            request.getChannel(), conn);
    return new GetMbusEncryptionKeyStatusByChannelResponseDto(
        device.getDeviceIdentification(), encryptionKeyStatusType, request.getChannel());
  }
}
