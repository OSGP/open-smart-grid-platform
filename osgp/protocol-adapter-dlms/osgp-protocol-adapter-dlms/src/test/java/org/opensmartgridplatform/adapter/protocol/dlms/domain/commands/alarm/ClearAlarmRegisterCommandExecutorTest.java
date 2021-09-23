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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
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
import org.openmuc.jdlms.SetParameter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.LoggingDlmsMessageListener;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DataAttribute;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClearAlarmRegisterRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.OsgpResultTypeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class ClearAlarmRegisterCommandExecutorTest {

  private static final String OBIS_CODE_ALARM_REGISTER_1 = "0.0.97.98.0.255";
  private static final String OBIS_CODE_ALARM_REGISTER_2 = "0.0.97.98.1.255";

  private static final int CLASS_ID_CLEAR_ALARM_REGISTER = 1;

  private static final Long ALARM_CODE = 0L;

  @Mock private DlmsObjectConfigService dlmsObjectConfigService;

  @Mock private DlmsConnectionManager connectionManager;

  @Mock private DlmsConnection dlmsConnection;

  private DlmsMessageListener dlmsMessageListener;

  @Mock private ClearAlarmRegisterRequestDto dto;

  @Mock private MessageMetadata messageMetadata;

  @Captor private ArgumentCaptor<SetParameter> setParameterArgumentCaptor;

  private ClearAlarmRegisterCommandExecutor executor;

  @BeforeEach
  void setup() {
    this.executor = new ClearAlarmRegisterCommandExecutor(this.dlmsObjectConfigService);
    this.dlmsMessageListener = new LoggingDlmsMessageListener(null, null);
  }

  @Test
  void testAsBundleResponse() throws ProtocolAdapterException, IOException {
    final ActionResponseDto responseDto = this.executor.asBundleResponse(AccessResultCode.SUCCESS);
    assertThat(responseDto.getResult()).isEqualTo(OsgpResultTypeDto.OK);

    assertThrows(
        ProtocolAdapterException.class,
        () -> {
          this.executor.asBundleResponse(AccessResultCode.OTHER_REASON);
        });
  }

  @Test
  void shouldExecuteForProtocolDsmr422() throws ProtocolAdapterException, IOException {
    this.assertForOneRegister("DSMR", "4.2.2");
  }

  @Test
  void shouldExecuteForProtocolSmr500() throws ProtocolAdapterException, IOException {
    this.assertForOneRegister("SMR", "5.0.0");
  }

  @Test
  void shouldExecuteForProtocolSmr51() throws ProtocolAdapterException, IOException {
    this.assertForOneRegister("SMR", "5.1");
  }

  @Test
  void shouldExecuteForProtocolSmr52() throws ProtocolAdapterException, IOException {
    this.assertForTwoRegisters("SMR", "5.2");
  }

  @Test
  void connectionProblemAlarmRegister1() throws ProtocolAdapterException, IOException {
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenThrow(new IOException());

    this.assertExceptionAlarmRegister1(ConnectionException.class);
  }

  @Test
  void nullResultAlarmRegister1() throws ProtocolAdapterException, IOException {
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture())).thenReturn(null);

    this.assertExceptionAlarmRegister1(ProtocolAdapterException.class);
  }

  @Test
  void connectionProblemAlarmRegister2() throws ProtocolAdapterException, IOException {
    when(this.dlmsConnection.set(any(SetParameter.class)))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenThrow(new IOException());

    this.assertExceptionAlarmRegister2(ConnectionException.class);
  }

  @Test
  void nullResultAlarmRegister2() throws ProtocolAdapterException, IOException {
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(null);

    this.assertExceptionAlarmRegister2(ProtocolAdapterException.class);
  }

  @Test
  void failureRegister1AndResultAlarmRegister2() throws ProtocolAdapterException, IOException {
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.OTHER_REASON)
        .thenReturn(AccessResultCode.SUCCESS);

    final AccessResultCode accessResultCode = this.assertExceptionAlarmRegister1(null);
    assertThat(accessResultCode).isEqualTo(AccessResultCode.OTHER_REASON);
  }

  @Test
  void successRegister1AndResultAlarmRegister2() throws ProtocolAdapterException, IOException {
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(AccessResultCode.TEMPORARY_FAILURE);

    final AccessResultCode accessResultCode = this.assertExceptionAlarmRegister2(null);
    assertThat(accessResultCode).isEqualTo(AccessResultCode.TEMPORARY_FAILURE);
  }

  AccessResultCode assertExceptionAlarmRegister1(
      final Class<? extends Exception> expectedExceptionClass)
      throws ProtocolAdapterException, IOException {
    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.2 device");
    dlmsDevice.setProtocol("SMR", "5.2");

    when(this.dlmsObjectConfigService.getAttributeAddress(
            dlmsDevice, DlmsObjectType.ALARM_REGISTER_1, null))
        .thenReturn(
            new AttributeAddress(
                InterfaceClass.DATA.id(),
                OBIS_CODE_ALARM_REGISTER_1,
                DataAttribute.VALUE.attributeId()));

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);

    if (expectedExceptionClass != null) {
      assertThrows(
          expectedExceptionClass,
          () -> {
            this.executor.execute(
                this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);
          });
    } else {
      return this.executor.execute(
          this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);
    }
    return null;
  }

  AccessResultCode assertExceptionAlarmRegister2(
      final Class<? extends Exception> expectedExceptionClass)
      throws ProtocolAdapterException, IOException {
    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.2 device");
    dlmsDevice.setProtocol("SMR", "5.2");

    when(this.dlmsObjectConfigService.getAttributeAddress(
            dlmsDevice, DlmsObjectType.ALARM_REGISTER_1, null))
        .thenReturn(
            new AttributeAddress(
                InterfaceClass.DATA.id(),
                OBIS_CODE_ALARM_REGISTER_1,
                DataAttribute.VALUE.attributeId()));

    when(this.dlmsObjectConfigService.findAttributeAddress(
            dlmsDevice, DlmsObjectType.ALARM_REGISTER_2, null))
        .thenReturn(
            Optional.of(
                new AttributeAddress(
                    InterfaceClass.DATA.id(),
                    OBIS_CODE_ALARM_REGISTER_2,
                    DataAttribute.VALUE.attributeId())));

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);

    if (expectedExceptionClass != null) {
      assertThrows(
          expectedExceptionClass,
          () -> {
            this.executor.execute(
                this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);
          });
    } else {
      return this.executor.execute(
          this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);
    }
    return null;
  }

  void assertForOneRegister(final String protocol, final String protocolVersion)
      throws ProtocolAdapterException, IOException {
    final DlmsDevice dlmsDevice = new DlmsDevice(protocol + " " + protocolVersion + " device");
    dlmsDevice.setProtocol(protocol, protocolVersion);

    when(this.dlmsObjectConfigService.getAttributeAddress(
            dlmsDevice, DlmsObjectType.ALARM_REGISTER_1, null))
        .thenReturn(
            new AttributeAddress(
                InterfaceClass.DATA.id(),
                OBIS_CODE_ALARM_REGISTER_1,
                DataAttribute.VALUE.attributeId()));

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS);

    this.executor.execute(this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);

    assertThat(this.setParameterArgumentCaptor.getAllValues()).hasSize(1);
    final SetParameter setParameter = this.setParameterArgumentCaptor.getAllValues().get(0);
    assertThat(((Number) setParameter.getData().getValue()).longValue()).isEqualTo(ALARM_CODE);
    final AttributeAddress attributeAddress = setParameter.getAttributeAddress();
    assertThat(attributeAddress.getInstanceId().asDecimalString())
        .isEqualTo(OBIS_CODE_ALARM_REGISTER_1);
    assertThat(attributeAddress.getClassId()).isEqualTo(CLASS_ID_CLEAR_ALARM_REGISTER);
  }

  void assertForTwoRegisters(final String protocol, final String protocolVersion)
      throws ProtocolAdapterException, IOException {
    final DlmsDevice dlmsDevice = new DlmsDevice(protocol + " " + protocolVersion + " device");
    dlmsDevice.setProtocol(protocol, protocolVersion);

    when(this.dlmsObjectConfigService.getAttributeAddress(
            dlmsDevice, DlmsObjectType.ALARM_REGISTER_1, null))
        .thenReturn(
            new AttributeAddress(
                InterfaceClass.DATA.id(),
                OBIS_CODE_ALARM_REGISTER_1,
                DataAttribute.VALUE.attributeId()));

    when(this.dlmsObjectConfigService.findAttributeAddress(
            dlmsDevice, DlmsObjectType.ALARM_REGISTER_2, null))
        .thenReturn(
            Optional.of(
                new AttributeAddress(
                    InterfaceClass.DATA.id(),
                    OBIS_CODE_ALARM_REGISTER_2,
                    DataAttribute.VALUE.attributeId())));

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS);

    this.executor.execute(this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);

    assertThat(this.setParameterArgumentCaptor.getAllValues()).hasSize(2);
    SetParameter setParameter = this.setParameterArgumentCaptor.getAllValues().get(0);
    assertThat(((Number) setParameter.getData().getValue()).longValue()).isEqualTo(ALARM_CODE);
    final AttributeAddress attributeAddress = setParameter.getAttributeAddress();
    assertThat(attributeAddress.getInstanceId().asDecimalString())
        .isEqualTo(OBIS_CODE_ALARM_REGISTER_1);
    assertThat(attributeAddress.getClassId()).isEqualTo(CLASS_ID_CLEAR_ALARM_REGISTER);

    setParameter = this.setParameterArgumentCaptor.getAllValues().get(1);
    assertThat(((Number) setParameter.getData().getValue()).longValue()).isEqualTo(ALARM_CODE);
    final AttributeAddress attributeAddress2 = setParameter.getAttributeAddress();
    assertThat(attributeAddress2.getInstanceId().asDecimalString())
        .isEqualTo(OBIS_CODE_ALARM_REGISTER_2);
    assertThat(attributeAddress2.getClassId()).isEqualTo(CLASS_ID_CLEAR_ALARM_REGISTER);
  }
}
