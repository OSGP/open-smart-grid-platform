/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.LoggingDlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClearMBusStatusRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class ClearMBusStatusCommandExecutorTest {

  private static final String OBIS_CODE_TEMPLATE_READ_STATUS = "0.x.24.2.6.255";
  private static final int CLASS_ID_READ_STATUS = 4;

  private static final String OBIS_CODE_TEMPLATE_CLEAR_STATUS = "0.x.94.31.10.255";
  private static final int CLASS_ID_CLEAR_STATUS = 1;

  @Mock private DlmsConnectionManager connectionManager;

  @Mock private DlmsConnection dlmsConnection;

  private DlmsMessageListener dlmsMessageListener;

  @Mock private ClearMBusStatusRequestDto dto;

  @Mock private MessageMetadata messageMetadata;

  @Mock private GetResult getResult;

  @Mock private MethodResult methodResult;

  @Captor private ArgumentCaptor<AttributeAddress> attributeAddressArgumentCaptor;

  @Captor private ArgumentCaptor<SetParameter> setParameterArgumentCaptor;

  @Captor private ArgumentCaptor<MethodParameter> methodParameterArgumentCaptor;

  private ClearMBusStatusCommandExecutor executor;

  @BeforeEach
  void setup() {
    this.executor = new ClearMBusStatusCommandExecutor();
    this.dlmsMessageListener = new LoggingDlmsMessageListener(null, null);
  }

  @Test
  void shouldNotExecuteForProtocol_DSMR_4_2_2() {
    final DlmsDevice dlmsDevice_4_2_2 = new DlmsDevice("DSMR 4.2.2 device");
    dlmsDevice_4_2_2.setProtocol("DSMR", "4.2.2");

    assertThatExceptionOfType(NotSupportedByProtocolException.class)
        .isThrownBy(
            () -> {
              this.executor.execute(
                  this.connectionManager, dlmsDevice_4_2_2, this.dto, this.messageMetadata);
            });
  }

  @Test
  void shouldNotExecuteForProtocol_SMR_5_0_0() {
    final DlmsDevice dlmsDevice_5_0_0 = new DlmsDevice("SMR 5.0.0 device");
    dlmsDevice_5_0_0.setProtocol("SMR", "5.0.0");

    assertThatExceptionOfType(NotSupportedByProtocolException.class)
        .isThrownBy(
            () -> {
              this.executor.execute(
                  this.connectionManager, dlmsDevice_5_0_0, this.dto, this.messageMetadata);
            });
  }

  @Test
  void shouldExecuteForProtocol_SMR_5_1() throws ProtocolAdapterException, IOException {
    final DlmsDevice dlmsDevice_5_1 = new DlmsDevice("SMR 5.1 device");
    dlmsDevice_5_1.setProtocol("SMR", "5.1");

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.get(this.attributeAddressArgumentCaptor.capture()))
        .thenReturn(this.getResult);
    when(this.getResult.getResultData()).thenReturn(DataObject.newUInteger32Data(0L));
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS);
    when(this.methodResult.getResultCode()).thenReturn(MethodResultCode.SUCCESS);
    when(this.dlmsConnection.action(this.methodParameterArgumentCaptor.capture()))
        .thenReturn(this.methodResult);

    this.executor.execute(this.connectionManager, dlmsDevice_5_1, this.dto, this.messageMetadata);

    this.assertCurrentStatusAttributeAddresses(this.attributeAddressArgumentCaptor.getAllValues());
    this.assertClearStatus(this.setParameterArgumentCaptor.getAllValues());
  }

  private void assertClearStatus(final List<SetParameter> setParameters) {
    for (int i = 1; i <= 4; i++) {
      final SetParameter setParameter = setParameters.get(i - 1);
      assertThat(((Number) setParameter.getData().getValue()).longValue()).isEqualTo(0L);

      final AttributeAddress attributeAddress = setParameter.getAttributeAddress();
      assertThat(attributeAddress.getInstanceId().asDecimalString())
          .isEqualTo(OBIS_CODE_TEMPLATE_CLEAR_STATUS.replace('x', (char) (i + '0')));
      assertThat(attributeAddress.getClassId()).isEqualTo(CLASS_ID_CLEAR_STATUS);
    }
  }

  private void assertCurrentStatusAttributeAddresses(final List<AttributeAddress> attributes) {
    for (int i = 1; i <= 4; i++) {
      final AttributeAddress attributeAddress = attributes.get(i - 1);
      assertThat(attributeAddress.getInstanceId().asDecimalString())
          .isEqualTo(OBIS_CODE_TEMPLATE_READ_STATUS.replace('x', (char) (i + '0')));
      assertThat(attributeAddress.getClassId()).isEqualTo(CLASS_ID_READ_STATUS);
    }
  }
}
