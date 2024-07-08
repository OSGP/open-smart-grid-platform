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
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Component
public class GetMbusEncryptionKeyStatusByChannelCommandExecutor
    extends AbstractCommandExecutor<
        GetMbusEncryptionKeyStatusByChannelRequestDataDto,
        GetMbusEncryptionKeyStatusByChannelResponseDto> {

  private final GetMbusEncryptionKeyStatusCommandExecutor getMbusEncryptionKeyStatusCommandExecutor;
  private final GetMBusDeviceOnChannelCommandExecutor getMBusDeviceOnChannelCommandExecutor;

  public GetMbusEncryptionKeyStatusByChannelCommandExecutor(
      final GetMbusEncryptionKeyStatusCommandExecutor getMbusEncryptionKeyStatusCommandExecutor,
      final GetMBusDeviceOnChannelCommandExecutor getMBusDeviceOnChannelCommandExecutor) {
    super(GetMbusEncryptionKeyStatusByChannelRequestDataDto.class);
    this.getMbusEncryptionKeyStatusCommandExecutor = getMbusEncryptionKeyStatusCommandExecutor;
    this.getMBusDeviceOnChannelCommandExecutor = getMBusDeviceOnChannelCommandExecutor;
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
      return new GetMbusEncryptionKeyStatusByChannelResponseDto(
          device.getDeviceIdentification(),
          EncryptionKeyStatusTypeDto.NO_ENCRYPTION_KEY,
          request.getChannel());
    }

    final EncryptionKeyStatusTypeDto encryptionKeyStatusType =
        this.getMbusEncryptionKeyStatusCommandExecutor.getEncryptionKeyStatusTypeDto(
            request.getChannel(), conn, device);
    return new GetMbusEncryptionKeyStatusByChannelResponseDto(
        device.getDeviceIdentification(), encryptionKeyStatusType, request.getChannel());
  }
}
