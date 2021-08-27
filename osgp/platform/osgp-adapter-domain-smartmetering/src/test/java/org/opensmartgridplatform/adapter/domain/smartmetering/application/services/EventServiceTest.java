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

  @Test
  void testAddEventTypeToEvents() throws FunctionalException {
    this.assertEventType(255, "", "", "", EventTypeDto.EVENTLOG_CLEARED);
    this.assertEventType(1, "SMART_METER_E", "", "", EventTypeDto.POWER_FAILURE);
    this.assertEventType(2, "", "", "", EventTypeDto.POWER_RETURNED);
    this.assertEventType(3, "", "", "", EventTypeDto.CLOCK_UPDATE);
    this.assertEventType(4, "", "", "", EventTypeDto.CLOCK_ADJUSTED_OLD_TIME);
    this.assertEventType(5, "", "", "", EventTypeDto.CLOCK_ADJUSTED_NEW_TIME);
    this.assertEventType(6, "", "", "", EventTypeDto.CLOCK_INVALID);
    this.assertEventType(7, "", "", "", EventTypeDto.REPLACE_BATTERY);
    this.assertEventType(8, "", "", "", EventTypeDto.BATTERY_VOLTAGE_LOW);
    this.assertEventType(9, "", "", "", EventTypeDto.TARIFF_ACTIVATED);
    this.assertEventType(10, "", "", "", EventTypeDto.ERROR_REGISTER_CLEARED);
    this.assertEventType(11, "", "", "", EventTypeDto.ALARM_REGISTER_CLEARED);
    this.assertEventType(12, "", "", "", EventTypeDto.HARDWARE_ERROR_PROGRAM_MEMORY);
    this.assertEventType(13, "", "", "", EventTypeDto.HARDWARE_ERROR_RAM);
    this.assertEventType(14, "", "", "", EventTypeDto.HARDWARE_ERROR_NV_MEMORY);
    this.assertEventType(15, "", "", "", EventTypeDto.WATCHDOG_ERROR);
    this.assertEventType(16, "", "", "", EventTypeDto.HARDWARE_ERROR_MEASUREMENT_SYSTEM);
    this.assertEventType(17, "", "", "", EventTypeDto.FIRMWARE_READY_FOR_ACTIVATION);
    this.assertEventType(18, "", "", "", EventTypeDto.FIRMWARE_ACTIVATED);
    this.assertEventType(19, "", "", "", EventTypeDto.PASSIVE_TARIFF_UPDATED);
    this.assertEventType(20, "", "", "", EventTypeDto.SUCCESSFUL_SELFCHECK_AFTER_FIRMWARE_UPDATE);
    this.assertEventType(21, "", "", "", EventTypeDto.COMMUNICATION_MODULE_REMOVED);
    this.assertEventType(22, "", "", "", EventTypeDto.COMMUNICATION_MODULE_INSERTED);
    this.assertEventType(40, "", "", "", EventTypeDto.TERMINAL_COVER_REMOVED);
    this.assertEventType(41, "", "", "", EventTypeDto.TERMINAL_COVER_CLOSED);
    this.assertEventType(42, "", "", "", EventTypeDto.STRONG_DC_FIELD_DETECTED);
    this.assertEventType(43, "", "", "", EventTypeDto.NO_STRONG_DC_FIELD_ANYMORE);
    this.assertEventType(44, "", "", "", EventTypeDto.METER_COVER_REMOVED);
    this.assertEventType(45, "", "", "", EventTypeDto.METER_COVER_CLOSED);
    this.assertEventType(46, "", "", "", EventTypeDto.FAILED_LOGIN_ATTEMPT);
    this.assertEventType(47, "", "", "", EventTypeDto.CONFIGURATION_CHANGE);
    this.assertEventType(48, "", "", "", EventTypeDto.MODULE_COVER_OPENED);
    this.assertEventType(49, "", "", "", EventTypeDto.MODULE_COVER_CLOSED);
    this.assertEventType(71, "", "", "", EventTypeDto.METROLOGICAL_MAINTENANCE);
    this.assertEventType(72, "", "", "", EventTypeDto.TECHNICAL_MAINTENANCE);
    this.assertEventType(73, "", "", "", EventTypeDto.RETRIEVE_METER_READINGS_E);
    this.assertEventType(74, "", "", "", EventTypeDto.RETRIEVE_METER_READINGS_G);
    this.assertEventType(75, "", "", "", EventTypeDto.RETRIEVE_INTERVAL_DATA_E);
    this.assertEventType(76, "", "", "", EventTypeDto.RETRIEVE_INTERVAL_DATA_G);
    this.assertEventType(77, "", "", "", EventTypeDto.UNDER_VOLTAGE_L1);
    this.assertEventType(78, "", "", "", EventTypeDto.UNDER_VOLTAGE_L2);
    this.assertEventType(79, "", "", "", EventTypeDto.UNDER_VOLTAGE_L3);
    this.assertEventType(80, "", "DSMR", "", EventTypeDto.PV_VOLTAGE_SAG_L1);
    this.assertEventType(81, "", "DSMR", "", EventTypeDto.PV_VOLTAGE_SAG_L2);
    this.assertEventType(82, "", "DSMR", "", EventTypeDto.PV_VOLTAGE_SAG_L3);
    this.assertEventType(83, "", "DSMR", "", EventTypeDto.PV_VOLTAGE_SWELL_L1);
    this.assertEventType(84, "", "DSMR", "", EventTypeDto.PV_VOLTAGE_SWELL_L2);
    this.assertEventType(85, "", "DSMR", "", EventTypeDto.PV_VOLTAGE_SWELL_L3);
    this.assertEventType(80, "", "SMR", "", EventTypeDto.OVER_VOLTAGE_L1);
    this.assertEventType(81, "", "SMR", "", EventTypeDto.OVER_VOLTAGE_L2);
    this.assertEventType(82, "", "SMR", "", EventTypeDto.OVER_VOLTAGE_L3);
    this.assertEventType(83, "", "SMR", "", EventTypeDto.VOLTAGE_L1_NORMAL);
    this.assertEventType(84, "", "SMR", "", EventTypeDto.VOLTAGE_L2_NORMAL);
    this.assertEventType(85, "", "SMR", "", EventTypeDto.VOLTAGE_L3_NORMAL);
    this.assertEventType(86, "", "", "", EventTypeDto.PHASE_OUTAGE_L1);
    this.assertEventType(87, "", "", "", EventTypeDto.PHASE_OUTAGE_L2);
    this.assertEventType(88, "", "", "", EventTypeDto.PHASE_OUTAGE_L3);
    this.assertEventType(89, "", "", "", EventTypeDto.PHASE_OUTAGE_TEST);
    this.assertEventType(90, "", "", "", EventTypeDto.PHASE_RETURNED_L1);
    this.assertEventType(91, "", "", "", EventTypeDto.PHASE_RETURNED_L2);
    this.assertEventType(92, "", "", "", EventTypeDto.PHASE_RETURNED_L3);
    this.assertEventType(100, "", "", "", EventTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_1);
    this.assertEventType(101, "", "", "", EventTypeDto.COMMUNICATION_OK_M_BUS_CHANNEL_1);
    this.assertEventType(102, "", "", "", EventTypeDto.REPLACE_BATTERY_M_BUS_CHANNEL_1);
    this.assertEventType(103, "", "", "", EventTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_1);
    this.assertEventType(104, "", "", "", EventTypeDto.CLOCK_ADJUSTED_M_BUS_CHANNEL_1);
    this.assertEventType(105, "", "", "", EventTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1);
    this.assertEventType(106, "", "", "", EventTypeDto.PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_1);
    this.assertEventType(110, "", "", "", EventTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_2);
    this.assertEventType(111, "", "", "", EventTypeDto.COMMUNICATION_OK_M_BUS_CHANNEL_2);
    this.assertEventType(112, "", "", "", EventTypeDto.REPLACE_BATTERY_M_BUS_CHANNEL_2);
    this.assertEventType(113, "", "", "", EventTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_2);
    this.assertEventType(114, "", "", "", EventTypeDto.CLOCK_ADJUSTED_M_BUS_CHANNEL_2);
    this.assertEventType(115, "", "", "", EventTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2);
    this.assertEventType(116, "", "", "", EventTypeDto.PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_2);
    this.assertEventType(120, "", "", "", EventTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_3);
    this.assertEventType(121, "", "", "", EventTypeDto.COMMUNICATION_OK_M_BUS_CHANNEL_3);
    this.assertEventType(122, "", "", "", EventTypeDto.REPLACE_BATTERY_M_BUS_CHANNEL_3);
    this.assertEventType(123, "", "", "", EventTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_3);
    this.assertEventType(124, "", "", "", EventTypeDto.CLOCK_ADJUSTED_M_BUS_CHANNEL_3);
    this.assertEventType(125, "", "", "", EventTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3);
    this.assertEventType(126, "", "", "", EventTypeDto.PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_3);
    this.assertEventType(130, "", "", "", EventTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_4);
    this.assertEventType(131, "", "", "", EventTypeDto.COMMUNICATION_OK_M_BUS_CHANNEL_4);
    this.assertEventType(132, "", "", "", EventTypeDto.REPLACE_BATTERY_M_BUS_CHANNEL_4);
    this.assertEventType(133, "", "", "", EventTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_4);
    this.assertEventType(134, "", "", "", EventTypeDto.CLOCK_ADJUSTED_M_BUS_CHANNEL_4);
    this.assertEventType(135, "", "", "", EventTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4);
    this.assertEventType(136, "", "", "", EventTypeDto.PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_4);
    this.assertEventType(230, "", "", "", EventTypeDto.MANUFACTURER_SPECIFIC_230);
    this.assertEventType(231, "", "", "", EventTypeDto.MANUFACTURER_SPECIFIC_231);
    this.assertEventType(232, "", "", "", EventTypeDto.MANUFACTURER_SPECIFIC_232);
    this.assertEventType(233, "", "", "", EventTypeDto.MANUFACTURER_SPECIFIC_233);
    this.assertEventType(234, "", "", "", EventTypeDto.MANUFACTURER_SPECIFIC_234);
    this.assertEventType(235, "", "", "", EventTypeDto.MANUFACTURER_SPECIFIC_235);
    this.assertEventType(236, "", "", "", EventTypeDto.MANUFACTURER_SPECIFIC_236);
    this.assertEventType(237, "", "", "", EventTypeDto.MANUFACTURER_SPECIFIC_237);
    this.assertEventType(238, "", "", "", EventTypeDto.MANUFACTURER_SPECIFIC_238);
    this.assertEventType(239, "", "", "", EventTypeDto.MANUFACTURER_SPECIFIC_239);
    this.assertEventType(240, "", "", "", EventTypeDto.MANUFACTURER_SPECIFIC_240);
    this.assertEventType(241, "", "", "", EventTypeDto.MANUFACTURER_SPECIFIC_241);
    this.assertEventType(242, "", "", "", EventTypeDto.MANUFACTURER_SPECIFIC_242);
    this.assertEventType(243, "", "", "", EventTypeDto.MANUFACTURER_SPECIFIC_243);
    this.assertEventType(244, "", "", "", EventTypeDto.MANUFACTURER_SPECIFIC_244);
    this.assertEventType(245, "", "", "", EventTypeDto.MANUFACTURER_SPECIFIC_245);
    this.assertEventType(246, "", "", "", EventTypeDto.MANUFACTURER_SPECIFIC_246);
    this.assertEventType(247, "", "", "", EventTypeDto.MANUFACTURER_SPECIFIC_247);
    this.assertEventType(248, "", "", "", EventTypeDto.MANUFACTURER_SPECIFIC_248);
    this.assertEventType(249, "", "", "", EventTypeDto.MANUFACTURER_SPECIFIC_249);

    this.assertEventType(0xFFFF, "", "", "", EventTypeDto.AUXILIARY_EVENTLOG_CLEARED);
    this.assertEventType(0x1000, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_SUCCESSFUL_CHANNEL_1);
    this.assertEventType(
        0x1001, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_BLOCK_SIZE_NOT_SUPPORTED_CHANNEL_1);
    this.assertEventType(
        0x1002, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_IMAGE_SIZE_TOO_BIG_CHANNEL_1);
    this.assertEventType(
        0x1003, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_INVALID_BLOCK_NUMBER_CHANNEL_1);
    this.assertEventType(
        0x1004, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_DATA_RECEIVE_ERROR_CHANNEL_1);
    this.assertEventType(
        0x1005, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_IMAGE_NOT_COMPLETE_ERROR_CHANNEL_1);
    this.assertEventType(
        0x1006, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_INVALID_SECURITY_ERROR_CHANNEL_1);
    this.assertEventType(
        0x1007,
        "",
        "",
        "",
        EventTypeDto.MBUS_FW_UPGRADE_INVALID_FIRMWARE_FOR_THIS_DEVICE_CHANNEL_1);
    this.assertEventType(0x1100, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_SUCCESSFUL_CHANNEL_2);
    this.assertEventType(
        0x1101, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_BLOCK_SIZE_NOT_SUPPORTED_CHANNEL_2);
    this.assertEventType(
        0x1102, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_IMAGE_SIZE_TOO_BIG_CHANNEL_2);
    this.assertEventType(
        0x1103, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_INVALID_BLOCK_NUMBER_CHANNEL_2);
    this.assertEventType(
        0x1104, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_DATA_RECEIVE_ERROR_CHANNEL_2);
    this.assertEventType(
        0x1105, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_IMAGE_NOT_COMPLETE_ERROR_CHANNEL_2);
    this.assertEventType(
        0x1106, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_INVALID_SECURITY_ERROR_CHANNEL_2);
    this.assertEventType(
        0x1107,
        "",
        "",
        "",
        EventTypeDto.MBUS_FW_UPGRADE_INVALID_FIRMWARE_FOR_THIS_DEVICE_CHANNEL_2);
    this.assertEventType(0x1200, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_SUCCESSFUL_CHANNEL_3);
    this.assertEventType(
        0x1201, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_BLOCK_SIZE_NOT_SUPPORTED_CHANNEL_3);
    this.assertEventType(
        0x1202, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_IMAGE_SIZE_TOO_BIG_CHANNEL_3);
    this.assertEventType(
        0x1203, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_INVALID_BLOCK_NUMBER_CHANNEL_3);
    this.assertEventType(
        0x1204, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_DATA_RECEIVE_ERROR_CHANNEL_3);
    this.assertEventType(
        0x1205, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_IMAGE_NOT_COMPLETE_ERROR_CHANNEL_3);
    this.assertEventType(
        0x1206, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_INVALID_SECURITY_ERROR_CHANNEL_3);
    this.assertEventType(
        0x1207,
        "",
        "",
        "",
        EventTypeDto.MBUS_FW_UPGRADE_INVALID_FIRMWARE_FOR_THIS_DEVICE_CHANNEL_3);
    this.assertEventType(0x1300, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_SUCCESSFUL_CHANNEL_4);
    this.assertEventType(
        0x1301, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_BLOCK_SIZE_NOT_SUPPORTED_CHANNEL_4);
    this.assertEventType(
        0x1302, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_IMAGE_SIZE_TOO_BIG_CHANNEL_4);
    this.assertEventType(
        0x1303, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_INVALID_BLOCK_NUMBER_CHANNEL_4);
    this.assertEventType(
        0x1304, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_DATA_RECEIVE_ERROR_CHANNEL_4);
    this.assertEventType(
        0x1305, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_IMAGE_NOT_COMPLETE_ERROR_CHANNEL_4);
    this.assertEventType(
        0x1306, "", "", "", EventTypeDto.MBUS_FW_UPGRADE_INVALID_SECURITY_ERROR_CHANNEL_4);
    this.assertEventType(
        0x1307,
        "",
        "",
        "",
        EventTypeDto.MBUS_FW_UPGRADE_INVALID_FIRMWARE_FOR_THIS_DEVICE_CHANNEL_4);

    this.assertEventType(0x8080, "", "", "", EventTypeDto.MBUS_STATUS_BIT_0_BATTERY_LOW_CHANNEL_1);
    this.assertEventType(
        0x8081, "", "", "", EventTypeDto.MBUS_STATUS_BIT_1_BATTERY_CONSUMPTION_TOO_HIGH_CHANNEL_1);
    this.assertEventType(0x8082, "", "", "", EventTypeDto.MBUS_STATUS_BIT_2_REVERSE_FLOW_CHANNEL_1);
    this.assertEventType(0x8083, "", "", "", EventTypeDto.MBUS_STATUS_BIT_3_TAMPER_P2_CHANNEL_1);
    this.assertEventType(0x8084, "", "", "", EventTypeDto.MBUS_STATUS_BIT_4_TAMPER_P0_CHANNEL_1);
    this.assertEventType(0x8085, "", "", "", EventTypeDto.MBUS_STATUS_BIT_5_TAMPER_CASE_CHANNEL_1);
    this.assertEventType(
        0x8086, "", "", "", EventTypeDto.MBUS_STATUS_BIT_6_TAMPER_MAGNETIC_CHANNEL_1);
    this.assertEventType(
        0x8087, "", "", "", EventTypeDto.MBUS_STATUS_BIT_7_TEMP_OUT_OF_RANGE_CHANNEL_1);
    this.assertEventType(
        0x8088, "", "", "", EventTypeDto.MBUS_STATUS_BIT_8_CLOCK_SYNC_ERROR_CHANNEL_1);
    this.assertEventType(0x8089, "", "", "", EventTypeDto.MBUS_STATUS_BIT_9_SW_ERROR_CHANNEL_1);
    this.assertEventType(
        0x808A, "", "", "", EventTypeDto.MBUS_STATUS_BIT_10_WATCHDOG_ERROR_CHANNEL_1);
    this.assertEventType(
        0x808B, "", "", "", EventTypeDto.MBUS_STATUS_BIT_11_SYSTEM_HW_ERROR_CHANNEL_1);
    this.assertEventType(
        0x808C, "", "", "", EventTypeDto.MBUS_STATUS_BIT_12_CFG_CALIBRATION_ERROR_CHANNEL_1);
    this.assertEventType(
        0x808D, "", "", "", EventTypeDto.MBUS_STATUS_BIT_13_HIGH_FLOW_GREATER_THAN_QMAX_CHANNEL_1);
    this.assertEventType(
        0x808E, "", "", "", EventTypeDto.MBUS_STATUS_BIT_14_TEMP_SENSOR_ERROR_CHANNEL_1);
    this.assertEventType(0x808F, "", "", "", EventTypeDto.MBUS_STATUS_BIT_15_RESERVED_CHANNEL_1);
    this.assertEventType(0x8090, "", "", "", EventTypeDto.MBUS_STATUS_BIT_16_P0_ENABLED_CHANNEL_1);
    this.assertEventType(
        0x8091, "", "", "", EventTypeDto.MBUS_STATUS_BIT_17_NEW_KEY_ACCEPTED_CHANNEL_1);
    this.assertEventType(
        0x8092, "", "", "", EventTypeDto.MBUS_STATUS_BIT_18_NEW_KEY_REJECTED_CHANNEL_1);
    this.assertEventType(0x8093, "", "", "", EventTypeDto.MBUS_STATUS_BIT_18_RESERVED_CHANNEL_1);
    this.assertEventType(
        0x8094, "", "", "", EventTypeDto.MBUS_STATUS_BIT_20_MANUFACTURER_SPECIFIC_CHANNEL_1);
    this.assertEventType(
        0x8095, "", "", "", EventTypeDto.MBUS_STATUS_BIT_21_MANUFACTURER_SPECIFIC_CHANNEL_1);
    this.assertEventType(
        0x8096, "", "", "", EventTypeDto.MBUS_STATUS_BIT_22_MANUFACTURER_SPECIFIC_CHANNEL_1);
    this.assertEventType(
        0x8097, "", "", "", EventTypeDto.MBUS_STATUS_BIT_23_MANUFACTURER_SPECIFIC_CHANNEL_1);
    this.assertEventType(
        0x8098, "", "", "", EventTypeDto.MBUS_STATUS_BIT_24_MANUFACTURER_SPECIFIC_CHANNEL_1);
    this.assertEventType(
        0x8099, "", "", "", EventTypeDto.MBUS_STATUS_BIT_25_MANUFACTURER_SPECIFIC_CHANNEL_1);
    this.assertEventType(
        0x809A, "", "", "", EventTypeDto.MBUS_STATUS_BIT_26_MANUFACTURER_SPECIFIC_CHANNEL_1);
    this.assertEventType(
        0x809B, "", "", "", EventTypeDto.MBUS_STATUS_BIT_27_MANUFACTURER_SPECIFIC_CHANNEL_1);
    this.assertEventType(
        0x809C, "", "", "", EventTypeDto.MBUS_STATUS_BIT_28_MANUFACTURER_SPECIFIC_CHANNEL_1);
    this.assertEventType(
        0x809D, "", "", "", EventTypeDto.MBUS_STATUS_BIT_29_MANUFACTURER_SPECIFIC_CHANNEL_1);
    this.assertEventType(
        0x809E, "", "", "", EventTypeDto.MBUS_STATUS_BIT_30_MANUFACTURER_SPECIFIC_CHANNEL_1);
    this.assertEventType(
        0x809F, "", "", "", EventTypeDto.MBUS_STATUS_BIT_31_MANUFACTURER_SPECIFIC_CHANNEL_1);
    this.assertEventType(0x80A0, "", "", "", EventTypeDto.KEY_SENT_TO_MBUS_DEVICE_ON_CHANNEL_1);
    this.assertEventType(
        0x80A1, "", "", "", EventTypeDto.KEY_ACKNOWLEDGED_BY_MBUS_DEVICE_ON_CHANNEL_1);

    this.assertEventType(0x8180, "", "", "", EventTypeDto.MBUS_STATUS_BIT_0_BATTERY_LOW_CHANNEL_2);
    this.assertEventType(
        0x8181, "", "", "", EventTypeDto.MBUS_STATUS_BIT_1_BATTERY_CONSUMPTION_TOO_HIGH_CHANNEL_2);
    this.assertEventType(0x8182, "", "", "", EventTypeDto.MBUS_STATUS_BIT_2_REVERSE_FLOW_CHANNEL_2);
    this.assertEventType(0x8183, "", "", "", EventTypeDto.MBUS_STATUS_BIT_3_TAMPER_P2_CHANNEL_2);
    this.assertEventType(0x8184, "", "", "", EventTypeDto.MBUS_STATUS_BIT_4_TAMPER_P0_CHANNEL_2);
    this.assertEventType(0x8185, "", "", "", EventTypeDto.MBUS_STATUS_BIT_5_TAMPER_CASE_CHANNEL_2);
    this.assertEventType(
        0x8186, "", "", "", EventTypeDto.MBUS_STATUS_BIT_6_TAMPER_MAGNETIC_CHANNEL_2);
    this.assertEventType(
        0x8187, "", "", "", EventTypeDto.MBUS_STATUS_BIT_7_TEMP_OUT_OF_RANGE_CHANNEL_2);
    this.assertEventType(
        0x8188, "", "", "", EventTypeDto.MBUS_STATUS_BIT_8_CLOCK_SYNC_ERROR_CHANNEL_2);
    this.assertEventType(0x8189, "", "", "", EventTypeDto.MBUS_STATUS_BIT_9_SW_ERROR_CHANNEL_2);
    this.assertEventType(
        0x818A, "", "", "", EventTypeDto.MBUS_STATUS_BIT_10_WATCHDOG_ERROR_CHANNEL_2);
    this.assertEventType(
        0x818B, "", "", "", EventTypeDto.MBUS_STATUS_BIT_11_SYSTEM_HW_ERROR_CHANNEL_2);
    this.assertEventType(
        0x818C, "", "", "", EventTypeDto.MBUS_STATUS_BIT_12_CFG_CALIBRATION_ERROR_CHANNEL_2);
    this.assertEventType(
        0x818D, "", "", "", EventTypeDto.MBUS_STATUS_BIT_13_HIGH_FLOW_GREATER_THAN_QMAX_CHANNEL_2);
    this.assertEventType(
        0x818E, "", "", "", EventTypeDto.MBUS_STATUS_BIT_14_TEMP_SENSOR_ERROR_CHANNEL_2);
    this.assertEventType(0x818F, "", "", "", EventTypeDto.MBUS_STATUS_BIT_15_RESERVED_CHANNEL_2);
    this.assertEventType(0x8190, "", "", "", EventTypeDto.MBUS_STATUS_BIT_16_P0_ENABLED_CHANNEL_2);
    this.assertEventType(
        0x8191, "", "", "", EventTypeDto.MBUS_STATUS_BIT_17_NEW_KEY_ACCEPTED_CHANNEL_2);
    this.assertEventType(
        0x8192, "", "", "", EventTypeDto.MBUS_STATUS_BIT_18_NEW_KEY_REJECTED_CHANNEL_2);
    this.assertEventType(0x8193, "", "", "", EventTypeDto.MBUS_STATUS_BIT_18_RESERVED_CHANNEL_2);
    this.assertEventType(
        0x8194, "", "", "", EventTypeDto.MBUS_STATUS_BIT_20_MANUFACTURER_SPECIFIC_CHANNEL_2);
    this.assertEventType(
        0x8195, "", "", "", EventTypeDto.MBUS_STATUS_BIT_21_MANUFACTURER_SPECIFIC_CHANNEL_2);
    this.assertEventType(
        0x8196, "", "", "", EventTypeDto.MBUS_STATUS_BIT_22_MANUFACTURER_SPECIFIC_CHANNEL_2);
    this.assertEventType(
        0x8197, "", "", "", EventTypeDto.MBUS_STATUS_BIT_23_MANUFACTURER_SPECIFIC_CHANNEL_2);
    this.assertEventType(
        0x8198, "", "", "", EventTypeDto.MBUS_STATUS_BIT_24_MANUFACTURER_SPECIFIC_CHANNEL_2);
    this.assertEventType(
        0x8199, "", "", "", EventTypeDto.MBUS_STATUS_BIT_25_MANUFACTURER_SPECIFIC_CHANNEL_2);
    this.assertEventType(
        0x819A, "", "", "", EventTypeDto.MBUS_STATUS_BIT_26_MANUFACTURER_SPECIFIC_CHANNEL_2);
    this.assertEventType(
        0x819B, "", "", "", EventTypeDto.MBUS_STATUS_BIT_27_MANUFACTURER_SPECIFIC_CHANNEL_2);
    this.assertEventType(
        0x819C, "", "", "", EventTypeDto.MBUS_STATUS_BIT_28_MANUFACTURER_SPECIFIC_CHANNEL_2);
    this.assertEventType(
        0x819D, "", "", "", EventTypeDto.MBUS_STATUS_BIT_29_MANUFACTURER_SPECIFIC_CHANNEL_2);
    this.assertEventType(
        0x819E, "", "", "", EventTypeDto.MBUS_STATUS_BIT_30_MANUFACTURER_SPECIFIC_CHANNEL_2);
    this.assertEventType(
        0x819F, "", "", "", EventTypeDto.MBUS_STATUS_BIT_31_MANUFACTURER_SPECIFIC_CHANNEL_2);
    this.assertEventType(0x81A0, "", "", "", EventTypeDto.KEY_SENT_TO_MBUS_DEVICE_ON_CHANNEL_2);
    this.assertEventType(
        0x81A1, "", "", "", EventTypeDto.KEY_ACKNOWLEDGED_BY_MBUS_DEVICE_ON_CHANNEL_2);

    this.assertEventType(0x8280, "", "", "", EventTypeDto.MBUS_STATUS_BIT_0_BATTERY_LOW_CHANNEL_3);
    this.assertEventType(
        0x8281, "", "", "", EventTypeDto.MBUS_STATUS_BIT_1_BATTERY_CONSUMPTION_TOO_HIGH_CHANNEL_3);
    this.assertEventType(0x8282, "", "", "", EventTypeDto.MBUS_STATUS_BIT_2_REVERSE_FLOW_CHANNEL_3);
    this.assertEventType(0x8283, "", "", "", EventTypeDto.MBUS_STATUS_BIT_3_TAMPER_P2_CHANNEL_3);
    this.assertEventType(0x8284, "", "", "", EventTypeDto.MBUS_STATUS_BIT_4_TAMPER_P0_CHANNEL_3);
    this.assertEventType(0x8285, "", "", "", EventTypeDto.MBUS_STATUS_BIT_5_TAMPER_CASE_CHANNEL_3);
    this.assertEventType(
        0x8286, "", "", "", EventTypeDto.MBUS_STATUS_BIT_6_TAMPER_MAGNETIC_CHANNEL_3);
    this.assertEventType(
        0x8287, "", "", "", EventTypeDto.MBUS_STATUS_BIT_7_TEMP_OUT_OF_RANGE_CHANNEL_3);
    this.assertEventType(
        0x8288, "", "", "", EventTypeDto.MBUS_STATUS_BIT_8_CLOCK_SYNC_ERROR_CHANNEL_3);
    this.assertEventType(0x8289, "", "", "", EventTypeDto.MBUS_STATUS_BIT_9_SW_ERROR_CHANNEL_3);
    this.assertEventType(
        0x828A, "", "", "", EventTypeDto.MBUS_STATUS_BIT_10_WATCHDOG_ERROR_CHANNEL_3);
    this.assertEventType(
        0x828B, "", "", "", EventTypeDto.MBUS_STATUS_BIT_11_SYSTEM_HW_ERROR_CHANNEL_3);
    this.assertEventType(
        0x828C, "", "", "", EventTypeDto.MBUS_STATUS_BIT_12_CFG_CALIBRATION_ERROR_CHANNEL_3);
    this.assertEventType(
        0x828D, "", "", "", EventTypeDto.MBUS_STATUS_BIT_13_HIGH_FLOW_GREATER_THAN_QMAX_CHANNEL_3);
    this.assertEventType(
        0x828E, "", "", "", EventTypeDto.MBUS_STATUS_BIT_14_TEMP_SENSOR_ERROR_CHANNEL_3);
    this.assertEventType(0x828F, "", "", "", EventTypeDto.MBUS_STATUS_BIT_15_RESERVED_CHANNEL_3);
    this.assertEventType(0x8290, "", "", "", EventTypeDto.MBUS_STATUS_BIT_16_P0_ENABLED_CHANNEL_3);
    this.assertEventType(
        0x8291, "", "", "", EventTypeDto.MBUS_STATUS_BIT_17_NEW_KEY_ACCEPTED_CHANNEL_3);
    this.assertEventType(
        0x8292, "", "", "", EventTypeDto.MBUS_STATUS_BIT_18_NEW_KEY_REJECTED_CHANNEL_3);
    this.assertEventType(0x8293, "", "", "", EventTypeDto.MBUS_STATUS_BIT_18_RESERVED_CHANNEL_3);
    this.assertEventType(
        0x8294, "", "", "", EventTypeDto.MBUS_STATUS_BIT_20_MANUFACTURER_SPECIFIC_CHANNEL_3);
    this.assertEventType(
        0x8295, "", "", "", EventTypeDto.MBUS_STATUS_BIT_21_MANUFACTURER_SPECIFIC_CHANNEL_3);
    this.assertEventType(
        0x8296, "", "", "", EventTypeDto.MBUS_STATUS_BIT_22_MANUFACTURER_SPECIFIC_CHANNEL_3);
    this.assertEventType(
        0x8297, "", "", "", EventTypeDto.MBUS_STATUS_BIT_23_MANUFACTURER_SPECIFIC_CHANNEL_3);
    this.assertEventType(
        0x8298, "", "", "", EventTypeDto.MBUS_STATUS_BIT_24_MANUFACTURER_SPECIFIC_CHANNEL_3);
    this.assertEventType(
        0x8299, "", "", "", EventTypeDto.MBUS_STATUS_BIT_25_MANUFACTURER_SPECIFIC_CHANNEL_3);
    this.assertEventType(
        0x829A, "", "", "", EventTypeDto.MBUS_STATUS_BIT_26_MANUFACTURER_SPECIFIC_CHANNEL_3);
    this.assertEventType(
        0x829B, "", "", "", EventTypeDto.MBUS_STATUS_BIT_27_MANUFACTURER_SPECIFIC_CHANNEL_3);
    this.assertEventType(
        0x829C, "", "", "", EventTypeDto.MBUS_STATUS_BIT_28_MANUFACTURER_SPECIFIC_CHANNEL_3);
    this.assertEventType(
        0x829D, "", "", "", EventTypeDto.MBUS_STATUS_BIT_29_MANUFACTURER_SPECIFIC_CHANNEL_3);
    this.assertEventType(
        0x829E, "", "", "", EventTypeDto.MBUS_STATUS_BIT_30_MANUFACTURER_SPECIFIC_CHANNEL_3);
    this.assertEventType(
        0x829F, "", "", "", EventTypeDto.MBUS_STATUS_BIT_31_MANUFACTURER_SPECIFIC_CHANNEL_3);
    this.assertEventType(0x82A0, "", "", "", EventTypeDto.KEY_SENT_TO_MBUS_DEVICE_ON_CHANNEL_3);
    this.assertEventType(
        0x82A1, "", "", "", EventTypeDto.KEY_ACKNOWLEDGED_BY_MBUS_DEVICE_ON_CHANNEL_3);

    this.assertEventType(0x8380, "", "", "", EventTypeDto.MBUS_STATUS_BIT_0_BATTERY_LOW_CHANNEL_4);
    this.assertEventType(
        0x8381, "", "", "", EventTypeDto.MBUS_STATUS_BIT_1_BATTERY_CONSUMPTION_TOO_HIGH_CHANNEL_4);
    this.assertEventType(0x8382, "", "", "", EventTypeDto.MBUS_STATUS_BIT_2_REVERSE_FLOW_CHANNEL_4);
    this.assertEventType(0x8383, "", "", "", EventTypeDto.MBUS_STATUS_BIT_3_TAMPER_P2_CHANNEL_4);
    this.assertEventType(0x8384, "", "", "", EventTypeDto.MBUS_STATUS_BIT_4_TAMPER_P0_CHANNEL_4);
    this.assertEventType(0x8385, "", "", "", EventTypeDto.MBUS_STATUS_BIT_5_TAMPER_CASE_CHANNEL_4);
    this.assertEventType(
        0x8386, "", "", "", EventTypeDto.MBUS_STATUS_BIT_6_TAMPER_MAGNETIC_CHANNEL_4);
    this.assertEventType(
        0x8387, "", "", "", EventTypeDto.MBUS_STATUS_BIT_7_TEMP_OUT_OF_RANGE_CHANNEL_4);
    this.assertEventType(
        0x8388, "", "", "", EventTypeDto.MBUS_STATUS_BIT_8_CLOCK_SYNC_ERROR_CHANNEL_4);
    this.assertEventType(0x8389, "", "", "", EventTypeDto.MBUS_STATUS_BIT_9_SW_ERROR_CHANNEL_4);
    this.assertEventType(
        0x838A, "", "", "", EventTypeDto.MBUS_STATUS_BIT_10_WATCHDOG_ERROR_CHANNEL_4);
    this.assertEventType(
        0x838B, "", "", "", EventTypeDto.MBUS_STATUS_BIT_11_SYSTEM_HW_ERROR_CHANNEL_4);
    this.assertEventType(
        0x838C, "", "", "", EventTypeDto.MBUS_STATUS_BIT_12_CFG_CALIBRATION_ERROR_CHANNEL_4);
    this.assertEventType(
        0x838D, "", "", "", EventTypeDto.MBUS_STATUS_BIT_13_HIGH_FLOW_GREATER_THAN_QMAX_CHANNEL_4);
    this.assertEventType(
        0x838E, "", "", "", EventTypeDto.MBUS_STATUS_BIT_14_TEMP_SENSOR_ERROR_CHANNEL_4);
    this.assertEventType(0x838F, "", "", "", EventTypeDto.MBUS_STATUS_BIT_15_RESERVED_CHANNEL_4);
    this.assertEventType(0x8390, "", "", "", EventTypeDto.MBUS_STATUS_BIT_16_P0_ENABLED_CHANNEL_4);
    this.assertEventType(
        0x8391, "", "", "", EventTypeDto.MBUS_STATUS_BIT_17_NEW_KEY_ACCEPTED_CHANNEL_4);
    this.assertEventType(
        0x8392, "", "", "", EventTypeDto.MBUS_STATUS_BIT_18_NEW_KEY_REJECTED_CHANNEL_4);
    this.assertEventType(0x8393, "", "", "", EventTypeDto.MBUS_STATUS_BIT_18_RESERVED_CHANNEL_4);
    this.assertEventType(
        0x8394, "", "", "", EventTypeDto.MBUS_STATUS_BIT_20_MANUFACTURER_SPECIFIC_CHANNEL_4);
    this.assertEventType(
        0x8395, "", "", "", EventTypeDto.MBUS_STATUS_BIT_21_MANUFACTURER_SPECIFIC_CHANNEL_4);
    this.assertEventType(
        0x8396, "", "", "", EventTypeDto.MBUS_STATUS_BIT_22_MANUFACTURER_SPECIFIC_CHANNEL_4);
    this.assertEventType(
        0x8397, "", "", "", EventTypeDto.MBUS_STATUS_BIT_23_MANUFACTURER_SPECIFIC_CHANNEL_4);
    this.assertEventType(
        0x8398, "", "", "", EventTypeDto.MBUS_STATUS_BIT_24_MANUFACTURER_SPECIFIC_CHANNEL_4);
    this.assertEventType(
        0x8399, "", "", "", EventTypeDto.MBUS_STATUS_BIT_25_MANUFACTURER_SPECIFIC_CHANNEL_4);
    this.assertEventType(
        0x839A, "", "", "", EventTypeDto.MBUS_STATUS_BIT_26_MANUFACTURER_SPECIFIC_CHANNEL_4);
    this.assertEventType(
        0x839B, "", "", "", EventTypeDto.MBUS_STATUS_BIT_27_MANUFACTURER_SPECIFIC_CHANNEL_4);
    this.assertEventType(
        0x839C, "", "", "", EventTypeDto.MBUS_STATUS_BIT_28_MANUFACTURER_SPECIFIC_CHANNEL_4);
    this.assertEventType(
        0x839D, "", "", "", EventTypeDto.MBUS_STATUS_BIT_29_MANUFACTURER_SPECIFIC_CHANNEL_4);
    this.assertEventType(
        0x839E, "", "", "", EventTypeDto.MBUS_STATUS_BIT_30_MANUFACTURER_SPECIFIC_CHANNEL_4);
    this.assertEventType(
        0x839F, "", "", "", EventTypeDto.MBUS_STATUS_BIT_31_MANUFACTURER_SPECIFIC_CHANNEL_4);
    this.assertEventType(0x83A0, "", "", "", EventTypeDto.KEY_SENT_TO_MBUS_DEVICE_ON_CHANNEL_4);
    this.assertEventType(
        0x83A1, "", "", "", EventTypeDto.KEY_ACKNOWLEDGED_BY_MBUS_DEVICE_ON_CHANNEL_4);
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
    assertThat(eventDetails.size()).isEqualTo(3);
    assertThat(eventDto.getEventDetailValue(EventDetailNameTypeDto.MANUFACTURER_CODE))
        .isEqualTo(manufacturerCode);
    assertThat(eventDto.getEventDetailValue(EventDetailNameTypeDto.DEVICE_TYPE))
        .isEqualTo(deviceType);
    assertThat(eventDto.getEventDetailValue(EventDetailNameTypeDto.PROTOCOL_NAME))
        .isEqualTo(protocol);
  }
}
