/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.mocks.oslpdevice;

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Resource;
import javax.persistence.Transient;

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

public class OslpChannelHandler extends SimpleChannelHandler {

    private static DateTimeZone localTimeZone = DateTimeZone.forID("Europe/Amsterdam");

    private static final Logger LOGGER = LoggerFactory.getLogger(OslpChannelHandler.class);

    @Transient
    private static final Integer SEQUENCE_NUMBER_MAXIMUM = 65535;

    // Device settings
    private Integer sequenceNumber;
    private String firmwareVersion = "R01";

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
                    throw new IOException("Failed to receive response within timelimit " + this.connectionTimeout
                            + " ms");
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
    private PrivateKey privateKey;

    @Resource
    private String oslpSignatureProvider;

    @Resource
    private String oslpSignature;

    @Resource
    private int connectionTimeout;

    @Autowired
    private ClientBootstrap bootstrap;

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

    public ClientBootstrap getBootstrap() {
        return this.bootstrap;
    }

    public void setBootstrap(final ClientBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        final OslpEnvelope message = (OslpEnvelope) e.getMessage();

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
                if (!(message.getPayloadMessage().hasRegisterDeviceRequest() || message.getPayloadMessage()
                        .hasConfirmRegisterDeviceRequest())) {
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

         // Calculate expected sequence number
        final Integer expectedSequenceNumber = this.doGetNextSequence();

        /*// Check sequence number
        if (Math.abs(expectedSequenceNumber - sequenceNumber) > this.sequenceNumberWindow) {
            this.outOfSequenceList.add(new OutOfSequenceEvent(device.getId(), message.getPayloadMessage().toString(),
                    DateTime.now()));

            throw new DeviceSimulatorException("SequenceNumber incorrect for device: "
                    + device.getDeviceIdentification() + " Expected: "
                    + (expectedSequenceNumber == 0 ? this.sequenceNumberMaximum : expectedSequenceNumber - 1)
                    + " Actual: " + (sequenceNumber == 0 ? this.sequenceNumberMaximum : sequenceNumber - 1)
                    + " SequenceNumberWindow: " + this.sequenceNumberWindow + " Request: "
                    + message.getPayloadMessage().toString());
        }*/
        
        // If responseDelayTime (and optional responseDelayRandomRange) are set,
        // sleep for a little while
        if (this.responseDelayTime != null && this.reponseDelayRandomRange == null) {
            this.sleep(this.responseDelayTime);
        } else if (this.responseDelayTime != null && this.reponseDelayRandomRange != null) {
            final Long randomDelay = (long) (this.reponseDelayRandomRange * this.random.nextDouble());
            this.sleep(this.responseDelayTime + randomDelay);
        }

        // Handle requests
        if (request.hasGetFirmwareVersionRequest()) {
            response = createGetFirmwareVersionResponse();
        }
        // TODO: Implement further requests.
        else {
            // Handle errors by logging
            LOGGER.error("Did not expect request, ignoring: " + request.toString());
        }

        // Write log entry for response
        LOGGER.debug("Responding: " + response);

        return response;
    }

    /**
     * Create a get firmware version response
     * @return
     */
    private Message createGetFirmwareVersionResponse() {
        return Oslp.Message.newBuilder()
                .setGetFirmwareVersionResponse(
                		GetFirmwareVersionResponse.newBuilder().setFirmwareVersion(this.firmwareVersion))
                .build();
    }
    
    private int doGetNextSequence() {
        int next = this.sequenceNumber + 1;
        if (next > SEQUENCE_NUMBER_MAXIMUM) {
            next = 0;
        }

        return next;
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
