/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.DlmsConnection;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class DlmsConnectionManagerTest {
  @Mock DlmsConnector connector;
  @Mock MessageMetadata messageMetadata;
  @Mock DlmsDevice device;
  @Mock DlmsMessageListener dlmsMessageListener;
  @Mock DomainHelperService domainHelperService;
  @InjectMocks DlmsConnectionManager manager;

  @BeforeEach
  void setUp() {
    this.manager =
        new DlmsConnectionManager(
            this.connector,
            this.messageMetadata,
            this.device,
            this.dlmsMessageListener,
            this.domainHelperService);
  }

  @Test
  void connectAndClose() throws OsgpException {
    final DlmsConnection dlmsConnection = mock(DlmsConnection.class);
    when(this.connector.connect(this.messageMetadata, this.device, this.dlmsMessageListener))
        .thenReturn(dlmsConnection);
    this.manager.connect();

    assertThat(this.manager.getConnection()).isEqualTo(dlmsConnection);

    this.manager.close();

    assertThat(this.manager.isConnected()).isFalse();
  }

  @Test
  void closeWithoutConnectionShouldNotFail() throws OsgpException {
    this.manager.close();

    assertThat(this.manager.isConnected()).isFalse();
  }

  @Test
  void closeWithIOExceptionShouldNotFail() throws OsgpException, IOException {
    final DlmsConnection dlmsConnection = mock(DlmsConnection.class);
    when(this.connector.connect(this.messageMetadata, this.device, this.dlmsMessageListener))
        .thenReturn(dlmsConnection);
    this.manager.connect();
    assertThat(this.manager.getConnection()).isEqualTo(dlmsConnection);

    doThrow(new IOException()).when(dlmsConnection).close();
    this.manager.close();

    assertThat(this.manager.isConnected()).isFalse();
  }
}
