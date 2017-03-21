/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.elster.infra.networking;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.protocol.oslp.elster.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.protocol.oslp.elster.application.services.DeviceRegistrationService;
import com.alliander.osgp.adapter.protocol.oslp.elster.application.services.oslp.OslpDeviceSettingsService;
import com.alliander.osgp.adapter.protocol.oslp.elster.application.services.oslp.OslpSigningService;
import com.alliander.osgp.adapter.protocol.oslp.elster.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.elster.exceptions.ProtocolAdapterException;
import com.alliander.osgp.core.db.api.application.services.DeviceDataService;
import com.alliander.osgp.dto.valueobjects.GpsCoordinatesDto;
import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.oslp.Oslp.EventNotification;
import com.alliander.osgp.oslp.Oslp.EventNotificationRequest;
import com.alliander.osgp.oslp.Oslp.LocationInfo;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.alliander.osgp.oslp.SignedOslpEnvelopeDto;

public class OslpChannelHandlerServer extends OslpChannelHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OslpChannelHandlerServer.class);

    private static DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMMddHHmmss");

    @Autowired
    private DeviceRegistrationService deviceRegistrationService;

    @Autowired
    private DeviceManagementService deviceManagementService;

    @Autowired
    private Integer sequenceNumberWindow;

    @Autowired
    private Integer timeZoneOffsetMinutes;

    @Autowired
    private String testDeviceId;

    @Autowired
    private String testDeviceIp;

    @Autowired
    private OslpDeviceSettingsService oslpDeviceSettingsService;

    @Autowired
    private DeviceDataService deviceDataService;

    @Autowired
    private OslpSigningService oslpSigningService;

    private final ConcurrentMap<Integer, Channel> channelMap = new ConcurrentHashMap<>();

    public OslpChannelHandlerServer() {
        super(LOGGER);
    }

    private Channel findChannel(final Integer channelId) {
        return this.channelMap.get(channelId);
    }

    private void cacheChannel(final Integer channelId, final Channel channel) {
        this.channelMap.put(channelId, channel);
    }

    public void setDeviceManagementService(final DeviceManagementService deviceManagementService) {
        this.deviceManagementService = deviceManagementService;
    }

    public void setDeviceRegistrationService(final DeviceRegistrationService deviceRegistrationService) {
        this.deviceRegistrationService = deviceRegistrationService;
    }

    public void setSequenceNumberWindow(final Integer sequenceNumberWindow) {
        this.sequenceNumberWindow = sequenceNumberWindow;
    }

    public void setTimeZoneOffsetMinutes(final int timeZoneOffsetMinutes) {
        this.timeZoneOffsetMinutes = timeZoneOffsetMinutes;
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {

        final OslpEnvelope message = (OslpEnvelope) e.getMessage();
        this.logMessage(message, true);

        final Integer channelId = e.getChannel().getId();
        if (message.isValid()) {
            if (this.isOslpResponse(message)) {
                LOGGER.warn("{} Received OSLP Response, which is not expected: {}", channelId,
                        message.getPayloadMessage());
            } else {
                LOGGER.info("{} Received OSLP Request: {}", channelId, message.getPayloadMessage());

                // Response pay-load to send to device.
                Message payload = null;

                // Check which request the device has sent and handle it.
                if (message.getPayloadMessage().hasRegisterDeviceRequest()) {
                    payload = this.handleRegisterDeviceRequest(message.getDeviceId(), message.getSequenceNumber(),
                            message.getPayloadMessage().getRegisterDeviceRequest());
                } else if (message.getPayloadMessage().hasConfirmRegisterDeviceRequest()) {
                    payload = this.handleConfirmRegisterDeviceRequest(message.getDeviceId(),
                            message.getSequenceNumber(), message.getPayloadMessage().getConfirmRegisterDeviceRequest());
                } else if (message.getPayloadMessage().hasEventNotificationRequest()) {
                    payload = this.handleEventNotificationRequest(message.getDeviceId(), message.getSequenceNumber(),
                            message.getPayloadMessage().getEventNotificationRequest());
                } else {
                    LOGGER.warn("{} Received unknown payload. Received: {}.", channelId, message.getPayloadMessage()
                            .toString());
                    // Optional extra: return error code to device.
                    return;
                }

                // Cache the channel so we can write the response to it later.
                this.cacheChannel(channelId, e.getChannel());

                // Send message to signing server to get our response signed.
                this.oslpSigningService.buildAndSignEnvelope(message.getDeviceId(), message.getSequenceNumber(),
                        payload, channelId, this);
            }
        } else {
            LOGGER.warn("{} Received message wasn't properly secured.", channelId);
        }
    }

    /**
     * Called when a signed OSLP envelope arrives from signing server. The
     * envelope will be sent to the device which is waiting for a response. The
     * channel for the waiting device should be present in the channelMap.
     *
     * @param signedOslpEnvelopeDto
     *            DTO containing signed OslpEnvelope.
     */
    public void processSignedOslpEnvelope(final SignedOslpEnvelopeDto signedOslpEnvelopeDto) {

        // Try to find the channel.
        final Integer channelId = Integer.parseInt(signedOslpEnvelopeDto.getUnsignedOslpEnvelopeDto()
                .getCorrelationUid());
        final Channel channel = this.findChannel(channelId);
        if (channel == null) {
            LOGGER.error("Unable to find channel for channelId: {}. Can't send response message to device.", channelId);
            return;
        }

        // Get signed envelope, log it and send it to device.
        final OslpEnvelope response = signedOslpEnvelopeDto.getOslpEnvelope();
        this.logMessage(response, false);
        channel.write(response);

        LOGGER.info("{} Send OSLP Response: {}", channelId, response.getPayloadMessage());
    }

    private Oslp.Message handleRegisterDeviceRequest(final byte[] deviceUid, final byte[] sequenceNumber,
            final Oslp.RegisterDeviceRequest registerRequest) throws UnknownHostException {

        final String deviceIdentification = registerRequest.getDeviceIdentification();
        InetAddress inetAddress = InetAddress.getByAddress(registerRequest.getIpAddress().toByteArray());
        if (this.testDeviceId != null && this.testDeviceIp != null && deviceIdentification.equals(this.testDeviceId)) {
            LOGGER.info("Using testDeviceId: {} and testDeviceIp: {}", this.testDeviceId, this.testDeviceIp);
            inetAddress = InetAddress.getByName(this.testDeviceIp);
        }
        final String deviceType = registerRequest.getDeviceType().toString();
        final boolean hasSchedule = registerRequest.getHasSchedule();

        // Send message to OSGP-CORE to save IP Address, device type and has
        // schedule values in OSGP-CORE database.
        this.deviceRegistrationService.sendDeviceRegisterRequest(inetAddress, deviceType, hasSchedule,
                deviceIdentification);

        OslpDevice oslpDevice = this.oslpDeviceSettingsService.getDeviceByDeviceIdentification(registerRequest
                .getDeviceIdentification());

        // Save the security related values in the OSLP database.
        oslpDevice.updateRegistrationData(deviceUid, registerRequest.getDeviceType().toString(),
                Integer.valueOf(registerRequest.getRandomDevice()));
        oslpDevice.setSequenceNumber(SequenceNumberUtils.convertByteArrayToInteger(sequenceNumber));
        oslpDevice = this.oslpDeviceSettingsService.updateDevice(oslpDevice);

        // Return current date and time in UTC so the device can sync the clock.
        final Oslp.RegisterDeviceResponse.Builder responseBuilder = Oslp.RegisterDeviceResponse.newBuilder()
                .setStatus(Oslp.Status.OK).setCurrentTime(Instant.now().toString(format))
                .setRandomDevice(registerRequest.getRandomDevice()).setRandomPlatform(oslpDevice.getRandomPlatform());

        // Return local time zone information of the platform. Devices can use
        // this to convert UTC times to local times.
        final LocationInfo.Builder locationInfo = LocationInfo.newBuilder();
        locationInfo.setTimeOffset(this.timeZoneOffsetMinutes);

        // Get the GPS values from OSGP-CORE database.
        final GpsCoordinatesDto gpsCoordinates = this.deviceDataService
                .getGpsCoordinatesForDevice(deviceIdentification);

        // Add GPS information when available in meta data.
        if (gpsCoordinates != null && gpsCoordinates.getLatitude() != null && gpsCoordinates.getLongitude() != null) {
            final int latitude = (int) ((gpsCoordinates.getLatitude()) * 1000000);
            final int longitude = (int) ((gpsCoordinates.getLongitude()) * 1000000);
            locationInfo.setLatitude(latitude).setLongitude(longitude);
        }

        responseBuilder.setLocationInfo(locationInfo);

        return Oslp.Message.newBuilder().setRegisterDeviceResponse(responseBuilder.build()).build();
    }

    private Oslp.Message handleConfirmRegisterDeviceRequest(final byte[] deviceId, final byte[] sequenceNumber,
            final Oslp.ConfirmRegisterDeviceRequest confirmRegisterDeviceRequest) throws ProtocolAdapterException {

        try {
            this.deviceRegistrationService.confirmRegisterDevice(deviceId,
                    SequenceNumberUtils.convertByteArrayToInteger(sequenceNumber),
                    confirmRegisterDeviceRequest.getRandomDevice(), confirmRegisterDeviceRequest.getRandomPlatform());
        } catch (final Exception e) {
            LOGGER.error("handle confirm register device request exception", e);
            throw new ProtocolAdapterException("ConfirmRegisterDevice failed", e);
        }

        return Oslp.Message
                .newBuilder()
                .setConfirmRegisterDeviceResponse(
                        Oslp.ConfirmRegisterDeviceResponse.newBuilder().setStatus(Oslp.Status.OK)
                        .setRandomDevice(confirmRegisterDeviceRequest.getRandomDevice())
                        .setRandomPlatform(confirmRegisterDeviceRequest.getRandomPlatform())
                        .setSequenceWindow(this.sequenceNumberWindow)).build();
    }

    private Oslp.Message handleEventNotificationRequest(final byte[] deviceId, final byte[] sequenceNumber,
            final EventNotificationRequest request) throws ProtocolAdapterException {

        // Check & update sequence number first
        try {
            this.deviceRegistrationService.updateDeviceSequenceNumber(deviceId,
                    SequenceNumberUtils.convertByteArrayToInteger(sequenceNumber));
        } catch (final ProtocolAdapterException ex) {
            LOGGER.error("handle event notification request exception", ex);
            return Oslp.Message
                    .newBuilder()
                    .setEventNotificationResponse(
                            Oslp.EventNotificationResponse.newBuilder().setStatus(Oslp.Status.REJECTED)).build();
        }

        // Send event notifications to osgp core
        final Oslp.Status oslpStatus = Oslp.Status.OK;
        for (final EventNotification event : request.getNotificationsList()) {
            Integer index = null;
            if (!event.getIndex().isEmpty()) {
                index = (int) event.getIndex().byteAt(0);
            }
            // Determine if the event notification contains a timestamp. Older
            // version of OSLP don't use this variable.
            String timestamp = null;
            if (StringUtils.isNotEmpty(event.getTimestamp())) {
                timestamp = event.getTimestamp();
            }
            // Send the event notification to OSGP-CORE to save in the
            // database.
            this.deviceManagementService.addEventNotification(Base64.encodeBase64String(deviceId), event.getEvent()
                    .name(), event.getDescription(), index, timestamp);
        }

        return Oslp.Message.newBuilder()
                .setEventNotificationResponse(Oslp.EventNotificationResponse.newBuilder().setStatus(oslpStatus))
                .build();
    }
}
