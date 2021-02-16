/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmonitoring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObject;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualValue;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.ActualPowerQualityRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringResponseClient;
import org.springframework.beans.factory.annotation.Autowired;

public class ActualPowerQualitySteps {

    @Autowired
    private SmartMeteringMonitoringRequestClient<ActualPowerQualityAsyncResponse, ActualPowerQualityRequest> requestClient;

    @Autowired
    private SmartMeteringMonitoringResponseClient<ActualPowerQualityResponse, ActualPowerQualityAsyncRequest> responseClient;

    @When("^the get actual power quality request is received$")
    public void theGetActualMeterReadsRequestIsReceived(final Map<String, String> settings) throws Throwable {
        final ActualPowerQualityRequest request = ActualPowerQualityRequestFactory.fromParameterMap(settings);

        final ActualPowerQualityAsyncResponse asyncResponse = this.requestClient.doRequest(request);

        assertThat(asyncResponse).as("AsyncResponse should not be null").isNotNull();
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    }

    @When("^the get actual power quality request generating an error is received$")
    public void theGetActualMeterReadsRequestGeneratingAnErrorIsReceived(final Map<String, String> settings)
            throws Throwable {
        final ActualPowerQualityRequest request = ActualPowerQualityRequestFactory.fromParameterMap(settings);
        final ActualPowerQualityAsyncResponse asyncResponse = this.requestClient.doRequest(request);

        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());

        final ActualPowerQualityAsyncRequest actualPowerQualityAsyncRequest = ActualPowerQualityRequestFactory
                .fromScenarioContext();

        try {
            final ActualPowerQualityResponse response = this.responseClient.getResponse(actualPowerQualityAsyncRequest);
            fail("Expected exception, but got a response: %s", response.toString());
        } catch (final Exception exception) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, exception);
        }
    }

    @Then("^the actual power quality result should be returned$")
    public void theActualPowerQualityResultShouldBeReturned(final Map<String, String> settings) throws Throwable {

        final ActualPowerQualityAsyncRequest asyncRequest = ActualPowerQualityRequestFactory.fromScenarioContext();

        final ActualPowerQualityResponse response = this.responseClient.getResponse(asyncRequest);
        assertThat(response).as("ActualPowerQualityResponseData should not be null").isNotNull();

        final int expectedNumberOfCaptureObjects = getInteger(settings, "NumberOfCaptureObjects", 0);
        final List<CaptureObject> actualCaptureObjects =
                response.getActualPowerQualityData().getCaptureObjects().getCaptureObjects();
        assertThat(actualCaptureObjects.size()).as("Number of capture objects")
                                               .isEqualTo(expectedNumberOfCaptureObjects);

        for (int i = 0; i < expectedNumberOfCaptureObjects; i++) {
            final CaptureObject actualCaptureObject = actualCaptureObjects.get(i);
            final Long expectedClassId = SettingsHelper.getLongValue(settings, "CaptureObject_ClassId", i+1);
            if (expectedClassId != null) {
                this.validateCaptureObject(actualCaptureObject, settings, i + 1);
            }
        }

        final int expectedNumberOfActualValues = getInteger(settings, "NumberOfActualValues", 0);
        final List<ActualValue> actualValues =
                response.getActualPowerQualityData().getActualValues().getActualValue();
        assertThat(actualValues.size()).as("Number of actual values")
                                               .isEqualTo(expectedNumberOfActualValues);

        if (expectedNumberOfActualValues > 0) {
            /*
             * Expected value equals expectedNumberOfCaptureObjects, because the
             * number of ActualValues should match the number of captured objects
             * from the buffer.
             */
            assertThat(actualValues.size()).as("Number of actual values").isEqualTo(expectedNumberOfCaptureObjects);
        }
    }

    private void validateCaptureObject(final CaptureObject actualCaptureObject, final Map<String, String> settings,
            final int index) {

        final Long expectedClassId = SettingsHelper.getLongValue(settings, "CaptureObject_ClassId", index);
        assertThat(Long.valueOf(actualCaptureObject.getClassId())).as("ClassId of CaptureObject " + index)
                                                                  .isEqualTo(expectedClassId);

        final String expectedLogicalName = SettingsHelper.getStringValue(settings, "CaptureObject_LogicalName", index);
        assertThat(actualCaptureObject.getLogicalName()).as("LogicalName of CaptureObject " + index)
                                                        .isEqualTo(expectedLogicalName);

        final BigInteger expectedAttributeIndex = SettingsHelper
                .getBigIntegerValue(settings, "CaptureObject_AttributeIndex", index);
        assertThat(actualCaptureObject.getAttributeIndex()).as("AttributeIndex of CaptureObject " + index)
                                                           .isEqualTo(expectedAttributeIndex);

        final Long expectedDataIndex = SettingsHelper.getLongValue(settings, "CaptureObject_DataIndex", index);
        assertThat(Long.valueOf(actualCaptureObject.getDataIndex())).as("DataIndex of CaptureObject " + index)
                                                                    .isEqualTo(expectedDataIndex);

        final String expectedUnit = SettingsHelper.getStringValue(settings, "CaptureObject_Unit", index);
        if (expectedUnit != null) {
            assertThat(actualCaptureObject.getUnit().value()).as("Unit of CaptureObject " + index).isEqualTo(expectedUnit);
        }
    }
}
