// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.admin.application.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.ws.admin.application.specifications.DeviceLogItemSpecifications;
import org.opensmartgridplatform.adapter.ws.admin.application.valueobjects.WsMessageLogFilter;
import org.opensmartgridplatform.adapter.ws.admin.infra.jms.AdminRequestMessage;
import org.opensmartgridplatform.adapter.ws.admin.infra.jms.AdminRequestMessageSender;
import org.opensmartgridplatform.adapter.ws.admin.infra.jms.AdminResponseMessageFinder;
import org.opensmartgridplatform.adapter.ws.admin.infra.specifications.JpaDeviceLogItemSpecifications;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.exceptions.ExistingEntityException;
import org.opensmartgridplatform.domain.core.exceptions.NotAuthorizedException;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.exceptions.ValidationException;
import org.opensmartgridplatform.domain.core.repositories.DeviceAuthorizationRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.EventRepository;
import org.opensmartgridplatform.domain.core.repositories.OrganisationRepository;
import org.opensmartgridplatform.domain.core.repositories.ProtocolInfoRepository;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.services.OrganisationDomainService;
import org.opensmartgridplatform.domain.core.services.SecurityService;
import org.opensmartgridplatform.domain.core.validation.PublicKey;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformDomain;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformFunction;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformFunctionGroup;
import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.logging.domain.repositories.DeviceLogItemSlicingRepository;
import org.opensmartgridplatform.shared.application.config.PagingSettings;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.validation.Identification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service(value = "wsAdminDeviceManagementService")
@Transactional(value = "transactionManager")
@Validated
public class DeviceManagementService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementService.class);

  @Autowired private PagingSettings pagingSettings;

  @Autowired private OrganisationDomainService organisationDomainService;

  @Autowired private DeviceDomainService deviceDomainService;

  @Autowired private SecurityService securityService;

  @Autowired private OrganisationRepository organisationRepository;

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private DeviceLogItemSlicingRepository logItemRepository;

  @Autowired private DeviceAuthorizationRepository authorizationRepository;

  @Autowired private EventRepository eventRepository;

  @Autowired private CorrelationIdProviderService correlationIdProviderService;

  @Autowired private AdminRequestMessageSender adminRequestMessageSender;

  @Autowired private AdminResponseMessageFinder adminResponseMessageFinder;

  @Autowired private ProtocolInfoRepository protocolRepository;

  /** Constructor */
  public DeviceManagementService() {
    // Parameterless constructor required for transactions...
  }

  public void addOrganisation(
      @Identification final String organisationIdentification,
      @Valid @NotNull final Organisation newOrganisation)
      throws FunctionalException {

    LOGGER.debug(
        "addOrganisation called with organisation {} and new organisation {}",
        organisationIdentification,
        newOrganisation.getOrganisationIdentification());

    final Organisation organisation = this.findOrganisation(organisationIdentification);

    this.isAllowed(organisation, PlatformFunction.CREATE_ORGANISATION);

    try {
      // Save the organisation.
      this.organisationRepository.save(newOrganisation);
    } catch (final JpaSystemException ex) {
      if (ex.getCause() instanceof PersistenceException) {
        LOGGER.error("Add organisation failure JpaSystemException", ex);
        throw new FunctionalException(
            FunctionalExceptionType.EXISTING_ORGANISATION,
            ComponentType.WS_ADMIN,
            new ExistingEntityException(
                Organisation.class, newOrganisation.getOrganisationIdentification(), ex));
      }
    }
  }

  public void removeOrganisation(
      @Identification final String organisationIdentification,
      @Identification final String organisationToRemoveIdentification)
      throws FunctionalException {

    LOGGER.debug(
        "removeOrganisation called with organisation {} and organisation to remove {}",
        organisationIdentification,
        organisationToRemoveIdentification);

    final Organisation organisation = this.findOrganisation(organisationIdentification);
    final Organisation organisationToRemove =
        this.findOrganisation(organisationToRemoveIdentification);

    this.isAllowed(organisation, PlatformFunction.REMOVE_ORGANISATION);

    try {
      final List<DeviceAuthorization> deviceAuthorizations =
          this.authorizationRepository.findByOrganisation(organisationToRemove);
      if (!deviceAuthorizations.isEmpty()) {
        throw new FunctionalException(
            FunctionalExceptionType.EXISTING_DEVICE_AUTHORIZATIONS,
            ComponentType.WS_ADMIN,
            new ValidationException(
                String.format(
                    "Device Authorizations are still present for the current organisation %s",
                    organisationToRemove.getOrganisationIdentification())));
      }

      organisationToRemove.setIsEnabled(false);
      this.organisationRepository.save(organisationToRemove);
    } catch (final JpaSystemException ex) {
      if (ex.getCause() instanceof PersistenceException) {
        LOGGER.error("Remove organisation failure JpaSystemException", ex);
        throw new FunctionalException(
            FunctionalExceptionType.UNKNOWN_ORGANISATION,
            ComponentType.WS_ADMIN,
            new UnknownEntityException(Organisation.class, organisationToRemoveIdentification, ex));
      }
    }
  }

  public void activateOrganisation(
      @Identification final String organisationIdentification,
      @Identification final String organisationIdentificationToActivate)
      throws FunctionalException {

    LOGGER.debug(
        "activateOrganisation called with organisation {} and organisation to activate {}",
        organisationIdentification,
        organisationIdentificationToActivate);

    final Organisation organisation = this.findOrganisation(organisationIdentification);
    final Organisation organisationToActivate =
        this.findOrganisation(organisationIdentificationToActivate);

    this.isAllowed(organisation, PlatformFunction.CHANGE_ORGANISATION);

    try {
      organisationToActivate.setIsEnabled(true);
      this.organisationRepository.save(organisationToActivate);
    } catch (final JpaSystemException ex) {
      if (ex.getCause() instanceof PersistenceException) {
        LOGGER.error("activate organisation failure JpaSystemException", ex);
        throw new FunctionalException(
            FunctionalExceptionType.UNKNOWN_ORGANISATION,
            ComponentType.WS_ADMIN,
            new UnknownEntityException(
                Organisation.class, organisationIdentificationToActivate, ex));
      }
    }
  }

  public void changeOrganisation(
      @Identification final String organisationIdentification,
      @Identification final String organisationToBeChangedIdentification,
      final String newOrganisationName,
      @NotNull final PlatformFunctionGroup newOrganisationPlatformFunctionGroup,
      @NotNull final List<PlatformDomain> newDomains)
      throws FunctionalException {

    LOGGER.info(
        "changeOrganisation called with organisation {} and organisation to change {}. new values for organisationName {}, organisationPlatformFunctionGroup {}",
        organisationIdentification,
        organisationToBeChangedIdentification,
        newOrganisationName,
        newOrganisationPlatformFunctionGroup);

    final Organisation organisation = this.findOrganisation(organisationIdentification);
    this.isAllowed(organisation, PlatformFunction.CHANGE_ORGANISATION);

    try {
      final Organisation organisationToChange =
          this.findOrganisation(organisationToBeChangedIdentification);
      organisationToChange.changeOrganisationData(
          newOrganisationName, newOrganisationPlatformFunctionGroup);
      organisationToChange.setDomains(newDomains);

      this.organisationRepository.save(organisationToChange);
    } catch (final JpaSystemException ex) {
      if (ex.getCause() instanceof PersistenceException) {
        LOGGER.error("change organisation failure JpaSystemException", ex);
        throw new FunctionalException(
            FunctionalExceptionType.UNKNOWN_ORGANISATION,
            ComponentType.WS_ADMIN,
            new UnknownEntityException(
                Organisation.class, organisationToBeChangedIdentification, ex));
      }
    }
  }

  public void addDeviceAuthorization(
      @Identification final String ownerOrganisationIdentification,
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      @NotNull final DeviceFunctionGroup group)
      throws FunctionalException {

    // Check input data and authorization
    final Organisation organisation = this.findOrganisation(organisationIdentification);

    final Organisation ownerOrganisation = this.findOrganisation(ownerOrganisationIdentification);

    final Device device = this.findDevice(deviceIdentification);

    this.isAllowed(ownerOrganisation, device, DeviceFunction.SET_DEVICE_AUTHORIZATION);

    // Check if group is already set on device
    for (final DeviceAuthorization authorization : device.getAuthorizations()) {
      if (authorization.getOrganisation() == organisation
          && authorization.getFunctionGroup() == group) {
        LOGGER.info(
            "Organisation {} already has authorization for group {} on device {}",
            organisationIdentification,
            group,
            deviceIdentification);
        // Ignore the request, the authorization is already available
        return;
      }
    }

    // All checks pass, add new authorization
    final DeviceAuthorization authorization = device.addAuthorization(organisation, group);
    this.deviceRepository.save(device);
    this.authorizationRepository.save(authorization);

    LOGGER.info(
        "Organisation {} now has authorization for function group {} on device {}",
        organisationIdentification,
        group,
        deviceIdentification);
  }

  public void removeDeviceAuthorization(
      @Identification final String ownerOrganisationIdentification,
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      @NotNull final DeviceFunctionGroup group)
      throws FunctionalException {

    // Check input data and authorization
    final Organisation organisation = this.findOrganisation(organisationIdentification);

    final Organisation ownerOrganisation = this.findOrganisation(ownerOrganisationIdentification);

    final Device device = this.findDevice(deviceIdentification);

    this.isAllowed(ownerOrganisation, device, DeviceFunction.SET_DEVICE_AUTHORIZATION);

    // Never remove the OWNER authorization
    if (ownerOrganisation.equals(organisation) && DeviceFunctionGroup.OWNER.equals(group)) {
      LOGGER.info("Not removing DeviceFunctionGroup.OWNER for organisation: {}", organisation);
      return;
    }

    // All checks pass, remove authorization
    device.removeAuthorization(organisation, group);
    this.deviceRepository.save(device);
    this.authorizationRepository.deleteByDeviceAndFunctionGroupAndOrganisation(
        device, group, organisation);

    LOGGER.info(
        "Organisation {} now no longer has authorization for function group {} on device {}",
        organisationIdentification,
        group,
        deviceIdentification);
  }

  /**
   * Get all devices which have no owner.
   *
   * @return All devices which have no owner.
   * @throws FunctionalException In case the organisation can not be found or the organisation is
   *     not allowed to perform this action.
   */
  public List<Device> findDevicesWhichHaveNoOwner(
      @Identification final String organisationIdentification) throws FunctionalException {
    final Organisation organisation = this.findOrganisation(organisationIdentification);

    this.isAllowed(organisation, PlatformFunction.GET_DEVICE_NO_OWNER);

    return this.deviceRepository.findDevicesWithNoOwner();
  }

  public List<DeviceAuthorization> findDeviceAuthorisations(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification)
      throws FunctionalException {

    LOGGER.debug(
        "findDeviceAuthorisations called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    final Organisation organisation = this.findOrganisation(organisationIdentification);

    final Device device = this.findDevice(deviceIdentification);

    this.isAllowed(organisation, device, DeviceFunction.GET_DEVICE_AUTHORIZATION);

    return this.authorizationRepository.findByDeviceForOrganisation(device, organisation);
  }

  public Slice<DeviceLogItem> findDeviceMessages(
      @Identification final String organisationIdentification, final WsMessageLogFilter filter)
      throws FunctionalException {

    LOGGER.info(
        "findDeviceMessages called with organisation {} for filter {}",
        organisationIdentification,
        filter);

    final Organisation organisation = this.findOrganisation(organisationIdentification);
    this.isAllowed(organisation, PlatformFunction.GET_MESSAGES);

    final PageRequest pageRequest;
    if (!StringUtils.isBlank(filter.getSortDirection())
        && !StringUtils.isBlank(filter.getSortBy())) {
      pageRequest =
          PageRequest.of(
              filter.getPageRequested(),
              this.pagingSettings.getMaximumPageSize(),
              Sort.Direction.valueOf(filter.getSortDirection()),
              filter.getSortBy());
    } else {
      pageRequest =
          PageRequest.of(
              filter.getPageRequested(),
              this.pagingSettings.getMaximumPageSize(),
              Sort.Direction.DESC,
              "modificationTime");
    }
    final Specification<DeviceLogItem> specification = this.applyFilter(filter);

    return this.logItemRepository.findAll(specification, pageRequest);
  }

  private Specification<DeviceLogItem> applyFilter(final WsMessageLogFilter filter) {
    final DeviceLogItemSpecifications specifications = new JpaDeviceLogItemSpecifications();

    return specifications
        .hasDeviceIdentification(filter.getDeviceIdentification())
        .and(specifications.hasOrganisationIdentification(filter.getOrganisationIdentification()))
        .and(specifications.hasStartDate(filter.getStartTime()))
        .and(specifications.hasEndDate(filter.getEndTime()));
  }

  // === REMOVE DEVICE ===

  /**
   * Removes a device.
   *
   * @param organisationIdentification The organisation identification who performs the action
   * @param deviceIdentification The device identification of the device
   * @throws FunctionalException In case the device or organisation can not be found or the
   *     organisation is not allowed to perform this action.
   */
  public void removeDevice(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification)
      throws FunctionalException {
    final Organisation organisation = this.findOrganisation(organisationIdentification);
    final Device device = this.findDevice(deviceIdentification);
    this.isAllowed(organisation, device, DeviceFunction.REMOVE_DEVICE);

    // First remove all authorizations
    final List<DeviceAuthorization> authorisations =
        this.authorizationRepository.findByDevice(device);
    for (final DeviceAuthorization authorisation : authorisations) {
      this.authorizationRepository.delete(authorisation);
    }

    // Remove all events
    final List<Event> events =
        this.eventRepository.findByDeviceIdentification(deviceIdentification);
    for (final Event event : events) {
      this.eventRepository.delete(event);
    }

    // Then remove the device.
    this.deviceRepository.delete(device);
  }

  // === SET OWNER ===

  /**
   * Sets the owner of the device
   *
   * @param organisationIdentification The organisation identification who performs the action
   *     (needed for security)
   * @param deviceIdentification The device identification of the device
   * @param newOwner The organisation identification of the new owner.
   * @throws FunctionalException In case the device or organisation can not be found or the
   *     organisation is not allowed to perform this action.
   */
  public void setOwner(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      @Identification final String newOwner)
      throws FunctionalException {
    Organisation organisation = this.findOrganisation(organisationIdentification);
    final Device device = this.findDevice(deviceIdentification);
    this.isAllowed(organisation, PlatformFunction.SET_OWNER);

    organisation = this.findOrganisation(newOwner);

    // First remove any other owners.
    final List<DeviceAuthorization> owners =
        this.authorizationRepository.findByDeviceAndFunctionGroup(
            device, DeviceFunctionGroup.OWNER);
    if (!owners.isEmpty()) {
      for (final DeviceAuthorization owner : owners) {
        this.authorizationRepository.delete(owner);
      }
    }

    // Now add the authorization
    final DeviceAuthorization authorization =
        new DeviceAuthorization(device, organisation, DeviceFunctionGroup.OWNER);
    this.authorizationRepository.save(authorization);
  }

  // === UPDATE KEY ===

  public void updateKey(
      final String organisationIdentification,
      @Identification final String deviceIdentification,
      @PublicKey final String publicKey,
      final Long protocolInfoId)
      throws FunctionalException {

    LOGGER.debug(
        "Updating key for device [{}] on behalf of organisation [{}]",
        deviceIdentification,
        organisationIdentification);

    final Organisation organisation = this.findOrganisation(organisationIdentification);
    this.isAllowed(organisation, PlatformFunction.UPDATE_KEY);
    this.organisationDomainService.isOrganisationEnabled(organisation, ComponentType.WS_ADMIN);

    final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);

    if (device == null) {
      // Device not found, create new device
      LOGGER.debug("Device [{}] does not exist, creating new device", deviceIdentification);
      final Ssld ssld = new Ssld(deviceIdentification);

      final DeviceAuthorization authorization =
          ssld.addAuthorization(organisation, DeviceFunctionGroup.OWNER);

      final ProtocolInfo protocolInfo =
          this.protocolRepository
              .findById(protocolInfoId)
              .orElseThrow(
                  () ->
                      new EntityNotFoundException(
                          "No protocol info record found with ID: " + protocolInfoId));
      ssld.updateProtocol(protocolInfo);

      this.authorizationRepository.save(authorization);
    }

    this.enqueueUpdateKeyRequest(organisationIdentification, deviceIdentification, publicKey);
  }

  public String enqueueUpdateKeyRequest(
      final String organisationIdentification,
      @Identification final String deviceIdentification,
      @PublicKey final String publicKey) {

    LOGGER.debug(
        "enqueueUpdateKeyRequest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final AdminRequestMessage message =
        new AdminRequestMessage(
            MessageType.UPDATE_KEY,
            correlationUid,
            organisationIdentification,
            deviceIdentification,
            publicKey);

    this.adminRequestMessageSender.send(message);

    return correlationUid;
  }

  public ResponseMessage dequeueUpdateKeyResponse(final String correlationUid)
      throws OsgpException {

    return this.adminResponseMessageFinder.findMessage(correlationUid);
  }

  // === REVOKE KEY ===

  public void revokeKey(
      final String organisationIdentification, @Identification final String deviceIdentification)
      throws FunctionalException {

    LOGGER.debug(
        "Revoking key for device [{}] on behalf of organisation [{}]",
        deviceIdentification,
        organisationIdentification);

    this.findDevice(deviceIdentification);
    final Organisation organisation = this.findOrganisation(organisationIdentification);
    this.isAllowed(organisation, PlatformFunction.REVOKE_KEY);

    this.enqueueRevokeKeyRequest(organisationIdentification, deviceIdentification);
  }

  public String enqueueRevokeKeyRequest(
      final String organisationIdentification, @Identification final String deviceIdentification) {

    LOGGER.debug(
        "enqueueRevokeKeyRequest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final AdminRequestMessage message =
        new AdminRequestMessage(
            MessageType.REVOKE_KEY,
            correlationUid,
            organisationIdentification,
            deviceIdentification,
            null);

    this.adminRequestMessageSender.send(message);

    return correlationUid;
  }

  public ResponseMessage dequeueRevokeKeyResponse(final String correlationUid)
      throws OsgpException {

    return this.adminResponseMessageFinder.findMessage(correlationUid);
  }

  public List<ProtocolInfo> getProtocolInfos(final String organisationIdentification)
      throws FunctionalException {

    LOGGER.debug(
        "Retrieving all protocol infos on behalf of organisation: {}", organisationIdentification);

    final Organisation organisation = this.findOrganisation(organisationIdentification);
    this.isAllowed(organisation, PlatformFunction.GET_PROTOCOL_INFOS);

    return this.protocolRepository.findAll(Sort.by(Direction.ASC, "protocol", "protocolVersion"));
  }

  public void updateDeviceProtocol(
      final String organisationIdentification,
      @Identification final String deviceIdentification,
      final String protocol,
      final String protocolVersion,
      final String protocolVariant)
      throws FunctionalException {

    LOGGER.debug(
        "Updating protocol for device [{}] on behalf of organisation [{}] to protocol: {}, version: {}, variant: {}",
        deviceIdentification,
        organisationIdentification,
        protocol,
        protocolVersion,
        protocolVariant);

    final Organisation organisation = this.findOrganisation(organisationIdentification);
    this.isAllowed(organisation, PlatformFunction.UPDATE_DEVICE_PROTOCOL);

    final Device device = this.findDevice(deviceIdentification);
    final ProtocolInfo protocolInfo =
        this.findProtocolInfo(protocol, protocolVersion, protocolVariant);

    if (protocolInfo.equals(device.getProtocolInfo())) {
      LOGGER.info(
          "Not updating protocol: {}, version: {}, variant: {} on device {} since it is already configured",
          protocol,
          protocolVersion,
          protocolVariant,
          deviceIdentification);
      return;
    }

    device.updateProtocol(protocolInfo);
    this.deviceRepository.save(device);

    LOGGER.info(
        "Organisation {} configured protocol: {}, version: {}, variant: {} on device {}",
        organisationIdentification,
        protocol,
        protocolVersion,
        protocolVariant,
        deviceIdentification);
  }

  public Device updateCommunicationNetworkInformation(
      final String organisationIdentification,
      final String deviceIdentification,
      final String ipAddress,
      final Integer btsId,
      final Integer cellId)
      throws FunctionalException {

    final Organisation organisation = this.findOrganisation(organisationIdentification);
    final Device device = this.findDevice(deviceIdentification);

    this.isAllowed(organisation, device, DeviceFunction.SET_COMMUNICATION_NETWORK_INFORMATION);

    if (ipAddress != null) {
      try {
        // Check if ip is valid format
        final InetAddress inetAddress = InetAddress.getByName(ipAddress);
        device.setNetworkAddress(inetAddress.getHostAddress());
      } catch (final UnknownHostException e) {
        LOGGER.error("Invalid ip address found {} for device {}", ipAddress, deviceIdentification);
        throw new FunctionalException(
            FunctionalExceptionType.INVALID_IP_ADDRESS, ComponentType.DOMAIN_SMART_METERING);
      }
    }

    if (btsId != null) {
      device.setBtsId(btsId);
    }

    if (cellId != null) {
      device.setCellId(cellId);
    }

    final Device updatedDevice = this.deviceRepository.save(device);
    LOGGER.info(
        "CommunicationNetworkInformation for Device {} updated to : ipAddress={}, btsId={}, cellId={} ",
        deviceIdentification,
        updatedDevice.getIpAddress(),
        updatedDevice.getBtsId(),
        updatedDevice.getCellId());

    return updatedDevice;
  }

  private Device findDevice(final String deviceIdentification) throws FunctionalException {
    return this.deviceDomainService.searchDevice(deviceIdentification);
  }

  private ProtocolInfo findProtocolInfo(
      final String protocol, final String protocolVersion, final String protocolVariant)
      throws FunctionalException {
    final ProtocolInfo protocolInfo =
        this.protocolRepository.findByProtocolAndProtocolVersionAndProtocolVariant(
            protocol, protocolVersion, protocolVariant);
    if (protocolInfo == null) {
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_PROTOCOL_NAME_OR_VERSION_OR_VARIANT,
          ComponentType.WS_ADMIN);
    }
    return protocolInfo;
  }

  private Organisation findOrganisation(final String organisationIdentification)
      throws FunctionalException {
    final Organisation organisation;
    try {
      organisation = this.organisationDomainService.searchOrganisation(organisationIdentification);
    } catch (final UnknownEntityException e) {
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_ORGANISATION, ComponentType.WS_ADMIN, e);
    }
    return organisation;
  }

  private void isAllowed(final Organisation organisation, final PlatformFunction platformFunction)
      throws FunctionalException {
    try {
      this.securityService.checkAuthorization(organisation, platformFunction);
    } catch (final NotAuthorizedException e) {
      throw new FunctionalException(
          FunctionalExceptionType.UNAUTHORIZED, ComponentType.WS_ADMIN, e);
    }
  }

  private void isAllowed(
      final Organisation organisation, final Device device, final DeviceFunction deviceFunction)
      throws FunctionalException {
    try {
      this.securityService.checkAuthorization(organisation, device, deviceFunction);
    } catch (final NotAuthorizedException e) {
      throw new FunctionalException(
          FunctionalExceptionType.UNAUTHORIZED, ComponentType.WS_ADMIN, e);
    }
  }
}
