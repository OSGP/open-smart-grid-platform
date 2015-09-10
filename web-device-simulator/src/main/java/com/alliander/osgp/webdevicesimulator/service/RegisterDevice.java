/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.webdevicesimulator.service;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.PrivateKey;
import java.text.MessageFormat;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.oslp.Oslp.DeviceType;
import com.alliander.osgp.oslp.Oslp.EventNotification;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.alliander.osgp.webdevicesimulator.application.services.DeviceManagementService;
import com.alliander.osgp.webdevicesimulator.domain.entities.Device;
import com.alliander.osgp.webdevicesimulator.domain.entities.DeviceMessageStatus;
import com.alliander.osgp.webdevicesimulator.domain.entities.OslpLogItem;
import com.alliander.osgp.webdevicesimulator.domain.repositories.OslpLogItemRepository;
import com.alliander.osgp.webdevicesimulator.exceptions.DeviceSimulatorException;
import com.alliander.osgp.webdevicesimulator.service.OslpChannelHandler.OutOfSequenceEvent;
import com.google.protobuf.ByteString;

public class RegisterDevice {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterDevice.class);

    protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_REGISTERED = "feedback.message.device.registered";
    protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_REGISTERED_CONFIRM = "feedback.message.device.registered.confirm";
    protected static final String FEEDBACK_MESSAGE_KEY_DEVICE_ERROR = "feedback.message.device.error";

    @Autowired
    private DeviceManagementService deviceManagementService;

    @Autowired
    private OslpLogItemRepository oslpLogItemRepository;

    @Resource
    private OslpChannelHandler oslpChannelHandler;

    @Resource
    private String oslpAddressServer;

    @Resource
    private int oslpPortClient;

    @Resource
    private String oslpSignatureProvider;

    @Resource
    private PrivateKey privateKey;

    @Resource
    private String oslpSignature;

    private String currentTime;

    private String errorMessage;

    public DeviceMessageStatus sendRegisterDeviceCommand(final long deviceId, final Boolean hasSchedule) {

        // Find device
        Device device = this.deviceManagementService.findDevice(deviceId);
        if (device == null) {
            // Set the DeviceMessageStatus NOT_FOUND as the Device is NOT_FOUND
            return DeviceMessageStatus.NOT_FOUND;
        }

        this.errorMessage = "";

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
            device = this.deviceManagementService.updateDevice(device);

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
                                                            device.getDeviceType().isEmpty() ? DeviceType.PSLD
                                                                    : DeviceType.valueOf(device.getDeviceType()))
                                                                    .setHasSchedule(hasSchedule).setRandomDevice(randomDevice)).build())
                                                                    .build();

            // Write request log
            OslpLogItem logItem = new OslpLogItem(olspRequest.getDeviceId(), device.getDeviceIdentification(), false,
                    olspRequest.getPayloadMessage());
            this.oslpLogItemRepository.save(logItem);

            // Send registration message
            final OslpEnvelope response = this.oslpChannelHandler.send(new InetSocketAddress(this.oslpAddressServer,
                    this.oslpPortClient), olspRequest, device.getDeviceIdentification());
            LOGGER.debug("Controller Received Send Register Device Command: " + response.getPayloadMessage().toString());

            // Write request log
            logItem = new OslpLogItem(response.getDeviceId(), device.getDeviceIdentification(), false,
                    response.getPayloadMessage());
            this.oslpLogItemRepository.save(logItem);

            this.currentTime = response.getPayloadMessage().getRegisterDeviceResponse().getCurrentTime();

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

            // Set the DeviceMessageStatus OK as the registration is success
            return DeviceMessageStatus.OK;
        } catch (final UnknownHostException ex) {
            LOGGER.error("incorrect IP address format", ex);
        } catch (final Exception e) {
            LOGGER.error("register device exception", e);
            this.errorMessage = e.getMessage();
            // Set the DeviceMessageStatus FAILURE as the registration is NOT
            // success
            return DeviceMessageStatus.FAILURE;
        }

        return DeviceMessageStatus.NOT_FOUND;
    }

    public DeviceMessageStatus sendConfirmDeviceRegistrationCommand(final long deviceId) {

        // Find device
        Device device = this.deviceManagementService.findDevice(deviceId);
        if (device == null) {
            // Set the DeviceMessageStatus NOT_FOUND as the device is NOT_FOUND
            return DeviceMessageStatus.NOT_FOUND;
        }
        this.errorMessage = "";
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
                    this.oslpPortClient), olspRequest, device.getDeviceIdentification());
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

            // Set the DeviceMessageStatus OK as the confirm registration is
            // success
            return DeviceMessageStatus.OK;
        } catch (final Exception e) {
            LOGGER.error("confirm device registration exception", e);
            this.errorMessage = e.getMessage();

            // Set the DeviceMessageStatus FAILURE as the confirm registration
            // is NOT success
            return DeviceMessageStatus.FAILURE;

        }
    }

    public DeviceMessageStatus sendEventNotificationCommand(final Long id, final Integer event,
            final String description, final Integer index) {
        // Find device
        Device device = this.deviceManagementService.findDevice(id);
        if (device == null) {

            // Set the DeviceMessageStatus NOT_FOUND as the device is NOT_FOUND
            return DeviceMessageStatus.NOT_FOUND;
        }
        this.errorMessage = "";

        try {
            // Set index when provided in request.
            Integer idx;
            if (index == null) {
                idx = 0;
            } else {
                idx = index;
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
                                                            ByteString.copyFrom(new byte[] { idx
                                                                    .byteValue() })))).build()).build();

            // Write request log
            OslpLogItem logItem = new OslpLogItem(request.getDeviceId(), device.getDeviceIdentification(), false,
                    request.getPayloadMessage());
            this.oslpLogItemRepository.save(logItem);

            // Send registration message
            final OslpEnvelope response = this.oslpChannelHandler.send(new InetSocketAddress(this.oslpAddressServer,
                    this.oslpPortClient), request, device.getDeviceIdentification());
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
            device = this.deviceManagementService.updateDevice(device);

            // Set the DeviceMessageStatus OK as the SendEvent is Success.
            return DeviceMessageStatus.OK;
        } catch (final Exception e) {
            LOGGER.error("send event notification exception", e);
            this.errorMessage = e.getMessage();

            // Set the DeviceMessageStatus FAILURE as the SendEvent is NOT
            // Success.
            return DeviceMessageStatus.FAILURE;

        }
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

    public OslpEnvelope.Builder createEnvelopeBuilder(final String deviceUid, final Integer sequenceNumber) {
        final byte[] sequenceNumberBytes = new byte[2];
        sequenceNumberBytes[0] = (byte) (sequenceNumber >>> 8);
        sequenceNumberBytes[1] = (byte) (sequenceNumber >>> 0);

        return new OslpEnvelope.Builder().withSignature(this.oslpSignature).withProvider(this.oslpSignatureProvider)
                .withPrimaryKey(this.privateKey).withDeviceId(Base64.decodeBase64(deviceUid))
                .withSequenceNumber(sequenceNumberBytes);
    }

    /**
     * Check for RegisterDevice, ConfirmRegisterDevice and
     * SendEventNotification.
     */
    public void checkSequenceNumber(final byte[] bytes, final Integer sequenceNumber) throws DeviceSimulatorException {
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

        final Integer num = ((bytes[0] & 0xFF) << 8) | ((bytes[1] & 0xFF));

        if (sequenceNumber - num != 0) {
            throw new DeviceSimulatorException(MessageFormat.format(
                    "sequence number incorrect - expected sequence number: {0} actual sequence number: {1}",
                    sequenceNumber, num));
        }
    }

    /**
     * Check for RegisterDevice.
     */
    public void checkRandomDeviceAndRandomPlatform(final Integer randomDevice, final Integer responseRandomDevice,
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

    public String getCurrentTime() {
        return this.currentTime;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

}
