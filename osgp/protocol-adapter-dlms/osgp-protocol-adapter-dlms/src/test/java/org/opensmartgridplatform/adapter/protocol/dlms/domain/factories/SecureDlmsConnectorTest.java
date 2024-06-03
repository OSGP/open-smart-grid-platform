/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics.METRIC_REQUEST_TIMER_PREFIX;
import static org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics.TAG_BTS_ID;
import static org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics.TAG_CELL_ID;
import static org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics.TAG_COMMUNICATION_METHOD;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.E_METER_AUTHENTICATION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.E_METER_ENCRYPTION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.LLS_PASSWORD;

import io.micrometer.core.instrument.Timer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.SecuritySuite;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.openmuc.jdlms.settings.client.ReferencingMethod;
import org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecretManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.threads.RecoverKeyProcessInitiator;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class SecureDlmsConnectorTest {
  private static final String TIMER_NAME = "create_connection";
  final int RESPONSE_TIMEOUT = 10;
  final int LOGICAL_DEVICE_ADDRESS = 1;
  final DlmsDeviceAssociation DEVICE_ASSOCIATION = mock(DlmsDeviceAssociation.class);
  final ProtocolAdapterMetrics PROTOCOL_ADAPTER_METRICS = mock(ProtocolAdapterMetrics.class);
  final SecretManagementService SECRET_MANAGEMENT_SERVICE = mock(SecretManagementService.class);

  final MessageMetadata messageMetadata = mock(MessageMetadata.class);
  final DlmsDevice device = mock(DlmsDevice.class);
  final DlmsMessageListener dlmsMessageListener = mock(DlmsMessageListener.class);

  private final SecureDlmsConnector connectorLls1 =
      new Lls1Connector(
          this.RESPONSE_TIMEOUT,
          this.LOGICAL_DEVICE_ADDRESS,
          this.DEVICE_ASSOCIATION,
          this.SECRET_MANAGEMENT_SERVICE,
          this.PROTOCOL_ADAPTER_METRICS);

  private final SecureDlmsConnector connectorHls5 =
      new Hls5Connector(
          mock(RecoverKeyProcessInitiator.class),
          this.RESPONSE_TIMEOUT,
          this.LOGICAL_DEVICE_ADDRESS,
          this.DEVICE_ASSOCIATION,
          this.SECRET_MANAGEMENT_SERVICE,
          this.PROTOCOL_ADAPTER_METRICS);

  @Test
  void testConnect() throws OsgpException {
    when(this.device.getIpAddress()).thenReturn("127.0.0.1");
    when(this.device.getChallengeLength()).thenReturn(8);
    when(this.device.getDeviceIdentification()).thenReturn("device1");
    final DlmsConnection dlmsConnection = mock(DlmsConnection.class);

    when(this.SECRET_MANAGEMENT_SERVICE.getKeys(any(MessageMetadata.class), anyString(), anyList()))
        .thenReturn(this.getKeys());
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
          this.connectorLls1.connect(this.messageMetadata, this.device, this.dlmsMessageListener);
      assertThat(result).isEqualTo(dlmsConnection);

      verify(this.PROTOCOL_ADAPTER_METRICS)
          .recordTimer(eq(timer), anyLong(), eq(TimeUnit.MILLISECONDS));
    }
  }

  private Map<SecurityKeyType, byte[]> getKeys() {
    final Map<SecurityKeyType, byte[]> keys = new HashMap<>();
    keys.put(LLS_PASSWORD, "1234567890123456".getBytes());
    keys.put(E_METER_ENCRYPTION, "1234567890123456".getBytes());
    keys.put(E_METER_AUTHENTICATION, "1234567890123456".getBytes());
    return keys;
  }

  @Test
  void testConnectionErrorLls1() throws OsgpException {
    this.testConnectionError(this.connectorLls1);
  }

  @Test
  void testConnectionErrorHls5() throws OsgpException {
    this.testConnectionError(this.connectorHls5);
  }

  void testConnectionError(final SecureDlmsConnector connector) {
    when(this.device.getIpAddress()).thenReturn("127.0.0.1");
    when(this.device.getChallengeLength()).thenReturn(8);
    when(this.device.getDeviceIdentification()).thenReturn("device1");

    when(this.SECRET_MANAGEMENT_SERVICE.getKeys(any(MessageMetadata.class), anyString(), anyList()))
        .thenReturn(this.getKeys());

    try (final MockedConstruction<TcpConnectionBuilder> mockTcpConnectionBuilder =
        mockConstruction(
            TcpConnectionBuilder.class,
            (mock, context) -> {
              this.prepareTcpBuidlerMock(mock);
              when(mock.build()).thenThrow(new IOException("Connection reset"));
            })) {
      final Timer timer = mock(Timer.class);
      when(this.PROTOCOL_ADAPTER_METRICS.createTimer(
              METRIC_REQUEST_TIMER_PREFIX + TIMER_NAME, this.getTags()))
          .thenReturn(timer);

      final ConnectionException connectionException =
          assertThrows(
              ConnectionException.class,
              () -> {
                connector.connect(this.messageMetadata, this.device, this.dlmsMessageListener);
              });
      assertThat(connectionException.getType()).isEqualTo(FunctionalExceptionType.CONNECTION_RESET);
    }
  }

  @Test
  void testDeviceNull() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          this.connectorLls1.connect(this.messageMetadata, null, this.dlmsMessageListener);
        });
  }

  @Test
  void testIpAddressNull() {
    when(this.device.getIpAddress()).thenReturn(null);
    final FunctionalException functionalException =
        assertThrows(
            FunctionalException.class,
            () -> {
              this.connectorLls1.connect(
                  this.messageMetadata, this.device, this.dlmsMessageListener);
            });
    assertThat(functionalException.getExceptionType())
        .isEqualTo(FunctionalExceptionType.INVALID_IP_ADDRESS);
  }

  private void prepareTcpBuidlerMock(final TcpConnectionBuilder mock) {
    when(mock.setResponseTimeout(anyInt())).thenReturn(mock);
    when(mock.setLogicalDeviceId(anyInt())).thenReturn(mock);
    when(mock.setClientId(anyInt())).thenReturn(mock);
    when(mock.setSecuritySuite(any(SecuritySuite.class))).thenReturn(mock);
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
