/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics.METRIC_REQUEST_TIMER_PREFIX;
import static org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics.TAG_BTS_ID;
import static org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics.TAG_CELL_ID;
import static org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics.TAG_COMMUNICATION_METHOD;

import io.micrometer.core.instrument.Timer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.openmuc.jdlms.settings.client.ReferencingMethod;
import org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class Lls0ConnectorTest {
  private static final String TIMER_NAME = "create_connection";
  final int RESPONSE_TIMEOUT = 10;
  final int LOGICAL_DEVICE_ADDRESS = 1;
  final DlmsDeviceAssociation DEVICE_ASSOCIATION = mock(DlmsDeviceAssociation.class);
  final ProtocolAdapterMetrics PROTOCOL_ADAPTER_METRICS = mock(ProtocolAdapterMetrics.class);

  final MessageMetadata messageMetadata = mock(MessageMetadata.class);
  final DlmsDevice device = mock(DlmsDevice.class);
  final DlmsMessageListener dlmsMessageListener = mock(DlmsMessageListener.class);

  private Lls0Connector connector;

  @BeforeEach
  void setUp() {
    this.connector =
        new Lls0Connector(
            this.RESPONSE_TIMEOUT,
            this.LOGICAL_DEVICE_ADDRESS,
            this.DEVICE_ASSOCIATION,
            this.PROTOCOL_ADAPTER_METRICS);
  }

  @Test
  void testConnect() throws OsgpException {
    when(this.device.getIpAddress()).thenReturn("127.0.0.1");
    when(this.device.getChallengeLength()).thenReturn(8);
    final DlmsConnection dlmsConnection = mock(DlmsConnection.class);

    try (final MockedConstruction<TcpConnectionBuilder> mockTcpConnectionBuilder =
        mockConstruction(
            TcpConnectionBuilder.class,
            (mock, context) -> {
              this.prepareTcpBuidlerMock(mock);
              when(mock.build()).thenReturn(dlmsConnection);
            })) {
      final Timer timer = mock(Timer.class);
      when(this.PROTOCOL_ADAPTER_METRICS.createTimer(
              METRIC_REQUEST_TIMER_PREFIX + TIMER_NAME, this.getTags()))
          .thenReturn(timer);

      final DlmsConnection result =
          this.connector.connect(this.messageMetadata, this.device, this.dlmsMessageListener);
      assertThat(result).isEqualTo(dlmsConnection);

      verify(this.PROTOCOL_ADAPTER_METRICS)
          .recordTimer(eq(timer), anyLong(), eq(TimeUnit.MILLISECONDS));
    }
  }

  @Test
  void testConnectionError() {
    when(this.device.getIpAddress()).thenReturn("127.0.0.1");
    when(this.device.getChallengeLength()).thenReturn(8);

    try (final MockedConstruction<TcpConnectionBuilder> mockTcpConnectionBuilder =
        mockConstruction(
            TcpConnectionBuilder.class,
            (mock, context) -> {
              this.prepareTcpBuidlerMock(mock);
              when(mock.build()).thenThrow(new IOException());
            })) {
      final Timer timer = mock(Timer.class);
      when(this.PROTOCOL_ADAPTER_METRICS.createTimer(
              METRIC_REQUEST_TIMER_PREFIX + TIMER_NAME, this.getTags()))
          .thenReturn(timer);

      assertThrows(
          ConnectionException.class,
          () -> {
            this.connector.connect(this.messageMetadata, this.device, this.dlmsMessageListener);
          });
    }
  }

  @Test
  void testDeviceNull() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          this.connector.connect(this.messageMetadata, null, this.dlmsMessageListener);
        });
  }

  @Test
  void testIpAddressNull() {
    when(this.device.getIpAddress()).thenReturn(null);
    final FunctionalException functionalException =
        assertThrows(
            FunctionalException.class,
            () -> {
              this.connector.connect(this.messageMetadata, this.device, this.dlmsMessageListener);
            });
    assertThat(functionalException.getExceptionType())
        .isEqualTo(FunctionalExceptionType.INVALID_IP_ADDRESS);
  }

  @Test
  void testChallengeLengthInvalid() {
    when(this.device.getIpAddress()).thenReturn("127.0.0.1");
    when(this.device.getChallengeLength()).thenReturn(0);
    final FunctionalException functionalException =
        assertThrows(
            FunctionalException.class,
            () -> {
              this.connector.connect(this.messageMetadata, this.device, this.dlmsMessageListener);
            });
    assertThat(functionalException.getExceptionType())
        .isEqualTo(FunctionalExceptionType.CHALLENGE_LENGTH_OUT_OF_RANGE);
  }

  private void prepareTcpBuidlerMock(final TcpConnectionBuilder mock) {
    when(mock.setResponseTimeout(anyInt())).thenReturn(mock);
    when(mock.setLogicalDeviceId(anyInt())).thenReturn(mock);
    when(mock.setClientId(anyInt())).thenReturn(mock);
    when(mock.setReferencingMethod(ReferencingMethod.LOGICAL)).thenReturn(mock);
  }

  private Map<String, String> getTags() {
    final Map<String, String> tags = new HashMap<>();
    tags.put(TAG_COMMUNICATION_METHOD, String.valueOf(this.device.getCommunicationMethod()));
    if (this.messageMetadata.getBaseTransceiverStationId() != null) {
      tags.put(TAG_BTS_ID, String.valueOf(this.messageMetadata.getBaseTransceiverStationId()));
    }
    if (this.messageMetadata.getCellId() != null) {
      tags.put(TAG_CELL_ID, String.valueOf(this.messageMetadata.getCellId()));
    }
    return tags;
  }
}
