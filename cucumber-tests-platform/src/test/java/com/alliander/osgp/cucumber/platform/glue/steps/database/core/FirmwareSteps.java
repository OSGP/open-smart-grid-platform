/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.database.core;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getBoolean;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.wait.Wait;
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

		DeviceModel deviceModel = this.deviceModelRepo.findByModelCode(getString(settings, Keys.DEVICEMODEL_MODELCODE));
		if (deviceModel == null) {
			deviceModel = this.deviceModelSteps.aDeviceModel(settings);
		}

		final FirmwareModuleData firmwareModuleData = new FirmwareModuleData(
				getString(settings, Keys.FIRMWARE_MODULE_VERSION_COMM, Defaults.FIRMWARE_MODULE_VERSION_COMM),
				getString(settings, Keys.FIRMWARE_MODULE_VERSION_FUNC, Defaults.FIRMWARE_MODULE_VERSION_FUNC),
				getString(settings, Keys.FIRMWARE_MODULE_VERSION_MA, Defaults.FIRMWARE_MODULE_VERSION_MA),
				getString(settings, Keys.FIRMWARE_MODULE_VERSION_MBUS, Defaults.FIRMWARE_MODULE_VERSION_MBUS),
				getString(settings, Keys.FIRMWARE_MODULE_VERSION_SEC, Defaults.FIRMWARE_MODULE_VERSION_SEC));

		final Firmware entity = new Firmware(deviceModel, getString(settings, Keys.FIRMWARE_FILENAME, ""),
				getString(settings, Keys.FIRMWARE_DESCRIPTION, Defaults.FIRMWARE_DESCRIPTION),
				getBoolean(settings, Keys.FIRMWARE_PUSH_TO_NEW_DEVICES, Defaults.FIRMWARE_PUSH_TO_NEW_DEVICE),
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
			final Firmware entity = this.firmwareRepo.findByFilename(getString(expectedEntity, Keys.FIRMWARE_FILENAME));
			final Manufacturer manufacturer = entity.getDeviceModel().getManufacturerId();
			
			Assert.assertEquals(getString(expectedEntity, Keys.MANUFACTURER_ID, Defaults.DEFAULT_MANUFACTURER_ID),
					manufacturer);
			Assert.assertEquals(getBoolean(expectedEntity, Keys.USE_PREFIX, Defaults.DEFAULT_MANUFACTURER_USE_PREFIX),
					manufacturer.isUsePrefix());
		});
	}
}