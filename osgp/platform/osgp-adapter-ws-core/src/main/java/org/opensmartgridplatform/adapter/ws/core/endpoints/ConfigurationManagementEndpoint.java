// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.endpoints;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.validation.ConstraintViolationException;
import org.opensmartgridplatform.adapter.ws.core.application.mapping.ConfigurationManagementMapper;
import org.opensmartgridplatform.adapter.ws.core.application.services.ConfigurationManagementService;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.MessagePriority;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.schema.core.common.AsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.GetConfigurationAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.GetConfigurationAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.GetConfigurationRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.GetConfigurationResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.SetConfigurationAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.SetConfigurationAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.SetConfigurationRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.SetConfigurationResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.SwitchConfigurationAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.SwitchConfigurationAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.SwitchConfigurationRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement.SwitchConfigurationResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.notification.NotificationType;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.opensmartgridplatform.domain.core.exceptions.ValidationException;
import org.opensmartgridplatform.domain.core.valueobjects.Configuration;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class ConfigurationManagementEndpoint extends CoreEndpoint {

  private static final String NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/configurationmanagement/2014/10";
  private static final Logger LOGGER =
      LoggerFactory.getLogger(ConfigurationManagementEndpoint.class);

  private final ConfigurationManagementService configurationManagementService;
  private final ConfigurationManagementMapper configurationManagementMapper;

  @Autowired private NotificationService notificationService;

  @Autowired
  public ConfigurationManagementEndpoint(
      @Qualifier(value = "wsCoreConfigurationManagementService")
          final ConfigurationManagementService configurationManagementService,
      @Qualifier(value = "coreConfigurationManagementMapper")
          final ConfigurationManagementMapper configurationManagementMapper) {
    this.configurationManagementMapper = configurationManagementMapper;
    this.configurationManagementService = configurationManagementService;
  }

  @PayloadRoot(localPart = "SetConfigurationRequest", namespace = NAMESPACE)
  @ResponsePayload
  public SetConfigurationAsyncResponse setConfiguration(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetConfigurationRequest request,
      @MessagePriority final String messagePriority)
      throws OsgpException {

    LOGGER.info(
        "Set Configuration Request received from organisation: {} for device: {} with message priority: {}.",
        organisationIdentification,
        request.getDeviceIdentification(),
        messagePriority);

    final SetConfigurationAsyncResponse response = new SetConfigurationAsyncResponse();

    try {
      // Get the request parameters, make sure that they are in UTC.
      // Maybe add an adapter to the service, so that all date-time are
      // converted to UTC automatically.
      final ZonedDateTime scheduleTime =
          request.getScheduledTime() == null
              ? null
              : request
                  .getScheduledTime()
                  .toGregorianCalendar()
                  .toZonedDateTime()
                  .withZoneSameInstant(ZoneId.of("UTC"));

      final Configuration configuration =
          this.configurationManagementMapper.map(request.getConfiguration(), Configuration.class);

      final String correlationUid =
          this.configurationManagementService.enqueueSetConfigurationRequest(
              organisationIdentification,
              request.getDeviceIdentification(),
              configuration,
              scheduleTime,
              MessagePriorityEnum.getMessagePriority(messagePriority));

      final AsyncResponse asyncResponse = new AsyncResponse();
      asyncResponse.setCorrelationUid(correlationUid);
      asyncResponse.setDeviceId(request.getDeviceIdentification());

      response.setAsyncResponse(asyncResponse);
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_CORE,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  @PayloadRoot(localPart = "SetConfigurationAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public SetConfigurationResponse getSetConfigurationResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetConfigurationAsyncRequest request)
      throws OsgpException {

    LOGGER.info(
        "Get Set Configuration Response received from organisation: {} with correlationUid: {}.",
        organisationIdentification,
        request.getAsyncRequest().getCorrelationUid());

    final SetConfigurationResponse response = new SetConfigurationResponse();

    try {
      final ResponseMessage responseMessage = this.getResponseMessage(request.getAsyncRequest());
      if (responseMessage != null) {
        throwExceptionIfResultNotOk(responseMessage, "setting configuration");
        response.setResult(OsgpResultType.fromValue(responseMessage.getResult().getValue()));
      }
    } catch (final Exception e) {
      this.handleException(e);
    }

    if (OsgpResultType.OK.equals(response.getResult())) {
      try {
        this.notificationService.sendNotification(
            organisationIdentification,
            request.getAsyncRequest().getDeviceId(),
            response.getResult().name(),
            request.getAsyncRequest().getCorrelationUid(),
            null,
            NotificationType.DEVICE_UPDATED);
      } catch (final Exception e) {
        LOGGER.error(e.getMessage(), e);
      }
    }

    return response;
  }

  @PayloadRoot(localPart = "GetConfigurationRequest", namespace = NAMESPACE)
  @ResponsePayload
  public GetConfigurationAsyncResponse getConfiguration(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetConfigurationRequest request,
      @MessagePriority final String messagePriority)
      throws OsgpException {

    LOGGER.info(
        "Get Configuration Request received from organisation: {} for device: {} with message priority: {}.",
        organisationIdentification,
        request.getDeviceIdentification(),
        messagePriority);

    final GetConfigurationAsyncResponse response = new GetConfigurationAsyncResponse();

    try {
      final String correlationUid =
          this.configurationManagementService.enqueueGetConfigurationRequest(
              organisationIdentification,
              request.getDeviceIdentification(),
              MessagePriorityEnum.getMessagePriority(messagePriority));

      final AsyncResponse asyncResponse = new AsyncResponse();
      asyncResponse.setCorrelationUid(correlationUid);
      asyncResponse.setDeviceId(request.getDeviceIdentification());
      response.setAsyncResponse(asyncResponse);
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_CORE,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  @PayloadRoot(localPart = "GetConfigurationAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public GetConfigurationResponse getGetConfigurationResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetConfigurationAsyncRequest request)
      throws OsgpException {

    LOGGER.info(
        "GetConfigurationRequest received from organisation: {} for device: {}.",
        organisationIdentification,
        request.getAsyncRequest().getDeviceId());

    final GetConfigurationResponse response = new GetConfigurationResponse();

    try {
      final ResponseMessage responseMessage = this.getResponseMessage(request.getAsyncRequest());
      if (responseMessage != null) {
        throwExceptionIfResultNotOk(responseMessage, "getting configuration");
        response.setResult(OsgpResultType.fromValue(responseMessage.getResult().getValue()));
        if (responseMessage.getDataObject() != null) {
          final Configuration configuration = (Configuration) responseMessage.getDataObject();
          response.setConfiguration(
              this.configurationManagementMapper.map(
                  configuration,
                  org.opensmartgridplatform.adapter.ws.schema.core.configurationmanagement
                      .Configuration.class));
        }
      } else {
        LOGGER.debug("Get Configuration data is null");
      }
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  @PayloadRoot(localPart = "SwitchConfigurationRequest", namespace = NAMESPACE)
  @ResponsePayload
  public SwitchConfigurationAsyncResponse switchConfiguration(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SwitchConfigurationRequest request,
      @MessagePriority final String messagePriority)
      throws OsgpException {

    LOGGER.info(
        "Switch Configuration Request received from organisation: {} for device: {} with message priority: {}.",
        organisationIdentification,
        request.getDeviceIdentification(),
        messagePriority);

    final SwitchConfigurationAsyncResponse response = new SwitchConfigurationAsyncResponse();

    try {
      final String correlationUid =
          this.configurationManagementService.enqueueSwitchConfigurationRequest(
              organisationIdentification,
              request.getDeviceIdentification(),
              String.valueOf(request.getConfigurationBank()),
              MessagePriorityEnum.getMessagePriority(messagePriority));

      final AsyncResponse asyncResponse = new AsyncResponse();
      asyncResponse.setCorrelationUid(correlationUid);
      asyncResponse.setDeviceId(request.getDeviceIdentification());
      response.setAsyncResponse(asyncResponse);
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_CORE,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  @PayloadRoot(localPart = "SwitchConfigurationAsyncRequest", namespace = NAMESPACE)
  @ResponsePayload
  public SwitchConfigurationResponse getSwitchConfigurationResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SwitchConfigurationAsyncRequest request)
      throws OsgpException {

    LOGGER.info(
        "Switch Configuration Async Request received from organisation: {} for device: {}.",
        organisationIdentification,
        request.getAsyncRequest().getDeviceId());

    final SwitchConfigurationResponse response = new SwitchConfigurationResponse();

    try {
      final ResponseMessage responseMessage = this.getResponseMessage(request.getAsyncRequest());
      if (responseMessage != null) {
        throwExceptionIfResultNotOk(responseMessage, "switching configuration");
        response.setResult(OsgpResultType.fromValue(responseMessage.getResult().getValue()));
      } else {
        LOGGER.debug("Get Configuration data is null");
      }
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }
}
