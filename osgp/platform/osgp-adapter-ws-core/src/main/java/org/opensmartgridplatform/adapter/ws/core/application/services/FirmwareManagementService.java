// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.core.application.services;

import static org.springframework.data.jpa.domain.Specification.where;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.ws.core.infra.jms.CommonRequestMessage;
import org.opensmartgridplatform.adapter.ws.core.infra.jms.CommonRequestMessageSender;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceFirmwareFileRepository;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceModelRepository;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceRepository;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableFirmwareFileRepository;
import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableManufacturerRepository;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceFirmwareFile;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.opensmartgridplatform.domain.core.entities.FirmwareModule;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.exceptions.ExistingEntityException;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.FirmwareModuleRepository;
import org.opensmartgridplatform.domain.core.specifications.FirmwareFileSpecifications;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleData;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareUpdateMessageDataContainer;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformFunction;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.validation.Identification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service(value = "wsCoreFirmwareManagementService")
@Transactional(value = "transactionManager")
@Validated
public class FirmwareManagementService {
  private static final Logger LOGGER = LoggerFactory.getLogger(FirmwareManagementService.class);

  private static final String SPACE_REPLACER = "_";

  @Autowired private DomainHelperService domainHelperService;

  @Autowired private CorrelationIdProviderService correlationIdProviderService;

  @Autowired private CommonRequestMessageSender commonRequestMessageSender;

  @Autowired private WritableManufacturerRepository manufacturerRepository;

  @Autowired private WritableDeviceModelRepository deviceModelRepository;

  @Autowired private WritableFirmwareFileRepository firmwareFileRepository;

  @Autowired private FirmwareModuleRepository firmwareModuleRepository;

  @Autowired private WritableDeviceRepository deviceRepository;

  @Autowired private WritableDeviceFirmwareFileRepository deviceFirmwareFileRepository;

  @Resource
  @Qualifier("wsCoreFirmwareManagementFirmwareDirectory")
  private String firmwareDirectory;

  @Autowired private boolean firmwareFileStorage;

  public String enqueueUpdateFirmwareRequest(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      final FirmwareUpdateMessageDataContainer firmwareUpdateMessageDataContainer,
      final DateTime scheduledTime,
      final int messagePriority)
      throws FunctionalException {
    LOGGER.debug("Queue update firmware request");

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.isAllowed(organisation, device, DeviceFunction.UPDATE_FIRMWARE);
    this.domainHelperService.isInMaintenance(device);

    LOGGER.debug(
        "enqueueUpdateFirmwareRequest called with organisation {} and device {}",
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
            .withMessageType(MessageType.UPDATE_FIRMWARE.name())
            .withMessagePriority(messagePriority)
            .withScheduleTime(scheduledTime == null ? null : scheduledTime.getMillis())
            .build();

    final CommonRequestMessage message =
        new CommonRequestMessage.Builder()
            .messageMetadata(messageMetadata)
            .request(firmwareUpdateMessageDataContainer)
            .build();

    this.commonRequestMessageSender.send(message);

    return correlationUid;
  }

  public String enqueueGetFirmwareRequest(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      final int messagePriority)
      throws FunctionalException {
    LOGGER.debug("Queue get firmware request");

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_FIRMWARE_VERSION);
    this.domainHelperService.isInMaintenance(device);

    LOGGER.debug(
        "enqueueGetFirmwareRequest called with organisation {} and device {}",
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
            .withMessageType(MessageType.GET_FIRMWARE_VERSION.name())
            .withMessagePriority(messagePriority)
            .build();

    final CommonRequestMessage message =
        new CommonRequestMessage.Builder().messageMetadata(messageMetadata).build();

    this.commonRequestMessageSender.send(message);

    return correlationUid;
  }

  /** Returns a list of all Manufacturers in the Platform */
  @Transactional(value = "writableTransactionManager")
  public List<Manufacturer> findAllManufacturers(final String organisationIdentification)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_MANUFACTURERS);

    return this.manufacturerRepository.findAll();
  }

  /** Returns a manufacturers in the Platform */
  @Transactional(value = "writableTransactionManager")
  public Manufacturer findManufacturer(final String manufacturerName) {
    return this.manufacturerRepository.findByName(manufacturerName);
  }

  /**
   * Adds new Manufacturer to the platform. Throws exception if {@link Manufacturer} already exists
   */
  @Transactional(value = "writableTransactionManager")
  public void addManufacturer(
      @Identification final String organisationIdentification,
      @Valid final Manufacturer manufacturer)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.CREATE_MANUFACTURER);

    final Manufacturer databaseManufacturer =
        this.manufacturerRepository.findByCode(manufacturer.getCode());

    if (databaseManufacturer != null) {
      LOGGER.info("Manufacturer already exists.");
      throw new FunctionalException(
          FunctionalExceptionType.EXISTING_MANUFACTURER,
          ComponentType.WS_CORE,
          new ExistingEntityException(Manufacturer.class, manufacturer.getCode()));
    } else {
      this.manufacturerRepository.save(manufacturer);
    }
  }

  /**
   * Updates a Manufacturer to the platform. Throws exception if {@link Manufacturer} doesn't exist.
   */
  @Transactional(value = "writableTransactionManager")
  public void changeManufacturer(
      @Identification final String organisationIdentification,
      @Valid final Manufacturer manufacturer)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.CHANGE_MANUFACTURER);

    final Manufacturer databaseManufacturer =
        this.manufacturerRepository.findByCode(manufacturer.getCode());

    if (databaseManufacturer == null) {
      LOGGER.info("Manufacturer not found.");
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_MANUFACTURER,
          ComponentType.WS_CORE,
          new ExistingEntityException(Manufacturer.class, manufacturer.getCode()));
    } else {
      databaseManufacturer.setCode(manufacturer.getCode());
      databaseManufacturer.setName(manufacturer.getName());
      databaseManufacturer.setUsePrefix(manufacturer.isUsePrefix());

      this.manufacturerRepository.save(databaseManufacturer);
    }
  }

  /**
   * Removes a Manufacturer from the platform. Throws exception if {@link Manufacturer} doesn't
   * exist
   */
  @Transactional(value = "writableTransactionManager")
  public void removeManufacturer(
      @Identification final String organisationIdentification, @Valid final String manufacturerCode)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.REMOVE_MANUFACTURER);

    final Manufacturer databaseManufacturer =
        this.manufacturerRepository.findByCode(manufacturerCode);
    final List<DeviceModel> deviceModels =
        this.deviceModelRepository.findByManufacturer(databaseManufacturer);

    if (!deviceModels.isEmpty()) {
      LOGGER.info("Manufacturer is linked to a Model.");
      throw new FunctionalException(
          FunctionalExceptionType.EXISTING_DEVICEMODEL_MANUFACTURER,
          ComponentType.WS_CORE,
          new ExistingEntityException(DeviceModel.class, deviceModels.get(0).getModelCode()));
    }

    if (databaseManufacturer == null) {
      LOGGER.info("Manufacturer not found.");
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_MANUFACTURER,
          ComponentType.WS_CORE,
          new ExistingEntityException(Manufacturer.class, manufacturerCode));
    } else {
      this.manufacturerRepository.delete(databaseManufacturer);
    }
  }

  /** Returns a list of all DeviceModels in the Platform */
  @Transactional(value = "writableTransactionManager")
  public List<DeviceModel> findAllDeviceModels(final String organisationIdentification)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_DEVICE_MODELS);

    return this.deviceModelRepository.findAll();
  }

  /** Returns a {@link DeviceModel}, if it exists */
  @Transactional(value = "writableTransactionManager")
  public DeviceModel findDeviceModel(
      final String organisationIdentification, final String deviceModelCode)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_DEVICE_MODELS);

    /*
     * Model code does not uniquely identify a device model, which is why
     * deviceModelRepository is changed to return a list of device models.
     *
     * A better solution would be to return a list of device models or to
     * determine the manufacturer and do a lookup by manufacturer and model
     * code, which should uniquely define the device model.
     */
    final List<DeviceModel> deviceModels =
        this.deviceModelRepository.findByModelCode(deviceModelCode);

    if (deviceModels.isEmpty()) {
      return null;
    }
    if (deviceModels.size() > 1) {
      throw new AssertionError(
          "Model code \"" + deviceModelCode + "\" does not uniquely identify a device model.");
    }

    return deviceModels.get(0);
  }

  /** Returns a devicemodel in the Platform */
  @Transactional(value = "writableTransactionManager")
  public DeviceModel findDeviceModel(final String modelCode) {
    /*
     * Model code does not uniquely identify a device model, which is why
     * deviceModelRepository is changed to return a list of device models.
     *
     * A better solution would be to return a list of device models or to
     * determine the manufacturer and do a lookup by manufacturer and model
     * code, which should uniquely define the device model.
     */
    final List<DeviceModel> deviceModels = this.deviceModelRepository.findByModelCode(modelCode);

    if (deviceModels.isEmpty()) {
      return null;
    }
    if (deviceModels.size() > 1) {
      throw new AssertionError(
          "Model code \"" + modelCode + "\" does not uniquely identify a device model.");
    }

    return deviceModels.get(0);
  }

  /**
   * Adds new deviceModel to the platform. Throws exception if {@link DeviceModel} already exists
   */
  @Transactional(value = "writableTransactionManager")
  public void addDeviceModel(
      @Identification final String organisationIdentification,
      final String manufacturerCode,
      final String modelCode,
      final String description)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.CREATE_DEVICE_MODEL);

    final Manufacturer manufacturer = this.findManufacturerByCode(manufacturerCode);

    final DeviceModel savedDeviceModel =
        this.deviceModelRepository.findByManufacturerAndModelCode(manufacturer, modelCode);

    if (savedDeviceModel != null) {
      LOGGER.info("DeviceModel already exists.");
      throw new FunctionalException(
          FunctionalExceptionType.EXISTING_DEVICEMODEL,
          ComponentType.WS_CORE,
          new ExistingEntityException(DeviceModel.class, manufacturerCode));
    } else {
      final DeviceModel deviceModel =
          new DeviceModel(manufacturer, modelCode, description, this.firmwareFileStorage);
      this.deviceModelRepository.save(deviceModel);
    }
  }

  /**
   * Removes a DeviceModel from the platform. Throws exception if {@link DeviceModel} doesn't exist
   */
  @Transactional(value = "writableTransactionManager")
  public void removeDeviceModel(
      @Identification final String organisationIdentification,
      @Valid final String manufacturer,
      final String modelCode)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.REMOVE_DEVICE_MODEL);

    final Manufacturer databaseManufacturer = this.manufacturerRepository.findByCode(manufacturer);
    final DeviceModel removedDeviceModel =
        this.deviceModelRepository.findByManufacturerAndModelCode(databaseManufacturer, modelCode);

    if (removedDeviceModel == null) {
      LOGGER.info("DeviceModel not found.");
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_DEVICEMODEL,
          ComponentType.WS_CORE,
          new ExistingEntityException(Manufacturer.class, modelCode));
    } else {
      final List<Device> devices = this.deviceRepository.findByDeviceModel(removedDeviceModel);
      if (!devices.isEmpty()) {
        LOGGER.info("DeviceModel is linked to a device.");
        throw new FunctionalException(
            FunctionalExceptionType.EXISTING_DEVICE_DEVICEMODEL,
            ComponentType.WS_CORE,
            new ExistingEntityException(Device.class, devices.get(0).getDeviceIdentification()));
      }
      final List<FirmwareFile> firmwareFiles =
          this.firmwareFileRepository.findByDeviceModel(removedDeviceModel);
      if (!firmwareFiles.isEmpty()) {
        LOGGER.info("DeviceModel is linked to a firmware file.");
        throw new FunctionalException(
            FunctionalExceptionType.EXISTING_DEVICEMODEL_FIRMWARE,
            ComponentType.WS_CORE,
            new ExistingEntityException(FirmwareFile.class, firmwareFiles.get(0).getFilename()));
      }
      this.deviceModelRepository.delete(removedDeviceModel);
    }
  }

  /**
   * Updates a DeviceModel to the platform. Throws exception if {@link DeviceModel} doesn't exist.
   */
  @Transactional(value = "writableTransactionManager")
  public void changeDeviceModel(
      @Identification final String organisationIdentification,
      final String manufacturer,
      final String modelCode,
      final String description)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.CHANGE_DEVICE_MODEL);

    final Manufacturer databaseManufacturer = this.manufacturerRepository.findByCode(manufacturer);
    final DeviceModel changedDeviceModel =
        this.deviceModelRepository.findByManufacturerAndModelCode(databaseManufacturer, modelCode);

    if (changedDeviceModel == null) {
      LOGGER.info("DeviceModel not found.");
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_DEVICEMODEL,
          ComponentType.WS_CORE,
          new ExistingEntityException(Manufacturer.class, modelCode));
    } else {

      changedDeviceModel.setDescription(description);
      this.deviceModelRepository.save(changedDeviceModel);
    }
  }

  /** Returns a list of all {@link FirmwareFile} in the Platform */
  public List<FirmwareFile> findAllFirmwareFiles(
      final String organisationIdentification,
      final String manufacturer,
      final String modelCode,
      final Boolean active)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_FIRMWARE);

    final List<FirmwareFile> firmwareFiles = new ArrayList<>();
    if (StringUtils.isNotBlank(manufacturer)) {
      firmwareFiles.addAll(this.getFirmwareFiles(manufacturer, modelCode, active));
    } else {
      firmwareFiles.addAll(this.getFirmwareFiles(modelCode, active));
    }

    // performance issue, clean list with firmware files for front-end admin
    // app.
    for (final FirmwareFile firmwareFile : firmwareFiles) {
      firmwareFile.setFile(null);
    }

    return firmwareFiles;
  }

  private List<FirmwareFile> getFirmwareFiles(
      final String manufacturerCode, final String modelCode, final Boolean active) {

    if (StringUtils.isBlank(manufacturerCode) || StringUtils.isBlank(modelCode)) {
      return new ArrayList<>();
    }

    final Manufacturer manufacturer = this.manufacturerRepository.findByCode(manufacturerCode);
    final DeviceModel deviceModel =
        this.deviceModelRepository.findByManufacturerAndModelCode(manufacturer, modelCode);

    if (deviceModel == null) {
      return new ArrayList<>();
    }

    Specification<FirmwareFile> specification =
        where(FirmwareFileSpecifications.forDeviceModel(deviceModel));
    if (specification == null) {
      return new ArrayList<>();
    }
    specification =
        specification.and(FirmwareFileSpecifications.forActiveFirmwareFilesOnly(active));
    if (specification == null) {
      return new ArrayList<>();
    }

    return this.firmwareFileRepository.findAll(specification);
  }

  private List<FirmwareFile> getFirmwareFiles(final String modelCode, final Boolean active) {

    if (StringUtils.isBlank(modelCode)) {
      return new ArrayList<>();
    }

    final List<DeviceModel> deviceModels = this.deviceModelRepository.findByModelCode(modelCode);
    if (deviceModels == null || deviceModels.isEmpty()) {
      return new ArrayList<>();
    }

    Specification<FirmwareFile> specification =
        where(FirmwareFileSpecifications.forDeviceModels(deviceModels));
    if (specification == null) {
      return new ArrayList<>();
    }

    specification =
        specification.and(FirmwareFileSpecifications.forActiveFirmwareFilesOnly(active));
    if (specification == null) {
      return new ArrayList<>();
    }

    return this.firmwareFileRepository.findAll(specification);
  }

  /** Returns the {@link FirmwareFile} of the given id, if it exists */
  public FirmwareFile findFirmwareFile(
      final String organisationIdentification, final int firmwareFileId)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_FIRMWARE);

    return this.firmwareFileRepository
        .findById((long) firmwareFileId)
        .orElseThrow(supplyFirmwareFileNotFoundException(firmwareFileId));
  }

  /**
   * Adds new {@link FirmwareFile} to the platform. Throws exception if {@link FirmwareFile} already
   * exists
   */
  @Transactional(value = "writableTransactionManager")
  public void addFirmware(
      @Identification final String organisationIdentification,
      final FirmwareFileRequest firmwareFileRequest,
      final byte[] file,
      final String manufacturer,
      final String modelCode,
      final FirmwareModuleData firmwareModuleData)
      throws OsgpException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.CREATE_FIRMWARE);

    final Manufacturer databaseManufacturer = this.findManufacturerByCode(manufacturer);

    final DeviceModel databaseDeviceModel =
        this.deviceModelRepository.findByManufacturerAndModelCode(databaseManufacturer, modelCode);

    if (databaseDeviceModel == null) {
      LOGGER.info("DeviceModel doesn't exist.");
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_DEVICEMODEL,
          ComponentType.WS_CORE,
          new UnknownEntityException(DeviceModel.class, modelCode));
    }

    final Map<FirmwareModule, String> firmwareVersionsByModule =
        firmwareModuleData.getVersionsByModule(this.firmwareModuleRepository, false);

    FirmwareFile savedFirmwareFile;

    // file == null, user selected an existing firmware file
    if (file == null) {
      final List<FirmwareFile> databaseFirmwareFiles =
          this.firmwareFileRepository.findByDeviceModelAndFilename(
              databaseDeviceModel, firmwareFileRequest.getFileName());

      if (databaseFirmwareFiles.isEmpty()) {
        LOGGER.error("Firmware file doesn't exist.");
        throw new FunctionalException(
            FunctionalExceptionType.UNKNOWN_FIRMWARE,
            ComponentType.WS_CORE,
            new UnknownEntityException(DeviceModel.class, firmwareFileRequest.getFileName()));
      }

      if (databaseDeviceModel.isFileStorage()) {
        // The file is already in the directory, so nothing else has to
        // happen
        savedFirmwareFile = this.firmwareFileFrom(firmwareFileRequest);
      } else {
        // Storing the file in the database
        savedFirmwareFile =
            this.createNewFirmwareFile(firmwareFileRequest, databaseFirmwareFiles.get(0).getFile());
      }
    } else if (databaseDeviceModel.isFileStorage()) {
      // Saving the file to the file system
      this.writeToFilesystem(file, firmwareFileRequest.getFileName(), databaseDeviceModel);
      savedFirmwareFile = this.firmwareFileFrom(firmwareFileRequest);
    } else {
      // Storing the file in the database
      savedFirmwareFile = this.createNewFirmwareFile(firmwareFileRequest, file);
    }

    if (firmwareFileRequest.isPushToNewDevices()) {
      final List<FirmwareFile> firmwareFiles =
          this.firmwareFileRepository.findByDeviceModel(databaseDeviceModel);
      this.setPushToNewDevicesToFalse(firmwareFiles);
    }
    /*
     * Save the firmware file before adding the device model and updating
     * the firmware module data. Trying to save a new firmware file with the
     * related entities that were persisted earlier causes Hibernate
     * exceptions referring to persistent entities in detached state.
     */
    savedFirmwareFile = this.firmwareFileRepository.save(savedFirmwareFile);
    savedFirmwareFile.addDeviceModel(databaseDeviceModel);
    savedFirmwareFile.updateFirmwareModuleData(firmwareVersionsByModule);
    this.firmwareFileRepository.save(savedFirmwareFile);
  }

  private Manufacturer findManufacturerByCode(final String manufacturer)
      throws FunctionalException {
    final Manufacturer databaseManufacturer = this.manufacturerRepository.findByCode(manufacturer);

    if (databaseManufacturer == null) {
      LOGGER.info("Manufacturer doesn't exist.");
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_MANUFACTURER,
          ComponentType.WS_CORE,
          new UnknownEntityException(Manufacturer.class, manufacturer));
    }
    return databaseManufacturer;
  }

  @Transactional(value = "writableTransactionManager")
  public void addOrChangeFirmware(
      @Identification final String organisationIdentification,
      final FirmwareFileRequest firmwareFileRequest,
      final byte[] file,
      final List<org.opensmartgridplatform.domain.core.valueobjects.DeviceModel> deviceModels,
      final FirmwareModuleData firmwareModuleData)
      throws OsgpException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.CREATE_FIRMWARE);

    // find for each DeviceModel from the WebServcies the corresponding entities
    // There should be at least one DeviceModel. If none found a FunctionalException should be
    // raised
    // Each DeviceModel can have it's own Manufacturer (at least a theory)
    // if a Manufacturer entity related to the DeviceModel can not be found a FunctionalException
    // should be raised
    // if one of the DeviceModel entities can not be found a FunctionalException should be raised
    final List<DeviceModel> databaseDeviceModels = new ArrayList<>();
    for (final org.opensmartgridplatform.domain.core.valueobjects.DeviceModel deviceModel :
        deviceModels) {

      final Manufacturer databaseManufacturer =
          this.findManufacturerByCode(deviceModel.getManufacturer());

      final DeviceModel databaseDeviceModel =
          this.deviceModelRepository.findByManufacturerAndModelCode(
              databaseManufacturer, deviceModel.getModelCode());

      if (databaseDeviceModel == null) {
        LOGGER.info("DeviceModel doesn't exist.");
        throw new FunctionalException(
            FunctionalExceptionType.UNKNOWN_DEVICEMODEL,
            ComponentType.WS_CORE,
            new UnknownEntityException(DeviceModel.class, deviceModel.getModelCode()));
      }
      databaseDeviceModels.add(databaseDeviceModel);
    }
    if (!deviceModels.isEmpty() && databaseDeviceModels.isEmpty()) {
      LOGGER.info("No DeviceModels found.");
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_DEVICEMODEL, ComponentType.WS_CORE);
    }

    final Map<FirmwareModule, String> firmwareVersionsByModule =
        firmwareModuleData.getVersionsByModule(this.firmwareModuleRepository, true);

    final FirmwareFile firmwareFile = this.insertOrUdateDatabase(firmwareFileRequest, file);

    firmwareFile.updateFirmwareDeviceModels(databaseDeviceModels);
    firmwareFile.updateFirmwareModuleData(firmwareVersionsByModule);

    this.firmwareFileRepository.save(firmwareFile);
  }

  private FirmwareFile firmwareFileFrom(final FirmwareFileRequest firmwareFileRequest) {
    return new FirmwareFile.Builder()
        .withFilename(firmwareFileRequest.getFileName())
        .withDescription(firmwareFileRequest.getDescription())
        .withPushToNewDevices(firmwareFileRequest.isPushToNewDevices())
        .withActive(firmwareFileRequest.isActive())
        .build();
  }

  private FirmwareFile insertOrUdateDatabase(
      final FirmwareFileRequest firmwareFileRequest, final byte[] file) {
    final String identification = firmwareFileRequest.getIdentification();
    final FirmwareFile existingFirmwareFile =
        this.firmwareFileRepository.findByIdentification(identification);
    final FirmwareFile savedFirmwareFile;
    if (existingFirmwareFile == null) {
      final FirmwareFile newFirmwareFile = this.createNewFirmwareFile(firmwareFileRequest, file);
      savedFirmwareFile = this.firmwareFileRepository.save(newFirmwareFile);
    } else {
      this.updateExistingFirmwareFile(existingFirmwareFile, firmwareFileRequest, file);
      savedFirmwareFile = this.firmwareFileRepository.save(existingFirmwareFile);
    }
    return savedFirmwareFile;
  }

  private FirmwareFile updateExistingFirmwareFile(
      final FirmwareFile existingFirmwareFile,
      final FirmwareFileRequest firmwareFileRequest,
      final byte[] file) {
    existingFirmwareFile.setDescription(firmwareFileRequest.getDescription());
    existingFirmwareFile.setImageIdentifier(firmwareFileRequest.getImageIdentifier());
    // A file can only be uploaded once
    // - directly at creation of the FirmwareFile record
    // - or as is processed here in a update action of the FirmwareFile record
    // Removing the File Content is not allowed. Null or empty value for file therefore will be
    // ignored.
    // Getting and directly setting of file(name) attributes of the existing FW file record seems
    // not necessary but asppearently a new record is created in the process
    if (existingFirmwareFile.getFile() == null || existingFirmwareFile.getFile().length == 0) {
      existingFirmwareFile.setFilename(firmwareFileRequest.getFileName());
      existingFirmwareFile.setFile(file);
    } else {
      existingFirmwareFile.setFilename(existingFirmwareFile.getFilename());
      existingFirmwareFile.setFile(existingFirmwareFile.getFile());
    }
    return existingFirmwareFile;
  }

  private FirmwareFile createNewFirmwareFile(
      final FirmwareFileRequest firmwareFileRequest, final byte[] file) {
    return new FirmwareFile.Builder()
        .withIdentification(firmwareFileRequest.getIdentification())
        .withFilename(firmwareFileRequest.getFileName())
        .withDescription(firmwareFileRequest.getDescription())
        .withPushToNewDevices(firmwareFileRequest.isPushToNewDevices())
        .withFile(file)
        .withImageIdentifier(firmwareFileRequest.getImageIdentifier())
        .build();
  }

  /** Saves a {@link DeviceFirmwareFile} instance. */
  @Transactional(value = "writableTransactionManager")
  public void saveDeviceFirmwareFile(final DeviceFirmwareFile deviceFirmwareFile) {
    this.deviceFirmwareFileRepository.save(deviceFirmwareFile);
  }

  /**
   * Updates a FirmwareFile to the platform. Throws exception if {@link FirmwareFile} doesn't exist.
   */
  @Transactional(value = "writableTransactionManager")
  public void changeFirmware(
      @Identification final String organisationIdentification,
      final int id,
      final FirmwareFileRequest firmwareFileRequest,
      final FirmwareModuleData firmwareModuleData)
      throws FunctionalException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.CHANGE_FIRMWARE);

    FirmwareFile changedFirmwareFile =
        this.firmwareFileRepository
            .findById((long) id)
            .orElseThrow(
                supplyFirmwareFileNotFoundException(id, firmwareFileRequest.getFileName()));

    changedFirmwareFile.setDescription(firmwareFileRequest.getDescription());

    changedFirmwareFile.setFilename(firmwareFileRequest.getFileName());
    changedFirmwareFile.updateFirmwareModuleData(
        firmwareModuleData.getVersionsByModule(this.firmwareModuleRepository, false));
    changedFirmwareFile.setPushToNewDevices(firmwareFileRequest.isPushToNewDevices());
    changedFirmwareFile.setActive(firmwareFileRequest.isActive());

    // Save the changed firmware entity
    changedFirmwareFile = this.firmwareFileRepository.save(changedFirmwareFile);

    this.firmwareFileRepository.save(changedFirmwareFile);
  }

  /**
   * Removes a {@link FirmwareFile} from the platform. Throws exception if {@link FirmwareFile}
   * doesn't exist
   */
  @Transactional(value = "writableTransactionManager")
  public void removeFirmware(
      @Identification final String organisationIdentification,
      @Valid final int firmwareIdentification)
      throws OsgpException {

    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.REMOVE_FIRMWARE);

    final FirmwareFile removedFirmwareFile =
        this.firmwareFileRepository
            .findById((long) firmwareIdentification)
            .orElseThrow(supplyFirmwareFileNotFoundException(firmwareIdentification));

    final List<DeviceFirmwareFile> deviceFirmwares =
        this.deviceFirmwareFileRepository.findByFirmwareFile(removedFirmwareFile);
    if (!deviceFirmwares.isEmpty()) {
      LOGGER.info("FirmwareFile is linked to device.");
      throw new FunctionalException(
          FunctionalExceptionType.EXISTING_FIRMWARE_DEVICEFIRMWARE,
          ComponentType.WS_CORE,
          new ExistingEntityException(
              DeviceFirmwareFile.class, deviceFirmwares.get(0).getFirmwareFile().getDescription()));
    }

    /*
     * A firmware file has been changed to be related to (possibly) multiple
     * device models to be usable across different value streams for all
     * kinds of devices.
     *
     * If this code gets used in a scenario where multiple device models are
     * actually related to the firmware file it may need to be updated to
     * deal with this.
     */
    final Set<DeviceModel> deviceModels = removedFirmwareFile.getDeviceModels();
    if (deviceModels.size() != 1) {
      LOGGER.warn(
          "Remove Firmware assumes a single DeviceModel, FirmwareFile (id={}) has {}: {}",
          removedFirmwareFile.getId(),
          deviceModels.size(),
          deviceModels);
    }
    final DeviceModel deviceModel = deviceModels.iterator().next();

    // Only remove the file if no other firmware is using it.
    if (deviceModel.isFileStorage()
        && this.firmwareFileRepository
                .findByDeviceModelAndFilename(deviceModel, removedFirmwareFile.getFilename())
                .size()
            == 1) {
      this.removePhysicalFirmwareFile(
          this.createFirmwarePath(deviceModel, removedFirmwareFile.getFilename()));
    }

    this.firmwareFileRepository.delete(removedFirmwareFile);
  }

  private static Supplier<FunctionalException> supplyFirmwareFileNotFoundException(
      final int firmwareId) {
    return supplyFirmwareFileNotFoundException(firmwareId, String.valueOf(firmwareId));
  }

  private static Supplier<FunctionalException> supplyFirmwareFileNotFoundException(
      final int firmwareId, final String firmwareIdentification) {
    LOGGER.info("FirmwareFile not found for id {}.", firmwareId);
    return () ->
        new FunctionalException(
            FunctionalExceptionType.UNKNOWN_FIRMWARE,
            ComponentType.WS_CORE,
            new UnknownEntityException(FirmwareFile.class, firmwareIdentification));
  }

  /** Returns a list of all {@link DeviceFirmwareFile}s in the Platform */
  public List<DeviceFirmwareFile> getDeviceFirmwareFiles(
      final String organisationIdentification, final String deviceIdentification)
      throws FunctionalException {
    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    this.domainHelperService.isAllowed(organisation, PlatformFunction.GET_FIRMWARE);

    final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);

    return this.deviceFirmwareFileRepository.findByDeviceOrderByInstallationDateAsc(device);
  }

  public String enqueueSwitchFirmwareRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final String version,
      final int messagePriority)
      throws FunctionalException {
    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);
    final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

    this.domainHelperService.isAllowed(organisation, device, DeviceFunction.SWITCH_FIRMWARE);
    this.domainHelperService.isInMaintenance(device);

    LOGGER.debug(
        "enqueueSwitchFirmwareRequest called with organisation {} and device {}",
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
            .withMessageType(MessageType.SWITCH_FIRMWARE.name())
            .withMessagePriority(messagePriority)
            .build();

    final CommonRequestMessage message =
        new CommonRequestMessage.Builder()
            .messageMetadata(messageMetadata)
            .request(version)
            .build();

    this.commonRequestMessageSender.send(message);

    return correlationUid;
  }

  // HELPER METHODS

  private void writeToFilesystem(
      final byte[] file, final String fileName, final DeviceModel deviceModel)
      throws TechnicalException {

    // Replacing spaces by SPACE_REPLACER
    final String newFileName = fileName.replace(" ", SPACE_REPLACER);

    final File path = this.createFirmwarePath(deviceModel, newFileName);

    // Creating the dir, if needed
    this.createModelDirectory(path.getParentFile(), deviceModel.getModelCode());

    try (final FileOutputStream fos = new FileOutputStream(path)) {
      fos.write(file);
    } catch (final IOException e) {
      throw new TechnicalException(
          ComponentType.WS_CORE,
          "Could not write firmware file to system".concat(e.getMessage()),
          e);
    }

    // Setting the file to readable to be downloadable
    if (!path.setReadable(true, false)) {
      LOGGER.warn("Unable to set the file {} to readable", path.getName());
    }
  }

  private void removePhysicalFirmwareFile(final File file) throws TechnicalException {

    try {
      // Delete file
      Files.deleteIfExists(file.toPath());

      // Delete directorty if it was the last file
      if (file.getParentFile().list().length == 0) {
        Files.deleteIfExists(file.toPath().getParent());
      }

    } catch (final IOException e) {
      throw new TechnicalException(
          ComponentType.WS_CORE,
          "Could not remove firmware file from directory: ".concat(e.getMessage()),
          e);
    }
  }

  /*
   * Creates a directory for the given modelCode and manufacturer, if it
   * doesn't exist yet.
   */
  private void createModelDirectory(final File file, final String modelCode)
      throws TechnicalException {
    if (!file.isDirectory()) {
      LOGGER.info("Creating directory for devicemodel {}", modelCode);
      if (!file.mkdirs()) {
        throw new TechnicalException(
            ComponentType.WS_CORE, "Could not create directory for devicemodel ".concat(modelCode));
      }
      // Setting the correct permissions so that the directory can be read
      // and displayed
      if (!file.setReadable(true, false)) {
        LOGGER.warn("Unable to set the file {} to readable", file.getName());
      }
      if (!file.setExecutable(true, false)) {
        LOGGER.warn("Unable to set the file {} to executable", file.getName());
      }
      if (!file.getParentFile().setReadable(true, false)) {
        LOGGER.warn("Unable to set the parent file {} to readable", file.getParentFile().getName());
      }
      if (!file.getParentFile().setExecutable(true, false)) {
        LOGGER.warn(
            "Unable to set the parent file {} to executable", file.getParentFile().getName());
      }
    }
  }

  private File createFirmwarePath(final DeviceModel deviceModel, final String fileName) {
    return new File(
        this.firmwareDirectory
            .concat(File.separator)
            .concat(deviceModel.getManufacturer().getCode().replace(" ", SPACE_REPLACER))
            .concat(File.separator)
            .concat(deviceModel.getModelCode().replace(" ", SPACE_REPLACER))
            .concat(File.separator)
            .concat(fileName));
  }

  private void setPushToNewDevicesToFalse(final List<FirmwareFile> firmwareFiles) {
    for (final FirmwareFile firmwareFile : firmwareFiles) {
      if (firmwareFile.getPushToNewDevices()) {
        firmwareFile.setPushToNewDevices(false);
      }
    }
    this.firmwareFileRepository.saveAll(firmwareFiles);
  }
}
