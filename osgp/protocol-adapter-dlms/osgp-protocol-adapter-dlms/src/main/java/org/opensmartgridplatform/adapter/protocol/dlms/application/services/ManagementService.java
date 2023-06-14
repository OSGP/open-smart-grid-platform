// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm.ClearMBusStatusOnAllChannelsCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.FindEventsCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.GetGsmDiagnosticCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.SetDeviceLifecycleStatusByChannelCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClearMBusStatusOnAllChannelsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventMessageDataResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FindEventsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FindEventsRequestList;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetGsmDiagnosticRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetGsmDiagnosticResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetDeviceCommunicationSettingsRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetDeviceCommunicationSettingsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateProtocolRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateProtocolResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "dlmsManagementService")
public class ManagementService {

  @Autowired private FindEventsCommandExecutor findEventsCommandExecutor;

  @Autowired private GetGsmDiagnosticCommandExecutor getGsmDiagnosticCommandExecutor;

  @Autowired
  private ClearMBusStatusOnAllChannelsCommandExecutor clearMBusStatusOnAllChannelsCommandExecutor;

  @Autowired
  private SetDeviceLifecycleStatusByChannelCommandExecutor
      setDeviceLifecycleStatusByChannelCommandExecutor;

  @Autowired private DlmsDeviceRepository dlmsDeviceRepository;

  public GetGsmDiagnosticResponseDto getGsmDiagnostic(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final GetGsmDiagnosticRequestDto getGsmDiagnosticRequestDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    return this.getGsmDiagnosticCommandExecutor.execute(
        conn, device, getGsmDiagnosticRequestDto, messageMetadata);
  }

  // === FIND EVENTS ===

  public EventMessageDataResponseDto findEvents(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final FindEventsRequestList findEventsQueryMessageDataContainer,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final List<EventDto> events = new ArrayList<>();

    log.info("findEvents setting up connection with meter {}", device.getDeviceIdentification());

    for (final FindEventsRequestDto findEventsQuery :
        findEventsQueryMessageDataContainer.getFindEventsQueryList()) {
      log.info(
          "findEventsQuery.eventLogCategory: {}, findEventsQuery.from: {}, findEventsQuery.until: {}",
          findEventsQuery.getEventLogCategory().toString(),
          findEventsQuery.getFrom(),
          findEventsQuery.getUntil());

      events.addAll(
          this.findEventsCommandExecutor.execute(conn, device, findEventsQuery, messageMetadata));
    }

    return new EventMessageDataResponseDto(events);
  }

  public void changeInDebugMode(final DlmsDevice device, final boolean debugMode) {
    device.setInDebugMode(debugMode);
    this.dlmsDeviceRepository.save(device);
  }

  public void setDeviceCommunicationSettings(
      final DlmsDevice device,
      final SetDeviceCommunicationSettingsRequestDto deviceCommunicationSettings) {

    this.dlmsDeviceRepository.save(
        this.setDeviceCommunicationSettings(
            device, deviceCommunicationSettings.getSetDeviceCommunicationSettingsData()));
  }

  private DlmsDevice setDeviceCommunicationSettings(
      final DlmsDevice device,
      final SetDeviceCommunicationSettingsRequestDataDto setCommunicationSettingsDataDto) {
    device.setChallengeLength(setCommunicationSettingsDataDto.getChallengeLength());
    device.setWithListSupported(setCommunicationSettingsDataDto.isWithListSupported());
    device.setSelectiveAccessSupported(
        setCommunicationSettingsDataDto.isSelectiveAccessSupported());
    device.setIpAddressIsStatic(setCommunicationSettingsDataDto.isIpAddressIsStatic());
    device.setUseSn(setCommunicationSettingsDataDto.isUseSn());
    device.setUseHdlc(setCommunicationSettingsDataDto.isUseHdlc());
    device.setPolyphase(setCommunicationSettingsDataDto.isPolyphase());

    return device;
  }

  public SetDeviceLifecycleStatusByChannelResponseDto setDeviceLifecycleStatusByChannel(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final SetDeviceLifecycleStatusByChannelRequestDataDto
          setDeviceLifecycleStatusByChannelRequest,
      final MessageMetadata messageMetadata)
      throws OsgpException {

    return this.setDeviceLifecycleStatusByChannelCommandExecutor.execute(
        conn, device, setDeviceLifecycleStatusByChannelRequest, messageMetadata);
  }

  public void clearMBusStatusOnAllChannels(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final ClearMBusStatusOnAllChannelsRequestDto clearMBusStatusOnAllChannelsRequestDto,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    this.clearMBusStatusOnAllChannelsCommandExecutor.execute(
        conn, device, clearMBusStatusOnAllChannelsRequestDto, messageMetadata);
  }

  public UpdateProtocolResponseDto updateProtocol(
      final DlmsDevice device, final UpdateProtocolRequestDto requestDto) {

    device.setProtocol(requestDto.getProtocol(), requestDto.getProtocolVersion());
    this.dlmsDeviceRepository.save(device);

    return new UpdateProtocolResponseDto(
        requestDto.getProtocol(), requestDto.getProtocolVersion(), requestDto.getProtocolVariant());
  }
}
