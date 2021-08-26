/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventMessageDataResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventTypeDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service(value = "eventService")
public class EventService {
  final DomainHelperService domainHelperService;
  final Map<Integer, List<EventTypeDtoLookup>> eventTypsByCode;

  private static final Logger LOGGER = LoggerFactory.getLogger(EventService.class);

  public EventService(final DomainHelperService domainHelperService) {
    this.domainHelperService = domainHelperService;
    this.eventTypsByCode =
        Arrays.stream(EventTypeDtoLookup.values())
            .collect(Collectors.groupingBy(EventTypeDtoLookup::getEventCode));
  }

  public void addEventTypeToEvents(
      final MessageMetadata deviceMessageMetadata, final EventMessageDataResponseDto responseDto)
      throws FunctionalException {

    LOGGER.info(
        "Enrich EventMessageDataResponse with EventTypes for device: {}",
        deviceMessageMetadata.getDeviceIdentification());
    final SmartMeter smartMeter =
        this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

    for (final EventDto eventDto : responseDto.getEvents()) {
      final EventTypeDto eventTypeDto = this.determineEventType(eventDto, smartMeter);
      eventDto.setEventTypeDto(eventTypeDto);
    }
  }

  private EventTypeDto determineEventType(final EventDto eventDto, final SmartMeter smartMeter)
      throws FunctionalException {
    final String deviceType = smartMeter.getDeviceType();
    final String protocolName =
        smartMeter.getProtocolInfo() != null
            ? smartMeter.getProtocolInfo().getProtocol()
            : "UNKNOWN";
    final String manufacturerCode =
        (smartMeter.getDeviceModel() != null
                && smartMeter.getDeviceModel().getManufacturer() != null)
            ? smartMeter.getDeviceModel().getManufacturer().getCode()
            : "UNKNOWN";

    final Integer eventCode = eventDto.getEventCode();

    final List<EventTypeDtoLookup> possibleEventTypes = this.eventTypsByCode.get(eventCode);
    if (possibleEventTypes == null) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.DOMAIN_SMART_METERING,
          new AssertionError("Event Type should be defined for eventCode: " + eventCode));
    }
    final List<EventTypeDtoLookup> eventTypes =
        possibleEventTypes.stream()
            .filter(
                lookup ->
                    lookup.getManufacturerCode() == null
                        || lookup.getManufacturerCode().equals(manufacturerCode))
            .filter(
                lookup ->
                    lookup.getDeviceType() == null || lookup.getDeviceType().equals(deviceType))
            .filter(
                lookup ->
                    lookup.getProtocol() == null || protocolName.startsWith(lookup.getProtocol()))
            .collect(Collectors.toList());
    if (eventTypes.size() == 1) {
      return eventTypes.get(0).getEventTypeDto();
    } else if (eventTypes.size() > 1) {

      /* Specific EventTypes overrule the Genric ones */
      final List<EventTypeDtoLookup> specificEventTypes =
          eventTypes.stream().filter(lookup -> !lookup.isGeneric()).collect(Collectors.toList());
      if (specificEventTypes.size() == 1) {
        return specificEventTypes.get(0).getEventTypeDto();
      }
    }

    throw new FunctionalException(
        FunctionalExceptionType.VALIDATION_ERROR,
        ComponentType.DOMAIN_SMART_METERING,
        new AssertionError(
            "Exactly one Event Type should be match eventCode: "
                + eventCode
                + ", deviceType: "
                + deviceType
                + ", protocolName: "
                + protocolName
                + ", manufacturerCode: "
                + manufacturerCode));
  }

  enum EventTypeDtoLookup {
    EVENTLOG_CLEARED(EventTypeDto.EVENTLOG_CLEARED, 255, null, null, null),
    POWER_FAILURE(EventTypeDto.POWER_FAILURE, 1, null, null, null),
    POWER_FAILURE_G(EventTypeDto.POWER_FAILURE_G, 1, null, null, "SMART_METER_G"),
    POWER_FAILURE_W(EventTypeDto.POWER_FAILURE_W, 1, null, null, "SMART_METER_W"),
    POWER_RETURNED(EventTypeDto.POWER_RETURNED, 2, null, null, null),
    CLOCK_UPDATE(EventTypeDto.CLOCK_UPDATE, 3, null, null, null),
    CLOCK_ADJUSTED_OLD_TIME(EventTypeDto.CLOCK_ADJUSTED_OLD_TIME, 4, null, null, null),
    CLOCK_ADJUSTED_NEW_TIME(EventTypeDto.CLOCK_ADJUSTED_NEW_TIME, 5, null, null, null),
    CLOCK_INVALID(EventTypeDto.CLOCK_INVALID, 6, null, null, null),
    REPLACE_BATTERY(EventTypeDto.REPLACE_BATTERY, 7, null, null, null),
    BATTERY_VOLTAGE_LOW(EventTypeDto.BATTERY_VOLTAGE_LOW, 8, null, null, null),
    TARIFF_ACTIVATED(EventTypeDto.TARIFF_ACTIVATED, 9, null, null, null),
    ERROR_REGISTER_CLEARED(EventTypeDto.ERROR_REGISTER_CLEARED, 10, null, null, null),
    ALARM_REGISTER_CLEARED(EventTypeDto.ALARM_REGISTER_CLEARED, 11, null, null, null),
    HARDWARE_ERROR_PROGRAM_MEMORY(EventTypeDto.HARDWARE_ERROR_PROGRAM_MEMORY, 12, null, null, null),
    HARDWARE_ERROR_RAM(EventTypeDto.HARDWARE_ERROR_RAM, 13, null, null, null),
    HARDWARE_ERROR_NV_MEMORY(EventTypeDto.HARDWARE_ERROR_NV_MEMORY, 14, null, null, null),
    WATCHDOG_ERROR(EventTypeDto.WATCHDOG_ERROR, 15, null, null, null),
    HARDWARE_ERROR_MEASUREMENT_SYSTEM(
        EventTypeDto.HARDWARE_ERROR_MEASUREMENT_SYSTEM, 16, null, null, null),
    FIRMWARE_READY_FOR_ACTIVATION(EventTypeDto.FIRMWARE_READY_FOR_ACTIVATION, 17, null, null, null),
    FIRMWARE_ACTIVATED(EventTypeDto.FIRMWARE_ACTIVATED, 18, null, null, null),
    PASSIVE_TARIFF_UPDATED(EventTypeDto.PASSIVE_TARIFF_UPDATED, 19, null, null, null),
    SUCCESSFUL_SELFCHECK_AFTER_FIRMWARE_UPDATE(
        EventTypeDto.SUCCESSFUL_SELFCHECK_AFTER_FIRMWARE_UPDATE, 20, null, null, null),
    COMMUNICATION_MODULE_REMOVED(EventTypeDto.COMMUNICATION_MODULE_REMOVED, 21, null, null, null),
    COMMUNICATION_MODULE_INSERTED(EventTypeDto.COMMUNICATION_MODULE_INSERTED, 22, null, null, null),
    TERMINAL_COVER_REMOVED(EventTypeDto.TERMINAL_COVER_REMOVED, 40, null, null, null),
    TERMINAL_COVER_CLOSED(EventTypeDto.TERMINAL_COVER_CLOSED, 41, null, null, null),
    STRONG_DC_FIELD_DETECTED(EventTypeDto.STRONG_DC_FIELD_DETECTED, 42, null, null, null),
    NO_STRONG_DC_FIELD_ANYMORE(EventTypeDto.NO_STRONG_DC_FIELD_ANYMORE, 43, null, null, null),
    METER_COVER_REMOVED(EventTypeDto.METER_COVER_REMOVED, 44, null, null, null),
    METER_COVER_CLOSED(EventTypeDto.METER_COVER_CLOSED, 45, null, null, null),
    FAILED_LOGIN_ATTEMPT(EventTypeDto.FAILED_LOGIN_ATTEMPT, 46, null, null, null),
    CONFIGURATION_CHANGE(EventTypeDto.CONFIGURATION_CHANGE, 47, null, null, null),
    MODULE_COVER_OPENED(EventTypeDto.MODULE_COVER_OPENED, 48, null, null, null),
    MODULE_COVER_CLOSED(EventTypeDto.MODULE_COVER_CLOSED, 49, null, null, null),
    METROLOGICAL_MAINTENANCE(EventTypeDto.METROLOGICAL_MAINTENANCE, 71, null, null, null),
    TECHNICAL_MAINTENANCE(EventTypeDto.TECHNICAL_MAINTENANCE, 72, null, null, null),
    RETRIEVE_METER_READINGS_E(EventTypeDto.RETRIEVE_METER_READINGS_E, 73, null, null, null),
    RETRIEVE_METER_READINGS_G(EventTypeDto.RETRIEVE_METER_READINGS_G, 74, null, null, null),
    RETRIEVE_INTERVAL_DATA_E(EventTypeDto.RETRIEVE_INTERVAL_DATA_E, 75, null, null, null),
    RETRIEVE_INTERVAL_DATA_G(EventTypeDto.RETRIEVE_INTERVAL_DATA_G, 76, null, null, null),
    UNDER_VOLTAGE_L1(EventTypeDto.UNDER_VOLTAGE_L1, 77, null, null, null),
    UNDER_VOLTAGE_L2(EventTypeDto.UNDER_VOLTAGE_L2, 78, null, null, null),
    UNDER_VOLTAGE_L3(EventTypeDto.UNDER_VOLTAGE_L3, 79, null, null, null),
    PV_VOLTAGE_SAG_L1(EventTypeDto.PV_VOLTAGE_SAG_L1, 80, "DSMR", null, null),
    PV_VOLTAGE_SAG_L2(EventTypeDto.PV_VOLTAGE_SAG_L2, 81, "DSMR", null, null),
    PV_VOLTAGE_SAG_L3(EventTypeDto.PV_VOLTAGE_SAG_L3, 82, "DSMR", null, null),
    PV_VOLTAGE_SWELL_L1(EventTypeDto.PV_VOLTAGE_SWELL_L1, 83, "DSMR", null, null),
    PV_VOLTAGE_SWELL_L2(EventTypeDto.PV_VOLTAGE_SWELL_L2, 84, "DSMR", null, null),
    PV_VOLTAGE_SWELL_L3(EventTypeDto.PV_VOLTAGE_SWELL_L3, 85, "DSMR", null, null),
    OVER_VOLTAGE_L1(EventTypeDto.OVER_VOLTAGE_L1, 80, "SMR", null, null),
    OVER_VOLTAGE_L2(EventTypeDto.OVER_VOLTAGE_L2, 81, "SMR", null, null),
    OVER_VOLTAGE_L3(EventTypeDto.OVER_VOLTAGE_L3, 82, "SMR", null, null),
    VOLTAGE_L1_NORMAL(EventTypeDto.VOLTAGE_L1_NORMAL, 83, "SMR", null, null),
    VOLTAGE_L2_NORMAL(EventTypeDto.VOLTAGE_L2_NORMAL, 84, "SMR", null, null),
    VOLTAGE_L3_NORMAL(EventTypeDto.VOLTAGE_L3_NORMAL, 85, "SMR", null, null),
    PHASE_OUTAGE_L1(EventTypeDto.PHASE_OUTAGE_L1, 86, null, null, null),
    PHASE_OUTAGE_L2(EventTypeDto.PHASE_OUTAGE_L2, 87, null, null, null),
    PHASE_OUTAGE_L3(EventTypeDto.PHASE_OUTAGE_L3, 88, null, null, null),
    PHASE_OUTAGE_TEST(EventTypeDto.PHASE_OUTAGE_TEST, 89, null, null, null),
    PHASE_RETURNED_L1(EventTypeDto.PHASE_RETURNED_L1, 90, null, null, null),
    PHASE_RETURNED_L2(EventTypeDto.PHASE_RETURNED_L2, 91, null, null, null),
    PHASE_RETURNED_L3(EventTypeDto.PHASE_RETURNED_L3, 92, null, null, null),
    COMMUNICATION_ERROR_M_BUS_CHANNEL_1(
        EventTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_1, 100, null, null, null),
    COMMUNICATION_OK_M_BUS_CHANNEL_1(
        EventTypeDto.COMMUNICATION_OK_M_BUS_CHANNEL_1, 101, null, null, null),
    REPLACE_BATTERY_M_BUS_CHANNEL_1(
        EventTypeDto.REPLACE_BATTERY_M_BUS_CHANNEL_1, 102, null, null, null),
    FRAUD_ATTEMPT_M_BUS_CHANNEL_1(
        EventTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_1, 103, null, null, null),
    CLOCK_ADJUSTED_M_BUS_CHANNEL_1(
        EventTypeDto.CLOCK_ADJUSTED_M_BUS_CHANNEL_1, 104, null, null, null),
    NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1(
        EventTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1, 105, null, null, null),
    PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_1(
        EventTypeDto.PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_1, 106, null, null, null),
    COMMUNICATION_ERROR_M_BUS_CHANNEL_2(
        EventTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_2, 110, null, null, null),
    COMMUNICATION_OK_M_BUS_CHANNEL_2(
        EventTypeDto.COMMUNICATION_OK_M_BUS_CHANNEL_2, 111, null, null, null),
    REPLACE_BATTERY_M_BUS_CHANNEL_2(
        EventTypeDto.REPLACE_BATTERY_M_BUS_CHANNEL_2, 112, null, null, null),
    FRAUD_ATTEMPT_M_BUS_CHANNEL_2(
        EventTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_2, 113, null, null, null),
    CLOCK_ADJUSTED_M_BUS_CHANNEL_2(
        EventTypeDto.CLOCK_ADJUSTED_M_BUS_CHANNEL_2, 114, null, null, null),
    NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2(
        EventTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2, 115, null, null, null),
    PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_2(
        EventTypeDto.PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_2, 116, null, null, null),
    COMMUNICATION_ERROR_M_BUS_CHANNEL_3(
        EventTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_3, 120, null, null, null),
    COMMUNICATION_OK_M_BUS_CHANNEL_3(
        EventTypeDto.COMMUNICATION_OK_M_BUS_CHANNEL_3, 121, null, null, null),
    REPLACE_BATTERY_M_BUS_CHANNEL_3(
        EventTypeDto.REPLACE_BATTERY_M_BUS_CHANNEL_3, 122, null, null, null),
    FRAUD_ATTEMPT_M_BUS_CHANNEL_3(
        EventTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_3, 123, null, null, null),
    CLOCK_ADJUSTED_M_BUS_CHANNEL_3(
        EventTypeDto.CLOCK_ADJUSTED_M_BUS_CHANNEL_3, 124, null, null, null),
    NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3(
        EventTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3, 125, null, null, null),
    PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_3(
        EventTypeDto.PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_3, 126, null, null, null),
    COMMUNICATION_ERROR_M_BUS_CHANNEL_4(
        EventTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_4, 130, null, null, null),
    COMMUNICATION_OK_M_BUS_CHANNEL_4(
        EventTypeDto.COMMUNICATION_OK_M_BUS_CHANNEL_4, 131, null, null, null),
    REPLACE_BATTERY_M_BUS_CHANNEL_4(
        EventTypeDto.REPLACE_BATTERY_M_BUS_CHANNEL_4, 132, null, null, null),
    FRAUD_ATTEMPT_M_BUS_CHANNEL_4(
        EventTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_4, 133, null, null, null),
    CLOCK_ADJUSTED_M_BUS_CHANNEL_4(
        EventTypeDto.CLOCK_ADJUSTED_M_BUS_CHANNEL_4, 134, null, null, null),
    NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4(
        EventTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4, 135, null, null, null),
    PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_4(
        EventTypeDto.PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_4, 136, null, null, null),
    MANUFACTURER_SPECIFIC_231(EventTypeDto.MANUFACTURER_SPECIFIC_231, 231, null, null, null),
    MANUFACTURER_SPECIFIC_232(EventTypeDto.MANUFACTURER_SPECIFIC_232, 232, null, null, null),
    MANUFACTURER_SPECIFIC_233(EventTypeDto.MANUFACTURER_SPECIFIC_233, 233, null, null, null),
    MANUFACTURER_SPECIFIC_234(EventTypeDto.MANUFACTURER_SPECIFIC_234, 234, null, null, null),
    MANUFACTURER_SPECIFIC_235(EventTypeDto.MANUFACTURER_SPECIFIC_235, 235, null, null, null),
    MANUFACTURER_SPECIFIC_236(EventTypeDto.MANUFACTURER_SPECIFIC_236, 236, null, null, null),
    MANUFACTURER_SPECIFIC_237(EventTypeDto.MANUFACTURER_SPECIFIC_237, 237, null, null, null),
    MANUFACTURER_SPECIFIC_238(EventTypeDto.MANUFACTURER_SPECIFIC_238, 238, null, null, null),
    MANUFACTURER_SPECIFIC_239(EventTypeDto.MANUFACTURER_SPECIFIC_239, 239, null, null, null),
    MANUFACTURER_SPECIFIC_240(EventTypeDto.MANUFACTURER_SPECIFIC_240, 240, null, null, null),
    MANUFACTURER_SPECIFIC_241(EventTypeDto.MANUFACTURER_SPECIFIC_241, 241, null, null, null),
    MANUFACTURER_SPECIFIC_242(EventTypeDto.MANUFACTURER_SPECIFIC_242, 242, null, null, null),
    MANUFACTURER_SPECIFIC_243(EventTypeDto.MANUFACTURER_SPECIFIC_243, 243, null, null, null),
    MANUFACTURER_SPECIFIC_244(EventTypeDto.MANUFACTURER_SPECIFIC_244, 244, null, null, null),
    MANUFACTURER_SPECIFIC_245(EventTypeDto.MANUFACTURER_SPECIFIC_245, 245, null, null, null),
    MANUFACTURER_SPECIFIC_246(EventTypeDto.MANUFACTURER_SPECIFIC_246, 246, null, null, null),
    MANUFACTURER_SPECIFIC_247(EventTypeDto.MANUFACTURER_SPECIFIC_247, 247, null, null, null),
    MANUFACTURER_SPECIFIC_248(EventTypeDto.MANUFACTURER_SPECIFIC_248, 248, null, null, null),
    MANUFACTURER_SPECIFIC_249(EventTypeDto.MANUFACTURER_SPECIFIC_249, 249, null, null, null),
    FATAL_ERROR_ISKR(EventTypeDto.FATAL_ERROR_ISKR, 230, null, "Iskr", null),
    BILLING_RESET_ISKR(EventTypeDto.BILLING_RESET_ISKR, 231, null, "Iskr", null),
    POWER_DOWN_PHASE_L1_ISKR(EventTypeDto.POWER_DOWN_PHASE_L1_ISKR, 232, null, "Iskr", null),
    POWER_DOWN_PHASE_L2_ISKR(EventTypeDto.POWER_DOWN_PHASE_L2_ISKR, 233, null, "Iskr", null),
    POWER_DOWN_PHASE_L3_ISKR(EventTypeDto.POWER_DOWN_PHASE_L3_ISKR, 234, null, "Iskr", null),
    POWER_RESTORED_PHASE_L1_ISKR(
        EventTypeDto.POWER_RESTORED_PHASE_L1_ISKR, 235, null, "Iskr", null),
    POWER_RESTORED_PHASE_L2_ISKR(
        EventTypeDto.POWER_RESTORED_PHASE_L2_ISKR, 236, null, "Iskr", null),
    POWER_RESTORED_PHASE_L3_ISKR(
        EventTypeDto.POWER_RESTORED_PHASE_L3_ISKR, 237, null, "Iskr", null),
    MODULE_COVER_OPENED_ISKR(EventTypeDto.MODULE_COVER_OPENED_ISKR, 244, null, "Iskr", null),
    MODULE_COVER_CLOSED_ISKR(EventTypeDto.MODULE_COVER_CLOSED_ISKR, 245, null, "Iskr", null),

    AUXILIARY_EVENTLOG_CLEARED(EventTypeDto.AUXILIARY_EVENTLOG_CLEARED, 0xFFFF, null, null, null),
    MBUS_FW_UPGRADE_SUCCESSFUL_CHANNEL_1(
        EventTypeDto.MBUS_FW_UPGRADE_SUCCESSFUL_CHANNEL_1, 0x1000, null, null, null),
    MBUS_FW_UPGRADE_BLOCK_SIZE_NOT_SUPPORTED_CHANNEL_1(
        EventTypeDto.MBUS_FW_UPGRADE_BLOCK_SIZE_NOT_SUPPORTED_CHANNEL_1, 0x1001, null, null, null),
    MBUS_FW_UPGRADE_IMAGE_SIZE_TOO_BIG_CHANNEL_1(
        EventTypeDto.MBUS_FW_UPGRADE_IMAGE_SIZE_TOO_BIG_CHANNEL_1, 0x1002, null, null, null),
    MBUS_FW_UPGRADE_INVALID_BLOCK_NUMBER_CHANNEL_1(
        EventTypeDto.MBUS_FW_UPGRADE_INVALID_BLOCK_NUMBER_CHANNEL_1, 0x1003, null, null, null),
    MBUS_FW_UPGRADE_DATA_RECEIVE_ERROR_CHANNEL_1(
        EventTypeDto.MBUS_FW_UPGRADE_DATA_RECEIVE_ERROR_CHANNEL_1, 0x1004, null, null, null),
    MBUS_FW_UPGRADE_IMAGE_NOT_COMPLETE_ERROR_CHANNEL_1(
        EventTypeDto.MBUS_FW_UPGRADE_IMAGE_NOT_COMPLETE_ERROR_CHANNEL_1, 0x1005, null, null, null),
    MBUS_FW_UPGRADE_INVALID_SECURITY_ERROR_CHANNEL_1(
        EventTypeDto.MBUS_FW_UPGRADE_INVALID_SECURITY_ERROR_CHANNEL_1, 0x1006, null, null, null),
    MBUS_FW_UPGRADE_INVALID_FIRMWARE_FOR_THIS_DEVICE_CHANNEL_1(
        EventTypeDto.MBUS_FW_UPGRADE_INVALID_FIRMWARE_FOR_THIS_DEVICE_CHANNEL_1,
        0x1007,
        null,
        null,
        null),
    MBUS_FW_UPGRADE_SUCCESSFUL_CHANNEL_2(
        EventTypeDto.MBUS_FW_UPGRADE_SUCCESSFUL_CHANNEL_2, 0x1100, null, null, null),
    MBUS_FW_UPGRADE_BLOCK_SIZE_NOT_SUPPORTED_CHANNEL_2(
        EventTypeDto.MBUS_FW_UPGRADE_BLOCK_SIZE_NOT_SUPPORTED_CHANNEL_2, 0x1101, null, null, null),
    MBUS_FW_UPGRADE_IMAGE_SIZE_TOO_BIG_CHANNEL_2(
        EventTypeDto.MBUS_FW_UPGRADE_IMAGE_SIZE_TOO_BIG_CHANNEL_2, 0x1102, null, null, null),
    MBUS_FW_UPGRADE_INVALID_BLOCK_NUMBER_CHANNEL_2(
        EventTypeDto.MBUS_FW_UPGRADE_INVALID_BLOCK_NUMBER_CHANNEL_2, 0x1103, null, null, null),
    MBUS_FW_UPGRADE_DATA_RECEIVE_ERROR_CHANNEL_2(
        EventTypeDto.MBUS_FW_UPGRADE_DATA_RECEIVE_ERROR_CHANNEL_2, 0x1104, null, null, null),
    MBUS_FW_UPGRADE_IMAGE_NOT_COMPLETE_ERROR_CHANNEL_2(
        EventTypeDto.MBUS_FW_UPGRADE_IMAGE_NOT_COMPLETE_ERROR_CHANNEL_2, 0x1105, null, null, null),
    MBUS_FW_UPGRADE_INVALID_SECURITY_ERROR_CHANNEL_2(
        EventTypeDto.MBUS_FW_UPGRADE_INVALID_SECURITY_ERROR_CHANNEL_2, 0x1106, null, null, null),
    MBUS_FW_UPGRADE_INVALID_FIRMWARE_FOR_THIS_DEVICE_CHANNEL_2(
        EventTypeDto.MBUS_FW_UPGRADE_INVALID_FIRMWARE_FOR_THIS_DEVICE_CHANNEL_2,
        0x1107,
        null,
        null,
        null),
    MBUS_FW_UPGRADE_SUCCESSFUL_CHANNEL_3(
        EventTypeDto.MBUS_FW_UPGRADE_SUCCESSFUL_CHANNEL_3, 0x1200, null, null, null),
    MBUS_FW_UPGRADE_BLOCK_SIZE_NOT_SUPPORTED_CHANNEL_3(
        EventTypeDto.MBUS_FW_UPGRADE_BLOCK_SIZE_NOT_SUPPORTED_CHANNEL_3, 0x1201, null, null, null),
    MBUS_FW_UPGRADE_IMAGE_SIZE_TOO_BIG_CHANNEL_3(
        EventTypeDto.MBUS_FW_UPGRADE_IMAGE_SIZE_TOO_BIG_CHANNEL_3, 0x1202, null, null, null),
    MBUS_FW_UPGRADE_INVALID_BLOCK_NUMBER_CHANNEL_3(
        EventTypeDto.MBUS_FW_UPGRADE_INVALID_BLOCK_NUMBER_CHANNEL_3, 0x1203, null, null, null),
    MBUS_FW_UPGRADE_DATA_RECEIVE_ERROR_CHANNEL_3(
        EventTypeDto.MBUS_FW_UPGRADE_DATA_RECEIVE_ERROR_CHANNEL_3, 0x1204, null, null, null),
    MBUS_FW_UPGRADE_IMAGE_NOT_COMPLETE_ERROR_CHANNEL_3(
        EventTypeDto.MBUS_FW_UPGRADE_IMAGE_NOT_COMPLETE_ERROR_CHANNEL_3, 0x1205, null, null, null),
    MBUS_FW_UPGRADE_INVALID_SECURITY_ERROR_CHANNEL_3(
        EventTypeDto.MBUS_FW_UPGRADE_INVALID_SECURITY_ERROR_CHANNEL_3, 0x1206, null, null, null),
    MBUS_FW_UPGRADE_INVALID_FIRMWARE_FOR_THIS_DEVICE_CHANNEL_3(
        EventTypeDto.MBUS_FW_UPGRADE_INVALID_FIRMWARE_FOR_THIS_DEVICE_CHANNEL_3,
        0x1207,
        null,
        null,
        null),
    MBUS_FW_UPGRADE_SUCCESSFUL_CHANNEL_4(
        EventTypeDto.MBUS_FW_UPGRADE_SUCCESSFUL_CHANNEL_4, 0x1300, null, null, null),
    MBUS_FW_UPGRADE_BLOCK_SIZE_NOT_SUPPORTED_CHANNEL_4(
        EventTypeDto.MBUS_FW_UPGRADE_BLOCK_SIZE_NOT_SUPPORTED_CHANNEL_4, 0x1301, null, null, null),
    MBUS_FW_UPGRADE_IMAGE_SIZE_TOO_BIG_CHANNEL_4(
        EventTypeDto.MBUS_FW_UPGRADE_IMAGE_SIZE_TOO_BIG_CHANNEL_4, 0x1302, null, null, null),
    MBUS_FW_UPGRADE_INVALID_BLOCK_NUMBER_CHANNEL_4(
        EventTypeDto.MBUS_FW_UPGRADE_INVALID_BLOCK_NUMBER_CHANNEL_4, 0x1303, null, null, null),
    MBUS_FW_UPGRADE_DATA_RECEIVE_ERROR_CHANNEL_4(
        EventTypeDto.MBUS_FW_UPGRADE_DATA_RECEIVE_ERROR_CHANNEL_4, 0x1304, null, null, null),
    MBUS_FW_UPGRADE_IMAGE_NOT_COMPLETE_ERROR_CHANNEL_4(
        EventTypeDto.MBUS_FW_UPGRADE_IMAGE_NOT_COMPLETE_ERROR_CHANNEL_4, 0x1305, null, null, null),
    MBUS_FW_UPGRADE_INVALID_SECURITY_ERROR_CHANNEL_4(
        EventTypeDto.MBUS_FW_UPGRADE_INVALID_SECURITY_ERROR_CHANNEL_4, 0x1306, null, null, null),
    MBUS_FW_UPGRADE_INVALID_FIRMWARE_FOR_THIS_DEVICE_CHANNEL_4(
        EventTypeDto.MBUS_FW_UPGRADE_INVALID_FIRMWARE_FOR_THIS_DEVICE_CHANNEL_4,
        0x1307,
        null,
        null,
        null),

    MBUS_STATUS_BIT_0_BATTERY_LOW_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_0_BATTERY_LOW_CHANNEL_1, 0x8080, null, null, null),
    MBUS_STATUS_BIT_1_BATTERY_CONSUMPTION_TOO_HIGH_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_1_BATTERY_CONSUMPTION_TOO_HIGH_CHANNEL_1,
        0x8081,
        null,
        null,
        null),
    MBUS_STATUS_BIT_2_REVERSE_FLOW_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_2_REVERSE_FLOW_CHANNEL_1, 0x8082, null, null, null),
    MBUS_STATUS_BIT_3_TAMPER_P2_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_3_TAMPER_P2_CHANNEL_1, 0x8083, null, null, null),
    MBUS_STATUS_BIT_4_TAMPER_P0_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_4_TAMPER_P0_CHANNEL_1, 0x8084, null, null, null),
    MBUS_STATUS_BIT_5_TAMPER_CASE_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_5_TAMPER_CASE_CHANNEL_1, 0x8085, null, null, null),
    MBUS_STATUS_BIT_6_TAMPER_MAGNETIC_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_6_TAMPER_MAGNETIC_CHANNEL_1, 0x8086, null, null, null),
    MBUS_STATUS_BIT_7_TEMP_OUT_OF_RANGE_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_7_TEMP_OUT_OF_RANGE_CHANNEL_1, 0x8087, null, null, null),
    MBUS_STATUS_BIT_8_CLOCK_SYNC_ERROR_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_8_CLOCK_SYNC_ERROR_CHANNEL_1, 0x8088, null, null, null),
    MBUS_STATUS_BIT_9_SW_ERROR_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_9_SW_ERROR_CHANNEL_1, 0x8089, null, null, null),
    MBUS_STATUS_BIT_10_WATCHDOG_ERROR_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_10_WATCHDOG_ERROR_CHANNEL_1, 0x808A, null, null, null),
    MBUS_STATUS_BIT_11_SYSTEM_HW_ERROR_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_11_SYSTEM_HW_ERROR_CHANNEL_1, 0x808B, null, null, null),
    MBUS_STATUS_BIT_12_CFG_CALIBRATION_ERROR_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_12_CFG_CALIBRATION_ERROR_CHANNEL_1, 0x808C, null, null, null),
    MBUS_STATUS_BIT_13_HIGH_FLOW_GREATER_THAN_QMAX_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_13_HIGH_FLOW_GREATER_THAN_QMAX_CHANNEL_1,
        0x808D,
        null,
        null,
        null),
    MBUS_STATUS_BIT_14_TEMP_SENSOR_ERROR_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_14_TEMP_SENSOR_ERROR_CHANNEL_1, 0x808E, null, null, null),
    MBUS_STATUS_BIT_15_RESERVED_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_15_RESERVED_CHANNEL_1, 0x808F, null, null, null),
    MBUS_STATUS_BIT_16_P0_ENABLED_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_16_P0_ENABLED_CHANNEL_1, 0x8090, null, null, null),
    MBUS_STATUS_BIT_17_NEW_KEY_ACCEPTED_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_17_NEW_KEY_ACCEPTED_CHANNEL_1, 0x8091, null, null, null),
    MBUS_STATUS_BIT_18_NEW_KEY_REJECTED_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_18_NEW_KEY_REJECTED_CHANNEL_1, 0x8092, null, null, null),
    MBUS_STATUS_BIT_18_RESERVED_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_18_RESERVED_CHANNEL_1, 0x8093, null, null, null),
    MBUS_STATUS_BIT_20_MANUFACTURER_SPECIFIC_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_20_MANUFACTURER_SPECIFIC_CHANNEL_1, 0x8094, null, null, null),
    MBUS_STATUS_BIT_21_MANUFACTURER_SPECIFIC_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_21_MANUFACTURER_SPECIFIC_CHANNEL_1, 0x8095, null, null, null),
    MBUS_STATUS_BIT_22_MANUFACTURER_SPECIFIC_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_22_MANUFACTURER_SPECIFIC_CHANNEL_1, 0x8096, null, null, null),
    MBUS_STATUS_BIT_23_MANUFACTURER_SPECIFIC_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_23_MANUFACTURER_SPECIFIC_CHANNEL_1, 0x8097, null, null, null),
    MBUS_STATUS_BIT_24_MANUFACTURER_SPECIFIC_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_24_MANUFACTURER_SPECIFIC_CHANNEL_1, 0x8098, null, null, null),
    MBUS_STATUS_BIT_25_MANUFACTURER_SPECIFIC_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_25_MANUFACTURER_SPECIFIC_CHANNEL_1, 0x8099, null, null, null),
    MBUS_STATUS_BIT_26_MANUFACTURER_SPECIFIC_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_26_MANUFACTURER_SPECIFIC_CHANNEL_1, 0x809A, null, null, null),
    MBUS_STATUS_BIT_27_MANUFACTURER_SPECIFIC_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_27_MANUFACTURER_SPECIFIC_CHANNEL_1, 0x809B, null, null, null),
    MBUS_STATUS_BIT_28_MANUFACTURER_SPECIFIC_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_28_MANUFACTURER_SPECIFIC_CHANNEL_1, 0x809C, null, null, null),
    MBUS_STATUS_BIT_29_MANUFACTURER_SPECIFIC_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_29_MANUFACTURER_SPECIFIC_CHANNEL_1, 0x809D, null, null, null),
    MBUS_STATUS_BIT_30_MANUFACTURER_SPECIFIC_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_30_MANUFACTURER_SPECIFIC_CHANNEL_1, 0x809E, null, null, null),
    MBUS_STATUS_BIT_31_MANUFACTURER_SPECIFIC_CHANNEL_1(
        EventTypeDto.MBUS_STATUS_BIT_31_MANUFACTURER_SPECIFIC_CHANNEL_1, 0x809F, null, null, null),
    KEY_SENT_TO_MBUS_DEVICE_ON_CHANNEL_1(
        EventTypeDto.KEY_SENT_TO_MBUS_DEVICE_ON_CHANNEL_1, 0x80A0, null, null, null),
    KEY_ACKNOWLEDGED_BY_MBUS_DEVICE_ON_CHANNEL_1(
        EventTypeDto.KEY_ACKNOWLEDGED_BY_MBUS_DEVICE_ON_CHANNEL_1, 0x80A1, null, null, null),

    MBUS_STATUS_BIT_0_BATTERY_LOW_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_0_BATTERY_LOW_CHANNEL_2, 0x8180, null, null, null),
    MBUS_STATUS_BIT_1_BATTERY_CONSUMPTION_TOO_HIGH_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_1_BATTERY_CONSUMPTION_TOO_HIGH_CHANNEL_2,
        0x8181,
        null,
        null,
        null),
    MBUS_STATUS_BIT_2_REVERSE_FLOW_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_2_REVERSE_FLOW_CHANNEL_2, 0x8182, null, null, null),
    MBUS_STATUS_BIT_3_TAMPER_P2_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_3_TAMPER_P2_CHANNEL_2, 0x8183, null, null, null),
    MBUS_STATUS_BIT_4_TAMPER_P0_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_4_TAMPER_P0_CHANNEL_2, 0x8184, null, null, null),
    MBUS_STATUS_BIT_5_TAMPER_CASE_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_5_TAMPER_CASE_CHANNEL_2, 0x8185, null, null, null),
    MBUS_STATUS_BIT_6_TAMPER_MAGNETIC_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_6_TAMPER_MAGNETIC_CHANNEL_2, 0x8186, null, null, null),
    MBUS_STATUS_BIT_7_TEMP_OUT_OF_RANGE_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_7_TEMP_OUT_OF_RANGE_CHANNEL_2, 0x8187, null, null, null),
    MBUS_STATUS_BIT_8_CLOCK_SYNC_ERROR_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_8_CLOCK_SYNC_ERROR_CHANNEL_2, 0x8188, null, null, null),
    MBUS_STATUS_BIT_9_SW_ERROR_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_9_SW_ERROR_CHANNEL_2, 0x8189, null, null, null),
    MBUS_STATUS_BIT_10_WATCHDOG_ERROR_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_10_WATCHDOG_ERROR_CHANNEL_2, 0x818A, null, null, null),
    MBUS_STATUS_BIT_11_SYSTEM_HW_ERROR_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_11_SYSTEM_HW_ERROR_CHANNEL_2, 0x818B, null, null, null),
    MBUS_STATUS_BIT_12_CFG_CALIBRATION_ERROR_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_12_CFG_CALIBRATION_ERROR_CHANNEL_2, 0x818C, null, null, null),
    MBUS_STATUS_BIT_13_HIGH_FLOW_GREATER_THAN_QMAX_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_13_HIGH_FLOW_GREATER_THAN_QMAX_CHANNEL_2,
        0x818D,
        null,
        null,
        null),
    MBUS_STATUS_BIT_14_TEMP_SENSOR_ERROR_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_14_TEMP_SENSOR_ERROR_CHANNEL_2, 0x818E, null, null, null),
    MBUS_STATUS_BIT_15_RESERVED_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_15_RESERVED_CHANNEL_2, 0x818F, null, null, null),
    MBUS_STATUS_BIT_16_P0_ENABLED_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_16_P0_ENABLED_CHANNEL_2, 0x8190, null, null, null),
    MBUS_STATUS_BIT_17_NEW_KEY_ACCEPTED_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_17_NEW_KEY_ACCEPTED_CHANNEL_2, 0x8191, null, null, null),
    MBUS_STATUS_BIT_18_NEW_KEY_REJECTED_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_18_NEW_KEY_REJECTED_CHANNEL_2, 0x8192, null, null, null),
    MBUS_STATUS_BIT_18_RESERVED_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_18_RESERVED_CHANNEL_2, 0x8193, null, null, null),
    MBUS_STATUS_BIT_20_MANUFACTURER_SPECIFIC_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_20_MANUFACTURER_SPECIFIC_CHANNEL_2, 0x8194, null, null, null),
    MBUS_STATUS_BIT_21_MANUFACTURER_SPECIFIC_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_21_MANUFACTURER_SPECIFIC_CHANNEL_2, 0x8195, null, null, null),
    MBUS_STATUS_BIT_22_MANUFACTURER_SPECIFIC_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_22_MANUFACTURER_SPECIFIC_CHANNEL_2, 0x8196, null, null, null),
    MBUS_STATUS_BIT_23_MANUFACTURER_SPECIFIC_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_23_MANUFACTURER_SPECIFIC_CHANNEL_2, 0x8197, null, null, null),
    MBUS_STATUS_BIT_24_MANUFACTURER_SPECIFIC_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_24_MANUFACTURER_SPECIFIC_CHANNEL_2, 0x8198, null, null, null),
    MBUS_STATUS_BIT_25_MANUFACTURER_SPECIFIC_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_25_MANUFACTURER_SPECIFIC_CHANNEL_2, 0x8199, null, null, null),
    MBUS_STATUS_BIT_26_MANUFACTURER_SPECIFIC_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_26_MANUFACTURER_SPECIFIC_CHANNEL_2, 0x819A, null, null, null),
    MBUS_STATUS_BIT_27_MANUFACTURER_SPECIFIC_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_27_MANUFACTURER_SPECIFIC_CHANNEL_2, 0x819B, null, null, null),
    MBUS_STATUS_BIT_28_MANUFACTURER_SPECIFIC_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_28_MANUFACTURER_SPECIFIC_CHANNEL_2, 0x819C, null, null, null),
    MBUS_STATUS_BIT_29_MANUFACTURER_SPECIFIC_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_29_MANUFACTURER_SPECIFIC_CHANNEL_2, 0x819D, null, null, null),
    MBUS_STATUS_BIT_30_MANUFACTURER_SPECIFIC_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_30_MANUFACTURER_SPECIFIC_CHANNEL_2, 0x819E, null, null, null),
    MBUS_STATUS_BIT_31_MANUFACTURER_SPECIFIC_CHANNEL_2(
        EventTypeDto.MBUS_STATUS_BIT_31_MANUFACTURER_SPECIFIC_CHANNEL_2, 0x819F, null, null, null),
    KEY_SENT_TO_MBUS_DEVICE_ON_CHANNEL_2(
        EventTypeDto.KEY_SENT_TO_MBUS_DEVICE_ON_CHANNEL_2, 0x81A0, null, null, null),
    KEY_ACKNOWLEDGED_BY_MBUS_DEVICE_ON_CHANNEL_2(
        EventTypeDto.KEY_ACKNOWLEDGED_BY_MBUS_DEVICE_ON_CHANNEL_2, 0x81A1, null, null, null),

    MBUS_STATUS_BIT_0_BATTERY_LOW_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_0_BATTERY_LOW_CHANNEL_3, 0x8280, null, null, null),
    MBUS_STATUS_BIT_1_BATTERY_CONSUMPTION_TOO_HIGH_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_1_BATTERY_CONSUMPTION_TOO_HIGH_CHANNEL_3,
        0x8281,
        null,
        null,
        null),
    MBUS_STATUS_BIT_2_REVERSE_FLOW_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_2_REVERSE_FLOW_CHANNEL_3, 0x8282, null, null, null),
    MBUS_STATUS_BIT_3_TAMPER_P2_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_3_TAMPER_P2_CHANNEL_3, 0x8283, null, null, null),
    MBUS_STATUS_BIT_4_TAMPER_P0_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_4_TAMPER_P0_CHANNEL_3, 0x8284, null, null, null),
    MBUS_STATUS_BIT_5_TAMPER_CASE_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_5_TAMPER_CASE_CHANNEL_3, 0x8285, null, null, null),
    MBUS_STATUS_BIT_6_TAMPER_MAGNETIC_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_6_TAMPER_MAGNETIC_CHANNEL_3, 0x8286, null, null, null),
    MBUS_STATUS_BIT_7_TEMP_OUT_OF_RANGE_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_7_TEMP_OUT_OF_RANGE_CHANNEL_3, 0x8287, null, null, null),
    MBUS_STATUS_BIT_8_CLOCK_SYNC_ERROR_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_8_CLOCK_SYNC_ERROR_CHANNEL_3, 0x8288, null, null, null),
    MBUS_STATUS_BIT_9_SW_ERROR_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_9_SW_ERROR_CHANNEL_3, 0x8289, null, null, null),
    MBUS_STATUS_BIT_10_WATCHDOG_ERROR_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_10_WATCHDOG_ERROR_CHANNEL_3, 0x828A, null, null, null),
    MBUS_STATUS_BIT_11_SYSTEM_HW_ERROR_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_11_SYSTEM_HW_ERROR_CHANNEL_3, 0x828B, null, null, null),
    MBUS_STATUS_BIT_12_CFG_CALIBRATION_ERROR_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_12_CFG_CALIBRATION_ERROR_CHANNEL_3, 0x828C, null, null, null),
    MBUS_STATUS_BIT_13_HIGH_FLOW_GREATER_THAN_QMAX_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_13_HIGH_FLOW_GREATER_THAN_QMAX_CHANNEL_3,
        0x828D,
        null,
        null,
        null),
    MBUS_STATUS_BIT_14_TEMP_SENSOR_ERROR_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_14_TEMP_SENSOR_ERROR_CHANNEL_3, 0x828E, null, null, null),
    MBUS_STATUS_BIT_15_RESERVED_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_15_RESERVED_CHANNEL_3, 0x828F, null, null, null),
    MBUS_STATUS_BIT_16_P0_ENABLED_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_16_P0_ENABLED_CHANNEL_3, 0x8290, null, null, null),
    MBUS_STATUS_BIT_17_NEW_KEY_ACCEPTED_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_17_NEW_KEY_ACCEPTED_CHANNEL_3, 0x8291, null, null, null),
    MBUS_STATUS_BIT_18_NEW_KEY_REJECTED_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_18_NEW_KEY_REJECTED_CHANNEL_3, 0x8292, null, null, null),
    MBUS_STATUS_BIT_18_RESERVED_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_18_RESERVED_CHANNEL_3, 0x8293, null, null, null),
    MBUS_STATUS_BIT_20_MANUFACTURER_SPECIFIC_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_20_MANUFACTURER_SPECIFIC_CHANNEL_3, 0x8294, null, null, null),
    MBUS_STATUS_BIT_21_MANUFACTURER_SPECIFIC_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_21_MANUFACTURER_SPECIFIC_CHANNEL_3, 0x8295, null, null, null),
    MBUS_STATUS_BIT_22_MANUFACTURER_SPECIFIC_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_22_MANUFACTURER_SPECIFIC_CHANNEL_3, 0x8296, null, null, null),
    MBUS_STATUS_BIT_23_MANUFACTURER_SPECIFIC_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_23_MANUFACTURER_SPECIFIC_CHANNEL_3, 0x8297, null, null, null),
    MBUS_STATUS_BIT_24_MANUFACTURER_SPECIFIC_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_24_MANUFACTURER_SPECIFIC_CHANNEL_3, 0x8298, null, null, null),
    MBUS_STATUS_BIT_25_MANUFACTURER_SPECIFIC_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_25_MANUFACTURER_SPECIFIC_CHANNEL_3, 0x8299, null, null, null),
    MBUS_STATUS_BIT_26_MANUFACTURER_SPECIFIC_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_26_MANUFACTURER_SPECIFIC_CHANNEL_3, 0x829A, null, null, null),
    MBUS_STATUS_BIT_27_MANUFACTURER_SPECIFIC_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_27_MANUFACTURER_SPECIFIC_CHANNEL_3, 0x829B, null, null, null),
    MBUS_STATUS_BIT_28_MANUFACTURER_SPECIFIC_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_28_MANUFACTURER_SPECIFIC_CHANNEL_3, 0x829C, null, null, null),
    MBUS_STATUS_BIT_29_MANUFACTURER_SPECIFIC_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_29_MANUFACTURER_SPECIFIC_CHANNEL_3, 0x829D, null, null, null),
    MBUS_STATUS_BIT_30_MANUFACTURER_SPECIFIC_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_30_MANUFACTURER_SPECIFIC_CHANNEL_3, 0x829E, null, null, null),
    MBUS_STATUS_BIT_31_MANUFACTURER_SPECIFIC_CHANNEL_3(
        EventTypeDto.MBUS_STATUS_BIT_31_MANUFACTURER_SPECIFIC_CHANNEL_3, 0x829F, null, null, null),
    KEY_SENT_TO_MBUS_DEVICE_ON_CHANNEL_3(
        EventTypeDto.KEY_SENT_TO_MBUS_DEVICE_ON_CHANNEL_3, 0x82A0, null, null, null),
    KEY_ACKNOWLEDGED_BY_MBUS_DEVICE_ON_CHANNEL_3(
        EventTypeDto.KEY_ACKNOWLEDGED_BY_MBUS_DEVICE_ON_CHANNEL_3, 0x82A1, null, null, null),

    MBUS_STATUS_BIT_0_BATTERY_LOW_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_0_BATTERY_LOW_CHANNEL_4, 0x8380, null, null, null),
    MBUS_STATUS_BIT_1_BATTERY_CONSUMPTION_TOO_HIGH_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_1_BATTERY_CONSUMPTION_TOO_HIGH_CHANNEL_4,
        0x8381,
        null,
        null,
        null),
    MBUS_STATUS_BIT_2_REVERSE_FLOW_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_2_REVERSE_FLOW_CHANNEL_4, 0x8382, null, null, null),
    MBUS_STATUS_BIT_3_TAMPER_P2_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_3_TAMPER_P2_CHANNEL_4, 0x8383, null, null, null),
    MBUS_STATUS_BIT_4_TAMPER_P0_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_4_TAMPER_P0_CHANNEL_4, 0x8384, null, null, null),
    MBUS_STATUS_BIT_5_TAMPER_CASE_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_5_TAMPER_CASE_CHANNEL_4, 0x8385, null, null, null),
    MBUS_STATUS_BIT_6_TAMPER_MAGNETIC_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_6_TAMPER_MAGNETIC_CHANNEL_4, 0x8386, null, null, null),
    MBUS_STATUS_BIT_7_TEMP_OUT_OF_RANGE_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_7_TEMP_OUT_OF_RANGE_CHANNEL_4, 0x8387, null, null, null),
    MBUS_STATUS_BIT_8_CLOCK_SYNC_ERROR_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_8_CLOCK_SYNC_ERROR_CHANNEL_4, 0x8388, null, null, null),
    MBUS_STATUS_BIT_9_SW_ERROR_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_9_SW_ERROR_CHANNEL_4, 0x8389, null, null, null),
    MBUS_STATUS_BIT_10_WATCHDOG_ERROR_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_10_WATCHDOG_ERROR_CHANNEL_4, 0x838A, null, null, null),
    MBUS_STATUS_BIT_11_SYSTEM_HW_ERROR_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_11_SYSTEM_HW_ERROR_CHANNEL_4, 0x838B, null, null, null),
    MBUS_STATUS_BIT_12_CFG_CALIBRATION_ERROR_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_12_CFG_CALIBRATION_ERROR_CHANNEL_4, 0x838C, null, null, null),
    MBUS_STATUS_BIT_13_HIGH_FLOW_GREATER_THAN_QMAX_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_13_HIGH_FLOW_GREATER_THAN_QMAX_CHANNEL_4,
        0x838D,
        null,
        null,
        null),
    MBUS_STATUS_BIT_14_TEMP_SENSOR_ERROR_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_14_TEMP_SENSOR_ERROR_CHANNEL_4, 0x838E, null, null, null),
    MBUS_STATUS_BIT_15_RESERVED_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_15_RESERVED_CHANNEL_4, 0x838F, null, null, null),
    MBUS_STATUS_BIT_16_P0_ENABLED_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_16_P0_ENABLED_CHANNEL_4, 0x8390, null, null, null),
    MBUS_STATUS_BIT_17_NEW_KEY_ACCEPTED_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_17_NEW_KEY_ACCEPTED_CHANNEL_4, 0x8391, null, null, null),
    MBUS_STATUS_BIT_18_NEW_KEY_REJECTED_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_18_NEW_KEY_REJECTED_CHANNEL_4, 0x8392, null, null, null),
    MBUS_STATUS_BIT_18_RESERVED_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_18_RESERVED_CHANNEL_4, 0x8393, null, null, null),
    MBUS_STATUS_BIT_20_MANUFACTURER_SPECIFIC_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_20_MANUFACTURER_SPECIFIC_CHANNEL_4, 0x8394, null, null, null),
    MBUS_STATUS_BIT_21_MANUFACTURER_SPECIFIC_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_21_MANUFACTURER_SPECIFIC_CHANNEL_4, 0x8395, null, null, null),
    MBUS_STATUS_BIT_22_MANUFACTURER_SPECIFIC_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_22_MANUFACTURER_SPECIFIC_CHANNEL_4, 0x8396, null, null, null),
    MBUS_STATUS_BIT_23_MANUFACTURER_SPECIFIC_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_23_MANUFACTURER_SPECIFIC_CHANNEL_4, 0x8397, null, null, null),
    MBUS_STATUS_BIT_24_MANUFACTURER_SPECIFIC_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_24_MANUFACTURER_SPECIFIC_CHANNEL_4, 0x8398, null, null, null),
    MBUS_STATUS_BIT_25_MANUFACTURER_SPECIFIC_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_25_MANUFACTURER_SPECIFIC_CHANNEL_4, 0x8399, null, null, null),
    MBUS_STATUS_BIT_26_MANUFACTURER_SPECIFIC_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_26_MANUFACTURER_SPECIFIC_CHANNEL_4, 0x839A, null, null, null),
    MBUS_STATUS_BIT_27_MANUFACTURER_SPECIFIC_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_27_MANUFACTURER_SPECIFIC_CHANNEL_4, 0x839B, null, null, null),
    MBUS_STATUS_BIT_28_MANUFACTURER_SPECIFIC_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_28_MANUFACTURER_SPECIFIC_CHANNEL_4, 0x839C, null, null, null),
    MBUS_STATUS_BIT_29_MANUFACTURER_SPECIFIC_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_29_MANUFACTURER_SPECIFIC_CHANNEL_4, 0x839D, null, null, null),
    MBUS_STATUS_BIT_30_MANUFACTURER_SPECIFIC_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_30_MANUFACTURER_SPECIFIC_CHANNEL_4, 0x839E, null, null, null),
    MBUS_STATUS_BIT_31_MANUFACTURER_SPECIFIC_CHANNEL_4(
        EventTypeDto.MBUS_STATUS_BIT_31_MANUFACTURER_SPECIFIC_CHANNEL_4, 0x839F, null, null, null),
    KEY_SENT_TO_MBUS_DEVICE_ON_CHANNEL_4(
        EventTypeDto.KEY_SENT_TO_MBUS_DEVICE_ON_CHANNEL_4, 0x83A0, null, null, null),
    KEY_ACKNOWLEDGED_BY_MBUS_DEVICE_ON_CHANNEL_4(
        EventTypeDto.KEY_ACKNOWLEDGED_BY_MBUS_DEVICE_ON_CHANNEL_4, 0x83A1, null, null, null),

    /* Manufacturer specific Channel 1 */
    MBUS_STATUS_BIT_20_POWERFAIL_CHANNEL_1_FLO(
        EventTypeDto.MBUS_STATUS_BIT_20_POWERFAIL_CHANNEL_1_FLO, 0x8094, null, "FLO", null),
    MBUS_STATUS_BIT_20_BATTERY_CHANGED_CHANNEL_1_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_20_BATTERY_CHANGED_CHANNEL_1_LAGY, 0x8094, null, "LAGY", null),
    MBUS_STATUS_BIT_21_MAX_FLOW_ERROR_CHANNEL_1_FLO(
        EventTypeDto.MBUS_STATUS_BIT_21_MAX_FLOW_ERROR_CHANNEL_1_FLO, 0x8095, null, "FLO", null),
    MBUS_STATUS_BIT_21_CLOCK_INVALID_CHANNEL_1_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_21_CLOCK_INVALID_CHANNEL_1_LAGY, 0x8095, null, "LAGY", null),
    MBUS_STATUS_BIT_21_BOTTOM_TAMPER_CHANNEL_1_GWI(
        EventTypeDto.MBUS_STATUS_BIT_21_BOTTOM_TAMPER_CHANNEL_1_GWI, 0x8095, null, "GWI", null),
    MBUS_STATUS_BIT_22_MIN_TEMPERATURE_ALARM_CHANNEL_1_FLO(
        EventTypeDto.MBUS_STATUS_BIT_22_MIN_TEMPERATURE_ALARM_CHANNEL_1_FLO,
        0x8096,
        null,
        "FLO",
        null),
    MBUS_STATUS_BIT_22_MEASUREMENT_DATA_CORRUPTION_ERROR_CHANNEL_1_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_22_MEASUREMENT_DATA_CORRUPTION_ERROR_CHANNEL_1_LAGY,
        0x8096,
        null,
        "LAGY",
        null),
    MBUS_STATUS_BIT_22_MID_TAMPER_CHANNEL_1_GWI(
        EventTypeDto.MBUS_STATUS_BIT_22_MID_TAMPER_CHANNEL_1_GWI, 0x8096, null, "GWI", null),
    MBUS_STATUS_BIT_23_HIGH_TEMPERATUR_ALARM_CHANNEL_1_FLO(
        EventTypeDto.MBUS_STATUS_BIT_23_HIGH_TEMPERATUR_ALARM_CHANNEL_1_FLO,
        0x8097,
        null,
        "FLO",
        null),
    MBUS_STATUS_BIT_23_SENSOR_DEGRADATION_ERROR_CHANNEL_1_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_23_SENSOR_DEGRADATION_ERROR_CHANNEL_1_LAGY,
        0x8097,
        null,
        "LAGY",
        null),
    MBUS_STATUS_BIT_23_BATTERY_COMPLETELY_EMPTY_ALARM_CHANNEL_1_GWI(
        EventTypeDto.MBUS_STATUS_BIT_23_BATTERY_COMPLETELY_EMPTY_ALARM_CHANNEL_1_GWI,
        0x8097,
        null,
        "GWI",
        null),
    MBUS_STATUS_BIT_24_PULSE_ERROR_CHANNEL_1_FLO(
        EventTypeDto.MBUS_STATUS_BIT_24_PULSE_ERROR_CHANNEL_1_FLO, 0x8098, null, "FLO", null),
    MBUS_STATUS_BIT_24_DISPLAY_ERROR_CHANNEL_1_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_24_DISPLAY_ERROR_CHANNEL_1_LAGY, 0x8098, null, "LAGY", null),
    MBUS_STATUS_BIT_24_BATTERY_REMOVED_ALARM_CHANNEL_1_GWI(
        EventTypeDto.MBUS_STATUS_BIT_24_BATTERY_REMOVED_ALARM_CHANNEL_1_GWI,
        0x8098,
        null,
        "GWI",
        null),
    MBUS_STATUS_BIT_25_CONSUMPTION_ERROR_CHANNEL_1_FLO(
        EventTypeDto.MBUS_STATUS_BIT_25_CONSUMPTION_ERROR_CHANNEL_1_FLO, 0x8099, null, "FLO", null),
    MBUS_STATUS_BIT_25_USAGE_P0_CHANNEL_1_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_25_USAGE_P0_CHANNEL_1_LAGY, 0x8099, null, "LAGY", null),
    MBUS_STATUS_BIT_25_INTERNAL_INDEX_MBUS_COMMUNICATION_ERROR_CHANNEL_1_GWI(
        EventTypeDto.MBUS_STATUS_BIT_25_INTERNAL_INDEX_MBUS_COMMUNICATION_ERROR_CHANNEL_1_GWI,
        0x8099,
        null,
        "GWI",
        null),
    MBUS_STATUS_BIT_26_USAGE_TESTMODE_CHANNEL_1_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_26_USAGE_TESTMODE_CHANNEL_1_LAGY, 0x809A, null, "LAGY", null),
    MBUS_STATUS_BIT_26_RESET_REQUESTED_CHANNEL_1_GWI(
        EventTypeDto.MBUS_STATUS_BIT_26_RESET_REQUESTED_CHANNEL_1_GWI, 0x809A, null, "GWI", null),
    MBUS_STATUS_BIT_27_SETTING_WAS_RESET_TO_DEFAULT_CHANNEL_1_GWI(
        EventTypeDto.MBUS_STATUS_BIT_27_SETTING_WAS_RESET_TO_DEFAULT_CHANNEL_1_GWI,
        0x809B,
        null,
        "GWI",
        null),
    MBUS_STATUS_BIT_30_MEASUREMENT_DATA_CORRUPTION_ERROR_CHANNEL_1_FLO(
        EventTypeDto.MBUS_STATUS_BIT_30_MEASUREMENT_DATA_CORRUPTION_ERROR_CHANNEL_1_FLO,
        0x809E,
        null,
        "FLO",
        null),

    /* Manufacturer specific Channel 2 */
    MBUS_STATUS_BIT_20_POWERFAIL_CHANNEL_2_FLO(
        EventTypeDto.MBUS_STATUS_BIT_20_POWERFAIL_CHANNEL_2_FLO, 0x8094, null, "FLO", null),
    MBUS_STATUS_BIT_20_BATTERY_CHANGED_CHANNEL_2_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_20_BATTERY_CHANGED_CHANNEL_2_LAGY, 0x8094, null, "LAGY", null),
    MBUS_STATUS_BIT_21_MAX_FLOW_ERROR_CHANNEL_2_FLO(
        EventTypeDto.MBUS_STATUS_BIT_21_MAX_FLOW_ERROR_CHANNEL_2_FLO, 0x8095, null, "FLO", null),
    MBUS_STATUS_BIT_21_CLOCK_INVALID_CHANNEL_2_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_21_CLOCK_INVALID_CHANNEL_2_LAGY, 0x8095, null, "LAGY", null),
    MBUS_STATUS_BIT_21_BOTTOM_TAMPER_CHANNEL_2_GWI(
        EventTypeDto.MBUS_STATUS_BIT_21_BOTTOM_TAMPER_CHANNEL_2_GWI, 0x8095, null, "GWI", null),
    MBUS_STATUS_BIT_22_MIN_TEMPERATURE_ALARM_CHANNEL_2_FLO(
        EventTypeDto.MBUS_STATUS_BIT_22_MIN_TEMPERATURE_ALARM_CHANNEL_2_FLO,
        0x8096,
        null,
        "FLO",
        null),
    MBUS_STATUS_BIT_22_MEASUREMENT_DATA_CORRUPTION_ERROR_CHANNEL_2_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_22_MEASUREMENT_DATA_CORRUPTION_ERROR_CHANNEL_2_LAGY,
        0x8096,
        null,
        "LAGY",
        null),
    MBUS_STATUS_BIT_22_MID_TAMPER_CHANNEL_2_GWI(
        EventTypeDto.MBUS_STATUS_BIT_22_MID_TAMPER_CHANNEL_2_GWI, 0x8096, null, "GWI", null),
    MBUS_STATUS_BIT_23_HIGH_TEMPERATUR_ALARM_CHANNEL_2_FLO(
        EventTypeDto.MBUS_STATUS_BIT_23_HIGH_TEMPERATUR_ALARM_CHANNEL_2_FLO,
        0x8097,
        null,
        "FLO",
        null),
    MBUS_STATUS_BIT_23_SENSOR_DEGRADATION_ERROR_CHANNEL_2_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_23_SENSOR_DEGRADATION_ERROR_CHANNEL_2_LAGY,
        0x8097,
        null,
        "LAGY",
        null),
    MBUS_STATUS_BIT_23_BATTERY_COMPLETELY_EMPTY_ALARM_CHANNEL_2_GWI(
        EventTypeDto.MBUS_STATUS_BIT_23_BATTERY_COMPLETELY_EMPTY_ALARM_CHANNEL_2_GWI,
        0x8097,
        null,
        "GWI",
        null),
    MBUS_STATUS_BIT_24_PULSE_ERROR_CHANNEL_2_FLO(
        EventTypeDto.MBUS_STATUS_BIT_24_PULSE_ERROR_CHANNEL_2_FLO, 0x8098, null, "FLO", null),
    MBUS_STATUS_BIT_24_DISPLAY_ERROR_CHANNEL_2_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_24_DISPLAY_ERROR_CHANNEL_2_LAGY, 0x8098, null, "LAGY", null),
    MBUS_STATUS_BIT_24_BATTERY_REMOVED_ALARM_CHANNEL_2_GWI(
        EventTypeDto.MBUS_STATUS_BIT_24_BATTERY_REMOVED_ALARM_CHANNEL_2_GWI,
        0x8098,
        null,
        "GWI",
        null),
    MBUS_STATUS_BIT_25_CONSUMPTION_ERROR_CHANNEL_2_FLO(
        EventTypeDto.MBUS_STATUS_BIT_25_CONSUMPTION_ERROR_CHANNEL_2_FLO, 0x8099, null, "FLO", null),
    MBUS_STATUS_BIT_25_USAGE_P0_CHANNEL_2_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_25_USAGE_P0_CHANNEL_2_LAGY, 0x8099, null, "LAGY", null),
    MBUS_STATUS_BIT_25_INTERNAL_INDEX_MBUS_COMMUNICATION_ERROR_CHANNEL_2_GWI(
        EventTypeDto.MBUS_STATUS_BIT_25_INTERNAL_INDEX_MBUS_COMMUNICATION_ERROR_CHANNEL_2_GWI,
        0x8099,
        null,
        "GWI",
        null),
    MBUS_STATUS_BIT_26_USAGE_TESTMODE_CHANNEL_2_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_26_USAGE_TESTMODE_CHANNEL_2_LAGY, 0x809A, null, "LAGY", null),
    MBUS_STATUS_BIT_26_RESET_REQUESTED_CHANNEL_2_GWI(
        EventTypeDto.MBUS_STATUS_BIT_26_RESET_REQUESTED_CHANNEL_2_GWI, 0x809A, null, "GWI", null),
    MBUS_STATUS_BIT_27_SETTING_WAS_RESET_TO_DEFAULT_CHANNEL_2_GWI(
        EventTypeDto.MBUS_STATUS_BIT_27_SETTING_WAS_RESET_TO_DEFAULT_CHANNEL_2_GWI,
        0x809B,
        null,
        "GWI",
        null),
    MBUS_STATUS_BIT_30_MEASUREMENT_DATA_CORRUPTION_ERROR_CHANNEL_2_FLO(
        EventTypeDto.MBUS_STATUS_BIT_30_MEASUREMENT_DATA_CORRUPTION_ERROR_CHANNEL_2_FLO,
        0x809E,
        null,
        "FLO",
        null),

    /* Manufacturer specific Channel 3 */
    MBUS_STATUS_BIT_20_POWERFAIL_CHANNEL_3_FLO(
        EventTypeDto.MBUS_STATUS_BIT_20_POWERFAIL_CHANNEL_3_FLO, 0x8094, null, "FLO", null),
    MBUS_STATUS_BIT_20_BATTERY_CHANGED_CHANNEL_3_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_20_BATTERY_CHANGED_CHANNEL_3_LAGY, 0x8094, null, "LAGY", null),
    MBUS_STATUS_BIT_21_MAX_FLOW_ERROR_CHANNEL_3_FLO(
        EventTypeDto.MBUS_STATUS_BIT_21_MAX_FLOW_ERROR_CHANNEL_3_FLO, 0x8095, null, "FLO", null),
    MBUS_STATUS_BIT_21_CLOCK_INVALID_CHANNEL_3_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_21_CLOCK_INVALID_CHANNEL_3_LAGY, 0x8095, null, "LAGY", null),
    MBUS_STATUS_BIT_21_BOTTOM_TAMPER_CHANNEL_3_GWI(
        EventTypeDto.MBUS_STATUS_BIT_21_BOTTOM_TAMPER_CHANNEL_3_GWI, 0x8095, null, "GWI", null),
    MBUS_STATUS_BIT_22_MIN_TEMPERATURE_ALARM_CHANNEL_3_FLO(
        EventTypeDto.MBUS_STATUS_BIT_22_MIN_TEMPERATURE_ALARM_CHANNEL_3_FLO,
        0x8096,
        null,
        "FLO",
        null),
    MBUS_STATUS_BIT_22_MEASUREMENT_DATA_CORRUPTION_ERROR_CHANNEL_3_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_22_MEASUREMENT_DATA_CORRUPTION_ERROR_CHANNEL_3_LAGY,
        0x8096,
        null,
        "LAGY",
        null),
    MBUS_STATUS_BIT_22_MID_TAMPER_CHANNEL_3_GWI(
        EventTypeDto.MBUS_STATUS_BIT_22_MID_TAMPER_CHANNEL_3_GWI, 0x8096, null, "GWI", null),
    MBUS_STATUS_BIT_23_HIGH_TEMPERATUR_ALARM_CHANNEL_3_FLO(
        EventTypeDto.MBUS_STATUS_BIT_23_HIGH_TEMPERATUR_ALARM_CHANNEL_3_FLO,
        0x8097,
        null,
        "FLO",
        null),
    MBUS_STATUS_BIT_23_SENSOR_DEGRADATION_ERROR_CHANNEL_3_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_23_SENSOR_DEGRADATION_ERROR_CHANNEL_3_LAGY,
        0x8097,
        null,
        "LAGY",
        null),
    MBUS_STATUS_BIT_23_BATTERY_COMPLETELY_EMPTY_ALARM_CHANNEL_3_GWI(
        EventTypeDto.MBUS_STATUS_BIT_23_BATTERY_COMPLETELY_EMPTY_ALARM_CHANNEL_3_GWI,
        0x8097,
        null,
        "GWI",
        null),
    MBUS_STATUS_BIT_24_PULSE_ERROR_CHANNEL_3_FLO(
        EventTypeDto.MBUS_STATUS_BIT_24_PULSE_ERROR_CHANNEL_3_FLO, 0x8098, null, "FLO", null),
    MBUS_STATUS_BIT_24_DISPLAY_ERROR_CHANNEL_3_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_24_DISPLAY_ERROR_CHANNEL_3_LAGY, 0x8098, null, "LAGY", null),
    MBUS_STATUS_BIT_24_BATTERY_REMOVED_ALARM_CHANNEL_3_GWI(
        EventTypeDto.MBUS_STATUS_BIT_24_BATTERY_REMOVED_ALARM_CHANNEL_3_GWI,
        0x8098,
        null,
        "GWI",
        null),
    MBUS_STATUS_BIT_25_CONSUMPTION_ERROR_CHANNEL_3_FLO(
        EventTypeDto.MBUS_STATUS_BIT_25_CONSUMPTION_ERROR_CHANNEL_3_FLO, 0x8099, null, "FLO", null),
    MBUS_STATUS_BIT_25_USAGE_P0_CHANNEL_3_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_25_USAGE_P0_CHANNEL_3_LAGY, 0x8099, null, "LAGY", null),
    MBUS_STATUS_BIT_25_INTERNAL_INDEX_MBUS_COMMUNICATION_ERROR_CHANNEL_3_GWI(
        EventTypeDto.MBUS_STATUS_BIT_25_INTERNAL_INDEX_MBUS_COMMUNICATION_ERROR_CHANNEL_3_GWI,
        0x8099,
        null,
        "GWI",
        null),
    MBUS_STATUS_BIT_26_USAGE_TESTMODE_CHANNEL_3_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_26_USAGE_TESTMODE_CHANNEL_3_LAGY, 0x809A, null, "LAGY", null),
    MBUS_STATUS_BIT_26_RESET_REQUESTED_CHANNEL_3_GWI(
        EventTypeDto.MBUS_STATUS_BIT_26_RESET_REQUESTED_CHANNEL_3_GWI, 0x809A, null, "GWI", null),
    MBUS_STATUS_BIT_27_SETTING_WAS_RESET_TO_DEFAULT_CHANNEL_3_GWI(
        EventTypeDto.MBUS_STATUS_BIT_27_SETTING_WAS_RESET_TO_DEFAULT_CHANNEL_3_GWI,
        0x809B,
        null,
        "GWI",
        null),
    MBUS_STATUS_BIT_30_MEASUREMENT_DATA_CORRUPTION_ERROR_CHANNEL_3_FLO(
        EventTypeDto.MBUS_STATUS_BIT_30_MEASUREMENT_DATA_CORRUPTION_ERROR_CHANNEL_3_FLO,
        0x809E,
        null,
        "FLO",
        null),

    /* Manufacturer specific Channel 4 */
    MBUS_STATUS_BIT_20_POWERFAIL_CHANNEL_4_FLO(
        EventTypeDto.MBUS_STATUS_BIT_20_POWERFAIL_CHANNEL_4_FLO, 0x8094, null, "FLO", null),
    MBUS_STATUS_BIT_20_BATTERY_CHANGED_CHANNEL_4_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_20_BATTERY_CHANGED_CHANNEL_4_LAGY, 0x8094, null, "LAGY", null),
    MBUS_STATUS_BIT_21_MAX_FLOW_ERROR_CHANNEL_4_FLO(
        EventTypeDto.MBUS_STATUS_BIT_21_MAX_FLOW_ERROR_CHANNEL_4_FLO, 0x8095, null, "FLO", null),
    MBUS_STATUS_BIT_21_CLOCK_INVALID_CHANNEL_4_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_21_CLOCK_INVALID_CHANNEL_4_LAGY, 0x8095, null, "LAGY", null),
    MBUS_STATUS_BIT_21_BOTTOM_TAMPER_CHANNEL_4_GWI(
        EventTypeDto.MBUS_STATUS_BIT_21_BOTTOM_TAMPER_CHANNEL_4_GWI, 0x8095, null, "GWI", null),
    MBUS_STATUS_BIT_22_MIN_TEMPERATURE_ALARM_CHANNEL_4_FLO(
        EventTypeDto.MBUS_STATUS_BIT_22_MIN_TEMPERATURE_ALARM_CHANNEL_4_FLO,
        0x8096,
        null,
        "FLO",
        null),
    MBUS_STATUS_BIT_22_MEASUREMENT_DATA_CORRUPTION_ERROR_CHANNEL_4_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_22_MEASUREMENT_DATA_CORRUPTION_ERROR_CHANNEL_4_LAGY,
        0x8096,
        null,
        "LAGY",
        null),
    MBUS_STATUS_BIT_22_MID_TAMPER_CHANNEL_4_GWI(
        EventTypeDto.MBUS_STATUS_BIT_22_MID_TAMPER_CHANNEL_4_GWI, 0x8096, null, "GWI", null),
    MBUS_STATUS_BIT_23_HIGH_TEMPERATUR_ALARM_CHANNEL_4_FLO(
        EventTypeDto.MBUS_STATUS_BIT_23_HIGH_TEMPERATUR_ALARM_CHANNEL_4_FLO,
        0x8097,
        null,
        "FLO",
        null),
    MBUS_STATUS_BIT_23_SENSOR_DEGRADATION_ERROR_CHANNEL_4_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_23_SENSOR_DEGRADATION_ERROR_CHANNEL_4_LAGY,
        0x8097,
        null,
        "LAGY",
        null),
    MBUS_STATUS_BIT_23_BATTERY_COMPLETELY_EMPTY_ALARM_CHANNEL_4_GWI(
        EventTypeDto.MBUS_STATUS_BIT_23_BATTERY_COMPLETELY_EMPTY_ALARM_CHANNEL_4_GWI,
        0x8097,
        null,
        "GWI",
        null),
    MBUS_STATUS_BIT_24_PULSE_ERROR_CHANNEL_4_FLO(
        EventTypeDto.MBUS_STATUS_BIT_24_PULSE_ERROR_CHANNEL_4_FLO, 0x8098, null, "FLO", null),
    MBUS_STATUS_BIT_24_DISPLAY_ERROR_CHANNEL_4_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_24_DISPLAY_ERROR_CHANNEL_4_LAGY, 0x8098, null, "LAGY", null),
    MBUS_STATUS_BIT_24_BATTERY_REMOVED_ALARM_CHANNEL_4_GWI(
        EventTypeDto.MBUS_STATUS_BIT_24_BATTERY_REMOVED_ALARM_CHANNEL_4_GWI,
        0x8098,
        null,
        "GWI",
        null),
    MBUS_STATUS_BIT_25_CONSUMPTION_ERROR_CHANNEL_4_FLO(
        EventTypeDto.MBUS_STATUS_BIT_25_CONSUMPTION_ERROR_CHANNEL_4_FLO, 0x8099, null, "FLO", null),
    MBUS_STATUS_BIT_25_USAGE_P0_CHANNEL_4_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_25_USAGE_P0_CHANNEL_4_LAGY, 0x8099, null, "LAGY", null),
    MBUS_STATUS_BIT_25_INTERNAL_INDEX_MBUS_COMMUNICATION_ERROR_CHANNEL_4_GWI(
        EventTypeDto.MBUS_STATUS_BIT_25_INTERNAL_INDEX_MBUS_COMMUNICATION_ERROR_CHANNEL_4_GWI,
        0x8099,
        null,
        "GWI",
        null),
    MBUS_STATUS_BIT_26_USAGE_TESTMODE_CHANNEL_4_LAGY(
        EventTypeDto.MBUS_STATUS_BIT_26_USAGE_TESTMODE_CHANNEL_4_LAGY, 0x809A, null, "LAGY", null),
    MBUS_STATUS_BIT_26_RESET_REQUESTED_CHANNEL_4_GWI(
        EventTypeDto.MBUS_STATUS_BIT_26_RESET_REQUESTED_CHANNEL_4_GWI, 0x809A, null, "GWI", null),
    MBUS_STATUS_BIT_27_SETTING_WAS_RESET_TO_DEFAULT_CHANNEL_4_GWI(
        EventTypeDto.MBUS_STATUS_BIT_27_SETTING_WAS_RESET_TO_DEFAULT_CHANNEL_4_GWI,
        0x809B,
        null,
        "GWI",
        null),
    MBUS_STATUS_BIT_30_MEASUREMENT_DATA_CORRUPTION_ERROR_CHANNEL_4_FLO(
        EventTypeDto.MBUS_STATUS_BIT_30_MEASUREMENT_DATA_CORRUPTION_ERROR_CHANNEL_4_FLO,
        0x809E,
        null,
        "FLO",
        null);

    private final EventTypeDto eventTypeDto;
    private final int eventCode;
    private final String protocol;
    private final String manufacturerCode;
    private final String deviceType;

    EventTypeDtoLookup(
        final EventTypeDto eventTypeDto,
        final int eventCode,
        final String protocol,
        final String manufacturerCode,
        final String deviceType) {
      this.eventTypeDto = eventTypeDto;
      this.eventCode = eventCode;
      this.protocol = protocol;
      this.manufacturerCode = manufacturerCode;
      this.deviceType = deviceType;
    }

    public EventTypeDto getEventTypeDto() {
      return this.eventTypeDto;
    }

    public int getEventCode() {
      return this.eventCode;
    }

    public String getProtocol() {
      return this.protocol;
    }

    public String getManufacturerCode() {
      return this.manufacturerCode;
    }

    public String getDeviceType() {
      return this.deviceType;
    }

    public boolean isGeneric() {
      return this.getProtocol() == null
          && this.getManufacturerCode() == null
          && this.getDeviceType() == null;
    }
  }
}
