/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.DeviceFirmwareFile;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.FirmwareFile;
import org.opensmartgridplatform.domain.core.entities.FirmwareModule;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.repositories.DeviceFirmwareFileRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceModelRepository;
import org.opensmartgridplatform.domain.core.repositories.FirmwareFileRepository;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.io.Files;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * The firmware file related steps.
 */
public class FirmwareFileSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirmwareFileSteps.class);

    @Autowired
    private SsldRepository ssldRepository;

    @Autowired
    private DeviceModelRepository deviceModelRepository;

    @Autowired
    private FirmwareFileRepository firmwareFileRepository;

    @Autowired
    private DeviceFirmwareFileRepository deviceFirmwareFileRepository;

    @Value("${firmware.file.path}")
    private String firmwareFilePath;

    @Autowired
    private DeviceModelSteps deviceModelSteps;

    @Autowired
    private DeviceFirmwareModuleSteps deviceFirmwareModuleSteps;

    /**
     * Generic method which adds a firmware using the settings.
     *
     * @param settings
     *            The settings for the firmware to be used.
     * @throws IOException
     */
    @Given("^a firmware$")
    public void aFirmware(final Map<String, String> settings) throws IOException {

        /*
         * Model code does not uniquely identify a device model, which is why
         * deviceModelRepository is changed to return a list of device models.
         * In the test data that is set up, there probably is only one device
         * model for the given model code, and just selecting the first device
         * model returned should work.
         *
         * A better solution might be to add the manufacturer in the scenario
         * data and do a lookup by manufacturer and model code, which should
         * uniquely define the device model.
         */
        final List<DeviceModel> deviceModels = this.deviceModelRepository
                .findByModelCode(getString(settings, PlatformKeys.DEVICEMODEL_MODELCODE));
        final DeviceModel deviceModel;
        if (deviceModels.isEmpty()) {
            deviceModel = this.deviceModelSteps.aDeviceModel(settings);
        } else {
            deviceModel = deviceModels.get(0);
        }

        final boolean isForSmartMeters = getBoolean(settings, "FirmwareIsForSmartMeters", false);
        final Map<FirmwareModule, String> versionsByModule = this.deviceFirmwareModuleSteps
                .getFirmwareModuleVersions(settings, isForSmartMeters);

        /*
         * Using the filename as firmware identification is necessary as long as
         * the DLMS protocol adapter expects the filename to identify a
         * firmware. As soon as the protocol adapter accepts the newer
         * identification, it is no longer necessary to do this and the default
         * random identification should do fine for the tests. (The
         * identification then no longer needs to be added to the constructor
         * used to create the firmware.)
         */
        final String identification = getString(settings, PlatformKeys.FIRMWARE_FILE_FILENAME,
                UUID.randomUUID().toString().replace("-", ""));
        final String filename = getString(settings, PlatformKeys.FIRMWARE_FILE_FILENAME, "");
        final boolean fileExists = getBoolean(settings, PlatformKeys.FIRMWARE_FILE_EXISTS,
                PlatformDefaults.FIRMWARE_FILE_EXISTS);
        final byte[] file;
        if (fileExists) {
            file = this.readFile(deviceModel, filename, isForSmartMeters);
        } else {
            file = null;
        }
        FirmwareFile firmwareFile = new FirmwareFile(identification, filename,
                getString(settings, PlatformKeys.FIRMWARE_DESCRIPTION, PlatformDefaults.FIRMWARE_DESCRIPTION),
                getBoolean(settings, PlatformKeys.FIRMWARE_PUSH_TO_NEW_DEVICES,
                        PlatformDefaults.FIRMWARE_PUSH_TO_NEW_DEVICE),
                file, null);

        if (!isForSmartMeters) {
            // Create file on disk
            this.createFile(deviceModel.getManufacturer().getCode(), deviceModel.getModelCode(), filename);
        }
        /*
         * Save the firmware file before adding the device model and updating
         * the firmware module data. Trying to save a new firmware file with the
         * related entities that were persisted earlier causes Hibernate
         * exceptions referring to persistent entities in detached state.
         */
        firmwareFile = this.firmwareFileRepository.save(firmwareFile);
        firmwareFile.addDeviceModel(deviceModel);
        firmwareFile.updateFirmwareModuleData(versionsByModule);
        this.firmwareFileRepository.save(firmwareFile);
    }

    private void createFile(final String manufacturerCode, final String modelCode, final String filename) {
        final File modelCodeFolder = new File(
                this.firmwareFilePath + File.separator + manufacturerCode + File.separator + modelCode);
        if (!modelCodeFolder.exists()) {
            modelCodeFolder.mkdirs();
        }

        final File firmwareFile = new File(modelCodeFolder.getAbsolutePath() + File.separator + filename);
        if (!firmwareFile.exists()) {
            try {
                firmwareFile.createNewFile();
            } catch (final IOException e) {
                LOGGER.error("Create new firmware file failed: {}/{}/{}", manufacturerCode, modelCode, filename, e);
            }
        }
    }

    @Given("^an installed firmware file for an ssld$")
    @Transactional("txMgrCore")
    public void anInstalledFirmwareFileForAnSsld(final Map<String, String> settings) {

        final Ssld ssld = this.ssldRepository.findByDeviceIdentification(getString(settings,
                PlatformKeys.KEY_DEVICE_IDENTIFICATION, PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        final FirmwareFile firmware = this.firmwareFileRepository.findByIdentification(
                getString(settings, PlatformKeys.FIRMWARE_FILE_FILENAME, PlatformDefaults.FIRMWARE_FILENAME));

        final Date installationDate = new Date();

        final String installedByUser = "installed by test code";

        final DeviceFirmwareFile deviceFirmwareFile = new DeviceFirmwareFile(ssld, firmware, installationDate,
                installedByUser);

        this.deviceFirmwareFileRepository.save(deviceFirmwareFile);
    }

    /**
     * Verify whether the entity is created as expected.
     */
    @Then("^the entity firmware exists$")
    public void theEntityFirmwareExists(final Map<String, String> expectedEntity) {
        Wait.until(() -> {
            /*
             * The filename does not necessarily need to be unique. Therefore
             * the lookup of FirmwareFile by filename has been changed to return
             * a list instead of a single result.
             *
             * For the test code with the known test data the short term change
             * to use the first firmware file from the list returned should lead
             * to similar test results as before.
             *
             * The errors occurring when the list would be empty don't look much
             * different from NullPointerExceptions if no firmware file would be
             * returned.
             *
             * (The way Wait.until logs the expected - or other - exceptions and
             * continues to loop does not look clean, but that is another issue
             * that may be good to address.)
             */
            final List<FirmwareFile> firmwareFiles = this.firmwareFileRepository
                    .findByFilename(getString(expectedEntity, PlatformKeys.FIRMWARE_FILE_FILENAME));
            final FirmwareFile firmwareFile = firmwareFiles.get(0);
            final DeviceModel deviceModel = firmwareFile.getDeviceModels().iterator().next();

            Assert.assertEquals(
                    getString(expectedEntity, PlatformKeys.FIRMWARE_DESCRIPTION, PlatformDefaults.FIRMWARE_DESCRIPTION),
                    firmwareFile.getDescription());
            Assert.assertEquals(getString(expectedEntity, PlatformKeys.FIRMWARE_MODULE_VERSION_COMM,
                    PlatformDefaults.FIRMWARE_MODULE_VERSION_COMM), firmwareFile.getModuleVersionComm());
            Assert.assertEquals(getString(expectedEntity, PlatformKeys.FIRMWARE_MODULE_VERSION_FUNC,
                    PlatformDefaults.FIRMWARE_MODULE_VERSION_FUNC), firmwareFile.getModuleVersionFunc());
            Assert.assertEquals(getString(expectedEntity, PlatformKeys.FIRMWARE_MODULE_VERSION_MA,
                    PlatformDefaults.FIRMWARE_MODULE_VERSION_MA), firmwareFile.getModuleVersionMa());
            Assert.assertEquals(getString(expectedEntity, PlatformKeys.FIRMWARE_MODULE_VERSION_MBUS,
                    PlatformDefaults.FIRMWARE_MODULE_VERSION_MBUS), firmwareFile.getModuleVersionMbus());
            Assert.assertEquals(getString(expectedEntity, PlatformKeys.FIRMWARE_MODULE_VERSION_SEC,
                    PlatformDefaults.FIRMWARE_MODULE_VERSION_SEC), firmwareFile.getModuleVersionSec());

            Assert.assertEquals(getString(expectedEntity, PlatformKeys.DEVICEMODEL_MODELCODE,
                    PlatformDefaults.DEVICE_MODEL_MODEL_CODE), deviceModel.getModelCode());
        });
    }

    /**
     * Verify whether the entity is NOT created as expected.
     */
    @Then("^the entity firmware does not exist$")
    public void theEntityFirmwareDoesNotExist(final Map<String, String> expectedEntity) {
        Wait.until(() -> {
            /*
             * The filename does not necessarily need to be unique. Therefore
             * the lookup of FirmwareFile by filename has been changed to return
             * a list instead of a single result.
             *
             * For the test code with the known test data the short term change
             * to use the first firmware file from the list returned should lead
             * to similar test results as before.
             */
            final List<FirmwareFile> firmwareFiles = this.firmwareFileRepository
                    .findByFilename(getString(expectedEntity, PlatformKeys.FIRMWARE_FILE_FILENAME));
            if (!firmwareFiles.isEmpty()) {
                final FirmwareFile firmwareFile = firmwareFiles.get(0);
                final DeviceModel deviceModel = firmwareFile.getDeviceModels().iterator().next();
                Assert.assertNotEquals(getString(expectedEntity, PlatformKeys.DEVICEMODEL_MODELCODE,
                        PlatformDefaults.DEVICE_MODEL_MODEL_CODE), deviceModel.getModelCode());
            }
        });
    }

    private byte[] readFile(final DeviceModel deviceModel, final String filename, final boolean isForSmartMeters)
            throws IOException {
        byte[] fileBytes = null;
        if (isForSmartMeters) {
            fileBytes = this.getFirmwareFileBytes(filename);
        }

        return fileBytes;
    }

    private byte[] getFirmwareFileBytes(final String filename) throws IOException {
        final String path = this.getFirmwareFilepath(filename);
        final File file = new File(path);
        return Files.toByteArray(file);
    }

    private String getFirmwareFilepath(final String filename) {
        String path;
        if (StringUtils.endsWith(this.firmwareFilePath, File.separator)) {
            path = this.firmwareFilePath + filename;
        } else {
            path = this.firmwareFilePath + File.separator + filename;
        }
        return path;
    }

}
