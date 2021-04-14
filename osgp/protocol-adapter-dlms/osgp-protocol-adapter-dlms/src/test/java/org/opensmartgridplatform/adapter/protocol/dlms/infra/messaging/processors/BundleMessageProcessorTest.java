/*
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.BundleService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ThrottlingService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDeviceBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.OsgpExceptionConverter;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.RetryHeaderFactory;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BundleMessagesRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;

@ExtendWith(MockitoExtension.class)
class BundleMessageProcessorTest {

  @Mock private ObjectMessage message;

  @Mock private OsgpExceptionConverter osgpExceptionConverter;

  @Mock private RetryHeaderFactory retryHeaderFactory;

  @Mock private DeviceResponseMessageSender responseMessageSender;

  @Mock private DomainHelperService domainHelperService;

  @Mock private ThrottlingService throttlingService;

  @Mock private DlmsConnectionHelper dlmsConnectionHelper;

  @Mock private BundleService bundleService;

  @Mock private DlmsConnectionManager dlmsConnectionManager;

  @Mock private DlmsMessageListener messageListener;

  private DlmsDevice dlmsDevice;
  private BundleMessagesRequestDto requestDto;

  @InjectMocks private BundleMessageProcessor messageProcessor;

  @BeforeEach
  public void setUp() throws JMSException, OsgpException {
    this.dlmsDevice = new DlmsDeviceBuilder().withHls5Active(true).build();
    this.requestDto = new BundleMessagesRequestDto(null);

    when(this.message.getJMSType()).thenReturn(MessageType.FIND_EVENTS.name());
    when(this.message.getObject()).thenReturn(this.requestDto);
    when(this.domainHelperService.findDlmsDevice(any(MessageMetadata.class)))
        .thenReturn(this.dlmsDevice);
  }

  @Test
  public void shouldSetEmptyHeaderOnSuccessfulOperation() throws OsgpException, JMSException {
    when(this.dlmsConnectionManager.getDlmsMessageListener()).thenReturn(this.messageListener);
    when(this.bundleService.callExecutors(
            this.dlmsConnectionManager, this.dlmsDevice, this.requestDto))
        .thenReturn(this.requestDto);
    when(this.dlmsConnectionHelper.createConnectionForDevice(this.dlmsDevice, null))
        .thenReturn(this.dlmsConnectionManager);

    this.messageProcessor.processMessage(this.message);

    verify(this.retryHeaderFactory).createEmtpyRetryHeader();
  }

  @Test
  public void shouldSetRetryHeaderOnRuntimeException() throws OsgpException, JMSException {
    when(this.dlmsConnectionHelper.createConnectionForDevice(this.dlmsDevice, null))
        .thenThrow(new RuntimeException());

    this.messageProcessor.processMessage(this.message);

    verify(this.retryHeaderFactory).createRetryHeader(0);
  }

  @Test
  public void shouldSetRetryHeaderOnOsgpException() throws OsgpException, JMSException {
    when(this.dlmsConnectionHelper.createConnectionForDevice(this.dlmsDevice, null))
        .thenThrow(new OsgpException(ComponentType.PROTOCOL_DLMS, ""));

    this.messageProcessor.processMessage(this.message);

    verify(this.retryHeaderFactory).createRetryHeader(0);
  }
}
