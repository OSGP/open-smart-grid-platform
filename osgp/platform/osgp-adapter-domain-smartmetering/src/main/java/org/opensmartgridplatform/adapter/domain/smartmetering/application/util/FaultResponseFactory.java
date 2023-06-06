// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.util;

import java.util.List;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseParameterDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseParametersDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;

public class FaultResponseFactory {

  public FaultResponseDto nonRetryablefaultResponseForException(
      final Exception exception,
      final List<FaultResponseParameterDto> parameters,
      final String defaultMessage) {
    return this.faultResponseForException(exception, parameters, defaultMessage, false);
  }

  public FaultResponseDto faultResponseForException(
      final Exception exception,
      final List<FaultResponseParameterDto> parameters,
      final String defaultMessage) {
    return this.faultResponseForException(exception, parameters, defaultMessage, true);
  }

  private FaultResponseDto faultResponseForException(
      final Exception exception,
      final List<FaultResponseParameterDto> parameters,
      final String defaultMessage,
      final boolean retryable) {

    final FaultResponseParametersDto faultResponseParameters =
        this.faultResponseParametersForList(parameters);

    if (exception instanceof FunctionalException || exception instanceof TechnicalException) {
      return this.faultResponseForFunctionalOrTechnicalException(
          (OsgpException) exception, faultResponseParameters, defaultMessage, retryable);
    }

    return new FaultResponseDto.Builder()
        .withMessage(defaultMessage)
        .withComponent(ComponentType.DOMAIN_SMART_METERING.name())
        .withInnerException(exception.getClass().getName())
        .withInnerMessage(exception.getMessage())
        .withFaultResponseParameters(faultResponseParameters)
        .withRetryable(retryable)
        .build();
  }

  private FaultResponseParametersDto faultResponseParametersForList(
      final List<FaultResponseParameterDto> parameterList) {
    if (parameterList == null || parameterList.isEmpty()) {
      return null;
    }
    return new FaultResponseParametersDto(parameterList);
  }

  private FaultResponseDto faultResponseForFunctionalOrTechnicalException(
      final OsgpException exception,
      final FaultResponseParametersDto faultResponseParameters,
      final String defaultMessage,
      final boolean retryable) {

    final Integer code;
    if (exception instanceof FunctionalException) {
      code = ((FunctionalException) exception).getCode();
    } else {
      code = null;
    }

    final String component;
    if (exception.getComponentType() == null) {
      component = null;
    } else {
      component = exception.getComponentType().name();
    }

    final String innerException;
    final String innerMessage;
    final Throwable cause = exception.getCause();
    if (cause == null) {
      innerException = null;
      innerMessage = null;
    } else {
      innerException = cause.getClass().getName();
      innerMessage = cause.getMessage();
    }

    final String message;
    if (exception.getMessage() == null) {
      message = defaultMessage;
    } else {
      message = exception.getMessage();
    }

    return new FaultResponseDto.Builder()
        .withCode(code)
        .withMessage(message)
        .withComponent(component)
        .withInnerException(innerException)
        .withInnerMessage(innerMessage)
        .withFaultResponseParameters(faultResponseParameters)
        .withRetryable(retryable)
        .build();
  }
}
