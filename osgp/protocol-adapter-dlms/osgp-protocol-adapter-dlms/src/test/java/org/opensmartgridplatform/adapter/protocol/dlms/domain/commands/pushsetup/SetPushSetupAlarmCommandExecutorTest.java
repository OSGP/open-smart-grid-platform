/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.adapter.protocol.dlms.application.mapping.PushSetupMapper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultImpl;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObisCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MessageTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupAlarmDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SendDestinationAndMethodDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.TransportServiceTypeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class SetPushSetupAlarmCommandExecutorTest {

  protected static final int CLASS_ID = 40;

  protected static final int ATTRIBUTE_ID_SEND_DESTINATION_AND_METHOD = 3;

  protected static final int ATTRIBUTE_ID_PUSH_OBJECT_LIST = 2;

  private static final ObisCode OBIS_CODE = new ObisCode("0.1.25.9.0.255");

  private static final String DESTINATION = "destination";

  private static final SendDestinationAndMethodDto SEND_DESTINATION_AND_METHOD =
      new SendDestinationAndMethodDto(null, DESTINATION, null);

  private static final int PUSH_OBJECT_1_CLASS_ID = 1;

  private static final CosemObisCodeDto PUSH_OBJECT_1_OBIS_CODE =
      new CosemObisCodeDto(10, 11, 12, 13, 14, 15);

  private static final int PUSH_OBJECT_1_ATTRIBUTE_ID = 11;

  private static final int PUSH_OBJECT_1_DATA_INDEX = 0;

  private static final CosemObjectDefinitionDto PUSH_OBJECT_1 =
      new CosemObjectDefinitionDto(
          PUSH_OBJECT_1_CLASS_ID,
          PUSH_OBJECT_1_OBIS_CODE,
          PUSH_OBJECT_1_ATTRIBUTE_ID,
          PUSH_OBJECT_1_DATA_INDEX);

  private static final int PUSH_OBJECT_2_CLASS_ID = 2;

  private static final CosemObisCodeDto PUSH_OBJECT_2_OBIS_CODE =
      new CosemObisCodeDto(20, 21, 22, 23, 24, 25);

  private static final int PUSH_OBJECT_2_ATTRIBUTE_ID = 22;

  private static final int PUSH_OBJECT_2_DATA_INDEX = 0;

  private static final CosemObjectDefinitionDto PUSH_OBJECT_2 =
      new CosemObjectDefinitionDto(
          PUSH_OBJECT_2_CLASS_ID,
          PUSH_OBJECT_2_OBIS_CODE,
          PUSH_OBJECT_2_ATTRIBUTE_ID,
          PUSH_OBJECT_2_DATA_INDEX);

  private static final List<CosemObjectDefinitionDto> PUSH_OBJECT_LIST =
      Arrays.asList(PUSH_OBJECT_1, PUSH_OBJECT_2);

  private final DlmsDevice DLMS_DEVICE = this.createDlmsDevice(Protocol.SMR_5_0_0);

  @Captor ArgumentCaptor<SetParameter> setParameterArgumentCaptor;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsConnection dlmsConnection;

  @Mock private MessageMetadata messageMetadata;

  private final SetPushSetupAlarmCommandExecutor executor =
      new SetPushSetupAlarmCommandExecutor(new DlmsHelper(), new PushSetupMapper());

  @ParameterizedTest
  @EnumSource(Protocol.class)
  void testSetSendDestinationAndMethod(final Protocol protocol)
      throws ProtocolAdapterException, IOException {
    // SETUP
    final DlmsDevice device = this.createDlmsDevice(protocol);
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.SUCCESS);

    final PushSetupAlarmDto pushSetupAlarmDto =
        new PushSetupAlarmDto.Builder()
            .withSendDestinationAndMethod(SEND_DESTINATION_AND_METHOD)
            .build();

    // CALL
    final AccessResultCode resultCode =
        this.executor.execute(this.conn, device, pushSetupAlarmDto, this.messageMetadata);

    // VERIFY
    assertThat(resultCode).isEqualTo(AccessResultCode.SUCCESS);
    verify(this.dlmsConnection, times(1)).set(this.setParameterArgumentCaptor.capture());
    final List<SetParameter> setParameters = this.setParameterArgumentCaptor.getAllValues();
    this.verifySendDestinationAndMethodParameter(setParameters.get(0), protocol);
  }

  @Test
  void testSetPushObjectList() throws ProtocolAdapterException, IOException {
    // SETUP
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.SUCCESS);
    when(this.dlmsConnection.get(any(AttributeAddress.class)))
        .thenReturn(new GetResultImpl(DataObject.newNullData(), AccessResultCode.SUCCESS));

    final PushSetupAlarmDto.Builder pushSetupAlarmBuilder = new PushSetupAlarmDto.Builder();
    pushSetupAlarmBuilder.withPushObjectList(PUSH_OBJECT_LIST);
    final PushSetupAlarmDto pushSetupAlarmDto = pushSetupAlarmBuilder.build();

    // CALL
    final AccessResultCode resultCode =
        this.executor.execute(this.conn, this.DLMS_DEVICE, pushSetupAlarmDto, this.messageMetadata);

    // VERIFY
    assertThat(resultCode).isEqualTo(AccessResultCode.SUCCESS);
    verify(this.dlmsConnection, times(1)).set(this.setParameterArgumentCaptor.capture());
    final List<SetParameter> setParameters = this.setParameterArgumentCaptor.getAllValues();
    this.verifyPushObjectListParameter(setParameters.get(0));
  }

  @Test
  void testSetBothSendDestinationAndPushObjectList() throws ProtocolAdapterException, IOException {
    // SETUP
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.SUCCESS);
    when(this.dlmsConnection.get(any(AttributeAddress.class)))
        .thenReturn(new GetResultImpl(DataObject.newNullData(), AccessResultCode.SUCCESS));

    final PushSetupAlarmDto pushSetupAlarmDto =
        new PushSetupAlarmDto.Builder()
            .withSendDestinationAndMethod(SEND_DESTINATION_AND_METHOD)
            .withPushObjectList(PUSH_OBJECT_LIST)
            .build();

    // CALL
    final AccessResultCode resultCode =
        this.executor.execute(this.conn, this.DLMS_DEVICE, pushSetupAlarmDto, this.messageMetadata);

    // VERIFY
    assertThat(resultCode).isEqualTo(AccessResultCode.SUCCESS);
    verify(this.dlmsConnection, times(2)).set(this.setParameterArgumentCaptor.capture());
    final List<SetParameter> setParameters = this.setParameterArgumentCaptor.getAllValues();
    this.verifySendDestinationAndMethodParameter(
        setParameters.get(0), Protocol.forDevice(this.DLMS_DEVICE));
    this.verifyPushObjectListParameter(setParameters.get(1));
  }

  @Test
  void testSetPushObjectListWithInvalidPushObject() throws IOException {
    // SETUP
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.get(any(AttributeAddress.class)))
        .thenReturn(new GetResultImpl(DataObject.newNullData(), AccessResultCode.OBJECT_UNDEFINED));

    final PushSetupAlarmDto pushSetupAlarmDto =
        new PushSetupAlarmDto.Builder().withPushObjectList(PUSH_OBJECT_LIST).build();

    // CALL
    final Throwable thrown =
        catchThrowable(
            () -> {
              this.executor.execute(
                  this.conn, this.DLMS_DEVICE, pushSetupAlarmDto, this.messageMetadata);
            });

    // VERIFY
    assertThat(thrown).isInstanceOf(ProtocolAdapterException.class);
  }

  @Test
  void testSetWithUnsupportedFieldsSet() {
    // SETUP
    final PushSetupAlarmDto pushSetupAlarmDto =
        new PushSetupAlarmDto.Builder().withCommunicationWindow(new ArrayList<>()).build();

    // CALL
    final Throwable thrown =
        catchThrowable(
            () -> {
              this.executor.execute(
                  this.conn, this.DLMS_DEVICE, pushSetupAlarmDto, this.messageMetadata);
            });

    // VERIFY
    assertThat(thrown).isInstanceOf(ProtocolAdapterException.class);
  }

  private void verifySendDestinationAndMethodParameter(
      final SetParameter setParameter, final Protocol protocol) {
    final AttributeAddress attributeAddress = setParameter.getAttributeAddress();
    assertThat(attributeAddress.getClassId()).isEqualTo(CLASS_ID);
    assertThat(attributeAddress.getInstanceId()).isEqualTo(OBIS_CODE);
    assertThat(attributeAddress.getId()).isEqualTo(ATTRIBUTE_ID_SEND_DESTINATION_AND_METHOD);

    final DataObject dataObject = setParameter.getData();
    assertThat(dataObject.getType()).isEqualTo(Type.STRUCTURE);

    final List<DataObject> values = dataObject.getValue();

    assertThat(values).hasSize(3);

    // Transport Service Type
    assertThat(values.get(0).getType()).isEqualTo((Type.ENUMERATE));
    assertThat((int) values.get(0).getValue())
        .isEqualTo(TransportServiceTypeDto.TCP.getDlmsEnumValue());

    // Destination
    assertThat(values.get(1).getType()).isEqualTo((Type.OCTET_STRING));
    assertThat((byte[]) values.get(1).getValue()).isEqualTo(DESTINATION.getBytes());

    // Message Type
    assertThat(values.get(2).getType()).isEqualTo((Type.ENUMERATE));
    if (protocol.isSmr5()) {
      assertThat((int) values.get(2).getValue())
          .isEqualTo(MessageTypeDto.A_XDR_ENCODED_X_DLMS_APDU.getDlmsEnumValue());
    } else {
      assertThat((int) values.get(2).getValue())
          .isEqualTo(MessageTypeDto.MANUFACTURER_SPECIFIC.getDlmsEnumValue());
    }
  }

  private void verifyPushObjectListParameter(final SetParameter setParameter) {
    final AttributeAddress attributeAddress = setParameter.getAttributeAddress();
    assertThat(attributeAddress.getClassId()).isEqualTo(CLASS_ID);
    assertThat(attributeAddress.getInstanceId()).isEqualTo(OBIS_CODE);
    assertThat(attributeAddress.getId()).isEqualTo(ATTRIBUTE_ID_PUSH_OBJECT_LIST);

    final DataObject dataObject = setParameter.getData();
    assertThat(dataObject.getType()).isEqualTo(Type.ARRAY);

    final List<DataObject> values = dataObject.getValue();
    assertThat(values).hasSize(PUSH_OBJECT_LIST.size());

    this.verifyPushObject(
        values.get(0),
        PUSH_OBJECT_1_CLASS_ID,
        PUSH_OBJECT_1_OBIS_CODE.toByteArray(),
        (byte) PUSH_OBJECT_1_ATTRIBUTE_ID,
        PUSH_OBJECT_1_DATA_INDEX);
    this.verifyPushObject(
        values.get(1),
        PUSH_OBJECT_2_CLASS_ID,
        PUSH_OBJECT_2_OBIS_CODE.toByteArray(),
        (byte) PUSH_OBJECT_2_ATTRIBUTE_ID,
        PUSH_OBJECT_2_DATA_INDEX);
  }

  private void verifyPushObject(
      final DataObject pushObject,
      final int classId,
      final byte[] instanceId,
      final byte attributeId,
      final int dataIndex) {
    assertThat(pushObject.getType()).isEqualTo((Type.STRUCTURE));

    final List<DataObject> pushObject2Values = pushObject.getValue();

    assertThat(pushObject2Values).hasSize(4);

    // ClassId
    assertThat(pushObject2Values.get(0).getType()).isEqualTo((Type.LONG_UNSIGNED));
    assertThat((int) pushObject2Values.get(0).getValue()).isEqualTo(classId);

    // Instance id
    assertThat(pushObject2Values.get(1).getType()).isEqualTo((Type.OCTET_STRING));
    assertThat((byte[]) pushObject2Values.get(1).getValue()).isEqualTo(instanceId);

    // Attribute id
    assertThat(pushObject2Values.get(2).getType()).isEqualTo((Type.INTEGER));
    assertThat((byte) pushObject2Values.get(2).getValue()).isEqualTo(attributeId);

    // DataIndex
    assertThat(pushObject2Values.get(3).getType()).isEqualTo((Type.LONG_UNSIGNED));
    assertThat((int) pushObject2Values.get(3).getValue()).isEqualTo(dataIndex);
  }

  private DlmsDevice createDlmsDevice(final Protocol protocol) {
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(protocol);
    return device;
  }
}
