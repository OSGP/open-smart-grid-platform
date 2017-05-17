/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.database.core;

import static com.alliander.osgp.cucumber.core.Helpers.getBoolean;
import static com.alliander.osgp.cucumber.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.core.Wait;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Firmware;
import com.alliander.osgp.domain.core.entities.Manufacturer;
import com.alliander.osgp.domain.core.repositories.DeviceModelRepository;
import com.alliander.osgp.domain.core.repositories.FirmwareRepository;
import com.alliander.osgp.domain.core.valueobjects.FirmwareModuleData;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * The manufacturer related steps.
 */
public class FirmwareSteps {

	@Autowired
	private DeviceModelRepository deviceModelRepo;

	@Autowired
	private FirmwareRepository firmwareRepo;

	@Autowired
	private DeviceModelSteps deviceModelSteps;

	/**
	 * Generic method which adds a firmware using the settings.
	 *
	 * @param settings
	 *            The settings for the firmware to be used.
	 * @throws Throwable
	 */
	@Given("^a firmware")
	public void aFirmware(final Map<String, String> settings) {

		DeviceModel deviceModel = this.deviceModelRepo.findByModelCode(getString(settings, PlatformKeys.DEVICEMODEL_MODELCODE));
		if (deviceModel == null) {
			deviceModel = this.deviceModelSteps.aDeviceModel(settings);
		}

		final FirmwareModuleData firmwareModuleData = new FirmwareModuleData(
				getString(settings, PlatformKeys.FIRMWARE_MODULE_VERSION_COMM, PlatformDefaults.FIRMWARE_MODULE_VERSION_COMM),
				getString(settings, PlatformKeys.FIRMWARE_MODULE_VERSION_FUNC, PlatformDefaults.FIRMWARE_MODULE_VERSION_FUNC),
				getString(settings, PlatformKeys.FIRMWARE_MODULE_VERSION_MA, PlatformDefaults.FIRMWARE_MODULE_VERSION_MA),
				getString(settings, PlatformKeys.FIRMWARE_MODULE_VERSION_MBUS, PlatformDefaults.FIRMWARE_MODULE_VERSION_MBUS),
				getString(settings, PlatformKeys.FIRMWARE_MODULE_VERSION_SEC, PlatformDefaults.FIRMWARE_MODULE_VERSION_SEC));

		final Firmware entity = new Firmware(deviceModel, getString(settings, PlatformKeys.FIRMWARE_FILENAME, ""),
				getString(settings, PlatformKeys.FIRMWARE_DESCRIPTION, PlatformDefaults.FIRMWARE_DESCRIPTION),
				getBoolean(settings, PlatformKeys.FIRMWARE_PUSH_TO_NEW_DEVICES, PlatformDefaults.FIRMWARE_PUSH_TO_NEW_DEVICE),
				firmwareModuleData);

		this.firmwareRepo.save(entity);
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
			final Firmware entity = this.firmwareRepo.findByFilename(getString(expectedEntity, PlatformKeys.FIRMWARE_FILENAME));
			final Manufacturer manufacturer = entity.getDeviceModel().getManufacturerId();
			
			Assert.assertEquals(getString(expectedEntity, PlatformKeys.MANUFACTURER_ID, PlatformDefaults.DEFAULT_MANUFACTURER_ID),
					manufacturer);
			Assert.assertEquals(getBoolean(expectedEntity, PlatformKeys.USE_PREFIX, PlatformDefaults.DEFAULT_MANUFACTURER_USE_PREFIX),
					manufacturer.isUsePrefix());
		});
	}
}