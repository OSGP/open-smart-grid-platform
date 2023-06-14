// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.endpoints;

import java.util.List;
import javax.validation.ConstraintViolationException;
import org.opensmartgridplatform.adapter.ws.core.application.mapping.DeviceInstallationMapper;
import org.opensmartgridplatform.adapter.ws.core.application.services.DeviceInstallationService;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.MessagePriority;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.schema.core.common.AsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.AddDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.AddDeviceResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.AddLightMeasurementDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.AddLightMeasurementDeviceResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.GetStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.GetStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.GetStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.GetStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StartDeviceTestAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StartDeviceTestAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StartDeviceTestRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StartDeviceTestResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StopDeviceTestAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StopDeviceTestAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StopDeviceTestRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.StopDeviceTestResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.UpdateDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.UpdateDeviceResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.UpdateLightMeasurementDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.UpdateLightMeasurementDeviceResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.notification.NotificationType;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.exceptions.ValidationException;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatus;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
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
public class DeviceInstallationEndpoint extends CoreEndpoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceInstallationEndpoint.class);
  private static final String DEVICE_INSTALLATION_NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/deviceinstallation/2014/10";
  private static final ComponentType COMPONENT_WS_CORE = ComponentType.WS_CORE;

  private static final String EXCEPTION_WHILE_ADDING_DEVICE =
      "Exception: {} while adding device: {} for organisation {}.";
  private static final String EXCEPTION_WHILE_UPDATING_DEVICE =
      "Exception: {} while updating device: {} for organisation {}.";

  private DeviceInstallationService deviceInstallationService;
  private DeviceInstallationMapper deviceInstallationMapper;

  @Autowired private NotificationService notificationService;

  public DeviceInstallationEndpoint() {}

  @Autowired
  public DeviceInstallationEndpoint(
      @Qualifier(value = "wsCoreDeviceInstallationService")
          final DeviceInstallationService deviceInstallationService,
      @Qualifier(value = "coreDeviceInstallationMapper")
          final DeviceInstallationMapper deviceInstallationMapper) {
    this.deviceInstallationService = deviceInstallationService;
    this.deviceInstallationMapper = deviceInstallationMapper;
  }

  @PayloadRoot(localPart = "GetStatusRequest", namespace = DEVICE_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public GetStatusAsyncResponse getStatus(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetStatusRequest request,
      @MessagePriority final String messagePriority)
      throws OsgpException {

    LOGGER.info(
        "Get Status received from organisation: {} for device: {} with message priority: {}.",
        organisationIdentification,
        request.getDeviceIdentification(),
        messagePriority);

    final GetStatusAsyncResponse response = new GetStatusAsyncResponse();
    try {
      final String correlationUid =
          this.deviceInstallationService.enqueueGetStatusRequest(
              organisationIdentification,
              request.getDeviceIdentification(),
              MessagePriorityEnum.getMessagePriority(messagePriority));

      final AsyncResponse asyncResponse = new AsyncResponse();
      asyncResponse.setCorrelationUid(correlationUid);
      asyncResponse.setDeviceId(request.getDeviceIdentification());
      response.setAsyncResponse(asyncResponse);
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  @PayloadRoot(localPart = "GetStatusAsyncRequest", namespace = DEVICE_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public GetStatusResponse getGetStatusResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetStatusAsyncRequest request)
      throws OsgpException {

    LOGGER.info("Get Status Response received from organisation: {}.", organisationIdentification);

    final GetStatusResponse response = new GetStatusResponse();

    try {
      final ResponseMessage responseMessage = this.getResponseMessage(request.getAsyncRequest());
      if (responseMessage != null) {
        throwExceptionIfResultNotOk(responseMessage, "getting status");
        response.setResult(OsgpResultType.fromValue(responseMessage.getResult().getValue()));
        final DeviceStatus deviceStatus = (DeviceStatus) responseMessage.getDataObject();
        if (deviceStatus != null) {
          response.setDeviceStatus(
              this.deviceInstallationMapper.map(
                  deviceStatus,
                  org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.DeviceStatus
                      .class));
        }
      }
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  @PayloadRoot(localPart = "AddDeviceRequest", namespace = DEVICE_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public AddDeviceResponse addDevice(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final AddDeviceRequest request)
      throws OsgpException {

    LOGGER.info("Adding device: {}.", request.getDevice().getDeviceIdentification());

    try {
      final Device device = this.deviceInstallationMapper.map(request.getDevice(), Device.class);
      final String ownerOrganisationIdentification = request.getDevice().getOwner();

      this.deviceInstallationService.addDevice(
          organisationIdentification, device, ownerOrganisationIdentification);
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_CORE,
          new ValidationException(e.getConstraintViolations()));
    } catch (final AssertionError e) {
      throw new TechnicalException(COMPONENT_WS_CORE, e);
    } catch (final Exception e) {
      LOGGER.error(
          EXCEPTION_WHILE_ADDING_DEVICE,
          e.getMessage(),
          request.getDevice().getDeviceIdentification(),
          organisationIdentification,
          e);
      this.handleException(e);
    }

    return new AddDeviceResponse();
  }

  @PayloadRoot(localPart = "UpdateDeviceRequest", namespace = DEVICE_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public UpdateDeviceResponse updateDevice(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final UpdateDeviceRequest request)
      throws OsgpException {

    LOGGER.info("Updating device: {}.", request.getDeviceIdentification());

    try {
      final Ssld device = this.deviceInstallationMapper.map(request.getUpdatedDevice(), Ssld.class);

      this.deviceInstallationService.updateDevice(organisationIdentification, device);

    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_CORE,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      LOGGER.error(
          EXCEPTION_WHILE_UPDATING_DEVICE,
          e.getMessage(),
          request.getUpdatedDevice().getDeviceIdentification(),
          organisationIdentification,
          e);
      this.handleException(e);
    }

    try {
      this.notificationService.sendNotification(
          organisationIdentification,
          request.getDeviceIdentification(),
          null,
          null,
          null,
          NotificationType.DEVICE_UPDATED);
    } catch (final Exception e) {
      LOGGER.error(e.getMessage(), e);
    }

    return new UpdateDeviceResponse();
  }

  @PayloadRoot(
      localPart = "AddLightMeasurementDeviceRequest",
      namespace = DEVICE_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public AddLightMeasurementDeviceResponse addLightMeasurementDevice(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final AddLightMeasurementDeviceRequest request)
      throws OsgpException {

    LOGGER.info(
        "Adding light measurement device: {}.",
        request.getLightMeasurementDevice().getDeviceIdentification());

    try {
      final LightMeasurementDevice lmd =
          this.deviceInstallationMapper.map(
              request.getLightMeasurementDevice(), LightMeasurementDevice.class);
      final String ownerOrganisationIdentification = request.getLightMeasurementDevice().getOwner();

      this.deviceInstallationService.addLightMeasurementDevice(
          organisationIdentification, lmd, ownerOrganisationIdentification);
    } catch (final ConstraintViolationException e) {
      LOGGER.error(
          EXCEPTION_WHILE_ADDING_DEVICE,
          e.getMessage(),
          request.getLightMeasurementDevice().getDeviceIdentification(),
          organisationIdentification,
          e);
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_CORE,
          new ValidationException(e.getConstraintViolations()));
    } catch (final AssertionError e) {
      LOGGER.error(
          EXCEPTION_WHILE_ADDING_DEVICE,
          e.getMessage(),
          request.getLightMeasurementDevice().getDeviceIdentification(),
          organisationIdentification,
          e);
      throw new TechnicalException(COMPONENT_WS_CORE, e);
    } catch (final Exception e) {
      LOGGER.error(
          EXCEPTION_WHILE_ADDING_DEVICE,
          e.getMessage(),
          request.getLightMeasurementDevice().getDeviceIdentification(),
          organisationIdentification,
          e);
      this.handleException(e);
    }

    return new AddLightMeasurementDeviceResponse();
  }

  @PayloadRoot(
      localPart = "UpdateLightMeasurementDeviceRequest",
      namespace = DEVICE_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public UpdateLightMeasurementDeviceResponse updateLightMeasurementDevice(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final UpdateLightMeasurementDeviceRequest request)
      throws OsgpException {

    LOGGER.info("Updating light measurement device: {}.", request.getDeviceIdentification());

    try {
      final LightMeasurementDevice device =
          this.deviceInstallationMapper.map(
              request.getUpdatedLightMeasurementDevice(), LightMeasurementDevice.class);

      this.deviceInstallationService.updateLightMeasurementDevice(
          organisationIdentification, device);
    } catch (final ConstraintViolationException e) {
      LOGGER.error("Exception update Device: {} ", e.getMessage(), e);
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_CORE,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      LOGGER.error(
          EXCEPTION_WHILE_UPDATING_DEVICE,
          e.getMessage(),
          request.getUpdatedLightMeasurementDevice().getDeviceIdentification(),
          organisationIdentification,
          e);
      this.handleException(e);
    }

    try {
      this.notificationService.sendNotification(
          organisationIdentification,
          request.getDeviceIdentification(),
          null,
          null,
          null,
          NotificationType.DEVICE_UPDATED);
    } catch (final Exception e) {
      LOGGER.error(e.getMessage(), e);
    }

    return new UpdateLightMeasurementDeviceResponse();
  }

  @PayloadRoot(localPart = "FindRecentDevicesRequest", namespace = DEVICE_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public FindRecentDevicesResponse findRecentDevices(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final FindRecentDevicesRequest request)
      throws OsgpException {

    LOGGER.info("Finding recent devices for organisation: {}.", organisationIdentification);

    final FindRecentDevicesResponse response = new FindRecentDevicesResponse();

    try {
      final List<Device> recentDevices =
          this.deviceInstallationService.findRecentDevices(organisationIdentification);
      response
          .getDevices()
          .addAll(
              this.deviceInstallationMapper.mapAsList(
                  recentDevices,
                  org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.Device
                      .class));
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

  // === START DEVICE TEST ===

  @PayloadRoot(localPart = "StartDeviceTestRequest", namespace = DEVICE_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public StartDeviceTestAsyncResponse startDeviceTest(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final StartDeviceTestRequest request,
      @MessagePriority final String messagePriority)
      throws OsgpException {

    LOGGER.info(
        "Start Device Test Request received from organisation: {} for device: {} with message priority: {}.",
        organisationIdentification,
        request.getDeviceIdentification(),
        messagePriority);

    final StartDeviceTestAsyncResponse response = new StartDeviceTestAsyncResponse();

    try {
      final AsyncResponse asyncResponse = new AsyncResponse();
      final String correlationUid =
          this.deviceInstallationService.enqueueStartDeviceTestRequest(
              organisationIdentification,
              request.getDeviceIdentification(),
              MessagePriorityEnum.getMessagePriority(messagePriority));
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

  @PayloadRoot(localPart = "StartDeviceTestAsyncRequest", namespace = DEVICE_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public StartDeviceTestResponse getStartDeviceTestResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final StartDeviceTestAsyncRequest request)
      throws OsgpException {

    LOGGER.info(
        "Get Start Device Test Response received from organisation: {} for device: {}.",
        organisationIdentification,
        request.getAsyncRequest().getDeviceId());

    final StartDeviceTestResponse response = new StartDeviceTestResponse();

    try {
      final ResponseMessage responseMessage = this.getResponseMessage(request.getAsyncRequest());
      if (responseMessage != null) {
        throwExceptionIfResultNotOk(responseMessage, "starting device test");
        response.setResult(OsgpResultType.fromValue(responseMessage.getResult().getValue()));
      }
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  // === STOP DEVICE TEST ===

  @PayloadRoot(localPart = "StopDeviceTestRequest", namespace = DEVICE_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public StopDeviceTestAsyncResponse stopDeviceTest(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final StopDeviceTestRequest request,
      @MessagePriority final String messagePriority)
      throws OsgpException {

    LOGGER.info(
        "Stop Device Test Request received from organisation: {} for device: {} with message priority: {}.",
        organisationIdentification,
        request.getDeviceIdentification(),
        messagePriority);

    final StopDeviceTestAsyncResponse response = new StopDeviceTestAsyncResponse();

    try {
      final AsyncResponse asyncResponse = new AsyncResponse();
      final String correlationUid =
          this.deviceInstallationService.enqueueStopDeviceTestRequest(
              organisationIdentification,
              request.getDeviceIdentification(),
              MessagePriorityEnum.getMessagePriority(messagePriority));
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

  @PayloadRoot(localPart = "StopDeviceTestAsyncRequest", namespace = DEVICE_INSTALLATION_NAMESPACE)
  @ResponsePayload
  public StopDeviceTestResponse getStopDeviceTestResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final StopDeviceTestAsyncRequest request)
      throws OsgpException {

    LOGGER.info(
        "Get Stop Device Test Response received from organisation: {} for device: {}.",
        organisationIdentification,
        request.getAsyncRequest().getDeviceId());

    final StopDeviceTestResponse response = new StopDeviceTestResponse();

    try {
      final ResponseMessage responseMessage = this.getResponseMessage(request.getAsyncRequest());
      if (responseMessage != null) {
        throwExceptionIfResultNotOk(responseMessage, "stopping device test");
        response.setResult(OsgpResultType.fromValue(responseMessage.getResult().getValue()));
      }
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }
}
