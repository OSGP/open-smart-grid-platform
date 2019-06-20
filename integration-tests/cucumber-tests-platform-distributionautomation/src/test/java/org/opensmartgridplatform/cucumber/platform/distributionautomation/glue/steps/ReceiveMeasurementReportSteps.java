/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.steps;

import org.openmuc.j60870.TypeId;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetHealthStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetHealthStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.notification.Notification;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.notification.NotificationType;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.mocks.iec60870.Iec60870MockServer;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.support.ws.distributionautomation.DistributionAutomationDeviceManagementClient;
import org.opensmartgridplatform.iec60870.Iec60870ASduHandler;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870ASduFactory;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers.Iec60870InterrogationCommandASduHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ReceiveMeasurementReportSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveMeasurementReportSteps.class);

    @Autowired
    private DistributionAutomationDeviceManagementClient client;

    @Autowired
    private Iec60870MockServer mockServer;

    @When("^Organization (.+) connects to device (.+)$")
    public void iRequestTheHealthStatus(final String organizationIdentification, final String deviceIdentification)
            throws WebServiceSecurityException {
        // There's no "connect" method yet. As a workaround, connect to the
        // device by requesting its health status.

        // Ensure the mocked device returns an ASDU
        this.mockServer.addIec60870ASduHandler(TypeId.C_IC_NA_1, this.getInterrogationCommandASduHandler());

        final GetHealthStatusRequest request = new GetHealthStatusRequest();
        request.setDeviceIdentification(deviceIdentification);

        final GetHealthStatusAsyncResponse response = this.client.getHealthStatus(organizationIdentification, request);
        LOGGER.debug("CorrelationUid received in the response for the getHealthStatusRequest: {}",
                response.getAsyncResponse().getCorrelationUid());
    }

    private Iec60870ASduHandler getInterrogationCommandASduHandler() {
        final Iec60870InterrogationCommandASduHandler iec60870InterrogationCommandASduHandler = new Iec60870InterrogationCommandASduHandler();
        final Iec60870ASduFactory iec60870AsduFactory = new Iec60870ASduFactory();
        ReflectionTestUtils.setField(iec60870InterrogationCommandASduHandler, "iec60870AsduFactory",
                iec60870AsduFactory);

        return iec60870InterrogationCommandASduHandler;
    }

    @Then("^I get a measurement report for device (.+)$")
    public void iGetAMeasurementReportForDevice(final String deviceIdentification) throws WebServiceSecurityException {

        Notification notification = null;
        boolean found = false;
        do {
            notification = this.client.waitForNotification();

            found = deviceIdentification.equals(notification.getDeviceIdentification())
                    && NotificationType.GET_MEASUREMENT_REPORT == notification.getNotificationType();
        } while (!found);

        this.client.getMeasurementReportResponse(notification);
    }

}
