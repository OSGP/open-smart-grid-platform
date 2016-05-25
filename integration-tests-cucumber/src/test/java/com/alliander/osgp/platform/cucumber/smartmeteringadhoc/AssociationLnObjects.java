package com.alliander.osgp.platform.cucumber.smartmeteringadhoc;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.SuperCucumber;
import com.alliander.osgp.platform.cucumber.support.DeviceId;
import com.alliander.osgp.platform.cucumber.support.OrganisationId;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AssociationLnObjects extends SuperCucumber {
    private String response;
    private String correlationUid;

    private static final String PATH_RESULT = "/Envelope/Body/GetAssociationLnObjectsResponse/Result/text()";
    private static final String PATH_RESULT_CLASSID = "/Envelope/Body/GetAssociationLnObjectsResponse/AssociationLnList/AssociationLnListElement/ClassId/text()";
    private static final String PATH_RESULT_VERSION = "/Envelope/Body/GetAssociationLnObjectsResponse/AssociationLnList/AssociationLnListElement/Version/text()";
    private static final String PATH_RESULT_LOGICALNAME = "/Envelope/Body/GetAssociationLnObjectsResponse/AssociationLnList/AssociationLnListElement/LogicalName/text()";
    private static final String PATH_RESULT_ATTRIBUTE_ID = "/Envelope/Body/GetAssociationLnObjectsResponse/AssociationLnList/AssociationLnListElement/AccessRights/AttributeAccess/AttributeAccessItem/AttributeId/text()";
    private static final String PATH_RESULT_ACCESS_MODE = "/Envelope/Body/GetAssociationLnObjectsResponse/AssociationLnList/AssociationLnListElement/AccessRights/AttributeAccess/AttributeAccessItem/AccessMode/text()";

    private static final String XPATH_MATCHER_RESULT = "OK";
    private static final String XPATH_MATCHER_RESULT_DECIMAL = "\\d+";
    private static final String XPATH_MATCHER_RESULT_ACCESS_MODE = "\\w+\\_\\w+";

    private static final String TEST_SUITE_XML = "SmartmeterAdhoc";
    private static final String TEST_CASE_XML = "505 Store association LN objectlist";
    private static final String TEST_CASE_NAME_REQUEST = "GetAssociationLnObjects - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetGetAssociationLnObjectsResponse - Request 1";

    private static final Logger LOGGER = LoggerFactory.getLogger(AssociationLnObjects.class);
    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    @Autowired
    private DeviceId deviceId;

    @Autowired
    private OrganisationId organisationId;

    @When("^the retrieve association LN objectlist request is received$")
    public void theRetrieveAssociationLNObjectlistRequestIsReceived() throws Throwable {
        PROPERTIES_MAP.put(DEVICE_IDENTIFICATION_E, this.deviceId.getDeviceIdE());
        PROPERTIES_MAP.put(ORGANISATION_IDENTIFICATION, this.organisationId.getOrganisationId());

        this.correlationUid = this.RequestRunner(PROPERTIES_MAP, TEST_CASE_NAME_REQUEST, TEST_CASE_XML, TEST_SUITE_XML);
    }

    @Then("^the objectlist should be returned$")
    public void theObjectlistShouldBeReturned() throws Throwable {
        PROPERTIES_MAP.put(CORRELATION_UID, this.correlationUid);

        this.response = this.ResponseRunner(PROPERTIES_MAP, TEST_CASE_NAME_RESPONSE, LOGGER);

        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT, XPATH_MATCHER_RESULT));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_CLASSID, XPATH_MATCHER_RESULT_DECIMAL));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_VERSION, XPATH_MATCHER_RESULT_DECIMAL));
        assertTrue(this.runXpathResult
                .assertXpath(this.response, PATH_RESULT_LOGICALNAME, XPATH_MATCHER_RESULT_DECIMAL));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_ATTRIBUTE_ID,
                XPATH_MATCHER_RESULT_DECIMAL));
        assertTrue(this.runXpathResult.assertXpath(this.response, PATH_RESULT_ACCESS_MODE,
                XPATH_MATCHER_RESULT_ACCESS_MODE));
    }

    @And("^the objeclist should be stored in the integration layer database$")
    public void theObjeclistShouldBeStoredInTheIntegrationLayerDatabase() throws Throwable {

    }
}
