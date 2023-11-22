// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.datetime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject.Type;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dlms.services.Protocol;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SetActivityCalendarCommandActivationExecutorTest {

  private static final int CLASS_ID = 20;

  private static final String OBIS = "0.0.13.0.0.255";
  private static final ObisCode OBIS_CODE = new ObisCode(OBIS);
  private static final int METHOD_ID_ACTIVATE_PASSIVE_CALENDAR = 1;

  @Captor ArgumentCaptor<MethodParameter> actionParameterArgumentCaptor;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsConnection dlmsConnection;

  @Mock private MessageMetadata messageMetadata;

  @Mock private MethodResult methodResult;

  @Mock private DlmsDevice dlmsDevice;

  @InjectMocks private SetActivityCalendarCommandActivationExecutor executor;

  @BeforeEach
  public void setUp() throws IOException, ObjectConfigException {
    final ObjectConfigService objectConfigService = new ObjectConfigService();
    ReflectionTestUtils.setField(this.executor, "objectConfigService", objectConfigService);

    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.action(any(MethodParameter.class))).thenReturn(this.methodResult);
  }

  @ParameterizedTest
  @EnumSource(Protocol.class)
  void testActivationWithSuccess(final org.opensmartgridplatform.dlms.services.Protocol protocol)
      throws ProtocolAdapterException, IOException {

    when(this.dlmsDevice.getProtocolName()).thenReturn(protocol.getName());
    when(this.dlmsDevice.getProtocolVersion()).thenReturn(protocol.getVersion());

    // SETUP
    when(this.methodResult.getResultCode()).thenReturn(MethodResultCode.SUCCESS);

    // CALL
    final MethodResultCode resultCode =
        this.executor.execute(this.conn, this.dlmsDevice, null, this.messageMetadata);

    // VERIFY
    assertThat(resultCode).isEqualTo(MethodResultCode.SUCCESS);
    verify(this.dlmsConnection, times(1)).action(this.actionParameterArgumentCaptor.capture());

    final MethodParameter methodParameter = this.actionParameterArgumentCaptor.getValue();
    assertThat(methodParameter.getClassId()).isEqualTo(CLASS_ID);
    assertThat(methodParameter.getInstanceId()).isEqualTo(OBIS_CODE);
    assertThat(methodParameter.getId()).isEqualTo(METHOD_ID_ACTIVATE_PASSIVE_CALENDAR);
    assertThat(methodParameter.getParameter().getType()).isEqualTo(Type.INTEGER);
    assertThat((byte) methodParameter.getParameter().getValue()).isZero();
  }

  @ParameterizedTest
  @EnumSource(Protocol.class)
  void testActivationWithFailure(final Protocol protocol) throws IOException {

    // SETUP
    when(this.dlmsDevice.getProtocolName()).thenReturn(protocol.getName());
    when(this.dlmsDevice.getProtocolVersion()).thenReturn(protocol.getVersion());
    when(this.methodResult.getResultCode()).thenReturn(MethodResultCode.OTHER_REASON);

    // CALL
    assertThrows(
        ProtocolAdapterException.class,
        () -> this.executor.execute(this.conn, this.dlmsDevice, null, this.messageMetadata));

    // VERIFY
    verify(this.dlmsConnection, times(1)).action(this.actionParameterArgumentCaptor.capture());

    final MethodParameter methodParameter = this.actionParameterArgumentCaptor.getValue();
    assertThat(methodParameter.getClassId()).isEqualTo(CLASS_ID);
    assertThat(methodParameter.getInstanceId()).isEqualTo(OBIS_CODE);
    assertThat(methodParameter.getId()).isEqualTo(METHOD_ID_ACTIVATE_PASSIVE_CALENDAR);
    assertThat(methodParameter.getParameter().getType()).isEqualTo(Type.INTEGER);
    assertThat((byte) methodParameter.getParameter().getValue()).isZero();
  }
}
