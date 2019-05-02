/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.mocks.oslpdevice;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.PrivateKey;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.joda.time.DateTime;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.oslp.Oslp;
import org.opensmartgridplatform.oslp.Oslp.Message;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;

public class MockOslpChannelHandler extends SimpleChannelHandler {

    private static class Callback {

        private final int connectionTimeout;

        private final CountDownLatch latch = new CountDownLatch(1);

        private OslpEnvelope response;

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

                LOGGER.debug("Response received within {} ms", this.connectionTimeout);
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

    private static final Logger LOGGER = LoggerFactory.getLogger(MockOslpChannelHandler.class);
    @Transient
    private static final Integer SEQUENCE_NUMBER_MAXIMUM = 65535;
    private final ConcurrentMap<Integer, Callback> callbacks = new ConcurrentHashMap<>();
    private final ClientBootstrap clientBootstrap;
    private final int connectionTimeout;
    private final Lock lock = new ReentrantLock();
    private final ConcurrentMap<MessageType, Message> mockResponses;
    private final String oslpSignature;

    private final String oslpSignatureProvider;

    private final List<OutOfSequenceEvent> outOfSequenceList = new ArrayList<>();

    private final PrivateKey privateKey;

    private final Random random = new Random();

    private final ConcurrentMap<MessageType, Message> receivedRequests;
    private final List<Message> receivedResponses;

    private final Long reponseDelayRandomRange;

    private final Long responseDelayTime;

    // Device settings
    private Integer sequenceNumber = 0;
    private final Integer sequenceNumberMaximum;

    public MockOslpChannelHandler(final String oslpSignature, final String oslpSignatureProvider,
            final int connectionTimeout, final Integer sequenceNumberWindow, final Integer sequenceNumberMaximum,
            final Long responseDelayTime, final Long reponseDelayRandomRange, final PrivateKey privateKey,
            final ClientBootstrap clientBootstrap, final ConcurrentMap<MessageType, Message> mockResponses,
            final ConcurrentMap<MessageType, Message> receivedRequests, final List<Message> receivedResponses) {
        this.oslpSignature = oslpSignature;
        this.oslpSignatureProvider = oslpSignatureProvider;
        this.connectionTimeout = connectionTimeout;
        this.sequenceNumberMaximum = sequenceNumberMaximum;
        this.responseDelayTime = responseDelayTime;
        this.reponseDelayRandomRange = reponseDelayRandomRange;
        this.privateKey = privateKey;
        this.clientBootstrap = clientBootstrap;
        this.mockResponses = mockResponses;
        this.receivedRequests = receivedRequests;
        this.receivedResponses = receivedResponses;
    }

    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        LOGGER.debug("Channel {} closed", e.getChannel().getId());
        super.channelClosed(ctx, e);
    }

    @Override
    public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        LOGGER.debug("Channel {} disconnected", e.getChannel().getId());
        super.channelDisconnected(ctx, e);
    }

    @Override
    public void channelOpen(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        LOGGER.debug("Channel {} opened", e.getChannel().getId());
        super.channelOpen(ctx, e);
    }

    @Override
    public void channelUnbound(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        LOGGER.debug("Channel {} unbound", e.getChannel().getId());
        super.channelUnbound(ctx, e);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) {
        if (this.isConnectionReset(e.getCause())) {
            LOGGER.debug("Connection was (as expected) reset by the device.");
        } else {
            LOGGER.warn("Unexpected exception from downstream.", e.getCause());
        }

        e.getChannel().close();
    }

    public ConcurrentMap<MessageType, Message> getMap() {
        return this.mockResponses;
    }

    public Integer getSequenceNumber() {
        return this.sequenceNumber;
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

    private boolean isConnectionReset(final Throwable e) {
        return e instanceof IOException && e.getMessage() != null
                && e.getMessage().contains("Connection reset by peer");
    }

    private boolean isOslpResponse(final OslpEnvelope envelope) {
        return envelope.getPayloadMessage().hasRegisterDeviceResponse()
                || envelope.getPayloadMessage().hasConfirmRegisterDeviceResponse()
                || envelope.getPayloadMessage().hasEventNotificationResponse();
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        final OslpEnvelope message = (OslpEnvelope) e.getMessage();

        if (message.isValid()) {
            if (this.isOslpResponse(message)) {
                LOGGER.debug("Received OSLP Response (before callback): {}", message.getPayloadMessage());

                // Lookup correct callback and call handle method
                final Integer channelId = e.getChannel().getId();
                final Callback callback = this.callbacks.remove(channelId);
                if (callback == null) {
                    LOGGER.warn("Callback for channel {} does not longer exist, dropping response.", channelId);
                    return;
                }

                callback.handle(message);
            } else {
                if (!this.mockResponses.isEmpty()) {
                    LOGGER.debug("Received OSLP Request: {}", message.getPayloadMessage().toString().split(" ")[0]);

                    final byte[] deviceId = message.getDeviceId();

                    // Build the OslpEnvelope
                    final OslpEnvelope.Builder responseBuilder = new OslpEnvelope.Builder()
                            .withSignature(this.oslpSignature).withProvider(this.oslpSignatureProvider)
                            .withPrimaryKey(this.privateKey).withDeviceId(deviceId);

                    // Pass the incremented sequence number to the
                    // handleRequest() function for checking.
                    responseBuilder.withPayloadMessage(this.handleRequest(message));
                    // Add the new sequence number to the OslpEnvelope
                    responseBuilder.withSequenceNumber(this.convertIntegerToByteArray(this.sequenceNumber));

                    final OslpEnvelope response = responseBuilder.build();

                    LOGGER.debug("sending OSLP response with sequence number: {}",
                            this.convertByteArrayToInteger(response.getSequenceNumber()));

                    // wait for the response to actually be written. This
                    // improves stability of the tests
                    final ChannelFuture future = e.getChannel().write(response);
                    future.await();

                    LOGGER.debug("Send OSLP Response: {}", response.getPayloadMessage().toString().split(" ")[0]);
                }
            }
        } else {
            LOGGER.warn("Received message wasn't properly secured.");
        }
    }

    public OslpEnvelope send(final InetSocketAddress address, final OslpEnvelope request,
            final String deviceIdentification) throws IOException, DeviceSimulatorException {
        LOGGER.debug("Sending OSLP request: {}", request.getPayloadMessage());

        final Callback callback = new Callback(this.connectionTimeout);

        this.lock.lock();

        // Open connection and send message
        ChannelFuture channelFuture = null;
        try {
            channelFuture = this.clientBootstrap.connect(address);
            channelFuture.awaitUninterruptibly(this.connectionTimeout, TimeUnit.MILLISECONDS);
            if (channelFuture.getChannel() != null && channelFuture.getChannel().isConnected()) {
                LOGGER.debug("Connection established to: {}", address);
            } else {
                LOGGER.debug("The connnection to the device {} is not successfull", deviceIdentification);
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
            LOGGER.debug("Received OSLP response (after callback): {}", response.getPayloadMessage());

            /*
             * Devices expect the channel to be closed if (and only if) the
             * platform initiated the conversation. If the device initiated the
             * conversation it needs to close the channel itself.
             */
            channelFuture.getChannel().close();

            this.receivedResponses.add(response.getPayloadMessage());

            return response;
        } catch (final IOException | DeviceSimulatorException e) {
            LOGGER.error("send exception", e);
            // Remove callback when exception has occurred
            this.callbacks.remove(channelFuture.getChannel().getId());
            throw e;
        }
    }

    private void sleep(final Long sleepTime) {
        if (sleepTime == null || sleepTime == 0) {
            return;
        }
        try {
            LOGGER.info("Sleeping for {} milliseconds", sleepTime);
            Thread.sleep(sleepTime);
        } catch (final InterruptedException e) {
            LOGGER.error("InterruptedException", e);
        }
    }

    // Note: This method is for other classes which are executing this method
    // WITH a sequencenumber
    public Oslp.Message handleRequest(final OslpEnvelope message, final int sequenceNumber)
            throws DeviceSimulatorException, IOException, ParseException {
        return this.handleRequest(message);
    }

    public Oslp.Message handleRequest(final OslpEnvelope message)
            throws DeviceSimulatorException, IOException, ParseException {
        final Oslp.Message request = message.getPayloadMessage();

        // Create response message
        Oslp.Message response = null;

        LOGGER.info("Received a new request: [" + request + "], sequencenumber [" + this.sequenceNumber + "]");

        // Calculate expected sequence number
        this.sequenceNumber = this.doGetNextSequence();

        // If responseDelayTime (and optional responseDelayRandomRange) are set,
        // sleep for a little while
        if (this.responseDelayTime != null && this.reponseDelayRandomRange == null) {
            this.sleep(this.responseDelayTime);
        } else if (this.responseDelayTime != null && this.reponseDelayRandomRange != null) {
            final Long randomDelay = (long) (this.reponseDelayRandomRange * this.random.nextDouble());
            this.sleep(this.responseDelayTime + randomDelay);
        }

        // Handle requests
        if (request.hasGetFirmwareVersionRequest()
                && this.mockResponses.containsKey(MessageType.GET_FIRMWARE_VERSION)) {
            response = this.processRequest(MessageType.GET_FIRMWARE_VERSION, request);
        } else if (request.hasUpdateFirmwareRequest() && this.mockResponses.containsKey(MessageType.UPDATE_FIRMWARE)) {
            response = this.processRequest(MessageType.UPDATE_FIRMWARE, request);
        } else if (request.hasSetLightRequest() && this.mockResponses.containsKey(MessageType.SET_LIGHT)) {
            response = this.processRequest(MessageType.SET_LIGHT, request);
        } else if (request.hasSetEventNotificationsRequest()
                && this.mockResponses.containsKey(MessageType.SET_EVENT_NOTIFICATIONS)) {
            response = this.processRequest(MessageType.SET_EVENT_NOTIFICATIONS, request);
        } else if (request.hasStartSelfTestRequest() && this.mockResponses.containsKey(MessageType.START_SELF_TEST)) {
            response = this.processRequest(MessageType.START_SELF_TEST, request);
        } else if (request.hasStopSelfTestRequest() && this.mockResponses.containsKey(MessageType.STOP_SELF_TEST)) {
            response = this.processRequest(MessageType.STOP_SELF_TEST, request);
        } else if (request.hasGetStatusRequest() && this.mockResponses.containsKey(MessageType.GET_STATUS)) {
            response = this.processRequest(MessageType.GET_STATUS, request);
        } else if (request.hasGetStatusRequest() && this.mockResponses.containsKey(MessageType.GET_LIGHT_STATUS)) {
            response = this.processRequest(MessageType.GET_LIGHT_STATUS, request);
        } else if (request.hasResumeScheduleRequest() && this.mockResponses.containsKey(MessageType.RESUME_SCHEDULE)) {
            response = this.processRequest(MessageType.RESUME_SCHEDULE, request);
        } else if (request.hasSetRebootRequest() && this.mockResponses.containsKey(MessageType.SET_REBOOT)) {
            response = this.processRequest(MessageType.SET_REBOOT, request);
        } else if (request.hasSetTransitionRequest() && this.mockResponses.containsKey(MessageType.SET_TRANSITION)) {
            response = this.processRequest(MessageType.SET_TRANSITION, request);
        } else if (request.hasSetDeviceVerificationKeyRequest()
                && this.mockResponses.containsKey(MessageType.UPDATE_KEY)) {
            response = this.processRequest(MessageType.UPDATE_KEY, request);
        } else if (request.hasGetPowerUsageHistoryRequest()
                && this.mockResponses.containsKey(MessageType.GET_POWER_USAGE_HISTORY)) {
            response = this.processRequest(MessageType.GET_POWER_USAGE_HISTORY, request);
        } else if (request.hasSetScheduleRequest()) {
            if (this.mockResponses.containsKey(MessageType.SET_LIGHT_SCHEDULE)) {
                response = this.processRequest(MessageType.SET_LIGHT_SCHEDULE, request);
            } else if (this.mockResponses.containsKey(MessageType.SET_TARIFF_SCHEDULE)) {
                response = this.processRequest(MessageType.SET_TARIFF_SCHEDULE, request);
            }
        } else if (request.hasGetConfigurationRequest()
                && this.mockResponses.containsKey(MessageType.GET_CONFIGURATION)) {
            response = this.processRequest(MessageType.GET_CONFIGURATION, request);
        } else if (request.hasSetConfigurationRequest()
                && this.mockResponses.containsKey(MessageType.SET_CONFIGURATION)) {
            response = this.processRequest(MessageType.SET_CONFIGURATION, request);
        }
        // TODO: Implement further requests.
        else {
            // Handle errors by logging
            LOGGER.error("Did not expect request, ignoring: " + request.toString());
        }

        // Write log entry for response
        LOGGER.info("Mock Response: " + response);

        return response;
    }

    private Oslp.Message processRequest(final MessageType type, final Oslp.Message request) {
        Oslp.Message response;

        LOGGER.debug("Processing [{}] ...", type.name());
        LOGGER.debug("Received [{}] ...", request);

        this.receivedRequests.put(type, request);
        response = this.mockResponses.get(type);
        this.mockResponses.remove(type);

        return response;
    }

    public void reset() {
        this.sequenceNumber = 0;
    }

    public int doGetNextSequence() {
        int numberToAddToSequenceNumberValue = 1;

        if (ScenarioContext.current().get(PlatformKeys.NUMBER_TO_ADD_TO_SEQUENCE_NUMBER) != null) {
            final String numberToAddAsNextSequenceNumber = ScenarioContext.current()
                    .get(PlatformKeys.NUMBER_TO_ADD_TO_SEQUENCE_NUMBER).toString();
            if (!numberToAddAsNextSequenceNumber.isEmpty()) {
                numberToAddToSequenceNumberValue = Integer.parseInt(numberToAddAsNextSequenceNumber);
            }
        }
        int next = this.sequenceNumber + numberToAddToSequenceNumberValue;
        if (next > SEQUENCE_NUMBER_MAXIMUM) {
            final int sequenceNumberMaximumCross = next - SEQUENCE_NUMBER_MAXIMUM;
            if (sequenceNumberMaximumCross >= 1) {
                next = sequenceNumberMaximumCross - 1;
            }
        } else if (next < 0) {
            final int sequenceNumberMaximumCross = next * -1;
            if (sequenceNumberMaximumCross >= 1) {
                next = SEQUENCE_NUMBER_MAXIMUM - sequenceNumberMaximumCross + 1;
            }
        }

        return this.sequenceNumber = next;
    }

    private byte[] convertIntegerToByteArray(final Integer value) {
        // See: platform.service.SequenceNumberUtils
        final byte[] bytes = new byte[2];
        bytes[0] = (byte) (value >>> 8);
        bytes[1] = value.byteValue();
        return bytes;
    }

    private Integer convertByteArrayToInteger(final byte[] array) {
        // See: platform.service.SequenceNumberUtils
        return (array[0] & 0xFF) << 8 | (array[1] & 0xFF);
    }
}
