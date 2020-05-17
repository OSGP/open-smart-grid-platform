/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.da.infra.jms.core.messageprocessors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opensmartgridplatform.adapter.domain.da.application.services.AdHocManagementService;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

class GetDataResponseMessageProcessorTest {

    @InjectMocks
    private GetDataResponseMessageProcessor getDataResponseMessageProcessor;

    @Mock
    private AdHocManagementService adHocManagementService;

    @Mock
    private ObjectMessage receivedMessage;

    @BeforeEach
    public void setup() {
        this.getDataResponseMessageProcessor = new GetDataResponseMessageProcessor(null, null);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void handlesResponseMessageValuesTest() throws JMSException {
        // Arrange
        final String correlationUid = "CorrelationID-1";
        final String messageType = "GET_DATA";
        final String organisationIdentification = "test-org";
        final String deviceIdentification = "device1";

        final ResponseMessage responseMessage = new ResponseMessage.Builder().withCorrelationUid(correlationUid)
                .withMessageType(messageType)
                .withOrganisationIdentification(organisationIdentification)
                .withDeviceIdentification(deviceIdentification)
                .withResult(ResponseMessageResultType.OK)
                .withDataObject("the payload")
                .build();

        when(this.receivedMessage.getJMSCorrelationID()).thenReturn(correlationUid);
        when(this.receivedMessage.getJMSType()).thenReturn(messageType);
        when(this.receivedMessage.getStringProperty(Constants.ORGANISATION_IDENTIFICATION))
                .thenReturn(organisationIdentification);
        when(this.receivedMessage.getStringProperty(Constants.DEVICE_IDENTIFICATION)).thenReturn(deviceIdentification);
        when(this.receivedMessage.getObject()).thenReturn(responseMessage);

        final ArgumentCaptor<ResponseMessage> argumentCaptor = ArgumentCaptor.forClass(ResponseMessage.class);

        // Act
        this.getDataResponseMessageProcessor.processMessage(this.receivedMessage);

        // Assert
        verify(this.adHocManagementService, times(1)).handleGetDataResponse(argumentCaptor.capture());

        final ResponseMessage capturedArgument = argumentCaptor.getValue();
        assertThat(capturedArgument).isEqualToComparingFieldByField(responseMessage);
    }

    @Test
    void noResponseHandledWhenJmsExceptionOccursTest() throws JMSException {
        // Arrange
        when(this.receivedMessage.getObject()).thenThrow(new JMSException("unit test throws this JMSException"));

        // Act
        this.getDataResponseMessageProcessor.processMessage(this.receivedMessage);

        // Assert
        verify(this.adHocManagementService, never()).handleGetDataResponse(any());
    }

}
