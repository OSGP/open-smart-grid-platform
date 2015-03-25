package com.alliander.osgp.webdevicesimulator.web.controller;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.PrivateKey;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
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

import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.oslp.Oslp.DeviceType;
import com.alliander.osgp.oslp.Oslp.EventNotification;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.alliander.osgp.webdevicesimulator.application.services.DeviceManagementService;
import com.alliander.osgp.webdevicesimulator.domain.entities.Device;
import com.alliander.osgp.webdevicesimulator.domain.entities.OslpLogItem;
import com.alliander.osgp.webdevicesimulator.domain.repositories.OslpLogItemRepository;
import com.alliander.osgp.webdevicesimulator.exceptions.DeviceSimulatorException;
import com.alliander.osgp.webdevicesimulator.service.OslpChannelHandler;
import com.alliander.osgp.webdevicesimulator.service.OslpChannelHandler.OutOfSequenceEvent;
import com.google.protobuf.ByteString;

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

    protected static final String COMMAND_REGISTER_URL = "/devices/commands/register";
    protected static final String COMMAND_REGISTER_CONFIRM_URL = "/devices/commands/register/confirm";
    protected static final String COMMAND_SENDNOTIFICATION_URL = "/devices/commands/sendnotification";
    protected static final String COMMAND_GET_SEQUENCE_NUMBER_URL = "/devices/commands/get-sequence-number";
    protected static final String COMMAND_SET_SEQUENCE_NUMBER_URL = "/devices/commands/set-sequence-number";
    protected static final String DEVICES_JSON_URL = "/devices/json";

    protected static final String MODEL_ATTRIBUTE_DEVICES = "devices";
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

    @Resource
    private String oslpAddressServer;

    @Resource
    private int oslpPortClient;

    @Resource
    private PrivateKey privateKey;

    @Resource
    private OslpChannelHandler oslpChannelHandler;

    @Resource
    private String oslpSignature;

    @Resource
    private String oslpSignatureProvider;

    @Autowired
    private OslpLogItemRepository oslpLogItemRepository;

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

        Device device = null;
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
        Device device = this.deviceManagementService.findDevice(request.getDeviceId());
        if (device == null) {
            return FAILURE_URL;
        }

        try {
            // Create new deviceUID. This is a temporary fix for devices that
            // have been created in the past (with a 10 byte deviceUID).
            // Alternative would be to 1) change the deviceUID in the database
            // or 2) delete all devices and create new devices (with a 12 byte
            // deviceUID).
            // There seems no problem with creating a new deviceUID for every
            // register of the device.
            // However, NOTE: THIS BEHAVIOUR IS NOT EQUAL TO THE REAL SSLD/PSLD.
            device.setDeviceUid(this.createRandomDeviceUid());

            // Generate random sequence number and random device number
            final Integer sequenceNumber = device.doGenerateRandomNumber();
            final Integer randomDevice = device.doGenerateRandomNumber();

            // Create registration message
            final OslpEnvelope olspRequest = this
                    .createEnvelopeBuilder(device.getDeviceUid(), sequenceNumber)
                    .withPayloadMessage(
                            Message.newBuilder()
                                    .setRegisterDeviceRequest(
                                            Oslp.RegisterDeviceRequest
                                                    .newBuilder()
                                                    .setDeviceIdentification(device.getDeviceIdentification())
                                                    .setIpAddress(
                                                            ByteString.copyFrom(InetAddress.getByName(
                                                                    device.getIpAddress()).getAddress()))
                                                    .setDeviceType(
                                                            request.getDeviceType().isEmpty() ? DeviceType.PSLD
                                                                    : DeviceType.valueOf(request.getDeviceType()))
                                                    .setHasSchedule(request.getHasSchedule())
                                                    .setRandomDevice(randomDevice)).build()).build();

            // Write request log
            OslpLogItem logItem = new OslpLogItem(olspRequest.getDeviceId(), device.getDeviceIdentification(), false,
                    olspRequest.getPayloadMessage());
            this.oslpLogItemRepository.save(logItem);

            // Send registration message
            final OslpEnvelope response = this.oslpChannelHandler.send(new InetSocketAddress(this.oslpAddressServer,
                    this.oslpPortClient), olspRequest);
            LOGGER.debug("Controller Received Send Register Device Command: " + response.getPayloadMessage().toString());

            // Write request log
            logItem = new OslpLogItem(response.getDeviceId(), device.getDeviceIdentification(), false,
                    response.getPayloadMessage());
            this.oslpLogItemRepository.save(logItem);

            final String currentTime = response.getPayloadMessage().getRegisterDeviceResponse().getCurrentTime();

            // Get the sequence number from the response envelope and check it
            this.checkSequenceNumber(response.getSequenceNumber(), sequenceNumber);

            // Get the two random numbers and check them both
            this.checkRandomDeviceAndRandomPlatform(randomDevice, response.getPayloadMessage()
                    .getRegisterDeviceResponse().getRandomDevice(), response.getPayloadMessage()
                    .getRegisterDeviceResponse().getRandomPlatform());

            // Set the sequence number and persist it
            device.setSequenceNumber(sequenceNumber);

            // Get the two random numbers and persist them both
            device.setRandomDevice(response.getPayloadMessage().getRegisterDeviceResponse().getRandomDevice());
            device.setRandomPlatform(response.getPayloadMessage().getRegisterDeviceResponse().getRandomPlatform());

            // Save the entity
            device = this.deviceManagementService.updateDevice(device);

            return this.getFeedbackMessage(FEEDBACK_MESSAGE_KEY_DEVICE_REGISTERED, device.getDeviceIdentification(),
                    currentTime);
        } catch (final UnknownHostException ex) {
            LOGGER.error("incorrect IP address format", ex);
        } catch (final Exception e) {
            LOGGER.error("register device exception", e);
            return this.getFeedbackMessage(FEEDBACK_MESSAGE_KEY_DEVICE_ERROR, device.getDeviceIdentification(),
                    e.getMessage());
        }

        return FAILURE_URL;
    }

    @RequestMapping(value = COMMAND_REGISTER_CONFIRM_URL, method = RequestMethod.POST)
    @ResponseBody
    public String sendConfirmDeviceRegistrationCommand(@RequestBody final ConfirmDeviceRegistrationRequest request) {
        // Find device
        Device device = this.deviceManagementService.findDevice(request.getDeviceId());
        if (device == null) {
            return FAILURE_URL;
        }

        try {
            final Integer sequenceNumber = device.doGetNextSequence();

            // Create registration confirm message
            final OslpEnvelope olspRequest = this
                    .createEnvelopeBuilder(device.getDeviceUid(), sequenceNumber)
                    .withPayloadMessage(
                            Message.newBuilder()
                                    .setConfirmRegisterDeviceRequest(
                                            Oslp.ConfirmRegisterDeviceRequest.newBuilder()
                                                    .setRandomDevice(device.getRandomDevice())
                                                    .setRandomPlatform(device.getRandomPlatform())).build()).build();

            // Send registration confirm message
            final OslpEnvelope response = this.oslpChannelHandler.send(new InetSocketAddress(this.oslpAddressServer,
                    this.oslpPortClient), olspRequest);
            LOGGER.debug("Controller Received Send Confirm Device Registration Command: "
                    + response.getPayloadMessage().toString());

            // Get the sequence number from the response envelope and check it
            this.checkSequenceNumber(response.getSequenceNumber(), sequenceNumber);

            // Get the two random numbers and check them both
            this.checkRandomDeviceAndRandomPlatform(device.getRandomDevice(), response.getPayloadMessage()
                    .getConfirmRegisterDeviceResponse().getRandomDevice(), device.getRandomPlatform(), response
                    .getPayloadMessage().getConfirmRegisterDeviceResponse().getRandomPlatform());

            // Success
            device.setSequenceNumber(sequenceNumber);
            device = this.deviceManagementService.updateDevice(device);

            // Check if there has been an out of sequence security event
            OutOfSequenceEvent outOfSequenceEvent = this.oslpChannelHandler.hasOutOfSequenceEventForDevice(device
                    .getId());
            while (outOfSequenceEvent != null) {
                // An event has occurred, send
                // SECURITY_EVENTS_OUT_OF_SEQUENCE_VALUE event notification
                this.sendEventNotificationCommand(outOfSequenceEvent.getDeviceId(),
                        Oslp.Event.SECURITY_EVENTS_OUT_OF_SEQUENCE_VALUE,
                        "out of sequence event occurred at time stamp: " + outOfSequenceEvent.getTimestamp().toString()
                                + " for request: " + outOfSequenceEvent.getRequest(), null);

                // Check if there has been another event, this will return null
                // if no more events are present in the list
                outOfSequenceEvent = this.oslpChannelHandler.hasOutOfSequenceEventForDevice(device.getId());
            }

            return this.getFeedbackMessage(FEEDBACK_MESSAGE_KEY_DEVICE_REGISTERED_CONFIRM,
                    device.getDeviceIdentification());
        } catch (final Exception e) {
            LOGGER.error("confirm device registration exception", e);
            return this.getFeedbackMessage(FEEDBACK_MESSAGE_KEY_DEVICE_ERROR, device.getDeviceIdentification(),
                    e.getMessage());
        }
    }

    @RequestMapping(value = COMMAND_SENDNOTIFICATION_URL, method = RequestMethod.POST)
    @ResponseBody
    public String sendEventNotificationCommandAll(@RequestBody final SendEventNotificationRequest request) {
        return this.sendEventNotificationCommand(request.getDeviceId(), request.getEvent(), request.getDescription(),
                request.getIndex());
    }

    private String sendEventNotificationCommand(final Long id, final Integer event, final String description,
            Integer index) {
        // Find device
        final Device device = this.deviceManagementService.findDevice(id);
        if (device == null) {
            return FAILURE_URL;
        }

        try {
            // Set index when provided in request.
            if (index == null) {
                index = 0;
            }

            final int sequenceNumber = device.doGetNextSequence();

            // Create registration message (for now with 1 event)
            final OslpEnvelope request = this
                    .createEnvelopeBuilder(device.getDeviceUid(), sequenceNumber)
                    .withPayloadMessage(
                            Message.newBuilder()
                                    .setEventNotificationRequest(
                                            Oslp.EventNotificationRequest.newBuilder()
                                                    .addNotifications(
                                                            EventNotification
                                                                    .newBuilder()
                                                                    .setEvent(Oslp.Event.valueOf(event))
                                                                    .setDescription(
                                                                            description == null ? "" : description)
                                                                    .setIndex(
                                                                            ByteString.copyFrom(new byte[] { index
                                                                                    .byteValue() })))).build()).build();

            // Write request log
            OslpLogItem logItem = new OslpLogItem(request.getDeviceId(), device.getDeviceIdentification(), false,
                    request.getPayloadMessage());
            this.oslpLogItemRepository.save(logItem);

            // Send registration message
            final OslpEnvelope response = this.oslpChannelHandler.send(new InetSocketAddress(this.oslpAddressServer,
                    this.oslpPortClient), request);
            LOGGER.debug("Controller Received Send Event Notification Command: "
                    + response.getPayloadMessage().toString());

            // Write request log
            logItem = new OslpLogItem(response.getDeviceId(), device.getDeviceIdentification(), false,
                    response.getPayloadMessage());
            this.oslpLogItemRepository.save(logItem);

            // Get the sequence number from the response envelope and check it
            this.checkSequenceNumber(response.getSequenceNumber(), sequenceNumber);

            // Success
            device.setSequenceNumber(sequenceNumber);
            this.deviceManagementService.updateDevice(device);

            return this.getFeedbackMessage(FEEDBACK_MESSAGE_KEY_EVENTNOTIFICATION_SENT,
                    device.getDeviceIdentification());
        } catch (final Exception e) {
            LOGGER.error("send event notification exception", e);
            return this.getFeedbackMessage(FEEDBACK_MESSAGE_KEY_DEVICE_ERROR, device.getDeviceIdentification(),
                    e.getMessage());
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
     * 
     * @param device
     * @param attributes
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

    /**
     * Check for RegisterDevice, ConfirmRegisterDevice and
     * SendEventNotification.
     */
    private void checkSequenceNumber(final byte[] bytes, final Integer sequenceNumber) throws DeviceSimulatorException {
        if (bytes == null) {
            throw new DeviceSimulatorException("sequence number byte array is null");
        }
        if (bytes.length != 2) {
            throw new DeviceSimulatorException(MessageFormat.format(
                    "sequence number byte array incorrect length - expected length: {0} actual length: {1}", 2,
                    bytes.length));
        }
        if (sequenceNumber == null) {
            throw new DeviceSimulatorException("sequence number Integer is null");
        }

        final Integer num = ((bytes[0] & 0xFF) << 8) | ((bytes[1] & 0xFF) << 0);

        if (sequenceNumber - num != 0) {
            throw new DeviceSimulatorException(MessageFormat.format(
                    "sequence number incorrect - expected sequence number: {0} actual sequence number: {1}",
                    sequenceNumber, num));
        }
    }

    /**
     * Check for RegisterDevice.
     */
    private void checkRandomDeviceAndRandomPlatform(final Integer randomDevice, final Integer responseRandomDevice,
            final Integer responseRandomPlatform) throws DeviceSimulatorException {
        if (responseRandomDevice == null) {
            throw new DeviceSimulatorException("random device Integer is null");
        }
        if (randomDevice - responseRandomDevice != 0) {
            throw new DeviceSimulatorException(
                    MessageFormat
                            .format("random device number incorrect - expected random device number: {0} actual random device number: {1}",
                                    randomDevice, responseRandomDevice));
        }
        if (responseRandomPlatform == null) {
            throw new DeviceSimulatorException("random platform Integer is null");
        }
    }

    /**
     * Check for ConfirmRegisterDevice.
     */
    private void checkRandomDeviceAndRandomPlatform(final Integer randomDevice, final Integer responseRandomDevice,
            final Integer randomPlatform, final Integer responseRandomPlatform) throws DeviceSimulatorException {
        this.checkRandomDeviceAndRandomPlatform(randomDevice, responseRandomDevice, responseRandomPlatform);

        if (randomPlatform - responseRandomPlatform != 0) {
            throw new DeviceSimulatorException(
                    MessageFormat
                            .format("random platform number incorrect - expected random platform number: {0} actual random platform number: {1}",
                                    randomPlatform, responseRandomPlatform));
        }
    }

    private OslpEnvelope.Builder createEnvelopeBuilder(final String deviceUid, final Integer sequenceNumber) {
        final byte[] sequenceNumberBytes = new byte[2];
        sequenceNumberBytes[0] = (byte) (sequenceNumber >>> 8);
        sequenceNumberBytes[1] = (byte) (sequenceNumber >>> 0);

        return new OslpEnvelope.Builder().withSignature(this.oslpSignature).withProvider(this.oslpSignatureProvider)
                .withPrimaryKey(this.privateKey).withDeviceId(Base64.decodeBase64(deviceUid))
                .withSequenceNumber(sequenceNumberBytes);
    }
}
