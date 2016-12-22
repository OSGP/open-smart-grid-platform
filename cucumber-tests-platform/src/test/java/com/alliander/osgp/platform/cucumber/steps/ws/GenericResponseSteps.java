package com.alliander.osgp.platform.cucumber.steps.ws;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Keys;

public class GenericResponseSteps {
	public static void VerifySoapFault(final Map<String, String> expectedResult) {
		SoapFaultClientException response = (SoapFaultClientException) ScenarioContext.Current().get(Keys.RESPONSE);
		
		Assert.assertEquals(getString(expectedResult, Keys.KEY_MESSAGE), response.getMessage());
		// TODO Check the rest of the details.
	
	}
}
