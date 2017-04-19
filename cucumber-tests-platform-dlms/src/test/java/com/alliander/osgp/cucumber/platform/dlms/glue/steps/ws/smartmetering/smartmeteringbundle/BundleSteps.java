package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.Actions;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetProfileGenericDataRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.CaptureObject;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.ProfileEntry;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericData;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataResponseData;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.dlms.Keys;
import com.alliander.osgp.cucumber.platform.dlms.builders.BundleRequestBuilder;
import com.alliander.osgp.cucumber.platform.dlms.builders.GetProfileGenericDataRequestBuilder;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.bundle.SmartMeteringBundleClient;
import com.alliander.osgp.cucumber.platform.helpers.SettingsHelper;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class BundleSteps {
    @Autowired
    private SmartMeteringBundleClient client;

    @When("^a get profile generic data request is received as part of a bundled request$")
    public void whenAGetProfileGenericDataBundleRequestIsReceived(final Map<String, String> settings) throws Throwable {

        final GetProfileGenericDataRequest action = new GetProfileGenericDataRequestBuilder().fromParameterMap(settings)
                .build();

        final Actions actions = new Actions();
        actions.getActionList().add(action);

        final BundleRequest request = new BundleRequestBuilder()
                .withDeviceIdentification(settings.get(Keys.DEVICE_IDENTIFICATION)).withActions(actions).build();
        final BundleAsyncResponse response = this.client.sendBundleRequest(request);

        ScenarioContext.Current().put(Keys.CORRELATION_UID, response.getCorrelationUid());
        ScenarioContext.Current().put(Keys.DEVICE_IDENTIFICATION, response.getDeviceIdentification());
    }

    @Then("^the profile generic data should be part of the bundle response$")
    public void thenTheProfileGenericDataShouldBePartOfTheBundleResponse(final Map<String, String> settings)
            throws Throwable {

        final BundleAsyncRequest request = new BundleAsyncRequest();
        request.setCorrelationUid((String) ScenarioContext.Current().get(Keys.CORRELATION_UID));
        request.setDeviceIdentification((String) ScenarioContext.Current().get(Keys.DEVICE_IDENTIFICATION));

        final BundleResponse bundleResponse = this.client.retrieveBundleResponse(request);
        final ProfileGenericDataResponseData profileGenericDataResponseData = (ProfileGenericDataResponseData) bundleResponse
                .getAllResponses().getResponseList().get(0);
        final ProfileGenericData profileGenericData = profileGenericDataResponseData.getProfileGenericData();

        this.assertEqualCaptureObjects(profileGenericData.getCaptureObjectList().getCaptureObjects(), settings);
        this.assertEqualProfileEntries(profileGenericData.getProfileEntryList().getProfileEntries(), settings);
    }

    private void assertEqualCaptureObjects(final List<CaptureObject> actualCaptureObjects,
            final Map<String, String> expectedValues) throws AssertionError {

        final int expectedNumberOfCaptureObjects = SettingsHelper.getIntegerValue(expectedValues,
                "NumberOfCaptureObjects");

        assertEquals("Number of capture objects", expectedNumberOfCaptureObjects, actualCaptureObjects.size());

        for (int i = 0; i < expectedNumberOfCaptureObjects; i++) {
            final CaptureObject actualCaptureObject = actualCaptureObjects.get(i);
            this.assertEqualCaptureObject(actualCaptureObject, expectedValues, i + 1);
        }
    }

    private void assertEqualCaptureObject(final CaptureObject actualCaptureObject,
            final Map<String, String> expectedValues, final int index) throws AssertionError {
        final Long expectedClassId = SettingsHelper.getLongValue(expectedValues, "CaptureObject_ClassId", index);
        assertEquals("ClassId of CaptureObject " + index, expectedClassId,
                Long.valueOf(actualCaptureObject.getClassId()));

        final String expectedLogicalName = SettingsHelper.getStringValue(expectedValues, "CaptureObject_LogicalName",
                index);
        assertEquals("LogicalName of CaptureObject " + index, expectedLogicalName,
                actualCaptureObject.getLogicalName());

        final BigInteger expectedAttributeIndex = SettingsHelper.getBigIntegerValue(expectedValues,
                "CaptureObject_AttributeIndex", index);
        assertEquals("AttributeIndex of CaptureObject " + index, expectedAttributeIndex,
                actualCaptureObject.getAttributeIndex());

        final Long expectedDataIndex = SettingsHelper.getLongValue(expectedValues, "CaptureObject_DataIndex", index);
        assertEquals("DataIndex of CaptureObject " + index, expectedDataIndex,
                Long.valueOf(actualCaptureObject.getDataIndex()));

        final String expectedUnit = SettingsHelper.getStringValue(expectedValues, "CaptureObject_Unit", index);
        assertEquals("Unit of CaptureObject " + index, expectedUnit, actualCaptureObject.getUnit().value());
    }

    private void assertEqualProfileEntries(final List<ProfileEntry> actualProfileEntries,
            final Map<String, String> expectedValues) {
        final int expectedNumberOfProfileEntries = SettingsHelper.getIntegerValue(expectedValues,
                "NumberOfProfileEntries");

        assertEquals("Number of profile entries", expectedNumberOfProfileEntries, actualProfileEntries.size());
    }
}
