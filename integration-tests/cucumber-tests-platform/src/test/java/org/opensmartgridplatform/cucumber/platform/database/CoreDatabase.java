// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.database;

import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.repositories.DeviceAuthorizationRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceFirmwareFileRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceFirmwareModuleRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceModelRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.EanRepository;
import org.opensmartgridplatform.domain.core.repositories.EventRepository;
import org.opensmartgridplatform.domain.core.repositories.FirmwareFileFirmwareModuleRepository;
import org.opensmartgridplatform.domain.core.repositories.FirmwareFileRepository;
import org.opensmartgridplatform.domain.core.repositories.LightMeasurementDeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.ManufacturerRepository;
import org.opensmartgridplatform.domain.core.repositories.OrganisationRepository;
import org.opensmartgridplatform.domain.core.repositories.RelayStatusRepository;
import org.opensmartgridplatform.domain.core.repositories.ScheduledTaskRepository;
import org.opensmartgridplatform.domain.core.repositories.SmartMeterRepository;
import org.opensmartgridplatform.domain.core.repositories.SsldPendingFirmwareUpdateRepository;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformDomain;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformFunctionGroup;
import org.opensmartgridplatform.logging.domain.repositories.DeviceLogItemPagingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CoreDatabase {

  private static final Logger LOGGER = LoggerFactory.getLogger(CoreDatabase.class);

  @Autowired private DeviceAuthorizationRepository deviceAuthorizationRepository;

  @Autowired private DeviceFirmwareFileRepository deviceFirmwareFileRepository;

  @Autowired private DeviceFirmwareModuleRepository deviceFirmwareModuleRepository;

  @Autowired private DeviceLogItemPagingRepository deviceLogItemRepository;

  @Autowired private DeviceModelRepository deviceModelRepository;

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private EanRepository eanRepository;

  @Autowired private EventRepository eventRepository;

  @Autowired private FirmwareFileFirmwareModuleRepository firmwareFileFirmwareModuleRepository;

  @Autowired private FirmwareFileRepository firmwareFileRepository;

  @Autowired private ManufacturerRepository manufacturerRepository;

  @Autowired private OrganisationRepository organisationRepository;

  @Autowired private ScheduledTaskRepository scheduledTaskRepository;

  @Autowired private SmartMeterRepository smartMeterRepository;

  @Autowired private SsldRepository ssldRepository;

  @Autowired private SsldPendingFirmwareUpdateRepository ssldPendingFirmwareUpdateRepository;

  @Autowired private RelayStatusRepository relayStatusRepository;

  @Autowired private LightMeasurementDeviceRepository lightMeasurementDeviceRepository;

  /**
   * This method is used to create default data not directly related to the specific tests. For
   * example: The test-org organization which is used to send authorized requests to the platform.
   */
  @Transactional("txMgrCore")
  public void insertDefaultData() {
    if (this.organisationRepository.findByOrganisationIdentification(
            PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION)
        == null) {
      // Create test organization used within the tests.
      final Organisation testOrg =
          new Organisation(
              PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION,
              PlatformDefaults.DEFAULT_ORGANIZATION_DESCRIPTION,
              PlatformDefaults.DEFAULT_PREFIX,
              PlatformFunctionGroup.ADMIN);
      testOrg.addDomain(PlatformDomain.COMMON);
      testOrg.addDomain(PlatformDomain.PUBLIC_LIGHTING);
      testOrg.addDomain(PlatformDomain.TARIFF_SWITCHING);
      testOrg.setIsEnabled(true);

      this.organisationRepository.save(testOrg);
    }

    // Create default test manufacturer
    final Manufacturer manufacturer =
        new Manufacturer(
            PlatformDefaults.DEFAULT_MANUFACTURER_CODE,
            PlatformDefaults.DEFAULT_MANUFACTURER_NAME,
            false);
    this.manufacturerRepository.save(manufacturer);

    // Create the default test model
    final DeviceModel deviceModel =
        new DeviceModel(
            manufacturer,
            PlatformDefaults.DEFAULT_DEVICE_MODEL_MODEL_CODE,
            PlatformDefaults.DEFAULT_DEVICE_MODEL_DESCRIPTION,
            true);
    this.deviceModelRepository.save(deviceModel);
  }

  @Transactional("txMgrCore")
  public void prepareDatabaseForScenario() {
    this.batchDeleteAll();
  }

  @Transactional("txMgrCore")
  public void removeLeftOvers() {
    this.normalDeleteAll();
  }

  private void batchDeleteAll() {
    LOGGER.info("Starting batchDeleteAll()");
    this.deviceAuthorizationRepository.deleteAllInBatch();
    this.deviceLogItemRepository.deleteAllInBatch();
    this.scheduledTaskRepository.deleteAllInBatch();
    this.eanRepository.deleteAllEans();
    this.deviceRepository.deleteDeviceOutputSettings();
    this.deviceFirmwareFileRepository.deleteAllInBatch();
    this.deviceFirmwareModuleRepository.deleteAllInBatch();
    this.eventRepository.deleteAllInBatch();
    this.smartMeterRepository.deleteAllInBatch();
    this.relayStatusRepository.deleteAllInBatch();
    this.ssldPendingFirmwareUpdateRepository.deleteAllInBatch();
    this.ssldRepository.deleteAllInBatch();
    this.deviceRepository.deleteAllInBatch();
    this.lightMeasurementDeviceRepository.deleteAllInBatch();
    this.firmwareFileFirmwareModuleRepository.deleteAllInBatch();
    this.firmwareFileRepository.deleteAllInBatch();
    this.deviceModelRepository.deleteAllInBatch();
    this.manufacturerRepository.deleteAllInBatch();
    this.organisationRepository.deleteAllInBatch();
  }

  private void normalDeleteAll() {
    LOGGER.info("Starting normalDeleteAll()");
    this.deviceAuthorizationRepository.deleteAll();
    this.deviceLogItemRepository.deleteAll();
    this.scheduledTaskRepository.deleteAll();
    this.eanRepository.deleteAllEans();
    this.deviceRepository.deleteDeviceOutputSettings();
    this.deviceFirmwareFileRepository.deleteAll();
    this.deviceFirmwareModuleRepository.deleteAll();
    this.eventRepository.deleteAll();
    this.smartMeterRepository.deleteAll();
    this.relayStatusRepository.deleteAll();
    this.ssldPendingFirmwareUpdateRepository.deleteAll();
    this.ssldRepository.deleteAll();
    this.deviceRepository.deleteAll();
    this.lightMeasurementDeviceRepository.deleteAll();
    this.firmwareFileFirmwareModuleRepository.deleteAll();
    this.firmwareFileRepository.deleteAll();
    this.deviceModelRepository.deleteAll();
    this.manufacturerRepository.deleteAll();
    this.organisationRepository.deleteAll();
  }
}
