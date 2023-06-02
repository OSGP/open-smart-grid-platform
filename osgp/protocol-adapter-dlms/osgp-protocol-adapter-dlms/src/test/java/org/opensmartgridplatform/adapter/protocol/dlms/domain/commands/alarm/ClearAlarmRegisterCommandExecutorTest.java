//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
  private static final String OBIS_CODE_ALARM_REGISTER_3 = "0.0.97.98.2.255";

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
  void testAsBundleResponse() throws ProtocolAdapterException {
    final ActionResponseDto responseDto = this.executor.asBundleResponse(AccessResultCode.SUCCESS);
    assertThat(responseDto.getResult()).isEqualTo(OsgpResultTypeDto.OK);
  }

  @Test
  void testAsBundleResponseOtherReason() {
    final Throwable actual =
        catchThrowable(() -> this.executor.asBundleResponse(AccessResultCode.OTHER_REASON));
    assertThat(actual).isInstanceOf(ProtocolAdapterException.class);
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
  void shouldExecuteForProtocolSmr55() throws ProtocolAdapterException, IOException {
    this.assertForThreeRegisters("SMR", "5.5");
  }

  @Test
  void connectionProblemAlarmRegister1() throws ProtocolAdapterException, IOException {
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenThrow(new IOException());

    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.2 device");
    this.setupAlarmRegister1(dlmsDevice);
    final Throwable actual =
        catchThrowable(
            () ->
                this.executor.execute(
                    this.connectionManager, dlmsDevice, this.dto, this.messageMetadata));
    assertThat(actual).isInstanceOf(ConnectionException.class);
  }

  @Test
  void nullResultAlarmRegister1() throws ProtocolAdapterException, IOException {
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture())).thenReturn(null);

    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.2 device");
    this.setupAlarmRegister1(dlmsDevice);
    final Throwable actual =
        catchThrowable(
            () ->
                this.executor.execute(
                    this.connectionManager, dlmsDevice, this.dto, this.messageMetadata));
    assertThat(actual).isInstanceOf(ProtocolAdapterException.class);
  }

  @Test
  void connectionProblemAlarmRegister2() throws ProtocolAdapterException, IOException {
    when(this.dlmsConnection.set(any(SetParameter.class)))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenThrow(new IOException());

    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.2 device");
    this.setupAlarmRegister2(dlmsDevice);
    final Throwable actual =
        catchThrowable(
            () ->
                this.executor.execute(
                    this.connectionManager, dlmsDevice, this.dto, this.messageMetadata));
    assertThat(actual).isInstanceOf(ConnectionException.class);
  }

  @Test
  void nullResultAlarmRegister2() throws ProtocolAdapterException, IOException {
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(null);

    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.2 device");
    this.setupAlarmRegister2(dlmsDevice);
    final Throwable actual =
        catchThrowable(
            () ->
                this.executor.execute(
                    this.connectionManager, dlmsDevice, this.dto, this.messageMetadata));
    assertThat(actual).isInstanceOf(ProtocolAdapterException.class);
  }

  @Test
  void failureRegister1AndResultAlarmRegister2() throws ProtocolAdapterException, IOException {
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.OTHER_REASON)
        .thenReturn(AccessResultCode.SUCCESS);

    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.2 device");
    this.setupAlarmRegister1(dlmsDevice);
    final AccessResultCode accessResultCode =
        this.executor.execute(this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);
    assertThat(accessResultCode).isEqualTo(AccessResultCode.OTHER_REASON);
    verify(this.dlmsObjectConfigService, times(1))
        .findAttributeAddress(eq(dlmsDevice), any(), any());
  }

  @Test
  void successRegister1AndResultAlarmRegister2() throws ProtocolAdapterException, IOException {
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(AccessResultCode.TEMPORARY_FAILURE);

    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.2 device");
    this.setupAlarmRegister2(dlmsDevice);
    final AccessResultCode accessResultCode =
        this.executor.execute(this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);
    assertThat(accessResultCode).isEqualTo(AccessResultCode.TEMPORARY_FAILURE);
  }

  @Test
  void resultAlarmRegister2() throws ProtocolAdapterException, IOException {
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(AccessResultCode.SUCCESS);

    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.2 device");
    this.setupAlarmRegister2(dlmsDevice);
    final AccessResultCode accessResultCode =
        this.executor.execute(this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);
    assertThat(accessResultCode).isEqualTo(AccessResultCode.SUCCESS);
  }

  @Test
  void connectionProblemAlarmRegister3() throws ProtocolAdapterException, IOException {
    when(this.dlmsConnection.set(any(SetParameter.class)))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(AccessResultCode.SUCCESS)
        .thenThrow(new IOException());

    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.5 device");
    this.setupAlarmRegister3(dlmsDevice);
    final Throwable actual =
        catchThrowable(
            () ->
                this.executor.execute(
                    this.connectionManager, dlmsDevice, this.dto, this.messageMetadata));
    assertThat(actual).isInstanceOf(ConnectionException.class);
  }

  @Test
  void nullResultAlarmRegister3() throws ProtocolAdapterException, IOException {
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(null);

    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.5 device");
    this.setupAlarmRegister3(dlmsDevice);
    final Throwable actual =
        catchThrowable(
            () ->
                this.executor.execute(
                    this.connectionManager, dlmsDevice, this.dto, this.messageMetadata));
    assertThat(actual).isInstanceOf(ProtocolAdapterException.class);
  }

  @Test
  void failureRegister2AndResultAlarmRegister3() throws ProtocolAdapterException, IOException {
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(AccessResultCode.OTHER_REASON)
        .thenReturn(AccessResultCode.SUCCESS);

    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.5 device");
    this.setupAlarmRegister2(dlmsDevice);
    final AccessResultCode accessResultCode =
        this.executor.execute(this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);
    assertThat(accessResultCode).isEqualTo(AccessResultCode.OTHER_REASON);
    verify(this.dlmsObjectConfigService, times(2))
        .findAttributeAddress(eq(dlmsDevice), any(), any());
  }

  @Test
  void successRegister1AndResultAlarmRegister3() throws ProtocolAdapterException, IOException {
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(AccessResultCode.TEMPORARY_FAILURE);

    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.5 device");
    this.setupAlarmRegister3(dlmsDevice);
    final AccessResultCode accessResultCode =
        this.executor.execute(this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);
    assertThat(accessResultCode).isEqualTo(AccessResultCode.TEMPORARY_FAILURE);
  }

  @Test
  void resultAlarmRegister3() throws ProtocolAdapterException, IOException {
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(AccessResultCode.SUCCESS);

    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.5 device");
    this.setupAlarmRegister2(dlmsDevice);
    final AccessResultCode accessResultCode =
        this.executor.execute(this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);
    assertThat(accessResultCode).isEqualTo(AccessResultCode.SUCCESS);
  }

  void setupAlarmRegister1(final DlmsDevice dlmsDevice)
      throws ProtocolAdapterException, IOException {

    dlmsDevice.setProtocol("SMR", "5.2");

    when(this.dlmsObjectConfigService.findAttributeAddress(
            dlmsDevice, DlmsObjectType.ALARM_REGISTER_1, null))
        .thenReturn(
            Optional.of(
                new AttributeAddress(
                    InterfaceClass.DATA.id(),
                    OBIS_CODE_ALARM_REGISTER_1,
                    DataAttribute.VALUE.attributeId())));

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
  }

  void setupAlarmRegister2(final DlmsDevice dlmsDevice)
      throws ProtocolAdapterException, IOException {
    dlmsDevice.setProtocol("SMR", "5.2");

    when(this.dlmsObjectConfigService.findAttributeAddress(
            dlmsDevice, DlmsObjectType.ALARM_REGISTER_1, null))
        .thenReturn(
            Optional.of(
                new AttributeAddress(
                    InterfaceClass.DATA.id(),
                    OBIS_CODE_ALARM_REGISTER_1,
                    DataAttribute.VALUE.attributeId())));

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
  }

  void setupAlarmRegister3(final DlmsDevice dlmsDevice)
      throws ProtocolAdapterException, IOException {
    dlmsDevice.setProtocol("SMR", "5.5");

    when(this.dlmsObjectConfigService.findAttributeAddress(
            dlmsDevice, DlmsObjectType.ALARM_REGISTER_1, null))
        .thenReturn(
            Optional.of(
                new AttributeAddress(
                    InterfaceClass.DATA.id(),
                    OBIS_CODE_ALARM_REGISTER_1,
                    DataAttribute.VALUE.attributeId())));

    when(this.dlmsObjectConfigService.findAttributeAddress(
            dlmsDevice, DlmsObjectType.ALARM_REGISTER_2, null))
        .thenReturn(
            Optional.of(
                new AttributeAddress(
                    InterfaceClass.DATA.id(),
                    OBIS_CODE_ALARM_REGISTER_2,
                    DataAttribute.VALUE.attributeId())));

    when(this.dlmsObjectConfigService.findAttributeAddress(
            dlmsDevice, DlmsObjectType.ALARM_REGISTER_3, null))
        .thenReturn(
            Optional.of(
                new AttributeAddress(
                    InterfaceClass.DATA.id(),
                    OBIS_CODE_ALARM_REGISTER_3,
                    DataAttribute.VALUE.attributeId())));

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
  }

  void assertForOneRegister(final String protocol, final String protocolVersion)
      throws ProtocolAdapterException, IOException {
    final DlmsDevice dlmsDevice = new DlmsDevice(protocol + " " + protocolVersion + " device");
    dlmsDevice.setProtocol(protocol, protocolVersion);

    when(this.dlmsObjectConfigService.findAttributeAddress(
            dlmsDevice, DlmsObjectType.ALARM_REGISTER_1, null))
        .thenReturn(
            Optional.of(
                new AttributeAddress(
                    InterfaceClass.DATA.id(),
                    OBIS_CODE_ALARM_REGISTER_1,
                    DataAttribute.VALUE.attributeId())));

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

    when(this.dlmsObjectConfigService.findAttributeAddress(
            dlmsDevice, DlmsObjectType.ALARM_REGISTER_1, null))
        .thenReturn(
            Optional.of(
                new AttributeAddress(
                    InterfaceClass.DATA.id(),
                    OBIS_CODE_ALARM_REGISTER_1,
                    DataAttribute.VALUE.attributeId())));

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

  void assertForThreeRegisters(final String protocol, final String protocolVersion)
      throws ProtocolAdapterException, IOException {
    final DlmsDevice dlmsDevice = new DlmsDevice(protocol + " " + protocolVersion + " device");
    dlmsDevice.setProtocol(protocol, protocolVersion);

    when(this.dlmsObjectConfigService.findAttributeAddress(
            dlmsDevice, DlmsObjectType.ALARM_REGISTER_1, null))
        .thenReturn(
            Optional.of(
                new AttributeAddress(
                    InterfaceClass.DATA.id(),
                    OBIS_CODE_ALARM_REGISTER_1,
                    DataAttribute.VALUE.attributeId())));

    when(this.dlmsObjectConfigService.findAttributeAddress(
            dlmsDevice, DlmsObjectType.ALARM_REGISTER_2, null))
        .thenReturn(
            Optional.of(
                new AttributeAddress(
                    InterfaceClass.DATA.id(),
                    OBIS_CODE_ALARM_REGISTER_2,
                    DataAttribute.VALUE.attributeId())));

    when(this.dlmsObjectConfigService.findAttributeAddress(
            dlmsDevice, DlmsObjectType.ALARM_REGISTER_3, null))
        .thenReturn(
            Optional.of(
                new AttributeAddress(
                    InterfaceClass.DATA.id(),
                    OBIS_CODE_ALARM_REGISTER_3,
                    DataAttribute.VALUE.attributeId())));

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS);

    this.executor.execute(this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);

    assertThat(this.setParameterArgumentCaptor.getAllValues()).hasSize(3);
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

    setParameter = this.setParameterArgumentCaptor.getAllValues().get(2);
    assertThat(((Number) setParameter.getData().getValue()).longValue()).isEqualTo(ALARM_CODE);
    final AttributeAddress attributeAddress3 = setParameter.getAttributeAddress();
    assertThat(attributeAddress3.getInstanceId().asDecimalString())
        .isEqualTo(OBIS_CODE_ALARM_REGISTER_3);
    assertThat(attributeAddress3.getClassId()).isEqualTo(CLASS_ID_CLEAR_ALARM_REGISTER);
  }
}
