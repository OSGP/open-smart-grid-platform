//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.application.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseParameterDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.OsgpResultTypeDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;

class FaultResponseFactoryTest {

  final FaultResponseFactory faultResponseFactory = new FaultResponseFactory();
  final List<FaultResponseParameterDto> parameters = new ArrayList<>();
  final String defaultMessage = "some error occurred";
  final ComponentType defaultComponent = ComponentType.DOMAIN_SMART_METERING;

  @Test
  void functionalExceptionDetailsAreIncludedInFaultResponse() throws Exception {

    final FunctionalExceptionType functionalException =
        FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION;
    final ComponentType component = ComponentType.DOMAIN_SMART_METERING;
    final String message =
        "No Action Value Response Object for Action Value Response DTO Object of class: org.opensmartgridplatform.dto.valueobjects.smartmetering.NonExistentResponseDto";
    final Throwable cause = new RuntimeException(message);
    final Exception exception = new FunctionalException(functionalException, component, cause);

    final FaultResponseDto faultResponse =
        this.faultResponseFactory.faultResponseForException(exception, null, this.defaultMessage);

    this.assertResponse(
        faultResponse,
        functionalException.getCode(),
        functionalException.name(),
        component.name(),
        cause.getClass().getName(),
        message,
        this.parameters,
        true);
  }

  @Test
  void nonRetryableException() throws Exception {

    final FunctionalExceptionType functionalException =
        FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION;
    final ComponentType component = ComponentType.DOMAIN_SMART_METERING;
    final String message =
        "No Action Value Response Object for Action Value Response DTO Object of class: org.opensmartgridplatform.dto.valueobjects.smartmetering.NonExistentResponseDto";
    final Throwable cause = new RuntimeException(message);
    final Exception exception = new FunctionalException(functionalException, component, cause);

    final FaultResponseDto faultResponse =
        this.faultResponseFactory.nonRetryablefaultResponseForException(
            exception, null, this.defaultMessage);

    this.assertResponse(
        faultResponse,
        functionalException.getCode(),
        functionalException.name(),
        component.name(),
        cause.getClass().getName(),
        message,
        this.parameters,
        false);
  }

  @Test
  void technicalExceptionDetailsAreIncludedInFaultResponse() throws Exception {

    final FunctionalExceptionType functionalException =
        FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION;
    final ComponentType component = ComponentType.PROTOCOL_DLMS;
    final String message = "java.net.ConnectException: Connection refused";
    final Throwable cause = new RuntimeException(message);
    final Exception exception = new FunctionalException(functionalException, component, cause);

    this.parameters.add(new FaultResponseParameterDto("deviceIdentification", "ESIM9999999999999"));

    final FaultResponseDto faultResponse =
        this.faultResponseFactory.faultResponseForException(
            exception, this.parameters, this.defaultMessage);

    this.assertResponse(
        faultResponse,
        functionalException.getCode(),
        functionalException.name(),
        component.name(),
        cause.getClass().getName(),
        message,
        this.parameters,
        true);
  }

  @Test
  void technicalExceptionDetailsWithoutCauseOrMessageInFaultResponse() throws Exception {

    final ComponentType component = ComponentType.PROTOCOL_DLMS;
    final Exception exception = new TechnicalException(component, null, null);

    final FaultResponseDto faultResponse =
        this.faultResponseFactory.faultResponseForException(
            exception, this.parameters, this.defaultMessage);

    this.assertResponse(
        faultResponse,
        null,
        this.defaultMessage,
        component.name(),
        null,
        null,
        this.parameters,
        true);
  }

  @Test
  void exceptionDetailsAreIncludedInFaultResponse() throws Exception {

    final String message = "general exception";
    final Exception exception = new RuntimeException(message);

    final FaultResponseDto faultResponse =
        this.faultResponseFactory.faultResponseForException(
            exception, this.parameters, this.defaultMessage);

    this.assertResponse(
        faultResponse,
        null,
        this.defaultMessage,
        this.defaultComponent.name(),
        exception.getClass().getName(),
        message,
        this.parameters,
        true);
  }

  public void assertResponse(
      final FaultResponseDto actualResponse,
      final Integer expectedCode,
      final String expectedMessage,
      final String expectedComponent,
      final String expectedInnerException,
      final String expectedInnerMessage,
      final List<FaultResponseParameterDto> expectedParameterList,
      final boolean expectedRetryable) {

    assertThat(actualResponse).withFailMessage("faultResponse").isNotNull();

    /*
     * Fault Response should not contain the result fields for a generic
     * Action Response, and the result should always be NOT OK.
     */
    assertThat(actualResponse.getException()).withFailMessage("exception").isNull();
    assertThat(actualResponse.getResultString()).withFailMessage("resultString").isNull();
    assertThat(actualResponse.getResult())
        .withFailMessage("result")
        .isEqualTo(OsgpResultTypeDto.NOT_OK);

    assertThat(actualResponse.getCode()).withFailMessage("code").isEqualTo(expectedCode);
    assertThat(actualResponse.getMessage()).withFailMessage("message").isEqualTo(expectedMessage);
    assertThat(actualResponse.getComponent())
        .withFailMessage("component")
        .isEqualTo(expectedComponent);
    assertThat(actualResponse.getInnerException())
        .withFailMessage("innerException")
        .isEqualTo(expectedInnerException);
    assertThat(actualResponse.getInnerMessage())
        .withFailMessage("innerMessage")
        .isEqualTo(expectedInnerMessage);

    assertThat(actualResponse.isRetryable())
        .withFailMessage("retryable")
        .isEqualTo(expectedRetryable);

    if (expectedParameterList == null || expectedParameterList.isEmpty()) {
      assertThat(actualResponse.getFaultResponseParameters())
          .withFailMessage("parameters")
          .isNull();
    } else {
      assertThat(actualResponse.getFaultResponseParameters())
          .withFailMessage("parameters")
          .isNotNull();
      final List<FaultResponseParameterDto> actualParameterList =
          actualResponse.getFaultResponseParameters().getParameterList();
      assertThat(actualParameterList).withFailMessage("parameter list").isNotNull();
      final int numberOfParameters = expectedParameterList.size();
      assertThat(actualParameterList.size())
          .withFailMessage("number of parameters")
          .isEqualTo(numberOfParameters);
      for (int i = 0; i < numberOfParameters; i++) {
        final FaultResponseParameterDto expectedParameter = expectedParameterList.get(i);
        final FaultResponseParameterDto actualParameter = actualParameterList.get(i);
        final int parameterNumber = i + 1;
        assertThat(actualParameter.getKey())
            .withFailMessage("parameter key " + parameterNumber)
            .isEqualTo(expectedParameter.getKey());
        assertThat(actualParameter.getValue())
            .withFailMessage("parameter value " + parameterNumber)
            .isEqualTo(expectedParameter.getValue());
      }
    }
  }
}
