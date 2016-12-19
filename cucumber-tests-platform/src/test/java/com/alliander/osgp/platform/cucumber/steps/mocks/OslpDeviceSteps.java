/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.mocks;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getBoolean;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getEnum;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getInteger;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.valueobjects.RelayType;
import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.oslp.Oslp.LightType;
import com.alliander.osgp.oslp.Oslp.LightValue;
import com.alliander.osgp.oslp.Oslp.LinkType;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.Oslp.ResumeScheduleRequest;
import com.alliander.osgp.oslp.Oslp.SetTransitionRequest;
import com.alliander.osgp.oslp.Oslp.Status;
import com.alliander.osgp.oslp.Oslp.TransitionType;
import com.alliander.osgp.oslp.OslpUtils;
import com.alliander.osgp.platform.cucumber.mocks.oslpdevice.MockOslpChannelHandler;
import com.alliander.osgp.platform.cucumber.mocks.oslpdevice.MockOslpServer;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.google.protobuf.ByteString;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * Class which holds all the OSLP device mock steps in order to let the device
 * mock behave correctly for the automatic test.
 */
public class OslpDeviceSteps {

	private static final Logger LOGGER = LoggerFactory.getLogger(MockOslpChannelHandler.class);

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
	public void the_device_returns_a_set_light_over_OSLP(final String result) throws Throwable {
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
	public void the_device_returns_firmware_version_over_OSLP(final String firmwareVersion) throws Throwable {
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
	public void the_device_returns_an_event_notification_over_OSLP(final String result) throws Throwable {
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
	public void the_device_returns_a_start_device_response_over_OSLP(final String result) throws Throwable {
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
	public void the_device_returns_a_stop_device_response_over_OSLP(final String result) throws Throwable {
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
	public void the_device_returns_a_get_status_response_over_OSLP(final Map<String, String> result) throws Throwable {
		final String[] results = result.get("Result").split(";");

		int internalId = 0, externalId = 0;
		RelayType relayType = null;

		String[] items = null;
		for (int i = 0; i < results.length; i++) {
			items = results[i].split(",");
			internalId = Integer.parseInt(items[0]);
			externalId = Integer.parseInt(items[1]);

			relayType = (items[2].equals("LIGHT")) ? RelayType.LIGHT
					: (items[2].equals("TARIFF")) ? RelayType.TARIFF
							: (items[2].equals("TARIFF_REVERSED")) ? RelayType.TARIFF_REVERSED : null;
		}

		// TODO: Check which status is needed and check if the other values need
		// to be set
		this.oslpMockServer.mockGetStatusResponse(LinkType.LINK_NOT_SET, LinkType.LINK_NOT_SET, LightType.LT_NOT_SET, 0,
				Status.OK);
	}

	/**
	 * Setup method to get a status which should be returned by the mock.
	 * 
	 * @param result
	 *            The get status to respond.
	 * @throws Throwable
	 */
	@Given("^the device returns a get status response \"([^\"]*)\" over OSLP$")
	public void the_device_returns_a_get_status_response_over_OSLP(final String result) throws Throwable {
		Oslp.Status oslpStatus = Status.OK;

		switch (result) {
		case "OK":
			oslpStatus = Status.OK;
			// TODO: Implement other possible status
		}

		// TODO: Check which status is needed and check if the other values need
		// to be set
		this.oslpMockServer.mockGetStatusResponse(LinkType.LINK_NOT_SET, LinkType.LINK_NOT_SET, LightType.LT_NOT_SET, 0,
				oslpStatus);
	}

	/**
	 * Setup method to resume a schedule which should be returned by the mock.
	 * 
	 * @param result
	 *            The resume schedule to respond.
	 * @throws Throwable
	 */
	@Given("^the device returns a resume schedule response \"([^\"]*)\" over OSLP$")
	public void the_device_returns_a_resume_schedule_response_over_OSLP(final String result) throws Throwable {
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
	public void the_device_returns_a_set_reboot_response_over_OSLP(final String result) throws Throwable {
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
	public void the_device_returns_a_set_transition_response_over_OSLP(final String result) throws Throwable {
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
	public void a_get_firmware_version_OSLP_message_is_sent_to_device(final String deviceIdentification)
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
	public void a_set_light_OSLP_message_with_one_lightvalue_is_sent_to_the_device(
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
	public void a_set_event_notification_oslp_message_is_sent_to_device(final String deviceIdentification)
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
	public void a_start_device_OSLP_message_is_sent_to_device(final String deviceIdentification) throws Throwable {
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
	public void a_stop_device_OSLP_message_is_sent_to_device(final String deviceIdentification) throws Throwable {
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
	public void a_get_status_OSLP_message_is_sent_to_device(final String deviceIdentification) throws Throwable {
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
	public void a_resume_schedule_OSLP_message_is_sent_to_device(final String deviceIdentification, final Map<String, String> expectedRequest) throws Throwable {
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
	public void a_set_reboot_OSLP_message_is_sent_to_device(final String deviceIdentification) throws Throwable {
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
	public void a_set_transition_OSLP_message_is_sent_to_device(final String deviceIdentification, final Map<String, String> expectedResult) throws Throwable {
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
}
