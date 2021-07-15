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
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.CosemObjectAccessor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DecoupleMbusDeviceDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DecoupleMbusDeviceResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DecoupleMBusDeviceCommandExecutor
    extends AbstractCommandExecutor<DecoupleMbusDeviceDto, DecoupleMbusDeviceResponseDto> {

  @Autowired private DeviceChannelsHelper deviceChannelsHelper;

  public DecoupleMBusDeviceCommandExecutor() {
    super(DecoupleMbusDeviceDto.class);
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
      final DecoupleMbusDeviceDto decoupleMbusDto)
      throws ProtocolAdapterException {

    final Short channel = decoupleMbusDto.getChannel();
    log.debug(
        "Decouple channel {} on gateway device {}", channel, device.getDeviceIdentification());

    final ObisCode obisCode = this.deviceChannelsHelper.getObisCode(channel);

    // Get the current channel element values before resetting the channel
    final List<GetResult> resultList =
        this.deviceChannelsHelper.getMBusClientAttributeValues(conn, device, channel);

    final ChannelElementValuesDto channelElementValues =
        this.deviceChannelsHelper.makeChannelElementValues(channel, resultList);

    // Deinstall and reset channel
    final CosemObjectAccessor mBusSetup =
        new CosemObjectAccessor(conn, obisCode, InterfaceClass.MBUS_CLIENT.id());

    this.deviceChannelsHelper.deinstallSlave(conn, device, channel, mBusSetup);

    this.deviceChannelsHelper.resetMBusClientAttributeValues(
        conn, channel, this.getClass().getSimpleName());

    // return the channel element values as before decoupling
    return new DecoupleMbusDeviceResponseDto(channelElementValues);
  }
}
