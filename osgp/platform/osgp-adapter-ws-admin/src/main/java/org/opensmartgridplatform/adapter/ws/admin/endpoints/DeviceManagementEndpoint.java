// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.admin.endpoints;

import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import org.opensmartgridplatform.adapter.ws.admin.application.mapping.DeviceManagementMapper;
import org.opensmartgridplatform.adapter.ws.admin.application.services.DeviceManagementService;
import org.opensmartgridplatform.adapter.ws.admin.application.valueobjects.WsMessageLogFilter;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.schema.admin.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.ActivateOrganisationRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.ActivateOrganisationResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.ChangeOrganisationRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.ChangeOrganisationResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.CreateOrganisationRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.CreateOrganisationResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.DeviceAuthorisation;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.FindDeviceAuthorisationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.FindDeviceAuthorisationsResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.FindDevicesWhichHaveNoOwnerRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.FindDevicesWhichHaveNoOwnerResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.FindMessageLogsRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.FindMessageLogsResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.GetProtocolInfosRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.GetProtocolInfosResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.MessageLog;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.MessageLogPage;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.ProtocolInfo;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RemoveDeviceRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RemoveDeviceResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RemoveOrganisationRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RemoveOrganisationResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RevokeKeyRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.RevokeKeyResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.SetCommunicationNetworkInformationRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.SetCommunicationNetworkInformationResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.SetOwnerRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.SetOwnerResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.UpdateDeviceAuthorisationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.UpdateDeviceAuthorisationsResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.UpdateDeviceProtocolRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.UpdateDeviceProtocolResponse;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.UpdateKeyRequest;
import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.UpdateKeyResponse;
import org.opensmartgridplatform.adapter.ws.schema.core.notification.NotificationType;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.exceptions.ValidationException;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformDomain;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformFunctionGroup;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/** Device Management Endpoint class */
@Endpoint(value = "adminDeviceManagmentEndpoint")
public class DeviceManagementEndpoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementEndpoint.class);

  private static final String DEVICE_MANAGEMENT_NAMESPACE =
      "http://www.opensmartgridplatform.org/schemas/admin/devicemanagement/2014/10";
  private static final ComponentType COMPONENT_TYPE_WS_ADMIN = ComponentType.WS_ADMIN;

  private final DeviceManagementService deviceManagementService;
  private final DeviceManagementMapper deviceManagementMapper;

  @Autowired private NotificationService notificationService;

  /** Constructor */
  @Autowired()
  public DeviceManagementEndpoint(
      @Qualifier(value = "wsAdminDeviceManagementService")
          final DeviceManagementService deviceManagementService,
      @Qualifier(value = "adminDeviceManagementMapper")
          final DeviceManagementMapper deviceManagementMapper) {
    this.deviceManagementService = deviceManagementService;
    this.deviceManagementMapper = deviceManagementMapper;
  }

  @PayloadRoot(localPart = "CreateOrganisationRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public CreateOrganisationResponse createOrganisation(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final CreateOrganisationRequest request)
      throws OsgpException {

    LOGGER.info(
        "Create organisation: {}, with name: {}.",
        request.getOrganisation().getOrganisationIdentification(),
        request.getOrganisation().getName());

    final Organisation organisation =
        this.deviceManagementMapper.map(request.getOrganisation(), Organisation.class);
    // mapping fails for the 'enabled' field of the DeviceManagement Schema
    // Organisation / Organisation
    organisation.setIsEnabled(request.getOrganisation().isEnabled());

    try {
      this.deviceManagementService.addOrganisation(organisationIdentification, organisation);
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_ADMIN,
          new ValidationException(e.getConstraintViolations()));
    } catch (final TransactionSystemException e) {
      throw new TechnicalException(COMPONENT_TYPE_WS_ADMIN, e);
    } catch (final Exception e) {
      this.handleException(e);
    }

    return new CreateOrganisationResponse();
  }

  @PayloadRoot(localPart = "RemoveOrganisationRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public RemoveOrganisationResponse removeOrganisation(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final RemoveOrganisationRequest request)
      throws OsgpException {

    LOGGER.info("Remove organisation: {}.", request.getOrganisationIdentification());

    try {
      this.deviceManagementService.removeOrganisation(
          organisationIdentification, request.getOrganisationIdentification());
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_ADMIN,
          new ValidationException(e.getConstraintViolations()));
    } catch (final TransactionSystemException ex) {
      throw new TechnicalException(COMPONENT_TYPE_WS_ADMIN, ex.getApplicationException());
    } catch (final Exception e) {
      this.handleException(e);
    }

    return new RemoveOrganisationResponse();
  }

  @PayloadRoot(localPart = "ActivateOrganisationRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public ActivateOrganisationResponse activateOrganisation(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final ActivateOrganisationRequest request)
      throws OsgpException {

    LOGGER.info("Activate organisation: {}.", request.getOrganisationIdentification());

    try {
      this.deviceManagementService.activateOrganisation(
          organisationIdentification, request.getOrganisationIdentification());
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.WS_ADMIN,
          new ValidationException(e.getConstraintViolations()));
    } catch (final TransactionSystemException ex) {
      throw new TechnicalException(COMPONENT_TYPE_WS_ADMIN, ex.getApplicationException());
    } catch (final Exception e) {
      this.handleException(e);
    }

    return new ActivateOrganisationResponse();
  }

  @PayloadRoot(localPart = "ChangeOrganisationRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public ChangeOrganisationResponse changeOrganisation(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final ChangeOrganisationRequest request)
      throws OsgpException {

    LOGGER.info("Change organisation: {}.", request.getOrganisationIdentification());

    try {
      this.deviceManagementService.changeOrganisation(
          organisationIdentification,
          request.getOrganisationIdentification(),
          request.getNewOrganisationName(),
          PlatformFunctionGroup.valueOf(request.getNewOrganisationPlatformFunctionGroup().value()),
          this.deviceManagementMapper.mapAsList(
              request.getNewOrganisationPlatformDomains(), PlatformDomain.class));
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          COMPONENT_TYPE_WS_ADMIN,
          new ValidationException(e.getConstraintViolations()));
    } catch (final TransactionSystemException ex) {
      throw new TechnicalException(COMPONENT_TYPE_WS_ADMIN, ex.getApplicationException());
    } catch (final Exception e) {
      this.handleException(e);
    }

    return new ChangeOrganisationResponse();
  }

  @PayloadRoot(localPart = "FindMessageLogsRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public FindMessageLogsResponse findMessageLogs(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final FindMessageLogsRequest request)
      throws OsgpException {

    final WsMessageLogFilter filter =
        this.deviceManagementMapper.map(request.getMessageLogFilter(), WsMessageLogFilter.class);

    LOGGER.info(
        "Find message logs of filter {} for organisation: {}.", filter, organisationIdentification);

    final FindMessageLogsResponse response = new FindMessageLogsResponse();

    try {
      final Slice<DeviceLogItem> page =
          this.deviceManagementService.findDeviceMessages(organisationIdentification, filter);

      // Map to output
      final MessageLogPage logPage = new MessageLogPage();
      logPage
          .getMessageLogs()
          .addAll(this.deviceManagementMapper.mapAsList(page.getContent(), MessageLog.class));
      logPage.setNextPageAvailable(page.hasNext());
      response.setMessageLogPage(logPage);
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          COMPONENT_TYPE_WS_ADMIN,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(
      localPart = "UpdateDeviceAuthorisationsRequest",
      namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public UpdateDeviceAuthorisationsResponse updateDeviceAuthorisations(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final UpdateDeviceAuthorisationsRequest request)
      throws OsgpException {

    LOGGER.info("Update device autorisations for organisation: {}.", organisationIdentification);

    final List<String> deviceIdentifications = new ArrayList<>();

    try {
      for (final DeviceAuthorisation authorization : request.getDeviceAuthorisations()) {
        LOGGER.info(
            "device: {}, organisation: {}, isRevoked: {}, functionGroup: {}",
            authorization.getDeviceIdentification(),
            authorization.getOrganisationIdentification(),
            authorization.isRevoked(),
            authorization.getFunctionGroup());
      }

      for (final DeviceAuthorisation authorization : request.getDeviceAuthorisations()) {
        if (authorization.isRevoked() != null && authorization.isRevoked()) {
          this.deviceManagementService.removeDeviceAuthorization(
              organisationIdentification,
              authorization.getOrganisationIdentification(),
              authorization.getDeviceIdentification(),
              this.deviceManagementMapper.map(
                  authorization.getFunctionGroup(), DeviceFunctionGroup.class));
        } else {
          this.deviceManagementService.addDeviceAuthorization(
              organisationIdentification,
              authorization.getOrganisationIdentification(),
              authorization.getDeviceIdentification(),
              this.deviceManagementMapper.map(
                  authorization.getFunctionGroup(), DeviceFunctionGroup.class));
        }
        // Save the device identification for notification later.
        if (!deviceIdentifications.contains(authorization.getDeviceIdentification())) {
          deviceIdentifications.add(authorization.getDeviceIdentification());
        }
      }
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          COMPONENT_TYPE_WS_ADMIN,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }

    for (final String deviceIdentification : deviceIdentifications) {
      try {
        this.notificationService.sendNotification(
            organisationIdentification,
            deviceIdentification,
            null,
            null,
            null,
            NotificationType.DEVICE_UPDATED);
      } catch (final Exception e) {
        LOGGER.error("Caught exception when sending notification", e);
      }
    }

    return new UpdateDeviceAuthorisationsResponse();
  }

  @PayloadRoot(
      localPart = "FindDeviceAuthorisationsRequest",
      namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public FindDeviceAuthorisationsResponse findDeviceAuthorisations(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final FindDeviceAuthorisationsRequest request)
      throws OsgpException {

    LOGGER.info(
        "Find device autorisations for organisation: {} and device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    final FindDeviceAuthorisationsResponse response = new FindDeviceAuthorisationsResponse();

    try {
      final List<org.opensmartgridplatform.domain.core.entities.DeviceAuthorization>
          authorizations =
              this.deviceManagementService.findDeviceAuthorisations(
                  organisationIdentification, request.getDeviceIdentification());
      response
          .getDeviceAuthorisations()
          .addAll(this.deviceManagementMapper.mapAsList(authorizations, DeviceAuthorisation.class));
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          COMPONENT_TYPE_WS_ADMIN,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }

    return response;
  }

  @PayloadRoot(
      localPart = "FindDevicesWhichHaveNoOwnerRequest",
      namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public FindDevicesWhichHaveNoOwnerResponse findDevicesWhichHaveNoOwner(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final FindDevicesWhichHaveNoOwnerRequest request)
      throws OsgpException {

    LOGGER.info(
        "Finding devices which have no owner for organisation: {}.", organisationIdentification);

    final FindDevicesWhichHaveNoOwnerResponse response = new FindDevicesWhichHaveNoOwnerResponse();

    try {
      final List<org.opensmartgridplatform.domain.core.entities.Device> devicesWithoutOwner =
          this.deviceManagementService.findDevicesWhichHaveNoOwner(organisationIdentification);

      response
          .getDevices()
          .addAll(
              this.deviceManagementMapper.mapAsList(
                  devicesWithoutOwner,
                  org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.Device.class));
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          COMPONENT_TYPE_WS_ADMIN,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  @PayloadRoot(localPart = "RemoveDeviceRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public RemoveDeviceResponse removeDevice(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final RemoveDeviceRequest request)
      throws OsgpException {

    LOGGER.info(
        "Remove decvice for organisation: {} and device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    try {
      this.deviceManagementService.removeDevice(
          organisationIdentification, request.getDeviceIdentification());
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          COMPONENT_TYPE_WS_ADMIN,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }

    return new RemoveDeviceResponse();
  }

  @PayloadRoot(localPart = "SetOwnerRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public SetOwnerResponse setOwner(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetOwnerRequest request)
      throws OsgpException {

    LOGGER.info(
        "Set owner for organisation: {} and device: {} new owner organisation: {}.",
        organisationIdentification,
        request.getDeviceIdentification(),
        request.getOrganisationIdentification());

    try {
      this.deviceManagementService.setOwner(
          organisationIdentification,
          request.getDeviceIdentification(),
          request.getOrganisationIdentification());
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          COMPONENT_TYPE_WS_ADMIN,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }

    return new SetOwnerResponse();
  }

  @PayloadRoot(localPart = "UpdateKeyRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public UpdateKeyResponse updateKey(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final UpdateKeyRequest request)
      throws OsgpException {

    LOGGER.info(
        "Update key for organisation: {} and device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    try {
      this.deviceManagementService.updateKey(
          organisationIdentification,
          request.getDeviceIdentification(),
          request.getPublicKey(),
          request.getProtocolInfoId());
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          COMPONENT_TYPE_WS_ADMIN,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }

    return new UpdateKeyResponse();
  }

  @PayloadRoot(localPart = "RevokeKeyRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public RevokeKeyResponse revokeKey(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final RevokeKeyRequest request)
      throws OsgpException {

    LOGGER.info(
        "Revoke key for organisation: {} and device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    try {
      this.deviceManagementService.revokeKey(
          organisationIdentification, request.getDeviceIdentification());
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          COMPONENT_TYPE_WS_ADMIN,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }

    return new RevokeKeyResponse();
  }

  @PayloadRoot(localPart = "GetProtocolInfosRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public GetProtocolInfosResponse getProtocolInfos(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final GetProtocolInfosRequest request)
      throws OsgpException {

    LOGGER.info("Get protocol infos for organisation: {}.", organisationIdentification);

    final GetProtocolInfosResponse getProtocolInfosResponse = new GetProtocolInfosResponse();

    try {
      final List<org.opensmartgridplatform.domain.core.entities.ProtocolInfo> protocolInfos =
          this.deviceManagementService.getProtocolInfos(organisationIdentification);
      getProtocolInfosResponse
          .getProtocolInfos()
          .addAll(
              this.deviceManagementMapper.mapAsList(
                  protocolInfos,
                  org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.ProtocolInfo
                      .class));
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          COMPONENT_TYPE_WS_ADMIN,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }

    return getProtocolInfosResponse;
  }

  @PayloadRoot(localPart = "UpdateDeviceProtocolRequest", namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public UpdateDeviceProtocolResponse updateDeviceProtocol(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final UpdateDeviceProtocolRequest request)
      throws OsgpException {

    LOGGER.info(
        "Update device protocol for organisation: {} and device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    try {
      final ProtocolInfo protocolInfo = request.getProtocolInfo();
      this.deviceManagementService.updateDeviceProtocol(
          organisationIdentification,
          request.getDeviceIdentification(),
          protocolInfo.getProtocol(),
          protocolInfo.getProtocolVersion(),
          protocolInfo.getProtocolVariant());
    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          COMPONENT_TYPE_WS_ADMIN,
          new ValidationException(e.getConstraintViolations()));
    } catch (final Exception e) {
      this.handleException(e);
    }

    return new UpdateDeviceProtocolResponse();
  }

  @PayloadRoot(
      localPart = "SetCommunicationNetworkInformationRequest",
      namespace = DEVICE_MANAGEMENT_NAMESPACE)
  @ResponsePayload
  public SetCommunicationNetworkInformationResponse setCommunicationNetworkInformation(
      @OrganisationIdentification final String organisationIdentification,
      @RequestPayload final SetCommunicationNetworkInformationRequest request)
      throws OsgpException {

    LOGGER.info(
        "SetCommunicationNetworkInformation for organisation: {} and device: {}.",
        organisationIdentification,
        request.getDeviceIdentification());

    SetCommunicationNetworkInformationResponse response = null;
    try {
      response = new SetCommunicationNetworkInformationResponse();

      final org.opensmartgridplatform.domain.core.entities.Device updatedDevice =
          this.deviceManagementService.updateCommunicationNetworkInformation(
              organisationIdentification,
              request.getDeviceIdentification(),
              request.getIpAddress(),
              request.getBtsId(),
              request.getCellId());

      response.setResult(OsgpResultType.OK);
      response.setIpAddress(updatedDevice.getNetworkAddress());
      response.setBtsId(updatedDevice.getBtsId());
      response.setCellId(updatedDevice.getCellId());

    } catch (final ConstraintViolationException e) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          COMPONENT_TYPE_WS_ADMIN,
          new ValidationException(e.getConstraintViolations()));

    } catch (final Exception e) {
      this.handleException(e);
    }
    return response;
  }

  private void handleException(final Exception e) throws OsgpException {
    // Rethrow exception if it already is a functional or technical
    // exception,
    // otherwise throw new technical exception.
    if (e instanceof OsgpException) {
      throw (OsgpException) e;
    } else {
      LOGGER.error("An unknown error occurred", e);
      throw new TechnicalException(COMPONENT_TYPE_WS_ADMIN, e);
    }
  }
}
