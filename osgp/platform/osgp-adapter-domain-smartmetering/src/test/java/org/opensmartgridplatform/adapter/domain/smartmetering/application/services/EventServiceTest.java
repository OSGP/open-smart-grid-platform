/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventMessageDataResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventTypeDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private DomainHelperService domainHelperService;

    private EventService eventService;
    private DeviceMessageMetadata deviceMessageMetadata;
    @Mock
    private SmartMeter smartMeter;

    @BeforeEach
    void setUp() throws FunctionalException {
        eventService = new EventService(domainHelperService);

        deviceMessageMetadata =
                DeviceMessageMetadata.newBuilder().withCorrelationUid(RandomStringUtils.randomAlphabetic(10))
                                     .withDeviceIdentification(RandomStringUtils.randomAlphabetic(10))
                                     .withOrganisationIdentification(RandomStringUtils.randomAlphabetic(10))
                                     .withMessageType(RandomStringUtils.randomAlphabetic(10)).build();

        when(domainHelperService.findSmartMeter(deviceMessageMetadata.getDeviceIdentification())).thenReturn(smartMeter);
    }

    @Test
    public void testWrongEventCode() {
        FunctionalException functionalException = Assertions.assertThrows(FunctionalException.class, () -> {
            this.assertEventType(266, "SMART_METER_E", "SMR", "iskr", EventTypeDto.POWER_FAILURE);
        });
        assertThat(functionalException.getExceptionType()).isEqualTo(FunctionalExceptionType.VALIDATION_ERROR);
    }

    @Test
    public void testDeviceType() throws FunctionalException {
        this.assertEventType(1, "XXXXXXX", "SMR", "iskr", EventTypeDto.POWER_FAILURE);
        this.assertEventType(1, "SMART_METER_E", "SMR", "iskr", EventTypeDto.POWER_FAILURE);
        this.assertEventType(1, "SMART_METER_G", "SMR", "iskr", EventTypeDto.POWER_FAILURE_G);
        this.assertEventType(1, "SMART_METER_W", "SMR", "iskr", EventTypeDto.POWER_FAILURE_W);
        this.assertEventType(2, "XXXXXXX", "SMR", "iskr", EventTypeDto.POWER_RETURNED);
    }

    @Test
    public void testProtocolNoMatch() throws FunctionalException {
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

        this.assertEventType(85, "SMART_METER_E", "DSMR_CDMA", "iskr", EventTypeDto.PV_VOLTAGE_SWELL_L3);
        this.assertEventType(85, "SMART_METER_E", "SMR_CDMA", "iskr", EventTypeDto.VOLTAGE_L3_NORMAL);
    }

    @Test
    public void testManufacturerNoMatch() throws FunctionalException {
        this.assertEventType(1, "SMART_METER_E", "SMR", "XXX", EventTypeDto.POWER_FAILURE);
    }

    @Test
    public void testAddEventTypeToEvents() throws FunctionalException {
        this.assertEventType(255, "", "", "", EventTypeDto.EVENTLOG_CLEARED);
        this.assertEventType(1, "SMART_METER_E", "", "", EventTypeDto.POWER_FAILURE);
        this.assertEventType(1, "SMART_METER_G", "", "", EventTypeDto.POWER_FAILURE_G);
        this.assertEventType(1, "SMART_METER_W", "", "", EventTypeDto.POWER_FAILURE_W);
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
        this.assertEventType(230, "", "", "Iskr", EventTypeDto.FATAL_ERROR_ISKR);
        this.assertEventType(231, "", "", "Iskr", EventTypeDto.BILLING_RESET_ISKR);
        this.assertEventType(232, "", "", "Iskr", EventTypeDto.POWER_DOWN_PHASE_L1_ISKR);
        this.assertEventType(233, "", "", "Iskr", EventTypeDto.POWER_DOWN_PHASE_L2_ISKR);
        this.assertEventType(234, "", "", "Iskr", EventTypeDto.POWER_DOWN_PHASE_L3_ISKR);
        this.assertEventType(235, "", "", "Iskr", EventTypeDto.POWER_RESTORED_PHASE_L1_ISKR);
        this.assertEventType(236, "", "", "Iskr", EventTypeDto.POWER_RESTORED_PHASE_L2_ISKR);
        this.assertEventType(237, "", "", "Iskr", EventTypeDto.POWER_RESTORED_PHASE_L3_ISKR);
        this.assertEventType(244, "", "", "Iskr", EventTypeDto.MODULE_COVER_OPENED_ISKR);
        this.assertEventType(245, "", "", "Iskr", EventTypeDto.MODULE_COVER_CLOSED_ISKR);
    }

    private void assertEventType(int eventCode, String deviceType, String protocol, String manufacturerCode,
            EventTypeDto expectedEventTypeDto) throws FunctionalException {
        when(smartMeter.getDeviceType()).thenReturn(deviceType);
        ProtocolInfo protocolInfo = mock(ProtocolInfo.class);
        when(protocolInfo.getProtocol()).thenReturn(protocol);
        when(smartMeter.getProtocolInfo()).thenReturn(protocolInfo);
        DeviceModel deviceModel = mock(DeviceModel.class);
        Manufacturer manufacturer = mock(Manufacturer.class);
        when(manufacturer.getCode()).thenReturn(manufacturerCode);
        when(deviceModel.getManufacturer()).thenReturn(manufacturer);
        when(smartMeter.getDeviceModel()).thenReturn(deviceModel);
        final EventDto event = new EventDto(new DateTime(), eventCode, 2, "STANDARD_EVENT_LOG");
        final ArrayList<EventDto> events = new ArrayList<>();
        events.add(event);
        final EventMessageDataResponseDto responseDto = new EventMessageDataResponseDto(events);

        eventService.addEventTypeToEvents(deviceMessageMetadata, responseDto);

        assertThat(responseDto.getEvents().get(0).getEventTypeDto()).isEqualTo(expectedEventTypeDto);
        assertThat(responseDto.getEvents().get(0).getEventCode()).isEqualTo(eventCode);
    }
}
