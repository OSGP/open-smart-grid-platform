/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.domain.core.application.services;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;

import org.junit.jupiter.api.Test;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DefaultDeviceResponseServiceTest {
	
    @Mock
    private WebServiceResponseMessageSender webServiceResponseMessageSender;
    
    @InjectMocks
    private DefaultDeviceResponseService defaultDeviceResponseService;
    
    @Test
    public void testDefaultDeviceResponseWithNotOkTypeAndException() {
    	//Arrange
    	CorrelationIds ids = new CorrelationIds("orginazationTestId", "deviceIdTest", "correlationUid");
    	String messageType = "Warning";
    	int messagePriority = 3;
    	ResponseMessageResultType deviceResult = ResponseMessageResultType.NOT_OK;
    	OsgpException exception = new OsgpException(ComponentType.DOMAIN_CORE, "There was an exception");

		ResponseMessage expectedResponseMessage = ResponseMessage.newResponseMessageBuilder()
				.withIds(ids)
				.withResult(ResponseMessageResultType.NOT_OK)
				.withOsgpException(exception)
				.withMessagePriority(messagePriority)
				.build();
    	
    	//Act
    	this.defaultDeviceResponseService.handleDefaultDeviceResponse(ids, messageType, messagePriority, deviceResult, exception);
    	
    	ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
    	verify(webServiceResponseMessageSender).send(argument.capture());

    	//Assert
		assertThat(argument.getValue()).usingRecursiveComparison().isEqualTo(expectedResponseMessage);
    }
    
    @Test
    public void testDefaultDeviceResponseWithNotOkTypeAndNoException2() {
    	//Arrange
    	CorrelationIds ids = new CorrelationIds("orginazationTestId", "deviceIdTest", "correlationUid");
    	String messageType = "Warning";
    	int messagePriority = 3;
    	ResponseMessageResultType deviceResult = ResponseMessageResultType.NOT_OK;
    	OsgpException exception = null;
    	OsgpException osgpException = new TechnicalException(ComponentType.DOMAIN_CORE, "An unknown error occurred");

		ResponseMessage expectedResponseMessage = ResponseMessage.newResponseMessageBuilder()
				.withIds(ids)
				.withResult(ResponseMessageResultType.NOT_OK)
				.withOsgpException(osgpException)
				.withMessagePriority(messagePriority)
				.build();
    	
    	this.defaultDeviceResponseService.handleDefaultDeviceResponse(ids, messageType, messagePriority, deviceResult, exception);
    	//Act
    	ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
    	verify(webServiceResponseMessageSender).send(argument.capture());

    	//Assert
		assertThat(argument.getValue()).usingRecursiveComparison().isEqualTo(expectedResponseMessage);
    }
    
    @Test
    public void testDefaultDeviceResponseWithOkTypeAndException() {
    	//Arrange
    	CorrelationIds ids = new CorrelationIds("orginazationTestId", "deviceIdTest", "correlationUid");
    	String messageType = "Warning";
    	int messagePriority = 3;
    	ResponseMessageResultType deviceResult = ResponseMessageResultType.OK;
    	OsgpException exception = new OsgpException(ComponentType.DOMAIN_CORE, "There was an exception");

		ResponseMessage expectedResponseMessage = ResponseMessage.newResponseMessageBuilder()
				.withIds(ids)
				.withResult(ResponseMessageResultType.NOT_OK)
				.withOsgpException(exception)
				.withMessagePriority(messagePriority)
				.build();


		//Act
    	this.defaultDeviceResponseService.handleDefaultDeviceResponse(ids, messageType, messagePriority, deviceResult, exception);
    
    	ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
    	verify(webServiceResponseMessageSender).send(argument.capture());

    	//Assert
		assertThat(argument.getValue()).usingRecursiveComparison().isEqualTo(expectedResponseMessage);
    }
    
    @Test
    public void testDefaultDeviceResponseWithOkTypeAndNoException() {
    	//Arrange
    	CorrelationIds ids = new CorrelationIds("orginazationTestId", "deviceIdTest", "correlationUid");
    	String messageType = "Warning";
    	int messagePriority = 3;
    	ResponseMessageResultType deviceResult = ResponseMessageResultType.OK;
    	OsgpException exception = null;

		ResponseMessage expectedResponseMessage = ResponseMessage.newResponseMessageBuilder()
				.withIds(ids)
				.withResult(ResponseMessageResultType.OK)
				.withOsgpException(exception)
				.withMessagePriority(messagePriority)
				.build();

		//Act
    	this.defaultDeviceResponseService.handleDefaultDeviceResponse(ids, messageType, messagePriority, deviceResult, exception);
    
    	ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
    	verify(webServiceResponseMessageSender).send(argument.capture());

    	//Assert
		assertThat(argument.getValue()).usingRecursiveComparison().isEqualTo(expectedResponseMessage);
    }
}
