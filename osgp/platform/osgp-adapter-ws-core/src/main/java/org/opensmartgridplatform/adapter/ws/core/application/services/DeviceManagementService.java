// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.application.services;

import static org.opensmartgridplatform.shared.utils.SearchUtil.replaceAndEscapeWildcards;
import static org.springframework.data.jpa.domain.Specification.where;

import jakarta.validation.Valid;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.QueryException;
import org.opensmartgridplatform.adapter.ws.core.application.criteria.SearchEventsCriteria;
import org.opensmartgridplatform.adapter.ws.core.infra.jms.CommonRequestMessage;
import org.opensmartgridplatform.adapter.ws.core.infra.jms.CommonRequestMessageSender;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceLifecycleStatus;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceAuthorizationRepository;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceRepository;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableSsldRepository;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.Ean;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.ScheduledTaskWithoutData;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.exceptions.ArgumentNullOrEmptyException;
import org.opensmartgridplatform.domain.core.exceptions.NotAuthorizedException;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.EventRepository;
import org.opensmartgridplatform.domain.core.repositories.OrganisationRepository;
import org.opensmartgridplatform.domain.core.repositories.ScheduledTaskWithoutDataRepository;
import org.opensmartgridplatform.domain.core.services.DeviceDomainService;
import org.opensmartgridplatform.domain.core.specifications.DeviceSpecifications;
import org.opensmartgridplatform.domain.core.specifications.EventSpecifications;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaSettings;
import org.opensmartgridplatform.domain.core.valueobjects.Certification;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceActivatedFilterType;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceExternalManagedFilterType;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFilter;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceInMaintenanceFilterType;
import org.opensmartgridplatform.domain.core.valueobjects.EventNotificationMessageDataContainer;
import org.opensmartgridplatform.domain.core.valueobjects.EventNotificationType;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformFunction;
import org.opensmartgridplatform.shared.application.config.PageSpecifier;
import org.opensmartgridplatform.shared.application.config.PagingSettings;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.utils.SearchUtil;
import org.opensmartgridplatform.shared.validation.Identification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service(value = "wsCoreDeviceManagementService")
@Validated
public class DeviceManagementService {
  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementService.class);

  @Autowired private PagingSettings pagingSettings;

  @Autowired private DomainHelperService domainHelperService;

  @Autowired private OrganisationRepository organisationRepository;

  @Autowired private EventSpecifications eventSpecifications;

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private DeviceSpecifications deviceSpecifications;

  @Autowired private FirmwareManagementService firmwareManagementService;

  @Autowired private EventRepository eventRepository;

  @Autowired private CorrelationIdProviderService correlationIdProviderService;

  @Autowired private CommonRequestMessageSender commonRequestMessageSender;

  @Autowired private ScheduledTaskWithoutDataRepository scheduledTaskRepository;

  @Autowired private WritableDeviceAuthorizationRepository writableAuthorizationRepository;

  @Autowired private WritableDeviceRepository writableDeviceRepository;

  @Autowired private WritableSsldRepository writableSsldRepository;

  @Autowired private DeviceDomainService deviceDomainService;

  @Autowired
  @Qualifier("wsCoreDeviceManagementNetManagementOrganisation")
  private String netManagementOrganisation;

  /** Constructor */
  public DeviceManagementService() {
    // Parameterless constructor required for transactions...
  }

  @Transactional(value = "transactionManager")
  public Organisation findOrganisation(
      @Identification final String organisationIdentification,
      @Identification final String organisationIdentificationToFind)
      throws FunctionalException {

    LOGGER.debug(
        "findOrganisation called with organisation {} and trying to find {}",
        organisationIdentification,
        organisationIdentificationToFind);

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_ORGANISATIONS);

    if (this.netManagementOrganisation.equals(organisationIdentification)
        || organisationIdentification.equals(organisationIdentificationToFind)) {
      return this.organisationRepository.findByOrganisationIdentification(
          organisationIdentificationToFind);
    } else {
      return null;
    }
  }

  @Transactional(value = "transactionManager")
  public List<Organisation> findAllOrganisations(
      @Identification final String organisationIdentification) throws FunctionalException {

    LOGGER.debug("findAllOrganisations called with organisation {}", organisationIdentification);

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_ORGANISATIONS);

    if (this.netManagementOrganisation.equals(organisationIdentification)) {
      return this.organisationRepository.findByOrderByOrganisationIdentification();
    } else {
      final Organisation org =
          this.organisationRepository.findByOrganisationIdentification(organisationIdentification);
      final List<Organisation> organisations = new ArrayList<>();
      organisations.add(org);
      return organisations;
    }
  }

  @Transactional(value = "transactionManager")
  public Page<Event> findEvents(final SearchEventsCriteria criteria) throws FunctionalException {

    final String organisationIdentification = criteria.getOrganisationIdentification();
    final String deviceIdentification = criteria.getDeviceIdentification();
    LOGGER.debug(
        "findEvents called for organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);

    this.pagingSettings.updatePagingSettings(criteria.getPageSpecifier());

    final PageRequest request =
        PageRequest.of(
            this.pagingSettings.getPageNumber(),
            this.pagingSettings.getPageSize(),
            Sort.Direction.DESC,
            "dateTime");

    Specification<Event> specification;

    if (deviceIdentification != null && !deviceIdentification.isEmpty()) {
      final Device device = this.domainHelperService.findDevice(deviceIdentification);
      this.domainHelperService.isAllowed(
          organisation, device, DeviceFunction.GET_EVENT_NOTIFICATIONS);

      specification = where(this.eventSpecifications.isFromDevice(deviceIdentification));
    } else {
      specification = where(this.eventSpecifications.isAuthorized(organisation));
    }

    final ZonedDateTime from = criteria.getFrom();
    if (from != null) {
      specification = specification.and(this.eventSpecifications.isCreatedAfter(from.toInstant()));
    }

    final ZonedDateTime until = criteria.getUntil();
    if (until != null) {
      specification =
          specification.and(this.eventSpecifications.isCreatedBefore(until.toInstant()));
    }

    specification =
        specification.and(this.eventSpecifications.hasEventTypes(criteria.getEventTypes()));
    specification =
        this.handleDescription(
            SearchUtil.replaceAndEscapeWildcards(criteria.getDescription()),
            SearchUtil.replaceAndEscapeWildcards(criteria.getDescriptionStartsWith()),
            specification);

    LOGGER.debug("request offset     : {}", request.getOffset());
    LOGGER.debug("        pageNumber : {}", request.getPageNumber());
    LOGGER.debug("        pageSize   : {}", request.getPageSize());
    LOGGER.debug("        sort       : {}", request.getSort());

    return this.eventRepository.findAll(specification, request);
  }

  private Specification<Event> handleDescription(
      final String description,
      final String descriptionStartsWith,
      final Specification<Event> specification) {

    final Specification<Event> descriptionSpecification =
        this.eventSpecifications.withDescription(description);
    final Specification<Event> descriptionStartsWithSpecification =
        this.eventSpecifications.startsWithDescription(descriptionStartsWith);

    if (description == null && descriptionStartsWith == null) {
      return specification;
    }
    if (description == null) {
      return specification.and(descriptionStartsWithSpecification);
    }
    if (descriptionStartsWith == null) {
      return specification.and(descriptionSpecification);
    }
    return specification.and(descriptionSpecification.or(descriptionStartsWithSpecification));
  }

  /**
   * Find all devices
   *
   * @param organisationIdentification The organisation who performed the action
   * @param pageSpecifier The page to be returned
   * @param deviceFilter the filter object
   * @return A page with devices
   * @throws FunctionalException
   */
  @Transactional(value = "transactionManager")
  public Page<Device> findDevices(
      @Identification final String organisationIdentification,
      final PageSpecifier pageSpecifier,
      final DeviceFilter deviceFilter)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.FIND_DEVICES);
    this.pagingSettings.updatePagingSettings(pageSpecifier);
    Sort.Direction sortDir = Sort.Direction.DESC;
    String sortedBy = "creationTime";
    if (deviceFilter != null) {
      if (!StringUtils.isEmpty(deviceFilter.getSortDir())
          && deviceFilter.getSortDir().contains("asc")) {
        sortDir = Sort.Direction.ASC;
      }
      if (!StringUtils.isEmpty(deviceFilter.getSortedBy())) {
        sortedBy = deviceFilter.getSortedBy();
      }
    }

    final PageRequest request =
        PageRequest.of(
            this.pagingSettings.getPageNumber(),
            this.pagingSettings.getPageSize(),
            sortDir,
            sortedBy);

    final Page<Device> devices =
        this.findDevices(organisationIdentification, deviceFilter, organisation, request);

    if (devices == null) {
      LOGGER.info("No devices found");
      return null;
    }

    for (final Device device : devices.getContent()) {
      for (final DeviceAuthorization deviceAutorization : device.getAuthorizations()) {
        device.addOrganisation(
            deviceAutorization.getOrganisation().getOrganisationIdentification());
      }
    }

    return devices;
  }

  private Page<Device> findDevices(
      final String organisationIdentification,
      final DeviceFilter deviceFilter,
      final Organisation organisation,
      final PageRequest request) {
    final Page<Device> devices;
    try {
      if (!this.netManagementOrganisation.equals(organisationIdentification)) {
        // Municipality organization.
        if (deviceFilter == null) {
          final DeviceFilter df = new DeviceFilter();
          df.setOrganisationIdentification(organisationIdentification);
          df.setDeviceExternalManaged(DeviceExternalManagedFilterType.BOTH);
          df.setDeviceActivated(DeviceActivatedFilterType.BOTH);
          df.setDeviceInMaintenance(DeviceInMaintenanceFilterType.BOTH);
          df.setHasTechnicalInstallation(false);
          df.setExactMatch(false);
          devices = this.applyFilter(df, organisation, request);
        } else {
          deviceFilter.setOrganisationIdentification(organisationIdentification);
          devices = this.applyFilter(deviceFilter, organisation, request);
        }
      } else {
        // Net management organization.
        devices = this.applyFilter(deviceFilter, organisation, request);
      }
    } catch (final ArgumentNullOrEmptyException e) {
      /*
       * The implementation of applyFilter should check everything passed
       * on to DeviceSpecifications for not being empty, thus avoiding
       * ArgumentNullOrEmptyException. If something is missed (which
       * should not occur) pass it on as IllegalArgumentException to avoid
       * multiple checked exceptions being thrown.
       */
      throw new IllegalArgumentException("Null or empty input provided to DeviceSpecifications", e);
    }
    return devices;
  }

  @Transactional(value = "transactionManager")
  public Page<Device> applyFilter(
      final DeviceFilter deviceFilter, final Organisation organisation, final PageRequest request)
      throws ArgumentNullOrEmptyException {
    Page<Device> devices = null;

    try {
      if (deviceFilter != null) {
        final Specification<Device> specification = this.doApplyFilter(deviceFilter, organisation);
        devices = this.deviceRepository.findAll(specification, request);
      } else {
        if (organisation != null) {
          final Specification<Device> specification =
              Specification.where(this.deviceSpecifications.forOrganisation(organisation));
          devices = this.deviceRepository.findAll(specification, request);
        } else {
          devices = this.deviceRepository.findAll(request);
        }
      }
    } catch (final FunctionalException functionalException) {
      LOGGER.error("FunctionalException", functionalException);
    } catch (final QueryException e) {
      LOGGER.error("QueryException", e);
    }

    return devices;
  }

  private Specification<Device> doApplyFilter(
      final DeviceFilter deviceFilter, final Organisation organisation)
      throws FunctionalException, ArgumentNullOrEmptyException {

    Specification<Device> specification =
        this.doFilterOnOrganisationIdentification(deviceFilter, organisation);
    specification = this.doFilterOnDeviceIdentification(deviceFilter, specification);
    specification = this.doFilterOnDeviceAlias(deviceFilter, specification);
    specification = this.doFilterOnAddress(deviceFilter, specification);
    specification = this.doFilterOnExternalManaged(deviceFilter, specification);
    specification = this.doFilterOnActivated(deviceFilter, specification);
    specification = this.doFilterOnInMaintenance(deviceFilter, specification);
    specification = this.doFilterOnHasTechnicalInstallationDate(deviceFilter, specification);
    specification = this.doFilterOnOwner(deviceFilter, specification);
    specification = this.doFilterOnDeviceType(deviceFilter, specification);
    specification = this.doFilterOnDeviceModel(deviceFilter, specification);
    specification = this.doFilterOnManufacturer(deviceFilter, specification);
    specification = this.doFilterOnFirmwareModuleVersion(deviceFilter, specification);
    specification = this.doFilterOnDeviceIdentificationsToUse(deviceFilter, specification);
    specification = this.doFilterOnDeviceIdentificationsToExclude(deviceFilter, specification);

    return specification;
  }

  private Specification<Device> doFilterOnDeviceIdentificationsToExclude(
      final DeviceFilter deviceFilter, Specification<Device> specification)
      throws ArgumentNullOrEmptyException {
    if (deviceFilter.getDeviceIdentificationsToExclude() != null
        && !deviceFilter.getDeviceIdentificationsToExclude().isEmpty()) {
      specification =
          specification.and(
              this.deviceSpecifications.excludeDeviceIdentificationList(
                  deviceFilter.getDeviceIdentificationsToExclude()));
    }
    return specification;
  }

  private Specification<Device> doFilterOnDeviceIdentificationsToUse(
      final DeviceFilter deviceFilter, Specification<Device> specification)
      throws ArgumentNullOrEmptyException {
    if (deviceFilter.getDeviceIdentificationsToUse() != null
        && !deviceFilter.getDeviceIdentificationsToUse().isEmpty()) {
      specification =
          specification.and(
              this.deviceSpecifications.existsInDeviceIdentificationList(
                  deviceFilter.getDeviceIdentificationsToUse()));
    }
    return specification;
  }

  private Specification<Device> doFilterOnFirmwareModuleVersion(
      final DeviceFilter deviceFilter, Specification<Device> specification)
      throws ArgumentNullOrEmptyException {
    if (!StringUtils.isEmpty(deviceFilter.getFirmwareModuleVersion())) {
      specification =
          specification.and(
              this.deviceSpecifications.forFirmwareModuleVersion(
                  deviceFilter.getFirmwareModuleType(),
                  replaceAndEscapeWildcards(deviceFilter.getFirmwareModuleVersion())
                      .toUpperCase()));
    }
    return specification;
  }

  private Specification<Device> doFilterOnManufacturer(
      final DeviceFilter deviceFilter, Specification<Device> specification)
      throws ArgumentNullOrEmptyException {
    if (!StringUtils.isEmpty(deviceFilter.getManufacturer())) {
      final Manufacturer manufacturer =
          this.firmwareManagementService.findManufacturer(deviceFilter.getManufacturer());
      specification = specification.and(this.deviceSpecifications.forManufacturer(manufacturer));
    }
    return specification;
  }

  private Specification<Device> doFilterOnDeviceModel(
      final DeviceFilter deviceFilter, Specification<Device> specification)
      throws ArgumentNullOrEmptyException {
    if (!StringUtils.isEmpty(deviceFilter.getModel())) {
      specification =
          specification.and(
              this.deviceSpecifications.forDeviceModel(
                  replaceAndEscapeWildcards(deviceFilter.getModel()).toUpperCase()));
    }
    return specification;
  }

  private Specification<Device> doFilterOnDeviceType(
      final DeviceFilter deviceFilter, Specification<Device> specification)
      throws ArgumentNullOrEmptyException {
    if (!StringUtils.isEmpty(deviceFilter.getDeviceType())) {
      specification =
          specification.and(
              this.deviceSpecifications.forDeviceType(
                  replaceAndEscapeWildcards(deviceFilter.getDeviceType()).toUpperCase()));
    }
    return specification;
  }

  private Specification<Device> doFilterOnOwner(
      final DeviceFilter deviceFilter, Specification<Device> specification)
      throws ArgumentNullOrEmptyException {
    if (!StringUtils.isEmpty(deviceFilter.getOwner())) {
      specification =
          specification.and(
              this.deviceSpecifications.forOwner(
                  replaceAndEscapeWildcards(deviceFilter.getOwner()).toUpperCase()));
    }
    return specification;
  }

  private Specification<Device> doFilterOnHasTechnicalInstallationDate(
      final DeviceFilter deviceFilter, Specification<Device> specification)
      throws ArgumentNullOrEmptyException {
    if (deviceFilter.isHasTechnicalInstallation()) {
      specification = specification.and(this.deviceSpecifications.hasTechnicalInstallationDate());
    }
    return specification;
  }

  private Specification<Device> doFilterOnInMaintenance(
      final DeviceFilter deviceFilter, Specification<Device> specification)
      throws ArgumentNullOrEmptyException {
    if (deviceFilter.getDeviceInMaintenance() != null
        && !DeviceInMaintenanceFilterType.BOTH.equals(deviceFilter.getDeviceInMaintenance())) {
      specification =
          specification.and(
              this.deviceSpecifications.isInMaintenance(
                  deviceFilter.getDeviceInMaintenance().getValue()));
    }
    return specification;
  }

  private Specification<Device> doFilterOnActivated(
      final DeviceFilter deviceFilter, Specification<Device> specification)
      throws ArgumentNullOrEmptyException {
    if (deviceFilter.getDeviceActivated() != null
        && !DeviceActivatedFilterType.BOTH.equals(deviceFilter.getDeviceActivated())) {
      specification =
          specification.and(
              this.deviceSpecifications.isActived(deviceFilter.getDeviceActivated().getValue()));
    }
    return specification;
  }

  private Specification<Device> doFilterOnExternalManaged(
      final DeviceFilter deviceFilter, Specification<Device> specification)
      throws ArgumentNullOrEmptyException {
    if (deviceFilter.getDeviceExternalManaged() != null
        && !DeviceExternalManagedFilterType.BOTH.equals(deviceFilter.getDeviceExternalManaged())) {
      specification =
          specification.and(
              this.deviceSpecifications.isManagedExternally(
                  deviceFilter.getDeviceExternalManaged().getValue()));
    }
    return specification;
  }

  private Specification<Device> doFilterOnAddress(
      final DeviceFilter deviceFilter, Specification<Device> specification)
      throws ArgumentNullOrEmptyException {
    if (!StringUtils.isEmpty(deviceFilter.getCity())) {
      specification =
          specification.and(
              this.deviceSpecifications.hasCity(
                  replaceAndEscapeWildcards(deviceFilter.getCity()).toUpperCase()));
    }
    if (!StringUtils.isEmpty(deviceFilter.getPostalCode())) {
      specification =
          specification.and(
              this.deviceSpecifications.hasPostalCode(
                  replaceAndEscapeWildcards(deviceFilter.getPostalCode()).toUpperCase()));
    }
    if (!StringUtils.isEmpty(deviceFilter.getStreet())) {
      specification =
          specification.and(
              this.deviceSpecifications.hasStreet(
                  replaceAndEscapeWildcards(deviceFilter.getStreet()).toUpperCase()));
    }
    if (!StringUtils.isEmpty(deviceFilter.getNumber())) {
      specification =
          specification.and(
              this.deviceSpecifications.hasNumber(
                  replaceAndEscapeWildcards(deviceFilter.getNumber()).toUpperCase()));
    }
    if (!StringUtils.isEmpty(deviceFilter.getMunicipality())) {
      specification =
          specification.and(
              this.deviceSpecifications.hasMunicipality(
                  replaceAndEscapeWildcards(deviceFilter.getMunicipality()).toUpperCase()));
    }
    return specification;
  }

  private Specification<Device> doFilterOnDeviceAlias(
      final DeviceFilter deviceFilter, Specification<Device> specification)
      throws ArgumentNullOrEmptyException {
    if (!StringUtils.isEmpty(deviceFilter.getAlias())) {
      specification =
          specification.and(
              this.deviceSpecifications.hasAlias(
                  replaceAndEscapeWildcards(deviceFilter.getAlias()).toUpperCase()));
    }
    return specification;
  }

  private Specification<Device> doFilterOnDeviceIdentification(
      final DeviceFilter deviceFilter, Specification<Device> specification)
      throws ArgumentNullOrEmptyException {
    if (!StringUtils.isEmpty(deviceFilter.getDeviceIdentification())) {
      String searchString = deviceFilter.getDeviceIdentification();

      if (!deviceFilter.isExactMatch()) {
        searchString = replaceAndEscapeWildcards(searchString).toUpperCase();
      }

      specification =
          specification.and(
              this.deviceSpecifications.hasDeviceIdentification(
                  searchString, deviceFilter.isExactMatch()));
    }
    return specification;
  }

  private Specification<Device> doFilterOnOrganisationIdentification(
      final DeviceFilter deviceFilter, final Organisation organisation)
      throws FunctionalException, ArgumentNullOrEmptyException {
    final Specification<Device> specification;
    if (!StringUtils.isEmpty(deviceFilter.getOrganisationIdentification())) {
      final Organisation org =
          this.domainHelperService.findOrganisation(deviceFilter.getOrganisationIdentification());
      specification = where(this.deviceSpecifications.forOrganisation(org));
    } else {
      // dummy for 'not initialized'
      specification = where(this.deviceSpecifications.forOrganisation(organisation));
    }
    return specification;
  }

  // === SET EVENT NOTIFICATIONS ===
  @Transactional(value = "transactionManager")
  public String enqueueSetEventNotificationsRequest(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      final List<EventNotificationType> eventNotifications,
      final int messagePriority)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.isAllowed(
        organisation, device, DeviceFunction.SET_EVENT_NOTIFICATIONS);
    this.domainHelperService.isInMaintenance(device);

    LOGGER.debug(
        "enqueueSetEventNotificationsRequest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final EventNotificationMessageDataContainer eventNotificationMessageDataContainer =
        new EventNotificationMessageDataContainer(eventNotifications);

    final MessageMetadata messageMetadata =
        new MessageMetadata.Builder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(MessageType.SET_EVENT_NOTIFICATIONS.name())
            .withMessagePriority(messagePriority)
            .build();

    final CommonRequestMessage message =
        new CommonRequestMessage.Builder()
            .messageMetadata(messageMetadata)
            .request(eventNotificationMessageDataContainer)
            .build();

    this.commonRequestMessageSender.send(message);

    return correlationUid;
  }

  @Transactional(value = "transactionManager")
  // === RETRIEVE SCHEDULED TASKS LIST FOR SPECIFIC DEVICE ===
  public List<ScheduledTaskWithoutData> findScheduledTasks(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.isAllowed(organisation, device, DeviceFunction.FIND_SCHEDULED_TASKS);

    return this.scheduledTaskRepository.findByDeviceIdentification(deviceIdentification);
  }

  @Transactional(value = "transactionManager")
  // === RETRIEVE SCHEDULED TASKS LIST FOR ALL DEVICES ===
  public List<ScheduledTaskWithoutData> findScheduledTasks(
      @Identification final String organisationIdentification) throws FunctionalException {
    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.FIND_SCHEDULED_TASKS);
    return this.scheduledTaskRepository.findByOrganisationIdentification(
        organisationIdentification);
  }

  @Transactional(value = "writableTransactionManager")
  public void updateDevice(
      @Identification final String organisationIdentification,
      final String deviceToUpdateIdentification,
      @Valid final Ssld updateDevice)
      throws FunctionalException {

    final Device existingDevice =
        this.writableDeviceRepository.findByDeviceIdentification(deviceToUpdateIdentification);
    if (existingDevice == null) {
      // device does not exist
      LOGGER.info("Device does not exist, nothing to update.");
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_DEVICE,
          ComponentType.WS_CORE,
          new UnknownEntityException(Device.class, deviceToUpdateIdentification));
    }

    final List<DeviceAuthorization> owners =
        this.writableAuthorizationRepository.findByDeviceAndFunctionGroup(
            existingDevice, DeviceFunctionGroup.OWNER);

    // Check organisation against owner of device
    boolean isOwner = false;
    for (final DeviceAuthorization owner : owners) {
      if (owner
          .getOrganisation()
          .getOrganisationIdentification()
          .equalsIgnoreCase(organisationIdentification)) {
        isOwner = true;
      }
    }

    if (!isOwner) {
      LOGGER.info("Device has no owner yet, or organisation is not the owner.");
      throw new FunctionalException(
          FunctionalExceptionType.UNAUTHORIZED,
          ComponentType.WS_CORE,
          new NotAuthorizedException(organisationIdentification));
    }

    // Update the device
    existingDevice.updateMetaData(
        updateDevice.getAlias(),
        updateDevice.getContainerAddress(),
        updateDevice.getGpsCoordinates());

    existingDevice.setActivated(updateDevice.isActivated());

    if (updateDevice.getDeviceLifecycleStatus() != null) {
      existingDevice.setDeviceLifecycleStatus(updateDevice.getDeviceLifecycleStatus());
    }

    if (updateDevice.getTechnicalInstallationDate() != null) {
      existingDevice.setTechnicalInstallationDate(updateDevice.getTechnicalInstallationDate());
    }

    final Ssld ssld =
        this.writableSsldRepository
            .findById(existingDevice.getId())
            .orElseThrow(
                () ->
                    new FunctionalException(
                        FunctionalExceptionType.UNKNOWN_DEVICE, ComponentType.WS_CORE));
    ssld.updateOutputSettings(updateDevice.receiveOutputSettings());
    ssld.setEans(updateDevice.getEans());

    for (final Ean ean : updateDevice.getEans()) {
      ean.setDevice(ssld);
    }

    this.writableSsldRepository.save(ssld);
  }

  @Transactional(value = "writableTransactionManager")
  public void setDeviceAlias(
      @Identification final String organisationIdentification,
      final String deviceIdentification,
      final String deviceAlias,
      final List<DeviceOutputSetting> newDeviceOutputSettings)
      throws FunctionalException {

    final Ssld existingSsld =
        this.writableSsldRepository.findByDeviceIdentification(deviceIdentification);

    if (existingSsld == null) {
      // device does not exist
      LOGGER.info("Device does not exist, cannot set Alias.");
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_DEVICE,
          ComponentType.WS_CORE,
          new UnknownEntityException(Device.class, deviceIdentification));
    }

    // Check to see if the organization is authorized for SET_DEVICE_ALIASES
    final Organisation organisation =
        this.organisationRepository.findByOrganisationIdentification(organisationIdentification);
    this.domainHelperService.isAllowed(
        organisation, existingSsld, DeviceFunction.SET_DEVICE_ALIASES);

    if (deviceAlias != null) {
      existingSsld.setAlias(deviceAlias);
      this.writableDeviceRepository.save(existingSsld);
    }

    if (newDeviceOutputSettings != null && !newDeviceOutputSettings.isEmpty()) {
      this.updateRelayAliases(newDeviceOutputSettings, existingSsld);
    }
  }

  private void updateRelayAliases(
      final List<DeviceOutputSetting> newDeviceOutputSettings, final Ssld ssld)
      throws FunctionalException {

    final List<org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting>
        currentOutputSettings = ssld.getOutputSettings();

    if (currentOutputSettings == null || currentOutputSettings.isEmpty()) {
      LOGGER.info("Trying to set relay alias(es) for a device without output settings");
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_DEVICE_OUTPUT_SETTINGS, ComponentType.WS_CORE);
    }

    for (final DeviceOutputSetting newSetting : newDeviceOutputSettings) {
      boolean outputSettingFound = false;
      for (final org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting oldSetting :
          currentOutputSettings) {
        if (oldSetting.getExternalId() == newSetting.getExternalId()) {
          oldSetting.setAlias(newSetting.getAlias());
          outputSettingFound = true;
        }
      }
      if (!outputSettingFound) {
        LOGGER.info(
            "Trying to set alias {} for internal relay {}, which has no output settings",
            newSetting.getAlias(),
            newSetting.getInternalId());
        throw new FunctionalException(
            FunctionalExceptionType.UNKNOWN_DEVICE_OUTPUT_SETTINGS, ComponentType.WS_CORE);
      }
    }

    this.writableSsldRepository.save(ssld);
  }

  @Transactional(value = "writableTransactionManager")
  public void setMaintenanceStatus(
      @Identification final String organisationIdentification,
      final String deviceIdentification,
      final boolean status)
      throws FunctionalException {

    final Device existingDevice =
        this.writableDeviceRepository.findByDeviceIdentification(deviceIdentification);

    if (existingDevice == null) {
      // device does not exist
      LOGGER.info("Device does not exist, cannot set maintenance status.");
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_DEVICE,
          ComponentType.WS_CORE,
          new UnknownEntityException(Device.class, deviceIdentification));
    } else {

      // Check to see if the organisation is CONFIGURATION or OWNER
      // authorized
      boolean isAuthorized = false;
      for (final DeviceAuthorization authorizations : existingDevice.getAuthorizations()) {
        if (organisationIdentification.equals(
                authorizations.getOrganisation().getOrganisationIdentification())
            && (DeviceFunctionGroup.OWNER.equals(authorizations.getFunctionGroup())
                || DeviceFunctionGroup.CONFIGURATION.equals(authorizations.getFunctionGroup()))) {
          isAuthorized = true;
          existingDevice.updateInMaintenance(status);
          this.writableDeviceRepository.save(existingDevice);
          break;
        }
      }

      if (!isAuthorized) {
        // unauthorized, throwing exception.
        throw new FunctionalException(
            FunctionalExceptionType.UNAUTHORIZED,
            ComponentType.WS_CORE,
            new NotAuthorizedException(organisationIdentification));
      }
    }
  }

  public String enqueueUpdateDeviceSslCertificationRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final Certification certification,
      final int messagePriority)
      throws FunctionalException {
    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.isAllowed(
        organisation, device, DeviceFunction.UPDATE_DEVICE_SSL_CERTIFICATION);
    this.domainHelperService.isInMaintenance(device);

    LOGGER.debug(
        "enqueueUpdateDeviceSslCertificationRequest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final MessageMetadata messageMetadata =
        new MessageMetadata.Builder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(MessageType.UPDATE_DEVICE_SSL_CERTIFICATION.name())
            .withMessagePriority(messagePriority)
            .build();

    final CommonRequestMessage message =
        new CommonRequestMessage.Builder()
            .messageMetadata(messageMetadata)
            .request(certification)
            .build();

    this.commonRequestMessageSender.send(message);

    return correlationUid;
  }

  public String enqueueSetDeviceVerificationKeyRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final String verificationKey,
      final int messagePriority)
      throws FunctionalException {
    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.isAllowed(
        organisation, device, DeviceFunction.SET_DEVICE_VERIFICATION_KEY);
    this.domainHelperService.isInMaintenance(device);

    LOGGER.debug(
        "enqueueSetDeviceVerificationKeyRequest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final MessageMetadata messageMetadata =
        new MessageMetadata.Builder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(MessageType.SET_DEVICE_VERIFICATION_KEY.name())
            .withMessagePriority(messagePriority)
            .build();

    final CommonRequestMessage message =
        new CommonRequestMessage.Builder()
            .messageMetadata(messageMetadata)
            .request(verificationKey)
            .build();

    this.commonRequestMessageSender.send(message);

    return correlationUid;
  }

  public String enqueueSetDeviceLifecycleStatusRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final DeviceLifecycleStatus deviceLifecycleStatus)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.deviceDomainService.searchDevice(deviceIdentification);

    this.domainHelperService.isAllowed(
        organisation, device, DeviceFunction.SET_DEVICE_LIFECYCLE_STATUS);

    LOGGER.debug(
        "enqueueSetDeviceLifecycleStatusRequest called with organisation {}, deviceLifecycleStatus {} and deviceIdentifcation {}",
        organisationIdentification,
        deviceLifecycleStatus.name(),
        deviceIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus
        newDeviceLifecycleStatus =
            org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus.valueOf(
                deviceLifecycleStatus.name());

    final MessageMetadata messageMetadata =
        new MessageMetadata.Builder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(MessageType.SET_DEVICE_LIFECYCLE_STATUS.name())
            .build();

    final CommonRequestMessage message =
        new CommonRequestMessage.Builder()
            .messageMetadata(messageMetadata)
            .request(newDeviceLifecycleStatus)
            .build();

    this.commonRequestMessageSender.send(message);

    return correlationUid;
  }

  public String enqueueUpdateDeviceCdmaSettingsRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final CdmaSettings cdmaSettings)
      throws FunctionalException {
    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.deviceDomainService.searchDevice(deviceIdentification);

    this.domainHelperService.isAllowed(
        organisation, device, DeviceFunction.UPDATE_DEVICE_CDMA_SETTINGS);

    LOGGER.debug(
        "enqueueUpdateDeviceCdmaSettingsRequest called with organisation {}, deviceIdentification {}, and {}",
        organisationIdentification,
        deviceIdentification,
        cdmaSettings);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final MessageMetadata messageMetadata =
        new MessageMetadata.Builder()
            .withDeviceIdentification(deviceIdentification)
            .withOrganisationIdentification(organisationIdentification)
            .withCorrelationUid(correlationUid)
            .withMessageType(MessageType.UPDATE_DEVICE_CDMA_SETTINGS.name())
            .build();

    final CommonRequestMessage message =
        new CommonRequestMessage.Builder()
            .messageMetadata(messageMetadata)
            .request(cdmaSettings)
            .build();

    this.commonRequestMessageSender.send(message);

    return correlationUid;
  }
}
