/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.integrationtests.domain.commands;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmuc.jdlms.LnClientConnection;
import org.osgp.adapter.protocol.dlms.application.config.ApplicationContext;
import org.osgp.adapter.protocol.dlms.application.services.DomainHelperService;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionFactory;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsDeviceMessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsUnit;

/**
 * This integration test requires A running up to date postgres db that can be
 * accessed using user and password from test.properties and an up and running
 * E-meter with device id E0004001515495114 and the ip address in this test.
 * Tests under the integrationtests package will only be run with
 * "-DskipITs=false"
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:/test.properties")
@ContextConfiguration(classes = { ApplicationContext.class })
public class ScalerUnitTest {

    @Autowired
    private TestScalerUnitCommandExecutor commandExecutor;

    @Autowired
    private DlmsConnectionFactory dlmsConnectionFactory;

    @Autowired
    private DomainHelperService domainHelperService;

    @Test
    public void testGetScalerUnit() throws Exception {
        final DlmsDeviceMessageMetadata dlmsDeviceMessageMetadata = new DlmsDeviceMessageMetadata();
        dlmsDeviceMessageMetadata.setDeviceIdentification("E0004001515495114");
        dlmsDeviceMessageMetadata.setIpAddress("89.200.96.223");

        final LnClientConnection connection = this.dlmsConnectionFactory.getConnection(this.domainHelperService
                .findDlmsDevice(dlmsDeviceMessageMetadata));

        final ScalerUnitTestResponse execute = this.commandExecutor.execute(connection, new TestChannelQuery());

        Assert.assertEquals(DlmsUnit.WH, execute.getScalerUnit().getDlmsUnit());

    }

}
