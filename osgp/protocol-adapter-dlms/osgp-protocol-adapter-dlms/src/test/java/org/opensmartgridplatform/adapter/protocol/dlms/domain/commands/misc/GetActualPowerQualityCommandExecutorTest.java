/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.GetActualPowerQualityCommandExecutor.PowerQualityObjectMetadata;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsUnitTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PowerQualityObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PowerQualityValueDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
public class GetActualPowerQualityCommandExecutorTest {
  private static final int CLASS_ID_DATA = 1;
  private static final int CLASS_ID_REGISTER = 3;
  private static final int CLASS_ID_CLOCK = 8;

  @Spy private DlmsHelper dlmsHelper;

  @Mock private DlmsDevice dlmsDevice;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsMessageListener dlmsMessageListener;

  private ActualPowerQualityRequestDto actualPowerQualityRequestDto;
  private MessageMetadata messageMetadata;

  @BeforeEach
  public void before() throws ProtocolAdapterException, IOException {
    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();

    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
  }

  @Test
  void testRetrievalPublic() throws ProtocolAdapterException {
    when(this.dlmsDevice.isPolyphase()).thenReturn(true);
    this.executeAndAssert("PUBLIC", GetActualPowerQualityCommandExecutor.getMetadatasPublic());
  }

  @Test
  void testRetrievalPrivate() throws ProtocolAdapterException {
    when(this.dlmsDevice.isPolyphase()).thenReturn(true);
    this.executeAndAssert("PRIVATE", GetActualPowerQualityCommandExecutor.getMetadatasPrivate());
  }

  @Test
  void testRetrievalPublicSinglePhase() throws ProtocolAdapterException {
    when(this.dlmsDevice.isPolyphase()).thenReturn(false);
    this.executeAndAssert(
        "PUBLIC",
        GetActualPowerQualityCommandExecutor.getMetadatasPublic().stream()
            .filter(metadata -> !metadata.isPolyphaseOnly())
            .collect(Collectors.toList()));
  }

  @Test
  void testRetrievalPrivateSinglePhase() throws ProtocolAdapterException {
    when(this.dlmsDevice.isPolyphase()).thenReturn(false);
    this.executeAndAssert(
        "PRIVATE",
        GetActualPowerQualityCommandExecutor.getMetadatasPrivate().stream()
            .filter(metadata -> !metadata.isPolyphaseOnly())
            .collect(Collectors.toList()));
  }

  @Test
  void testOtherReasonResult() throws ProtocolAdapterException {
    this.actualPowerQualityRequestDto = new ActualPowerQualityRequestDto("PRIVATE");

    final List<GetActualPowerQualityCommandExecutor.PowerQualityObjectMetadata> metadatas =
        GetActualPowerQualityCommandExecutor.getMetadatasPrivate();

    doReturn(this.generateMockedResult(metadatas, AccessResultCode.OTHER_REASON))
        .when(this.dlmsHelper)
        .getAndCheck(
            eq(this.conn),
            eq(this.dlmsDevice),
            eq("retrieve actual power quality"),
            any(AttributeAddress.class));

    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () -> {
              new GetActualPowerQualityCommandExecutor(this.dlmsHelper)
                  .execute(
                      this.conn,
                      this.dlmsDevice,
                      this.actualPowerQualityRequestDto,
                      this.messageMetadata);
            });
  }

  void executeAndAssert(
      final String profileType,
      final List<GetActualPowerQualityCommandExecutor.PowerQualityObjectMetadata> metadatas)
      throws ProtocolAdapterException {
    this.actualPowerQualityRequestDto = new ActualPowerQualityRequestDto(profileType);

    doReturn(this.generateMockedResult(metadatas, AccessResultCode.SUCCESS))
        .when(this.dlmsHelper)
        .getAndCheck(
            eq(this.conn),
            eq(this.dlmsDevice),
            eq("retrieve actual power quality"),
            any(AttributeAddress.class));

    final GetActualPowerQualityCommandExecutor executor =
        new GetActualPowerQualityCommandExecutor(this.dlmsHelper);

    final ActualPowerQualityResponseDto responseDto =
        executor.execute(
            this.conn, this.dlmsDevice, this.actualPowerQualityRequestDto, this.messageMetadata);

    assertThat(responseDto.getActualPowerQualityData().getPowerQualityValues())
        .hasSize(metadatas.size());
    assertThat(responseDto.getActualPowerQualityData().getPowerQualityObjects())
        .hasSize(metadatas.size());

    for (int i = 0; i < metadatas.size(); i++) {
      final GetActualPowerQualityCommandExecutor.PowerQualityObjectMetadata metadata =
          metadatas.get(i);

      final Serializable expectedValue = this.getExpectedValue(i, metadata);
      final String expectedUnit = this.getExpectedUnit(metadata);

      final PowerQualityObjectDto powerQualityObjectDto =
          responseDto.getActualPowerQualityData().getPowerQualityObjects().get(i);
      assertThat(powerQualityObjectDto.getName()).isEqualTo(metadata.name());
      assertThat(powerQualityObjectDto.getUnit()).isEqualTo(expectedUnit);

      final PowerQualityValueDto powerQualityValue =
          responseDto.getActualPowerQualityData().getPowerQualityValues().get(i);
      assertThat(powerQualityValue.getValue()).isEqualTo(expectedValue);
    }
  }

  private Serializable getExpectedValue(
      final int i, final GetActualPowerQualityCommandExecutor.PowerQualityObjectMetadata metadata) {
    switch (metadata.getClassId()) {
      case CLASS_ID_CLOCK:
        return DateTime.parse("2018-12-31T23:00:00Z").toDate();
      case CLASS_ID_REGISTER:
        return BigDecimal.valueOf(i * 10.0);
      case CLASS_ID_DATA:
        return BigDecimal.valueOf(i);
      default:
        return null;
    }
  }

  private String getExpectedUnit(
      final GetActualPowerQualityCommandExecutor.PowerQualityObjectMetadata metadata) {
    switch (metadata.getClassId()) {
      case CLASS_ID_REGISTER:
        return DlmsUnitTypeDto.VOLT.getUnit();
      default:
        return null;
    }
  }

  private List<GetResult> generateMockedResult(
      final List<GetActualPowerQualityCommandExecutor.PowerQualityObjectMetadata> metadatas,
      final AccessResultCode resultCode) {
    return this.generateMockedResult(
        metadatas,
        resultCode,
        DataObject.newDateTimeData(new CosemDateTime(2018, 12, 31, 23, 0, 0, 0)));
  }

  private List<GetResult> generateMockedResult(
      final List<GetActualPowerQualityCommandExecutor.PowerQualityObjectMetadata> metadatas,
      final AccessResultCode resultCode,
      final DataObject dateTimeDataObject) {
    final List<GetResult> results = new ArrayList<>();

    int idx = 1;
    for (final PowerQualityObjectMetadata metadata : metadatas) {
      if (metadata.getClassId() == CLASS_ID_CLOCK) {
        results.add(new GetResultImpl(dateTimeDataObject, resultCode));
      } else {
        results.add(new GetResultImpl(DataObject.newInteger64Data(idx++), resultCode));
        if (metadata.getClassId() == CLASS_ID_REGISTER) {
          final List<DataObject> scalerUnit = new ArrayList<>();
          scalerUnit.add(DataObject.newInteger64Data(1));
          scalerUnit.add(DataObject.newInteger64Data(DlmsUnitTypeDto.VOLT.getIndex()));
          results.add(new GetResultImpl(DataObject.newArrayData(scalerUnit), resultCode));
        }
      }
    }
    return results;
  }
}
