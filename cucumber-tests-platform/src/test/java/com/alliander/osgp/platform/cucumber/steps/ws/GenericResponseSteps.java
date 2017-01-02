/**
 * Copyright 2012-2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Assert;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.support.ws.FaultDetailElement;
import com.alliander.osgp.platform.cucumber.support.ws.SoapFaultHelper;

/**
 * Class with generic web service response steps.
 */
public class GenericResponseSteps {
	
	/**
	 * Verify the soap fault in the ScenarioContext.Current().get(Keys.RESPONSE)
	 * 
	 * @param expectedResult The list with expected result.
	 */
	public static void verifySoapFault(final Map<String, String> expectedResult){
		SoapFaultClientException soapFault = (SoapFaultClientException) ScenarioContext.Current().get(Keys.RESPONSE);
		
		Assert.assertEquals(getString(expectedResult, Keys.KEY_MESSAGE), soapFault.getMessage());
		
		final Map<FaultDetailElement, String> faultDetailValuesByElement = SoapFaultHelper
                .getFaultDetailValuesByElement(soapFault);

        assertFaultDetails(expectedResult, faultDetailValuesByElement);	
	}

    private static void assertFaultDetails(final Map<String, String> expected, final Map<FaultDetailElement, String> actual) {

        for (final Map.Entry<String, String> expectedEntry : expected.entrySet()) {
            final String localName = expectedEntry.getKey();
            final FaultDetailElement faultDetailElement = FaultDetailElement.forLocalName(localName);
            if (faultDetailElement == null) {
                /*
                 * Specified response parameter is not a FaultDetailElement
                 * (e.g. DeviceIdentification), skip it for the assertions.
                 */
                continue;
            }
            final String expectedValue = expectedEntry.getValue();
            final String actualValue = actual.get(faultDetailElement);
            assertEquals(localName, expectedValue, actualValue);
        }
    }
}
