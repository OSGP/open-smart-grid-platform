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
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.application.config.ThrottlingConfig;
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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BundleMessagesRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClearAlarmRegisterRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.throttling.api.Permit;

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

  @Mock private ThrottlingConfig throttlingConfig;

  private DlmsDevice dlmsDevice;

  @InjectMocks private BundleMessageProcessor messageProcessor;

  @BeforeEach
  public void setUp() throws OsgpException {
    this.dlmsDevice = new DlmsDeviceBuilder().withHls5Active(true).build();
    when(this.domainHelperService.findDlmsDevice(any(MessageMetadata.class)))
        .thenReturn(this.dlmsDevice);
    when(this.throttlingConfig.clientEnabled()).thenReturn(false);
  }

  @Test
  void shouldSetEmptyHeaderOnSuccessfulOperation() throws OsgpException, JMSException {
    this.prepareBundleServiceMockWithRequestAndResponse(new ActionResponseDto());
    when(this.dlmsConnectionManager.getDlmsMessageListener()).thenReturn(this.messageListener);
    when(this.dlmsConnectionHelper.createConnectionForDevice(
            any(MessageMetadata.class),
            same(this.dlmsDevice),
            nullable(DlmsMessageListener.class),
            nullable(Permit.class)))
        .thenReturn(this.dlmsConnectionManager);

    this.messageProcessor.processMessage(this.message);

    verify(this.retryHeaderFactory, times(1)).createEmptyRetryHeader();
  }

  @Test
  void shouldSetRetryHeaderOnRuntimeException() throws OsgpException, JMSException {
    when(this.dlmsConnectionHelper.createConnectionForDevice(
            any(MessageMetadata.class), same(this.dlmsDevice), nullable(DlmsMessageListener.class)))
        .thenThrow(new RuntimeException());

    this.messageProcessor.processMessage(this.message);

    verify(this.retryHeaderFactory).createRetryHeader(0);
  }

  @Test
  void shouldSetRetryHeaderOnOsgpException() throws OsgpException, JMSException {
    when(this.dlmsConnectionHelper.createConnectionForDevice(
            any(MessageMetadata.class), same(this.dlmsDevice), nullable(DlmsMessageListener.class)))
        .thenThrow(new OsgpException(ComponentType.PROTOCOL_DLMS, ""));

    this.messageProcessor.processMessage(this.message);

    verify(this.retryHeaderFactory).createRetryHeader(0);
  }

  @Test
  void shouldSetRetryHeaderOnSuccessfulOperationWithRetryableFaultResponse()
      throws OsgpException, JMSException {
    when(this.dlmsConnectionManager.getDlmsMessageListener()).thenReturn(this.messageListener);
    when(this.dlmsConnectionHelper.createConnectionForDevice(
            any(MessageMetadata.class), same(this.dlmsDevice), nullable(DlmsMessageListener.class)))
        .thenReturn(this.dlmsConnectionManager);

    this.prepareBundleServiceMockWithRequestAndResponse(
        new FaultResponseDto.Builder().withRetryable(true).build());

    this.messageProcessor.processMessage(this.message);

    verify(this.retryHeaderFactory).createRetryHeader(0);
  }

  @Test
  void shouldSetEmptyHeaderOnSuccessfulOperationWithNonRetryableFaultResponse()
      throws OsgpException, JMSException {
    when(this.dlmsConnectionManager.getDlmsMessageListener()).thenReturn(this.messageListener);
    when(this.dlmsConnectionHelper.createConnectionForDevice(
            any(MessageMetadata.class),
            same(this.dlmsDevice),
            nullable(DlmsMessageListener.class),
            nullable(Permit.class)))
        .thenReturn(this.dlmsConnectionManager);

    this.prepareBundleServiceMockWithRequestAndResponse(
        new FaultResponseDto.Builder().withRetryable(false).build());

    this.messageProcessor.processMessage(this.message);

    verify(this.retryHeaderFactory).createEmptyRetryHeader();
  }

  private void prepareBundleServiceMockWithRequestAndResponse(final ActionResponseDto response)
      throws JMSException {
    final ActionDto action = new ActionDto(new ClearAlarmRegisterRequestDto());
    action.setResponse(response);
    final BundleMessagesRequestDto request = new BundleMessagesRequestDto(Arrays.asList(action));

    when(this.message.getJMSType()).thenReturn(MessageType.CLEAR_ALARM_REGISTER.name());
    when(this.message.getObject()).thenReturn(request);

    when(this.bundleService.callExecutors(
            same(this.dlmsConnectionManager),
            same(this.dlmsDevice),
            same(request),
            any(MessageMetadata.class)))
        .thenReturn(request);
  }
}
