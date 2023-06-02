//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.pushsetup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
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
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MessageTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupSmsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SendDestinationAndMethodDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.TransportServiceTypeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class SetPushSetupSmsCommandExecutorTest {

  protected static final int CLASS_ID = 40;

  protected static final int ATTRIBUTE_ID_SEND_DESTINATION_AND_METHOD = 3;

  private static final ObisCode OBIS_CODE = new ObisCode("0.2.25.9.0.255");

  private static final String DESTINATION = "destination";

  private static final SendDestinationAndMethodDto SEND_DESTINATION_AND_METHOD =
      new SendDestinationAndMethodDto(null, DESTINATION, null);

  private final DlmsDevice DLMS_DEVICE = this.createDlmsDevice(Protocol.SMR_5_0_0);

  @Captor ArgumentCaptor<SetParameter> setParameterArgumentCaptor;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsConnection dlmsConnection;

  @Mock private MessageMetadata messageMetadata;

  private final SetPushSetupSmsCommandExecutor executor =
      new SetPushSetupSmsCommandExecutor(new PushSetupMapper());

  @ParameterizedTest
  @EnumSource(Protocol.class)
  void testSetSendDestinationAndMethod(final Protocol protocol)
      throws ProtocolAdapterException, IOException {
    // SETUP
    final DlmsDevice device = this.createDlmsDevice(protocol);
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.SUCCESS);

    final PushSetupSmsDto pushSetupSmsDto =
        new PushSetupSmsDto.Builder()
            .withSendDestinationAndMethod(SEND_DESTINATION_AND_METHOD)
            .build();

    // CALL
    final AccessResultCode resultCode =
        this.executor.execute(this.conn, device, pushSetupSmsDto, this.messageMetadata);

    // VERIFY
    assertThat(resultCode).isEqualTo(AccessResultCode.SUCCESS);
    verify(this.dlmsConnection, times(1)).set(this.setParameterArgumentCaptor.capture());
    final List<SetParameter> setParameters = this.setParameterArgumentCaptor.getAllValues();
    this.verifySendDestinationAndMethodParameter(setParameters.get(0), protocol);
  }

  @Test
  void testSetWithUnsupportedFieldsSet() {
    // SETUP
    final PushSetupSmsDto pushSetupSmsDto =
        new PushSetupSmsDto.Builder().withCommunicationWindow(new ArrayList<>()).build();

    // CALL
    final Throwable thrown =
        catchThrowable(
            () -> {
              this.executor.execute(
                  this.conn, this.DLMS_DEVICE, pushSetupSmsDto, this.messageMetadata);
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

  private DlmsDevice createDlmsDevice(final Protocol protocol) {
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(protocol);
    return device;
  }
}
