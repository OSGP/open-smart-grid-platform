/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.util.ArrayList;
import java.util.List;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.CommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.CommandExecutorMap;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BundleMessagesRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseParameterDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseParametersDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "dlmsBundleService")
public class BundleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleService.class);

    @Autowired
    private CommandExecutorMap bundleCommandExecutorMap;

    public BundleMessagesRequestDto callExecutors(final DlmsConnectionManager conn, final DlmsDevice device,
            final BundleMessagesRequestDto bundleMessagesRequest) {

        final List<ActionDto> actionList = bundleMessagesRequest.getActionList();
        for (final ActionDto actionDto : actionList) {

            // Only execute the request when there is no response available yet.
            // Because it could be a retry.
            if (actionDto.getResponse() == null) {

                final Class<? extends ActionRequestDto> actionRequestClass = actionDto.getRequest().getClass();

                final CommandExecutor<?, ?> executor = this.bundleCommandExecutorMap
                        .getCommandExecutor(actionRequestClass);

                final String executorName = executor == null ? "null" : executor.getClass().getSimpleName();

                try {

                    this.checkIfExecutorExists(actionRequestClass, executor);

                    LOGGER.debug("**************************************************");
                    LOGGER.info("Calling executor in bundle {}", executorName);
                    LOGGER.debug("**************************************************");
                    actionDto.setResponse(executor.executeBundleAction(conn, device, actionDto.getRequest()));
                } catch (final ConnectionException connectionException) {
                    LOGGER.warn("A connection exception occurred while executing {}", executorName,
                            connectionException);

                    final List<ActionDto> remainingActionDtoList = actionList
                            .subList(actionList.indexOf(actionDto), actionList.size());

                    for (final ActionDto remainingActionDto : remainingActionDtoList) {
                        LOGGER.debug("Skipping: {}", remainingActionDto.getRequest().getClass().getSimpleName());
                    }

                    actionDto.setResponse(null);
                    throw connectionException;
                } catch (final Exception exception) {

                    LOGGER.error("Error while executing bundle action for {} with {}", actionRequestClass.getName(),
                            executorName, exception);
                    final String responseMessage = executor == null ? "Unable to handle request" :
                            "Error handling request with " + executorName;

                    this.addFaultResponse(actionDto, exception, responseMessage, device);
                }
            }
        }

        return bundleMessagesRequest;
    }

    private void addFaultResponse(final ActionDto actionDto, final Exception exception, final String defaultMessage,
            final DlmsDevice device) {

        final List<FaultResponseParameterDto> parameterList = new ArrayList<>();
        final FaultResponseParameterDto deviceIdentificationParameter = new FaultResponseParameterDto(
                "deviceIdentification", device.getDeviceIdentification());
        parameterList.add(deviceIdentificationParameter);

        final FaultResponseDto faultResponse = this.faultResponseForException(exception, parameterList, defaultMessage);
        actionDto.setResponse(faultResponse);
    }

    protected FaultResponseDto faultResponseForException(final Exception exception,
            final List<FaultResponseParameterDto> parameters, final String defaultMessage) {

        final FaultResponseParametersDto faultResponseParameters = this.faultResponseParametersForList(parameters);

        if (exception instanceof FunctionalException || exception instanceof TechnicalException) {
            return this
                    .faultResponseForFunctionalOrTechnicalException((OsgpException) exception, faultResponseParameters,
                            defaultMessage);
        }

        return new FaultResponseDto.Builder().withMessage(defaultMessage)
                                             .withComponent(ComponentType.PROTOCOL_DLMS.name())
                                             .withInnerException(exception.getClass().getName())
                                             .withInnerMessage(exception.getMessage())
                                             .withFaultResponseParameters(faultResponseParameters).build();
    }

    private FaultResponseParametersDto faultResponseParametersForList(
            final List<FaultResponseParameterDto> parameterList) {
        if (parameterList == null || parameterList.isEmpty()) {
            return null;
        }
        return new FaultResponseParametersDto(parameterList);
    }

    private FaultResponseDto faultResponseForFunctionalOrTechnicalException(final OsgpException exception,
            final FaultResponseParametersDto faultResponseParameters, final String defaultMessage) {

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

        String message;
        if (exception.getMessage() == null) {
            message = defaultMessage;
        } else {
            message = exception.getMessage();
        }

        return new FaultResponseDto.Builder().withCode(code).withMessage(message).withComponent(component)
                                             .withInnerException(innerException).withInnerMessage(innerMessage)
                                             .withFaultResponseParameters(faultResponseParameters).build();
    }

    private void checkIfExecutorExists(final Class<? extends ActionRequestDto> actionRequestClass,
            final CommandExecutor<?, ?> executor) throws ProtocolAdapterException {
        if (executor == null) {
            LOGGER.error("bundleCommandExecutorMap in " + this.getClass().getName()
                    + " does not have a CommandExecutor registered for action: " + actionRequestClass.getName());
            throw new ProtocolAdapterException(
                    "No CommandExecutor available to handle " + actionRequestClass.getSimpleName());
        }
    }
}
