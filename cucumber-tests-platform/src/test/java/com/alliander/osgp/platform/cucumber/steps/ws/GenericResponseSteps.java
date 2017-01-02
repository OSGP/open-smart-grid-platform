/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Keys;

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
		SoapFaultClientException response = (SoapFaultClientException) ScenarioContext.Current().get(Keys.RESPONSE);
		
		Assert.assertEquals(getString(expectedResult, Keys.KEY_MESSAGE), response.getMessage());
		// TODO Check the rest of the soap fault.
	
	}
}
