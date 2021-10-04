/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class InvocationCounterManagerTest {
  private static final AttributeAddress ATTRIBUTE_ADDRESS_INVOCATION_COUNTER_VALUE =
      new AttributeAddress(1, new ObisCode(new byte[] {0, 0, 43, 1, 0, -1}), 2);

  private InvocationCounterManager manager;
  private MessageMetadata messageMetadata;
  private Consumer<DlmsConnectionManager> task;

  @Mock private DlmsConnectionFactory connectionFactory;

  @Mock private DlmsHelper dlmsHelper;

  @BeforeEach
  public void setUp() {
    this.manager = new InvocationCounterManager(this.connectionFactory, this.dlmsHelper);
    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();
    this.task = t -> {};
  }

  //  @Test
  //  void initializesInvocationCounterForDevice() throws Exception {
  //    final long invocationCounterValueInDatabaseEntity = 7;
  //    final long invocationCounterValueOnDevice = 123;
  //
  //    final DlmsDevice device =
  //        new DlmsDeviceBuilder()
  //            .withInvocationCounter(invocationCounterValueInDatabaseEntity)
  //            .build();
  //
  //    //    final DlmsConnectionManager connectionManager = mock(DlmsConnectionManager.class);
  //    doNothing()
  //        .when(this.connectionFactory)
  //        .handlePublicClientConnection(eq(this.messageMetadata), eq(device), isNull(), any());
  //
  //    final DataObject dataObject = DataObject.newUInteger32Data(invocationCounterValueOnDevice);
  //    when(this.dlmsHelper.getAttributeValue(
  //            any(), refEq(ATTRIBUTE_ADDRESS_INVOCATION_COUNTER_VALUE)))
  //        .thenReturn(dataObject);
  //
  //    this.manager.initializeInvocationCounter(this.messageMetadata, device);
  //
  //    assertThat(device.getInvocationCounter()).isEqualTo(invocationCounterValueOnDevice);
  //
  //    //    verify(connectionManager).close();
  //  }
}
