/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.webdevicesimulator.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.PrivateKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.codec.binary.Base64;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.oslp.Oslp.ConfirmRegisterDeviceResponse;
import com.alliander.osgp.oslp.Oslp.DaliConfiguration;
import com.alliander.osgp.oslp.Oslp.GetActualPowerUsageRequest;
import com.alliander.osgp.oslp.Oslp.GetActualPowerUsageResponse;
import com.alliander.osgp.oslp.Oslp.GetFirmwareVersionResponse;
import com.alliander.osgp.oslp.Oslp.GetPowerUsageHistoryRequest;
import com.alliander.osgp.oslp.Oslp.GetPowerUsageHistoryResponse;
import com.alliander.osgp.oslp.Oslp.GetStatusResponse;
import com.alliander.osgp.oslp.Oslp.HistoryTermType;
import com.alliander.osgp.oslp.Oslp.IndexAddressMap;
import com.alliander.osgp.oslp.Oslp.LightValue;
import com.alliander.osgp.oslp.Oslp.LongTermIntervalType;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.Oslp.MeterType;
import com.alliander.osgp.oslp.Oslp.PageInfo;
import com.alliander.osgp.oslp.Oslp.PowerUsageData;
import com.alliander.osgp.oslp.Oslp.PsldData;
import com.alliander.osgp.oslp.Oslp.RelayConfiguration;
import com.alliander.osgp.oslp.Oslp.RelayType;
import com.alliander.osgp.oslp.Oslp.SetEventNotificationsRequest;
import com.alliander.osgp.oslp.Oslp.SetEventNotificationsResponse;
import com.alliander.osgp.oslp.Oslp.SetLightRequest;
import com.alliander.osgp.oslp.Oslp.SetLightResponse;
import com.alliander.osgp.oslp.Oslp.SetScheduleRequest;
import com.alliander.osgp.oslp.Oslp.SetScheduleResponse;
import com.alliander.osgp.oslp.Oslp.SsldData;
import com.alliander.osgp.oslp.Oslp.StartSelfTestResponse;
import com.alliander.osgp.oslp.Oslp.StopSelfTestResponse;
import com.alliander.osgp.oslp.Oslp.UpdateFirmwareRequest;
import com.alliander.osgp.oslp.Oslp.UpdateFirmwareResponse;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.alliander.osgp.oslp.OslpUtils;
import com.alliander.osgp.webdevicesimulator.application.services.DeviceManagementService;
import com.alliander.osgp.webdevicesimulator.domain.entities.Device;
import com.alliander.osgp.webdevicesimulator.domain.entities.DeviceMessageStatus;
import com.alliander.osgp.webdevicesimulator.domain.entities.DeviceOutputSetting;
import com.alliander.osgp.webdevicesimulator.domain.entities.OslpLogItem;
import com.alliander.osgp.webdevicesimulator.domain.repositories.OslpLogItemRepository;
import com.alliander.osgp.webdevicesimulator.domain.valueobjects.EventNotificationToBeSent;
import com.alliander.osgp.webdevicesimulator.domain.valueobjects.LightType;
import com.alliander.osgp.webdevicesimulator.domain.valueobjects.LinkType;
import com.alliander.osgp.webdevicesimulator.domain.valueobjects.OutputType;
import com.alliander.osgp.webdevicesimulator.domain.valueobjects.ProtocolType;
import com.alliander.osgp.webdevicesimulator.exceptions.DeviceSimulatorException;
import com.google.protobuf.ByteString;

public class OslpChannelHandler extends SimpleChannelHandler {

    private static DateTimeZone localTimeZone = DateTimeZone.forID("Europe/Paris");

    private static final Logger LOGGER = LoggerFactory.getLogger(OslpChannelHandler.class);

    private static class Callback {

        private final CountDownLatch latch = new CountDownLatch(1);

        private OslpEnvelope response;

        private final int connectionTimeout;

        Callback(final int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        OslpEnvelope get(final String deviceIdentification) throws IOException, DeviceSimulatorException {
            try {
                if (!this.latch.await(this.connectionTimeout, TimeUnit.MILLISECONDS)) {
                    LOGGER.warn("Failed to receive response from device {} within timelimit {} ms",
                            deviceIdentification, this.connectionTimeout);
                    throw new IOException(
                            "Failed to receive response within timelimit " + this.connectionTimeout + " ms");
                }

                LOGGER.info("Response received within {} ms", this.connectionTimeout);
            } catch (final InterruptedException e) {
                throw new DeviceSimulatorException("InterruptedException", e);
            }
            return this.response;
        }

        void handle(final OslpEnvelope response) {
            this.response = response;
            this.latch.countDown();
        }
    }

    @Autowired
    private OslpLogItemRepository oslpLogItemRepository;

    @Autowired
    private PrivateKey privateKey;

    @Autowired
    private String oslpSignatureProvider;

    @Autowired
    private String oslpSignature;

    @Autowired
    private int connectionTimeout;

    @Autowired
    private String firmwareVersion;

    @Autowired
    private String configurationIpConfigFixedIpAddress;

    @Autowired
    private String configurationIpConfigNetmask;

    @Autowired
    private String configurationIpConfigGateway;

    @Autowired
    private String configurationOsgpIpAddress;

    @Autowired
    private Integer configurationOsgpPortNumber;

    @Autowired
    private String statusInternalIpAddress;

    @Autowired
    private ClientBootstrap bootstrap;

    @Autowired
    private DeviceManagementService deviceManagementService;

    @Autowired
    private RegisterDevice registerDevice;

    private final Lock lock = new ReentrantLock();

    private final ConcurrentMap<Integer, Callback> callbacks = new ConcurrentHashMap<>();

    @Autowired
    private Integer sequenceNumberWindow;

    @Autowired
    private Integer sequenceNumberMaximum;

    private final List<OutOfSequenceEvent> outOfSequenceList = new ArrayList<>();

    @Autowired
    private Long responseDelayTime;

    @Autowired
    private Long reponseDelayRandomRange;

    private final Random random = new Random();

    private static final int CUMALATIVE_BURNING_MINUTES = 600;
    private static int INITIAL_BURNING_MINUTES = 100000;

    public static class OutOfSequenceEvent {
        private final Long deviceId;
        private final String request;
        private final DateTime timestamp;

        public OutOfSequenceEvent(final Long deviceId, final String request, final DateTime timestamp) {
            this.deviceId = deviceId;
            this.request = request;
            this.timestamp = timestamp;
        }

        public Long getDeviceId() {
            return this.deviceId;
        }

        public String getRequest() {
            return this.request;
        }

        public DateTime getTimestamp() {
            return this.timestamp;
        }
    }

    /**
     * Get an OutOfSequenceEvent for given device id. The OutOfSequenceEvent
     * instance will be removed from the list, before the instance is returned.
     *
     * @param deviceId
     *            The id of the device.
     *
     * @return An OutOfSequenceEvent instance, or null.
     */
    public OutOfSequenceEvent hasOutOfSequenceEventForDevice(final Long deviceId) {
        for (final OutOfSequenceEvent outOfSequenceEvent : this.outOfSequenceList) {
            if (outOfSequenceEvent.getDeviceId().equals(deviceId)) {
                this.outOfSequenceList.remove(outOfSequenceEvent);
                return outOfSequenceEvent;
            }
        }
        return null;
    }

    public void setPrivateKey(final PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public void setProvider(final String provider) {
        this.oslpSignatureProvider = provider;
    }

    public void setSignature(final String signature) {
        this.oslpSignature = signature;
    }

    public void setOslpLogItemRepository(final OslpLogItemRepository oslpLogItemRepository) {
        this.oslpLogItemRepository = oslpLogItemRepository;
    }

    public void setDeviceManagementService(final DeviceManagementService deviceManagementService) {
        this.deviceManagementService = deviceManagementService;
    }

    public ClientBootstrap getBootstrap() {
        return this.bootstrap;
    }

    public void setBootstrap(final ClientBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        final OslpEnvelope message = (OslpEnvelope) e.getMessage();

        this.oslpLogItemRepository.save(new OslpLogItem(message.getDeviceId(),
                this.getDeviceIdentificationFromMessage(message.getPayloadMessage()), true,
                message.getPayloadMessage()));

        if (message.isValid()) {
            if (this.isOslpResponse(message)) {
                LOGGER.info("Received OSLP Response (before callback): {}", message.getPayloadMessage());

                // Lookup correct callback and call handle method
                final Integer channelId = e.getChannel().getId();
                final Callback callback = this.callbacks.remove(channelId);
                if (callback == null) {
                    LOGGER.warn("Callback for channel {} does not longer exist, dropping response.", channelId);
                    return;
                }

                callback.handle(message);
            } else {
                LOGGER.info("Received OSLP Request: {}", message.getPayloadMessage().toString().split(" ")[0]);

                // Sequence number logic
                byte[] sequenceNumber = message.getSequenceNumber();
                Integer number = -1;
                if (!(message.getPayloadMessage().hasRegisterDeviceRequest()
                        || message.getPayloadMessage().hasConfirmRegisterDeviceRequest())) {
                    // Convert byte array to integer
                    number = this.convertByteArrayToInteger(sequenceNumber);

                    // Wrap the number back to 0 if the limit is reached or
                    // increment
                    if (number >= this.sequenceNumberMaximum) {
                        LOGGER.info(
                                "wrapping sequence number back to 0, current sequence number: {} next sequence number: 0",
                                number);
                        number = 0;
                    } else {
                        LOGGER.info(
                                "incrementing sequence number, current sequence number: {} next sequence number: {}",
                                number, number + 1);
                        number += 1;
                    }

                    // Convert integer back to byte array
                    sequenceNumber = this.convertIntegerToByteArray(number);
                }

                final byte[] deviceId = message.getDeviceId();

                // Build the OslpEnvelope with the incremented sequence number.
                final OslpEnvelope.Builder responseBuilder = new OslpEnvelope.Builder()
                        .withSignature(this.oslpSignature).withProvider(this.oslpSignatureProvider)
                        .withPrimaryKey(this.privateKey).withDeviceId(deviceId).withSequenceNumber(sequenceNumber);

                // Pass the incremented sequence number to the handleRequest()
                // function for checking.
                responseBuilder.withPayloadMessage(this.handleRequest(message, number));
                final OslpEnvelope response = responseBuilder.build();

                this.oslpLogItemRepository.save(new OslpLogItem(response.getDeviceId(),
                        this.getDeviceIdentificationFromMessage(response.getPayloadMessage()), false,
                        response.getPayloadMessage()));

                LOGGER.info("sending OSLP response with sequence number: {}",
                        this.convertByteArrayToInteger(response.getSequenceNumber()));
                e.getChannel().write(response);
                LOGGER.info("Send OSLP Response: {}", response.getPayloadMessage().toString().split(" ")[0]);
            }
        } else {
            LOGGER.warn("Received message wasn't properly secured.");
        }
    }

    @Override
    public void channelOpen(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        LOGGER.info("Channel {} opened", e.getChannel().getId());
        super.channelOpen(ctx, e);
    }

    @Override
    public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        LOGGER.info("Channel {} disconnected", e.getChannel().getId());
        super.channelDisconnected(ctx, e);
    }

    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        LOGGER.info("Channel {} closed", e.getChannel().getId());
        super.channelClosed(ctx, e);
    }

    @Override
    public void channelUnbound(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        LOGGER.info("Channel {} unbound", e.getChannel().getId());
        super.channelUnbound(ctx, e);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) {
        if (this.isConnectionReset(e.getCause())) {
            LOGGER.info("Connection was (as expected) reset by the device.");
        } else {
            LOGGER.warn("Unexpected exception from downstream.", e.getCause());
        }

        e.getChannel().close();
    }

    private boolean isConnectionReset(final Throwable e) {
        return e instanceof IOException && e.getMessage() != null
                && e.getMessage().contains("Connection reset by peer");
    }

    public OslpEnvelope send(final InetSocketAddress address, final OslpEnvelope request,
            final String deviceIdentification) throws IOException, DeviceSimulatorException {
        LOGGER.info("Sending OSLP request: {}", request.getPayloadMessage());

        final Callback callback = new Callback(this.connectionTimeout);

        this.lock.lock();

        // Open connection and send message
        ChannelFuture channelFuture = null;
        try {
            channelFuture = this.bootstrap.connect(address);
            channelFuture.awaitUninterruptibly(this.connectionTimeout, TimeUnit.MILLISECONDS);
            if (channelFuture.getChannel() != null && channelFuture.getChannel().isConnected()) {
                LOGGER.info("Connection established to: {}", address);
            } else {
                LOGGER.info("The connnection to the device {} is not successfull", deviceIdentification);
                LOGGER.warn("Unable to connect to: {}", address);
                throw new IOException("Unable to connect");
            }

            this.callbacks.put(channelFuture.getChannel().getId(), callback);
            channelFuture.getChannel().write(request);
        } finally {
            this.lock.unlock();
        }

        // wait for response and close connection
        try {
            final OslpEnvelope response = callback.get(deviceIdentification);
            LOGGER.info("Received OSLP response (after callback): {}", response.getPayloadMessage());

            /*
             * Devices expect the channel to be closed if (and only if) the
             * platform initiated the conversation. If the device initiated the
             * conversation it needs to close the channel itself.
             */
            channelFuture.getChannel().close();

            return response;
        } catch (final IOException | DeviceSimulatorException e) {
            LOGGER.error("send exception", e);
            // Remove callback when exception has occurred
            this.callbacks.remove(channelFuture.getChannel().getId());
            throw e;
        }
    }

    private boolean isOslpResponse(final OslpEnvelope envelope) {
        return envelope.getPayloadMessage().hasRegisterDeviceResponse()
                || envelope.getPayloadMessage().hasConfirmRegisterDeviceResponse()
                || envelope.getPayloadMessage().hasEventNotificationResponse();
    }

    private void sleep(final Long sleepTime) {
        if (sleepTime == null || sleepTime == 0) {
            return;
        }
        try {
            LOGGER.info("Sleeping for {} milliseconds", sleepTime);
            Thread.sleep(sleepTime);
        } catch (final InterruptedException e) {
            LOGGER.info("InterruptedException", e);
        }
    }

    private Oslp.Message handleRequest(final OslpEnvelope message, final int sequenceNumber)
            throws DeviceSimulatorException, IOException, ParseException {
        final Oslp.Message request = message.getPayloadMessage();

        // Create response message
        Oslp.Message response = null;
        final String deviceIdString = Base64.encodeBase64String(message.getDeviceId());

        LOGGER.info("request received, sequenceNumber: {}", sequenceNumber);
        LOGGER.info("manufacturerId byte[0]: {} byte[1]: {}", message.getDeviceId()[0], message.getDeviceId()[1]);
        LOGGER.info("deviceId as BASE 64 STRING: {}", deviceIdString);

        // lookup correct device.
        final Device device = this.deviceManagementService.findDevice(deviceIdString);
        if (device == null) {
            throw new DeviceSimulatorException("device with id: " + deviceIdString + " is unknown");
        }

        // Calculate expected sequence number
        final Integer expectedSequenceNumber = device.doGetNextSequence();

        // Check sequence number
        if (Math.abs(expectedSequenceNumber - sequenceNumber) > this.sequenceNumberWindow) {
            this.outOfSequenceList.add(
                    new OutOfSequenceEvent(device.getId(), message.getPayloadMessage().toString(), DateTime.now()));

            throw new DeviceSimulatorException(
                    "SequenceNumber incorrect for device: " + device.getDeviceIdentification() + " Expected: "
                            + (expectedSequenceNumber == 0 ? this.sequenceNumberMaximum : expectedSequenceNumber - 1)
                            + " Actual: " + (sequenceNumber == 0 ? this.sequenceNumberMaximum : sequenceNumber - 1)
                            + " SequenceNumberWindow: " + this.sequenceNumberWindow + " Request: "
                            + message.getPayloadMessage().toString());
        }

        // If responseDelayTime (and optional responseDelayRandomRange) are set,
        // sleep for a little while
        if (this.responseDelayTime != null && this.reponseDelayRandomRange == null) {
            this.sleep(this.responseDelayTime);
        } else if (this.responseDelayTime != null && this.reponseDelayRandomRange != null) {
            final Long randomDelay = (long) (this.reponseDelayRandomRange * this.random.nextDouble());
            this.sleep(this.responseDelayTime + randomDelay);
        }

        // Handle only expected messages
        if (request.hasStartSelfTestRequest()) {
            device.setLightOn(true);
            device.setSelftestActive(true);

            response = createStartSelfTestResponse();
        } else if (request.hasStopSelfTestRequest()) {
            device.setLightOn(false);
            device.setSelftestActive(false);

            response = createStopSelfTestResponse();
        } else if (request.hasSetLightRequest()) {
            handleSetLightRequest(device, request.getSetLightRequest());

            response = createSetLightResponse();
        } else if (request.hasSetEventNotificationsRequest()) {
            this.handleSetEventNotificationsRequest(device, request.getSetEventNotificationsRequest());

            response = createSetEventNotificationsResponse();
        } else if (request.hasUpdateFirmwareRequest()) {
            this.handleUpdateFirmwareRequest(device, request.getUpdateFirmwareRequest());

            response = createUpdateFirmwareResponse();
        } else if (request.hasGetFirmwareVersionRequest()) {
            response = createGetFirmwareVersionResponse(this.firmwareVersion);
        } else if (request.hasSwitchFirmwareRequest()) {
            response = createSwitchFirmwareResponse();
        } else if (request.hasUpdateDeviceSslCertificationRequest()) {
            response = createUpdateDeviceSslCertificationResponse();
        } else if (request.hasSetDeviceVerificationKeyRequest()) {
            response = createSetDeviceVerificationKeyResponse();
        } else if (request.hasSetScheduleRequest()) {
            this.handleSetScheduleRequest(device, request.getSetScheduleRequest());

            response = createSetScheduleResponse();
        } else if (request.hasSetConfigurationRequest()) {
            this.handleSetConfigurationRequest(device, request.getSetConfigurationRequest());

            response = this.createSetConfigurationResponse();
        } else if (request.hasGetConfigurationRequest()) {
            this.handleGetConfigurationRequest(device, request.getGetConfigurationRequest());

            response = this.createGetConfigurationResponse(device);
        } else if (request.hasSwitchConfigurationRequest()) {
            response = createSwitchConfigurationResponse();
        } else if (request.hasGetActualPowerUsageRequest()) {
            this.handleGetActualPowerUsageRequest(device, request.getGetActualPowerUsageRequest());

            response = createGetActualPowerUsageResponse();
        } else if (request.hasGetPowerUsageHistoryRequest()) {
            this.handleGetPowerUsageHistoryRequest(device, request.getGetPowerUsageHistoryRequest());

            response = createGetPowerUsageHistoryWithDatesResponse(request.getGetPowerUsageHistoryRequest());
        } else if (request.hasGetStatusRequest()) {
            response = this.createGetStatusResponse(device);
        } else if (request.hasResumeScheduleRequest()) {
            response = createResumeScheduleResponse();
        } else if (request.hasSetRebootRequest()) {
            response = createSetRebootResponse();

            this.sendDelayedDeviceRegistration(device);
        } else if (request.hasSetTransitionRequest()) {
            this.handleSetTransitionRequest(device);

            response = createSetTransitionResponse();
        } else if (request.hasConfirmRegisterDeviceRequest()) {
            response = createConfirmRegisterDeviceResponse(request.getConfirmRegisterDeviceRequest().getRandomDevice(),
                    request.getConfirmRegisterDeviceRequest().getRandomPlatform());
        } else {
            // Handle errors by logging
            LOGGER.error("Did not expect request, ignoring: " + request.toString());
        }

        // Update device
        device.setSequenceNumber(expectedSequenceNumber);
        this.deviceManagementService.updateDevice(device);

        // Write log entry for response
        LOGGER.debug("Responding: " + response);

        return response;
    }

    private void sendDelayedDeviceRegistration(final Device device) {
        if (device == null) {
            return;
        }

        final String deviceIdentification = device.getDeviceIdentification();
        if (StringUtils.isEmpty(deviceIdentification)) {
            return;
        }

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    LOGGER.info("Sending DeviceRegistrationRequest for device: {}", deviceIdentification);
                    final DeviceMessageStatus deviceMessageStatus = OslpChannelHandler.this.registerDevice
                            .sendRegisterDeviceCommand(device.getId(), true);
                    if (DeviceMessageStatus.OK.equals(deviceMessageStatus)) {
                        LOGGER.info("Sending ConfirmDeviceRegistrationRequest for device: {}", deviceIdentification);
                        OslpChannelHandler.this.registerDevice.sendConfirmDeviceRegistrationCommand(device.getId());
                    }
                } catch (final Exception e) {
                    LOGGER.error("Caught exception during sendDelayedDeviceRegistration() for device : "
                            + deviceIdentification, e);
                }
            }

        }, 2000);
    }

    private static Message createConfirmRegisterDeviceResponse(final int randomDevice, final int randomPlatform) {
        return Oslp.Message.newBuilder()
                .setConfirmRegisterDeviceResponse(ConfirmRegisterDeviceResponse.newBuilder()
                        .setRandomDevice(randomDevice).setRandomPlatform(randomPlatform).setStatus(Oslp.Status.OK))
                .build();
    }

    private void handleSetScheduleRequest(final Device device, final SetScheduleRequest setScheduleRequest) {
        // Not yet implemented.
        LOGGER.info("handleSetScheduleRequest not yet implemented. Device: {}, number of schedule entries: {}",
                device.getDeviceIdentification(), setScheduleRequest.getSchedulesCount());
    }

    private static Message createStartSelfTestResponse() throws IOException {
        return Oslp.Message.newBuilder()
                .setStartSelfTestResponse(StartSelfTestResponse.newBuilder().setStatus(Oslp.Status.OK)).build();
    }

    private static Message createStopSelfTestResponse() throws IOException {
        return Oslp.Message.newBuilder().setStopSelfTestResponse(StopSelfTestResponse.newBuilder()
                .setStatus(Oslp.Status.OK).setSelfTestResult(ByteString.copyFrom(new byte[] { 0 }))).build();
    }

    private static Message createSetLightResponse() throws IOException {
        return Oslp.Message.newBuilder().setSetLightResponse(SetLightResponse.newBuilder().setStatus(Oslp.Status.OK))
                .build();
    }

    private static Message createSetEventNotificationsResponse() {
        return Oslp.Message.newBuilder()
                .setSetEventNotificationsResponse(SetEventNotificationsResponse.newBuilder().setStatus(Oslp.Status.OK))
                .build();
    }

    private static Message createUpdateFirmwareResponse() {
        return Oslp.Message.newBuilder()
                .setUpdateFirmwareResponse(UpdateFirmwareResponse.newBuilder().setStatus(Oslp.Status.OK)).build();
    }

    private static Message createGetFirmwareVersionResponse(final String firmwareVersion) {
        return Oslp.Message.newBuilder().setGetFirmwareVersionResponse(
                GetFirmwareVersionResponse.newBuilder().setFirmwareVersion(firmwareVersion)).build();
    }

    private static Message createSwitchFirmwareResponse() {
        return Oslp.Message.newBuilder()
                .setSwitchFirmwareResponse(Oslp.SwitchFirmwareResponse.newBuilder().setStatus(Oslp.Status.FAILURE))
                .build();
    }

    private static Message createSetDeviceVerificationKeyResponse() {
        return Oslp.Message.newBuilder().setSetDeviceVerificationKeyResponse(
                Oslp.SetDeviceVerificationKeyResponse.newBuilder().setStatus(Oslp.Status.OK)).build();
    }

    private static Message createUpdateDeviceSslCertificationResponse() {
        return Oslp.Message.newBuilder().setUpdateDeviceSslCertificationResponse(
                Oslp.UpdateDeviceSslCertificationResponse.newBuilder().setStatus(Oslp.Status.OK)).build();
    }

    private static Message createSetScheduleResponse() {
        return Oslp.Message.newBuilder()
                .setSetScheduleResponse(SetScheduleResponse.newBuilder().setStatus(Oslp.Status.OK)).build();
    }

    private static Message createGetActualPowerUsageResponse() {
        // yyyyMMddhhmmss z
        final SimpleDateFormat utcTimeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        utcTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        final Date currentDateTime = new Date();
        final String utcTimestamp = utcTimeFormat.format(currentDateTime);

        @SuppressWarnings("deprecation")
        final int actualConsumedPower = currentDateTime.getMinutes();

        return Oslp.Message.newBuilder().setGetActualPowerUsageResponse(GetActualPowerUsageResponse.newBuilder()
                .setPowerUsageData(PowerUsageData.newBuilder().setRecordTime(utcTimestamp).setMeterType(MeterType.P1)
                        .setTotalConsumedEnergy(actualConsumedPower * 2L).setActualConsumedPower(actualConsumedPower)
                        .setPsldData(PsldData.newBuilder().setTotalLightingHours(actualConsumedPower * 3))
                        .setSsldData(SsldData.newBuilder().setActualCurrent1(1).setActualCurrent2(2)
                                .setActualCurrent3(3).setActualPower1(1).setActualPower2(2).setActualPower3(3)
                                .setAveragePowerFactor1(1).setAveragePowerFactor2(2).setAveragePowerFactor3(3)
                                .addRelayData(Oslp.RelayData.newBuilder()
                                        .setIndex(ByteString.copyFrom(new byte[] { 2 })).setTotalLightingMinutes(480))
                                .addRelayData(Oslp.RelayData.newBuilder()
                                        .setIndex(ByteString.copyFrom(new byte[] { 3 })).setTotalLightingMinutes(480))
                                .addRelayData(Oslp.RelayData.newBuilder()
                                        .setIndex(ByteString.copyFrom(new byte[] { 4 })).setTotalLightingMinutes(480))))
                .setStatus(Oslp.Status.OK)).build();
    }

    private static Message createGetPowerUsageHistoryWithDatesResponse(
            final GetPowerUsageHistoryRequest powerUsageHistoryRequest) throws ParseException {

        final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHHmmss").withZoneUTC();

        // 20140405 220000
        final DateTime now = new DateTime();
        final DateTime dateTimeFrom = formatter.parseDateTime(powerUsageHistoryRequest.getTimePeriod().getStartTime());
        DateTime dateTimeUntil = formatter.parseDateTime(powerUsageHistoryRequest.getTimePeriod().getEndTime());

        final int itemsPerPage = 2;
        final int intervalMinutes = powerUsageHistoryRequest.getTermType() == HistoryTermType.Short ? 60 : 1440;
        final int usagePerItem = powerUsageHistoryRequest.getTermType() == HistoryTermType.Short ? 2400 : 57600;

        // If from in the future, return emtpy list
        final List<PowerUsageData> powerUsageDataList = new ArrayList<>();
        if (dateTimeFrom.isAfter(now)) {
            return createUsageMessage(1, itemsPerPage, 1, powerUsageDataList);
        }

        // Ensure until date is not in future
        dateTimeUntil = correctUsageUntilDate(dateTimeUntil, powerUsageHistoryRequest.getTermType());

        final int queryInterval = Minutes.minutesBetween(dateTimeFrom, dateTimeUntil).getMinutes();
        final int totalNumberOfItems = queryInterval / intervalMinutes;
        final int numberOfPages = (int) Math.ceil((double) totalNumberOfItems / (double) itemsPerPage);

        // Determine page number
        int currentPageNumber;
        if (powerUsageHistoryRequest.getPage() == 0) {
            currentPageNumber = 1;
        } else {
            currentPageNumber = powerUsageHistoryRequest.getPage();
        }

        int page = 1;
        int itemsToSkip = 0;
        while (currentPageNumber != page) {
            itemsToSkip += itemsPerPage;
            page++;
        }

        // Advance time to correct page starting point, last to first (like real
        // device)
        DateTime pageStartTime = dateTimeUntil.minusMinutes(intervalMinutes * itemsToSkip)
                .minusMinutes(intervalMinutes);
        final int itemsOnPage = Math.min(Math.abs(totalNumberOfItems - itemsToSkip), itemsPerPage);

        // Advance usage to start of page
        int totalUsage = (totalNumberOfItems * usagePerItem) - (usagePerItem * itemsToSkip);

        // Fill page with items
        for (int i = 0; i < itemsOnPage; i++) {
            final int range = (100) + 1;
            final int randomCumulativeMinutes = (int) (Math.random() * range) + 100;

            // Increase the meter
            final double random = usagePerItem - (usagePerItem / 50d * Math.random());
            totalUsage -= random;
            // Add power usage item to response
            final PowerUsageData powerUsageData = PowerUsageData.newBuilder()
                    .setRecordTime(pageStartTime.toString(formatter)).setMeterType(MeterType.P1)
                    .setTotalConsumedEnergy(totalUsage).setActualConsumedPower((int) random)
                    .setPsldData(PsldData.newBuilder().setTotalLightingHours((int) random * 3))
                    .setSsldData(SsldData.newBuilder().setActualCurrent1(10).setActualCurrent2(20).setActualCurrent3(30)
                            .setActualPower1(10).setActualPower2(20).setActualPower3(30).setAveragePowerFactor1(10)
                            .setAveragePowerFactor2(20).setAveragePowerFactor3(30)
                            .addRelayData(Oslp.RelayData.newBuilder().setIndex(ByteString.copyFrom(new byte[] { 2 }))
                                    .setTotalLightingMinutes(INITIAL_BURNING_MINUTES - randomCumulativeMinutes))
                            .addRelayData(Oslp.RelayData.newBuilder().setIndex(ByteString.copyFrom(new byte[] { 3 }))
                                    .setTotalLightingMinutes(INITIAL_BURNING_MINUTES - randomCumulativeMinutes))
                            .addRelayData(Oslp.RelayData.newBuilder().setIndex(ByteString.copyFrom(new byte[] { 4 }))
                                    .setTotalLightingMinutes(INITIAL_BURNING_MINUTES - randomCumulativeMinutes)))
                    .build();

            powerUsageDataList.add(powerUsageData);
            pageStartTime = pageStartTime.minusMinutes(intervalMinutes);

            INITIAL_BURNING_MINUTES -= CUMALATIVE_BURNING_MINUTES;
        }

        return createUsageMessage(currentPageNumber, itemsPerPage, numberOfPages, powerUsageDataList);
    }

    private static DateTime correctUsageUntilDate(final DateTime dateTimeUntil, final HistoryTermType termType) {
        final DateTime now = new DateTime();
        if (dateTimeUntil.isAfter(now)) {
            if (termType == HistoryTermType.Short) {
                return now.hourOfDay().roundCeilingCopy();
            } else {
                return now.withZone(localTimeZone).dayOfWeek().roundCeilingCopy().withZone(DateTimeZone.UTC);
            }
        }

        return dateTimeUntil;
    }

    private static Message createUsageMessage(final int currentPageNumber, final int itemsPerPage,
            final int numberOfPages, final List<PowerUsageData> powerUsageDataList) {
        return Oslp.Message.newBuilder().setGetPowerUsageHistoryResponse(GetPowerUsageHistoryResponse.newBuilder()
                .addAllPowerUsageData(powerUsageDataList).setPageInfo(PageInfo.newBuilder()
                        .setCurrentPage(currentPageNumber).setPageSize(itemsPerPage).setTotalPages(numberOfPages))
                .setStatus(Oslp.Status.OK)).build();

    }

    private Message createSetConfigurationResponse() {
        return Oslp.Message.newBuilder()
                .setSetConfigurationResponse(Oslp.SetConfigurationResponse.newBuilder().setStatus(Oslp.Status.OK))
                .build();
    }

    private Message createGetConfigurationResponse(final Device device) {
        final DaliConfiguration.Builder daliConfiguration = DaliConfiguration.newBuilder()
                .addAddressMap(IndexAddressMap.newBuilder().setIndex(ByteString.copyFrom(new byte[] { 1 }))
                        .setAddress(ByteString.copyFrom(new byte[] { 1 })).setRelayType(RelayType.RT_NOT_SET))
                .setNumberOfLights(ByteString.copyFrom(new byte[] { 1 }));

        final Oslp.GetConfigurationResponse.Builder configuration = Oslp.GetConfigurationResponse.newBuilder();
        try {
            configuration.setStatus(Oslp.Status.OK)
                    .setPreferredLinkType(Enum.valueOf(Oslp.LinkType.class, device.getPreferredLinkType().name()))
                    .setLightType(Enum.valueOf(Oslp.LightType.class, device.getLightType().name()))
                    .setShortTermHistoryIntervalMinutes(15);

            if (device.getProtocol().equals(ProtocolType.OSLP.toString())) {
                // AME devices
                configuration.setMeterType(MeterType.P1);
                configuration.setLongTermHistoryIntervalType(LongTermIntervalType.DAYS);
                configuration.setLongTermHistoryInterval(1);
            } else {
                // ELSTER devices
                configuration.setMeterType(MeterType.MT_NOT_SET);
                configuration.setLongTermHistoryIntervalType(LongTermIntervalType.DAYS);
                configuration.setLongTermHistoryInterval(1);
                configuration.setTimeSyncFrequency(86400);
                configuration.setDeviceFixIpValue(ByteString
                        .copyFrom(InetAddress.getByName(this.configurationIpConfigFixedIpAddress).getAddress()));
                configuration.setNetMask(
                        ByteString.copyFrom(InetAddress.getByName(this.configurationIpConfigNetmask).getAddress()));
                configuration.setGateWay(
                        ByteString.copyFrom(InetAddress.getByName(this.configurationIpConfigGateway).getAddress()));
                configuration.setIsDhcpEnabled(false);
                configuration.setCommunicationTimeout(30);
                configuration.setCommunicationNumberOfRetries(5);
                configuration.setCommunicationPauseTimeBetweenConnectionTrials(120);
                configuration.setOspgIpAddress(
                        ByteString.copyFrom(InetAddress.getByName(this.configurationOsgpIpAddress).getAddress()));
                configuration.setOsgpPortNumber(this.configurationOsgpPortNumber);
                configuration.setIsTestButtonEnabled(false);
                configuration.setIsAutomaticSummerTimingEnabled(false);
                configuration.setAstroGateSunRiseOffset(-15);
                configuration.setAstroGateSunSetOffset(15);
                configuration.addSwitchingDelay(1);
                configuration.addSwitchingDelay(2);
                configuration.addSwitchingDelay(3);
                configuration.addSwitchingDelay(4);
                configuration.addRelayLinking(Oslp.RelayMatrix.newBuilder()
                        .setMasterRelayIndex(ByteString.copyFrom(new byte[] { 2 })).setMasterRelayOn(false)
                        .setIndicesOfControlledRelaysOn(ByteString.copyFrom(new byte[] { 3, 4 }))
                        .setIndicesOfControlledRelaysOff(ByteString.copyFrom(new byte[] { 3, 4 })));
                configuration.setRelayRefreshing(false).setSummerTimeDetails("0360100").setWinterTimeDetails("1060200");
            }

            if (device.getDeviceType().equals(Device.PSLD_TYPE)) {
                configuration.setDaliConfiguration(daliConfiguration);
            }

            if (device.getDeviceType().equals(Device.SSLD_TYPE)) {
                configuration.setRelayConfiguration(createRelayConfiguration(device.getOutputSettings()));
            }

            return Oslp.Message.newBuilder().setGetConfigurationResponse(configuration).build();
        } catch (final Exception e) {
            LOGGER.error("Unexpected UnknownHostException", e);
            return null;
        }
    }

    private static Message createSwitchConfigurationResponse() {
        return Oslp.Message.newBuilder()
                .setSwitchConfigurationResponse(Oslp.SwitchConfigurationResponse.newBuilder().setStatus(Oslp.Status.OK))
                .build();
    }

    /**
     * Create relay configuration based on stored configuration values.
     */
    private static RelayConfiguration createRelayConfiguration(final List<DeviceOutputSetting> outputSettings) {
        final RelayConfiguration.Builder configuration = RelayConfiguration.newBuilder();

        for (final DeviceOutputSetting dos : outputSettings) {
            final IndexAddressMap.Builder relayMap = IndexAddressMap.newBuilder()
                    .setIndex(OslpUtils.integerToByteString(dos.getInternalId()))
                    .setAddress(OslpUtils.integerToByteString(dos.getExternalId()));

            // Map device-simulator enum OutputType to OSLP enum RelayType
            if (dos.getOutputType() == OutputType.LIGHT) {
                relayMap.setRelayType(RelayType.LIGHT);
            } else if (dos.getOutputType() == OutputType.TARIFF) {
                relayMap.setRelayType(RelayType.TARIFF);
            } else {
                relayMap.setRelayType(RelayType.RT_NOT_SET);
            }

            configuration.addAddressMap(relayMap);
        }

        return configuration.build();
    }

    private Message createGetStatusResponse(final Device device) {

        final List<LightValue> outputValues = new ArrayList<>();
        for (final DeviceOutputSetting dos : device.getOutputSettings()) {
            final LightValue.Builder lightValue = LightValue.newBuilder()
                    .setIndex(OslpUtils.integerToByteString(dos.getInternalId()));

            if (dos.getOutputType().equals(OutputType.LIGHT)) {
                lightValue.setOn(device.isLightOn());

                if (device.getDimValue() != null) {
                    lightValue.setDimValue(OslpUtils.integerToByteString(device.getDimValue()));
                }

                // Real device specifies dimvalue 0 when off.
                if (!device.isLightOn()) {
                    lightValue.setDimValue(OslpUtils.integerToByteString(0));
                }

            } else if (dos.getOutputType().equals(OutputType.TARIFF)) {
                lightValue.setOn(device.isTariffOn());
            }
            outputValues.add(lightValue.build());
        }

        // Fallback in case output settings are not yet defined.
        if (outputValues.isEmpty()) {
            final LightValue.Builder lightValue = LightValue.newBuilder().setIndex(OslpUtils.integerToByteString(0))
                    .setOn(device.isLightOn());

            if (device.getDimValue() != null) {
                lightValue.setDimValue(OslpUtils.integerToByteString(device.getDimValue()));
            }

            // Real device specifies dimvalue 0 when off.
            if (!device.isLightOn()) {
                lightValue.setDimValue(OslpUtils.integerToByteString(0));
            }

            outputValues.add(lightValue.build());
        }

        final Oslp.GetStatusResponse.Builder builder = GetStatusResponse.newBuilder();

        builder.setStatus(Oslp.Status.OK);
        builder.addAllValue(outputValues);
        builder.setPreferredLinktype(Enum.valueOf(Oslp.LinkType.class, device.getPreferredLinkType().name()));
        builder.setActualLinktype(Enum.valueOf(Oslp.LinkType.class, device.getActualLinkType().name()));
        builder.setLightType(Enum.valueOf(Oslp.LightType.class, device.getLightType().name()));
        builder.setEventNotificationMask(device.getEventNotificationMask());

        LOGGER.info("device.getProtocol(): {}", device.getProtocol());
        LOGGER.info("ProtocolType.OSLP_ELSTER.name(): {}", ProtocolType.OSLP_ELSTER.name());

        if (device.getProtocol().equals(ProtocolType.OSLP_ELSTER.toString())) {
            builder.setNumberOfOutputs(4);
            builder.setDcOutputVoltageMaximum(24000);
            builder.setDcOutputVoltageCurrent(24000);
            builder.setMaximumOutputPowerOnDcOutput(15000);
            builder.setSerialNumber(
                    ByteString.copyFrom(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9 }));
            builder.setMacAddress(ByteString.copyFrom(new byte[] { 1, 2, 3, 4, 5, 6 }));
            builder.setHardwareId("Hardware ID").setInternalFlashMemSize(1024);
            builder.setExternalFlashMemSize(2048).setLastInternalTestResultCode(0).setStartupCounter(42);
            builder.setBootLoaderVersion("1.1.1").setFirmwareVersion("2.8.5");
            builder.setCurrentConfigurationBackUsed(ByteString.copyFrom(new byte[] { 0 }));
            builder.setName("ELS_DEV-SIM-DEVICE").setCurrentTime("20251231155959");
            builder.setCurrentIp(this.statusInternalIpAddress);
        }

        return Oslp.Message.newBuilder().setGetStatusResponse(builder.build()).build();
    }

    private static Message createResumeScheduleResponse() {
        return Oslp.Message.newBuilder()
                .setResumeScheduleResponse(Oslp.ResumeScheduleResponse.newBuilder().setStatus(Oslp.Status.OK)).build();
    }

    private static Message createSetRebootResponse() {
        return Oslp.Message.newBuilder()
                .setSetRebootResponse(Oslp.SetRebootResponse.newBuilder().setStatus(Oslp.Status.OK)).build();
    }

    private static Message createSetTransitionResponse() {
        return Oslp.Message.newBuilder()
                .setSetTransitionResponse(Oslp.SetTransitionResponse.newBuilder().setStatus(Oslp.Status.OK)).build();
    }

    private static void handleSetLightRequest(final Device device, final SetLightRequest request) {
        // Device simulator will only use first light value,
        // other light values will be ignored
        final LightValue lightValue = request.getValues(0);

        device.setLightOn(lightValue.getOn());

        if (lightValue.hasDimValue()) {
            final int dimValue = lightValue.getDimValue().byteAt(0);
            device.setDimValue(dimValue);
        } else {
            device.setDimValue(null);
        }
    }

    private void handleSetTransitionRequest(final Device device) {
        // Device simulator will only use first light value,
        // other light values will be ignored

        // reverse the light.
        device.setLightOn(!device.isLightOn());

        final EventNotificationToBeSent eventNotificationToBeSent = new EventNotificationToBeSent(device.getId(),
                device.isLightOn());
        this.deviceManagementService.getEventNotificationToBeSent().add(eventNotificationToBeSent);
    }

    private void handleSetEventNotificationsRequest(final Device device, final SetEventNotificationsRequest request) {
        device.setEventNotifications(request.getNotificationMask());
    }

    private void handleUpdateFirmwareRequest(final Device device, final UpdateFirmwareRequest request) {
        // For now, do nothing, perhaps store firmware version, so that it can
        // be displayed ???
    }

    private void handleSetConfigurationRequest(final Device device,
            final Oslp.SetConfigurationRequest setConfigurationRequest) {
        if (setConfigurationRequest.hasPreferredLinkType()) {
            device.setPreferredLinkType(
                    Enum.valueOf(LinkType.class, setConfigurationRequest.getPreferredLinkType().name()));
        }
        if (setConfigurationRequest.hasLightType()) {
            device.setLightType(Enum.valueOf(LightType.class, setConfigurationRequest.getLightType().name()));
        }
        if (setConfigurationRequest.hasRelayConfiguration()) {
            final List<DeviceOutputSetting> outputSettings = new ArrayList<>();
            for (final IndexAddressMap iam : setConfigurationRequest.getRelayConfiguration().getAddressMapList()) {
                final int index = iam.getIndex().byteAt(0);
                final int address = iam.getAddress().byteAt(0);
                final OutputType outputType = OutputType.valueOf(iam.getRelayType().name());

                outputSettings.add(new DeviceOutputSetting(index, address, outputType));
            }
            device.setOutputSettings(outputSettings);
        }
    }

    private void handleGetConfigurationRequest(final Device device,
            final Oslp.GetConfigurationRequest getConfigurationRequest) {
        // Do nothing for now.
    }

    private void handleGetActualPowerUsageRequest(final Device device, final GetActualPowerUsageRequest request) {
        // Do nothing for now.
    }

    private void handleGetPowerUsageHistoryRequest(final Device device, final GetPowerUsageHistoryRequest request) {
        // Do nothing for now.
    }

    private String getDeviceIdentificationFromMessage(final Oslp.Message message) {
        String deviceIdentification = "";

        // Expand with other message types which contain a device
        // identification if needed.
        if (message.hasRegisterDeviceRequest()) {
            deviceIdentification = message.getRegisterDeviceRequest().getDeviceIdentification();
        }

        return deviceIdentification;
    }

    private byte[] convertIntegerToByteArray(final Integer value) {
        // See: platform.service.SequenceNumberUtils
        final byte[] bytes = new byte[2];
        bytes[0] = (byte) (value >>> 8);
        bytes[1] = (byte) (value >>> 0);
        LOGGER.info(
                "web-device-simulator.OslpChannelHandler.convertIntegerToByteArray() byte[0]: {} byte[1]: {} Integer value: {}",
                bytes[0], bytes[1], value);
        return bytes;
    }

    private Integer convertByteArrayToInteger(final byte[] array) {
        // See: platform.service.SequenceNumberUtils
        final Integer value = (array[0] & 0xFF) << 8 | (array[1] & 0xFF);
        LOGGER.info(
                "web-device-simulator.OslpChannelHandler.convertByteArrayToInteger() byte[0]: {} byte[1]: {} Integer value: {}",
                array[0], array[1], value);
        return value;
    }
}
