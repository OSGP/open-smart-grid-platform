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
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.GetResult;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CoupleMbusDeviceByChannelCommandExecutor
    extends AbstractCommandExecutor<
        CoupleMbusDeviceByChannelRequestDataDto, CoupleMbusDeviceByChannelResponseDto> {

  @Autowired private DeviceChannelsHelper deviceChannelsHelper;

  public CoupleMbusDeviceByChannelCommandExecutor() {
    super(CoupleMbusDeviceByChannelRequestDataDto.class);
  }

  @Override
  public CoupleMbusDeviceByChannelResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final CoupleMbusDeviceByChannelRequestDataDto requestDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    log.info(
        "Retrieving values for mbus channel {} on device {}",
        requestDto.getChannel(),
        device.getDeviceIdentification());
    final List<GetResult> resultList =
        this.deviceChannelsHelper.getMBusClientAttributeValues(
            conn, device, requestDto.getChannel());

    /*
     * Couple M-Bus device by channel is created to couple the M-Bus device
     * in the platform based on a new M-Bus device discovered alarm for a
     * particular channel. As such there is no write action to the M-Bus
     * Client Setup involved, since the platform depends on the attributes
     * on the gateway device to be able to determine which M-Bus device was
     * actually involved when the alarm was triggered for the channel from
     * the request.
     */
    return new CoupleMbusDeviceByChannelResponseDto(
        this.deviceChannelsHelper.makeChannelElementValues(requestDto.getChannel(), resultList));
  }
}
