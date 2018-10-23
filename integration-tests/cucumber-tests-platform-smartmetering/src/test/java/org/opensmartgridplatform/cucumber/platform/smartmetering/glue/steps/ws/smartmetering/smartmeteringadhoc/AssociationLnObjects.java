/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringadhoc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.AssociationLnListElement;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAssociationLnObjectsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAssociationLnObjectsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAssociationLnObjectsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetAssociationLnObjectsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.AssociationLnObjectsRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AssociationLnObjects {

    @Autowired
    SmartMeteringAdHocRequestClient<GetAssociationLnObjectsAsyncResponse, GetAssociationLnObjectsRequest> requestClient;

    @Autowired
    SmartMeteringAdHocResponseClient<GetAssociationLnObjectsResponse, GetAssociationLnObjectsAsyncRequest> responseClient;

    @When("^receiving a retrieve association LN objectlist request$")
    public void receivingARetrieveAssociationLNObjectlistRequest(final Map<String, String> settings) throws Throwable {

        final GetAssociationLnObjectsRequest request = AssociationLnObjectsRequestFactory.fromParameterMap(settings);
        final GetAssociationLnObjectsAsyncResponse asyncResponse = this.requestClient.doRequest(request);

        assertNotNull("AsyncRespone should not be null", asyncResponse);
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    }

    @Then("^the objectlist should be returned$")
    public void theObjectlistShouldBeReturned(final Map<String, String> settings) throws Throwable {

        final GetAssociationLnObjectsAsyncRequest asyncRequest = AssociationLnObjectsRequestFactory
                .fromScenarioContext();
        final GetAssociationLnObjectsResponse response = this.responseClient.getResponse(asyncRequest);

        assertEquals("Response should be OK", OsgpResultType.OK, response.getResult());
        assertNotNull("Response should contain an AssociationLnList", response.getAssociationLnList());
        assertNotNull("AssociationLnList shoudl have at least one entry",
                response.getAssociationLnList().getAssociationLnListElement().get(0));

        final AssociationLnListElement element = response.getAssociationLnList().getAssociationLnListElement().get(0);
        assertNotNull("AccessRights should be present", element.getAccessRights());
        assertNotNull("ClassId should be present", element.getClassId());
        assertNotNull("LogicalName should be present", element.getLogicalName());
        assertNotNull("Version should be present", element.getVersion());
        assertNotNull("AttributeId should be present",
                element.getAccessRights().getAttributeAccess().getAttributeAccessItem().get(0).getAttributeId());

    }
}
