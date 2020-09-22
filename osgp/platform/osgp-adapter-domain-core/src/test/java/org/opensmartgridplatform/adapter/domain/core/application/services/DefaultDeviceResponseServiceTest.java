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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public void testDefaultDeviseResponseWithNotOkTypeAndException() {
    	//Arrange
    	CorrelationIds ids = new CorrelationIds("orginazationTestId", "deviceIdTest", "correlationUid");
    	String messageType = "Warning";
    	int messagePriority = 3;
    	ResponseMessageResultType deviceResult = ResponseMessageResultType.NOT_OK;
    	OsgpException exception = new OsgpException(ComponentType.DOMAIN_CORE, "There was an exception");
    	
    	//Act
    	this.defaultDeviceResponseService.handleDefaultDeviceResponse(ids, messageType, messagePriority, deviceResult, exception);
    	
    	ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
    	verify(webServiceResponseMessageSender).send(argument.capture());

    	//Assert
    	assertEquals("orginazationTestId", argument.getValue().getOrganisationIdentification());
    	assertEquals("deviceIdTest", argument.getValue().getDeviceIdentification());
    	assertEquals("correlationUid", argument.getValue().getCorrelationUid());
    	assertEquals(3, argument.getValue().getMessagePriority());
    	assertEquals(ResponseMessageResultType.NOT_OK, argument.getValue().getResult());
    	assertEquals(exception, argument.getValue().getOsgpException());
    }
    
    @Test
    public void testDefaultDeviseResponseWithNotOkTypeAndNoException2() {
    	//Arrange
    	CorrelationIds correlationIds = new CorrelationIds("orginazationTestId", "deviceIdTest", "correlationUid");
    	String messageType = "Warning";
    	int messagePriority = 3;
    	ResponseMessageResultType deviceResult = ResponseMessageResultType.NOT_OK;
    	OsgpException exception = null;
    	OsgpException osgpException = new TechnicalException(ComponentType.DOMAIN_CORE, "An unknown error occurred");
    	
    	this.defaultDeviceResponseService.handleDefaultDeviceResponse(correlationIds, messageType, messagePriority, deviceResult, exception);
    	//Act
    	ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
    	verify(webServiceResponseMessageSender).send(argument.capture());

    	//Assert
    	assertEquals("orginazationTestId", argument.getValue().getOrganisationIdentification());
    	assertEquals("deviceIdTest", argument.getValue().getDeviceIdentification());
    	assertEquals("correlationUid", argument.getValue().getCorrelationUid());
    	assertEquals(3, argument.getValue().getMessagePriority());
    	assertEquals(ResponseMessageResultType.NOT_OK, argument.getValue().getResult());
    	assertEquals(osgpException.getComponentType(), argument.getValue().getOsgpException().getComponentType());
    }
    
    @Test
    public void testDefaultDeviseResponseWithOkTypeAndException() {
    	//Arrange
    	CorrelationIds correlationIds = new CorrelationIds("orginazationTestId", "deviceIdTest", "correlationUid");
    	String messageType = "Warning";
    	int messagePriority = 3;
    	ResponseMessageResultType deviceResult = ResponseMessageResultType.OK;
    	OsgpException exception = new OsgpException(ComponentType.DOMAIN_CORE, "There was an exception");
    	
    	//Act
    	this.defaultDeviceResponseService.handleDefaultDeviceResponse(correlationIds, messageType, messagePriority, deviceResult, exception);
    
    	ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
    	verify(webServiceResponseMessageSender).send(argument.capture());

    	//Assert
    	assertEquals("orginazationTestId", argument.getValue().getOrganisationIdentification());
    	assertEquals("deviceIdTest", argument.getValue().getDeviceIdentification());
    	assertEquals("correlationUid", argument.getValue().getCorrelationUid());
    	assertEquals(3, argument.getValue().getMessagePriority());
    	assertEquals(ResponseMessageResultType.NOT_OK, argument.getValue().getResult());
    	assertEquals(exception, argument.getValue().getOsgpException());
    }
    
    @Test
    public void testDefaultDeviseResponseWithOkTypeAndNoException() {
    	//Arrange
    	CorrelationIds correlationIds = new CorrelationIds("orginazationTestId", "deviceIdTest", "correlationUid");
    	String messageType = "Warning";
    	int messagePriority = 3;
    	ResponseMessageResultType deviceResult = ResponseMessageResultType.OK;
    	OsgpException exception = null;
    	
    	//Act
    	this.defaultDeviceResponseService.handleDefaultDeviceResponse(correlationIds, messageType, messagePriority, deviceResult, exception);
    
    	ArgumentCaptor<ResponseMessage> argument = ArgumentCaptor.forClass(ResponseMessage.class);
    	verify(webServiceResponseMessageSender).send(argument.capture());

    	//Assert
    	assertEquals("orginazationTestId", argument.getValue().getOrganisationIdentification());
    	assertEquals("deviceIdTest", argument.getValue().getDeviceIdentification());
    	assertEquals("correlationUid", argument.getValue().getCorrelationUid());
    	assertEquals(3, argument.getValue().getMessagePriority());
    	assertEquals(ResponseMessageResultType.OK, argument.getValue().getResult());
    	assertEquals(exception, argument.getValue().getOsgpException());
    }
}
