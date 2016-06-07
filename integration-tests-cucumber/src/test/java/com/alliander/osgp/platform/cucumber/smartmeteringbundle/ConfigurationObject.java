/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.platform.cucumber.smartmeteringbundle;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.SmartMetering;
import com.alliander.osgp.platform.cucumber.support.DeviceId;
import com.alliander.osgp.platform.cucumber.support.OrganisationId;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ConfigurationObject extends SmartMetering {

    private static final String TEST_SUITE_XML = "SmartmeterAdhoc";
    private static final String TEST_CASE_XML_501 = "501 Retrieve specific configuration object bundle";
    private static final String TEST_CASE_XML_526 = "526 Retrieve association objectlist bundle";

    private static final String TEST_CASE_NAME_REQUEST = "Bundle - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetBundleResponse - Request 1";

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationObject.class);
    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    @Autowired
    private DeviceId deviceId;

    @Autowired
    private OrganisationId organisationId;

    @When("^a retrieve configuration request for OBIS code (\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+) is received as part of a bundled request$")
    public void aRetrieveConfigurationRequestForOBISCodeIsReceivedAsPartOfABundledRequest(final int obisCodeA,
            final int obisCodeB, final int obisCodeC, final int obisCodeD, final int obisCodeE, final int obisCodeF)
            throws Throwable {
        this.setDeviceAndOrganisationProperties();
        PROPERTIES_MAP.put("ObisCodeA", Integer.toString(obisCodeA));
        PROPERTIES_MAP.put("ObisCodeB", Integer.toString(obisCodeB));
        PROPERTIES_MAP.put("ObisCodeC", Integer.toString(obisCodeC));
        PROPERTIES_MAP.put("ObisCodeD", Integer.toString(obisCodeD));
        PROPERTIES_MAP.put("ObisCodeE", Integer.toString(obisCodeE));
        PROPERTIES_MAP.put("ObisCodeF", Integer.toString(obisCodeF));

        this.RequestRunner(PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML_501, TEST_SUITE_XML);
    }

    private void setDeviceAndOrganisationProperties() {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E_LABEL, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION_LABEL, this.organisationId.getOrganisationId());
    }

    @When("^the get associationLnObjects request is received as part of a bundled request$")
    public void theGetAssociationLnObjectsRequestIsReceivedAsPartOfABundledRequest() throws Throwable {
        this.setDeviceAndOrganisationProperties();
        this.RequestRunner(PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML_526, TEST_SUITE_XML);
    }

    @Then("^\"([^\"]*)\" is part of the response$")
    public void isPartOfTheResponse(final String responsePart) throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID_LABEL, this.correlationUid);
        this.ResponseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);
        assertTrue(this.response.contains(responsePart));
    }

}
