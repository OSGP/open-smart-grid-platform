// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.LoggingDlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.RegisterAttribute;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ReadAlarmRegisterRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ReadAlarmRegisterCommandExecutorTest {

  private static final String OBIS_CODE_ALARM_REGISTER_1 = "0.0.97.98.0.255";
  private static final String OBIS_CODE_ALARM_REGISTER_2 = "0.0.97.98.1.255";
  private static final int CLASS_ID_READ_ALARM_REGISTER = InterfaceClass.REGISTER.id();

  @Mock private ObjectConfigServiceHelper objectConfigServiceHelper;

  @Mock private DlmsConnectionManager connectionManager;

  @Mock private DlmsConnection dlmsConnection;

  private DlmsMessageListener dlmsMessageListener;

  @Mock private ReadAlarmRegisterRequestDto dto;

  @Mock private MessageMetadata messageMetadata;

  @Captor private ArgumentCaptor<AttributeAddress> attributeAddressArgumentCaptor;
  @Mock private GetResult getResult;
  @Mock private AlarmHelperService alarmHelperService;
  @InjectMocks private ReadAlarmRegisterCommandExecutor executor;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(this.executor, "alarmHelperService", this.alarmHelperService);
    this.dlmsMessageListener = new LoggingDlmsMessageListener(null, null);
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
  void shouldExecuteForProtocolSmr52()
      throws ProtocolAdapterException, IOException, ObjectConfigException {
    this.assertForTwoRegisters("SMR", "5.2");
  }

  @Test
  void shouldExecuteForProtocolSmr55()
      throws ProtocolAdapterException, IOException, ObjectConfigException {
    this.assertForTwoRegisters("SMR", "5.5");
  }

  @Test
  void connectionProblemThrowsConnectionException() throws ProtocolAdapterException {
    when(this.connectionManager.getConnection()).thenThrow(new ConnectionException("error"));

    final DlmsDevice dlmsDevice = this.getDlmsDevice("SMR", " 5.2");
    this.mockAlarmCosemObject(
        dlmsDevice, OBIS_CODE_ALARM_REGISTER_1, DlmsObjectType.ALARM_REGISTER_1.name());

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    final Throwable actual =
        ThrowableAssert.catchThrowable(
            () ->
                this.executor.execute(
                    this.connectionManager, dlmsDevice, this.dto, this.messageMetadata));
    assertThat(actual).isInstanceOf(ConnectionException.class);
  }

  @Test
  void nullResultAlarmRegisterThrowsProtocolAdapterException() throws ProtocolAdapterException {
    final DlmsDevice dlmsDevice = this.getDlmsDevice("SMR", " 5.2");
    this.setupAlarmRegister1(dlmsDevice);
    final Throwable actual =
        ThrowableAssert.catchThrowable(
            () ->
                this.executor.execute(
                    this.connectionManager, dlmsDevice, this.dto, this.messageMetadata));
    assertThat(actual).isInstanceOf(ProtocolAdapterException.class);
  }

  @Test
  void accessResultCodeIsNotSuccessThrowsProtocolAdapterException()
      throws ProtocolAdapterException, IOException {
    final DlmsDevice dlmsDevice = this.getDlmsDevice("SMR", " 5.0");

    this.mockAlarmCosemObject(
        dlmsDevice, OBIS_CODE_ALARM_REGISTER_1, DlmsObjectType.ALARM_REGISTER_1.name());

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);

    when(this.dlmsConnection.get(this.attributeAddressArgumentCaptor.capture()))
        .thenReturn(this.getResult);
    when(this.getResult.getResultCode()).thenReturn(AccessResultCode.TEMPORARY_FAILURE);

    final Throwable actual =
        ThrowableAssert.catchThrowable(
            () ->
                this.executor.execute(
                    this.connectionManager, dlmsDevice, this.dto, this.messageMetadata));
    assertThat(actual).isInstanceOf(ProtocolAdapterException.class);
  }

  @Test
  void noResultThrowsProtocolAdapterException() throws ProtocolAdapterException, IOException {
    final DlmsDevice dlmsDevice = this.getDlmsDevice("SMR", " 5.0");
    this.mockAlarmCosemObject(
        dlmsDevice, OBIS_CODE_ALARM_REGISTER_1, DlmsObjectType.ALARM_REGISTER_1.name());

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);

    when(this.dlmsConnection.get(this.attributeAddressArgumentCaptor.capture()))
        .thenReturn(this.getResult);
    when(this.getResult.getResultData()).thenReturn(null);
    when(this.getResult.getResultCode()).thenReturn(AccessResultCode.SUCCESS);

    final Throwable actual =
        ThrowableAssert.catchThrowable(
            () ->
                this.executor.execute(
                    this.connectionManager, dlmsDevice, this.dto, this.messageMetadata));
    assertThat(actual).isInstanceOf(ProtocolAdapterException.class);
  }

  void setupAlarmRegister1(final DlmsDevice dlmsDevice) throws ProtocolAdapterException {

    this.mockAlarmCosemObject(
        dlmsDevice, OBIS_CODE_ALARM_REGISTER_1, DlmsObjectType.ALARM_REGISTER_1.name());

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
  }

  private void mockAlarmCosemObject(
      final DlmsDevice dlmsDevice, final String obisCode, final String dlmsObjectTypeName)
      throws ProtocolAdapterException {

    final AttributeAddress attributeAddress =
        new AttributeAddress(
            InterfaceClass.REGISTER.id(),
            new ObisCode(obisCode),
            RegisterAttribute.VALUE.attributeId());

    when(this.objectConfigServiceHelper.findOptionalDefaultAttributeAddress(
            Protocol.forDevice(dlmsDevice), DlmsObjectType.valueOf(dlmsObjectTypeName)))
        .thenReturn(Optional.of(attributeAddress));
  }

  void assertForOneRegister(final String protocol, final String protocolVersion)
      throws ProtocolAdapterException, IOException {
    final DlmsDevice dlmsDevice = new DlmsDevice(protocol + " " + protocolVersion + " device");
    dlmsDevice.setProtocol(protocol, protocolVersion);

    this.mockAlarmCosemObject(
        dlmsDevice, OBIS_CODE_ALARM_REGISTER_1, DlmsObjectType.ALARM_REGISTER_1.name());

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);

    when(this.dlmsConnection.get(this.attributeAddressArgumentCaptor.capture()))
        .thenReturn(this.getResult);
    when(this.getResult.getResultData()).thenReturn(DataObject.newUInteger32Data(0L));
    when(this.getResult.getResultCode()).thenReturn(AccessResultCode.SUCCESS);
    when(this.alarmHelperService.toAlarmTypes(DlmsObjectType.ALARM_REGISTER_1, 0L))
        .thenReturn(this.getAlarmRegister1());

    this.executor.execute(this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);

    this.assertValuesAttributeAddresses(this.attributeAddressArgumentCaptor.getAllValues());
    verify(this.dlmsConnection, never()).set(any(SetParameter.class));
    verify(this.dlmsConnection, never()).action(any(MethodParameter.class));
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

    when(this.dlmsConnection.get(this.attributeAddressArgumentCaptor.capture()))
        .thenReturn(this.getResult);
    when(this.getResult.getResultData()).thenReturn(DataObject.newUInteger32Data(0L));
    when(this.getResult.getResultCode()).thenReturn(AccessResultCode.SUCCESS);
    when(this.alarmHelperService.toAlarmTypes(DlmsObjectType.ALARM_REGISTER_1, 0L))
        .thenReturn(this.getAlarmRegister1());
    when(this.alarmHelperService.toAlarmTypes(DlmsObjectType.ALARM_REGISTER_2, 0L))
        .thenReturn(this.getAlarmRegister2());

    this.executor.execute(this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);

    this.assertValuesAttributeAddresses(this.attributeAddressArgumentCaptor.getAllValues());
    verify(this.dlmsConnection, never()).set(any(SetParameter.class));
    verify(this.dlmsConnection, never()).action(any(MethodParameter.class));
  }

  private void assertValuesAttributeAddresses(final List<AttributeAddress> attributes) {
    final List<String> obisCodeAlarmRegisterList = new ArrayList<>();
    obisCodeAlarmRegisterList.add(OBIS_CODE_ALARM_REGISTER_1);
    obisCodeAlarmRegisterList.add(OBIS_CODE_ALARM_REGISTER_2);
    for (int i = 0; i < attributes.size(); i++) {
      final AttributeAddress attributeAddress = attributes.get(i);
      assertThat(attributeAddress.getInstanceId().asDecimalString())
          .isEqualTo(obisCodeAlarmRegisterList.get(i));
      assertThat(attributeAddress.getClassId()).isEqualTo(CLASS_ID_READ_ALARM_REGISTER);
    }
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
    return alarmTypesRegister2;
  }

  private DlmsDevice getDlmsDevice(final String protocol, final String protocolVersion) {
    return new DlmsDevice(protocol + " " + protocolVersion + " device");
  }
}
