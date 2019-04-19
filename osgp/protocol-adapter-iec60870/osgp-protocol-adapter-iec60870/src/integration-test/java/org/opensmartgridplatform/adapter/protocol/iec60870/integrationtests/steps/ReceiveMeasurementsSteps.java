/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.steps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ConnectionEventListener;
import org.openmuc.j60870.TypeId;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.AsduFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientAsduHandlerRegistry;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionCache;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.Iec60870ClientConnectionEventListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ConnectionInfo;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DomainInfo;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.LogItem;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseInfo;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.LogItemRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.matchers.MeasurementReportTypeMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ReceiveMeasurementsSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveMeasurementsSteps.class);

    private ConnectionEventListener connectionEventListener;

    @Autowired
    private DeviceResponseMessageSender iec60870ResponseMessageSender;

    @Autowired
    private LogItemRequestMessageSender iec60870LogItemRequestMessageSender;

    @Autowired
    private ClientAsduHandlerRegistry iec60870ClientAsduHandlerRegistry;

    @Given("an existing connection with an IEC60870 device")
    public void givenAnExistingConnection() {
        LOGGER.debug("Given an existing connection");

        final ConnectionInfo connectionInfo = new ConnectionInfo.Builder().commonAddress(0)
                .deviceIdentification("TEST-DEVICE").ipAddress("localhost").port(2404).build();
        final ResponseInfo responseInfo = new ResponseInfo.Builder().withDeviceIdentification("TEST-DEVICE")
                .withOrganisationIdentification("TEST-ORGANISATION")
                .withDomainInfo(new DomainInfo("TEST-DOMAIN", "TEST_DOMAIN-VERSION"))
                .withMessageType("TEST-MESSAGE-TYPE").build();
        this.connectionEventListener = new Iec60870ClientConnectionEventListener(connectionInfo,
                mock(ClientConnectionCache.class), this.iec60870ClientAsduHandlerRegistry, responseInfo);
    }

    @When("I receive an ASDU of type {string} from the IEC60870 device")
    public void whenIReceiveAnAsduOfType(final String typeId) {
        LOGGER.debug("When I receive an ASDU of type {}", typeId);

        final ASdu asdu = AsduFactory.ofType(TypeId.valueOf(typeId));
        this.connectionEventListener.newASdu(asdu);
    }

    @Then("I should send a measurement report of type {string} to the platform")
    public void thenIShouldSendAMeasurementReportOfType(final String typeId) {
        LOGGER.debug("Then I should send a measurement report of type {}", typeId);

        verify(this.iec60870ResponseMessageSender).send(argThat(new MeasurementReportTypeMatcher(typeId)));
    }

    @Then("I should send a log item")
    public void thenIShouldSendALogItemOfType() {
        LOGGER.debug("Then I should send a log item");

        verify(this.iec60870LogItemRequestMessageSender).send(any(LogItem.class));
    }
}
