// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.DlmsConnection;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDeviceBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.InvocationCountingDlmsMessageListener;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class DlmsConnectionFactoryTest {
  private DlmsConnectionFactory factory;
  private MessageMetadata messageMetadata;
  private Consumer<DlmsConnectionManager> task;
  private final AtomicReference<DlmsConnectionManager> actualConnectionManagerReference =
      new AtomicReference<>();
  private final AtomicReference<DlmsConnection> actualConnectionReference = new AtomicReference<>();

  @Mock private Hls5Connector hls5Connector;

  @Mock private Lls1Connector lls1Connector;

  @Mock private Lls0Connector lls0Connector;

  @Mock private DomainHelperService domainHelperService;

  @Mock private DlmsConnection connection;

  @BeforeEach
  void setUp() {
    this.factory =
        new DlmsConnectionFactory(
            this.hls5Connector, this.lls1Connector, this.lls0Connector, this.domainHelperService);
    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();
    this.actualConnectionManagerReference.set(null);
    this.actualConnectionReference.set(null);
    this.task =
        conn -> {
          this.actualConnectionManagerReference.set(conn);
          this.actualConnectionReference.set(conn.getConnection());
        };
  }

  @Test
  void createsConnectionManagerForHls5Device() throws Exception {
    final DlmsDevice device = new DlmsDeviceBuilder().withHls5Active(true).build();
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();
    when(this.hls5Connector.connect(this.messageMetadata, device, listener))
        .thenReturn(this.connection);
    final DlmsConnectionManager expected =
        this.newConnectionManager(device, listener, this.hls5Connector);

    this.factory.createAndHandleConnection(this.messageMetadata, device, listener, this.task);

    this.assertConnectionManagerForDevice(expected);
  }

  @Test
  void createAndHandleConnectionThrowsForHls4Device() {
    final DlmsDevice device = new DlmsDeviceBuilder().withHls4Active(true).build();
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();

    assertThatExceptionOfType(FunctionalException.class)
        .isThrownBy(
            () ->
                this.factory.createAndHandleConnection(
                    this.messageMetadata, device, listener, this.task));
  }

  @Test
  void createAndHandleConnectionThrowsForHls3Device() {
    final DlmsDevice device = new DlmsDeviceBuilder().withHls3Active(true).build();
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();

    assertThatExceptionOfType(FunctionalException.class)
        .isThrownBy(
            () ->
                this.factory.createAndHandleConnection(
                    this.messageMetadata, device, listener, this.task));
  }

  @Test
  void createsConnectionManagerForLls1Device() throws Exception {
    final DlmsDevice device = new DlmsDeviceBuilder().withLls1Active(true).build();
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();
    when(this.lls1Connector.connect(this.messageMetadata, device, listener))
        .thenReturn(this.connection);
    final DlmsConnectionManager expected =
        this.newConnectionManager(device, listener, this.lls1Connector);

    this.factory.createAndHandleConnection(this.messageMetadata, device, listener, this.task);

    this.assertConnectionManagerForDevice(expected);
  }

  @Test
  void createsConnectionManagerForLls0Device() throws Exception {
    final DlmsDevice device = new DlmsDeviceBuilder().withLls1Active(false).build();
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();
    when(this.lls0Connector.connect(this.messageMetadata, device, listener))
        .thenReturn(this.connection);
    final DlmsConnectionManager expected =
        this.newConnectionManager(device, listener, this.lls0Connector);

    this.factory.createAndHandleConnection(this.messageMetadata, device, listener, this.task);

    this.assertConnectionManagerForDevice(expected);
  }

  @Test
  void createsPublicClientConnectionManagerForDevice() throws Exception {
    final DlmsDevice device = new DlmsDeviceBuilder().withHls5Active(true).build();
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();
    when(this.lls0Connector.connect(this.messageMetadata, device, listener))
        .thenReturn(this.connection);
    final DlmsConnectionManager expected =
        this.newConnectionManager(device, listener, this.lls0Connector);

    this.factory.createAndHandlePublicClientConnection(
        this.messageMetadata, device, listener, this.task);

    this.assertConnectionManagerForDevice(expected);
  }

  @Test
  void setsIpAddressWhenConnectingToTheDevice() throws Exception {
    final DlmsDevice device =
        new DlmsDeviceBuilder()
            .withHls5Active(true)
            .withIpAddress(null)
            .withIpAddressStatic(false)
            .build();
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();
    when(this.hls5Connector.connect(this.messageMetadata, device, listener))
        .thenReturn(this.connection);

    this.factory.createAndHandleConnection(this.messageMetadata, device, listener, this.task);

    verify(this.domainHelperService)
        .setIpAddressFromMessageMetadataOrSessionProvider(device, this.messageMetadata);
  }

  private void assertConnectionManagerForDevice(final DlmsConnectionManager expected)
      throws IOException {
    assertThat(this.actualConnectionManagerReference.get())
        .isEqualToComparingOnlyGivenFields(
            expected,
            "messageMetadata",
            "connector",
            "device",
            "dlmsMessageListener",
            "domainHelperService");

    assertThat(this.actualConnectionReference.get()).isSameAs(this.connection);
    verify(this.connection, times(1)).close();
  }

  private DlmsConnectionManager newConnectionManager(
      final DlmsDevice device, final DlmsMessageListener listener, final DlmsConnector connector)
      throws OsgpException {
    final DlmsConnectionManager connectionManager =
        new DlmsConnectionManager(
            connector, this.messageMetadata, device, listener, this.domainHelperService, null);
    connectionManager.connect();
    return connectionManager;
  }
}
