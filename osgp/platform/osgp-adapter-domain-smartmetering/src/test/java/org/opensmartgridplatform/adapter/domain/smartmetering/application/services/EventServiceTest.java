/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDetailDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDetailNameTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventMessageDataResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventTypeDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

  @Mock private DomainHelperService domainHelperService;

  private EventService eventService;
  private MessageMetadata deviceMessageMetadata;
  @Mock private SmartMeter smartMeter;

  @BeforeEach
  void setUp() throws FunctionalException {
    this.eventService = new EventService(this.domainHelperService);

    this.deviceMessageMetadata =
        new MessageMetadata.Builder()
            .withCorrelationUid(RandomStringUtils.randomAlphabetic(10))
            .withDeviceIdentification(RandomStringUtils.randomAlphabetic(10))
            .withOrganisationIdentification(RandomStringUtils.randomAlphabetic(10))
            .withMessageType(RandomStringUtils.randomAlphabetic(10))
            .build();

    when(this.domainHelperService.findSmartMeter(
            this.deviceMessageMetadata.getDeviceIdentification()))
        .thenReturn(this.smartMeter);
  }

  @Test
  void testWrongEventCode() {
    final FunctionalException functionalException =
        Assertions.assertThrows(
            FunctionalException.class,
            () -> {
              final ProtocolInfo protocolInfo = mock(ProtocolInfo.class);
              when(protocolInfo.getProtocol()).thenReturn("SMR");
              when(this.smartMeter.getProtocolInfo()).thenReturn(protocolInfo);

              final EventDto event = new EventDto(new DateTime(), 266, 2, "STANDARD_EVENT_LOG");
              final ArrayList<EventDto> events = new ArrayList<>();
              events.add(event);
              final EventMessageDataResponseDto responseDto =
                  new EventMessageDataResponseDto(events);

              this.eventService.enrichEvents(this.deviceMessageMetadata, responseDto);
            });
    assertThat(functionalException.getExceptionType())
        .isEqualTo(FunctionalExceptionType.VALIDATION_ERROR);
  }

  @Test
  void testProtocolNoMatch() throws FunctionalException {
    this.assertEventType(1, "SMART_METER_E", "XXX", "iskr", EventTypeDto.POWER_FAILURE);
    this.assertEventType(80, "SMART_METER_E", "DSMR", "iskr", EventTypeDto.PV_VOLTAGE_SAG_L1);
    this.assertEventType(81, "SMART_METER_E", "DSMR", "iskr", EventTypeDto.PV_VOLTAGE_SAG_L2);
    this.assertEventType(82, "SMART_METER_E", "DSMR", "iskr", EventTypeDto.PV_VOLTAGE_SAG_L3);
    this.assertEventType(83, "SMART_METER_E", "DSMR", "iskr", EventTypeDto.PV_VOLTAGE_SWELL_L1);
    this.assertEventType(84, "SMART_METER_E", "DSMR", "iskr", EventTypeDto.PV_VOLTAGE_SWELL_L2);
    this.assertEventType(85, "SMART_METER_E", "DSMR", "iskr", EventTypeDto.PV_VOLTAGE_SWELL_L3);

    this.assertEventType(80, "SMART_METER_E", "SMR", "iskr", EventTypeDto.OVER_VOLTAGE_L1);
    this.assertEventType(81, "SMART_METER_E", "SMR", "iskr", EventTypeDto.OVER_VOLTAGE_L2);
    this.assertEventType(82, "SMART_METER_E", "SMR", "iskr", EventTypeDto.OVER_VOLTAGE_L3);
    this.assertEventType(83, "SMART_METER_E", "SMR", "iskr", EventTypeDto.VOLTAGE_L1_NORMAL);
    this.assertEventType(84, "SMART_METER_E", "SMR", "iskr", EventTypeDto.VOLTAGE_L2_NORMAL);
    this.assertEventType(85, "SMART_METER_E", "SMR", "iskr", EventTypeDto.VOLTAGE_L3_NORMAL);

    this.assertEventType(
        85, "SMART_METER_E", "DSMR_CDMA", "iskr", EventTypeDto.PV_VOLTAGE_SWELL_L3);
    this.assertEventType(85, "SMART_METER_E", "SMR_CDMA", "iskr", EventTypeDto.VOLTAGE_L3_NORMAL);
  }

  @ParameterizedTest
  @CsvFileSource(resources = "event_types.csv", numLinesToSkip = 0, delimiter = ',')
  void testAddEventTypeToEvents(
      final int eventCode,
      final String deviceType,
      final String protocol,
      final String manufacturerCode,
      final EventTypeDto expectedEventTypeDto)
      throws FunctionalException {

    this.assertEventType(eventCode, deviceType, protocol, manufacturerCode, expectedEventTypeDto);
  }

  private void assertEventType(
      final int eventCode,
      final String deviceType,
      final String protocol,
      final String manufacturerCode,
      final EventTypeDto expectedEventTypeDto)
      throws FunctionalException {
    final ProtocolInfo protocolInfo = mock(ProtocolInfo.class);
    when(protocolInfo.getProtocol()).thenReturn(protocol);
    when(this.smartMeter.getProtocolInfo()).thenReturn(protocolInfo);

    when(this.smartMeter.getDeviceType()).thenReturn(deviceType);
    final DeviceModel deviceModel = mock(DeviceModel.class);
    final Manufacturer manufacturer = mock(Manufacturer.class);
    when(manufacturer.getCode()).thenReturn(manufacturerCode);
    when(deviceModel.getManufacturer()).thenReturn(manufacturer);
    when(this.smartMeter.getDeviceModel()).thenReturn(deviceModel);

    final EventDto event = new EventDto(new DateTime(), eventCode, 2, "STANDARD_EVENT_LOG");
    final ArrayList<EventDto> events = new ArrayList<>();
    events.add(event);
    final EventMessageDataResponseDto responseDto = new EventMessageDataResponseDto(events);

    this.eventService.enrichEvents(this.deviceMessageMetadata, responseDto);

    assertThat(responseDto.getEvents().size()).isOne();
    final EventDto eventDto = responseDto.getEvents().get(0);
    assertThat(eventDto.getEventTypeDto()).isEqualTo(expectedEventTypeDto);
    assertThat(eventDto.getEventCode()).isEqualTo(eventCode);
    final List<EventDetailDto> eventDetails = eventDto.getEventDetails();
    final int expectedNrOfDetails =
        (deviceType == null ? 0 : 1)
            + (manufacturerCode == null ? 0 : 1)
            + (protocol == null ? 0 : 1);
    assertThat(eventDetails.size()).isEqualTo(expectedNrOfDetails);
    assertThat(eventDto.getEventDetailValue(EventDetailNameTypeDto.MANUFACTURER_CODE))
        .isEqualTo(manufacturerCode);
    assertThat(eventDto.getEventDetailValue(EventDetailNameTypeDto.DEVICE_TYPE))
        .isEqualTo(deviceType);
    assertThat(eventDto.getEventDetailValue(EventDetailNameTypeDto.PROTOCOL_NAME))
        .isEqualTo(protocol);
  }
}
