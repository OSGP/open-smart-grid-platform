// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.LoggingDlmsMessageListener;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DataAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ExtendedRegisterAttribute;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClearMBusStatusOnAllChannelsRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class ClearMBusStatusOnAllChannelsCommandExecutorTest {

  private static final String OBIS_CODE_TEMPLATE_READ_STATUS = "0.<c>.24.2.6.255";
  private static final int CLASS_ID_READ_STATUS = 4;

  private static final String OBIS_CODE_TEMPLATE_CLEAR_STATUS = "0.<c>.94.31.10.255";
  private static final int CLASS_ID_CLEAR_STATUS = 1;

  private static final String OBIS_CODE_TEMPLATE_MBUS_CLIENT_SETUP = "0.<c>.24.1.0.255";
  private static final int CLASS_ID_MBUS_CLIENT = 72;

  private static final Long STATUS_MASK = 123456L;

  @Mock private DlmsObjectConfigService dlmsObjectConfigService;

  @Mock private DlmsConnectionManager connectionManager;

  @Mock private DlmsConnection dlmsConnection;

  private DlmsMessageListener dlmsMessageListener;

  @Mock private ClearMBusStatusOnAllChannelsRequestDto dto;

  @Mock private MessageMetadata messageMetadata;

  @Mock private GetResult getResult;

  @Mock private MethodResult methodResult;

  @Captor private ArgumentCaptor<AttributeAddress> attributeAddressArgumentCaptor;

  @Captor private ArgumentCaptor<SetParameter> setParameterArgumentCaptor;

  @Captor private ArgumentCaptor<MethodParameter> methodParameterArgumentCaptor;

  private ClearMBusStatusOnAllChannelsCommandExecutor executor;

  @BeforeEach
  void setup() {
    this.executor = new ClearMBusStatusOnAllChannelsCommandExecutor(this.dlmsObjectConfigService);
    this.dlmsMessageListener = new LoggingDlmsMessageListener(null, null);
  }

  @Test
  void testExecuteObjectNotFound() throws ProtocolAdapterException {
    when(this.dlmsObjectConfigService.getAttributeAddress(any(), any(), any()))
        .thenThrow(new ProtocolAdapterException("Object not found"));

    final DlmsDevice dlmsDevice = new DlmsDevice();
    dlmsDevice.setProtocol("SMR", "5.0.0");

    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () -> {
              this.executor.execute(
                  this.connectionManager, dlmsDevice, this.dto, this.messageMetadata);
            });
  }

  @Test
  void shouldSkipIfStatusIsZero() throws ProtocolAdapterException, IOException {
    final DlmsDevice dlmsDevice_5_1 = new DlmsDevice("SMR 5.1 device");
    dlmsDevice_5_1.setProtocol("SMR", "5.1");

    for (int channel = 1; channel <= 4; channel++) {
      when(this.dlmsObjectConfigService.getAttributeAddress(
              dlmsDevice_5_1, DlmsObjectType.READ_MBUS_STATUS, channel))
          .thenReturn(
              new AttributeAddress(
                  InterfaceClass.EXTENDED_REGISTER.id(),
                  OBIS_CODE_TEMPLATE_READ_STATUS.replaceAll("<c>", Integer.toString(channel)),
                  ExtendedRegisterAttribute.VALUE.attributeId()));

      when(this.dlmsObjectConfigService.getAttributeAddress(
              dlmsDevice_5_1, DlmsObjectType.CLEAR_MBUS_STATUS, channel))
          .thenReturn(
              new AttributeAddress(
                  InterfaceClass.DATA.id(),
                  OBIS_CODE_TEMPLATE_CLEAR_STATUS.replaceAll("<c>", Integer.toString(channel)),
                  DataAttribute.VALUE.attributeId()));

      when(this.dlmsObjectConfigService.getAttributeAddress(
              dlmsDevice_5_1, DlmsObjectType.CLIENT_SETUP_MBUS, channel))
          .thenReturn(
              new AttributeAddress(
                  InterfaceClass.MBUS_CLIENT.id(),
                  OBIS_CODE_TEMPLATE_MBUS_CLIENT_SETUP.replaceAll("<c>", Integer.toString(channel)),
                  DataAttribute.VALUE.attributeId()));
    }

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.get(this.attributeAddressArgumentCaptor.capture()))
        .thenReturn(this.getResult);
    when(this.getResult.getResultData()).thenReturn(DataObject.newUInteger32Data(0L));

    this.executor.execute(this.connectionManager, dlmsDevice_5_1, this.dto, this.messageMetadata);

    this.assertCurrentStatusAttributeAddresses(this.attributeAddressArgumentCaptor.getAllValues());
    verify(this.dlmsConnection, never()).set(any(SetParameter.class));
    verify(this.dlmsConnection, never()).action(any(MethodParameter.class));
  }

  @Test
  void shouldExecuteForProtocol_SMR_5_1() throws ProtocolAdapterException, IOException {
    final DlmsDevice dlmsDevice_5_1 = new DlmsDevice("SMR 5.1 device");
    dlmsDevice_5_1.setProtocol("SMR", "5.1");

    for (int channel = 1; channel <= 4; channel++) {
      when(this.dlmsObjectConfigService.getAttributeAddress(
              dlmsDevice_5_1, DlmsObjectType.READ_MBUS_STATUS, channel))
          .thenReturn(
              new AttributeAddress(
                  InterfaceClass.EXTENDED_REGISTER.id(),
                  OBIS_CODE_TEMPLATE_READ_STATUS.replaceAll("<c>", Integer.toString(channel)),
                  ExtendedRegisterAttribute.VALUE.attributeId()));

      when(this.dlmsObjectConfigService.getAttributeAddress(
              dlmsDevice_5_1, DlmsObjectType.CLEAR_MBUS_STATUS, channel))
          .thenReturn(
              new AttributeAddress(
                  InterfaceClass.DATA.id(),
                  OBIS_CODE_TEMPLATE_CLEAR_STATUS.replaceAll("<c>", Integer.toString(channel)),
                  DataAttribute.VALUE.attributeId()));

      when(this.dlmsObjectConfigService.getAttributeAddress(
              dlmsDevice_5_1, DlmsObjectType.CLIENT_SETUP_MBUS, channel))
          .thenReturn(
              new AttributeAddress(
                  InterfaceClass.MBUS_CLIENT.id(),
                  OBIS_CODE_TEMPLATE_MBUS_CLIENT_SETUP.replaceAll("<c>", Integer.toString(channel)),
                  DataAttribute.VALUE.attributeId()));
    }

    when(this.connectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.connectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.get(this.attributeAddressArgumentCaptor.capture()))
        .thenReturn(this.getResult);
    when(this.getResult.getResultData()).thenReturn(DataObject.newUInteger32Data(STATUS_MASK));
    when(this.dlmsConnection.set(this.setParameterArgumentCaptor.capture()))
        .thenReturn(AccessResultCode.SUCCESS);
    when(this.methodResult.getResultCode()).thenReturn(MethodResultCode.SUCCESS);
    when(this.dlmsConnection.action(this.methodParameterArgumentCaptor.capture()))
        .thenReturn(this.methodResult);

    this.executor.execute(this.connectionManager, dlmsDevice_5_1, this.dto, this.messageMetadata);

    this.assertCurrentStatusAttributeAddresses(this.attributeAddressArgumentCaptor.getAllValues());
    this.assertClearStatus(this.setParameterArgumentCaptor.getAllValues());
    this.assertMethodResetAlarm(this.methodParameterArgumentCaptor.getAllValues());
  }

  private void assertMethodResetAlarm(final List<MethodParameter> allMethods) {
    for (int i = 1; i <= 4; i++) {
      final MethodParameter methodParameter = allMethods.get(i - 1);
      assertThat(((Number) methodParameter.getParameter().getValue()).longValue()).isEqualTo(0L);

      assertThat(methodParameter.getInstanceId().asDecimalString())
          .isEqualTo(OBIS_CODE_TEMPLATE_MBUS_CLIENT_SETUP.replaceAll("<c>", String.valueOf(i)));
      assertThat(methodParameter.getClassId()).isEqualTo(CLASS_ID_MBUS_CLIENT);
    }
  }

  private void assertClearStatus(final List<SetParameter> setParameters) {
    for (int i = 1; i <= 4; i++) {
      final SetParameter setParameter = setParameters.get(i - 1);
      assertThat(((Number) setParameter.getData().getValue()).longValue()).isEqualTo(STATUS_MASK);

      final AttributeAddress attributeAddress = setParameter.getAttributeAddress();
      assertThat(attributeAddress.getInstanceId().asDecimalString())
          .isEqualTo(OBIS_CODE_TEMPLATE_CLEAR_STATUS.replaceAll("<c>", String.valueOf(i)));
      assertThat(attributeAddress.getClassId()).isEqualTo(CLASS_ID_CLEAR_STATUS);
    }
  }

  private void assertCurrentStatusAttributeAddresses(final List<AttributeAddress> attributes) {
    for (int i = 1; i <= 4; i++) {
      final AttributeAddress attributeAddress = attributes.get(i - 1);
      assertThat(attributeAddress.getInstanceId().asDecimalString())
          .isEqualTo(OBIS_CODE_TEMPLATE_READ_STATUS.replaceAll("<c>", String.valueOf(i)));
      assertThat(attributeAddress.getClassId()).isEqualTo(CLASS_ID_READ_STATUS);
    }
  }
}
