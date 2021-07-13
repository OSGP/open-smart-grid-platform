/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import java.util.List;
import org.openmuc.jdlms.GetResult;
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

  @Autowired private DeviceChannelsHelper deviceChannelsHelper;

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetMBusDeviceOnChannelCommandExecutor.class);

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

    LOGGER.info(
        "Retrieving values for mbus channel {} on meter {}",
        requestDto.getChannel(),
        requestDto.getGatewayDeviceIdentification());
    final List<GetResult> resultList =
        this.deviceChannelsHelper.getMBusClientAttributeValues(
            conn, device, requestDto.getChannel());
    return this.deviceChannelsHelper.makeChannelElementValues(requestDto.getChannel(), resultList);
  }
}
