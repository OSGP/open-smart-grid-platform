/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.hooks;

import java.util.Arrays;
import java.util.List;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;

import cucumber.api.Scenario;
import cucumber.api.java.Before;

/**
 * Class with all the scenario hooks when each scenario runs.
 */
public class ScenarioHooks {

    private DeviceRepository deviceRepository;
    private DlmsDeviceRepository dlmsDeviceRepository;
    private DeviceAuthorizationRepository deviceAuthorizationRepository;

    /**
     * Remove all stuff from the database before each test. Each test should stand on its own. Therefore
     * you should garantee that the scenario is complete.
     */
    @Before
    public void beforeScenario() {

        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("cucumber.xml");

        this.deviceRepository = context.getBean(DeviceRepository.class);
        this.dlmsDeviceRepository = context.getBean(DlmsDeviceRepository.class);
        this.deviceAuthorizationRepository = context.getBean(DeviceAuthorizationRepository.class);

        this.deviceAuthorizationRepository.removeAll();
        this.deviceRepository.removeAll();
        this.dlmsDeviceRepository.removeAll();

        context.close();
    }
    
    @After
    public void afterScenario() {
    	
    }
}
