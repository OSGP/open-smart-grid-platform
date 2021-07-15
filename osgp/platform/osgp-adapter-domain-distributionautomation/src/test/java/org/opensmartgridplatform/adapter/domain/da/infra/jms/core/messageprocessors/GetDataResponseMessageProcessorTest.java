/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.da.infra.jms.core.messageprocessors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opensmartgridplatform.adapter.domain.da.application.services.AdHocManagementService;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

public class GetDataResponseMessageProcessorTest {

  @InjectMocks private GetDataResponseMessageProcessor getDataResponseMessageProcessor;

  @Mock private AdHocManagementService adHocManagementService;

  @Mock private ObjectMessage receivedMessage;

  @BeforeEach
  public void setup() {
    this.getDataResponseMessageProcessor = new GetDataResponseMessageProcessor(null, null);
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void handlesResponseMessageValuesTest() throws JMSException {
    // Arrange
    final String correlationUid = "CorrelationID-1";
    final MessageType messageType = MessageType.GET_DATA;
    final String organisationIdentification = "test-org";
    final String deviceIdentification = "device1";

    final ResponseMessage responseMessage =
        new ResponseMessage.Builder()
            .withCorrelationUid(correlationUid)
            .withMessageType(messageType.toString())
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(deviceIdentification)
            .withResult(ResponseMessageResultType.OK)
            .withDataObject("the payload")
            .build();

    when(this.receivedMessage.getObject()).thenReturn(responseMessage);

    // Act
    this.getDataResponseMessageProcessor.processMessage(this.receivedMessage);

    // Assert
    verify(this.adHocManagementService, times(1))
        .handleGetDataResponse(responseMessage, messageType);
  }

  @Test
  void noResponseHandledWhenJmsExceptionOccursTest() throws JMSException {
    // Arrange
    when(this.receivedMessage.getObject())
        .thenThrow(new JMSException("unit test throws this JMSException"));

    // Act
    this.getDataResponseMessageProcessor.processMessage(this.receivedMessage);

    // Assert
    verify(this.adHocManagementService, never()).handleGetDataResponse(any(), any());
  }
}
