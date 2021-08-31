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
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.FindMatchingChannelHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelElementsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelElementsResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CoupleMBusDeviceCommandExecutor
    extends AbstractCommandExecutor<MbusChannelElementsDto, MbusChannelElementsResponseDto> {

  @Autowired private DeviceChannelsHelper deviceChannelsHelper;

  public CoupleMBusDeviceCommandExecutor() {
    super(MbusChannelElementsDto.class);
  }

  @Override
  public MbusChannelElementsResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final MbusChannelElementsDto requestDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    log.debug("retrieving mbus info on e-meter");

    final List<ChannelElementValuesDto> candidateChannelElementValues =
        this.deviceChannelsHelper.findCandidateChannelsForDevice(conn, device, requestDto);

    final ChannelElementValuesDto lastChannelElementValuesRetrieved =
        candidateChannelElementValues.get(candidateChannelElementValues.size() - 1);

    if (FindMatchingChannelHelper.matches(requestDto, lastChannelElementValuesRetrieved)) {
      /*
       * Match found, indicating device is already coupled on this
       * channel: return it.
       */
      return new MbusChannelElementsResponseDto(
          requestDto,
          lastChannelElementValuesRetrieved.getChannel(),
          candidateChannelElementValues);
    }

    final ChannelElementValuesDto bestMatch =
        FindMatchingChannelHelper.bestMatch(requestDto, candidateChannelElementValues);
    if (bestMatch != null) {
      /*
       * Good enough match found indicating a channel the device is
       * already coupled on: return it.
       */
      return new MbusChannelElementsResponseDto(
          requestDto, bestMatch.getChannel(), candidateChannelElementValues);
    }

    final ChannelElementValuesDto emptyChannelMatch =
        this.deviceChannelsHelper.findEmptyChannel(candidateChannelElementValues);
    if (emptyChannelMatch == null) {
      /*
       * No channel free, all are occupied by M-Bus devices not matching
       * the one to be coupled here. Return null for the channel.
       */
      return new MbusChannelElementsResponseDto(requestDto, null, candidateChannelElementValues);
    }

    /*
     * If a free channel is found, write the attribute values from the
     * request to the M-Bus Client Setup for this channel.
     *
     * Note that this will not work for wired M-Bus devices. In order to
     * properly couple a wired M-Bus device the M-Bus Client Setup
     * slave_install method needs to be invoked so a primary_address is set
     * and its value is transferred to the M-Bus slave device.
     */
    final ChannelElementValuesDto updatedChannelElementValues =
        this.deviceChannelsHelper.writeUpdatedMbus(
            conn, requestDto, emptyChannelMatch.getChannel(), "CoupleMBusDevice");

    /*
     * Also update the entry in the candidateChannelElementValues list. Take
     * into account that the candidateChannelElementsValues List is 0-based,
     * while the channel in emptyChannelMatch is not
     */
    candidateChannelElementValues.set(
        this.deviceChannelsHelper.correctFirstChannelOffset(emptyChannelMatch),
        updatedChannelElementValues);

    return new MbusChannelElementsResponseDto(
        requestDto, updatedChannelElementValues.getChannel(), candidateChannelElementValues);
  }
}
