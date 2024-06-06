// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMBusDeviceOnChannelRequestDataDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetMBusDeviceOnChannelCommandExecutor
    extends AbstractCommandExecutor<GetMBusDeviceOnChannelRequestDataDto, ChannelElementValuesDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetMBusDeviceOnChannelCommandExecutor.class);

  @Autowired private DeviceChannelsHelper deviceChannelsHelper;

  public GetMBusDeviceOnChannelCommandExecutor() {
    super(GetMBusDeviceOnChannelRequestDataDto.class);
  }

  @Override
  public ChannelElementValuesDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final GetMBusDeviceOnChannelRequestDataDto requestDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    LOGGER.debug(
        "Retrieving values for mbus channel {} on meter {}",
        requestDto.getChannel(),
        requestDto.getGatewayDeviceIdentification());
    return this.deviceChannelsHelper.getChannelElementValues(conn, device, requestDto.getChannel());
  }
}
