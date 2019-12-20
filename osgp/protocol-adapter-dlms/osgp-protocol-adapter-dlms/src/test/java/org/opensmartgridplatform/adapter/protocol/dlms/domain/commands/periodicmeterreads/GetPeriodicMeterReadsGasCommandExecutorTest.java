/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.AttributeAddressForProfile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsCaptureObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsClock;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsExtendedRegister;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsProfile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.Medium;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.ProfileCaptureTime;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.RegisterUnit;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadGasResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsGasResponseItemDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GetPeriodicMeterReadsGasCommandExecutorTest {

    @InjectMocks
    private GetPeriodicMeterReadsGasCommandExecutor executor;

    @Mock
    private DlmsMessageListener dlmsMessageListener;

    @Mock
    private DlmsHelper dlmsHelper;

    @Mock
    private DlmsObjectConfigService dlmsObjectConfigService;

    @Mock
    private DlmsConnectionManager connectionManager;

    private final DlmsDevice device = this.createDevice(Protocol.DSMR_4_2_2);
    private final long from = 1111111L;
    private final long to = 2222222L;
    private final DateTime fromDateTime = new DateTime(this.from);
    private final DateTime toDateTime = new DateTime(this.to);

    @BeforeEach
    public void setUp() {
        when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    }

    @Test
    public void testExecuteNullRequest() throws ProtocolAdapterException {
        try {
            this.executor.execute(this.connectionManager, this.device, null);
            fail("Calling execute with null query should fail");
        } catch (final IllegalArgumentException e) {
            // expected exception
        }
    }

    @Test
    public void testExecuteObjectNotFound() {
        // SETUP
        final PeriodicMeterReadsRequestDto request = new PeriodicMeterReadsRequestDto(PeriodTypeDto.DAILY,
                this.fromDateTime.toDate(), this.toDateTime.toDate(), ChannelDto.ONE);
        when(this.dlmsObjectConfigService.findAttributeAddressForProfile(any(), any(), any(), any(), any(), any()))
                .thenReturn(Optional.empty());

        // CALL
        try {
            this.executor.execute(this.connectionManager, this.device, request);
            fail("When no matching object is found, then execute should fail");
        } catch (final ProtocolAdapterException e) {
            assertThat(e.getMessage()).isEqualTo("No address found for " + DlmsObjectType.DAILY_LOAD_PROFILE);
        }
    }

    @Test
    public void testHappy() throws Exception {

        // SETUP - request
        final PeriodTypeDto periodType = PeriodTypeDto.DAILY;
        final ChannelDto channel = ChannelDto.ONE;
        final PeriodicMeterReadsRequestDto request = new PeriodicMeterReadsRequestDto(periodType,
                this.fromDateTime.toDate(), this.toDateTime.toDate(), channel);

        // SETUP - dlms objects
        final DlmsObject dlmsClock = new DlmsClock("0.0.1.0.0.255");
        final DlmsCaptureObject captureObject1 = new DlmsCaptureObject(dlmsClock, 2);

        final DlmsObject dlmsExtendedRegister = new DlmsExtendedRegister(DlmsObjectType.MBUS_MASTER_VALUE,
                "0.0.24.0.0.255", 0, RegisterUnit.M3, Medium.GAS);
        final DlmsCaptureObject captureObject2 = new DlmsCaptureObject(dlmsExtendedRegister, 2);
        final DlmsCaptureObject captureObject3 = new DlmsCaptureObject(dlmsExtendedRegister, 5);

        final List<DlmsCaptureObject> captureObjects = Arrays.asList(captureObject1, captureObject2, captureObject3);

        final DlmsProfile dlmsProfile = new DlmsProfile(DlmsObjectType.DAILY_LOAD_PROFILE, "1.2.3.4.5.6",
                captureObjects, ProfileCaptureTime.DAY, Medium.COMBINED);

        // SETUP - mock dlms object config to return attribute addresses
        final AttributeAddressForProfile attributeAddressForProfile = this.createAttributeAddressForProfile(dlmsProfile,
                captureObjects);
        final AttributeAddress attributeAddressScalerUnit = this.createAttributeAddress(dlmsExtendedRegister);

        when(this.dlmsObjectConfigService.findAttributeAddressForProfile(eq(this.device),
                eq(DlmsObjectType.DAILY_LOAD_PROFILE), eq(channel.getChannelNumber()), eq(this.fromDateTime),
                eq(this.toDateTime), eq(Medium.GAS))).thenReturn(Optional.of(attributeAddressForProfile));

        when(this.dlmsObjectConfigService.getAttributeAddressesForScalerUnit(eq(attributeAddressForProfile),
                eq(channel.getChannelNumber()))).thenReturn(Collections.singletonList(attributeAddressScalerUnit));

        final DlmsObject intervalTime = mock(DlmsObject.class);
        when(this.dlmsObjectConfigService.findDlmsObject(any(Protocol.class), any(DlmsObjectType.class),
                any(Medium.class))).thenReturn(Optional.of(intervalTime));

        // SETUP - mock dlms helper to return data objects on request
        final DataObject data0 = mock(DataObject.class);
        final DataObject data1 = mock(DataObject.class);
        final DataObject data2 = mock(DataObject.class);
        final DataObject bufferedObject1 = mock(DataObject.class);
        when(bufferedObject1.getValue()).thenReturn(asList(data0, data1, data2));

        final DataObject data3 = mock(DataObject.class);
        final DataObject data4 = mock(DataObject.class);
        final DataObject data5 = mock(DataObject.class);
        final DataObject bufferedObject2 = mock(DataObject.class);
        when(bufferedObject2.getValue()).thenReturn(asList(data3, data4, data5));

        final DataObject resultData = mock(DataObject.class);
        when(resultData.getValue()).thenReturn(asList(bufferedObject1, bufferedObject2));

        final String expectedDescription = "retrieve periodic meter reads for " + periodType + ", channel " + channel;
        final GetResult getResult = mock(GetResult.class);
        when(this.dlmsHelper.getAndCheck(eq(this.connectionManager), eq(this.device), eq(expectedDescription),
                eq(attributeAddressForProfile.getAttributeAddress()))).thenReturn(Collections.singletonList(getResult));
        when(this.dlmsHelper.getAndCheck(this.connectionManager, this.device, expectedDescription,
                attributeAddressScalerUnit)).thenReturn(Collections.singletonList(getResult));

        when(this.dlmsHelper.readDataObject(eq(getResult), any(String.class))).thenReturn(resultData);

        // SETUP - mock dlms helper to handle converting the data objects
        final String expectedDateTimeDescriptionLogTime = String.format("Clock from %s buffer", periodType);
        final String expectedDateTimeDescriptionCaptureTime = "Clock from mbus interval extended register";
        final CosemDateTimeDto cosemDateTime = new CosemDateTimeDto(this.fromDateTime);
        when(this.dlmsHelper.readDateTime(data0, expectedDateTimeDescriptionLogTime)).thenReturn(cosemDateTime);
        when(this.dlmsHelper.readDateTime(data3, expectedDateTimeDescriptionLogTime)).thenReturn(cosemDateTime);
        when(this.dlmsHelper.readDateTime(data2, expectedDateTimeDescriptionCaptureTime)).thenReturn(cosemDateTime);
        when(this.dlmsHelper.readDateTime(data5, expectedDateTimeDescriptionCaptureTime)).thenReturn(cosemDateTime);

        final DlmsMeterValueDto meterValue1 = mock(DlmsMeterValueDto.class);
        final DlmsMeterValueDto meterValue2 = mock(DlmsMeterValueDto.class);
        when(this.dlmsHelper.getScaledMeterValue(data1, null, "gasValue")).thenReturn(meterValue1);
        when(this.dlmsHelper.getScaledMeterValue(data4, null, "gasValue")).thenReturn(meterValue2);

        // CALL
        final PeriodicMeterReadGasResponseDto result = this.executor.execute(this.connectionManager, this.device,
                request);

        // VERIFY - the right functions should be called
        verify(this.dlmsMessageListener).setDescription(String.format(
                "GetPeriodicMeterReadsGas for channel ONE, DAILY from %s until %s, retrieve attribute: {%s,%s,%s}",
                new DateTime(this.from), new DateTime(this.to), dlmsProfile.getClassId(),
                dlmsProfile.getObisCodeAsString(), dlmsProfile.getDefaultAttributeId()));

        verify(this.dlmsHelper, times(2)).validateBufferedDateTime(any(DateTime.class),
                argThat(new DateTimeMatcher(this.from)), argThat(new DateTimeMatcher(this.to)));

        verify(this.dlmsObjectConfigService).findDlmsObject(any(Protocol.class), any(DlmsObjectType.class),
                any(Medium.class));

        // ASSERT - the result should contain 2 values
        final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReads = result.getPeriodicMeterReadsGas();

        assertThat(periodicMeterReads.size()).isEqualTo(2);
        assertThat(periodicMeterReads.stream().anyMatch(r -> r.getConsumption() == meterValue1)).isEqualTo(true);
        assertThat(periodicMeterReads.stream().anyMatch(r -> r.getConsumption() == meterValue2)).isEqualTo(true);
        assertThat(periodicMeterReads.stream().allMatch(r -> this.areDatesEqual(r.getLogTime(), cosemDateTime)))
                .isEqualTo(true);
        assertThat(periodicMeterReads.stream().allMatch(r -> this.areDatesEqual(r.getCaptureTime(), cosemDateTime)))
                .isEqualTo(true);
    }

    private AttributeAddress createAttributeAddress(final DlmsObject dlmsObject) {
        return new AttributeAddress(dlmsObject.getClassId(), new ObisCode(dlmsObject.getObisCodeAsString()),
                dlmsObject.getDefaultAttributeId());
    }

    private AttributeAddressForProfile createAttributeAddressForProfile(final DlmsObject dlmsObject,
            final List<DlmsCaptureObject> selectedObjects) {
        return new AttributeAddressForProfile(new AttributeAddress(dlmsObject.getClassId(),
                new ObisCode(dlmsObject.getObisCodeAsString()), dlmsObject.getDefaultAttributeId(), null),
                selectedObjects);
    }

    private DlmsDevice createDevice(final Protocol protocol) {
        final DlmsDevice device = new DlmsDevice();
        device.setProtocol(protocol);
        return device;
    }

    // Compares date with cosemDateTime. Note: cosemDateTime uses hundredths and
    // not milliseconds
    private boolean areDatesEqual(final Date date, final CosemDateTimeDto cosemDateTime) {
        final DateTime dateTime = new DateTime(date);
        final CosemDateDto cosemDate = cosemDateTime.getDate();
        final CosemTimeDto cosemTime = cosemDateTime.getTime();

        return (dateTime.getYear() == cosemDate.getYear() && dateTime.getMonthOfYear() == cosemDate.getMonth()
                && dateTime.getDayOfMonth() == cosemDate.getDayOfMonth()
                && dateTime.getHourOfDay() == cosemTime.getHour() && dateTime.getMinuteOfHour() == cosemTime.getMinute()
                && dateTime.getSecondOfMinute() == cosemTime.getSecond()
                && dateTime.getMillisOfSecond() == cosemTime.getHundredths() * 10);
    }
}
