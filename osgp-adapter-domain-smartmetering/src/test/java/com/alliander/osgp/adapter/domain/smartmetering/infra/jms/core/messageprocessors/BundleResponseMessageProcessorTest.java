package com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.alliander.osgp.dto.valueobjects.smartmetering.FaultResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.FaultResponseParameterDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.OsgpResultTypeDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

public class BundleResponseMessageProcessorTest {

    final BundleResponseMessageProcessor processor = new BundleResponseMessageProcessor();
    final List<FaultResponseParameterDto> parameters = new ArrayList<>();
    final String defaultMessage = "some error occurred";
    final ComponentType defaultComponent = ComponentType.DOMAIN_SMART_METERING;

    @Test
    public void functionalExceptionDetailsAreIncludedInFaultResponse() throws Exception {

        final FunctionalExceptionType functionalException = FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION;
        final ComponentType component = ComponentType.DOMAIN_SMART_METERING;
        final String message = "No Action Value Response Object for Action Value Response DTO Object of class: com.alliander.osgp.dto.valueobjects.smartmetering.NonExistentResponseDto";
        final Throwable cause = new RuntimeException(message);
        final Exception exception = new FunctionalException(functionalException, component, cause);

        final FaultResponseDto faultResponse = this.processor.faultResponseForException(exception, null,
                this.defaultMessage);

        this.assertResponse(faultResponse, functionalException.getCode(), functionalException.name(), component.name(),
                cause.getClass().getName(), message, this.parameters);
    }

    @Test
    public void technicalExceptionDetailsAreIncludedInFaultResponse() throws Exception {

        final FunctionalExceptionType functionalException = FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION;
        final ComponentType component = ComponentType.PROTOCOL_DLMS;
        final String message = "java.net.ConnectException: Connection refused";
        final Throwable cause = new RuntimeException(message);
        final Exception exception = new FunctionalException(functionalException, component, cause);

        this.parameters.add(new FaultResponseParameterDto("deviceIdentification", "ESIM9999999999999"));

        final FaultResponseDto faultResponse = this.processor.faultResponseForException(exception, this.parameters,
                this.defaultMessage);

        this.assertResponse(faultResponse, functionalException.getCode(), functionalException.name(), component.name(),
                cause.getClass().getName(), message, this.parameters);
    }

    @Test
    public void technicalExceptionDetailsWithoutCauseOrMessageInFaultResponse() throws Exception {

        final ComponentType component = ComponentType.PROTOCOL_DLMS;
        final Exception exception = new TechnicalException(component, null, null);

        final FaultResponseDto faultResponse = this.processor.faultResponseForException(exception, this.parameters,
                this.defaultMessage);

        this.assertResponse(faultResponse, null, this.defaultMessage, component.name(), null, null, this.parameters);
    }

    @Test
    public void exceptionDetailsAreIncludedInFaultResponse() throws Exception {

        final String message = "general exception";
        final Exception exception = new RuntimeException(message);

        final FaultResponseDto faultResponse = this.processor.faultResponseForException(exception, this.parameters,
                this.defaultMessage);

        this.assertResponse(faultResponse, null, this.defaultMessage, this.defaultComponent.name(),
                exception.getClass().getName(), message, this.parameters);
    }

    public void assertResponse(final FaultResponseDto actualResponse, final Integer expectedCode,
            final String expectedMessage, final String expectedComponent, final String expectedInnerException,
            final String expectedInnerMessage, final List<FaultResponseParameterDto> expectedParameterList) {

        assertNotNull("faultResponse", actualResponse);

        /*
         * Fault Response should not contain the result fields for a generic
         * Action Response, and the result should always be NOT OK.
         */
        assertNull("exception", actualResponse.getException());
        assertNull("resultString", actualResponse.getResultString());
        assertSame("result", OsgpResultTypeDto.NOT_OK, actualResponse.getResult());

        assertEquals("code", expectedCode, actualResponse.getCode());
        assertEquals("message", expectedMessage, actualResponse.getMessage());
        assertEquals("component", expectedComponent, actualResponse.getComponent());
        assertEquals("innerException", expectedInnerException, actualResponse.getInnerException());
        assertEquals("innerMessage", expectedInnerMessage, actualResponse.getInnerMessage());

        if (expectedParameterList == null || expectedParameterList.isEmpty()) {
            assertNull("parameters", actualResponse.getFaultResponseParameters());
        } else {
            assertNotNull("parameters", actualResponse.getFaultResponseParameters());
            final List<FaultResponseParameterDto> actualParameterList = actualResponse.getFaultResponseParameters()
                    .getParameterList();
            assertNotNull("parameter list", actualParameterList);
            final int numberOfParameters = expectedParameterList.size();
            assertEquals("number of parameters", numberOfParameters, actualParameterList.size());
            for (int i = 0; i < numberOfParameters; i++) {
                final FaultResponseParameterDto expectedParameter = expectedParameterList.get(i);
                final FaultResponseParameterDto actualParameter = actualParameterList.get(i);
                final int parameterNumber = i + 1;
                assertEquals("parameter key " + parameterNumber, expectedParameter.getKey(), actualParameter.getKey());
                assertEquals("parameter value " + parameterNumber, expectedParameter.getValue(),
                        actualParameter.getValue());
            }
        }
    }
}
