//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.CommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.CommandExecutorMap;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.DeviceKeyProcessAlreadyRunningException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.MissingExecutorException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NonRetryableException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BundleMessagesRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseParameterDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseParametersDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Service;

@Slf4j
@Service(value = "dlmsBundleService")
public class BundleService {

  private static final String DEVICE_IDENTIFICATION = "deviceIdentification";

  private final CommandExecutorMap commandExecutorMap;

  public BundleService(final CommandExecutorMap commandExecutorMap) {
    this.commandExecutorMap = commandExecutorMap;
  }

  public BundleMessagesRequestDto callExecutors(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final BundleMessagesRequestDto request,
      final MessageMetadata messageMetadata) {

    request.getActionList().stream()
        .filter(this::shouldExecute)
        .forEach(
            action -> {
              final CommandExecutor<?, ?> executor = this.getCommandExecutor(action);

              if (executor == null) {
                this.handleMissingExecutor(action, device);

              } else {
                this.callExecutor(executor, action, conn, device, messageMetadata);
              }
            });

    return request;
  }

  private CommandExecutor<?, ?> getCommandExecutor(final ActionDto action) {
    return this.commandExecutorMap.getCommandExecutor(action.getRequest().getClass());
  }

  /*
   * Only execute the request when there is no response available yet. Because it could be a
   * retry. Or it went wrong last time and it is a retryable situation.
   */
  private boolean shouldExecute(final ActionDto action) {
    return action.getResponse() == null || this.isRetryableFaultResponse(action);
  }

  private boolean isRetryableFaultResponse(final ActionDto action) {
    return action.getResponse() instanceof FaultResponseDto
        && ((FaultResponseDto) action.getResponse()).isRetryable();
  }

  private void handleMissingExecutor(final ActionDto action, final DlmsDevice device) {
    final Class<? extends ActionRequestDto> actionRequestClass = action.getRequest().getClass();

    log.error(
        "bundleCommandExecutorMap in {} does not have a CommandExecutor registered for action: {}",
        this.getClass().getName(),
        actionRequestClass.getName());

    final MissingExecutorException e =
        new MissingExecutorException(
            String.format(
                "No CommandExecutor available to handle %s", actionRequestClass.getSimpleName()));

    this.addFaultResponse(action, e, "Unable to handle request", device);
  }

  private void callExecutor(
      final CommandExecutor<?, ?> executor,
      final ActionDto action,
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final MessageMetadata messageMetadata) {

    final String executorName = executor.getClass().getSimpleName();

    try {
      log.info(
          "Calling executor in bundle {} [deviceId={}]",
          executorName,
          device.getDeviceIdentification());

      final ActionResponseDto response =
          executor.executeBundleAction(conn, device, action.getRequest(), messageMetadata);
      action.setResponse(response);

    } catch (final ConnectionException ce) {
      log.warn(
          "A connection exception occurred while executing {} [deviceId={}]",
          executorName,
          device.getDeviceIdentification(),
          ce);
      throw ce;

    } catch (final DeviceKeyProcessAlreadyRunningException e) {
      // This exception will be caught in the DeviceRequestMessageProcessor.
      // The request will NOT be sent back to Core to retry but put back on the queue
      throw e;

    } catch (final Exception e) {
      log.error(
          "Error while executing bundle action for {} with {} [deviceId={}]",
          action.getRequest().getClass().getName(),
          executorName,
          device.getDeviceIdentification(),
          e);
      final String message =
          String.format("Error handling request with %s: %s", executorName, e.getMessage());
      this.addFaultResponse(action, e, message, device);
    }
  }

  private void addFaultResponse(
      final ActionDto action,
      final Exception exception,
      final String defaultMessage,
      final DlmsDevice device) {

    final List<FaultResponseParameterDto> parameterList = new ArrayList<>();
    final FaultResponseParameterDto deviceIdentificationParameter =
        new FaultResponseParameterDto(DEVICE_IDENTIFICATION, device.getDeviceIdentification());
    parameterList.add(deviceIdentificationParameter);

    final FaultResponseDto faultResponse =
        this.faultResponseForException(exception, parameterList, defaultMessage);
    action.setResponse(faultResponse);
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
        .withComponent(ComponentType.PROTOCOL_DLMS.name())
        .withInnerException(exception.getClass().getName())
        .withInnerMessage(exception.getMessage())
        .withFaultResponseParameters(faultResponseParameters)
        .withRetryable(!(exception instanceof NonRetryableException))
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
