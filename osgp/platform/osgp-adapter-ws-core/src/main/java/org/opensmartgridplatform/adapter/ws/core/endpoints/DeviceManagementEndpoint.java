// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.endpoints;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintViolationException;
import org.opensmartgridplatform.adapter.ws.core.application.criteria.SearchEventsCriteria;
import org.opensmartgridplatform.adapter.ws.core.application.mapping.DeviceManagementMapper;
import org.opensmartgridplatform.adapter.ws.core.application.services.DeviceManagementService;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.MessagePriority;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.schema.core.common.AsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Event;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindAllOrganisationsResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindDevicesRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindDevicesResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindEventsRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindEventsResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindOrganisationRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindOrganisationResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindScheduledTasksRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindScheduledTasksResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetDeviceAliasRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetDeviceAliasResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetDeviceLifecycleStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetDeviceVerificationKeyAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetDeviceVerificationKeyAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetDeviceVerificationKeyRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetDeviceVerificationKeyResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetEventNotificationsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetEventNotificationsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetEventNotificationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetEventNotificationsResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetMaintenanceStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.SetMaintenanceStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceCdmaSettingsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceCdmaSettingsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceCdmaSettingsRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceCdmaSettingsResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceSslCertificationAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceSslCertificationAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceSslCertificationRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdateDeviceSslCertificationResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.notification.NotificationType;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.ScheduledTaskWithoutData;
import org.opensmartgridplatform.domain.core.exceptions.ValidationException;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaSettings;
import org.opensmartgridplatform.domain.core.valueobjects.Certification;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFilter;
import org.opensmartgridplatform.domain.core.valueobjects.EventNotificationType;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.opensmartgridplatform.shared.application.config.PageSpecifier;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.utils.JavaTimeHelpers;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/** Device Management Endpoint class */
@Endpoint(value = "coreDeviceManagementEndpoint")
public class DeviceManagementEndpoint extends CoreEndpoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementEndpoint.class);
  private static final String DEVICE_MANAGEMENT_NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/devicemanagement/2014/10";

  private static final String EXCEPTION = "Exception: {}, StackTrace: {}";
  private static final String EXCEPTION_WHILE_UPDATING_DEVICE =
      "Exception: {} while updating device: {} for organisation: {}.";

  private final DeviceManagementService deviceManagementService;
  private final DeviceManagementMapper deviceManagementMapper;

  @Autowired private CorrelationIdProviderService correlationIdProviderService;

  @Autowired private NotificationService notificationService;

  @Autowired
  public DeviceManagementEndpoint(
      @Qualifier(value = "wsCoreDeviceManagementService")
          final DeviceManagementService deviceManagementService,
      @Qualifier(value = "coreDeviceManagementMapper")
          final DeviceManagementMapper deviceManagementMapper) {
    this.deviceManagementService = deviceManagementService;
    this.deviceManagementMapper = deviceManagementMapper;
  }

  @PayloadRoot(localPart = "FindOrganisationRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public FindOrganisationResponse findOrganisation(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final FindOrganisationRequest request)
      throws OsgpException {

    LOGGER.info("Find organisation for organisation: {}.", organisationIdentification);

    final FindOrganisationResponse response = new FindOrganisationResponse();

    try {
      final Organisation organisation =
          this.deviceManagementService.findOrganisation(
              organisationIdentification, request.getOrganisationIdentification());
      response.setOrganisation(
          this.deviceManagementMapper.map(
              organisation,
              org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Organisation
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

  @PayloadRoot(localPart = "FindAllOrganisationsRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public FindAllOrganisationsResponse findAllOrganisations(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final FindAllOrganisationsRequest request)
      throws OsgpException {

    LOGGER.info("Find all organisations for organisation: {}.", organisationIdentification);

    final FindAllOrganisationsResponse response = new FindAllOrganisationsResponse();

    try {
      final List<Organisation> organisations =
          this.deviceManagementService.findAllOrganisations(organisationIdentification);
      response
          .getOrganisations()
          .addAll(
              this.deviceManagementMapper.mapAsList(
                  organisations,
                  org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Organisation
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

  @PayloadRoot(localPart = "SetEventNotificationsRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public SetEventNotificationsAsyncResponse setEventNotifications(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetEventNotificationsRequest request,
      @MessagePriority final String messagePriority)
      throws OsgpException {

    LOGGER.info(
        "Set EventNotifications Request received from organisation: {} for event device: {} with message priority: {}.",
        organisationIdentification,
        request.getDeviceIdentification(),
        messagePriority);

    final SetEventNotificationsAsyncResponse response = new SetEventNotificationsAsyncResponse();

    try {
      final List<EventNotificationType> eventNotifications =
          new ArrayList<>(
              this.deviceManagementMapper.mapAsList(
                  request.getEventNotifications(), EventNotificationType.class));
      final String correlationUid =
          this.deviceManagementService.enqueueSetEventNotificationsRequest(
              organisationIdentification,
              request.getDeviceIdentification(),
              eventNotifications,
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

  @PayloadRoot(
      localPart = "SetEventNotificationsAsyncRequest",
      namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public SetEventNotificationsResponse getSetEventNotificationsResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetEventNotificationsAsyncRequest request)
      throws OsgpException {

    LOGGER.info(
        "Get Set Event Notifications Response received from organisation: {} with correlationUid: {}.",
        organisationIdentification,
        request.getAsyncRequest().getCorrelationUid());

    final SetEventNotificationsResponse response = new SetEventNotificationsResponse();

    try {
      final ResponseMessage responseMessage = this.getResponseMessage(request.getAsyncRequest());
      if (responseMessage != null) {
        throwExceptionIfResultNotOk(responseMessage, "setting event notifications");
        response.setResult(OsgpResultType.fromValue(responseMessage.getResult().getValue()));
      }
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  @PayloadRoot(localPart = "FindEventsRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public FindEventsResponse findEventsRequest(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final FindEventsRequest request)
      throws OsgpException {

    LOGGER.info(
        "Find events response for organisation: {} and device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    // Create response.
    final FindEventsResponse response = new FindEventsResponse();

    try {
      // Get the request parameters, make sure that they are in UTC.
      // Maybe add an adapter to the service, so that all datetime are
      // converted to utc automatically.
      final ZonedDateTime from =
          request.getFrom() == null
              ? null
              : JavaTimeHelpers.gregorianCalendarToZonedDateTimeWithUTCZone(
                  request.getFrom().toGregorianCalendar());
      final ZonedDateTime until =
          request.getUntil() == null
              ? null
              : JavaTimeHelpers.gregorianCalendarToZonedDateTimeWithUTCZone(
                  request.getUntil().toGregorianCalendar());

      // Get all events matching the request.
      final SearchEventsCriteria criteria =
          SearchEventsCriteria.builder()
              .organisationIdentification(organisationIdentification)
              .deviceIdentification(request.getDeviceIdentification())
              .pageSpecifier(this.pageFrom(request))
              .from(from)
              .until(until)
              .eventTypes(
                  this.deviceManagementMapper.mapAsList(request.getEventTypes(), EventType.class))
              .description(request.getDescription())
              .descriptionStartsWith(request.getDescriptionStartsWith())
              .build();
      final Page<org.opensmartgridplatform.domain.core.entities.Event> result =
          this.deviceManagementService.findEvents(criteria);

      response
          .getEvents()
          .addAll(this.deviceManagementMapper.mapAsList(result.getContent(), Event.class));
      response.setPage(new org.opensmartgridplatform.adapter.ws.schema.core.common.Page());
      response.getPage().setPageSize(result.getSize());
      response.getPage().setTotalPages(result.getTotalPages());
      response.getPage().setCurrentPage(result.getNumber());
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

  private PageSpecifier pageFrom(final FindEventsRequest request) {
    return new PageSpecifier(request.getPageSize(), request.getPage());
  }

  @PayloadRoot(localPart = "FindDevicesRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public FindDevicesResponse findDevices(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final FindDevicesRequest request)
      throws OsgpException {

    LOGGER.info("Find devices for organisation: {}.", organisationIdentification);

    final FindDevicesResponse response = new FindDevicesResponse();

    try {
      final PageSpecifier pageSpecifier = this.pageFrom(request);
      Page<org.opensmartgridplatform.domain.core.entities.Device> result =
          this.deviceManagementService.findDevices(
              organisationIdentification, pageSpecifier, this.deviceFilterFrom(request));

      if (result != null && response.getDevices() != null) {
        response
            .getDevices()
            .addAll(this.deviceManagementMapper.mapAsList(result.getContent(), Device.class));
        response.setPage(new org.opensmartgridplatform.adapter.ws.schema.core.common.Page());
        response.getPage().setPageSize(result.getSize());
        response.getPage().setTotalPages(result.getTotalPages());
        response.getPage().setCurrentPage(result.getNumber());
      }

      if (result != null && request.isUsePages() != null && !request.isUsePages()) {
        int calls = 0;
        while ((calls += 1) < result.getTotalPages()) {
          request.setPage(calls);
          result =
              this.deviceManagementService.findDevices(
                  organisationIdentification, pageSpecifier, this.deviceFilterFrom(request));
          response
              .getDevices()
              .addAll(this.deviceManagementMapper.mapAsList(result.getContent(), Device.class));
        }
      }
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_CORE,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      LOGGER.error(EXCEPTION, e.getMessage(), e.getStackTrace(), e);
      this.handleException(e);
    }

    return response;
  }

  private DeviceFilter deviceFilterFrom(final FindDevicesRequest request) {
    return this.deviceManagementMapper.map(request.getDeviceFilter(), DeviceFilter.class);
  }

  // suppress warning about unused method. This method is used in findDevices.
  @SuppressWarnings("squid:S1144")
  private PageSpecifier pageFrom(final FindDevicesRequest request) {
    return new PageSpecifier(request.getPageSize(), request.getPage());
  }

  @PayloadRoot(localPart = "FindScheduledTasksRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public FindScheduledTasksResponse findScheduledTasks(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final FindScheduledTasksRequest request)
      throws OsgpException {

    LOGGER.info("Finding scheduled tasks for organisation: {}.", organisationIdentification);

    final FindScheduledTasksResponse response = new FindScheduledTasksResponse();

    try {
      final List<ScheduledTaskWithoutData> scheduledTasks;
      if (request.getDeviceIdentification() == null) {
        scheduledTasks =
            this.deviceManagementService.findScheduledTasks(organisationIdentification);
      } else {
        scheduledTasks =
            this.deviceManagementService.findScheduledTasks(
                organisationIdentification, request.getDeviceIdentification());
      }

      response
          .getScheduledTask()
          .addAll(
              this.deviceManagementMapper.mapAsList(
                  scheduledTasks,
                  org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.ScheduledTask
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

  @PayloadRoot(localPart = "UpdateDeviceRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public UpdateDeviceResponse updateDevice(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final UpdateDeviceRequest request)
      throws OsgpException {

    final String deviceToUpdateIdentification = request.getDeviceIdentification();

    LOGGER.info(
        "UpdateDeviceRequest received for device: {} for organisation: {}.",
        deviceToUpdateIdentification,
        organisationIdentification);

    try {
      final org.opensmartgridplatform.domain.core.entities.Ssld ssld =
          this.deviceManagementMapper.map(
              request.getUpdatedDevice(),
              org.opensmartgridplatform.domain.core.entities.Ssld.class);

      this.deviceManagementService.updateDevice(
          organisationIdentification, deviceToUpdateIdentification, ssld);
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_CORE,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      LOGGER.error(
          EXCEPTION_WHILE_UPDATING_DEVICE,
          e.getMessage(),
          deviceToUpdateIdentification,
          organisationIdentification,
          e);
      this.handleException(e);
    }

    final UpdateDeviceResponse updateDeviceResponse = new UpdateDeviceResponse();

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceToUpdateIdentification);

    final AsyncResponse asyncResponse = new AsyncResponse();
    asyncResponse.setCorrelationUid(correlationUid);
    asyncResponse.setDeviceId(deviceToUpdateIdentification);

    updateDeviceResponse.setAsyncResponse(asyncResponse);

    try {
      this.notificationService.sendNotification(
          organisationIdentification,
          deviceToUpdateIdentification,
          null,
          null,
          null,
          NotificationType.DEVICE_UPDATED);
    } catch (final Exception e) {
      LOGGER.error(e.getMessage(), e);
    }

    return updateDeviceResponse;
  }

  @PayloadRoot(localPart = "SetDeviceAliasRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public SetDeviceAliasResponse setDeviceAlias(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetDeviceAliasRequest request)
      throws OsgpException {

    LOGGER.info(
        "Setting device alias for device: {} to: {}.",
        request.getDeviceIdentification(),
        request.getDeviceAlias());

    try {
      this.deviceManagementService.setDeviceAlias(
          organisationIdentification,
          request.getDeviceIdentification(),
          request.getDeviceAlias(),
          request.getDeviceOutputSettings());
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_CORE,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      LOGGER.error(
          EXCEPTION_WHILE_UPDATING_DEVICE,
          e.getMessage(),
          request.getDeviceIdentification(),
          organisationIdentification,
          e);
      this.handleException(e);
    }

    final SetDeviceAliasResponse setDeviceAliasResponse = new SetDeviceAliasResponse();

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, request.getDeviceIdentification());

    final AsyncResponse asyncResponse = new AsyncResponse();
    asyncResponse.setCorrelationUid(correlationUid);
    asyncResponse.setDeviceId(request.getDeviceIdentification());

    setDeviceAliasResponse.setAsyncResponse(asyncResponse);

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

    return setDeviceAliasResponse;
  }

  @PayloadRoot(localPart = "SetMaintenanceStatusRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public SetMaintenanceStatusResponse setMaintenanceStatus(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetMaintenanceStatusRequest request)
      throws OsgpException {

    LOGGER.info(
        "Setting maintenance for device:{} to: {}.",
        request.getDeviceIdentification(),
        request.isStatus());

    try {
      this.deviceManagementService.setMaintenanceStatus(
          organisationIdentification, request.getDeviceIdentification(), request.isStatus());
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_CORE,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      LOGGER.error(
          EXCEPTION_WHILE_UPDATING_DEVICE,
          e.getMessage(),
          request.getDeviceIdentification(),
          organisationIdentification,
          e);
      this.handleException(e);
    }

    final SetMaintenanceStatusResponse setMaintenanceStatusResponse =
        new SetMaintenanceStatusResponse();
    setMaintenanceStatusResponse.setResult(OsgpResultType.OK);

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

    return setMaintenanceStatusResponse;
  }

  @PayloadRoot(
      localPart = "UpdateDeviceSslCertificationRequest",
      namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public UpdateDeviceSslCertificationAsyncResponse updateDeviceSslCertification(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final UpdateDeviceSslCertificationRequest request,
      @MessagePriority final String messagePriority)
      throws OsgpException {

    LOGGER.info(
        "Update Device Ssl Certification Request received from organisation: {} for device: {} with message priority: {}.",
        organisationIdentification,
        request.getDeviceIdentification(),
        messagePriority);

    final UpdateDeviceSslCertificationAsyncResponse response =
        new UpdateDeviceSslCertificationAsyncResponse();

    try {
      final Certification certification =
          this.deviceManagementMapper.map(request.getCertification(), Certification.class);

      final String correlationUid =
          this.deviceManagementService.enqueueUpdateDeviceSslCertificationRequest(
              organisationIdentification,
              request.getDeviceIdentification(),
              certification,
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

  @PayloadRoot(
      localPart = "UpdateDeviceSslCertificationAsyncRequest",
      namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public UpdateDeviceSslCertificationResponse getUpdateDeviceSslCertificationResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final UpdateDeviceSslCertificationAsyncRequest request)
      throws OsgpException {

    LOGGER.info(
        "Update Device Ssl Certification Response received from organisation: {} with correlationUid: {}.",
        organisationIdentification,
        request.getAsyncRequest().getCorrelationUid());

    final UpdateDeviceSslCertificationResponse response =
        new UpdateDeviceSslCertificationResponse();

    try {
      final ResponseMessage responseMessage = this.getResponseMessage(request.getAsyncRequest());
      if (responseMessage != null) {
        throwExceptionIfResultNotOk(responseMessage, "updating device ssl certificate");
        response.setResult(OsgpResultType.fromValue(responseMessage.getResult().getValue()));
      } else {
        LOGGER.debug("Update Device Ssl Certification data is null");
      }
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  @PayloadRoot(
      localPart = "SetDeviceVerificationKeyRequest",
      namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public SetDeviceVerificationKeyAsyncResponse setDeviceVerificationKey(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetDeviceVerificationKeyRequest request,
      @MessagePriority final String messagePriority)
      throws OsgpException {

    LOGGER.info(
        "Set Device Verification Key Request received from organisation: {} for device: {} with message priority: {}.",
        organisationIdentification,
        request.getDeviceIdentification(),
        messagePriority);

    final SetDeviceVerificationKeyAsyncResponse response =
        new SetDeviceVerificationKeyAsyncResponse();

    try {
      final String correlationUid =
          this.deviceManagementService.enqueueSetDeviceVerificationKeyRequest(
              organisationIdentification,
              request.getDeviceIdentification(),
              request.getVerificationKey(),
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

  @PayloadRoot(
      localPart = "SetDeviceVerificationKeyAsyncRequest",
      namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public SetDeviceVerificationKeyResponse getSetDeviceVerificationKeyResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetDeviceVerificationKeyAsyncRequest request)
      throws OsgpException {

    LOGGER.info(
        "Set Device Verification Key Response received from organisation: {} with correlationUid: {}.",
        organisationIdentification,
        request.getAsyncRequest().getCorrelationUid());

    final SetDeviceVerificationKeyResponse response = new SetDeviceVerificationKeyResponse();

    try {
      final ResponseMessage responseMessage = this.getResponseMessage(request.getAsyncRequest());
      if (responseMessage != null) {
        throwExceptionIfResultNotOk(responseMessage, "setting device verification key");
        response.setResult(OsgpResultType.fromValue(responseMessage.getResult().getValue()));
      } else {
        LOGGER.debug("Set Device Verification Key is null");
      }
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  @PayloadRoot(
      localPart = "SetDeviceLifecycleStatusRequest",
      namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public SetDeviceLifecycleStatusAsyncResponse setDeviceLifecycleRequest(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetDeviceLifecycleStatusRequest request)
      throws OsgpException {

    LOGGER.info(
        "SetDeviceLifecycleStatusRequest received for device: {} and organisation: {}.",
        request.getDeviceIdentification(),
        organisationIdentification);

    final SetDeviceLifecycleStatusAsyncResponse asyncResponse =
        new SetDeviceLifecycleStatusAsyncResponse();

    try {
      final String correlationUid =
          this.deviceManagementService.enqueueSetDeviceLifecycleStatusRequest(
              organisationIdentification,
              request.getDeviceIdentification(),
              request.getDeviceLifecycleStatus());

      asyncResponse.setCorrelationUid(correlationUid);
      asyncResponse.setDeviceId(request.getDeviceIdentification());

    } catch (final Exception e) {
      this.handleException(e);
    }

    return asyncResponse;
  }

  @PayloadRoot(
      localPart = "SetDeviceLifecycleStatusAsyncRequest",
      namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public SetDeviceLifecycleStatusResponse getSetDeviceLifecycleStatusResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetDeviceLifecycleStatusAsyncRequest asyncRequest)
      throws OsgpException {

    LOGGER.info(
        "Get Set Device Lifecycle Status Notifications Response received from organisation: {} with correlationUid: {}.",
        organisationIdentification,
        asyncRequest.getCorrelationUid());

    final SetDeviceLifecycleStatusResponse response = new SetDeviceLifecycleStatusResponse();

    try {
      final ResponseMessage responseMessage = this.getResponseMessage(asyncRequest);
      if (responseMessage != null) {
        throwExceptionIfResultNotOk(responseMessage, "setting device lifecycle status");
        response.setResult(OsgpResultType.fromValue(responseMessage.getResult().getValue()));
      }
    } catch (final Exception e) {
      this.handleException(e);
    }

    if (OsgpResultType.OK.equals(response.getResult())) {
      try {
        this.notificationService.sendNotification(
            organisationIdentification,
            asyncRequest.getDeviceId(),
            response.getResult().name(),
            asyncRequest.getCorrelationUid(),
            null,
            NotificationType.DEVICE_UPDATED);
      } catch (final Exception e) {
        LOGGER.error(e.getMessage(), e);
      }
    }

    return response;
  }

  @PayloadRoot(
      localPart = "UpdateDeviceCdmaSettingsRequest",
      namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public UpdateDeviceCdmaSettingsAsyncResponse updateDeviceCdmaSettingsRequest(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final UpdateDeviceCdmaSettingsRequest request)
      throws OsgpException {

    LOGGER.info(
        "UpdateDeviceCdmaSettingsRequest received for device: {} and organisation: {}.",
        request.getDeviceIdentification(),
        organisationIdentification);

    final UpdateDeviceCdmaSettingsAsyncResponse asyncResponse =
        new UpdateDeviceCdmaSettingsAsyncResponse();

    try {
      final CdmaSettings cdmaSettings =
          new CdmaSettings(request.getMastSegment(), request.getBatchNumber());
      final String correlationUid =
          this.deviceManagementService.enqueueUpdateDeviceCdmaSettingsRequest(
              organisationIdentification, request.getDeviceIdentification(), cdmaSettings);

      asyncResponse.setCorrelationUid(correlationUid);
      asyncResponse.setDeviceId(request.getDeviceIdentification());

    } catch (final Exception e) {
      this.handleException(e);
    }

    return asyncResponse;
  }

  @PayloadRoot(
      localPart = "UpdateDeviceCdmaSettingsAsyncRequest",
      namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public UpdateDeviceCdmaSettingsResponse getUpdateDeviceCdmaSettingsResponse(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final UpdateDeviceCdmaSettingsAsyncRequest asyncRequest)
      throws OsgpException {

    LOGGER.info(
        "GetUpdateDeviceCdmaSettingsResponse received from organisation: {} with correlationUid: {}.",
        organisationIdentification,
        asyncRequest.getCorrelationUid());

    final UpdateDeviceCdmaSettingsResponse response = new UpdateDeviceCdmaSettingsResponse();

    try {
      final ResponseMessage responseMessage = this.getResponseMessage(asyncRequest);
      if (responseMessage != null) {
        throwExceptionIfResultNotOk(responseMessage, "updating CDMA settings for device");
        response.setResult(OsgpResultType.fromValue(responseMessage.getResult().getValue()));
      }
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }
}
