//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_DEVICE_IDENTIFICATION;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_DOMAIN;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_DOMAIN_VERSION;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_MESSAGE_TYPE;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.TestDefaults.DEFAULT_ORGANISATION_IDENTIFICATION;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.LogItemFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.ResponseMetadataFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DomainInfo;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.LogItem;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories.AsduFactory;

@ExtendWith(MockitoExtension.class)
class ClientConnectionEventListenerTest {

  private ClientConnectionEventListener clientConnectionEventListener;
  private ResponseMetadata responseMetadata;

  @Mock private ClientConnectionCache connectionCache;

  @Mock private ClientAsduHandlerRegistry asduHandlerRegistry;

  @Mock private ClientAsduHandler asduHandler;

  @Mock private LoggingService loggingService;

  @Mock private LogItemFactory logItemFactory;

  @Mock private ResponseMetadataFactory responseMetadataFactory;

  @BeforeEach
  public void setup() {
    this.responseMetadata =
        new ResponseMetadata.Builder()
            .withDeviceIdentification(DEFAULT_DEVICE_IDENTIFICATION)
            .withOrganisationIdentification(DEFAULT_ORGANISATION_IDENTIFICATION)
            .withDomainInfo(new DomainInfo(DEFAULT_DOMAIN, DEFAULT_DOMAIN_VERSION))
            .withMessageType(DEFAULT_MESSAGE_TYPE)
            .build();

    this.clientConnectionEventListener =
        new ClientConnectionEventListener.Builder()
            .withDeviceIdentification(DEFAULT_DEVICE_IDENTIFICATION)
            .withClientAsduHandlerRegistry(this.asduHandlerRegistry)
            .withClientConnectionCache(this.connectionCache)
            .withLoggingService(this.loggingService)
            .withLogItemFactory(this.logItemFactory)
            .withResponseMetadata(this.responseMetadata)
            .withResponseMetadataFactory(this.responseMetadataFactory)
            .build();
  }

  @Test
  void shouldHandleAsduWhenNewAsduIsReceived() throws Exception {
    // Arrange
    final ASdu asdu = AsduFactory.ofType(ASduType.C_IC_NA_1);
    when(this.asduHandlerRegistry.getHandler(asdu)).thenReturn(this.asduHandler);
    when(this.responseMetadataFactory.createWithNewCorrelationUid(this.responseMetadata))
        .thenReturn(this.responseMetadata);

    // Act
    this.clientConnectionEventListener.newASdu(asdu);

    // Assert
    verify(this.asduHandler).handleAsdu(asdu, this.responseMetadata);
  }

  @Test
  void shouldSendLogItemWhenNewAsduIsReceived() throws Exception {
    // Arrange
    final ASdu asdu = AsduFactory.ofType(ASduType.C_IC_NA_1);
    final LogItem logItem =
        new LogItem(
            DEFAULT_DEVICE_IDENTIFICATION,
            DEFAULT_ORGANISATION_IDENTIFICATION,
            true,
            asdu.toString());

    when(this.asduHandlerRegistry.getHandler(asdu)).thenReturn(this.asduHandler);
    when(this.responseMetadataFactory.createWithNewCorrelationUid(this.responseMetadata))
        .thenReturn(this.responseMetadata);
    when(this.logItemFactory.create(
            asdu, DEFAULT_DEVICE_IDENTIFICATION, DEFAULT_ORGANISATION_IDENTIFICATION, true))
        .thenReturn(logItem);

    // Act
    this.clientConnectionEventListener.newASdu(asdu);

    // Assert
    verify(this.loggingService).log(logItem);
  }

  @Test
  void shouldRemoveConnectionFromCacheWhenConnectionIsClosed() {
    // Arrange
    final IOException e = new IOException();

    // Act
    this.clientConnectionEventListener.connectionClosed(e);

    // Assert
    verify(this.connectionCache).removeConnection(DEFAULT_DEVICE_IDENTIFICATION);
  }
}
