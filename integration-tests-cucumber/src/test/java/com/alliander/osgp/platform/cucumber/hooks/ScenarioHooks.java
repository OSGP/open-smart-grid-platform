/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.hooks;

import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.OrganisationRepository;
import com.alliander.osgp.domain.core.valueobjects.PlatformDomain;
import com.alliander.osgp.domain.core.valueobjects.PlatformFunctionGroup;
import com.alliander.osgp.platform.cucumber.SmartMetering;
import com.alliander.osgp.platform.cucumber.support.DeviceId;
import com.alliander.osgp.platform.cucumber.support.ServiceEndpoint;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;

/**
 * Class with all the scenario hooks when each scenario runs.
 */
public class ScenarioHooks {

	private final Logger LOGGER = LoggerFactory.getLogger(SmartMetering.class);

    private DeviceRepository deviceRepository;
    private DlmsDeviceRepository dlmsDeviceRepository;
    private DeviceAuthorizationRepository deviceAuthorizationRepository;
    private OrganisationRepository organizationRepository;

	@Autowired
    private DeviceId deviceId;

    @Autowired
    private ServiceEndpoint serviceEndpoint;

    /**
     * Executed before each scenario.
     * 
     * Remove all stuff from the database before each test. Each test should stand on its own. Therefore
     * you should guarantee that the scenario is complete.
     */
    @Before
    public void beforeScenario() {
        clearDatabase();

        setServiceEndPoint();
    }

    /**
     * Executed after each scenario.
     */
    @After
    public void afterScenario() {
    	// Nothing to do yet.
    }

    /**
     * Clears the database except for the normal data.
     */
    private void clearDatabase() {
    	final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("cucumber.xml");

        // Get all the repositories.
        this.deviceRepository = context.getBean(DeviceRepository.class);
        this.dlmsDeviceRepository = context.getBean(DlmsDeviceRepository.class);
        this.deviceAuthorizationRepository = context.getBean(DeviceAuthorizationRepository.class);
        this.organizationRepository = context.getBean(OrganisationRepository.class);

        // Remove all data from previous scenario.
        this.deviceAuthorizationRepository.deleteAll();
        this.deviceRepository.deleteAll();
        this.dlmsDeviceRepository.deleteAll();

        // TODO Remove here all organizations except test-org. In GlobalHooks, the default data will be added
        // in the database. The stuff here should not remove that.
        this.organizationRepository.deleteAll();
        Organisation testOrg = new Organisation("test-org", "Test Organization for all tests", "MAA", PlatformFunctionGroup.ADMIN);
		testOrg.addDomain(PlatformDomain.COMMON);
		organizationRepository.save(testOrg);
		//this.organizationRepository.deleteAllExcept("test-org");
        //for (Organisation organisation : this.organizationRepository.findAll()) {
        //	if (organisation.getOrganisationIdentification() != "test-org") {
        //    	this.organizationRepository.delete(organisation);
        //	}
        //}
        
        // TODO: Clean all other repositories ....

        context.close();
    }
    
    /**
     * TODO: Where is this needed for, and why is it hard coded?
     */
    private void setServiceEndPoint() {
        final Map<String, String> PROPERTIES_MAP = new HashMap<>();
        PROPERTIES_MAP.put("TST2", "osgp-tst.cloudapp.net:62443");
        PROPERTIES_MAP.put("TST7", "osgp-tst.cloudapp.net:57443");
        PROPERTIES_MAP.put("AWS", "slimme-meters.aws.osg-platform.com:443");

        this.serviceEndpoint.setServiceEndpoint(PROPERTIES_MAP.get("TST2"));
    }
    
    @After("@SLIM-511")
    public void activateDevice(final Scenario scenario) {
        LOGGER.info("Resetting database after runnign scenario @SLIM-511");
        this.setDeviceIsActivateState(this.deviceId.getDeviceIdE(), true);
        LOGGER.info("Database settings are reset after @SLIM-511");
    }

    private void deleteDevicesFromPlatform(final List<String> deviceIdList) {
        for (final String deviceId : deviceIdList) {
            this.deleteCoreRecords(deviceId);
            this.deleteDlmsRecords(deviceId);
        }
    }

    private void deleteCoreRecords(final String deviceId) {
        Device device = this.deviceRepository.findByDeviceIdentification(deviceId);

        if (device != null) {
            LOGGER.info("deleting device and device_authorisations" + deviceId);
            final List<DeviceAuthorization> devAuths = this.deviceAuthorizationRepository.findByDevice(device);
            for (final DeviceAuthorization devauth : devAuths) {
                this.deviceAuthorizationRepository.delete(devauth);
            }

            device = this.deviceRepository.findByDeviceIdentification(deviceId);
            if (device != null) {
                this.deviceRepository.delete(device);
            }
        }
    }

    private void deleteDlmsRecords(final String deviceId) {
        final DlmsDevice dlmsDevice = this.dlmsDeviceRepository.findByDeviceIdentification(deviceId);
        if (dlmsDevice != null) {
            LOGGER.info("deleting dlmsDevice and securityKeys..." + deviceId);
            this.dlmsDeviceRepository.delete(dlmsDevice);
        }
    }

    public void setDeviceIsActivateState(final String deviceId, final boolean isActiveState) {
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("cucumber.xml");
        this.deviceRepository = context.getBean(DeviceRepository.class);
        this.setDeviceIsActive(deviceId, isActiveState);
        context.close();
    }

    private void setDeviceIsActive(final String deviceId, final boolean newState) {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceId);
        if (device != null) {
            LOGGER.info("setting dlmsDevice.setActivated() to " + newState + " for device " + deviceId);
            device.setActive(newState);
            this.deviceRepository.save(device);
        } else {
            LOGGER.error("no such device " + deviceId);
        }
    }
}
