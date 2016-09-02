/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.mocks;

import junit.framework.Assert;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.mocks.oslpdevice.OSLPDeviceMock;

import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class which holds all the OSLP device mock steps in order to let the device mock behave correctly for the automatic test.
 */
public class OSLPDeviceSteps {
	
	@Autowired
	private OSLPDeviceMock oslpDeviceMock;
	
	@When("^the device returns firmware version \"([^\"]*)\" over OSLP$")
	public void the_device_returns_firmware_version_over_OSLP(String arg1) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    throw new PendingException();
	}
	
	/**
	 * Verify that a get firmware version OSLP message is sent to the device.
	 * @param deviceIdentification The device identification expected in the message to the device.
	 * @throws Throwable
	 */
	@Then("^a get firmware version OSLP message is sent to device \"([^\"]*)\"$")
	public void a_get_firmware_version_OSLP_message_is_sent_to_device(String deviceIdentification) throws Throwable {	    
	    // TODO: Wait until the mock service received the get firmware version oslp message from the platform.
	    // TODO: Make this more generic maybe.
	    String message = oslpDeviceMock.WaitForGetFirmwareVersionMessage();
	    
	    Assert.assertEquals(deviceIdentification, message);
	}
	
	@Then("^a get firmware version OSLP response message is received$")
	public void a_get_firmware_version_OSLP_response_message_is_received(DataTable arg1) throws Throwable {
	    // Write code here that turns the phrase above into concrete actions
	    // For automatic transformation, change DataTable to one of
	    // List<YourType>, List<List<E>>, List<Map<K,V>> or Map<K,V>.
	    // E,K,V must be a scalar (String, Integer, Date, enum etc)
	    throw new PendingException();
	}
}
