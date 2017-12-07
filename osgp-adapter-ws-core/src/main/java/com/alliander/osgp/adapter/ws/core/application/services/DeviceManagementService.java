/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.services;

import static org.springframework.data.jpa.domain.Specifications.where;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.QueryException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.core.infra.jms.CommonRequestMessage;
import com.alliander.osgp.adapter.ws.core.infra.jms.CommonRequestMessageSender;
import com.alliander.osgp.adapter.ws.core.infra.jms.CommonRequestMessageType;
import com.alliander.osgp.adapter.ws.core.infra.jms.CommonResponseMessageFinder;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceLifecycleStatus;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceAuthorizationRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceRepository;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableSsldRepository;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.Ean;
import com.alliander.osgp.domain.core.entities.Event;
import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.ScheduledTask;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.exceptions.ArgumentNullOrEmptyException;
import com.alliander.osgp.domain.core.exceptions.NotAuthorizedException;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.EventRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.repositories.ScheduledTaskRepository;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.services.DeviceDomainService;
import com.alliander.osgp.domain.core.specifications.DeviceSpecifications;
import com.alliander.osgp.domain.core.specifications.EventSpecifications;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.Certification;
import com.alliander.osgp.domain.core.valueobjects.DeviceActivatedFilterType;
import com.alliander.osgp.domain.core.valueobjects.DeviceExternalManagedFilterType;
import com.alliander.osgp.domain.core.valueobjects.DeviceFilter;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;
import com.alliander.osgp.domain.core.valueobjects.DeviceInMaintenanceFilterType;
import com.alliander.osgp.domain.core.valueobjects.EventNotificationMessageDataContainer;
import com.alliander.osgp.domain.core.valueobjects.EventNotificationType;
import com.alliander.osgp.domain.core.valueobjects.EventType;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunction;
import com.alliander.osgp.logging.domain.entities.DeviceLogItem;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;
import com.alliander.osgp.shared.application.config.PagingSettings;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

@Service(value = "wsCoreDeviceManagementService")
@Validated
public class DeviceManagementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementService.class);

    // The wildcard, used for filtering
    private static final String WILDCARD = "\\*";

    @Autowired
    private PagingSettings pagingSettings;

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private EventSpecifications eventSpecifications;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceSpecifications deviceSpecifications;

    @Autowired
    private FirmwareManagementService firmwareManagementService;

    @Autowired
    private DeviceLogItemRepository logItemRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private CommonRequestMessageSender commonRequestMessageSender;

    @Autowired
    private CommonResponseMessageFinder commonResponseMessageFinder;

    @Autowired
    private ScheduledTaskRepository scheduledTaskRepository;

    @Autowired
    private WritableDeviceAuthorizationRepository writableAuthorizationRepository;

    @Autowired
    private WritableDeviceRepository writableDeviceRepository;

    @Autowired
    private WritableSsldRepository writableSsldRepository;

    @Autowired
    private DeviceDomainService deviceDomainService;

    @Autowired
    @Qualifier("wsCoreDeviceManagementNetManagementOrganisation")
    private String netManagementOrganisation;

    /**
     * Constructor
     */
    public DeviceManagementService() {
        // Parameterless constructor required for transactions...
    }

    @Transactional(value = "transactionManager")
    public List<Organisation> findAllOrganisations(@Identification final String organisationIdentification)
            throws FunctionalException {

        LOGGER.debug("findAllOrganisations called with organisation {}", organisationIdentification);

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_ORGANISATIONS);

        if (this.netManagementOrganisation.equals(organisationIdentification)) {
            return this.organisationRepository.findAll();
        } else {
            final Organisation org = this.organisationRepository
                    .findByOrganisationIdentification(organisationIdentification);
            final List<Organisation> organisations = new ArrayList<>();
            organisations.add(org);
            return organisations;
        }
    }

    @Transactional(value = "readableTransactionManager")
    public Page<DeviceLogItem> findDeviceMessages(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, @Min(value = 0) final int pageNumber)
            throws FunctionalException {

        LOGGER.debug("findOslpMessage called with organisation {}, device {} and pagenumber {}",
                new Object[] { organisationIdentification, deviceIdentification, pageNumber });

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_MESSAGES);

        final PageRequest request = new PageRequest(pageNumber, this.pagingSettings.getMaximumPageSize(),
                Sort.Direction.DESC, "modificationTime");

        if (deviceIdentification != null && !deviceIdentification.isEmpty()) {
            return this.logItemRepository.findByDeviceIdentification(deviceIdentification, request);
        }

        return this.logItemRepository.findAll(request);
    }

    @Transactional(value = "transactionManager")
    public Page<Event> findEvents(@Identification final String organisationIdentification,
            final String deviceIdentification, final Integer pageSize, final Integer pageNumber, final DateTime from,
            final DateTime until, final List<EventType> eventTypes) throws FunctionalException {

        LOGGER.debug("findEvents called for organisation {} and device {}", organisationIdentification,
                deviceIdentification);

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);

        this.pagingSettings.updatePagingSettings(pageSize, pageNumber);

        final PageRequest request = new PageRequest(this.pagingSettings.getPageNumber(),
                this.pagingSettings.getPageSize(), Sort.Direction.DESC, "dateTime");

        Specifications<Event> specifications = null;

        try {
            if (deviceIdentification != null && !deviceIdentification.isEmpty()) {
                final Device device = this.domainHelperService.findDevice(deviceIdentification);
                this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_EVENT_NOTIFICATIONS);

                specifications = where(this.eventSpecifications.isFromDevice(device));
            } else {
                specifications = where(this.eventSpecifications.isAuthorized(organisation));
            }

            if (from != null) {
                specifications = specifications.and(this.eventSpecifications.isCreatedAfter(from.toDate()));
            }

            if (until != null) {
                specifications = specifications.and(this.eventSpecifications.isCreatedBefore(until.toDate()));
            }

            if (eventTypes != null && !eventTypes.isEmpty()) {
                specifications = specifications.and(this.eventSpecifications.hasEventTypes(eventTypes));
            }
        } catch (final ArgumentNullOrEmptyException e) {
            throw new FunctionalException(FunctionalExceptionType.ARGUMENT_NULL, ComponentType.WS_CORE, e);
        }

        LOGGER.debug("request offset     : {}", request.getOffset());
        LOGGER.debug("        pageNumber : {}", request.getPageNumber());
        LOGGER.debug("        pageSize   : {}", request.getPageSize());
        LOGGER.debug("        sort       : {}", request.getSort());

        return this.eventRepository.findAll(specifications, request);
    }

    /**
     * Find all devices
     *
     * @param organisationIdentification
     *            The organisation who performed the action
     * @param pageSize
     *            The page size
     * @param pageNumber
     *            the page to be returned.
     * @param deviceFilter
     *            the filter object
     *
     * @return A page with devices
     *
     * @throws FunctionalException
     * @throws ArgumentNullOrEmptyException
     */
    @Transactional(value = "transactionManager")
    public Page<Device> findDevices(@Identification final String organisationIdentification, final Integer pageSize,
            final Integer pageNumber, final DeviceFilter deviceFilter)
            throws FunctionalException, ArgumentNullOrEmptyException {
        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.FIND_DEVICES);
        this.pagingSettings.updatePagingSettings(pageSize, pageNumber);
        Sort.Direction sortDir = Sort.Direction.DESC;
        String sortedBy = "creationTime";
        if (deviceFilter != null) {
            if (!StringUtils.isEmpty(deviceFilter.getSortDir()) && deviceFilter.getSortDir().contains("asc")) {
                sortDir = Sort.Direction.ASC;
            }
            if (!StringUtils.isEmpty(deviceFilter.getSortedBy())) {
                sortedBy = deviceFilter.getSortedBy();
            }
        }

        final PageRequest request = new PageRequest(this.pagingSettings.getPageNumber(),
                this.pagingSettings.getPageSize(), sortDir, sortedBy);

        Page<Device> devices = null;
        if (!this.netManagementOrganisation.equals(organisationIdentification)) {
            if (deviceFilter == null) {
                final DeviceFilter df = new DeviceFilter(organisationIdentification, null, null, null, null, null, null,
                        null, DeviceExternalManagedFilterType.BOTH, DeviceActivatedFilterType.BOTH,
                        DeviceInMaintenanceFilterType.BOTH, null, null, false, null, null, null, null, null, null,
                        false, null, null);
                devices = this.applyFilter(df, organisation, request);
            } else {
                deviceFilter.updateOrganisationIdentification(organisationIdentification);
                devices = this.applyFilter(deviceFilter, organisation, request);
            }
        } else {
            devices = this.applyFilter(deviceFilter, organisation, request);
        }

        if (devices == null) {
            LOGGER.info("No devices found");
            return null;
        }

        for (final Device device : devices.getContent()) {
            for (final DeviceAuthorization deviceAutorization : device.getAuthorizations()) {
                device.addOrganisation(deviceAutorization.getOrganisation().getOrganisationIdentification());
            }
        }

        return devices;
    }

    @Transactional(value = "transactionManager")
    public Page<Device> applyFilter(final DeviceFilter deviceFilter, final Organisation organisation,
            final PageRequest request) throws ArgumentNullOrEmptyException {
        Page<Device> devices = null;

        try {
            if (deviceFilter != null) {
                Specifications<Device> specifications;

                if (!StringUtils.isEmpty(deviceFilter.getOrganisationIdentification())) {
                    final Organisation org = this.domainHelperService
                            .findOrganisation(deviceFilter.getOrganisationIdentification());
                    specifications = where(this.deviceSpecifications.forOrganisation(org));
                } else {
                    // dummy for 'not initialized'
                    specifications = where(this.deviceSpecifications.forOrganisation(organisation));
                }
                if (!StringUtils.isEmpty(deviceFilter.getDeviceIdentification())) {
                    String searchString = deviceFilter.getDeviceIdentification();

                    if (!deviceFilter.isExactMatch()) {
                        searchString = searchString.replaceAll(WILDCARD, "%") + "%";
                    }

                    specifications = specifications.and(this.deviceSpecifications.hasDeviceIdentification(searchString,
                            deviceFilter.isExactMatch()));
                }
                if (!StringUtils.isEmpty(deviceFilter.getAlias())) {
                    specifications = specifications.and(this.deviceSpecifications
                            .hasAlias(deviceFilter.getAlias().replaceAll(WILDCARD, "%") + "%"));
                }
                if (!StringUtils.isEmpty(deviceFilter.getCity())) {
                    specifications = specifications.and(
                            this.deviceSpecifications.hasCity(deviceFilter.getCity().replaceAll(WILDCARD, "%") + "%"));
                }
                if (!StringUtils.isEmpty(deviceFilter.getPostalCode())) {
                    specifications = specifications.and(this.deviceSpecifications
                            .hasPostalCode(deviceFilter.getPostalCode().replaceAll(WILDCARD, "%") + "%"));
                }
                if (!StringUtils.isEmpty(deviceFilter.getStreet())) {
                    specifications = specifications.and(this.deviceSpecifications
                            .hasStreet(deviceFilter.getStreet().replaceAll(WILDCARD, "%") + "%"));
                }
                if (!StringUtils.isEmpty(deviceFilter.getNumber())) {
                    specifications = specifications.and(this.deviceSpecifications
                            .hasNumber(deviceFilter.getNumber().replaceAll(WILDCARD, "%") + "%"));
                }
                if (!StringUtils.isEmpty(deviceFilter.getMunicipality())) {
                    specifications = specifications.and(this.deviceSpecifications
                            .hasMunicipality(deviceFilter.getMunicipality().replaceAll(WILDCARD, "%") + "%"));
                }
                if (deviceFilter.getDeviceExternalManaged() != null
                        && !DeviceExternalManagedFilterType.BOTH.equals(deviceFilter.getDeviceExternalManaged())) {
                    specifications = specifications.and(this.deviceSpecifications
                            .isManagedExternally(deviceFilter.getDeviceExternalManaged().getValue()));
                }
                if (deviceFilter.getDeviceActivated() != null
                        && !DeviceActivatedFilterType.BOTH.equals(deviceFilter.getDeviceActivated())) {
                    specifications = specifications
                            .and(this.deviceSpecifications.isActived(deviceFilter.getDeviceActivated().getValue()));
                }
                if (deviceFilter.getDeviceInMaintenance() != null
                        && !DeviceInMaintenanceFilterType.BOTH.equals(deviceFilter.getDeviceInMaintenance())) {
                    specifications = specifications.and(this.deviceSpecifications
                            .isInMaintenance(deviceFilter.getDeviceInMaintenance().getValue()));
                }
                if (deviceFilter.isHasTechnicalInstallation()) {
                    specifications = specifications.and(this.deviceSpecifications.hasTechnicalInstallationDate());
                }
                if (!StringUtils.isEmpty(deviceFilter.getOwner())) {
                    specifications = specifications.and(this.deviceSpecifications
                            .forOwner(deviceFilter.getOwner().replaceAll(WILDCARD, "%") + "%"));
                }
                if (!StringUtils.isEmpty(deviceFilter.getDeviceType())) {
                    specifications = specifications.and(this.deviceSpecifications
                            .forDeviceType(deviceFilter.getDeviceType().replaceAll(WILDCARD, "%") + "%"));
                }
                if (!StringUtils.isEmpty(deviceFilter.getModel())) {
                    specifications = specifications.and(this.deviceSpecifications
                            .forDeviceModel(deviceFilter.getModel().replaceAll(WILDCARD, "%") + "%"));
                }
                if (!StringUtils.isEmpty(deviceFilter.getManufacturer())) {
                    final Manufacturer manufacturer = this.firmwareManagementService
                            .findManufacturer(deviceFilter.getManufacturer());
                    specifications = specifications.and(this.deviceSpecifications.forManufacturer(manufacturer));
                }
                if (!StringUtils.isEmpty(deviceFilter.getFirmwareModuleVersion())) {
                    specifications = specifications.and(
                            this.deviceSpecifications.forFirmwareModuleVersion(deviceFilter.getFirmwareModuleType(),
                                    deviceFilter.getFirmwareModuleVersion().replaceAll(WILDCARD, "%") + "%"));
                }
                if (deviceFilter.getDeviceIdentificationsToUse() != null
                        && !deviceFilter.getDeviceIdentificationsToUse().isEmpty()) {
                    specifications = specifications.and(this.deviceSpecifications
                            .existsInDeviceIdentificationList(deviceFilter.getDeviceIdentificationsToUse()));

                }
                if (deviceFilter.getDeviceIdentificationsToExclude() != null
                        && !deviceFilter.getDeviceIdentificationsToExclude().isEmpty()) {
                    specifications = specifications.and(this.deviceSpecifications
                            .excludeDeviceIdentificationList(deviceFilter.getDeviceIdentificationsToExclude()));

                }
                devices = this.deviceRepository.findAll(specifications, request);
            } else {
                if (organisation != null) {
                    final Specifications<Device> specifications = where(
                            this.deviceSpecifications.forOrganisation(organisation));
                    devices = this.deviceRepository.findAll(specifications, request);
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

    // === SET EVENT NOTIFICATIONS ===
    @Transactional(value = "transactionManager")
    public String enqueueSetEventNotificationsRequest(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final List<EventNotificationType> eventNotifications)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_EVENT_NOTIFICATIONS);
        this.domainHelperService.isInMaintenance(device);

        LOGGER.debug("enqueueSetEventNotificationsRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final EventNotificationMessageDataContainer eventNotificationMessageDataContainer = new EventNotificationMessageDataContainer(
                eventNotifications);

        final CommonRequestMessage message = new CommonRequestMessage(CommonRequestMessageType.SET_EVENT_NOTIFICATIONS,
                correlationUid, organisationIdentification, deviceIdentification, eventNotificationMessageDataContainer,
                null);

        this.commonRequestMessageSender.send(message);

        return correlationUid;
    }

    @Transactional(value = "transactionManager")
    public ResponseMessage dequeueSetEventNotificationsResponse(final String correlationUid) throws OsgpException {

        return this.commonResponseMessageFinder.findMessage(correlationUid);
    }

    @Transactional(value = "transactionManager")
    // === RETRIEVE SCHEDULED TASKS LIST FOR SPECIFIC DEVICE ===
    public List<ScheduledTask> findScheduledTasks(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.FIND_SCHEDULED_TASKS);

        return this.scheduledTaskRepository.findByDeviceIdentification(deviceIdentification);
    }

    @Transactional(value = "transactionManager")
    // === RETRIEVE SCHEDULED TASKS LIST FOR ALL DEVICES ===
    public List<ScheduledTask> findScheduledTasks(@Identification final String organisationIdentification)
            throws FunctionalException {
        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, PlatformFunction.FIND_SCHEDULED_TASKS);
        return this.scheduledTaskRepository.findByOrganisationIdentification(organisationIdentification);
    }

    @Transactional(value = "writableTransactionManager")
    public void updateDevice(@Identification final String organisationIdentification, @Valid final Ssld updateDevice)
            throws FunctionalException {

        final Device existingDevice = this.writableDeviceRepository
                .findByDeviceIdentification(updateDevice.getDeviceIdentification());
        if (existingDevice == null) {
            // device does not exist
            LOGGER.info("Device does not exist, nothing to update.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, ComponentType.WS_CORE,
                    new UnknownEntityException(Device.class, updateDevice.getDeviceIdentification()));
        }

        final List<DeviceAuthorization> owners = this.writableAuthorizationRepository
                .findByDeviceAndFunctionGroup(existingDevice, DeviceFunctionGroup.OWNER);

        // Check organisation against owner of device
        boolean isOwner = false;
        for (final DeviceAuthorization owner : owners) {
            if (owner.getOrganisation().getOrganisationIdentification().equalsIgnoreCase(organisationIdentification)) {
                isOwner = true;
            }
        }

        if (!isOwner) {
            LOGGER.info("Device has no owner yet, or organisation is not the owner.");
            throw new FunctionalException(FunctionalExceptionType.UNAUTHORIZED, ComponentType.WS_CORE,
                    new NotAuthorizedException(organisationIdentification));
        }

        // Update the device
        existingDevice.updateMetaData(updateDevice.getAlias(), updateDevice.getContainerCity(),
                updateDevice.getContainerPostalCode(), updateDevice.getContainerStreet(),
                updateDevice.getContainerNumber(), updateDevice.getContainerMunicipality(),
                updateDevice.getGpsLatitude(), updateDevice.getGpsLongitude());

        existingDevice.setActivated(updateDevice.isActivated());
        existingDevice.setDeviceLifecycleStatus(updateDevice.getDeviceLifecycleStatus());

        if (updateDevice.getTechnicalInstallationDate() != null) {
            existingDevice.setTechnicalInstallationDate(updateDevice.getTechnicalInstallationDate());
        }

        final Ssld ssld = this.writableSsldRepository.findOne(existingDevice.getId());
        ssld.updateOutputSettings(updateDevice.receiveOutputSettings());
        ssld.setEans(updateDevice.getEans());

        for (final Ean ean : updateDevice.getEans()) {
            ean.setDevice(ssld);
        }

        this.writableSsldRepository.save(ssld);
    }

    @Transactional(value = "writableTransactionManager")
    public void setDeviceAlias(@Identification final String organisationIdentification,
            final String deviceIdentification, final String deviceAlias,
            final List<DeviceOutputSetting> newDeviceOutputSettings) throws FunctionalException {

        final Ssld existingSsld = this.writableSsldRepository.findByDeviceIdentification(deviceIdentification);

        if (existingSsld == null) {
            // device does not exist
            LOGGER.info("Device does not exist, cannot set Alias.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, ComponentType.WS_CORE,
                    new UnknownEntityException(Device.class, deviceIdentification));
        }

        // Check to see if the organization is authorized for SET_DEVICE_ALIASES
        final Organisation organisation = this.organisationRepository
                .findByOrganisationIdentification(organisationIdentification);
        this.domainHelperService.isAllowed(organisation, existingSsld, DeviceFunction.SET_DEVICE_ALIASES);

        if (deviceAlias != null) {
            existingSsld.setAlias(deviceAlias);
            this.writableDeviceRepository.save(existingSsld);
        }

        if (newDeviceOutputSettings != null && !newDeviceOutputSettings.isEmpty()) {
            this.updateRelayAliases(newDeviceOutputSettings, existingSsld);
        }

    }

    private void updateRelayAliases(final List<DeviceOutputSetting> newDeviceOutputSettings, final Ssld ssld)
            throws FunctionalException {

        final List<com.alliander.osgp.domain.core.entities.DeviceOutputSetting> currentOutputSettings = ssld
                .getOutputSettings();

        if (currentOutputSettings == null || currentOutputSettings.isEmpty()) {
            LOGGER.info("Trying to set relay alias(es) for a device without output settings");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE_OUTPUT_SETTINGS,
                    ComponentType.WS_CORE);
        }

        for (final DeviceOutputSetting newSetting : newDeviceOutputSettings) {
            boolean outputSettingFound = false;
            for (final com.alliander.osgp.domain.core.entities.DeviceOutputSetting oldSetting : currentOutputSettings) {
                if (oldSetting.getExternalId() == newSetting.getExternalId()) {
                    oldSetting.setAlias(newSetting.getAlias());
                    outputSettingFound = true;
                }
            }
            if (!outputSettingFound) {
                LOGGER.info("Trying to set alias {} for internal relay {}, which has no output settings",
                        newSetting.getAlias(), newSetting.getInternalId());
                throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE_OUTPUT_SETTINGS,
                        ComponentType.WS_CORE);
            }
        }

        this.writableSsldRepository.save(ssld);
    }

    @Transactional(value = "writableTransactionManager")
    public void setMaintenanceStatus(@Identification final String organisationIdentification,
            final String deviceIdentification, final boolean status) throws FunctionalException {

        final Device existingDevice = this.writableDeviceRepository.findByDeviceIdentification(deviceIdentification);

        if (existingDevice == null) {
            // device does not exist
            LOGGER.info("Device does not exist, cannot set maintenance status.");
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, ComponentType.WS_CORE,
                    new UnknownEntityException(Device.class, deviceIdentification));
        } else {

            // Check to see if the organisation is CONFIGURATION or OWNER
            // authorized
            boolean isAuthorized = false;
            for (final DeviceAuthorization authorizations : existingDevice.getAuthorizations()) {
                if (organisationIdentification.equals(authorizations.getOrganisation().getOrganisationIdentification())
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
                throw new FunctionalException(FunctionalExceptionType.UNAUTHORIZED, ComponentType.WS_CORE,
                        new NotAuthorizedException(organisationIdentification));
            }
        }
    }

    public String enqueueUpdateDeviceSslCertificationRequest(final String organisationIdentification,
            final String deviceIdentification, final Certification certification) throws FunctionalException {
        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.UPDATE_DEVICE_SSL_CERTIFICATION);
        this.domainHelperService.isInMaintenance(device);

        LOGGER.debug("enqueueUpdateDeviceSslCertificationRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final CommonRequestMessage message = new CommonRequestMessage(
                CommonRequestMessageType.UPDATE_DEVICE_SSL_CERTIFICATION, correlationUid, organisationIdentification,
                deviceIdentification, certification, null);

        this.commonRequestMessageSender.send(message);

        return correlationUid;
    }

    public ResponseMessage dequeueUpdateDeviceSslCertificationResponse(final String correlationUid)
            throws OsgpException {
        return this.commonResponseMessageFinder.findMessage(correlationUid);
    }

    public String enqueueSetDeviceVerificationKeyRequest(final String organisationIdentification,
            final String deviceIdentification, final String verificationKey) throws FunctionalException {
        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_DEVICE_VERIFICATION_KEY);
        this.domainHelperService.isInMaintenance(device);

        LOGGER.debug("enqueueSetDeviceVerificationKeyRequest called with organisation {} and device {}",
                organisationIdentification, deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final CommonRequestMessage message = new CommonRequestMessage(
                CommonRequestMessageType.SET_DEVICE_VERIFICATION_KEY, correlationUid, organisationIdentification,
                deviceIdentification, verificationKey, null);

        this.commonRequestMessageSender.send(message);

        return correlationUid;
    }

    public ResponseMessage dequeueSetDeviceVerificationKeyResponse(final String correlationUid) throws OsgpException {
        return this.commonResponseMessageFinder.findMessage(correlationUid);
    }

    public String enqueueSetDeviceLifecycleStatusRequest(final String organisationIdentification,
            final String deviceIdentification, final DeviceLifecycleStatus deviceLifecycleStatus)
            throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.deviceDomainService.searchDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SET_DEVICE_LIFECYCLE_STATUS);

        LOGGER.debug(
                "enqueueSetDeviceLifecycleStatusRequest called with organisation {}, deviceLifecycleStatus {} and deviceIdentifcation {}",
                organisationIdentification, deviceLifecycleStatus.name(), deviceIdentification);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final com.alliander.osgp.domain.core.valueobjects.DeviceLifecycleStatus newDeviceLifecycleStatus = com.alliander.osgp.domain.core.valueobjects.DeviceLifecycleStatus
                .valueOf(deviceLifecycleStatus.name());

        final CommonRequestMessage message = new CommonRequestMessage(
                CommonRequestMessageType.SET_DEVICE_LIFECYCLE_STATUS, correlationUid, organisationIdentification,
                deviceIdentification, newDeviceLifecycleStatus, null);

        this.commonRequestMessageSender.send(message);

        return correlationUid;
    }

    public ResponseMessage dequeueSetDeviceLifecycleStatusResponse(final String correlationUid) throws OsgpException {
        return this.commonResponseMessageFinder.findMessage(correlationUid);
    }
}
