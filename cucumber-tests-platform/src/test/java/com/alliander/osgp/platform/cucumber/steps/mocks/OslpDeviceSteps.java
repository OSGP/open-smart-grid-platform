/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.mocks;

import java.util.Map;

import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alliander.osgp.domain.core.valueobjects.RelayType;
import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.oslp.Oslp.LightType;
import com.alliander.osgp.oslp.Oslp.LinkType;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.Oslp.Status;
import com.alliander.osgp.platform.cucumber.mocks.oslpdevice.MockOslpServer;
import com.alliander.osgp.platform.cucumber.steps.ws.core.devicemanagement.SetEventNotificationsSteps;
import com.google.protobuf.ByteString;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * Class which holds all the OSLP device mock steps in order to let the device
 * mock behave correctly for the automatic test.
 */
public class OslpDeviceSteps {

    @Autowired
    private MockOslpServer oslpMockServer;

    /**
     * Setup method to set the firmware which should be returned by the mock.
     * @param firmwareVersion The firmware to respond.
     * @throws Throwable
     */
    @Given("^the device returns a set light response \"([^\"]*)\" over OSLP$")
    public void the_device_returns_a_set_light_over_OSLP(final String result) throws Throwable {
    	Oslp.Status oslpStatus = Status.OK;
    	
    	switch (result){
    	case "OK":
    		oslpStatus = Status.OK;
    		// TODO: Implement other possible status
    	}
    	
        this.oslpMockServer.mockSetLightResponse(oslpStatus);
    }

    /**
     * Setup method to set the firmware which should be returned by the mock.
     * @param firmwareVersion The firmware to respond.
     * @throws Throwable
     */
    @Given("^the device returns firmware version \"([^\"]*)\" over OSLP$")
    public void the_device_returns_firmware_version_over_OSLP(final String firmwareVersion) throws Throwable {
        this.oslpMockServer.mockFirmwareResponse(firmwareVersion);
    }
    
    /**
     * Setup method to set the event notification which should be returned by the mock.
     * @param firmwareVersion The event notification to respond.
     * @throws Throwable
     */
    
    @Given("^the device returns a set event notification \"([^\"]*)\" over OSLP$")
    public void the_device_returns_an_event_notification_over_OSLP(final String result) throws Throwable {
		Oslp.Status oslpStatus = Status.OK;
    	
    	switch (result){
    	case "OK":
    		oslpStatus = Status.OK;
    		// TODO: Implement other possible status
    	}
    	
        this.oslpMockServer.mockSetEventNotificationResponse(oslpStatus);
    }
    
    /**
     * Setup method to start a device which should be returned by the mock.
     * @param result The start device to respond.
     * @throws Throwable
     */    
    @Given("^the device returns a start device response \"([^\"]*)\" over OSLP$")
    public void the_device_returns_a_start_device_response_over_OSLP(final String result) throws Throwable
    {
		Oslp.Status oslpStatus = Status.OK;
    	
    	switch (result){
    	case "OK":
    		oslpStatus = Status.OK;
    		// TODO: Implement other possible status
    	}
    	// TODO: Make Mock method
        this.oslpMockServer.mockStartDeviceResponse(oslpStatus);
    }
    
    /**
     * Setup method to stop a device which should be returned by the mock.
     * @param result The stop device to respond.
     * @throws Throwable
     */    
    @Given("^the device returns a stop device response \"([^\"]*)\" over OSLP$")
    public void the_device_returns_a_stop_device_response_over_OSLP(final String result) throws Throwable
    {
		Oslp.Status oslpStatus = Status.OK;
    	
    	switch (result){
    	case "OK":
    		oslpStatus = Status.OK;
    		// TODO: Implement other possible status
    	}
    	// TODO: Check if ByteString.EMPTY must be something else
        this.oslpMockServer.mockStopDeviceResponse(ByteString.EMPTY, oslpStatus);
    }
    
    /**
     * Setup method to get a status which should be returned by the mock.
     * @param result The get status to respond.
     * @throws Throwable
     */    
    @Given("^the device returns a get status response over OSLP$")
    public void the_device_returns_a_get_status_response_over_OSLP(final Map<String, String> result) throws Throwable
    {
		final String[] results = result.get("Result").split(";");
		
		int internalId = 0, externalId = 0;
		RelayType relayType = null;
		
		String[] items = null;
		for (int i = 0; i < results.length; i++)
		{
			items = results[i].split(",");
			internalId = Integer.parseInt(items[0]);
			externalId = Integer.parseInt(items[1]);
			
            relayType = (items[2].equals("LIGHT")) ? RelayType.LIGHT : 
            			(items[2].equals("TARIFF")) ? RelayType.TARIFF : 
            			(items[2].equals("TARIFF_REVERSED")) ? RelayType.TARIFF_REVERSED : null;
		}

    	// TODO: Check which status is needed and check if the other values need to be set
        this.oslpMockServer.mockGetStatusResponse(LinkType.LINK_NOT_SET, LinkType.LINK_NOT_SET, LightType.LT_NOT_SET, 0, Status.OK);
    }
    
    /**
     * Setup method to get a status which should be returned by the mock.
     * @param result The get status to respond.
     * @throws Throwable
     */    
    @Given("^the device returns a get status response \"([^\"]*)\" over OSLP$")
    public void the_device_returns_a_get_status_response_over_OSLP(final String result) throws Throwable
    {
		Oslp.Status oslpStatus = Status.OK;
    	
    	switch (result){
    	case "OK":
    		oslpStatus = Status.OK;
    		// TODO: Implement other possible status
    	}

    	// TODO: Check which status is needed and check if the other values need to be set
        this.oslpMockServer.mockGetStatusResponse(LinkType.LINK_NOT_SET, LinkType.LINK_NOT_SET, LightType.LT_NOT_SET, 0, oslpStatus);
    }
    
    /**
     * Setup method to resume a schedule which should be returned by the mock.
     * @param result The resume schedule to respond.
     * @throws Throwable
     */    
    @Given("^the device returns a resume schedule response \"([^\"]*)\" over OSLP$")
    public void the_device_returns_a_resume_schedule_response_over_OSLP(final String result) throws Throwable
    {
		Oslp.Status oslpStatus = Status.OK;
    	
    	switch (result){
    	case "OK":
    		oslpStatus = Status.OK;
    		// TODO: Implement other possible status
    	}
    	//
        this.oslpMockServer.mockResumeScheduleResponse(oslpStatus);
    }
    
    /**
     * Setup method to set a reboot which should be returned by the mock.
     * @param result The stop device to respond.
     * @throws Throwable
     */    
    @Given("^the device returns a set reboot response \"([^\"]*)\" over OSLP$")
    public void the_device_returns_a_set_reboot_response_over_OSLP(final String result) throws Throwable
    {
		Oslp.Status oslpStatus = Status.OK;
    	
    	switch (result){
    	case "OK":
    		oslpStatus = Status.OK;
    		// TODO: Implement other possible status
    	}

        this.oslpMockServer.mockSetRebootResponse(oslpStatus);
    }
    
    /**
     * Setup method to set a transition which should be returned by the mock.
     * @param result The stop device to respond.
     * @throws Throwable
     */    
    @Given("^the device returns a set transition response \"([^\"]*)\" over OSLP$")
    public void the_device_returns_a_set_transition_response_over_OSLP(final String result) throws Throwable
    {
		Oslp.Status oslpStatus = Status.OK;
    	
    	switch (result){
    	case "OK":
    		oslpStatus = Status.OK;
    		// TODO: Implement other possible status
    	}

        this.oslpMockServer.mockSetTransitionResponse(oslpStatus);
    }
    
    /**
     * Verify that a get firmware version OSLP message is sent to the device.
     *
     * @param deviceIdentification
     *            The device identification expected in the message to the
     *            device.
     * @throws Throwable
     */
    @Then("^a get firmware version OSLP message is sent to device \"([^\"]*)\"$")
    public void a_get_firmware_version_OSLP_message_is_sent_to_device(final String deviceIdentification)
            throws Throwable {
        final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.GET_FIRMWARE_VERSION);
        Assert.notNull(message);
        Assert.isTrue(message.hasGetFirmwareVersionRequest());
        // TODO: Check actual message for the correct firmware(s).
    }

    /**
     * Verify that a set light OSLP message is sent to the device.
     *
     * @param deviceIdentification
     *            The device identification expected in the message to the
     *            device.
     * @throws Throwable
     */
    @Then("^a set light OSLP message is sent to device \"([^\"]*)\"$")
    public void a_get_set_light_OSLP_message_is_sent_to_device(final String deviceIdentification)
            throws Throwable {
        final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.SET_LIGHT);
        Assert.notNull(message);
        Assert.isTrue(message.hasSetLightRequest());
    }
    
    /**
     * Verify that a event notification OSLP message is sent to the device.
     * 
     * @param deviceIdentification
     *            The device identification expected in the message to the device.
     * @throws Throwable
     */
    @Then("^a set event notification OSLP message is sent to device \"([^\"]*)\"")
    public void a_set_event_notification_oslp_message_is_sent_to_device(final String deviceIdentification) throws Throwable
    {
    	LoggerFactory.getLogger(SetEventNotificationsSteps.class).info("A set event notification OSLP message is sent to device");
    	final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.SET_EVENT_NOTIFICATIONS);
        Assert.notNull(message);
        Assert.isTrue(message.hasSetEventNotificationsRequest());
    }
    
    /**
     * Verify that a start device OSLP message is sent to the device.
     * 
     * @param deviceIdentification
     *            The device identification expected in the message to the device.
     * @throws Throwable
     */
    @Then("^a start device OSLP message is sent to device \"([^\"]*)\"$")
    public void a_start_device_OSLP_message_is_sent_to_device(final String deviceIdentification) throws Throwable
    {
    	// TODO: Sent an OSLP start device message to device
    	final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.START_SELF_TEST);
        Assert.notNull(message);
        Assert.isTrue(message.hasStartSelfTestRequest());
    }
    
    /**
     * Verify that a stop device OSLP message is sent to the device.
     * 
     * @param deviceIdentification
     *            The device identification expected in the message to the device.
     * @throws Throwable
     */
    @Then("^a stop device OSLP message is sent to device \"([^\"]*)\"$")
    public void a_stop_device_OSLP_message_is_sent_to_device(final String deviceIdentification) throws Throwable
    {
    	// TODO: Sent an OSLP start device message to device
    	final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.STOP_SELF_TEST);
        Assert.notNull(message);
        Assert.isTrue(message.hasStopSelfTestRequest());
    }
    
    /**
     * Verify that a get status OSLP message is sent to the device.
     * 
     * @param deviceIdentification
     *            The device identification expected in the message to the device.
     * @throws Throwable
     */
    @Then("^a get status OSLP message is sent to device \"([^\"]*)\"$")
    public void a_get_status_OSLP_message_is_sent_to_device(final String deviceIdentification) throws Throwable
    {
    	// TODO: Sent an OSLP start device message to device
    	final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.GET_STATUS);
        Assert.notNull(message);
        Assert.isTrue(message.hasGetStatusRequest());
    }
    
    /**
     * Verify that a resume schedule OSLP message is sent to the device.
     * 
     * @param deviceIdentification
     *            The device identification expected in the message to the device.
     * @throws Throwable
     */
    @Then("^a resume schedule OSLP message is sent to device \"([^\"]*)\"$")
    public void a_resume_schedule_OSLP_message_is_sent_to_device(final String deviceIdentification) throws Throwable
    {
    	// TODO: Sent an OSLP start device message to device
    	final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.RESUME_SCHEDULE);
        Assert.notNull(message);
        Assert.isTrue(message.hasResumeScheduleRequest());
    }
    
    /**
     * Verify that a set reboot OSLP message is sent to the device.
     * 
     * @param deviceIdentification
     *            The device identification expected in the message to the device.
     * @throws Throwable
     */
    @Then("^a set reboot OSLP message is sent to device \"([^\"]*)\"$")
    public void a_set_reboot_OSLP_message_is_sent_to_device(final String deviceIdentification) throws Throwable
    {
    	// TODO: Sent an OSLP start device message to device
    	final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.SET_REBOOT);
        Assert.notNull(message);
        Assert.isTrue(message.hasSetRebootRequest());
    }
    
    /**
     * Verify that a set transition OSLP message is sent to the device.
     * 
     * @param deviceIdentification
     *            The device identification expected in the message to the device.
     * @throws Throwable
     */
    @Then("^a set transition OSLP message is sent to device \"([^\"]*)\"$")
    public void a_set_transition_OSLP_message_is_sent_to_device(final String deviceIdentification) throws Throwable
    {
    	// TODO: Sent an OSLP start device message to device
    	final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.SET_TRANSITION);
        Assert.notNull(message);
        Assert.isTrue(message.hasSetTransitionRequest());
    }
}
