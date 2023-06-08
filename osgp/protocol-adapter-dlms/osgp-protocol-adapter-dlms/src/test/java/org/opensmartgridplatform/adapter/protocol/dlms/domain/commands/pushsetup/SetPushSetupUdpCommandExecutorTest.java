/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
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
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetPushSetupUdpRequestDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class SetPushSetupUdpCommandExecutorTest {

  protected static final int CLASS_ID = 40;

  protected static final int ATTRIBUTE_ID_COMMUNICATION_WINDOW = 4;

  private static final ObisCode OBIS_CODE = new ObisCode("0.3.25.9.0.255");

  @Captor ArgumentCaptor<SetParameter> setParameterArgumentCaptor;

  @Mock private ObjectConfigService objectConfigService;
  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsConnection dlmsConnection;

  @Mock private MessageMetadata messageMetadata;

  @InjectMocks private SetPushSetupUdpCommandExecutor executor;

  @Test
  void testSetCommunicationWindow()
      throws ProtocolAdapterException, IOException, ObjectConfigException {
    // SETUP
    final Protocol protocol = Protocol.SMR_5_5;
    final DlmsDevice device = this.createDlmsDevice(protocol);
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.SUCCESS);

    final CosemObject cosemObject = mock(CosemObject.class);
    when(cosemObject.getClassId()).thenReturn(CLASS_ID);
    when(cosemObject.getObis()).thenReturn(OBIS_CODE.toString());

    when(this.objectConfigService.getCosemObject(
            protocol.getName(), protocol.getVersion(), DlmsObjectType.PUSH_SETUP_UDP))
        .thenReturn(cosemObject);

    final SetPushSetupUdpRequestDto requestDto = new SetPushSetupUdpRequestDto();

    // CALL
    final AccessResultCode resultCode =
        this.executor.execute(this.conn, device, requestDto, this.messageMetadata);

    // VERIFY
    assertThat(resultCode).isEqualTo(AccessResultCode.SUCCESS);
    verify(this.dlmsConnection, times(1)).set(this.setParameterArgumentCaptor.capture());
    final List<SetParameter> setParameters = this.setParameterArgumentCaptor.getAllValues();
    this.verifySetParameter(setParameters.get(0), protocol);
  }

  @Test
  void testSetCommunicationWindowNoConfigured() throws IOException, ObjectConfigException {
    // SETUP
    final Protocol protocol = Protocol.SMR_5_2;
    final DlmsDevice device = this.createDlmsDevice(protocol);

    when(this.objectConfigService.getCosemObject(
            protocol.getName(), protocol.getVersion(), DlmsObjectType.PUSH_SETUP_UDP))
        .thenThrow(IllegalArgumentException.class);

    final SetPushSetupUdpRequestDto requestDto = new SetPushSetupUdpRequestDto();

    // CALL
    assertThrows(
        IllegalArgumentException.class,
        () -> this.executor.execute(this.conn, device, requestDto, this.messageMetadata));

    verify(this.dlmsConnection, never()).set(any(SetParameter.class));
  }

  private void verifySetParameter(final SetParameter setParameter, final Protocol protocol) {
    final AttributeAddress attributeAddress = setParameter.getAttributeAddress();
    assertThat(attributeAddress.getClassId()).isEqualTo(CLASS_ID);
    assertThat(attributeAddress.getInstanceId()).isEqualTo(OBIS_CODE);
    assertThat(attributeAddress.getId()).isEqualTo(ATTRIBUTE_ID_COMMUNICATION_WINDOW);

    final DataObject dataObject = setParameter.getData();
    assertThat(dataObject.getType()).isEqualTo(Type.ARRAY);

    final List<DataObject> values = dataObject.getValue();
    assertThat(values).hasSize(0);
  }

  private DlmsDevice createDlmsDevice(final Protocol protocol) {
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(protocol);
    return device;
  }
}
