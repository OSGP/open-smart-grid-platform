// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.CosemObjectAccessor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DecoupleMbusDeviceDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DecoupleMbusDeviceResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DecoupleMBusDeviceCommandExecutor
    extends AbstractCommandExecutor<DecoupleMbusDeviceDto, DecoupleMbusDeviceResponseDto> {

  private final DeviceChannelsHelper deviceChannelsHelper;
  private final ObjectConfigServiceHelper objectConfigServiceHelper;

  public DecoupleMBusDeviceCommandExecutor(
      final DeviceChannelsHelper deviceChannelsHelper,
      final ObjectConfigServiceHelper objectConfigServiceHelper) {
    super(DecoupleMbusDeviceDto.class);
    this.objectConfigServiceHelper = objectConfigServiceHelper;
    this.deviceChannelsHelper = deviceChannelsHelper;
  }

  @Override
  public ActionResponseDto asBundleResponse(final DecoupleMbusDeviceResponseDto executionResult)
      throws ProtocolAdapterException {
    return new DecoupleMbusDeviceResponseDto(executionResult.getChannelElementValues());
  }

  @Override
  public DecoupleMbusDeviceResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final DecoupleMbusDeviceDto decoupleMbusDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final Short channel = decoupleMbusDto.getChannel();
    log.debug(
        "Decouple channel {} on gateway device {}", channel, device.getDeviceIdentification());

    // Get the current channel element values before resetting the channel
    ChannelElementValuesDto channelElementValues;
    boolean readChannelElementSuccessfully = true;
    try {
      channelElementValues =
          this.deviceChannelsHelper.getChannelElementValues(conn, device, channel);
    } catch (final InvalidIdentificationNumberException e) {
      channelElementValues = e.getChannelElementValuesDto();
      readChannelElementSuccessfully = false;
    }

    // Deinstall and reset channel
    final CosemObjectAccessor mBusSetup =
        new CosemObjectAccessor(
            conn,
            this.objectConfigServiceHelper,
            DlmsObjectType.MBUS_CLIENT_SETUP,
            Protocol.forDevice(device),
            channel);

    this.deviceChannelsHelper.deinstallSlave(conn, device, channel, mBusSetup);

    this.deviceChannelsHelper.resetMBusClientAttributeValues(
        conn, device, channel, this.getClass().getSimpleName());

    // return the channel element values as before decoupling
    return new DecoupleMbusDeviceResponseDto(readChannelElementSuccessfully, channelElementValues);
  }
}
