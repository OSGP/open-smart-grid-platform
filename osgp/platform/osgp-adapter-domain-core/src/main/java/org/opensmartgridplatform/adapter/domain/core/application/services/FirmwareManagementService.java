//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.core.application.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceFirmwareFile;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.opensmartgridplatform.domain.core.entities.FirmwareModule;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.entities.SsldPendingFirmwareUpdate;
import org.opensmartgridplatform.domain.core.repositories.DeviceFirmwareFileRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceModelRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.FirmwareFileRepository;
import org.opensmartgridplatform.domain.core.repositories.ManufacturerRepository;
import org.opensmartgridplatform.domain.core.repositories.SsldPendingFirmwareUpdateRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleType;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareUpdateMessageDataContainer;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.shared.validation.Identification;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

@Service(value = "domainCoreFirmwareManagementService")
@Transactional(value = "transactionManager")
public class FirmwareManagementService extends AbstractService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FirmwareManagementService.class);

  private static final String INSTALLER = "Inserted to match the version reported by the device.";

  @Autowired private DeviceFirmwareFileRepository deviceFirmwareFileRepository;

  @Autowired private DeviceModelRepository deviceModelRepository;

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private FirmwareFileRepository firmwareFileRepository;

  @Autowired private ManufacturerRepository manufacturerRepository;

  @Autowired private SsldPendingFirmwareUpdateRepository ssldPendingFirmwareUpdateRepository;

  @Value("${ssld.pending.firmware.update.get.firmware.version.delay}")
  private Long getFirmwareVersionDelay;

  /** Constructor */
  public FirmwareManagementService() {
    // Parameterless constructor required for transactions...
  }

  // === UPDATE FIRMWARE ===

  public void updateFirmware(
      final CorrelationIds ids,
      final FirmwareUpdateMessageDataContainer firmwareUpdateMessageDataContainer,
      final Long scheduleTime,
      final String messageType,
      final int messagePriority)
      throws FunctionalException {

    LOGGER.debug(
        "Update firmware called with organisation [{}], device [{}], firmwareIdentification [{}].",
        ids.getOrganisationIdentification(),
        ids.getDeviceIdentification(),
        firmwareUpdateMessageDataContainer.getFirmwareUrl());

    this.findOrganisation(ids.getOrganisationIdentification());
    final Device device = this.findActiveDevice(ids.getDeviceIdentification());

    if (device instanceof Ssld) {
      this.createSsldPendingFirmwareUpdateRecord(
          ids, firmwareUpdateMessageDataContainer.getFirmwareUrl());
    }

    this.osgpCoreRequestMessageSender.sendWithScheduledTime(
        new RequestMessage(
            ids,
            this.domainCoreMapper.map(
                firmwareUpdateMessageDataContainer,
                org.opensmartgridplatform.dto.valueobjects.FirmwareUpdateMessageDataContainer
                    .class)),
        messageType,
        messagePriority,
        device.getIpAddress(),
        scheduleTime);
  }

  private void createSsldPendingFirmwareUpdateRecord(
      final CorrelationIds ids, final String firmwareUrl) {
    try {
      final String firmwareFilename = getFirmwareFilename(firmwareUrl);

      final List<FirmwareFile> firmwareFiles =
          this.firmwareFileRepository.findByFilename(firmwareFilename);
      Assert.isTrue(
          firmwareFiles.size() == 1, "Expected 1 firmware file for filename: " + firmwareFilename);
      final FirmwareFile firmwareFile = firmwareFiles.get(0);

      final Map<FirmwareModule, String> firmwareModuleVersions = firmwareFile.getModuleVersions();
      Assert.isTrue(
          firmwareModuleVersions.size() == 1,
          "Expected 1 firmware module for: " + firmwareModuleVersions);
      final Entry<FirmwareModule, String> firmwareModuleVersion =
          firmwareModuleVersions.entrySet().iterator().next();
      final FirmwareModuleType firmwareModuleType =
          FirmwareModuleType.valueOf(firmwareModuleVersion.getKey().getDescription().toUpperCase());
      final String firmwareVersion = firmwareModuleVersion.getValue();

      SsldPendingFirmwareUpdate ssldPendingFirmwareUpdate =
          new SsldPendingFirmwareUpdate(
              ids.getDeviceIdentification(),
              firmwareModuleType,
              firmwareVersion,
              ids.getOrganisationIdentification(),
              ids.getCorrelationUid());
      ssldPendingFirmwareUpdate =
          this.ssldPendingFirmwareUpdateRepository.save(ssldPendingFirmwareUpdate);

      LOGGER.info(
          "Saved pending fimware update record for SSLD: {}, {}",
          ids.getDeviceIdentification(),
          ssldPendingFirmwareUpdate);
    } catch (final Exception e) {
      LOGGER.error(
          "Caught exception when creating pending firmware update record for SSLD: {}",
          ids.getDeviceIdentification(),
          e);
    }
  }

  private static String getFirmwareFilename(final String firmwareUrl) {
    final String[] split = firmwareUrl.split("/");
    Assert.isTrue(split.length >= 1, "Splitting URL on / failed!");
    return split[split.length - 1];
  }

  public void handleSsldPendingFirmwareUpdate(final String deviceIdentification) {

    final List<SsldPendingFirmwareUpdate> ssldPendingFirmwareUpdates =
        this.ssldPendingFirmwareUpdateRepository.findByDeviceIdentification(deviceIdentification);

    if (CollectionUtils.isEmpty(ssldPendingFirmwareUpdates)) {
      return;
    }

    /*
     * A pending firmware update record was stored for this device earlier.
     * This means this method is probably called following a firmware
     * update. Retrieve the firmware version from the device to have the
     * current version that is installed available.
     *
     * If multiple pending update records exist, it is not really clear what
     * to do. The following approach assumes the most recently created one
     * is relevant, and other pending records are out-dated.
     */
    final SsldPendingFirmwareUpdate ssldPendingFirmwareUpdate;
    if (ssldPendingFirmwareUpdates.size() == 1) {
      ssldPendingFirmwareUpdate = ssldPendingFirmwareUpdates.get(0);
    } else {
      LOGGER.warn(
          "Found multiple pending firmware update records for SSLD: {}",
          ssldPendingFirmwareUpdates);
      ssldPendingFirmwareUpdate =
          this.getMostRecentSsldPendingFirmwareUpdate(ssldPendingFirmwareUpdates)
              .orElseThrow(
                  () ->
                      new AssertionError(
                          "No most recent pending firmware update from a non-empty list"));
      this.deleteOutdatedSsldPendingFirmwareUpdates(
          ssldPendingFirmwareUpdates, ssldPendingFirmwareUpdate);
    }

    final String organisationIdentification =
        ssldPendingFirmwareUpdate.getOrganisationIdentification();
    final String correlationUid = ssldPendingFirmwareUpdate.getCorrelationUid();

    LOGGER.info(
        "Handling SSLD pending firmware update for device identification: {}, organisation identification: {} and correlation UID: {}.",
        deviceIdentification,
        organisationIdentification,
        correlationUid);

    try {
      final int messagePriority = MessagePriorityEnum.DEFAULT.getPriority();
      this.getFirmwareVersion(
          organisationIdentification,
          deviceIdentification,
          correlationUid,
          DeviceFunction.GET_FIRMWARE_VERSION.name(),
          messagePriority,
          this.getFirmwareVersionDelay);
    } catch (final FunctionalException e) {
      LOGGER.error("Caught exception when calling get firmware version", e);
    }
  }

  private Optional<SsldPendingFirmwareUpdate> getMostRecentSsldPendingFirmwareUpdate(
      final List<SsldPendingFirmwareUpdate> ssldPendingFirmwareUpdates) {

    return ssldPendingFirmwareUpdates.stream()
        .max(
            Comparator.comparing(SsldPendingFirmwareUpdate::getCreationTime)
                .thenComparing(SsldPendingFirmwareUpdate::getId));
  }

  private void deleteOutdatedSsldPendingFirmwareUpdates(
      final List<SsldPendingFirmwareUpdate> updatesToDelete,
      final SsldPendingFirmwareUpdate notToBeDeleted) {

    updatesToDelete.stream()
        .filter(pendingUpdate -> !Objects.equals(notToBeDeleted.getId(), pendingUpdate.getId()))
        .forEach(
            pendingUpdate -> {
              LOGGER.warn("Deleting pending firmware update assumed outdated: {}", pendingUpdate);
              this.ssldPendingFirmwareUpdateRepository.delete(pendingUpdate);
            });
  }

  // === GET FIRMWARE VERSION ===

  public void getFirmwareVersion(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      final String correlationUid,
      final String messageType,
      final int messagePriority)
      throws FunctionalException {

    this.getFirmwareVersion(
        organisationIdentification,
        deviceIdentification,
        correlationUid,
        messageType,
        messagePriority,
        null);
  }

  public void getFirmwareVersion(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      final String correlationUid,
      final String messageType,
      final int messagePriority,
      final Long delay)
      throws FunctionalException {

    LOGGER.debug(
        "Get firmware version called with organisation [{}], device [{}].",
        organisationIdentification,
        deviceIdentification);

    this.findOrganisation(organisationIdentification);
    final Device device = this.findActiveDevice(deviceIdentification);

    this.osgpCoreRequestMessageSender.sendWithDelay(
        new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, null),
        messageType,
        messagePriority,
        device.getIpAddress(),
        delay);
  }

  public void handleGetFirmwareVersionResponse(
      final List<FirmwareVersionDto> firmwareVersionsDto,
      final CorrelationIds ids,
      final String messageType,
      final int messagePriority,
      final ResponseMessageResultType deviceResult,
      final OsgpException exception) {

    LOGGER.info("handleResponse for MessageType: {}", messageType);

    ResponseMessageResultType result = ResponseMessageResultType.OK;
    OsgpException osgpException = exception;

    try {
      if (deviceResult == ResponseMessageResultType.NOT_OK || osgpException != null) {
        LOGGER.error("Device Response not ok.", osgpException);
        throw osgpException;
      }
    } catch (final Exception e) {
      LOGGER.error("Unexpected Exception", e);
      result = ResponseMessageResultType.NOT_OK;
      osgpException =
          new TechnicalException(
              ComponentType.UNKNOWN, "Exception occurred while getting device firmware version", e);
    }
    final List<FirmwareVersion> firmwareVersions =
        this.domainCoreMapper.mapAsList(firmwareVersionsDto, FirmwareVersion.class);

    this.checkFirmwareHistory(ids.getDeviceIdentification(), firmwareVersions);

    final boolean hasPendingFirmwareUpdate =
        this.checkSsldPendingFirmwareUpdate(ids, firmwareVersions);

    if (!hasPendingFirmwareUpdate) {
      final ResponseMessage responseMessage =
          ResponseMessage.newResponseMessageBuilder()
              .withIds(ids)
              .withResult(result)
              .withOsgpException(osgpException)
              .withDataObject((Serializable) firmwareVersions)
              .withMessagePriority(messagePriority)
              .withMessageType(MessageType.GET_FIRMWARE_VERSION.name())
              .build();
      this.webServiceResponseMessageSender.send(responseMessage);
    }
  }

  boolean checkSsldPendingFirmwareUpdate(
      final CorrelationIds ids, final List<FirmwareVersion> firmwareVersions) {

    final String deviceIdentification = ids.getDeviceIdentification();

    final SsldPendingFirmwareUpdate ssldPendingFirmwareUpdate =
        this.ssldPendingFirmwareUpdateRepository
            .findByDeviceIdentification(deviceIdentification)
            .stream()
            .filter(
                pendingUpdate -> pendingUpdate.getCorrelationUid().equals(ids.getCorrelationUid()))
            .findAny()
            .orElse(null);
    if (ssldPendingFirmwareUpdate == null) {
      return false;
    }

    LOGGER.info(
        "Found SSLD pending firmware update record for device identification: {}, {}.",
        deviceIdentification,
        ssldPendingFirmwareUpdate);

    final FirmwareModuleType expectedFirmwareModuleType =
        ssldPendingFirmwareUpdate.getFirmwareModuleType();
    final String expectedFirmwareVersion = ssldPendingFirmwareUpdate.getFirmwareVersion();
    final boolean foundExpectedFirmwareVersion =
        firmwareVersions.stream()
            .anyMatch(
                fv ->
                    expectedFirmwareModuleType.equals(fv.getFirmwareModuleType())
                        && expectedFirmwareVersion.equals(fv.getVersion()));

    if (foundExpectedFirmwareVersion) {
      LOGGER.info(
          "Expected firmware version from SSLD pending firmware update record matches firmware version as retrieved from device identification: {}, firmware version: {}, firmware module type: {}.",
          deviceIdentification,
          expectedFirmwareVersion,
          expectedFirmwareModuleType);
    } else {
      LOGGER.error(
          "Expected firmware version from SSLD pending firmware update record does )not match firmware version as retrieved from device identification: {}, expected firmware version: {}, expected firmware module type: {}, actual firmware version and module type list: {}",
          deviceIdentification,
          expectedFirmwareVersion,
          expectedFirmwareModuleType,
          firmwareVersions);
    }

    this.ssldPendingFirmwareUpdateRepository.delete(ssldPendingFirmwareUpdate);
    return true;
  }

  private void checkFirmwareHistory(
      final String deviceId,
      final List<org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion>
          firmwareVersions) {
    final List<org.opensmartgridplatform.domain.core.valueobjects.FirmwareVersion>
        firmwareVersionsNotCurrent =
            this.checkFirmwareHistoryForModuleVersionsNotCurrentlyInstalled(
                deviceId, firmwareVersions);
    this.tryToAddDeviceFirmwareFile(deviceId, firmwareVersionsNotCurrent);
  }

  /**
   * @param deviceId the id of the device we are checking
   * @param firmwareVersions the list of firmware versions to check if they are in the history of
   *     the devices firmware history
   * @return a list of firmware versions not present in the the devices firmware history
   */
  public List<FirmwareVersion> checkFirmwareHistoryForVersion(
      final String deviceId, final List<FirmwareVersion> firmwareVersions) {

    if (firmwareVersions.isEmpty()) {
      return firmwareVersions;
    }
    // copy input parameter
    final List<FirmwareVersion> firmwareVersionsToCheck = new ArrayList<>(firmwareVersions);

    // get history
    final Device device = this.deviceRepository.findByDeviceIdentification(deviceId);
    final List<DeviceFirmwareFile> deviceFirmwareFiles =
        this.deviceFirmwareFileRepository.findByDeviceOrderByInstallationDateAsc(device);
    final List<FirmwareVersion> firmwareVersionsInHistory =
        deviceFirmwareFiles.stream()
            .map(d -> d.getFirmwareFile().getModuleVersions().entrySet())
            .flatMap(Collection::stream)
            .map(
                e ->
                    new FirmwareVersion(
                        FirmwareModuleType.forDescription(e.getKey().getDescription()),
                        e.getValue()))
            .collect(Collectors.toList());

    // remove the history versions
    firmwareVersionsToCheck.removeAll(firmwareVersionsInHistory);

    return firmwareVersionsToCheck;
  }

  /**
   * @param deviceId the id of the device we are checking
   * @param firmwareVersions the list of firmware modules versions (so type and version) to check if
   *     they are currently installed on the device, using the history of the devices firmware
   *     history
   * @return a list of firmware module versions not present in the the devices firmware history
   */
  private List<FirmwareVersion> checkFirmwareHistoryForModuleVersionsNotCurrentlyInstalled(
      final String deviceId, final List<FirmwareVersion> firmwareVersions) {

    if (firmwareVersions.isEmpty()) {
      return firmwareVersions;
    }
    // copy input parameter
    final List<FirmwareVersion> firmwareVersionsToCheck = new ArrayList<>(firmwareVersions);

    // gets list of all historically installed modules
    final Device device = this.deviceRepository.findByDeviceIdentification(deviceId);
    final List<DeviceFirmwareFile> deviceFirmwareFiles =
        this.deviceFirmwareFileRepository.findByDeviceOrderByInstallationDateAsc(device);

    // Transform this list so it contains only the latest entry for each
    // moduleType
    final Map<String, FirmwareVersionWithInstallationDate>
        currentlyInstalledFirmwareVersionsPerType = new HashMap<>();

    for (final DeviceFirmwareFile firmwareFile : deviceFirmwareFiles) {

      final Map<FirmwareModule, String> fwms = firmwareFile.getFirmwareFile().getModuleVersions();
      final Date installationDate = firmwareFile.getInstallationDate();

      for (final Map.Entry<FirmwareModule, String> entry : fwms.entrySet()) {
        final String version = entry.getValue();
        final FirmwareModule fwm = entry.getKey();
        // check if this installation of this same kind of module is
        // of a later date
        if (currentlyInstalledFirmwareVersionsPerType.containsKey(fwm.getDescription())
            && currentlyInstalledFirmwareVersionsPerType
                .get(fwm.getDescription())
                .getInstallationDate()
                .before(installationDate)) {
          currentlyInstalledFirmwareVersionsPerType.replace(
              fwm.getDescription(),
              new FirmwareVersionWithInstallationDate(
                  installationDate,
                  new FirmwareVersion(
                      FirmwareModuleType.forDescription(fwm.getDescription()), version)));
        } else {
          // no other module of this type found yet so just add it
          currentlyInstalledFirmwareVersionsPerType.put(
              fwm.getDescription(),
              new FirmwareVersionWithInstallationDate(
                  installationDate,
                  new FirmwareVersion(
                      FirmwareModuleType.forDescription(fwm.getDescription()), version)));
        }
      }
    }

    final List<FirmwareVersion> latestfirmwareVersionsOfEachModuleTypeInHistory =
        currentlyInstalledFirmwareVersionsPerType.values().stream()
            .map(
                e ->
                    new FirmwareVersion(
                        e.getFirmwareVersion().getFirmwareModuleType(),
                        e.getFirmwareVersion().getVersion()))
            .collect(Collectors.toList());

    // remove the latest history (module)versions from the firmwareVersions
    // parameter
    firmwareVersionsToCheck.removeAll(latestfirmwareVersionsOfEachModuleTypeInHistory);

    return firmwareVersionsToCheck;
  }

  // Helper class to keep track of InstallationDate and FirmwareVersion
  private static class FirmwareVersionWithInstallationDate {
    private final Date installationDate;
    private final FirmwareVersion firmwareVersion;

    public FirmwareVersionWithInstallationDate(
        final Date installationDate, final FirmwareVersion firmwareVersion) {
      this.installationDate = installationDate;
      this.firmwareVersion = firmwareVersion;
    }

    public Date getInstallationDate() {
      return this.installationDate;
    }

    public FirmwareVersion getFirmwareVersion() {
      return this.firmwareVersion;
    }
  }

  public void tryToAddDeviceFirmwareFile(
      final String deviceIdentification, final List<FirmwareVersion> firmwareVersionsNotCurrent) {

    if (firmwareVersionsNotCurrent.isEmpty()) {
      LOGGER.info(
          "No firmware to look for, concerning device {}, so nothing to add.",
          deviceIdentification);
      return;
    }

    final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);

    // check each file for the module and the version as returned by the
    // device, in theory there could be files that have partially
    // overlapping modules,
    // therefore we check if all modules are in the file

    for (final FirmwareFile file :
        this.getAvailableFirmwareFilesForDeviceModel(device.getDeviceModel())) {
      if (firmwareFileContainsAllOfTheseModules(file, firmwareVersionsNotCurrent)) {
        // file found, insert a record into the history
        final DeviceFirmwareFile deviceFirmwareFile =
            new DeviceFirmwareFile(device, file, new Date(), INSTALLER);
        this.deviceFirmwareFileRepository.save(deviceFirmwareFile);

        if (LOGGER.isInfoEnabled()) {
          LOGGER.info(
              "Added new record to deviceFirmwareFile for device: {} with following modules (ModulesType/Versions):{} "
                  + ", using file: {}",
              deviceIdentification,
              firmwareVersionsNotCurrent.toString(),
              file.getFilename());
        }
        return;
      }
    }

    LOGGER.warn(
        "Could not find any firmware file for device: {} that contains (all of) the following modules (ModulesType/Versions):{}",
        deviceIdentification,
        firmwareVersionsNotCurrent);
  }

  private static boolean firmwareFileContainsAllOfTheseModules(
      final FirmwareFile file, final List<FirmwareVersion> firmwareVersions) {
    int numberOfModulesFound = 0;
    final Map<FirmwareModule, String> moduleVersionsInFile = file.getModuleVersions();

    for (final FirmwareVersion firmwareVersion : firmwareVersions) {
      final FirmwareModule module = createFirmwareModule(firmwareVersion);

      if (moduleVersionsInFile.containsKey(module)
          && moduleVersionsInFile.get(module).equals(firmwareVersion.getVersion())) {
        // module found in this file
        numberOfModulesFound++;

        // check if all different modules are in this file
        if (numberOfModulesFound == firmwareVersions.size()) {
          return true;
        }
      }
    }
    return false;
  }

  private static FirmwareModule createFirmwareModule(final FirmwareVersion firmwareVersion) {
    final String description =
        firmwareVersion.getFirmwareModuleType().getDescription().toLowerCase(Locale.getDefault());
    return new FirmwareModule(description);
  }

  private List<FirmwareFile> getAvailableFirmwareFilesForDeviceModel(
      final DeviceModel deviceModel) {
    final Manufacturer manufacturer = deviceModel.getManufacturer();

    return this.findAllFirmwareFiles(manufacturer.getCode(), deviceModel.getModelCode());
  }

  private List<FirmwareFile> findAllFirmwareFiles(
      final String manufacturer, final String modelCode) {
    List<FirmwareFile> firmwareFiles = new ArrayList<>();
    if (manufacturer != null) {
      final Manufacturer databaseManufacturer =
          this.manufacturerRepository.findByCode(manufacturer);
      final DeviceModel databaseDeviceModel =
          this.deviceModelRepository.findByManufacturerAndModelCode(
              databaseManufacturer, modelCode);
      if (databaseDeviceModel != null) {
        firmwareFiles = this.firmwareFileRepository.findByDeviceModel(databaseDeviceModel);
      }
    } else {
      final List<DeviceModel> deviceModels = this.deviceModelRepository.findByModelCode(modelCode);
      for (final DeviceModel deviceModel : deviceModels) {
        firmwareFiles.addAll(this.firmwareFileRepository.findByDeviceModel(deviceModel));
      }
    }

    return firmwareFiles;
  }

  // === SWITCH TO OTHER FIRMWARE VERSION ===

  public void switchFirmware(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final String messageType,
      final int messagePriority,
      final String version)
      throws FunctionalException {
    LOGGER.debug(
        "switchFirmware called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    this.findOrganisation(organisationIdentification);
    final Device device = this.findActiveDevice(deviceIdentification);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(
            correlationUid, organisationIdentification, deviceIdentification, version),
        messageType,
        messagePriority,
        device.getIpAddress());
  }
}
