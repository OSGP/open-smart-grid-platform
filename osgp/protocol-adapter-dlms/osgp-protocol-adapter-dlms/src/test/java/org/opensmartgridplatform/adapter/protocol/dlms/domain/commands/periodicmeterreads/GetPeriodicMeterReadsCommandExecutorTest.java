/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.GetPeriodicMeterReadsCommandExecutor.PERIODIC_E_METER_READS;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.AttributeAddressForProfile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsCaptureObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsClock;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsProfile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.Medium;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.ProfileCaptureTime;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsDateTimeConverter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseItemDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GetPeriodicMeterReadsCommandExecutorTest {

  @InjectMocks private GetPeriodicMeterReadsCommandExecutor executor;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsHelper dlmsHelper;

  @Mock private DlmsObjectConfigService dlmsObjectConfigService;

  @Mock private DlmsConnectionManager connectionManager;

  private final DlmsDevice device = this.createDevice(Protocol.DSMR_4_2_2);
  private final long from = 1111110L;
  private final long to = 2222222L;
  private final DateTime fromDateTime =
      DlmsDateTimeConverter.toDateTime(new Date(this.from), device);
  private final DateTime toDateTime =
      DlmsDateTimeConverter.toDateTime(new Date(this.to), device);

  private final String DEFAULT_TIMEZONE = "UTC";
  private MessageMetadata messageMetadata;

  @BeforeEach
  void setUp() {
    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();
    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
  }

  @Test()
  void testExecuteNullRequest() {
    assertThatThrownBy(
            () ->
                this.executor.execute(
                    this.connectionManager, this.device, null, this.messageMetadata))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void testExecuteObjectNotFound() {
    // SETUP
    final PeriodicMeterReadsRequestDto request =
        new PeriodicMeterReadsRequestDto(
            PeriodTypeDto.DAILY,
            this.fromDateTime.toDate(),
            this.toDateTime.toDate(),
            ChannelDto.ONE);
    when(this.dlmsObjectConfigService.findAttributeAddressForProfile(
            any(), any(), any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    // CALL
    try {
      this.executor.execute(this.connectionManager, this.device, request, this.messageMetadata);
      fail("When no matching object is found, then execute should fail");
    } catch (final ProtocolAdapterException e) {
      assertThat(e.getMessage())
          .isEqualTo("No address found for " + DlmsObjectType.DAILY_LOAD_PROFILE);
    }
  }

  @Test
  void testBundle() throws ProtocolAdapterException {
    final PeriodicMeterReadsRequestDataDto request =
        new PeriodicMeterReadsRequestDataDto(
            PeriodTypeDto.DAILY, new Date(this.from), new Date(this.to));

    final PeriodicMeterReadsRequestDto dto = this.executor.fromBundleRequestInput(request);

    assertThat(dto).isNotNull();
  }

  @ParameterizedTest
  @ValueSource(strings = {"UTC", "Pacific/Honolulu"})
  void testHappyWithDifferentTimeZones(final String timeZone) throws Exception {

    // SETUP
    final PeriodTypeDto periodType = PeriodTypeDto.DAILY;
    final PeriodicMeterReadsRequestDto request =
        new PeriodicMeterReadsRequestDto(periodType, new Date(this.from), new Date(this.to));

    this.device.setTimezone(timeZone);
    final DateTime convertedFromTime = DlmsDateTimeConverter.toDateTime(new Date(this.from), device);
    final DateTime convertedToTime = DlmsDateTimeConverter.toDateTime(new Date(this.to), device);

    // SETUP - dlms objects
    final DlmsObject dlmsClock = new DlmsClock("0.0.1.0.0.255");
    final DlmsCaptureObject captureObject1 = new DlmsCaptureObject(dlmsClock, 2);

    final DlmsObject activeEnergyImportRate1 =
        new DlmsObject(DlmsObjectType.ACTIVE_ENERGY_IMPORT_RATE_1, 0, "1.0.1.8.1.255");
    final DlmsCaptureObject captureObject2 = new DlmsCaptureObject(activeEnergyImportRate1, 2);
    final DlmsObject activeEnergyImportRate2 =
        new DlmsObject(DlmsObjectType.ACTIVE_ENERGY_IMPORT_RATE_2, 0, "1.0.1.8.2.255");
    final DlmsCaptureObject captureObject3 = new DlmsCaptureObject(activeEnergyImportRate2, 2);
    final DlmsObject activeEnergyExportRate1 =
        new DlmsObject(DlmsObjectType.ACTIVE_ENERGY_EXPORT_RATE_1, 0, "1.0.2.8.1.255");
    final DlmsCaptureObject captureObject4 = new DlmsCaptureObject(activeEnergyExportRate1, 2);
    final DlmsObject activeEnergyExportRate2 =
        new DlmsObject(DlmsObjectType.ACTIVE_ENERGY_EXPORT_RATE_2, 0, "1.0.2.8.2.255");
    final DlmsCaptureObject captureObject5 = new DlmsCaptureObject(activeEnergyExportRate2, 2);

    final List<DlmsCaptureObject> captureObjects =
        Arrays.asList(
            captureObject1, captureObject2, captureObject3, captureObject4, captureObject5);

    final DlmsProfile dlmsProfile =
        new DlmsProfile(
            DlmsObjectType.DAILY_LOAD_PROFILE,
            "1.0.99.2.0.255",
            captureObjects,
            ProfileCaptureTime.DAY,
            Medium.ELECTRICITY);

    // SETUP - mock dlms object config to return attribute addresses
    final AttributeAddressForProfile attributeAddressForProfile =
        this.createAttributeAddressForProfile(dlmsProfile, captureObjects);
    final AttributeAddress attributeAddress = this.createAttributeAddress(dlmsProfile);

    when(this.dlmsObjectConfigService.findAttributeAddressForProfile(
            this.device,
            DlmsObjectType.DAILY_LOAD_PROFILE,
            0,
            convertedFromTime,
            convertedToTime,
            Medium.ELECTRICITY))
        .thenReturn(Optional.of(attributeAddressForProfile));

    final DlmsObject intervalTime = mock(DlmsObject.class);
    when(this.dlmsObjectConfigService.findDlmsObject(
            any(Protocol.class), any(DlmsObjectType.class), any(Medium.class)))
        .thenReturn(Optional.of(intervalTime));

    // SETUP - mock dlms helper to return data objects on request
    final DataObject data0_object1 = mock(DataObject.class);
    final DataObject data0_object2 = mock(DataObject.class);
    final DataObject data0_object3 = mock(DataObject.class);
    final DataObject data1 = mock(DataObject.class);
    when(data1.isNumber()).thenReturn(true);
    final DataObject data2 = mock(DataObject.class);
    final DataObject data3 = mock(DataObject.class);
    final DataObject data4 = mock(DataObject.class);
    final DataObject data5 = mock(DataObject.class);
    final DataObject bufferedObject1 = mock(DataObject.class);
    when(bufferedObject1.getValue())
        .thenReturn(asList(data0_object1, data1, data2, data3, data4, data5));
    final DataObject bufferedObject2 = mock(DataObject.class);
    when(bufferedObject2.getValue())
        .thenReturn(asList(data0_object2, data1, data2, data3, data4, data5));
    final DataObject bufferedObject3 = mock(DataObject.class);
    when(bufferedObject3.getValue())
        .thenReturn(asList(data0_object3, data1, data2, data3, data4, data5));
    final DataObject resultData = mock(DataObject.class);
    when(resultData.getValue())
        .thenReturn(Arrays.asList(bufferedObject1, bufferedObject2, bufferedObject3));

    final String expectedDescription = "retrieve periodic meter reads for " + periodType;
    final GetResult result0 = mock(GetResult.class);
    final GetResult result1 = mock(GetResult.class);
    final GetResult result2 = mock(GetResult.class);
    final GetResult result3 = mock(GetResult.class);
    final GetResult result4 = mock(GetResult.class);
    final GetResult result5 = mock(GetResult.class);

    final GetResult getResult = mock(GetResult.class);

    when(this.dlmsHelper.getAndCheck(
            this.connectionManager, this.device, expectedDescription, attributeAddress))
        .thenReturn(asList(result0, result1, result2, result3, result4, result5));

    when(this.dlmsHelper.readDataObject(result0, PERIODIC_E_METER_READS)).thenReturn(resultData);

    when(this.dlmsHelper.getAndCheck(
            this.connectionManager,
            this.device,
            expectedDescription,
            attributeAddressForProfile.getAttributeAddress()))
        .thenReturn(Collections.singletonList(getResult));
    when(this.dlmsHelper.getAndCheck(
            this.connectionManager, this.device, expectedDescription, attributeAddress))
        .thenReturn(Collections.singletonList(getResult));

    when(this.dlmsHelper.readDataObject(eq(getResult), any(String.class))).thenReturn(resultData);

    // Make mocks return different logtimes for each meterread. The last meterread has a time
    // outside of the requested period, causing the meterread to be not included in the result.
    final CosemDateTimeDto cosemDateTime_1 = mock(CosemDateTimeDto.class);
    when(this.dlmsHelper.readDateTime(eq(data0_object1), any())).thenReturn(cosemDateTime_1);
    when(cosemDateTime_1.asDateTime()).thenReturn(convertedFromTime);
    final CosemDateTimeDto cosemDateTime_2 = mock(CosemDateTimeDto.class);
    when(this.dlmsHelper.readDateTime(eq(data0_object2), any())).thenReturn(cosemDateTime_2);
    when(cosemDateTime_2.asDateTime()).thenReturn(convertedFromTime.plusMinutes(1));
    final CosemDateTimeDto cosemDateTime_3 = mock(CosemDateTimeDto.class);
    when(this.dlmsHelper.readDateTime(eq(data0_object3), any())).thenReturn(cosemDateTime_3);
    when(cosemDateTime_3.asDateTime()).thenReturn(convertedFromTime.plusYears(1));

    // CALL
    final PeriodicMeterReadsResponseDto result =
        this.executor.execute(this.connectionManager, this.device, request, this.messageMetadata);

    // VERIFY calls to mocks
    verify(this.dlmsMessageListener)
        .setDescription(
            String.format(
                "GetPeriodicMeterReads DAILY from %s until %s, retrieve attribute: {%s,%s,%s}",
                convertedFromTime,
                convertedToTime,
                dlmsProfile.getClassId(),
                dlmsProfile.getObisCode(),
                dlmsProfile.getDefaultAttributeId()));

    verify(this.dlmsObjectConfigService)
        .findDlmsObject(any(Protocol.class), any(DlmsObjectType.class), any(Medium.class));

    // ASSERT - the result should contain 2 values
    final List<PeriodicMeterReadsResponseItemDto> periodicMeterReads =
        result.getPeriodicMeterReads();

    // Only 2 meterreads are expected. The 3rd meterread has a logtime outside the requested period.
    assertThat(periodicMeterReads.size()).isEqualTo(2);

    periodicMeterReads.forEach(p -> assertThat(p.getLogTime()).isNotNull());
  }

  private AttributeAddressForProfile createAttributeAddressForProfile(
      final DlmsObject dlmsObject, final List<DlmsCaptureObject> selectedObjects) {
    return new AttributeAddressForProfile(
        new AttributeAddress(
            dlmsObject.getClassId(),
            dlmsObject.getObisCode(),
            dlmsObject.getDefaultAttributeId(),
            null),
        selectedObjects);
  }

  private AttributeAddress createAttributeAddress(final DlmsObject dlmsObject) {
    return new AttributeAddress(
        dlmsObject.getClassId(), dlmsObject.getObisCode(), dlmsObject.getDefaultAttributeId());
  }

  private DlmsDevice createDevice(final Protocol protocol) {
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(protocol);
    return device;
  }
}
