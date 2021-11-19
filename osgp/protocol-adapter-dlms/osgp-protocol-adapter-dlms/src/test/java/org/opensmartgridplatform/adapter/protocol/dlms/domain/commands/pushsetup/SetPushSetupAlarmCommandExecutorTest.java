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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MessageTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupAlarmDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SendDestinationAndMethodDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.TransportServiceTypeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class SetPushSetupAlarmCommandExecutorTest {

  protected static final int CLASS_ID = 40;

  protected static final int ATTRIBUTE_ID_SEND_DESTINATION_AND_METHOD = 3;

  private static final ObisCode OBIS_CODE = new ObisCode("0.1.25.9.0.255");

  private static final int TRANSPORT_SERVICE_TYPE_TCP = 0;

  private static final String DESTINATION = "destination";

  private static final int MESSAGE_TYPE_A_XDR_ENCODED_X_DLMS_APDU = 0;

  private final DlmsDevice DLMS_DEVICE_5_2 = this.createDlmsDevice(Protocol.SMR_5_2);

  private final DlmsDevice DLMS_DEVICE_5_1 = this.createDlmsDevice(Protocol.SMR_5_1);

  private final DlmsDevice DLMS_DEVICE_5_0 = this.createDlmsDevice(Protocol.SMR_5_0_0);

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsConnection dlmsConnection;

  @Mock private MessageMetadata messageMetadata = mock(MessageMetadata.class);

  private final SetPushSetupAlarmCommandExecutor executor = new SetPushSetupAlarmCommandExecutor();

  @Test
  void testSetSendDestinationAndMethod() throws ProtocolAdapterException, IOException {
    // SETUP
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.SUCCESS);

    final PushSetupAlarmDto.Builder pushSetupAlarmBuilder = new PushSetupAlarmDto.Builder();
    pushSetupAlarmBuilder.withSendDestinationAndMethod(
        new SendDestinationAndMethodDto(
            TransportServiceTypeDto.TCP, DESTINATION, MessageTypeDto.A_XDR_ENCODED_X_DLMS_APDU));
    final PushSetupAlarmDto pushSetupAlarmDto = pushSetupAlarmBuilder.build();

    // CALL
    final AccessResultCode resultCode =
        this.executor.execute(
            this.conn, this.DLMS_DEVICE_5_1, pushSetupAlarmDto, this.messageMetadata);

    // VERIFY
    assertThat(resultCode).isEqualTo(AccessResultCode.SUCCESS);
  }

  @Test
  void testSetPushObjectList() throws ProtocolAdapterException, IOException {
    // SETUP
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.SUCCESS);

    final PushSetupAlarmDto.Builder pushSetupAlarmBuilder = new PushSetupAlarmDto.Builder();
    pushSetupAlarmBuilder.withSendDestinationAndMethod(
        new SendDestinationAndMethodDto(
            TransportServiceTypeDto.TCP, DESTINATION, MessageTypeDto.A_XDR_ENCODED_X_DLMS_APDU));
    pushSetupAlarmBuilder.withPushObjectList(new ArrayList<>());
    final PushSetupAlarmDto pushSetupAlarmDto = pushSetupAlarmBuilder.build();

    // CALL
    final AccessResultCode resultCode =
        this.executor.execute(
            this.conn, this.DLMS_DEVICE_5_1, pushSetupAlarmDto, this.messageMetadata);

    // VERIFY
    assertThat(resultCode).isEqualTo(AccessResultCode.SUCCESS);
  }

  @Test
  void testSetWithoutSendDestinationAndMethod() {
    // SETUP
    final PushSetupAlarmDto.Builder pushSetupAlarmBuilder = new PushSetupAlarmDto.Builder();
    final PushSetupAlarmDto pushSetupAlarmDto = pushSetupAlarmBuilder.build();

    // CALL
    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () -> {
              this.executor.execute(
                  this.conn, this.DLMS_DEVICE_5_1, pushSetupAlarmDto, this.messageMetadata);
            });
  }

  @Test
  void testSetWithUnsupportedFieldsSet() {
    // SETUP
    final PushSetupAlarmDto.Builder pushSetupAlarmBuilder = new PushSetupAlarmDto.Builder();
    pushSetupAlarmBuilder.withCommunicationWindow(new ArrayList<>());
    final PushSetupAlarmDto pushSetupAlarmDto = pushSetupAlarmBuilder.build();

    // CALL
    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () -> {
              this.executor.execute(
                  this.conn, this.DLMS_DEVICE_5_1, pushSetupAlarmDto, this.messageMetadata);
            });
  }

  private DlmsDevice createDlmsDevice(final Protocol protocol) {
    final DlmsDevice device = new DlmsDevice();
    device.setDeviceIdentification("123456789012");
    device.setProtocol(protocol);
    return device;
  }

  private DataObject createSendDestinationAndMethodObject() {

    final List<DataObject> sendDestinationAndMethodElements = new ArrayList<>();

    sendDestinationAndMethodElements.add(DataObject.newEnumerateData(TRANSPORT_SERVICE_TYPE_TCP));
    sendDestinationAndMethodElements.add(
        DataObject.newOctetStringData(DESTINATION.getBytes(StandardCharsets.US_ASCII)));
    sendDestinationAndMethodElements.add(
        DataObject.newEnumerateData(MESSAGE_TYPE_A_XDR_ENCODED_X_DLMS_APDU));

    return DataObject.newStructureData(sendDestinationAndMethodElements);
  }
}
