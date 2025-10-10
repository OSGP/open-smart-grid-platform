// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.LoggingDlmsMessageListener;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmRegisterResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;
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

  @Mock private ReadAlarmRegisterCommandExecutor readAlarmRegisterCommandExecutor;

  @Mock private ObjectConfigServiceHelper objectConfigServiceHelper;

  @Mock private DlmsConnectionManager connectionManager;

  @Mock private DlmsConnection dlmsConnection;

  private DlmsMessageListener dlmsMessageListener;

  @Mock private ClearAlarmRegisterRequestDto dto;

  @Mock private MessageMetadata messageMetadata;

  @Captor private ArgumentCaptor<SetParameter> setParameterArgumentCaptor;

  private ClearAlarmRegisterCommandExecutor executor;

  @BeforeEach
  void setup() {
    this.executor =
        new ClearAlarmRegisterCommandExecutor(
            this.readAlarmRegisterCommandExecutor, this.objectConfigServiceHelper);
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
        ThrowableAssert.catchThrowable(
            () -> this.executor.asBundleResponse(AccessResultCode.OTHER_REASON));
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
  void connectionProblemAlarmRegister1() throws IOException, ProtocolAdapterException {
    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.2 device");

    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenThrow(new IOException());
    this.setupAlarmRegister1(dlmsDevice);

    final Throwable actual =
        ThrowableAssert.catchThrowable(
            () ->
                this.executor.execute(
                    this.connectionManager, dlmsDevice, this.dto, this.messageMetadata));

    assertThat(actual).isInstanceOf(ConnectionException.class);
  }

  @Test
  void nullResultAlarmRegister1() throws IOException, ProtocolAdapterException {
    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.2 device");

    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture())).thenReturn(null);
    this.setupAlarmRegister1(dlmsDevice);

    final Throwable actual =
        ThrowableAssert.catchThrowable(
            () ->
                this.executor.execute(
                    this.connectionManager, dlmsDevice, this.dto, this.messageMetadata));

    assertThat(actual).isInstanceOf(ProtocolAdapterException.class);
  }

  @Test
  void connectionProblemAlarmRegister2() throws IOException, ProtocolAdapterException {
    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.2 device");

    when(this.dlmsConnection.set(any(SetParameter.class)))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenThrow(new IOException());
    this.setupAlarmRegister2(dlmsDevice);

    final Throwable actual =
        ThrowableAssert.catchThrowable(
            () ->
                this.executor.execute(
                    this.connectionManager, dlmsDevice, this.dto, this.messageMetadata));

    assertThat(actual).isInstanceOf(ConnectionException.class);
  }

  @Test
  void nullResultAlarmRegister2() throws IOException, ProtocolAdapterException {
    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.2 device");

    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(null);
    this.setupAlarmRegister2(dlmsDevice);

    final Throwable actual =
        ThrowableAssert.catchThrowable(
            () ->
                this.executor.execute(
                    this.connectionManager, dlmsDevice, this.dto, this.messageMetadata));

    assertThat(actual).isInstanceOf(ProtocolAdapterException.class);
  }

  @Test
  void failureRegister1AndResultAlarmRegister2() throws ProtocolAdapterException, IOException {
    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.2 device");

    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.OTHER_REASON)
        .thenReturn(AccessResultCode.SUCCESS);
    this.setupAlarmRegister1(dlmsDevice);

    final AccessResultCode accessResultCode =
        this.executor.execute(this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);

    assertThat(accessResultCode).isEqualTo(AccessResultCode.OTHER_REASON);
  }

  @Test
  void successRegister1AndResultAlarmRegister2() throws ProtocolAdapterException, IOException {
    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.2 device");

    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(AccessResultCode.TEMPORARY_FAILURE);
    this.setupAlarmRegister2(dlmsDevice);

    final AccessResultCode accessResultCode =
        this.executor.execute(this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);

    assertThat(accessResultCode).isEqualTo(AccessResultCode.TEMPORARY_FAILURE);
  }

  @Test
  void resultAlarmRegister2() throws ProtocolAdapterException, IOException {
    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.2 device");

    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(AccessResultCode.SUCCESS);
    this.setupAlarmRegister2(dlmsDevice);

    final AccessResultCode accessResultCode =
        this.executor.execute(this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);

    assertThat(accessResultCode).isEqualTo(AccessResultCode.SUCCESS);
  }

  @Test
  void connectionProblemAlarmRegister3() throws IOException, ProtocolAdapterException {
    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.5 device");

    when(this.dlmsConnection.set(any(SetParameter.class)))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(AccessResultCode.SUCCESS)
        .thenThrow(new IOException());
    this.setupAlarmRegister3(dlmsDevice);

    final Throwable actual =
        ThrowableAssert.catchThrowable(
            () ->
                this.executor.execute(
                    this.connectionManager, dlmsDevice, this.dto, this.messageMetadata));

    assertThat(actual).isInstanceOf(ConnectionException.class);
  }

  @Test
  void nullResultAlarmRegister3() throws IOException, ProtocolAdapterException {
    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.5 device");

    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(null);
    this.setupAlarmRegister3(dlmsDevice);

    final Throwable actual =
        ThrowableAssert.catchThrowable(
            () ->
                this.executor.execute(
                    this.connectionManager, dlmsDevice, this.dto, this.messageMetadata));

    assertThat(actual).isInstanceOf(ProtocolAdapterException.class);
  }

  @Test
  void failureRegister2AndResultAlarmRegister3() throws ProtocolAdapterException, IOException {
    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.5 device");

    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(AccessResultCode.OTHER_REASON)
        .thenReturn(AccessResultCode.SUCCESS);
    this.setupAlarmRegister2(dlmsDevice);

    final AccessResultCode accessResultCode =
        this.executor.execute(this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);

    assertThat(accessResultCode).isEqualTo(AccessResultCode.OTHER_REASON);
  }

  @Test
  void successRegister1AndResultAlarmRegister3() throws ProtocolAdapterException, IOException {
    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.5 device");

    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(AccessResultCode.TEMPORARY_FAILURE);
    this.setupAlarmRegister3(dlmsDevice);

    final AccessResultCode accessResultCode =
        this.executor.execute(this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);

    assertThat(accessResultCode).isEqualTo(AccessResultCode.TEMPORARY_FAILURE);
  }

  @Test
  void resultAlarmRegister3() throws ProtocolAdapterException, IOException {
    final DlmsDevice dlmsDevice = new DlmsDevice("SMR 5.5 device");

    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(AccessResultCode.SUCCESS)
        .thenReturn(AccessResultCode.SUCCESS);
    this.setupAlarmRegister2(dlmsDevice);

    final AccessResultCode accessResultCode =
        this.executor.execute(this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);

    assertThat(accessResultCode).isEqualTo(AccessResultCode.SUCCESS);
  }

  void setupAlarmRegister1(final DlmsDevice dlmsDevice) throws ProtocolAdapterException {

    this.mockAlarmCosemObject(
        dlmsDevice, OBIS_CODE_ALARM_REGISTER_1, DlmsObjectType.ALARM_REGISTER_1.name());

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.readAlarmRegisterCommandExecutor.execute(
            this.connectionManager, dlmsDevice, null, null))
        .thenReturn(new AlarmRegisterResponseDto(this.getAlarmRegister1()));
  }

  void setupAlarmRegister2(final DlmsDevice dlmsDevice) throws ProtocolAdapterException {

    this.mockAlarmCosemObject(
        dlmsDevice, OBIS_CODE_ALARM_REGISTER_1, DlmsObjectType.ALARM_REGISTER_1.name());
    this.mockAlarmCosemObject(
        dlmsDevice, OBIS_CODE_ALARM_REGISTER_2, DlmsObjectType.ALARM_REGISTER_2.name());

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.readAlarmRegisterCommandExecutor.execute(
            this.connectionManager, dlmsDevice, null, null))
        .thenReturn(new AlarmRegisterResponseDto(this.getAlarmRegister2()));
  }

  private void mockAlarmCosemObject(
      final DlmsDevice dlmsDevice, final String obisCode, final String dlmsObjectTypeName) {

    final ObisCode newObisCode = new ObisCode(obisCode);

    final AttributeAddress attributeAddress = Mockito.mock(AttributeAddress.class);
    when(attributeAddress.getClassId()).thenReturn(1);
    when(attributeAddress.getId()).thenReturn(2);
    when(attributeAddress.getInstanceId()).thenReturn(newObisCode);

    when(this.objectConfigServiceHelper.findOptionalDefaultAttributeAddress(
            Protocol.forDevice(dlmsDevice), DlmsObjectType.valueOf(dlmsObjectTypeName)))
        .thenReturn(Optional.of(attributeAddress));
  }

  void setupAlarmRegister3(final DlmsDevice dlmsDevice) throws ProtocolAdapterException {
    dlmsDevice.setProtocol("SMR", "5.5");

    this.mockAlarmCosemObject(
        dlmsDevice,
        OBIS_CODE_ALARM_REGISTER_1,
        org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ALARM_REGISTER_1.name());
    this.mockAlarmCosemObject(
        dlmsDevice,
        OBIS_CODE_ALARM_REGISTER_2,
        org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ALARM_REGISTER_2.name());
    this.mockAlarmCosemObject(
        dlmsDevice,
        OBIS_CODE_ALARM_REGISTER_3,
        org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.ALARM_REGISTER_3.name());

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.readAlarmRegisterCommandExecutor.execute(
            this.connectionManager, dlmsDevice, null, null))
        .thenReturn(new AlarmRegisterResponseDto(this.getAlarmRegister3()));
  }

  void assertForOneRegister(final String protocol, final String protocolVersion)
      throws ProtocolAdapterException, IOException {
    final DlmsDevice dlmsDevice = new DlmsDevice(protocol + " " + protocolVersion + " device");
    dlmsDevice.setProtocol(protocol, protocolVersion);

    this.mockAlarmCosemObject(
        dlmsDevice, OBIS_CODE_ALARM_REGISTER_1, DlmsObjectType.ALARM_REGISTER_1.name());

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS);
    when(this.readAlarmRegisterCommandExecutor.execute(
            this.connectionManager, dlmsDevice, null, null))
        .thenReturn(new AlarmRegisterResponseDto(this.getAlarmRegister1()));

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

    this.mockAlarmCosemObject(
        dlmsDevice, OBIS_CODE_ALARM_REGISTER_1, DlmsObjectType.ALARM_REGISTER_1.name());

    this.mockAlarmCosemObject(
        dlmsDevice, OBIS_CODE_ALARM_REGISTER_2, DlmsObjectType.ALARM_REGISTER_2.name());

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS);
    when(this.readAlarmRegisterCommandExecutor.execute(
            this.connectionManager, dlmsDevice, null, null))
        .thenReturn(new AlarmRegisterResponseDto(this.getAlarmRegister1And2()));

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

    this.mockAlarmCosemObject(
        dlmsDevice, OBIS_CODE_ALARM_REGISTER_1, DlmsObjectType.ALARM_REGISTER_1.name());

    this.mockAlarmCosemObject(
        dlmsDevice, OBIS_CODE_ALARM_REGISTER_2, DlmsObjectType.ALARM_REGISTER_2.name());

    this.mockAlarmCosemObject(
        dlmsDevice, OBIS_CODE_ALARM_REGISTER_3, DlmsObjectType.ALARM_REGISTER_3.name());

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS);
    when(this.readAlarmRegisterCommandExecutor.execute(
            this.connectionManager, dlmsDevice, null, null))
        .thenReturn(new AlarmRegisterResponseDto(this.getAllAlarmRegisters()));

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

  private Set<AlarmTypeDto> getAlarmRegister1() {
    final Set<AlarmTypeDto> alarmTypesRegister1 = new HashSet<>();
    alarmTypesRegister1.add(AlarmTypeDto.CLOCK_INVALID);
    alarmTypesRegister1.add(AlarmTypeDto.PROGRAM_MEMORY_ERROR);
    alarmTypesRegister1.add(AlarmTypeDto.WATCHDOG_ERROR);
    alarmTypesRegister1.add(AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_1);
    alarmTypesRegister1.add(AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_1);
    alarmTypesRegister1.add(AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1);
    alarmTypesRegister1.add(AlarmTypeDto.PHASE_OUTAGE_TEST_INDICATION);
    return alarmTypesRegister1;
  }

  private Set<AlarmTypeDto> getAlarmRegister2() {
    final Set<AlarmTypeDto> alarmTypesRegister2 = new HashSet<>();
    alarmTypesRegister2.add(AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L1);
    alarmTypesRegister2.add(AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L2);
    alarmTypesRegister2.add(AlarmTypeDto.VOLTAGE_SAG_IN_PHASE_DETECTED_L3);
    alarmTypesRegister2.add(AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L1);
    alarmTypesRegister2.add(AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L2);
    alarmTypesRegister2.add(AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L3);
    alarmTypesRegister2.add(AlarmTypeDto.THD_OVERLIMIT_IN_PHASE_L1);
    alarmTypesRegister2.add(AlarmTypeDto.THD_OVERLIMIT_IN_PHASE_L2);
    alarmTypesRegister2.add(AlarmTypeDto.THD_OVERLIMIT_IN_PHASE_L3);
    alarmTypesRegister2.add(AlarmTypeDto.THD_LONG_OVERLIMIT_IN_PHASE_L1);
    alarmTypesRegister2.add(AlarmTypeDto.THD_LONG_OVERLIMIT_IN_PHASE_L2);
    alarmTypesRegister2.add(AlarmTypeDto.THD_LONG_OVERLIMIT_IN_PHASE_L3);

    return alarmTypesRegister2;
  }

  private Set<AlarmTypeDto> getAlarmRegister3() {
    final Set<AlarmTypeDto> alarmTypesRegister3 = new HashSet<>();
    alarmTypesRegister3.add(AlarmTypeDto.LAST_GASP);
    alarmTypesRegister3.add(AlarmTypeDto.LAST_GASP_TEST);

    return alarmTypesRegister3;
  }

  private Set<AlarmTypeDto> getAlarmRegister1And2() {
    final Set<AlarmTypeDto> alarmRegister1And2 = new HashSet<>(this.getAlarmRegister1());
    alarmRegister1And2.addAll(this.getAlarmRegister2());
    return alarmRegister1And2;
  }

  private Set<AlarmTypeDto> getAllAlarmRegisters() {
    final Set<AlarmTypeDto> allAlarmRegisters = new HashSet<>(this.getAlarmRegister1());
    allAlarmRegisters.addAll(this.getAlarmRegister2());
    allAlarmRegisters.addAll(this.getAlarmRegister3());
    return allAlarmRegisters;
  }
}
