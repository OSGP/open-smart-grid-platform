/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.basicfunctions;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.apache.commons.codec.binary.Base64;
import org.givwenzen.annotations.DomainStep;
import org.givwenzen.annotations.DomainSteps;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.junit.Assert;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.alliander.osgp.acceptancetests.OslpTestUtils;
import com.alliander.osgp.adapter.protocol.oslp.application.services.DeviceRegistrationService;
import com.alliander.osgp.adapter.protocol.oslp.application.services.oslp.OslpDeviceSettingsService;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDeviceBuilder;
import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpChannelHandlerServer;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpSecurityHandler;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.SequenceNumberUtils;
import com.alliander.osgp.core.db.api.repositories.DeviceDataRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.services.SecurityService;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;
import com.alliander.osgp.oslp.Oslp.ConfirmRegisterDeviceRequest;
import com.alliander.osgp.oslp.Oslp.DeviceType;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.Oslp.RegisterDeviceRequest;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.google.protobuf.ByteString;

@Configurable
@DomainSteps
public class ProtocolSequenceNumberSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolSequenceNumberSteps.class);

    // Device in Margraten
    private static final String DEVICE_UID = "AAAAAAAAAAYAAAAA";
    private static final String DEVICE_ID = "DEVICE-01";

    private OslpEnvelope message;
    private RegisterDeviceRequest registerDeviceRequest;
    private ConfirmRegisterDeviceRequest confirmRegisterDeviceRequest;

    private OslpDevice oslpDevice;

    private final Integer deviceSequenceNumberInitial = 1;
    private final Integer deviceSequenceNumberIncremented = 2;
    private final byte[] deviceSequenceNumberBytesInitial = new byte[] { 0, 1 };
    private final byte[] deviceSequenceNumberBytesIncremented = new byte[] { 0, 2 };

    private Integer randomDevice;
    private Integer randomPlatform;

    private Integer messageSequenceNumber;
    private Integer osgpSequenceNumber;

    @Autowired
    private OslpDeviceSettingsService oslpDeviceSettingsService;

    @Autowired
    private ChannelHandlerContext channelHandlerContextMock;

    @Autowired
    private Channel channelMock;

    @Autowired
    private MessageEvent messageEvent;

    @Autowired
    private DeviceRepository deviceRepositoryMock;

    @Autowired
    private OslpDeviceRepository oslpDeviceRepositoryMock;

    @Autowired
    private DeviceLogItemRepository deviceLogItemRepositoryMock;

    @Autowired
    private DeviceDataRepository deviceDataRepositoryMock;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private DeviceRegistrationService deviceRegistrationService;

    @Autowired
    private OslpSecurityHandler oslpSecurityHandler;

    @Autowired
    private OslpChannelHandlerServer oslpChannelHandler;

    private void setup() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.deviceLogItemRepositoryMock, this.channelMock,
                this.oslpDeviceRepositoryMock, this.deviceDataRepositoryMock });

        OslpTestUtils.configureOslpChannelHandler(this.oslpChannelHandler);
        this.deviceRegistrationService.setSequenceNumberMaximum(OslpTestUtils.OSLP_SEQUENCE_NUMBER_MAXIMUM);
        this.deviceRegistrationService.setSequenceNumberWindow(OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW);
        this.oslpChannelHandler.setDeviceRegistrationService(this.deviceRegistrationService);
        this.oslpChannelHandler.setTimeZoneOffsetMinutes(OslpTestUtils.TIME_ZONE_OFFSET_MINUTES);
        this.oslpChannelHandler.setSequenceNumberWindow(OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW);

        this.messageSequenceNumber = null;
        this.osgpSequenceNumber = null;

        this.randomDevice = 1;
        this.randomPlatform = 1;
    }

    // === GIVEN ===

    @DomainStep("a valid register device OSLP message")
    public void givenAValidRegisterDeviceRequest() throws NoSuchAlgorithmException, InvalidKeySpecException,
            IOException {
        LOGGER.info("GIVEN: \"a valid register device OSLP message\".");
        this.setup();

        this.registerDeviceRequest = RegisterDeviceRequest.newBuilder().setDeviceIdentification(DEVICE_ID)
                .setIpAddress(ByteString.copyFrom(new byte[] { 127, 0, 0, 1 })).setDeviceType(DeviceType.SSLD)
                .setHasSchedule(false).setRandomDevice(this.randomDevice).build();

        this.message = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withSequenceNumber(this.deviceSequenceNumberBytesInitial)
                .withPayloadMessage(Message.newBuilder().setRegisterDeviceRequest(this.registerDeviceRequest).build())
                .build();

        when(this.messageEvent.getMessage()).thenReturn(this.message);
        when(this.messageEvent.getChannel()).thenReturn(this.channelMock);
    }

    @DomainStep("a valid confirm device registration OSLP message")
    public void givenAValidConfirmDeviceRegistrationRequest() throws NoSuchAlgorithmException, InvalidKeySpecException,
            IOException {
        LOGGER.info("GIVEN: \"a valid confirm device registration OSLP message\".");
        this.setup();

        this.confirmRegisterDeviceRequest = ConfirmRegisterDeviceRequest.newBuilder()
                .setRandomDevice(this.randomDevice).setRandomPlatform(this.randomPlatform).build();

        this.message = OslpTestUtils
                .createOslpEnvelopeBuilder()
                .withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withSequenceNumber(this.deviceSequenceNumberBytesIncremented)
                .withPayloadMessage(
                        Message.newBuilder().setConfirmRegisterDeviceRequest(this.confirmRegisterDeviceRequest).build())
                .build();

        when(this.messageEvent.getMessage()).thenReturn(this.message);
        when(this.messageEvent.getChannel()).thenReturn(this.channelMock);
    }

    @DomainStep("a valid confirm device registration OSLP message with sequence number (.*)")
    public void givenAValidConfirmDeviceRegistrationRequestWithSequenceNumber(final Integer messageSequenceNumber)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        LOGGER.info("GIVEN: \"a valid confirm device registration OSLP message with sequence number [{}]\".",
                messageSequenceNumber);
        this.setup();

        this.messageSequenceNumber = messageSequenceNumber;

        this.confirmRegisterDeviceRequest = ConfirmRegisterDeviceRequest.newBuilder()
                .setRandomDevice(this.randomDevice).setRandomPlatform(this.randomPlatform).build();

        final byte[] sequenceNumberBytes = SequenceNumberUtils.convertIntegerToByteArray(messageSequenceNumber);

        this.message = OslpTestUtils
                .createOslpEnvelopeBuilder()
                .withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withSequenceNumber(sequenceNumberBytes)
                .withPayloadMessage(
                        Message.newBuilder().setConfirmRegisterDeviceRequest(this.confirmRegisterDeviceRequest).build())
                .build();

        when(this.messageEvent.getMessage()).thenReturn(this.message);
        when(this.messageEvent.getChannel()).thenReturn(this.channelMock);
    }

    @DomainStep("an existing device with initial sequence number")
    public void givenAnExistingDeviceWithInitalSequenceNumber() {
        LOGGER.info("GIVEN: \"an existing device with initial sequence number\".");
        this.oslpDevice = new OslpDeviceBuilder().withDeviceIdentification(DEVICE_ID).build();
        when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.oslpDevice);
        when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(this.oslpDevice);
        when(this.oslpDeviceRepositoryMock.save(this.oslpDevice)).thenReturn(this.oslpDevice);
    }

    @DomainStep("an existing device with incremented sequence number")
    public void givenAnExistingDeviceWithIncrementedSequenceNumber() {
        LOGGER.info("GIVEN: \"an existing device with incremented sequence number\".");
        this.oslpDevice = new OslpDeviceBuilder().withDeviceIdentification(DEVICE_ID).build();
        when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.oslpDevice);
        when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(this.oslpDevice);
        when(this.oslpDeviceRepositoryMock.save(this.oslpDevice)).thenReturn(this.oslpDevice);
    }

    @DomainStep("an existing osgp device with sequence number (.*)")
    public void givenAnExistingDeviceWithSequenceNumber(final Integer osgpSequenceNumber) {
        LOGGER.info("GIVEN: \"an existing device with sequence number [{}]\".", osgpSequenceNumber);

        this.osgpSequenceNumber = osgpSequenceNumber;

        this.oslpDevice = new OslpDeviceBuilder().withDeviceIdentification(DEVICE_ID)
                .withSequenceNumber(osgpSequenceNumber).build();
        when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.oslpDevice);
        when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(this.oslpDevice);
        when(this.oslpDeviceRepositoryMock.save(this.oslpDevice)).thenReturn(this.oslpDevice);
    }

    @DomainStep("an existing osgp device with invalid randomPlatform")
    public void givenAnExistingDeviceWithInvalidRandomPlatform() {
        LOGGER.info("GIVEN: \"an existing device with invalid randomPlatform\".");

        this.randomPlatform = -1;

        this.oslpDevice = new OslpDeviceBuilder().withDeviceIdentification(DEVICE_ID)
                .withRandomPlatform(this.randomPlatform).build();
        when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.oslpDevice);
        when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(this.oslpDevice);
        when(this.oslpDeviceRepositoryMock.save(this.oslpDevice)).thenReturn(this.oslpDevice);
    }

    @DomainStep("an existing osgp device with invalid randomDevice")
    public void givenAnExistingDeviceWithInvalidRandomDevice() {
        LOGGER.info("GIVEN: \"an existing device with invalid randomDevice\".");

        this.randomDevice = -1;

        this.oslpDevice = new OslpDeviceBuilder().withDeviceIdentification(DEVICE_ID)
                .withRandomDevice(this.randomDevice).build();
        when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.oslpDevice);
        when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(this.oslpDevice);
        when(this.oslpDeviceRepositoryMock.save(this.oslpDevice)).thenReturn(this.oslpDevice);
    }

    @DomainStep("an osgp configuration with sequence window (.*)")
    public void givenAnOsgpConfigurationWithSequenceWindow(final Integer osgpSequenceWindow) {
        LOGGER.info("GIVEN: \"an osgp configuration with sequence window [{}]\".", osgpSequenceWindow);
        this.deviceRegistrationService.setSequenceNumberWindow(osgpSequenceWindow);
        this.oslpChannelHandler.setSequenceNumberWindow(osgpSequenceWindow);
    }

    // === WHEN ===

    @DomainStep("the register device request is received")
    public void whenTheRegisterDeviceRequestIsReceived() {
        LOGGER.info("WHEN: \"the register device request is received\".");
        try {
            OslpTestUtils.onMessageReceivedWrapper(this.message, this.oslpChannelHandler,
                    this.channelHandlerContextMock, this.messageEvent);
        } catch (final Throwable t) {
        }
    }

    @DomainStep("the confirm device registration request is received")
    public void whenTheConfirmDeviceRegistrationIsReceived() {
        LOGGER.info("WHEN: \"the confirm device registration request is received\".");
        try {
            this.oslpDevice.setRandomDevice(this.randomDevice);
            this.oslpDevice.setRandomPlatform(this.randomPlatform);

            OslpTestUtils.onMessageReceivedWrapper(this.message, this.oslpChannelHandler,
                    this.channelHandlerContextMock, this.messageEvent);

        } catch (final Throwable t) {
            LOGGER.error("Failure: {}", t);
        }
    }

    // === THEN ===

    @DomainStep("the device should contain an expected - equal to init - sequence number")
    public boolean thenTheDeviceShouldBeUpdated() {
        LOGGER.info("THEN: \"the device should contain an expected (equal to init) sequence number\".");

        try {
            verify(this.oslpDeviceRepositoryMock, times(1)).save(this.oslpDevice);
            Assert.assertTrue(this.oslpDevice.getSequenceNumber() - this.deviceSequenceNumberInitial == 0);
        } catch (final Throwable t) {
            LOGGER.error("Failure: {}", t);
            return false;
        }
        return true;
    }

    @DomainStep("the device should be updated (.*)")
    public boolean thenTheDeviceShouldBeUpdated(final boolean isUpdated) {
        LOGGER.info("THEN: \"the device should be updated [{}]\".", isUpdated);
        try {
            verify(this.oslpDeviceRepositoryMock, times(isUpdated ? 1 : 0)).save(this.oslpDevice);
        } catch (final Throwable t) {
            LOGGER.error("Failure: {}", t);
            return false;
        }
        return true;
    }

    @DomainStep("the device should have updated the sequence number (.*)")
    public boolean thenTheDeviceShouldHaveUpdatedTheSequenceNumber(final boolean isUpdated) {
        LOGGER.info("THEN: \"the device should have updated the sequence number [{}]\".", isUpdated);
        try {
            if (isUpdated) {
                Assert.assertEquals("Device sequence number should be updated", this.messageSequenceNumber,
                        this.oslpDevice.getSequenceNumber());
            } else {
                Assert.assertEquals("Device sequence number should not be updated", this.osgpSequenceNumber,
                        this.oslpDevice.getSequenceNumber());
            }
        } catch (final Throwable t) {
            LOGGER.error("Failure: {}", t);
            return false;
        }
        return true;
    }

    @DomainStep("the device should have both random values set")
    public boolean thenTheDeviceShouldHaveBothRandomValuesSet() {
        LOGGER.info("THEN: \"both 'randomDevice' and 'randomPlatform' should be set\".");

        try {
            Assert.assertNotNull("The variable 'randomDevice' is expected to be present.",
                    this.oslpDevice.getRandomDevice());
            Assert.assertNotNull("The variable 'randomPlatform' is expected to be present.",
                    this.oslpDevice.getRandomPlatform());

            this.randomDevice = this.oslpDevice.getRandomDevice();
            this.randomPlatform = this.oslpDevice.getRandomPlatform();
        } catch (final Throwable t) {
            LOGGER.error("Failure: {}", t);
            return false;
        }
        return true;
    }

    @DomainStep("the device should contain an expected - incremented - sequence number")
    public boolean thenTheSequenceNumberShouldContainIncrementedSequenceNumber() {
        LOGGER.info("THEN: \"the device should contain an expected (incremented) sequence number\".");

        try {
            verify(this.oslpDeviceRepositoryMock, times(1)).save(this.oslpDevice);
            Assert.assertTrue(this.oslpDevice.getSequenceNumber() - this.deviceSequenceNumberIncremented == 0);
        } catch (final Throwable t) {
            LOGGER.error("Failure: {} Expected sequence number: {} Actual sequence number: {}", t,
                    this.deviceSequenceNumberIncremented, this.oslpDevice.getSequenceNumber());
            return false;
        }
        return true;
    }

    @DomainStep("the device should not be updated")
    public boolean thenTheDeviceShouldNotBeUpdated() {
        LOGGER.info("THEN: \"the device should not be updated\".");

        try {
            verify(this.oslpDeviceRepositoryMock, times(0)).save(this.oslpDevice);
        } catch (final Throwable t) {
            LOGGER.error("Device should not be updated. Failure: {}", t);
            return false;
        }
        return true;
    }
}
