/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.database.core;

import static com.alliander.osgp.cucumber.core.Helpers.getBoolean;
import static com.alliander.osgp.cucumber.core.Helpers.getNullOrNonEmptyString;
import static com.alliander.osgp.cucumber.core.Helpers.getString;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alliander.osgp.cucumber.core.Wait;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.FirmwareFile;
import com.alliander.osgp.domain.core.entities.FirmwareModule;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.FirmwareFileRepository;
import com.alliander.osgp.domain.core.repositories.FirmwareModuleRepository;
import com.alliander.osgp.domain.core.valueobjects.FirmwareModuleData;
import com.google.common.io.Files;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * The firmware file related steps.
 */
public class FirmwareFileSteps {

    @Autowired
    private DeviceModelRepository deviceModelRepository;

    @Autowired
    private FirmwareFileRepository firmwareFileRepository;

    @Autowired
    private FirmwareModuleRepository firmwareModuleRepository;

    @Value("${firmware.file.path}")
    private String firmwareFilePath;

    @Autowired
    private DeviceModelSteps deviceModelSteps;

    /**
     * Generic method which adds a firmware using the settings.
     *
     * @param settings
     *            The settings for the firmware to be used.
     * @throws IOException
     * @throws Throwable
     */
    @Given("^a firmware")
    public void aFirmware(final Map<String, String> settings) {

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

        final String comm = getNullOrNonEmptyString(settings, PlatformKeys.FIRMWARE_MODULE_VERSION_COMM,
                PlatformDefaults.FIRMWARE_MODULE_VERSION_COMM);
        final String func = getNullOrNonEmptyString(settings, PlatformKeys.FIRMWARE_MODULE_VERSION_FUNC,
                PlatformDefaults.FIRMWARE_MODULE_VERSION_FUNC);
        final String ma = getNullOrNonEmptyString(settings, PlatformKeys.FIRMWARE_MODULE_VERSION_MA,
                PlatformDefaults.FIRMWARE_MODULE_VERSION_MA);
        final String mbus = getNullOrNonEmptyString(settings, PlatformKeys.FIRMWARE_MODULE_VERSION_MBUS,
                PlatformDefaults.FIRMWARE_MODULE_VERSION_MBUS);
        final String sec = getNullOrNonEmptyString(settings, PlatformKeys.FIRMWARE_MODULE_VERSION_SEC,
                PlatformDefaults.FIRMWARE_MODULE_VERSION_SEC);

        final FirmwareModuleData firmwareModuleData = new FirmwareModuleData(comm, func, ma, mbus, sec);
        /*
         * The model for storing firmware module versions has changed from
         * firmware table columns to more flexible mappings for potentially more
         * types of firmware modules. In the earlier implementation the 'func'
         * version could refer to different firmware modules depending if smart
         * meters or other devices were involved.
         *
         * A cleaner way to integrate the new model for firmware version modules
         * in the test steps will have to be worked out, but for now a hack is
         * introduced to set "FirmwareIsForSmartMeters" to true in the settings
         * for smart meter firmware (see insertCoreFirmware in
         * com.alliander.osgp.cucumber.smartmetering.integration.glue.steps.
         * FirmwareSteps).
         */
        final boolean isForSmartMeters = getBoolean(settings, "FirmwareIsForSmartMeters", false);
        final Map<FirmwareModule, String> versionsByModule = firmwareModuleData
                .getVersionsByModule(this.firmwareModuleRepository, isForSmartMeters);

        /*
         * Using the filename as firmware identification is necessary as long as
         * the DLMS protocol adapter expects the filename to identify a
         * firmware. As soon as the protocol adapter accepts the newer
         * identification, it is no longer necessary to do this and the default
         * random identification should do fine for the tests. (The
         * identification then no longer needs to be added to the constructor
         * used to create the firmware.)
         */
        final String identification = getString(settings, PlatformKeys.FIRMWARE_FILENAME,
                UUID.randomUUID().toString().replace("-", ""));
        final String filename = getString(settings, PlatformKeys.FIRMWARE_FILENAME, "");
        byte[] file = null;
        try {
            file = this.getFirmwareFileBytes(filename);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        FirmwareFile firmwareFile = new FirmwareFile(identification, filename,
                getString(settings, PlatformKeys.FIRMWARE_DESCRIPTION, PlatformDefaults.FIRMWARE_DESCRIPTION),
                getBoolean(settings, PlatformKeys.FIRMWARE_PUSH_TO_NEW_DEVICES,
                        PlatformDefaults.FIRMWARE_PUSH_TO_NEW_DEVICE),
                file, null);
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

    /**
     * Verify whether the entity is created as expected.
     *
     * @param expectedEntity
     * @throws Throwable
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
                    .findByFilename(getString(expectedEntity, PlatformKeys.FIRMWARE_FILENAME));
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
     *
     * @param expectedEntity
     * @throws Throwable
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
                    .findByFilename(getString(expectedEntity, PlatformKeys.FIRMWARE_FILENAME));
            if (!firmwareFiles.isEmpty()) {
                final FirmwareFile firmwareFile = firmwareFiles.get(0);
                final DeviceModel deviceModel = firmwareFile.getDeviceModels().iterator().next();
                Assert.assertNotEquals(getString(expectedEntity, PlatformKeys.DEVICEMODEL_MODELCODE,
                        PlatformDefaults.DEVICE_MODEL_MODEL_CODE), deviceModel.getModelCode());
            }
        });
    }

    private byte[] getFirmwareFileBytes(final String filename) throws IOException {
        final String path = this.getFirmwareFilepath(filename);
        try {
            final File file = new File(path);
            final byte[] bytes = Files.toByteArray(file);
            return bytes;
        } catch (final IOException e) {
            throw e;
        }
    }

    private String getFirmwareFilepath(final String filename) {
        String path = this.firmwareFilePath;
        if (StringUtils.endsWith(this.firmwareFilePath, "/")) {
            path = this.firmwareFilePath + filename;
        } else {
            path = this.firmwareFilePath + "/" + filename;
        }
        return path;
    }

}
