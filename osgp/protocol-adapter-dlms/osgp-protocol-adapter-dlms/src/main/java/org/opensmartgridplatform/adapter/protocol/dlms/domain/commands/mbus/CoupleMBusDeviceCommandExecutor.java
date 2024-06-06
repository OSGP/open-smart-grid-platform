// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.FindMatchingChannelHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelElementsDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CoupleMBusDeviceCommandExecutor
    extends AbstractCommandExecutor<CoupleMbusDeviceRequestDataDto, CoupleMbusDeviceResponseDto> {

  @Autowired private DeviceChannelsHelper deviceChannelsHelper;

  public CoupleMBusDeviceCommandExecutor() {
    super(MbusChannelElementsDto.class);
  }

  @Override
  public CoupleMbusDeviceResponseDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final CoupleMbusDeviceRequestDataDto requestDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {
    final String mbusDeviceIdentification = requestDto.getMbusDeviceIdentification();
    final MbusChannelElementsDto mbusChannelElementsDto = requestDto.getMbusChannelElements();

    log.debug("retrieving mbus info on e-meter");

    final List<ChannelElementValuesDto> candidateChannelElementValues =
        this.deviceChannelsHelper.findCandidateChannelsForDevice(
            conn, device, mbusChannelElementsDto);

    final ChannelElementValuesDto lastChannelElementValuesRetrieved =
        candidateChannelElementValues.get(candidateChannelElementValues.size() - 1);

    if (FindMatchingChannelHelper.matches(
        mbusChannelElementsDto, lastChannelElementValuesRetrieved)) {
      /*
       * Match found, indicating device is already coupled on this
       * channel: return it.
       */
      return new CoupleMbusDeviceResponseDto(
          mbusDeviceIdentification, lastChannelElementValuesRetrieved);
    }

    final ChannelElementValuesDto bestMatch =
        FindMatchingChannelHelper.bestMatch(mbusChannelElementsDto, candidateChannelElementValues);
    if (bestMatch != null) {
      /*
       * Good enough match found indicating a channel the device is
       * already coupled on: return it.
       */
      return new CoupleMbusDeviceResponseDto(mbusDeviceIdentification, bestMatch);
    }

    final ChannelElementValuesDto emptyChannelMatch =
        this.deviceChannelsHelper.findEmptyChannel(candidateChannelElementValues);
    if (emptyChannelMatch == null) {
      /*
       * No channel free, all are occupied by M-Bus devices not matching
       * the one to be coupled here. Return null.
       */
      return new CoupleMbusDeviceResponseDto(mbusDeviceIdentification, null);
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
            conn,
            device,
            mbusChannelElementsDto,
            emptyChannelMatch.getChannel(),
            "CoupleMBusDevice");

    return new CoupleMbusDeviceResponseDto(mbusDeviceIdentification, updatedChannelElementValues);
  }
}
