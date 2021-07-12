/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import java.util.ArrayList;
import java.util.List;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.BundleService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.WebServiceResponseMessageSender;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BundleMessagesRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseParameterDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseParametersDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class BundleResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

  @Autowired
  @Qualifier("domainSmartMeteringBundleService")
  private BundleService bundleService;

  @Autowired
  public BundleResponseMessageProcessor(
      final WebServiceResponseMessageSender responseMessageSender,
      @Qualifier("domainSmartMeteringInboundOsgpCoreResponsesMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    super(
        responseMessageSender,
        messageProcessorMap,
        MessageType.HANDLE_BUNDLED_ACTIONS,
        ComponentType.DOMAIN_SMART_METERING);
  }

  @Override
  protected boolean hasRegularResponseObject(final ResponseMessage responseMessage) {
    return responseMessage.getDataObject() instanceof BundleMessagesRequestDto;
  }

  @Override
  protected void handleMessage(
      final MessageMetadata deviceMessageMetadata,
      final ResponseMessage responseMessage,
      final OsgpException osgpException)
      throws FunctionalException {

    final BundleMessagesRequestDto bundleMessagesResponseDto =
        (BundleMessagesRequestDto) responseMessage.getDataObject();

    this.bundleService.handleBundleResponse(
        deviceMessageMetadata,
        responseMessage.getResult(),
        osgpException,
        bundleMessagesResponseDto);
  }

  @Override
  protected void handleError(
      final Exception e,
      final MessageMetadata deviceMessageMetadata,
      final ResponseMessage responseMessage)
      throws FunctionalException {

    final OsgpException osgpException = this.ensureOsgpException(e);

    final BundleMessagesRequestDto bundleMessagesResponseDto =
        (BundleMessagesRequestDto) responseMessage.getDataObject();

    final List<ActionDto> actionList = bundleMessagesResponseDto.getActionList();
    for (final ActionDto action : actionList) {
      if (action.getResponse() == null) {
        final List<FaultResponseParameterDto> parameterList = new ArrayList<>();
        final FaultResponseParameterDto deviceIdentificationParameter =
            new FaultResponseParameterDto(
                "deviceIdentification", deviceMessageMetadata.getDeviceIdentification());
        parameterList.add(deviceIdentificationParameter);
        action.setResponse(
            this.faultResponseForException(e, parameterList, "Unable to handle request"));
      }
    }

    this.bundleService.handleBundleResponse(
        deviceMessageMetadata,
        responseMessage.getResult(),
        osgpException,
        bundleMessagesResponseDto);
  }

  protected FaultResponseDto faultResponseForException(
      final Exception exception,
      final List<FaultResponseParameterDto> parameters,
      final String defaultMessage) {

    final FaultResponseParametersDto faultResponseParameters =
        this.faultResponseParametersForList(parameters);

    if (exception instanceof FunctionalException || exception instanceof TechnicalException) {
      return this.faultResponseForFunctionalOrTechnicalException(
          (OsgpException) exception, faultResponseParameters, defaultMessage);
    }

    return new FaultResponseDto.Builder()
        .withMessage(defaultMessage)
        .withComponent(ComponentType.DOMAIN_SMART_METERING.name())
        .withInnerException(exception.getClass().getName())
        .withInnerMessage(exception.getMessage())
        .withFaultResponseParameters(faultResponseParameters)
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
      final String defaultMessage) {

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
        .build();
  }
}
