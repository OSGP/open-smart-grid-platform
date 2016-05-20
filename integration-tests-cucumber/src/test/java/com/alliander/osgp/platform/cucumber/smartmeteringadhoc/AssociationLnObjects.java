package com.alliander.osgp.platform.cucumber.smartmeteringadhoc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.support.DeviceId;
import com.alliander.osgp.platform.cucumber.support.OrganisationId;
import com.alliander.osgp.platform.cucumber.support.RunXpathResult;
import com.alliander.osgp.platform.cucumber.support.TestCaseResult;
import com.alliander.osgp.platform.cucumber.support.TestCaseRunner;
import com.alliander.osgp.platform.cucumber.support.WsdlProjectFactory;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner;
import com.eviware.soapui.model.iface.MessageExchange;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AssociationLnObjects {
    private TestCase testCase;
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

    private static final String SOAP_PROJECT_XML = "../cucumber/soap-ui-project/FLEX-OVL-V3---SmartMetering-soapui-project.xml";
    private static final String TEST_SUITE_XML = "SmartmeterAdhoc";
    private static final String TEST_CASE_XML = "505 Store association LN objectlist";
    private static final String TEST_CASE_NAME_REQUEST = "GetAssociationLnObjects - Request 1";
    private static final String TEST_CASE_NAME_RESPONSE = "GetGetAssociationLnObjectsResponse - Request 1";

    private static final Logger LOGGER = LoggerFactory.getLogger(AssociationLnObjects.class);
    private static final Map<String, String> PROPERTIES_MAP = new HashMap<>();

    private Pattern correlationUidPattern;
    private Matcher correlationUidMatcher;

    @Autowired
    private WsdlProjectFactory wsdlProjectFactory;

    @Autowired
    private TestCaseRunner testCaseRunner;

    @Autowired
    private RunXpathResult runXpathResult;

    @Autowired
    private DeviceId deviceId;

    @Autowired
    private OrganisationId organisationId;

    @When("^the retrieve association LN objectlist request is received$")
    public void theRetrieveAssociationLNObjectlistRequestIsReceived() throws Throwable {
        this.correlationUidPattern = Pattern.compile(this.organisationId.getOrganisationId()
                + "\\|\\|\\|\\S{17}\\|\\|\\|\\S{17}");
        this.testCase = this.wsdlProjectFactory.createWsdlTestCase(SOAP_PROJECT_XML, TEST_SUITE_XML, TEST_CASE_XML);

        PROPERTIES_MAP.put("DeviceIdentificationE", this.deviceId.getDeviceId());
        PROPERTIES_MAP.put("OrganisationIdentification", this.organisationId.getOrganisationId());

        final TestCaseResult runTestStepByName = this.testCaseRunner.runWsdlTestCase(this.testCase, PROPERTIES_MAP,
                TEST_CASE_NAME_REQUEST);

        final TestStepResult runTestStepByNameResult = runTestStepByName.getRunTestStepByName();
        final WsdlTestCaseRunner wsdlTestCaseRunner = runTestStepByName.getResults();
        assertEquals(TestStepStatus.OK, runTestStepByNameResult.getStatus());

        for (final TestStepResult tcr : wsdlTestCaseRunner.getResults()) {
            this.response = ((MessageExchange) tcr).getResponseContent();
            this.correlationUidMatcher = this.correlationUidPattern.matcher(this.response);
        }
        assertTrue(this.correlationUidMatcher.find());
        this.correlationUid = this.correlationUidMatcher.group();
    }

    @Then("^the objectlist should be returned$")
    public void theObjectlistShouldBeReturned() throws Throwable {
        PROPERTIES_MAP.put("CorrelationUid", this.correlationUid);

        final TestCaseResult runTestStepByName = this.testCaseRunner.runWsdlTestCase(this.testCase, PROPERTIES_MAP,
                TEST_CASE_NAME_RESPONSE);
        final TestStepResult runTestStepByNameResult = runTestStepByName.getRunTestStepByName();
        final WsdlTestCaseRunner wsdlTestCaseRunner = runTestStepByName.getResults();
        assertEquals(TestStepStatus.OK, runTestStepByNameResult.getStatus());

        for (final TestStepResult tcr : wsdlTestCaseRunner.getResults()) {
            LOGGER.info(TEST_CASE_NAME_RESPONSE + " response {}",
                    this.response = ((MessageExchange) tcr).getResponseContent());
        }

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
