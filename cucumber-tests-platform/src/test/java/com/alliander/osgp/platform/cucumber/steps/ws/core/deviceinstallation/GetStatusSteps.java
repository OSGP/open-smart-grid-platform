/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.core.deviceinstallation;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.core.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.GetStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.GetStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.GetStatusRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.GetStatusResponse;
import com.alliander.osgp.platform.cucumber.config.CoreDeviceConfiguration;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.support.ws.core.CoreDeviceInstallationClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GetStatusSteps {

    @Autowired
    private CoreDeviceConfiguration configuration;
    
    @Autowired
    private CoreDeviceInstallationClient client;

    @When("receiving a device installation get status request")
    public void receivingADeviceInstallationGetStatusRequest(final Map<String, String> settings) throws Throwable {
        final GetStatusRequest request = new GetStatusRequest();
        
        request.setDeviceIdentification(getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.getStatus(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }
    /**
    *
    * @param expectedResponseData
    * @throws Throwable
    */
   @Then("the device installation get status async response contains")
   public void theDeviceInstallationGetStatusAsyncResponseContains(final Map<String, String> expectedResponseData)
           throws Throwable {
       final GetStatusAsyncResponse response = (GetStatusAsyncResponse) ScenarioContext.Current()
               .get(Keys.RESPONSE);

       Assert.assertNotNull(response.getAsyncResponse().getCorrelationUid());
       Assert.assertEquals(getString(expectedResponseData, Keys.KEY_DEVICE_IDENTIFICATION),
               response.getAsyncResponse().getDeviceId());

       // Save the returned CorrelationUid in the Scenario related context for
       // further use.
       saveCorrelationUidInScenarioContext(response.getAsyncResponse().getCorrelationUid(),
               getString(expectedResponseData, Keys.KEY_ORGANIZATION_IDENTIFICATION,
                       Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
   }

   /**
    *
    * @param deviceIdentification
    * @throws Throwable
    */
   @Then("the platform buffers a device installation get status response message for device \"([^\"]*)\"")
   public void thePlatformBuffersADeviceInstallationGetStatusResponseMessageForDevice(final String deviceIdentification, final Map<String, String> expectedResult) throws Throwable
   {
       GetStatusAsyncRequest request = new GetStatusAsyncRequest();
       AsyncRequest asyncRequest = new AsyncRequest();
       asyncRequest.setDeviceId(deviceIdentification);
       asyncRequest.setCorrelationUid((String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
       request.setAsyncRequest(asyncRequest);
       
       boolean success = false;
       int count = 0;
       while (!success) {
           if (count > configuration.getTimeout()) {
               Assert.fail("Timeout");
           }
           
           count++;
           Thread.sleep(1000);

           try {
               GetStatusResponse response = client.getStatusResponse(request);
               
               Assert.assertEquals(Enum.valueOf(OsgpResultType.class, expectedResult.get(Keys.KEY_RESULT)), response.getResult());
               
               success = true; 
           }
           catch(Exception ex) {
               // Do nothing
           }
       }
   }
}
