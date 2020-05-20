/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationKeys.INFORMATION_ELEMENT_VALUE;
import static org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationKeys.INFORMATION_OBJECT_ADDRESS;
import static org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationKeys.INFORMATION_OBJECT_TYPE;
import static org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationKeys.PROFILE;

import java.util.Map;

import org.openmuc.j60870.ASduType;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.BitmaskMeasurementElement;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.FloatMeasurementElement;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetHealthStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetHealthStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.GetMeasurementReportResponse;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementElement;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.generic.MeasurementGroup;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.notification.Notification;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.notification.NotificationType;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.mocks.iec60870.Iec60870MockServer;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.support.ws.distributionautomation.DistributionAutomationDeviceManagementClient;
import org.opensmartgridplatform.iec60870.Iec60870AsduHandler;
import org.opensmartgridplatform.iec60870.Iec60870InformationObjectType;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduFactory;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.profile.DefaultControlledStationAsduFactory;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.profile.LightMeasurementDeviceAsduFactory;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers.Iec60870InterrogationCommandAsduHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

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
        this.mockServer.addIec60870ASduHandler(ASduType.C_IC_NA_1, this.getInterrogationCommandASduHandler());

        final GetHealthStatusRequest request = new GetHealthStatusRequest();
        request.setDeviceIdentification(deviceIdentification);

        final GetHealthStatusAsyncResponse response = this.client.getHealthStatus(organizationIdentification, request);
        LOGGER.debug("CorrelationUid received in the response for the getHealthStatusRequest: {}",
                response.getAsyncResponse().getCorrelationUid());
    }

    private Iec60870AsduHandler getInterrogationCommandASduHandler() {
        final Iec60870AsduFactory iec60870AsduFactory = this.getAsduFactory();

        iec60870AsduFactory.setIec60870Server(this.mockServer.getRtuSimulator());

        return new Iec60870InterrogationCommandAsduHandler(iec60870AsduFactory);
    }

    private Iec60870AsduFactory getAsduFactory() {
        final String profile = (String) ScenarioContext.current().get(PROFILE);
        if ("light_measurement_device".equals(profile)) {
            return new LightMeasurementDeviceAsduFactory();
        }
        return new DefaultControlledStationAsduFactory();
    }

    @Then("^I receive a measurement report for device (.+)$")
    public void iReceiveAMeasurementReportForDevice(final String deviceIdentification)
            throws WebServiceSecurityException {
        this.checkReceivingMeasurementReport(deviceIdentification);
    }

    @Then("^I get a measurement report for device (.+) with values$")
    public void iGetAMeasurementReportForDeviceWithValues(final String deviceIdentification,
            final Map<String, String> reportValues) throws WebServiceSecurityException {

        final GetMeasurementReportResponse measurementReportResponse = this
                .checkReceivingMeasurementReport(deviceIdentification);

        this.checkMeasurementReportValues(reportValues, measurementReportResponse);

    }

    private GetMeasurementReportResponse checkReceivingMeasurementReport(final String deviceIdentification)
            throws WebServiceSecurityException {
        Notification notification = null;
        boolean found = false;
        do {
            notification = this.client.waitForNotification();

            found = deviceIdentification.equals(notification.getDeviceIdentification())
                    && NotificationType.GET_MEASUREMENT_REPORT == notification.getNotificationType();
        } while (!found);

        final GetMeasurementReportResponse measurementReportResponse = this.client
                .getMeasurementReportResponse(notification);

        assertThat(measurementReportResponse).isNotNull();
        return measurementReportResponse;
    }

    private void checkMeasurementReportValues(final Map<String, String> reportValues,
            final GetMeasurementReportResponse measurementReportResponse) {
        final MeasurementGroup measurementGroup = measurementReportResponse.getMeasurementReport()
                .getMeasurementGroups()
                .getMeasurementGroupList()
                .get(0);
        final String expectedAddress = reportValues.get(INFORMATION_OBJECT_ADDRESS);
        assertThat(measurementGroup.getIdentification()).isEqualTo(expectedAddress);

        this.checkInformationElementValue(reportValues, measurementGroup);

    }

    private void checkInformationElementValue(final Map<String, String> reportValues,
            final MeasurementGroup measurementGroup) {
        final Iec60870InformationObjectType expectedType = Iec60870InformationObjectType
                .fromString(reportValues.get(INFORMATION_OBJECT_TYPE));
        final String expectedValue = reportValues.get(INFORMATION_ELEMENT_VALUE);
        final MeasurementElement measurementElement = measurementGroup.getMeasurements()
                .getMeasurementList()
                .get(0)
                .getMeasurementElements()
                .getMeasurementElementList()
                .get(0);
        if (Iec60870InformationObjectType.SHORT_FLOAT.equals(expectedType)) {
            final FloatMeasurementElement element = (FloatMeasurementElement) measurementElement;
            assertThat(element.getValue()).isEqualTo(Float.valueOf(expectedValue));
        }
        if (Iec60870InformationObjectType.SINGLE_POINT_INFORMATION_WITH_QUALITY.equals(expectedType)) {
            final BitmaskMeasurementElement element = (BitmaskMeasurementElement) measurementElement;
            final Boolean booleanValue = Boolean.valueOf(expectedValue);
            assertThat(element.getValue()).isEqualTo(booleanValue ? Byte.valueOf("1") : Byte.valueOf("0"));
        }
    }

}
