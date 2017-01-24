/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.automatictests.platform.glue.steps.mocks;

import static com.alliander.osgp.automatictests.platform.core.Helpers.getBoolean;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getDate;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getEnum;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getInteger;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getString;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceRequestMessageType;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.EventNotificationType;
import com.alliander.osgp.automatictests.platform.Defaults;
import com.alliander.osgp.automatictests.platform.Keys;
import com.alliander.osgp.automatictests.platform.core.ScenarioContext;
import com.alliander.osgp.automatictests.platform.mocks.oslpdevice.DeviceSimulatorException;
import com.alliander.osgp.automatictests.platform.mocks.oslpdevice.MockOslpServer;
import com.alliander.osgp.dto.valueobjects.EventNotificationTypeDto;
import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.oslp.Oslp.ActionTime;
import com.alliander.osgp.oslp.Oslp.Event;
import com.alliander.osgp.oslp.Oslp.EventNotification;
import com.alliander.osgp.oslp.Oslp.EventNotificationRequest;
import com.alliander.osgp.oslp.Oslp.EventNotificationResponse;
import com.alliander.osgp.oslp.Oslp.LightType;
import com.alliander.osgp.oslp.Oslp.LightValue;
import com.alliander.osgp.oslp.Oslp.LinkType;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.Oslp.ResumeScheduleRequest;
import com.alliander.osgp.oslp.Oslp.Schedule;
import com.alliander.osgp.oslp.Oslp.SetScheduleRequest;
import com.alliander.osgp.oslp.Oslp.SetTransitionRequest;
import com.alliander.osgp.oslp.Oslp.Status;
import com.alliander.osgp.oslp.Oslp.TransitionType;
import com.alliander.osgp.oslp.Oslp.TriggerType;
import com.alliander.osgp.oslp.Oslp.Weekday;
import com.alliander.osgp.oslp.OslpUtils;
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
        final String eventNotificationTypesString = getString(result, Keys.EVENTNOTIFICATIONTYPES,
                Defaults.EVENTNOTIFICATIONTYPES);
        for (String eventNotificationType : eventNotificationTypesString.split(Keys.SEPARATOR)) {
            if (!eventNotificationType.isEmpty()) {
                eventNotificationTypes = eventNotificationTypes
                        + Enum.valueOf(EventNotificationTypeDto.class, eventNotificationType.trim()).getValue();
            }
        }

        List<LightValue> lightValues = new ArrayList<LightValue>();
        if (!getString(result, Keys.LIGHTVALUES, Defaults.LIGHTVALUES).isEmpty()
                && getString(result, Keys.LIGHTVALUES, Defaults.LIGHTVALUES).split(Keys.SEPARATOR).length > 0) {

            for (String lightValueString : getString(result, Keys.LIGHTVALUES, Defaults.LIGHTVALUES)
                    .split(Keys.SEPARATOR)) {
                String[] parts = lightValueString.split(Keys.SEPARATOR_SEMICOLON);

                LightValue lightValue = LightValue.newBuilder()
                        .setIndex(OslpUtils.integerToByteString(Integer.parseInt(parts[0])))
                        .setOn(parts[1].toLowerCase().equals("true"))
                        .setDimValue(OslpUtils.integerToByteString(Integer.parseInt(parts[2]))).build();

                lightValues.add(lightValue);
            }
        }

        this.oslpMockServer.mockGetStatusResponse(
                getEnum(result, Keys.PREFERRED_LINKTYPE, LinkType.class, Defaults.PREFERRED_LINKTYPE),
                getEnum(result, Keys.ACTUAL_LINKTYPE, LinkType.class, Defaults.ACTUAL_LINKTYPE),
                getEnum(result, Keys.LIGHTTYPE, LightType.class, Defaults.LIGHTTYPE), eventNotificationTypes,
                getEnum(result, Keys.STATUS, Oslp.Status.class, Defaults.STATUS), lightValues);
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
    public void aGetFirmwareVersionOSLPMessageIsSentToDevice(final String deviceIdentification) throws Throwable {
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
    public void aSetLightOSLPMessageWithOneLightvalueIsSentToTheDevice(final Map<String, String> expectedParameters)
            throws Throwable {
        final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.SET_LIGHT);
        Assert.assertNotNull(message);
        Assert.assertTrue(message.hasSetLightRequest());

        LightValue lightValue = message.getSetLightRequest().getValues(0);

        Assert.assertEquals(getInteger(expectedParameters, Keys.INDEX, Defaults.INDEX),
                OslpUtils.byteStringToInteger(lightValue.getIndex()));
        if (expectedParameters.containsKey(Keys.DIMVALUE)
                && !StringUtils.isEmpty(expectedParameters.get(Keys.DIMVALUE))) {
            Assert.assertEquals(getInteger(expectedParameters, Keys.DIMVALUE, Defaults.DIMVALUE),
                    OslpUtils.byteStringToInteger(lightValue.getDimValue()));
        }
        Assert.assertEquals(getBoolean(expectedParameters, Keys.ON, Defaults.ON), lightValue.getOn());
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
    public void aSetEventNotificationOslpMessageIsSentToDevice(final String deviceIdentification) throws Throwable {
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
    public void aResumeScheduleOSLPMessageIsSentToDevice(final String deviceIdentification,
            final Map<String, String> expectedRequest) throws Throwable {
        final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.RESUME_SCHEDULE);
        Assert.assertNotNull(message);
        Assert.assertTrue(message.hasResumeScheduleRequest());

        ResumeScheduleRequest request = message.getResumeScheduleRequest();

        Assert.assertEquals(getBoolean(expectedRequest, Keys.ISIMMEDIATE), request.getImmediate());
        Assert.assertEquals(getInteger(expectedRequest, Keys.INDEX), OslpUtils.byteStringToInteger(request.getIndex()));
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
    public void aSetTransitionOSLPMessageIsSentToDevice(final String deviceIdentification,
            final Map<String, String> expectedResult) throws Throwable {
        final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.SET_TRANSITION);
        Assert.assertNotNull(message);
        Assert.assertTrue(message.hasSetTransitionRequest());

        SetTransitionRequest request = message.getSetTransitionRequest();

        Assert.assertEquals(getEnum(expectedResult, Keys.TRANSITION_TYPE, TransitionType.class),
                request.getTransitionType());
        if (expectedResult.containsKey(Keys.TIME)) {
            // TODO: How to check the time?
            // Assert.assertEquals(expectedResult.get(Keys.KEY_TIME),
            // request.getTime());
        }
    }

    public void anUpdateKeyOSLPMessageIsSentToDevice(final String deviceIdentification) throws Throwable {
        final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.UPDATE_KEY);
        Assert.assertNotNull(message);
        Assert.assertTrue(message.hasSetDeviceVerificationKeyRequest());
    }

    /**
     * Simulates sending an OSLP EventNotification message to the OSLP Protocol
     * adapter.
     *
     * @param settings
     * @throws DeviceSimulatorException
     * @throws IOException
     * @throws ParseException
     */
    @When("^receiving an OSLP event notification message$")
    public void receivingAnOSLPEventNotificationMessage(final Map<String, String> settings)
            throws DeviceSimulatorException, IOException, ParseException {

        final EventNotification eventNotification = EventNotification.newBuilder()
                .setDescription(getString(settings, Keys.DESCRIPTION, ""))
                .setEvent(getEnum(settings, Keys.EVENT, Event.class)).build();

        final Message message = Oslp.Message.newBuilder()
                .setEventNotificationRequest(EventNotificationRequest.newBuilder().addNotifications(eventNotification))
                .build();

        // Save the OSLP response for later validation.
        ScenarioContext.Current().put(Keys.RESPONSE, this.oslpMockServer.sendRequest(message));
    }

    @Then("^the OSLP event notification response contains$")
    public void theOSLPEventNotificationResponseContains(final Map<String, String> expectedResponse) {
        final Message responseMessage = (Message) ScenarioContext.Current().get(Keys.RESPONSE);

        final EventNotificationResponse response = responseMessage.getEventNotificationResponse();

        Assert.assertEquals(getString(expectedResponse, Keys.STATUS), response.getStatus());
    }

    /**
     * Setup method to get a status which should be returned by the mock.
     *
     * @param result
     *            The get status to respond.
     * @throws Throwable
     */
    @Given("^the device returns a get status response \"([^\"]*)\" over OSLP$")
    public void theDeviceReturnsAGetStatusResponseOverOSLP(final String result,
            final Map<String, String> requestParameters) throws Throwable {

        int eventNotificationTypes = 0;
        if (getString(requestParameters, Keys.EVENTNOTIFICATIONTYPES, Defaults.EVENTNOTIFICATIONTYPES).trim()
                .split(",").length > 0) {
            for (final String eventNotificationType : getString(requestParameters, Keys.EVENTNOTIFICATIONTYPES,
                    Defaults.EVENTNOTIFICATIONTYPES).trim().split(",")) {
                if (!eventNotificationType.isEmpty()) {
                    eventNotificationTypes = eventNotificationTypes
                            + Enum.valueOf(EventNotificationType.class, eventNotificationType.trim()).ordinal();
                }
            }
        }

        final List<LightValue> lightValues = new ArrayList<>();
        if (!getString(requestParameters, Keys.LIGHTVALUES, Defaults.LIGHTVALUES).isEmpty()
                && getString(requestParameters, Keys.LIGHTVALUES, Defaults.LIGHTVALUES)
                        .split(Keys.SEPARATOR).length > 0) {

            for (final String lightValueString : getString(requestParameters, Keys.LIGHTVALUES, Defaults.LIGHTVALUES)
                    .split(Keys.SEPARATOR)) {
                final String[] parts = lightValueString.split(Keys.SEPARATOR_SEMICOLON);

                final LightValue lightValue = LightValue.newBuilder()
                        .setIndex(OslpUtils.integerToByteString(Integer.parseInt(parts[0])))
                        .setOn(parts[1].toLowerCase().equals("true"))
                        .setDimValue(OslpUtils.integerToByteString(Integer.parseInt(parts[2]))).build();

                lightValues.add(lightValue);
            }
        }

        this.oslpMockServer.mockGetStatusResponse(
                getEnum(requestParameters, Keys.PREFERRED_LINKTYPE, LinkType.class, Defaults.PREFERRED_LINKTYPE),
                getEnum(requestParameters, Keys.ACTUAL_LINKTYPE, LinkType.class, Defaults.ACTUAL_LINKTYPE),
                getEnum(requestParameters, Keys.LIGHTTYPE, LightType.class, Defaults.LIGHTTYPE), eventNotificationTypes,
                Oslp.Status.valueOf(result), lightValues);
    }

    /**
     * Setup method to get a status which should be returned by the mock.
     *
     * @param result
     *            The get status to respond.
     * @throws Throwable
     */
    @Given("^the device returns a set light schedule response \"([^\"]*)\" over OSLP$")
    public void theDeviceReturnsASetLightScheduleResponseOverOSLP(final String result) throws Throwable {

        this.callMockSetScheduleResponse(result, DeviceRequestMessageType.SET_LIGHT_SCHEDULE);
    }

    /**
     * Setup method to get a status which should be returned by the mock.
     *
     * @param result
     *            The get status to respond.
     * @throws Throwable
     */
    @Given("^the device returns a set tariff schedule response \"([^\"]*)\" over OSLP$")
    public void theDeviceReturnsASetTariffScheduleResponseOverOSLP(final String result) throws Throwable {

        this.callMockSetScheduleResponse(result, DeviceRequestMessageType.SET_TARIFF_SCHEDULE);
    }

    private void callMockSetScheduleResponse(final String result, final DeviceRequestMessageType type) {
        Oslp.Status oslpStatus = Status.OK;

        switch (result) {
        case "OK":
            oslpStatus = Status.OK;
            break;
        case "FAILURE":
            oslpStatus = Status.FAILURE;
            break;
        case "REJECTED":
            oslpStatus = Status.REJECTED;
            break;
        // TODO: Implement other possible status
        }

        this.oslpMockServer.mockSetScheduleResponse(type, oslpStatus);
    }

    /**
     * Verify that a set light schedule OSLP message is sent to the device.
     *
     * @param deviceIdentification
     *            The device identification expected in the message to the
     *            device.
     * @throws Throwable
     */
    @Then("^a set light schedule OSLP message is sent to device \"([^\"]*)\"$")
    public void aSetLightScheduleOSLPMessageIsSentToDevice(final String deviceIdentification,
            final Map<String, String> expectedRequest) throws Throwable {
        this.checkAndValidateRequest(DeviceRequestMessageType.SET_LIGHT_SCHEDULE, expectedRequest);
    }

    /**
     * Verify that a set tariff schedule OSLP message is sent to the device.
     *
     * @param deviceIdentification
     *            The device identification expected in the message to the
     *            device.
     * @throws Throwable
     */
    @Then("^a set tariff schedule OSLP message is sent to device \"([^\"]*)\"$")
    public void aSetTariffScheduleOSLPMessageIsSentToDevice(final String deviceIdentification,
            final Map<String, String> expectedRequest) throws Throwable {
        this.checkAndValidateRequest(DeviceRequestMessageType.SET_TARIFF_SCHEDULE, expectedRequest);
    }

    private void checkAndValidateRequest(final DeviceRequestMessageType type,
            final Map<String, String> expectedRequest) {
        final Message message = this.oslpMockServer.waitForRequest(type);
        Assert.assertNotNull(message);
        Assert.assertTrue(message.hasSetScheduleRequest());

        final SetScheduleRequest request = message.getSetScheduleRequest();

        for (final Schedule schedule : request.getSchedulesList()) {
            if (type == DeviceRequestMessageType.SET_LIGHT_SCHEDULE) {
                Assert.assertEquals(getEnum(expectedRequest, Keys.SCHEDULE_WEEKDAY, Weekday.class),
                        schedule.getWeekday());
            }
            if (!expectedRequest.get(Keys.SCHEDULE_STARTDAY).isEmpty()) {
                final String startDay = getDate(expectedRequest, Keys.SCHEDULE_STARTDAY).toDateTime(DateTimeZone.UTC)
                        .toString("yyyyMMdd");

                Assert.assertEquals(startDay, schedule.getStartDay());
            }
            if (!expectedRequest.get(Keys.SCHEDULE_ENDDAY).isEmpty()) {
                final String endDay = getDate(expectedRequest, Keys.SCHEDULE_ENDDAY).toDateTime(DateTimeZone.UTC)
                        .toString("yyyyMMdd");

                Assert.assertEquals(endDay, schedule.getEndDay());
            }

            if (type == DeviceRequestMessageType.SET_LIGHT_SCHEDULE) {
                Assert.assertEquals(getEnum(expectedRequest, Keys.SCHEDULE_ACTIONTIME, ActionTime.class),
                        schedule.getActionTime());
            }
            String expectedTime = getString(expectedRequest, Keys.SCHEDULE_TIME).replace(":", "");
            if (expectedTime.contains(".")) {
                expectedTime = expectedTime.substring(0, expectedTime.indexOf("."));
            }
            Assert.assertEquals(expectedTime, schedule.getTime());
            final String scheduleLightValue = getString(expectedRequest,
                    (type == DeviceRequestMessageType.SET_LIGHT_SCHEDULE) ? Keys.SCHEDULE_LIGHTVALUES
                            : Keys.SCHEDULE_TARIFFVALUES);
            final String[] scheduleLightValues = scheduleLightValue.split(";");
            Assert.assertEquals(scheduleLightValues.length, schedule.getValueCount());
            for (int i = 0; i < scheduleLightValues.length; i++) {
                final Integer index = OslpUtils.byteStringToInteger(schedule.getValue(i).getIndex()),
                        dimValue = OslpUtils.byteStringToInteger(schedule.getValue(i).getDimValue());
                if (type == DeviceRequestMessageType.SET_LIGHT_SCHEDULE) {
                    Assert.assertEquals(scheduleLightValues[i], String.format("%s,%s,%s", (index != null) ? index : "",
                            schedule.getValue(i).getOn(), (dimValue != null) ? dimValue : ""));
                } else if (type == DeviceRequestMessageType.SET_TARIFF_SCHEDULE) {
                    Assert.assertEquals(scheduleLightValues[i],
                            String.format("%s,%s", (index != null) ? index : "", !schedule.getValue(i).getOn()));
                }
            }

            if (type == DeviceRequestMessageType.SET_LIGHT_SCHEDULE) {
                Assert.assertEquals((!getString(expectedRequest, Keys.SCHEDULE_TRIGGERTYPE).isEmpty())
                        ? getEnum(expectedRequest, Keys.SCHEDULE_TRIGGERTYPE, TriggerType.class)
                        : TriggerType.TT_NOT_SET, schedule.getTriggerType());

                final String[] windowTypeValues = getString(expectedRequest, Keys.SCHEDULE_TRIGGERWINDOW).split(",");
                if (windowTypeValues.length == 2) {
                    Assert.assertEquals(Integer.parseInt(windowTypeValues[0]), schedule.getWindow().getMinutesBefore());
                    Assert.assertEquals(Integer.parseInt(windowTypeValues[1]), schedule.getWindow().getMinutesAfter());
                }
            }
        }
    }
}
