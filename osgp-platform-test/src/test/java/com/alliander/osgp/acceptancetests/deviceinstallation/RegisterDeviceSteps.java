/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.deviceinstallation;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.givwenzen.annotations.DomainStep;
import org.givwenzen.annotations.DomainSteps;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.alliander.osgp.acceptancetests.OslpTestUtils;
import com.alliander.osgp.acceptancetests.ProtocolInfoTestUtils;
import com.alliander.osgp.adapter.protocol.oslp.application.services.DeviceRegistrationService;
import com.alliander.osgp.adapter.protocol.oslp.application.services.oslp.OslpDeviceSettingsService;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDeviceBuilder;
import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpChannelHandlerServer;
import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpSecurityHandler;
import com.alliander.osgp.core.db.api.domain.entities.DeviceDataBuilder;
import com.alliander.osgp.core.db.api.entities.Organisation;
import com.alliander.osgp.core.db.api.repositories.DeviceDataRepository;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceBuilder;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.services.SecurityService;
import com.alliander.osgp.logging.domain.repositories.DeviceLogItemRepository;
import com.alliander.osgp.oslp.Oslp.DeviceType;
import com.alliander.osgp.oslp.Oslp.LocationInfo;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.Oslp.RegisterDeviceRequest;
import com.alliander.osgp.oslp.Oslp.RegisterDeviceResponse;
import com.alliander.osgp.oslp.OslpEnvelope;
import com.google.protobuf.ByteString;

@Configurable
@DomainSteps
public class RegisterDeviceSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterDeviceSteps.class);

    // Device in Margraten
    private static final String DEVICE_UID = "AAAAAAAAAAYAAAAA";
    private static final String DEVICE_ID = "DEVICE-01";
    private static final Float DEVICE_GPS_LAT = 50.820463F;
    private static final Float DEVICE_GPS_LONG = 5.820993F;

    // Device in Arnhem
    private static final String ANOTHER_DEVICE_ID = "DEVICE-02";
    private static final Float ANOTHER_DEVICE_GPS_LAT = 51.985103F;
    private static final Float ANOTHER_DEVICE_GPS_LONG = 5.820993F;

    private static final double GPS_MULTIPLY_FACTOR = 1000000.0;
    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");

    // TODO - Add as parameters to tests
    private static final Boolean PUBLIC_KEY_PRESENT = true;
    private static final String PROTOCOL = "OSLP";
    private static final String PROTOCOL_VERSION = "1.0";

    private InetAddress inetAddress;

    private OslpEnvelope message;
    private RegisterDeviceRequest request;

    @Autowired
    private OslpDeviceSettingsService oslpDeviceSettingsService;

    private RegisterDeviceResponse response;

    private Device device;
    private Device anotherDevice;

    private final Random random = new Random();

    private OslpDevice oslpDevice;
    private com.alliander.osgp.core.db.api.entities.Device deviceData;
    private Organisation organisation = new Organisation("LianderNetManagement");

    @Autowired
    private ChannelHandlerContext channelHandlerContextMock;

    @Autowired
    private Channel channelMock;

    @Autowired
    private MessageEvent messageEvent;

    @Autowired
    private DeviceRepository deviceRepositoryMock;

    @Autowired
    private DeviceLogItemRepository deviceLogItemRepositoryMock;

    /*
     * this one has a different name now
     */
    @Autowired
    private OslpDeviceRepository oslpDeviceRepositoryMock;

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

    private final Integer sequenceNumberMaximum = OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW;

    private void setup() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        Mockito.reset(new Object[] { this.deviceRepositoryMock, this.deviceLogItemRepositoryMock, this.channelMock,
                this.oslpDeviceRepositoryMock, this.deviceDataRepositoryMock });

        OslpTestUtils.configureOslpChannelHandler(this.oslpChannelHandler);
        this.deviceRegistrationService.setSequenceNumberMaximum(OslpTestUtils.OSLP_SEQUENCE_NUMBER_MAXIMUM);
        this.deviceRegistrationService.setSequenceNumberWindow(OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW);
        this.oslpChannelHandler.setDeviceRegistrationService(this.deviceRegistrationService);
        this.oslpChannelHandler.setTimeZoneOffsetMinutes(OslpTestUtils.TIME_ZONE_OFFSET_MINUTES);
        this.oslpChannelHandler.setSequenceNumberWindow(OslpTestUtils.OSLP_SEQUENCE_NUMBER_WINDOW);

        if (this.oslpDeviceSettingsService == null) {
            LOGGER.error("OSLP Device Settings Service is null");
        }

        this.inetAddress = InetAddress.getByName("127.0.0.2");
    }

    // === GIVEN ===
    @DomainStep("a valid register device OSLP message")
    public void givenAValidRegisterDeviceRequest() throws NoSuchAlgorithmException, InvalidKeySpecException,
    IOException {
        LOGGER.info("GIVEN: \"a valid register device OSLP message\".");

        this.setup();

        this.request = RegisterDeviceRequest.newBuilder().setDeviceIdentification(DEVICE_ID)
                .setIpAddress(ByteString.copyFrom(new byte[] { 127, 0, 0, 2 })).setDeviceType(DeviceType.SSLD)
                .setHasSchedule(false).setRandomDevice(this.random.nextInt(this.sequenceNumberMaximum)).build();

        this.message = OslpTestUtils.createOslpEnvelopeBuilder().withDeviceId(Base64.decodeBase64(DEVICE_UID))
                .withSequenceNumber(new byte[] { 0, 1 })
                .withPayloadMessage(Message.newBuilder().setRegisterDeviceRequest(this.request).build()).build();

        when(this.messageEvent.getMessage()).thenReturn(this.message);
        when(this.messageEvent.getChannel()).thenReturn(this.channelMock);
    }

    @DomainStep("a device that is installed but has not registered before")
    public void givenAnInstalledDevice() {
        LOGGER.info("GIVEN: \"a device that is installed but has not registered before\".");
        this.device = new DeviceBuilder().withDeviceIdentification(DEVICE_ID).withNetworkAddress(this.inetAddress)
                .isActivated(false).withProtocolInfo(ProtocolInfoTestUtils.getProtocolInfo(PROTOCOL, PROTOCOL_VERSION))
                .withGps(DEVICE_GPS_LAT, DEVICE_GPS_LONG).build();
        this.oslpDevice = new OslpDeviceBuilder().withDeviceIdentification(DEVICE_ID).withDeviceUid(DEVICE_UID)
                .withPublicKey(OslpTestUtils.PUBLIC_KEY_BASE_64).build();
        this.deviceData = new DeviceDataBuilder().withDeviceIdentification(DEVICE_ID)
                .withOrganisation(this.organisation).withGps(DEVICE_GPS_LAT, DEVICE_GPS_LONG).build();
        when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(this.oslpDevice);
        when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.oslpDevice);
        when(this.oslpDeviceRepositoryMock.save(this.oslpDevice)).thenReturn(this.oslpDevice);
        when(this.deviceRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.device);
        when(this.deviceRepositoryMock.save(this.device)).thenReturn(this.device);
        when(this.deviceDataRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.deviceData);
    }

    @DomainStep("a device that is installed and has already registered on the platform")
    public void givenAnInstalledAndRegisteredDevice() {
        LOGGER.info("GIVEN: \"a device that is installed and has already registered on the platform\".");
        this.device = new DeviceBuilder().withDeviceIdentification(DEVICE_ID).withNetworkAddress(this.inetAddress)
                .isActivated(false).withProtocolInfo(ProtocolInfoTestUtils.getProtocolInfo(PROTOCOL, PROTOCOL_VERSION))
                .withGps(DEVICE_GPS_LAT, DEVICE_GPS_LONG).build();
        this.oslpDevice = new OslpDeviceBuilder().withDeviceIdentification(DEVICE_ID)
                .withDeviceIdentification(DEVICE_UID).withPublicKey(OslpTestUtils.PUBLIC_KEY_BASE_64).build();
        this.deviceData = new DeviceDataBuilder().withDeviceIdentification(DEVICE_ID)
                .withOrganisation(this.organisation).withGps(DEVICE_GPS_LAT, DEVICE_GPS_LONG).build();
        when(this.oslpDeviceRepositoryMock.findByDeviceUid(DEVICE_UID)).thenReturn(this.oslpDevice);
        when(this.oslpDeviceRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.oslpDevice);
        when(this.oslpDeviceRepositoryMock.save(this.oslpDevice)).thenReturn(this.oslpDevice);
        when(this.deviceRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.device);
        when(this.deviceRepositoryMock.save(this.device)).thenReturn(this.device);
        when(this.deviceDataRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.deviceData);
    }

    @DomainStep("the device has no metadata for GPS")
    public void givenDeviceWithoutGps() {
        LOGGER.info("GIVEN: \"the device has no metadata for GPS\".");
        this.device.updateMetaData(null, null, null, null, null, null);
        this.deviceData = new DeviceDataBuilder().withDeviceIdentification(DEVICE_ID).build();
        when(this.deviceDataRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(this.deviceData);
    }

    @DomainStep("a device that does not yet exist on the platform")
    public void givenAnNonExistingDevice() {
        LOGGER.info("GIVEN: \"a device that does not yet exist on the platform\".");
        when(this.deviceRepositoryMock.findByDeviceIdentification(DEVICE_ID)).thenReturn(null);
        when(this.deviceRepositoryMock.save(this.device)).thenReturn(this.device);
    }

    @DomainStep("another device exists using the same network address")
    public void givenAnotherDeviceExistsUsingTheSameNetworkAddress() {
        LOGGER.info("GIVEN: \"another device exists using the same network address\".");
        this.anotherDevice = new DeviceBuilder().withDeviceIdentification(ANOTHER_DEVICE_ID)
                .withNetworkAddress(this.inetAddress).isActivated(false)
                .withGps(ANOTHER_DEVICE_GPS_LAT, ANOTHER_DEVICE_GPS_LONG).build();
        when(this.deviceRepositoryMock.findByNetworkAddress(this.anotherDevice.getNetworkAddress())).thenReturn(
                Arrays.asList(this.device, this.anotherDevice));
    }

    // === WHEN ===

    @DomainStep("the register device request is received")
    public void whenTheRegisterDeviceRequestIsReceived() {
        LOGGER.info("WHEN: \"the register device request is received\".");

        try {
            this.oslpSecurityHandler.messageReceived(this.channelHandlerContextMock, this.messageEvent);
            this.oslpChannelHandler.messageReceived(this.channelHandlerContextMock, this.messageEvent);

            Thread.sleep(250);
        } catch (final Throwable t) {
            LOGGER.error("Caught exception: {}", t);
        }
    }

    // === THEN ===
    @DomainStep("the device should be updated with new deviceUID, IP address and device type")
    public boolean thenTheDeviceShouldBeUpdated() {
        LOGGER.info("THEN: \"the device should be updated with new deviceUID, IP address and device type\".");

        try {
            verify(this.oslpDeviceRepositoryMock, atLeastOnce()).save(any(OslpDevice.class));
            verify(this.deviceRepositoryMock, atLeastOnce()).save(any(Device.class));
        } catch (final Throwable t) {
            LOGGER.error("Failure: {}", t);
            return false;
        }
        return true;
    }

    @DomainStep("the device should be created without owner")
    public boolean thenTheDeviceShouldBeCreatedWithoutOwner() {
        LOGGER.info("THEN: \"the device should be created without owner\".");
        final Device expectedDevice = new DeviceBuilder().withDeviceIdentification(DEVICE_ID)
                .withNetworkAddress(InetAddress.getLoopbackAddress()).isActivated(true)
                .ofDeviceType(DeviceType.SSLD.toString()).build();

        try {
            verify(this.deviceRepositoryMock, times(1)).save(eq(expectedDevice));
        } catch (final Throwable t) {
            LOGGER.error("Failure: {}", t);
            return false;
        }
        return true;
    }

    @DomainStep("the device should not be created for security reasons")
    public boolean thenTheDeviceShouldNotBeCreated() {
        LOGGER.info("THEN: \"the device should not be created for security reasons\".");
        try {
            verify(this.deviceRepositoryMock, times(0)).save(any(Device.class));
        } catch (final Throwable t) {
            LOGGER.error("Failure: {}", t);
            return false;
        }
        return true;
    }

    @DomainStep("the network address for the other device should be cleared")
    public boolean thenTheNetworkAddressForTheOtherDeviceShouldBeCleared() {
        LOGGER.info("THEN: \"the network address for the other device should be cleared\".");
        try {
            Assert.assertNull(this.anotherDevice.getNetworkAddress());
            verify(this.deviceRepositoryMock, times(1)).save(eq(this.anotherDevice));
        } catch (final Throwable t) {
            LOGGER.error("Failure: {}", t);
            return false;
        }
        return true;
    }

    @DomainStep("the register device request should return a register device response")
    public boolean thenTheRegisterDeviceRequestShouldReturnARegisterDeviceResponse() {
        LOGGER.info("THEN: \"the register device request should return a register device response\".");
        this.response = null;
        OslpEnvelope responseMessage = null;

        final ArgumentCaptor<OslpEnvelope> responseCaptor = ArgumentCaptor.forClass(OslpEnvelope.class);

        try {
            verify(this.channelMock, times(1)).write(responseCaptor.capture());
        } catch (final Throwable t) {
            LOGGER.error("Failure: {}", t);
            return false;
        }

        // Store response for further verification
        responseMessage = responseCaptor.getValue();
        final Message payLoad = responseMessage.getPayloadMessage();
        Assert.assertTrue(payLoad.hasRegisterDeviceResponse());
        this.response = payLoad.getRegisterDeviceResponse();

        return true;
    }

    @DomainStep("the register device request should not return a response")
    public boolean thenTheRegisterDeviceRequestShouldNotReturnResponse() {
        LOGGER.info("THEN: \"the register device request should not return a response\".");

        try {
            verify(this.channelMock, times(0)).write(any(Device.class));
        } catch (final Throwable t) {
            LOGGER.error("Failure: {}", t);
            return false;
        }

        return true;
    }

    @DomainStep("the register response should contain the current UTC time")
    public boolean thenTheRegisterDeviceResponseShouldContainTheCurrentUtcTime() {
        LOGGER.info("THEN: \"the register response should contain the current UTC time\".");

        final String currentTime = this.response.getCurrentTime();
        Assert.assertNotNull(currentTime);
        final DateTime parsed = formatter.parseDateTime(currentTime);
        Assert.assertTrue(parsed.isBeforeNow() || parsed.isEqualNow());

        return true;
    }

    @DomainStep("the register response should contain GPS location from metadata")
    public boolean thenTheRegisterDeviceResponseShouldContainGpsLocationFromMetadata() {
        LOGGER.info("THEN: \"the register response should contain GPS location from metadata\".");

        Assert.assertTrue(this.response.hasLocationInfo());
        final LocationInfo location = this.response.getLocationInfo();
        Assert.assertEquals(DEVICE_GPS_LAT, location.getLatitude() / GPS_MULTIPLY_FACTOR, 0.001);
        Assert.assertEquals(DEVICE_GPS_LONG, location.getLongitude() / GPS_MULTIPLY_FACTOR, 0.001);
        return true;
    }

    @DomainStep("the register response should NOT contain GPS location from metadata")
    public boolean thenTheRegisterDeviceResponseShouldNotContainGpsLocationFromMetadata() {
        LOGGER.info("THEN: \"the register response should NOT contain GPS location from metadata\".");

        Assert.assertTrue(this.response.hasLocationInfo());
        final LocationInfo location = this.response.getLocationInfo();
        Assert.assertFalse(location.hasLatitude());
        Assert.assertFalse(location.hasLongitude());
        return true;
    }

    @DomainStep("the register response should contain timezone of platform")
    public boolean thenTheRegisterDeviceResponseShouldContainTimezoneOfPlatform() {
        LOGGER.info("THEN: \"the register response should contain timezone of platform\".");

        Assert.assertTrue(this.response.hasLocationInfo());
        final LocationInfo location = this.response.getLocationInfo();

        // Offset should be +1 hour
        Assert.assertEquals(1 * DateTimeConstants.MINUTES_PER_HOUR, location.getTimeOffset());
        return true;
    }
}
