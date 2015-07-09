/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.devicemanagement;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.givwenzen.annotations.DomainStep;
import org.givwenzen.annotations.DomainSteps;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.alliander.osgp.acceptancetests.OslpTestUtils;
import com.alliander.osgp.acceptancetests.ProtocolInfoTestUtils;
import com.alliander.osgp.adapter.protocol.oslp.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.protocol.oslp.application.services.DeviceRegistrationService;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDeviceBuilder;
import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpChannelHandlerServer;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceBuilder;
import com.alliander.osgp.domain.core.entities.EventBuilder;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.EventRepository;
import com.alliander.osgp.domain.core.valueobjects.EventType;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;
import com.alliander.osgp.oslp.Oslp.Event;
import com.alliander.osgp.oslp.Oslp.EventNotification;
import com.alliander.osgp.oslp.Oslp.EventNotificationRequest;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.google.protobuf.ByteString;

@Configurable
@DomainSteps
public class ReceiveEventNotificationsSteps {

    private static final String DEVICE_UID = "AAAAAAAAAAYAAAAA";
    private static final String EMPTY_INDEX = "EMPTY";
    private static final String DESCRIPTION = "dummy";

    // TODO - Add as parameters to tests
    private static final Boolean PUBLIC_KEY_PRESENT = true;
    private static final String PROTOCOL = "OSLP";
    private static final String PROTOCOL_VERSION = "1.0";

    // === OSGP fields ===

    @Autowired
    private DeviceRepository deviceRepositoryMock;
    private Device device;

    @Autowired
    private EventRepository eventRepositoryMock;

    // === OSLP fields ===

    @Autowired
    private OslpDeviceRepository oslpDeviceRepositoryMock;
    private OslpDevice oslpDevice;

    @Autowired
    private DeviceLogItemRepository deviceLogItemRepositoryMock;

    @Autowired
    private ChannelHandlerContext channelHandlerContextMock;

    @Autowired
    private MessageEvent messageEvent;

    @Autowired
    private Channel channelMock;

    @Autowired
    private OslpChannelHandlerServer oslpChannelHandler;

    @Autowired
    private DeviceManagementService deviceManagementService;

    @Autowired
    private DeviceRegistrationService deviceRegistrationService;

    private final Integer sequenceNumberWindow = OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW;
    private final Integer sequenceNumberMaximum = OslpTestUtils.OSLP_SEQUENCE_NUMBER_MAXIMUM;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveEventNotificationsSteps.class);
    private OslpEnvelope message;
    private EventNotificationRequest request;

    private Throwable throwable;

    @DomainStep("a registered device (.*)")
    public void givenARegisteredDevice(final String deviceIdentification) throws NoSuchAlgorithmException,
            InvalidKeySpecException, IOException {

        LOGGER.info("GIVEN: \"a registered device\".");

        this.setup();

        this.device = new DeviceBuilder().withDeviceIdentification(deviceIdentification)
                .withNetworkAddress(InetAddress.getLoopbackAddress()).withPublicKeyPresent(PUBLIC_KEY_PRESENT)
                .withProtocolInfo(ProtocolInfoTestUtils.getProtocolInfo(PROTOCOL, PROTOCOL_VERSION)).isActivated(true)
                .build();

        this.oslpDevice = new OslpDeviceBuilder().withDeviceIdentification(deviceIdentification)
                .withDeviceUid(DEVICE_UID).build();

        when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(any(String.class))).thenReturn(this.oslpDevice);
        when(this.oslpDeviceRepositoryMock.findByDeviceUid(any(String.class))).thenReturn(this.oslpDevice);
        when(this.oslpDeviceRepositoryMock.save(this.oslpDevice)).thenReturn(this.oslpDevice);
        when(this.deviceRepositoryMock.findByDeviceIdentification(any(String.class))).thenReturn(this.device);
        when(this.deviceRepositoryMock.save(this.device)).thenReturn(this.device);
    }

    @DomainStep("a unregistered device (.*)")
    public void givenAUnregisteredDevice(final String device) throws NoSuchAlgorithmException, InvalidKeySpecException,
            IOException {

        LOGGER.info("GIVEN: \"a unregistered device\".");

        this.setup();

        when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(null);
        when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(device)).thenReturn(null);
    }

    @DomainStep("a OSLP event notification message with (.*) and (.*) and (.*)")
    public void givenAOslpEventNotification(final String event, final String description, final String index)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {

        LOGGER.info("GIVEN: \"a OSLP event notification message with {} and {} and {}\".", event, description, index);

        final EventNotification.Builder builder = EventNotification.newBuilder().setEvent(Event.valueOf(event))
                .setDescription(description);

        // Fill index parameter if present
        if (!index.equals(EMPTY_INDEX)) {
            final Integer numericIndex = Integer.parseInt(index);
            builder.setIndex(ByteString.copyFrom(new byte[] { numericIndex.byteValue() }));
        }

        this.request = EventNotificationRequest.newBuilder().addNotifications(builder).build();

        this.setupMessage();
    }

    @DomainStep("a OSLP event notification message with multiple (.*) and (.*)")
    public void givenOslpEventNotificationMessageWithMultipleTypes(final String[] eventtypes, final Integer[] indexes)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {

        LOGGER.info("GIVEN: \"a OSLP event notification message with multiple event types and indexes\".");

        final EventNotificationRequest.Builder requestBuilder = EventNotificationRequest.newBuilder();
        for (int i = 0; i < eventtypes.length; i++) {
            final EventNotification.Builder builder = EventNotification.newBuilder()
                    .setEvent(Event.valueOf(eventtypes[i])).setDescription(DESCRIPTION)
                    .setIndex(ByteString.copyFrom(new byte[] { indexes[i].byteValue() }));

            requestBuilder.addNotifications(builder);
        }

        this.request = requestBuilder.build();
        this.setupMessage();
    }

    @DomainStep("the OSLP event notification message is received")
    public void whenTheOslpEventNotificationMessageIsReceived() {

        LOGGER.info("WHEN: \"the OSLP event notification message is received\".");

        try {
            OslpTestUtils.onMessageReceivedWrapper(this.message, this.oslpChannelHandler,
                    this.channelHandlerContextMock, this.messageEvent);
        } catch (final Throwable t) {
            LOGGER.error("Failure: {}", t);
            this.throwable = t;
        }
    }

    @DomainStep("the OSLP event notification message is stored on OSGP")
    public boolean thenTheOslpEventNotificationMessageIsStoredOnOsgp() {

        LOGGER.info("THEN: \"the OSLP event notification message is stored\".");

        // Build in delay for preventing failed tests...
        try {
            Thread.sleep(1000);
        } catch (final InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final ByteString index = this.request.getNotifications(0).getIndex();
        if (index.isEmpty()) {
            return this.sendEventNotification(1, new Integer[] { null });
        }

        return this.sendEventNotification(1, new Integer[] { (int) index.byteAt(0) });
    }

    @DomainStep("(.*) OSLP event notification messages is stored on OSGP each with (.*)")
    public boolean thenNumberOfEventsIsStored(final int numberOfEvents, final Integer[] expectedIndexes) {

        LOGGER.info("THEN: \"number of events is stored on OSGP\".");

        try {
            Thread.sleep(250);
        } catch (final InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return this.sendEventNotification(numberOfEvents, expectedIndexes);
    }

    @DomainStep("the OSGP sends an OSLP event notification message with status (.*)")
    public boolean thenTheOsgpSendsAnOslpEventNotificationMessageWithStatus(final String status) {

        LOGGER.info("THEN: \"the OSGP sends an OSLP event notification message with status {}\".", status);

        try {
            verify(this.channelMock, timeout(1000).times(1)).write(any(OslpEnvelope.class));
        } catch (final Throwable t) {
            LOGGER.error("Failure: {}", t);
            return false;
        }

        return this.throwable == null;
    }

    @DomainStep("the OSLP event notification message is not stored on OSGP")
    public boolean thenTheOslpEventNotificationMessageIsNotStoredOnOsgp() {

        LOGGER.info("THEN: \"the OSLP event notification message is not stored on OSGP\".");

        try {
            verify(this.oslpDeviceRepositoryMock, atLeastOnce()).findByDeviceUid(any(String.class));
            verify(this.eventRepositoryMock, times(0)).save(any(com.alliander.osgp.domain.core.entities.Event.class));
        } catch (final Throwable t) {
            LOGGER.error("Failure: {}", t);
            return false;
        }
        return true;
    }

    private void setup() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {

        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.deviceLogItemRepositoryMock, this.channelMock,
                this.oslpDeviceRepositoryMock, this.eventRepositoryMock });

        OslpTestUtils.configureOslpChannelHandler(this.oslpChannelHandler);
        this.oslpChannelHandler.setDeviceManagementService(this.deviceManagementService);
        this.deviceRegistrationService.setSequenceNumberMaximum(this.sequenceNumberMaximum);
        this.deviceRegistrationService.setSequenceNumberWindow(this.sequenceNumberWindow);
        this.oslpChannelHandler.setDeviceRegistrationService(this.deviceRegistrationService);
        this.oslpChannelHandler.setSequenceNumberWindow(this.sequenceNumberWindow);

        this.device = null;
        this.oslpDevice = null;
        this.request = null;
        this.message = null;
        this.throwable = null;

    }

    private void setupMessage() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        this.message = OslpTestUtils.createOslpEnvelopeBuilder()
                .withDeviceId(Base64.decodeBase64(ReceiveEventNotificationsSteps.DEVICE_UID))
                .withPayloadMessage(Message.newBuilder().setEventNotificationRequest(this.request).build()).build();

        when(this.messageEvent.getMessage()).thenReturn(this.message);
        when(this.messageEvent.getChannel()).thenReturn(this.channelMock);
    }

    private boolean sendEventNotification(final int numberOfEvents, final Integer[] expectedIndexes) {
        if (this.request.getNotificationsCount() != numberOfEvents) {
            LOGGER.error("Expected {} event notifications in request, but {} are present.", numberOfEvents,
                    this.request.getNotificationsCount());
            return false;
        }

        final List<com.alliander.osgp.domain.core.entities.Event> expectedEvents = new ArrayList<com.alliander.osgp.domain.core.entities.Event>();
        for (int i = 0; i < this.request.getNotificationsList().size(); i++) {
            final EventNotification event = this.request.getNotifications(i);
            final com.alliander.osgp.domain.core.entities.EventBuilder expectedEvent = new EventBuilder()
                    .withDevice(this.device).withEventType(EventType.valueOf(event.getEvent().name()))
                    .withDescription(event.getDescription()).withIndex(expectedIndexes[i]);
            expectedEvents.add(expectedEvent.build());
        }

        try {
            verify(this.eventRepositoryMock, times(numberOfEvents)).save(
                    any(com.alliander.osgp.domain.core.entities.Event.class));
            for (final com.alliander.osgp.domain.core.entities.Event event : expectedEvents) {
                verify(this.eventRepositoryMock, times(1)).save(eq(event));
            }

            //            verify(this.deviceRepositoryMock, times(1)).save(this.device);
        } catch (final Throwable t) {
            LOGGER.error("Failure: {}", t);
            return false;
        }
        return true;

    }
}
