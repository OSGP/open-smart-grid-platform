/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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
public class DlmsConnectionFactoryTest {
  private DlmsConnectionFactory factory;
  private MessageMetadata messageMetadata;
  private Consumer<DlmsConnectionManager> task;

  @Mock private Hls5Connector hls5Connector;

  @Mock private Lls1Connector lls1Connector;

  @Mock private Lls0Connector lls0Connector;

  @Mock private DomainHelperService domainHelperService;

  @Mock private DlmsConnection connection;

  @BeforeEach
  public void setUp() {
    this.factory =
        new DlmsConnectionFactory(
            this.hls5Connector, this.lls1Connector, this.lls0Connector, this.domainHelperService);
    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();
    this.task = t -> {};
  }

  //  @Test
  //  public void returnsConnectionManagerForHls5Device() throws Exception {
  //    final DlmsDevice device = new DlmsDeviceBuilder().withHls5Active(true).build();
  //    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();
  //    final boolean taskDone = false;
  //    final Consumer<DlmsConnectionManager> task = connection -> taskDone = true;
  //    when(this.hls5Connector.connect(this.messageMetadata, device, listener))
  //        .thenReturn(this.connection);
  //
  //    this.factory.handleConnection(this.messageMetadata, device, listener);
  //
  //    final DlmsConnectionManager expected =
  //        this.newConnectionManager(device, listener, this.hls5Connector);
  //    assertThat(result).isEqualToComparingFieldByField(expected);
  //    assertThat(result.getConnection()).isSameAs(this.connection);
  //  }

  @Test
  public void getConnection_throwsForHls4Device() throws Exception {

    final DlmsDevice device = new DlmsDeviceBuilder().withHls4Active(true).build();
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();

    assertThatExceptionOfType(FunctionalException.class)
        .isThrownBy(
            () -> {
              this.factory.handleConnection(this.messageMetadata, device, listener, this.task);
            });
  }

  @Test
  public void getConnection_throwsForHls3Device() throws Exception {
    final DlmsDevice device = new DlmsDeviceBuilder().withHls3Active(true).build();
    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();

    assertThatExceptionOfType(FunctionalException.class)
        .isThrownBy(
            () -> {
              this.factory.handleConnection(this.messageMetadata, device, listener, this.task);
            });
  }

  //  @Test
  //  public void returnsConnectionManagerForLls1Device() throws Exception {
  //    final DlmsDevice device = new DlmsDeviceBuilder().withLls1Active(true).build();
  //    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();
  //    when(this.lls1Connector.connect(this.messageMetadata, device, listener))
  //        .thenReturn(this.connection);
  //
  //    final DlmsConnectionManager result =
  //        this.factory.handleConnection(this.messageMetadata, device, listener);
  //
  //    final DlmsConnectionManager expected =
  //        this.newConnectionManager(device, listener, this.lls1Connector);
  //    assertThat(result).isEqualToComparingFieldByField(expected);
  //    assertThat(result.getConnection()).isSameAs(this.connection);
  //  }

  //  @Test
  //  public void returnsConnectionManagerForLls0Device() throws Exception {
  //    final DlmsDevice device = new DlmsDeviceBuilder().withLls1Active(false).build();
  //    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();
  //    when(this.lls0Connector.connect(this.messageMetadata, device, listener))
  //        .thenReturn(this.connection);
  //
  //    final DlmsConnectionManager result =
  //        this.factory.handleConnection(this.messageMetadata, device, listener);
  //
  //    final DlmsConnectionManager expected =
  //        this.newConnectionManager(device, listener, this.lls0Connector);
  //    assertThat(result).isEqualToComparingFieldByField(expected);
  //    assertThat(result.getConnection()).isSameAs(this.connection);
  //  }

  //  @Test
  //  public void returnsPublicClientConnectionManagerForDevice() throws Exception {
  //    final DlmsDevice device = new DlmsDeviceBuilder().withHls5Active(true).build();
  //    final DlmsMessageListener listener = new InvocationCountingDlmsMessageListener();
  //    when(this.lls0Connector.connect(this.messageMetadata, device, listener))
  //        .thenReturn(this.connection);
  //
  //    final DlmsConnectionManager result =
  //        this.factory.handlePublicClientConnection(this.messageMetadata, device, listener);
  //
  //    final DlmsConnectionManager expected =
  //        this.newConnectionManager(device, listener, this.lls0Connector);
  //    assertThat(result).isEqualToComparingFieldByField(expected);
  //    assertThat(result.getConnection()).isSameAs(this.connection);
  //  }

  private DlmsConnectionManager newConnectionManager(
      final DlmsDevice device, final DlmsMessageListener listener, final DlmsConnector connector)
      throws OsgpException {
    final DlmsConnectionManager connectionManager =
        new DlmsConnectionManager(
            connector, this.messageMetadata, device, listener, this.domainHelperService);
    connectionManager.connect();
    return connectionManager;
  }
}
