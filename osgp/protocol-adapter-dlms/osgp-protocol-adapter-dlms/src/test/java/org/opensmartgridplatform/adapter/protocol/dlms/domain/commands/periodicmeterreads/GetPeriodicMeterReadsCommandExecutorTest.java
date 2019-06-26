/**
 * Copyright 2019 Smart Society Services B.V.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.AttributeAddressForProfile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsCaptureObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.*;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.*;

import java.util.*;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.GetPeriodicMeterReadsCommandExecutor.PERIODIC_E_METER_READS;

@RunWith(MockitoJUnitRunner.class)
public class GetPeriodicMeterReadsCommandExecutorTest {

    @InjectMocks
    private GetPeriodicMeterReadsCommandExecutor executor;

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

    @Before
    public void setUp() {
        when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    }

    @Test
    public void testHappy() throws Exception {

        // SETUP
        final PeriodTypeDto periodType = PeriodTypeDto.DAILY;
        final PeriodicMeterReadsRequestDto request = new PeriodicMeterReadsRequestDto(periodType, new Date(from),
                new Date(to));

        // SETUP - dlms objects
        final DlmsObject dlmsClock = new DlmsClock("0.0.1.0.0.255");
        final DlmsCaptureObject captureObject1 = new DlmsCaptureObject(dlmsClock, 2);

//        final DlmsObject amrStatusDailyE = new DlmsData(AMR_STATUS, "0.0.96.10.4.255");
//        final DlmsCaptureObject captureObjectDailyE = new DlmsCaptureObject(amrStatusDailyE, 2);

        DlmsObject activeEnergyImportRate1 = new DlmsObject(DlmsObjectType.ACTIVE_ENERGY_IMPORT_RATE_1, 0, "1.0.1.8.1.255");
        final DlmsCaptureObject captureObject2 = new DlmsCaptureObject(activeEnergyImportRate1, 2);
        DlmsObject activeEnergyImportRate2 = new DlmsObject(DlmsObjectType.ACTIVE_ENERGY_IMPORT_RATE_2, 0, "1.0.1.8.2.255");
        final DlmsCaptureObject captureObject3 = new DlmsCaptureObject(activeEnergyImportRate2, 2);
        DlmsObject activeEnergyExportRate1 = new DlmsObject(DlmsObjectType.ACTIVE_ENERGY_EXPORT_RATE_1, 0, "1.0.2.8.1.255");
        final DlmsCaptureObject captureObject4 = new DlmsCaptureObject(activeEnergyExportRate1, 2);
        DlmsObject activeEnergyExportRate2 = new DlmsObject(DlmsObjectType.ACTIVE_ENERGY_EXPORT_RATE_2, 0, "1.0.2.8.2.255");
        final DlmsCaptureObject captureObject5 = new DlmsCaptureObject(activeEnergyExportRate2, 2);

        final DlmsObject dlmsExtendedRegister = new DlmsExtendedRegister(DlmsObjectType.ACTIVE_ENERGY_IMPORT_RATE_1,
                "0.0.24.0.0.255", 0, RegisterUnit.WH, Medium.ELECTRICITY);

        final List<DlmsCaptureObject> captureObjects = Arrays.asList(captureObject1, captureObject2, captureObject3, captureObject4, captureObject5);

        final DlmsProfile dlmsProfile = new DlmsProfile(DlmsObjectType.DAILY_LOAD_PROFILE, "1.0.99.2.0.255",
                captureObjects, ProfileCaptureTime.DAY, Medium.ELECTRICITY);

        // SETUP - mock dlms object config to return attribute addresses
        final AttributeAddressForProfile attributeAddressForProfile = this.createAttributeAddressForProfile(dlmsProfile,
                captureObjects);
        final AttributeAddress attributeAddress = this.createAttributeAddress(dlmsExtendedRegister);

        when(this.dlmsObjectConfigService.findAttributeAddressForProfile(eq(this.device),
                eq(DlmsObjectType.DAILY_LOAD_PROFILE), eq(0), eq(this.fromDateTime),
                eq(this.toDateTime), eq(Medium.ELECTRICITY))).thenReturn(Optional.of(attributeAddressForProfile));

        DlmsObject intervalTime = mock(DlmsObject.class);
        when(this.dlmsObjectConfigService.findDlmsObject(any(Protocol.class),
                any(DlmsObjectType.class), any(Medium.class))).thenReturn(Optional.of(intervalTime));

        // SETUP - mock dlms helper to return data objects on request
        final DataObject data0 = mock(DataObject.class);

        // make sure to set logTime on first dataObject
        List<DataObject> bufferedObjectValue = new ArrayList<>();
        when(data0.getValue()).thenReturn(bufferedObjectValue);

        final DataObject data1 = mock(DataObject.class);
        when(data1.isNumber()).thenReturn(true);
        final DataObject data2 = mock(DataObject.class);
        final DataObject data3 = mock(DataObject.class);
        final DataObject data4 = mock(DataObject.class);
        final DataObject data5 = mock(DataObject.class);
        final DataObject bufferedObject = mock(DataObject.class);
        when(bufferedObject.getValue()).thenReturn(asList(data0, data1, data2, data3, data4, data5));
        final DataObject resultData = mock(DataObject.class);
        when(resultData.getValue()).thenReturn(Collections.singletonList(bufferedObject));


        final String expectedDescription = "retrieve periodic meter reads for " + periodType;
        final GetResult result0 = mock(GetResult.class);
        final GetResult result1 = mock(GetResult.class);
        final GetResult result2 = mock(GetResult.class);
        final GetResult result3 = mock(GetResult.class);
        final GetResult result4 = mock(GetResult.class);
        final GetResult result5 = mock(GetResult.class);

        final GetResult getResult = mock(GetResult.class);

        when(this.dlmsHelper.getAndCheck(this.connectionManager, device, expectedDescription, attributeAddress))
                .thenReturn(asList(result0, result1, result2, result3, result4, result5));

        when(this.dlmsHelper.readDataObject(result0, PERIODIC_E_METER_READS)).thenReturn(resultData);

        when(this.dlmsHelper.getAndCheck(eq(this.connectionManager), eq(this.device), eq(expectedDescription),
                eq(attributeAddressForProfile.getAttributeAddress()))).thenReturn(Collections.singletonList(getResult));
        when(this.dlmsHelper.getAndCheck(this.connectionManager, this.device, expectedDescription,
                attributeAddress)).thenReturn(Collections.singletonList(getResult));

        when(this.dlmsHelper.readDataObject(eq(getResult), any(String.class))).thenReturn(resultData);

        final CosemDateTimeDto cosemDateTime = mock(CosemDateTimeDto.class);
        final String expectedDateTimeDescription = String.format("Clock from %s buffer gas", periodType);
        when(this.dlmsHelper.readDateTime(data0, expectedDateTimeDescription)).thenReturn(cosemDateTime);

        final DateTime bufferedDateTime = DateTime.now();
        when(cosemDateTime.asDateTime()).thenReturn(bufferedDateTime);

        final DlmsMeterValueDto meterValue1 = mock(DlmsMeterValueDto.class);
        final DlmsMeterValueDto meterValue2 = mock(DlmsMeterValueDto.class);
        when(this.dlmsHelper.getScaledMeterValue(data1, null, "positiveActiveEnergyTariff1")).thenReturn(meterValue1);
        when(this.dlmsHelper.getScaledMeterValue(data4, null, "gasValue")).thenReturn(meterValue2);

        // CALL
        final PeriodicMeterReadsResponseDto result = this.executor.execute(this.connectionManager, device, request);

        // VERIFY calls to mocks
        verify(this.dlmsMessageListener).setDescription(String.format(
                "GetPeriodicMeterReads DAILY from %s until %s, retrieve attribute: {%s,%s,%s}",
                new DateTime(this.from), new DateTime(this.to), dlmsProfile.getClassId(), dlmsProfile.getObisCode(),
                dlmsProfile.getDefaultAttributeId()));

        verify(this.dlmsHelper).validateBufferedDateTime(same(bufferedDateTime), same(cosemDateTime),
                argThat(new DateTimeMatcher(from)), argThat(new DateTimeMatcher(to)));

        verify(this.dlmsObjectConfigService).findDlmsObject(any(Protocol.class),
                any(DlmsObjectType.class), any(Medium.class));

        // ASSERT - the result should contain 1 value
        final List<PeriodicMeterReadsResponseItemDto> periodicMeterReads = result.getPeriodicMeterReads();

        assertThat(periodicMeterReads.size()).isEqualTo(1);

        PeriodicMeterReadsResponseItemDto periodicMeterReadsResponseItemDto = periodicMeterReads.get(0);
        assertThat(periodicMeterReadsResponseItemDto.getLogTime()).isNotNull();

    }

    private AttributeAddressForProfile createAttributeAddressForProfile(final DlmsObject dlmsObject,
                                                                        final List<DlmsCaptureObject> selectedObjects) {
        return new AttributeAddressForProfile(new AttributeAddress(dlmsObject.getClassId(),
                new ObisCode(dlmsObject.getObisCode()), dlmsObject.getDefaultAttributeId(), null), selectedObjects);
    }

    private AttributeAddress createAttributeAddress(final DlmsObject dlmsObject) {
        return new AttributeAddress(dlmsObject.getClassId(), new ObisCode(dlmsObject.getObisCode()),
                dlmsObject.getDefaultAttributeId());
    }

    private DlmsDevice createDevice(final Protocol protocol) {
        final DlmsDevice device = new DlmsDevice();
        device.setProtocol(protocol.getName(), protocol.getVersion());
        return device;
    }
}

