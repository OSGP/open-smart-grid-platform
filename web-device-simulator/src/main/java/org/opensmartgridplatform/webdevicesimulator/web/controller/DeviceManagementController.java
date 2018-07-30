/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.opensmartgridplatform.oslp.Oslp.DeviceType;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.webdevicesimulator.application.services.DeviceManagementService;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.Device;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.DeviceMessageStatus;
import org.opensmartgridplatform.webdevicesimulator.service.RegisterDevice;

@Controller
@SessionAttributes
public class DeviceManagementController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementController.class);

    protected static final String DEVICES_URL = "/devices";
    protected static final String DEVICE_CREATE_URL = "/devices/create";
    protected static final String DEVICE_EDIT_URL = "/devices/edit/{id}";

    protected static final String DEVICES_VIEW = "devices/list";
    protected static final String DEVICE_CREATE_VIEW = "devices/create";
    protected static final String DEVICE_EDIT_VIEW = "devices/edit";
    protected static final String DEVICE_REGISTRATION_VIEW = "devices/deviceRegistrationCheck";

    protected static final String COMMAND_REGISTER_URL = "/devices/commands/register";
    protected static final String COMMAND_REGISTER_CONFIRM_URL = "/devices/commands/register/confirm";
    protected static final String COMMAND_SENDNOTIFICATION_URL = "/devices/commands/sendnotification";
    protected static final String COMMAND_GET_SEQUENCE_NUMBER_URL = "/devices/commands/get-sequence-number";
    protected static final String COMMAND_SET_SEQUENCE_NUMBER_URL = "/devices/commands/set-sequence-number";
    protected static final String DEVICES_JSON_URL = "/devices/json";

    protected static final String DEVICE_REGISTRATION_CHECK_JSON_URL = "/devices/deviceRegistrationCheck/json";
    protected static final String DEVICE_REBOOT_CHECK_JSON_URL = "/devices/deviceRebootCheck/json";
    protected static final String TARIFF_SWITCHING_CHECK_JSON_URL = "/devices/tariffSwitchingCheck/json";
    protected static final String LIGHT_SWITCHING_CHECK_JSON_URL = "/devices/lightSwitchingCheck/json";
    protected static final String EVENT_NOTIFICATION_CHECK_JSON_URL = "/devices/eventNotificationCheck/json";

    protected static final String DEVICE_REGISTRATION_CHECK_URL = "/devices/deviceRegistrationCheck";
    protected static final String DEVICE_REBOOT_CHECK_URL = "/devices/deviceRebootCheck";
    protected static final String TARIFF_SWITCHING_CHECK_URL = "/devices/tariffSwitchingCheck";
    protected static final String LIGHT_SWITCHING_CHECK_URL = "/devices/lightSwitchingCheck";
    protected static final String EVENT_NOTIFICATION_CHECK_URL = "/devices/eventNotificationCheck";

    protected static final String MODEL_ATTRIBUTE_DEVICES = "devices";
    protected static final String MODEL_ATTRIBUTE_ISAUTONOMOUS = "isAutonomous";
    protected static final String MODEL_ATTRIBUTE_DEVICE = "device";
    protected static final String MODEL_ATTRIBUTE_DEVICETYPES = "deviceTypes";
    protected static final String MODEL_ATTRIBUTE_LINKTYPES = "linkTypes";

    protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_CREATED = "feedback.message.device.created";
    protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_UPDATED = "feedback.message.device.updated";
    protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_REGISTERED = "feedback.message.device.registered";
    protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_REGISTERED_CONFIRM = "feedback.message.device.registered.confirm";
    protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_TIMEOUT = "feedback.message.device.timeout";
    protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_ERROR = "feedback.message.device.error";
    protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_ERROR_CREATE_DEVICE_DUPLICATE = "feedback.message.device.error.createdevice.duplicate";
    protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_ERROR_CREATE_DEVICE_NO_ID = "feedback.message.device.error.createdevice.noid";
    protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_ERROR_CREATE_DEVICE_NO_DEVICE_TYPE = "feedback.message.device.error.createdevice.nodevicetype";

    private static final String FEEDBACK_MESSAGE_KEY_EVENTNOTIFICATION_SENT = "feedback.message.eventnotification.sent";

    private static final String FAILURE_URL = "failure";

    @Resource
    private DeviceManagementService deviceManagementService;

    @Autowired
    private RegisterDevice registerDevice;

    @RequestMapping(value = DEVICES_URL, method = RequestMethod.GET)
    public String showDevices(final Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_DEVICES, this.deviceManagementService.findAllDevices());

        final List<String> deviceTypes = new ArrayList<>();
        for (final DeviceType devicetype : DeviceType.values()) {
            deviceTypes.add(devicetype.name());
        }
        model.addAttribute(MODEL_ATTRIBUTE_DEVICETYPES, deviceTypes);

        return DEVICES_VIEW;
    }

    @RequestMapping(value = DEVICE_REGISTRATION_CHECK_URL, method = RequestMethod.POST)
    public void setDeviceRegistrationValue(@RequestBody final AutonomousRequest request) {
        this.deviceManagementService.setdeviceRegistration(request.getAutonomousStatus());

    }

    @RequestMapping(value = DEVICE_REGISTRATION_CHECK_JSON_URL, method = RequestMethod.GET)
    @ResponseBody
    public Boolean getDeviceRegistrationState() {
        return this.deviceManagementService.getDevRegistration();
    }

    @RequestMapping(value = DEVICE_REBOOT_CHECK_URL, method = RequestMethod.POST)
    public void getDeviceRebootValue(@RequestBody final AutonomousRequest request) {

        this.deviceManagementService.setDeviceReboot(request.getAutonomousStatus());
    }

    @RequestMapping(value = DEVICE_REBOOT_CHECK_JSON_URL, method = RequestMethod.GET)
    @ResponseBody
    public Boolean getDeviceRebootState() {
        return this.deviceManagementService.getDevReboot();
    }

    @RequestMapping(value = TARIFF_SWITCHING_CHECK_URL, method = RequestMethod.POST)
    public void getTariffSwitchingValue(@RequestBody final AutonomousRequest request) {

        this.deviceManagementService.setTariffSwitching(request.getAutonomousStatus());

    }

    @RequestMapping(value = TARIFF_SWITCHING_CHECK_JSON_URL, method = RequestMethod.GET)
    @ResponseBody
    public Boolean getTariffSwitchingState() {
        return this.deviceManagementService.getTariffSwitching();
    }

    @RequestMapping(value = LIGHT_SWITCHING_CHECK_URL, method = RequestMethod.POST)
    public void getLightSwitchingValue(@RequestBody final AutonomousRequest request) {

        this.deviceManagementService.setLightSwitching(request.getAutonomousStatus());

    }

    @RequestMapping(value = LIGHT_SWITCHING_CHECK_JSON_URL, method = RequestMethod.GET)
    @ResponseBody
    public Boolean getLightSwitchingState() {
        return this.deviceManagementService.getLightSwitching();
    }

    @RequestMapping(value = EVENT_NOTIFICATION_CHECK_URL, method = RequestMethod.POST)
    public void getEventNotificationValue(@RequestBody final AutonomousRequest request) {

        this.deviceManagementService.setEventNotification(request.getAutonomousStatus());

    }

    @RequestMapping(value = EVENT_NOTIFICATION_CHECK_JSON_URL, method = RequestMethod.GET)
    @ResponseBody
    public Boolean getEventNotificationState() {
        return this.deviceManagementService.getEventNotification();
    }

    @RequestMapping(value = DEVICE_CREATE_URL, method = RequestMethod.GET)
    public String showCreateDevice(final Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_DEVICE, new Device());

        return DEVICE_CREATE_VIEW;
    }

    private byte[] createRandomDeviceUid() {
        // Generate random bytes for UID
        final byte[] deviceUid = new byte[OslpEnvelope.DEVICE_ID_LENGTH];
        final Random byteGenerator = new Random();
        byteGenerator.nextBytes(deviceUid);
        // Combine manufacturer id of 2 bytes (1 is AME) and device UID of 10
        // bytes.
        return ArrayUtils.addAll(new byte[] { 0, 1 }, deviceUid);
    }

    @RequestMapping(value = DEVICE_CREATE_URL, method = RequestMethod.POST)
    public String createDevice(@ModelAttribute(MODEL_ATTRIBUTE_DEVICE) final Device created,
            final BindingResult bindingResult, final RedirectAttributes attributes) {

        if (bindingResult.hasErrors()) {
            return DEVICE_CREATE_VIEW;
        }

        created.setDeviceUid(this.createRandomDeviceUid());

        Device device;
        try {
            // Store device
            device = this.deviceManagementService.addDevice(created);
            this.addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_DEVICE_CREATED, device.getDeviceIdentification());
        } catch (final Exception e) {
            LOGGER.error("Error creating device: {}", e);
            this.setErrorFeedbackMessage(created, attributes);
        }

        return this.createRedirectViewPath(DEVICES_URL);
    }

    @RequestMapping(value = DEVICE_EDIT_URL, method = RequestMethod.GET)
    public String showEditDevice(@PathVariable final Long id, final Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_DEVICE, this.deviceManagementService.findDevice(id));
        return DEVICE_EDIT_VIEW;
    }

    @RequestMapping(value = DEVICE_EDIT_URL, method = RequestMethod.POST)
    public String editDevice(@ModelAttribute(MODEL_ATTRIBUTE_DEVICE) final Device updated, @PathVariable final Long id,
            final BindingResult bindingResult, final RedirectAttributes attributes, final Model model) {

        if (bindingResult.hasErrors()) {
            return DEVICE_EDIT_VIEW;
        }

        // Find device
        final Device deviceToUpdate = this.deviceManagementService.findDevice(id);
        if (deviceToUpdate == null) {
            return DEVICE_EDIT_VIEW;
        }

        // Update data
        deviceToUpdate.setIpAddress(updated.getIpAddress());
        deviceToUpdate.setDeviceType(updated.getDeviceType());
        deviceToUpdate.setActualLinkType(updated.getActualLinkType());
        deviceToUpdate.setTariffOn(updated.isTariffOn());
        deviceToUpdate.setProtocol(updated.getProtocol());

        // Store device
        final Device device = this.deviceManagementService.updateDevice(deviceToUpdate);
        this.addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_DEVICE_UPDATED, device.getDeviceIdentification());

        model.addAttribute(MODEL_ATTRIBUTE_DEVICE, deviceToUpdate);

        return DEVICE_EDIT_VIEW;
    }

    @RequestMapping(value = COMMAND_REGISTER_URL, method = RequestMethod.POST)
    @ResponseBody
    public String sendRegisterDeviceCommand(@RequestBody final RegisterDeviceRequest request) {
        // Find device
        final Device device = this.deviceManagementService.findDevice(request.getDeviceId());
        final DeviceMessageStatus status = this.registerDevice.sendRegisterDeviceCommand(request.getDeviceId(),
                request.getHasSchedule());
        if (status == DeviceMessageStatus.OK) {
            return this.getFeedbackMessage(FEEDBACK_MESSAGE_KEY_DEVICE_REGISTERED, device.getDeviceIdentification(),
                    this.registerDevice.getCurrentTime());
        } else if (status == DeviceMessageStatus.FAILURE) {
            return this.getFeedbackMessage(FEEDBACK_MESSAGE_KEY_DEVICE_ERROR, device.getDeviceIdentification(),
                    this.registerDevice.getErrorMessage());
        } else {
            return FAILURE_URL;
        }
    }

    @RequestMapping(value = COMMAND_REGISTER_CONFIRM_URL, method = RequestMethod.POST)
    @ResponseBody
    public String sendConfirmDeviceRegistrationCommand(@RequestBody final ConfirmDeviceRegistrationRequest request) {

        // Find device
        final Device device = this.deviceManagementService.findDevice(request.getDeviceId());
        final DeviceMessageStatus status = this.registerDevice.sendConfirmDeviceRegistrationCommand(request
                .getDeviceId());
        if (status == DeviceMessageStatus.OK) {
            return this.getFeedbackMessage(FEEDBACK_MESSAGE_KEY_DEVICE_REGISTERED_CONFIRM,
                    device.getDeviceIdentification());
        } else if (status == DeviceMessageStatus.FAILURE) {
            return this.getFeedbackMessage(FEEDBACK_MESSAGE_KEY_DEVICE_ERROR, device.getDeviceIdentification(),
                    this.registerDevice.getErrorMessage());
        } else {
            return FAILURE_URL;
        }

    }

    @RequestMapping(value = COMMAND_SENDNOTIFICATION_URL, method = RequestMethod.POST)
    @ResponseBody
    public String sendEventNotificationCommandAll(@RequestBody final SendEventNotificationRequest request) {

        // Find device
        final Device device = this.deviceManagementService.findDevice(request.getDeviceId());
        final DeviceMessageStatus status = this.registerDevice.sendEventNotificationCommand(request.getDeviceId(),
                request.getEvent(), request.getDescription(), request.getIndex(), request.getHasTimestamp());
        if (status == DeviceMessageStatus.OK) {
            return this.getFeedbackMessage(FEEDBACK_MESSAGE_KEY_EVENTNOTIFICATION_SENT,
                    device.getDeviceIdentification());
        } else if (status == DeviceMessageStatus.FAILURE) {
            return this.getFeedbackMessage(FEEDBACK_MESSAGE_KEY_DEVICE_ERROR, device.getDeviceIdentification(),
                    this.registerDevice.getErrorMessage());
        } else {
            return FAILURE_URL;
        }
    }

    @RequestMapping(value = DEVICES_JSON_URL, method = RequestMethod.GET)
    @ResponseBody
    public List<Device> getLightStates() {
        return this.deviceManagementService.findAllDevices();
    }

    @RequestMapping(value = COMMAND_GET_SEQUENCE_NUMBER_URL, method = RequestMethod.POST)
    @ResponseBody
    public Integer getSequenceNumber(@RequestBody final GetSequenceNumberRequest getSequenceNumberRequest) {
        final Long deviceId = getSequenceNumberRequest.getDeviceId();

        final Device device = this.deviceManagementService.findDevice(deviceId);
        if (device != null) {
            return device.getSequenceNumber();
        } else {
            return -1;
        }
    }

    @RequestMapping(value = COMMAND_SET_SEQUENCE_NUMBER_URL, method = RequestMethod.POST)
    @ResponseBody
    public Integer setSequenceNumber(@RequestBody final SetSequenceNumberRequest setSequenceNumberRequest) {
        final Long deviceId = setSequenceNumberRequest.getDeviceId();
        final Integer sequenceNumber = setSequenceNumberRequest.getSequenceNumber();

        Device device = this.deviceManagementService.findDevice(deviceId);
        if (device != null) {
            device.setSequenceNumber(sequenceNumber);
            device = this.deviceManagementService.updateDevice(device);
            return device.getSequenceNumber();
        } else {
            return -1;
        }
    }

    /**
     * Set error messages for device.
     */
    private void setErrorFeedbackMessage(final Device device, final RedirectAttributes attributes) {
        if (device.getDeviceIdentification() == null) {
            this.addErrorMessage(attributes, FEEDBACK_MESSAGE_KEY_DEVICE_ERROR_CREATE_DEVICE_NO_ID);
        } else if (device.getDeviceType() == null) {
            this.addErrorMessage(attributes, FEEDBACK_MESSAGE_KEY_DEVICE_ERROR_CREATE_DEVICE_NO_DEVICE_TYPE,
                    device.getDeviceIdentification());
        } else {
            this.addErrorMessage(attributes, FEEDBACK_MESSAGE_KEY_DEVICE_ERROR_CREATE_DEVICE_DUPLICATE,
                    device.getDeviceIdentification());
        }
    }
}
