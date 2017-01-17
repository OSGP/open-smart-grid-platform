/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.glue.steps.mocks;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getBoolean;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getEnum;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getInteger;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.dto.valueobjects.EventNotificationTypeDto;
import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.oslp.Oslp.Event;
import com.alliander.osgp.oslp.Oslp.EventNotification;
import com.alliander.osgp.oslp.Oslp.EventNotificationRequest;
import com.alliander.osgp.oslp.Oslp.EventNotificationResponse;
import com.alliander.osgp.oslp.Oslp.LightType;
import com.alliander.osgp.oslp.Oslp.LightValue;
import com.alliander.osgp.oslp.Oslp.LinkType;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.Oslp.ResumeScheduleRequest;
import com.alliander.osgp.oslp.Oslp.SetTransitionRequest;
import com.alliander.osgp.oslp.Oslp.Status;
import com.alliander.osgp.oslp.Oslp.TransitionType;
import com.alliander.osgp.oslp.OslpUtils;
import com.alliander.osgp.platform.cucumber.Defaults;
import com.alliander.osgp.platform.cucumber.Keys;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.mocks.oslpdevice.DeviceSimulatorException;
import com.alliander.osgp.platform.cucumber.mocks.oslpdevice.MockOslpServer;
import com.google.protobuf.ByteString;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class which holds all the OSLP device mock steps in order to let the device
 * mock behave correctly for the automatic test.
 */
public class OslpDeviceSteps {


	@Autowired
	private MockOslpServer oslpMockServer;

	/**
	 * Setup method to set the firmware which should be returned by the mock.
	 * 
	 * @param firmwareVersion
	 *            The firmware to respond.
	 * @throws Throwable
	 */
	@Given("^the device returns a set light response \"([^\"]*)\" over OSLP$")
	public void theDeviceReturnsASetLightOverOSLP(final String result) throws Throwable {
		Oslp.Status oslpStatus = Status.OK;

		switch (result) {
		case "OK":
			oslpStatus = Status.OK;
			// TODO: Implement other possible status
		}

		this.oslpMockServer.mockSetLightResponse(oslpStatus);
	}

	/**
	 * Setup method to set the firmware which should be returned by the mock.
	 * 
	 * @param firmwareVersion
	 *            The firmware to respond.
	 * @throws Throwable
	 */
	@Given("^the device returns firmware version \"([^\"]*)\" over OSLP$")
	public void theDeviceReturnsFirmwareVersionOverOSLP(final String firmwareVersion) throws Throwable {
		this.oslpMockServer.mockFirmwareResponse(firmwareVersion);
	}

	/**
	 * Setup method to set the event notification which should be returned by
	 * the mock.
	 * 
	 * @param firmwareVersion
	 *            The event notification to respond.
	 * @throws Throwable
	 */

	@Given("^the device returns a set event notification \"([^\"]*)\" over OSLP$")
	public void theDeviceReturnsAnEventNotificationOverOSLP(final String result) throws Throwable {
		Oslp.Status oslpStatus = Status.OK;

		switch (result) {
		case "OK":
			oslpStatus = Status.OK;
			// TODO: Implement other possible status
		}

		this.oslpMockServer.mockSetEventNotificationResponse(oslpStatus);
	}

	/**
	 * Setup method to start a device which should be returned by the mock.
	 * 
	 * @param result
	 *            The start device to respond.
	 * @throws Throwable
	 */
	@Given("^the device returns a start device response \"([^\"]*)\" over OSLP$")
	public void theDeviceReturns_AStartDeviceResponseOverOSLP(final String result) throws Throwable {
		Oslp.Status oslpStatus = Status.OK;

		switch (result) {
		case "OK":
			oslpStatus = Status.OK;
			// TODO: Implement other possible status
		}
		// TODO: Make Mock method
		this.oslpMockServer.mockStartDeviceResponse(oslpStatus);
	}

	/**
	 * Setup method to stop a device which should be returned by the mock.
	 * 
	 * @param result
	 *            The stop device to respond.
	 * @throws Throwable
	 */
	@Given("^the device returns a stop device response \"([^\"]*)\" over OSLP$")
	public void theDeviceReturnsAStopDeviceResponseOverOSLP(final String result) throws Throwable {
		Oslp.Status oslpStatus = Status.OK;

		switch (result) {
		case "OK":
			oslpStatus = Status.OK;
			// TODO: Implement other possible status
		}
		// TODO: Check if ByteString.EMPTY must be something else
		this.oslpMockServer.mockStopDeviceResponse(ByteString.EMPTY, oslpStatus);
	}

	/**
	 * Setup method to get a status which should be returned by the mock.
	 * 
	 * @param result
	 *            The get status to respond.
	 * @throws Throwable
	 */
	@Given("^the device returns a get status response over OSLP$")
	public void theDeviceReturnsAGetStatusResponseOverOSLP(final Map<String, String> result) throws Throwable {
	
		int eventNotificationTypes = 0;
		final String eventNotificationTypesString = getString(result, Keys.KEY_EVENTNOTIFICATIONTYPES, Defaults.DEFAULT_EVENTNOTIFICATIONTYPES);
		for (String eventNotificationType : eventNotificationTypesString.split(Keys.SEPARATOR)) {
			if (!eventNotificationType.isEmpty()) {
				eventNotificationTypes = eventNotificationTypes + Enum.valueOf(EventNotificationTypeDto.class, eventNotificationType.trim()).getValue();
			}
		}	
				
		List<LightValue> lightValues = new ArrayList<LightValue>();
		if (!getString(result, Keys.KEY_LIGHTVALUES, Defaults.DEFAULT_LIGHTVALUES).isEmpty() && 
				getString(result, Keys.KEY_LIGHTVALUES, Defaults.DEFAULT_LIGHTVALUES).split(Keys.SEPARATOR).length > 0) {
			
			for (String lightValueString : getString(result, Keys.KEY_LIGHTVALUES, Defaults.DEFAULT_LIGHTVALUES).split(Keys.SEPARATOR)) {
				String[] parts = lightValueString.split(Keys.SEPARATOR_SEMICOLON);
	
				LightValue lightValue = LightValue.newBuilder()
						.setIndex(OslpUtils.integerToByteString(Integer.parseInt(parts[0])))
						.setOn(parts[1].toLowerCase().equals("true"))
						.setDimValue(OslpUtils.integerToByteString(Integer.parseInt(parts[2])))
						.build();
						
				lightValues.add(lightValue);
			}
		}

		this.oslpMockServer.mockGetStatusResponse(
			getEnum(result, Keys.KEY_PREFERRED_LINKTYPE, LinkType.class, Defaults.DEFAULT_PREFERRED_LINKTYPE),
			getEnum(result, Keys.KEY_ACTUAL_LINKTYPE, LinkType.class, Defaults.DEFAULT_ACTUAL_LINKTYPE),
			getEnum(result, Keys.KEY_LIGHTTYPE, LightType.class, Defaults.DEFAULT_LIGHTTYPE),
			eventNotificationTypes,
			getEnum(result, Keys.KEY_STATUS, Oslp.Status.class, Defaults.DEFAULT_STATUS),
			lightValues);
	}
	
	/**
	 * Setup method to resume a schedule which should be returned by the mock.
	 * 
	 * @param result
	 *            The resume schedule to respond.
	 * @throws Throwable
	 */
	@Given("^the device returns a resume schedule response \"([^\"]*)\" over OSLP$")
	public void theDeviceReturnsAResumeScheduleResponseOverOSLP(final String result) throws Throwable {
		Oslp.Status oslpStatus = Status.OK;

		switch (result) {
		case "OK":
			oslpStatus = Status.OK;
			// TODO: Implement other possible status
		}
		//
		this.oslpMockServer.mockResumeScheduleResponse(oslpStatus);
	}

	/**
	 * Setup method to set a reboot which should be returned by the mock.
	 * 
	 * @param result
	 *            The stop device to respond.
	 * @throws Throwable
	 */
	@Given("^the device returns a set reboot response \"([^\"]*)\" over OSLP$")
	public void theDeviceReturnsASetRebootResponseOverOSLP(final String result) throws Throwable {
		Oslp.Status oslpStatus = Status.OK;

		switch (result) {
		case "OK":
			oslpStatus = Status.OK;
			// TODO: Implement other possible status
		}

		this.oslpMockServer.mockSetRebootResponse(oslpStatus);
	}

	/**
	 * Setup method to set a transition which should be returned by the mock.
	 * 
	 * @param result
	 *            The stop device to respond.
	 * @throws Throwable
	 */
	@Given("^the device returns a set transition response \"([^\"]*)\" over OSLP$")
	public void theDeviceReturnsASetTransitionResponseOverOSLP(final String result) throws Throwable {
		Oslp.Status oslpStatus = Status.OK;

		switch (result) {
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
	public void aGetFirmwareVersionOSLPMessageIsSentToDevice(final String deviceIdentification)
			throws Throwable {
		final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.GET_FIRMWARE_VERSION);
		Assert.assertNotNull(message);
		Assert.assertTrue(message.hasGetFirmwareVersionRequest());
		// TODO: Check actual message for the correct firmware(s).
	}

	/**
	 * Verify that a set light OSLP message is sent to the device.
	 *
	 * @throws Throwable
	 */
	@Then("^a set light OSLP message with one light value is sent to the device$")
	public void aSetLightOSLPMessageWithOneLightvalueIsSentToTheDevice(
			final Map<String, String> expectedParameters) throws Throwable {
		final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.SET_LIGHT);
		Assert.assertNotNull(message);
		Assert.assertTrue(message.hasSetLightRequest());

		LightValue lightValue = message.getSetLightRequest().getValues(0);
		
		Assert.assertEquals(
				getInteger(expectedParameters, Keys.KEY_INDEX, Defaults.DEFAULT_INDEX),
				OslpUtils.byteStringToInteger(lightValue.getIndex()));
		if (expectedParameters.containsKey(Keys.KEY_DIMVALUE)
				&& !StringUtils.isEmpty(expectedParameters.get(Keys.KEY_DIMVALUE))) {
			Assert.assertEquals(getInteger(expectedParameters, Keys.KEY_DIMVALUE, Defaults.DEFAULT_DIMVALUE),
					OslpUtils.byteStringToInteger(lightValue.getDimValue()));
		}
		Assert.assertEquals(getBoolean(expectedParameters, Keys.KEY_ON, Defaults.DEFAULT_ON), lightValue.getOn());
	}
	
	@Then("^a set light OSLP message with \"([^\"]*)\" lightvalues is sent to the device$")
	public void aSetLightOslpMessageWithLightValuesIsSentToTheDevice(final int nofLightValues) {
		final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.SET_LIGHT);
		Assert.assertNotNull(message);
		Assert.assertTrue(message.hasSetLightRequest());
		
		Assert.assertEquals(nofLightValues, message.getSetLightRequest().getValuesList().size());
	}
	
	/**
	 * Verify that a event notification OSLP message is sent to the device.
	 * 
	 * @param deviceIdentification
	 *            The device identification expected in the message to the
	 *            device.
	 * @throws Throwable
	 */
	@Then("^a set event notification OSLP message is sent to device \"([^\"]*)\"")
	public void aSetEventNotificationOslpMessageIsSentToDevice(final String deviceIdentification)
			throws Throwable {
		final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.SET_EVENT_NOTIFICATIONS);
		Assert.assertNotNull(message);
		Assert.assertTrue(message.hasSetEventNotificationsRequest());
	}

	/**
	 * Verify that a start device OSLP message is sent to the device.
	 * 
	 * @param deviceIdentification
	 *            The device identification expected in the message to the
	 *            device.
	 * @throws Throwable
	 */
	@Then("^a start device OSLP message is sent to device \"([^\"]*)\"$")
	public void aStartDeviceOSLPMessageIsSentToDevice(final String deviceIdentification) throws Throwable {
		// TODO: Sent an OSLP start device message to device
		final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.START_SELF_TEST);
		Assert.assertNotNull(message);
		Assert.assertTrue(message.hasStartSelfTestRequest());
	}
	
	/**
	 * Verify that a stop device OSLP message is sent to the device.
	 * 
	 * @param deviceIdentification
	 *            The device identification expected in the message to the
	 *            device.
	 * @throws Throwable
	 */
	@Then("^a stop device OSLP message is sent to device \"([^\"]*)\"$")
	public void aStopDeviceOSLPMessageIsSentToDevice(final String deviceIdentification) throws Throwable {
		// TODO: Sent an OSLP start device message to device
		final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.STOP_SELF_TEST);
		Assert.assertNotNull(message);
		Assert.assertTrue(message.hasStopSelfTestRequest());
	}

	/**
	 * Verify that a get status OSLP message is sent to the device.
	 * 
	 * @param deviceIdentification
	 *            The device identification expected in the message to the
	 *            device.
	 * @throws Throwable
	 */
	@Then("^a get status OSLP message is sent to device \"([^\"]*)\"$")
	public void aGetStatusOSLPMessageIsSentToDevice(final String deviceIdentification) throws Throwable {
		final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.GET_STATUS);
		Assert.assertNotNull(message);
		Assert.assertTrue(message.hasGetStatusRequest());
	}

	/**
	 * Verify that a resume schedule OSLP message is sent to the device.
	 * 
	 * @param deviceIdentification
	 *            The device identification expected in the message to the
	 *            device.
	 * @throws Throwable
	 */
	@Then("^a resume schedule OSLP message is sent to device \"([^\"]*)\"$")
	public void aResumeScheduleOSLPMessageIsSentToDevice(final String deviceIdentification, final Map<String, String> expectedRequest) throws Throwable {
		final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.RESUME_SCHEDULE);
		Assert.assertNotNull(message);
		Assert.assertTrue(message.hasResumeScheduleRequest());
		
		ResumeScheduleRequest request = message.getResumeScheduleRequest();
		
		Assert.assertEquals(getBoolean(expectedRequest, Keys.KEY_ISIMMEDIATE), request.getImmediate());
		Assert.assertEquals(getInteger(expectedRequest, Keys.KEY_INDEX), OslpUtils.byteStringToInteger(request.getIndex()));
	}

	/**
	 * Verify that a set reboot OSLP message is sent to the device.
	 * 
	 * @param deviceIdentification
	 *            The device identification expected in the message to the
	 *            device.
	 * @throws Throwable
	 */
	@Then("^a set reboot OSLP message is sent to device \"([^\"]*)\"$")
	public void aSetRebootOSLPMessageIsSentToDevice(final String deviceIdentification) throws Throwable {
		// TODO: Sent an OSLP start device message to device
		final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.SET_REBOOT);
		Assert.assertNotNull(message);
		Assert.assertTrue(message.hasSetRebootRequest());
	}

	/**
	 * Verify that a set transition OSLP message is sent to the device.
	 * 
	 * @param deviceIdentification
	 *            The device identification expected in the message to the
	 *            device.
	 * @throws Throwable
	 */
	@Then("^a set transition OSLP message is sent to device \"([^\"]*)\"$")
	public void aSetTransitionOSLPMessageIsSentToDevice(final String deviceIdentification, final Map<String, String> expectedResult) throws Throwable {
		final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.SET_TRANSITION);
		Assert.assertNotNull(message);
		Assert.assertTrue(message.hasSetTransitionRequest());
		
		SetTransitionRequest request = message.getSetTransitionRequest();
		
		Assert.assertEquals(getEnum(expectedResult, Keys.KEY_TRANSITION_TYPE, TransitionType.class), request.getTransitionType());
		if (expectedResult.containsKey(Keys.KEY_TIME)) {
			// TODO: How to check the time?
	 		//Assert.assertEquals(expectedResult.get(Keys.KEY_TIME), request.getTime());
		}
	}
	
    @Then("^an update key OSLP message is sent to device \"([^\"]*)\"$")
    public void anUpdateKeyOSLPMessageIsSentToDevice(final String deviceIdentification) throws Throwable {
        final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.UPDATE_KEY);
        Assert.assertNotNull(message);
        Assert.assertTrue(message.hasSetDeviceVerificationKeyRequest());
    }
    
    /**
     * Simulates sending an OSLP EventNotification message to the OSLP Protocol adapter.
     * @param settings
     * @throws DeviceSimulatorException
     * @throws IOException
     * @throws ParseException
     */
    @When("^receiving an OSLP event notification message$")
    public void receivingAnOSLPEventNotificationMessage(final Map<String, String> settings) throws DeviceSimulatorException, IOException, ParseException {
        
        EventNotification eventNotification = EventNotification.newBuilder()
                .setDescription(getString(settings, Keys.KEY_DESCRIPTION, ""))
                .setEvent(getEnum(settings, Keys.KEY_EVENT, Event.class)).build();
        
        Message message = Oslp.Message.newBuilder()
        .setEventNotificationRequest(
                EventNotificationRequest.newBuilder().addNotifications(eventNotification)).build();

        // Save the OSLP response for later validation.
        ScenarioContext.Current().put(Keys.RESPONSE, this.oslpMockServer.sendRequest(message));
    }
    
    @Then("^the OSLP event notification response contains$")
    public void theOSLPEventNotificationResponseContains(final Map<String, String> expectedResponse) {
        Message responseMessage = (Message) ScenarioContext.Current().get(Keys.RESPONSE);
        
        EventNotificationResponse response = responseMessage.getEventNotificationResponse();
        
        Assert.assertEquals(getString(expectedResponse, Keys.KEY_STATUS), response.getStatus());
    }
}
