/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
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
                Arrays.stream(EventTypeDtoLookup.values()).collect(Collectors.groupingBy(EventTypeDtoLookup::getEventCode));
    }

    public void addEventTypeToEvents(final DeviceMessageMetadata deviceMessageMetadata,
            final EventMessageDataResponseDto responseDto) throws FunctionalException {

        LOGGER.info("Enrich EventMessageDataResponse with EventTypes for device: {}",
                deviceMessageMetadata.getDeviceIdentification());
        final SmartMeter smartMeter =
                this.domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification());

        for (EventDto eventDto : responseDto.getEvents()) {
            final EventTypeDto eventTypeDto = this.determineEventType(eventDto, smartMeter);
            eventDto.setEventTypeDto(eventTypeDto);
        }
    }

    private EventTypeDto determineEventType(final EventDto eventDto, final SmartMeter smartMeter)
            throws FunctionalException {
        final String deviceType = smartMeter.getDeviceType();
        final String protocolName = smartMeter.getProtocolInfo()!=null ?
                                    smartMeter.getProtocolInfo().getProtocol() : "UNKNOWN";
        final String manufacturerCode = (smartMeter.getDeviceModel() != null &&
                smartMeter.getDeviceModel().getManufacturer() != null) ?
                 smartMeter.getDeviceModel().getManufacturer().getCode() : "UNKNOWN";

        final Integer eventCode = eventDto.getEventCode();

        final List<EventTypeDtoLookup> possibleEventTypes = eventTypsByCode.get(eventCode);
        if (possibleEventTypes == null) {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                    ComponentType.DOMAIN_SMART_METERING,
                    new AssertionError("Event Type should be defined for eventCode: " + eventCode));
        }
        final List<EventTypeDtoLookup> eventTypes = possibleEventTypes.stream()
                          .filter(lookup -> lookup.getManufacturerCode() == null || lookup.getManufacturerCode().equals(manufacturerCode))
                          .filter(lookup -> lookup.getDeviceType() == null || lookup.getDeviceType().equals(deviceType))
                          .filter(lookup -> lookup.getProtocol() == null || protocolName.startsWith(lookup.getProtocol()))
            .collect(Collectors.toList());
        if (eventTypes.size() == 1) {
            return eventTypes.get(0).getEventTypeDto();
        } else if (eventTypes.size() > 1) {

            /* Specific EventTypes overrule the Genric ones */
            final List<EventTypeDtoLookup> specificEventTypes = eventTypes.stream()
                      .filter(lookup -> !lookup.isGeneric())
                      .collect(Collectors.toList());
            if (specificEventTypes.size() == 1) {
                return specificEventTypes.get(0).getEventTypeDto();
            }
        }

        throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                ComponentType.DOMAIN_SMART_METERING,
                new AssertionError("Exactly one Event Type should be match eventCode: " + eventCode
                        + ", deviceType: " + deviceType + ", protocolName: " + protocolName
                        + ", manufacturerCode: " + manufacturerCode));
    }

    enum EventTypeDtoLookup {
        EVENTLOG_CLEARED(EventTypeDto.EVENTLOG_CLEARED, 255, null, null, null),
        POWER_FAILURE(EventTypeDto.POWER_FAILURE, 1, null, null, null),
        POWER_FAILURE_G(EventTypeDto.POWER_FAILURE_G, 1,null, null, "SMART_METER_G"),
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
        HARDWARE_ERROR_MEASUREMENT_SYSTEM(EventTypeDto.HARDWARE_ERROR_MEASUREMENT_SYSTEM, 16, null, null, null),
        FIRMWARE_READY_FOR_ACTIVATION(EventTypeDto.FIRMWARE_READY_FOR_ACTIVATION, 17, null, null, null),
        FIRMWARE_ACTIVATED(EventTypeDto.FIRMWARE_ACTIVATED, 18, null, null, null),
        PASSIVE_TARIFF_UPDATED(EventTypeDto.PASSIVE_TARIFF_UPDATED, 19, null, null, null),
        SUCCESSFUL_SELFCHECK_AFTER_FIRMWARE_UPDATE(EventTypeDto.SUCCESSFUL_SELFCHECK_AFTER_FIRMWARE_UPDATE, 20, null, null, null),
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
        COMMUNICATION_ERROR_M_BUS_CHANNEL_1(EventTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_1, 100, null, null, null),
        COMMUNICATION_OK_M_BUS_CHANNEL_1(EventTypeDto.COMMUNICATION_OK_M_BUS_CHANNEL_1, 101, null, null, null),
        REPLACE_BATTERY_M_BUS_CHANNEL_1(EventTypeDto.REPLACE_BATTERY_M_BUS_CHANNEL_1, 102, null, null, null),
        FRAUD_ATTEMPT_M_BUS_CHANNEL_1(EventTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_1, 103, null, null, null),
        CLOCK_ADJUSTED_M_BUS_CHANNEL_1(EventTypeDto.CLOCK_ADJUSTED_M_BUS_CHANNEL_1, 104, null, null, null),
        NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1(EventTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1, 105, null, null, null),
        PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_1(EventTypeDto.PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_1, 106, null, null, null),
        COMMUNICATION_ERROR_M_BUS_CHANNEL_2(EventTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_2, 110, null, null, null),
        COMMUNICATION_OK_M_BUS_CHANNEL_2(EventTypeDto.COMMUNICATION_OK_M_BUS_CHANNEL_2, 111, null, null, null),
        REPLACE_BATTERY_M_BUS_CHANNEL_2(EventTypeDto.REPLACE_BATTERY_M_BUS_CHANNEL_2, 112, null, null, null),
        FRAUD_ATTEMPT_M_BUS_CHANNEL_2(EventTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_2, 113, null, null, null),
        CLOCK_ADJUSTED_M_BUS_CHANNEL_2(EventTypeDto.CLOCK_ADJUSTED_M_BUS_CHANNEL_2, 114, null, null, null),
        NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2(EventTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2, 115, null, null, null),
        PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_2(EventTypeDto.PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_2, 116, null, null, null),
        COMMUNICATION_ERROR_M_BUS_CHANNEL_3(EventTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_3, 120, null, null, null),
        COMMUNICATION_OK_M_BUS_CHANNEL_3(EventTypeDto.COMMUNICATION_OK_M_BUS_CHANNEL_3, 121, null, null, null),
        REPLACE_BATTERY_M_BUS_CHANNEL_3(EventTypeDto.REPLACE_BATTERY_M_BUS_CHANNEL_3, 122, null, null, null),
        FRAUD_ATTEMPT_M_BUS_CHANNEL_3(EventTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_3, 123, null, null, null),
        CLOCK_ADJUSTED_M_BUS_CHANNEL_3(EventTypeDto.CLOCK_ADJUSTED_M_BUS_CHANNEL_3, 124, null, null, null),
        NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3(EventTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3, 125, null, null, null),
        PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_3(EventTypeDto.PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_3, 126, null, null, null),
        COMMUNICATION_ERROR_M_BUS_CHANNEL_4(EventTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_4, 130, null, null, null),
        COMMUNICATION_OK_M_BUS_CHANNEL_4(EventTypeDto.COMMUNICATION_OK_M_BUS_CHANNEL_4, 131, null, null, null),
        REPLACE_BATTERY_M_BUS_CHANNEL_4(EventTypeDto.REPLACE_BATTERY_M_BUS_CHANNEL_4, 132, null, null, null),
        FRAUD_ATTEMPT_M_BUS_CHANNEL_4(EventTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_4, 133, null, null, null),
        CLOCK_ADJUSTED_M_BUS_CHANNEL_4(EventTypeDto.CLOCK_ADJUSTED_M_BUS_CHANNEL_4, 134, null, null, null),
        NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4(EventTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4, 135, null, null, null),
        PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_4(EventTypeDto.PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_4, 136, null, null, null),
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
        POWER_RESTORED_PHASE_L1_ISKR(EventTypeDto.POWER_RESTORED_PHASE_L1_ISKR, 235, null, "Iskr", null),
        POWER_RESTORED_PHASE_L2_ISKR(EventTypeDto.POWER_RESTORED_PHASE_L2_ISKR, 236, null, "Iskr", null),
        POWER_RESTORED_PHASE_L3_ISKR(EventTypeDto.POWER_RESTORED_PHASE_L3_ISKR, 237, null, "Iskr", null),
        MODULE_COVER_OPENED_ISKR(EventTypeDto.MODULE_COVER_OPENED_ISKR, 244, null, "Iskr", null),
        MODULE_COVER_CLOSED_ISKR(EventTypeDto.MODULE_COVER_CLOSED_ISKR, 245, null, "Iskr", null);

        private final EventTypeDto eventTypeDto;
        private final int eventCode;
        private final String protocol;
        private final String manufacturerCode;
        private final String deviceType;

        EventTypeDtoLookup(final EventTypeDto eventTypeDto, final int eventCode, final String protocol,
                String manufacturerCode,
                String deviceType) {
            this.eventTypeDto = eventTypeDto;
            this.eventCode = eventCode;
            this.protocol = protocol;
            this.manufacturerCode = manufacturerCode;
            this.deviceType = deviceType;
        }

        public EventTypeDto getEventTypeDto() {
            return eventTypeDto;
        }

        public int getEventCode() {
            return eventCode;
        }

        public String getProtocol() {
            return protocol;
        }

        public String getManufacturerCode() {
            return manufacturerCode;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public boolean isGeneric() {
            return getProtocol() == null && getManufacturerCode() == null &&  getDeviceType() == null;
        }
    }
}
