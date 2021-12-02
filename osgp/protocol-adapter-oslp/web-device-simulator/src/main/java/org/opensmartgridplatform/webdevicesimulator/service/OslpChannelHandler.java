/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator.service;

import com.google.protobuf.ByteString;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.opensmartgridplatform.oslp.Oslp;
import org.opensmartgridplatform.oslp.Oslp.ConfirmRegisterDeviceResponse;
import org.opensmartgridplatform.oslp.Oslp.DaliConfiguration;
import org.opensmartgridplatform.oslp.Oslp.GetFirmwareVersionResponse;
import org.opensmartgridplatform.oslp.Oslp.GetStatusResponse;
import org.opensmartgridplatform.oslp.Oslp.IndexAddressMap;
import org.opensmartgridplatform.oslp.Oslp.LightValue;
import org.opensmartgridplatform.oslp.Oslp.LightValue.Builder;
import org.opensmartgridplatform.oslp.Oslp.LongTermIntervalType;
import org.opensmartgridplatform.oslp.Oslp.Message;
import org.opensmartgridplatform.oslp.Oslp.MeterType;
import org.opensmartgridplatform.oslp.Oslp.RelayConfiguration;
import org.opensmartgridplatform.oslp.Oslp.RelayType;
import org.opensmartgridplatform.oslp.Oslp.SetEventNotificationsRequest;
import org.opensmartgridplatform.oslp.Oslp.SetEventNotificationsResponse;
import org.opensmartgridplatform.oslp.Oslp.SetLightRequest;
import org.opensmartgridplatform.oslp.Oslp.SetLightResponse;
import org.opensmartgridplatform.oslp.Oslp.SetScheduleRequest;
import org.opensmartgridplatform.oslp.Oslp.SetScheduleResponse;
import org.opensmartgridplatform.oslp.Oslp.SetTransitionRequest;
import org.opensmartgridplatform.oslp.Oslp.StartSelfTestResponse;
import org.opensmartgridplatform.oslp.Oslp.StopSelfTestResponse;
import org.opensmartgridplatform.oslp.Oslp.TransitionType;
import org.opensmartgridplatform.oslp.Oslp.UpdateFirmwareRequest;
import org.opensmartgridplatform.oslp.Oslp.UpdateFirmwareResponse;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.oslp.OslpUtils;
import org.opensmartgridplatform.webdevicesimulator.application.services.DeviceManagementService;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.Device;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.DeviceMessageStatus;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.DeviceOutputSetting;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.OslpLogItem;
import org.opensmartgridplatform.webdevicesimulator.domain.repositories.OslpLogItemRepository;
import org.opensmartgridplatform.webdevicesimulator.domain.valueobjects.LightType;
import org.opensmartgridplatform.webdevicesimulator.domain.valueobjects.LinkType;
import org.opensmartgridplatform.webdevicesimulator.domain.valueobjects.OutputType;
import org.opensmartgridplatform.webdevicesimulator.domain.valueobjects.ProtocolType;
import org.opensmartgridplatform.webdevicesimulator.exceptions.DeviceSimulatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

@Sharable
public class OslpChannelHandler extends SimpleChannelInboundHandler<OslpEnvelope> {

  private static final Logger LOGGER = LoggerFactory.getLogger(OslpChannelHandler.class);

  private static final SortedSet<String> REBOOTING_DEVICE_IDS =
      Collections.synchronizedSortedSet(new TreeSet<>());

  private final Lock lock = new ReentrantLock();
  private final ConcurrentMap<String, Callback> callbacks = new ConcurrentHashMap<>();
  private final List<OutOfSequenceEvent> outOfSequenceList = new ArrayList<>();
  private final Random random = new Random();
  @Autowired private OslpLogItemRepository oslpLogItemRepository;
  @Autowired private PrivateKey privateKey;
  @Autowired private String oslpSignatureProvider;
  @Autowired private String oslpSignature;
  @Autowired private int connectionTimeout;
  @Autowired private String configurationIpConfigFixedIpAddress;
  @Autowired private String configurationIpConfigNetmask;
  @Autowired private String configurationIpConfigGateway;
  @Autowired private String configurationOsgpIpAddress;
  @Autowired private Integer configurationOsgpPortNumber;
  @Autowired private String statusInternalIpAddress;
  @Autowired private Bootstrap bootstrap;
  @Autowired private DeviceManagementService deviceManagementService;
  @Autowired private RegisterDevice registerDevice;
  @Autowired private Integer sequenceNumberWindow;
  @Autowired private Integer sequenceNumberMaximum;
  @Autowired private Long responseDelayTime;
  @Autowired private Long reponseDelayRandomRange;

  private static Message createConfirmRegisterDeviceResponse(
      final int randomDevice, final int randomPlatform) {
    return Oslp.Message.newBuilder()
        .setConfirmRegisterDeviceResponse(
            ConfirmRegisterDeviceResponse.newBuilder()
                .setRandomDevice(randomDevice)
                .setRandomPlatform(randomPlatform)
                .setStatus(Oslp.Status.OK))
        .build();
  }

  private static Message createStartSelfTestResponse() {
    return Oslp.Message.newBuilder()
        .setStartSelfTestResponse(StartSelfTestResponse.newBuilder().setStatus(Oslp.Status.OK))
        .build();
  }

  private static Message createStopSelfTestResponse() {
    return Oslp.Message.newBuilder()
        .setStopSelfTestResponse(
            StopSelfTestResponse.newBuilder()
                .setStatus(Oslp.Status.OK)
                .setSelfTestResult(ByteString.copyFrom(new byte[] {0})))
        .build();
  }

  private static Message createSetLightResponse() {
    return Oslp.Message.newBuilder()
        .setSetLightResponse(SetLightResponse.newBuilder().setStatus(Oslp.Status.OK))
        .build();
  }

  private static Message createSetEventNotificationsResponse() {
    return Oslp.Message.newBuilder()
        .setSetEventNotificationsResponse(
            SetEventNotificationsResponse.newBuilder().setStatus(Oslp.Status.OK))
        .build();
  }

  private static Message createUpdateFirmwareResponse() {
    return Oslp.Message.newBuilder()
        .setUpdateFirmwareResponse(UpdateFirmwareResponse.newBuilder().setStatus(Oslp.Status.OK))
        .build();
  }

  private static Message createGetFirmwareVersionResponse(final String firmwareVersion) {
    return Oslp.Message.newBuilder()
        .setGetFirmwareVersionResponse(
            GetFirmwareVersionResponse.newBuilder().setFirmwareVersion(firmwareVersion))
        .build();
  }

  private static Message createSwitchFirmwareResponse() {
    return Oslp.Message.newBuilder()
        .setSwitchFirmwareResponse(
            Oslp.SwitchFirmwareResponse.newBuilder().setStatus(Oslp.Status.FAILURE))
        .build();
  }

  private static Message createSetDeviceVerificationKeyResponse() {
    return Oslp.Message.newBuilder()
        .setSetDeviceVerificationKeyResponse(
            Oslp.SetDeviceVerificationKeyResponse.newBuilder().setStatus(Oslp.Status.OK))
        .build();
  }

  private static Message createUpdateDeviceSslCertificationResponse() {
    return Oslp.Message.newBuilder()
        .setUpdateDeviceSslCertificationResponse(
            Oslp.UpdateDeviceSslCertificationResponse.newBuilder().setStatus(Oslp.Status.OK))
        .build();
  }

  private static Message createSetScheduleResponse() {
    return Oslp.Message.newBuilder()
        .setSetScheduleResponse(SetScheduleResponse.newBuilder().setStatus(Oslp.Status.OK))
        .build();
  }

  private static Message createSwitchConfigurationResponse() {
    return Oslp.Message.newBuilder()
        .setSwitchConfigurationResponse(
            Oslp.SwitchConfigurationResponse.newBuilder().setStatus(Oslp.Status.OK))
        .build();
  }

  /** Create relay configuration based on stored configuration values. */
  private static RelayConfiguration createRelayConfiguration(
      final List<DeviceOutputSetting> outputSettings) {
    final RelayConfiguration.Builder configuration = RelayConfiguration.newBuilder();

    for (final DeviceOutputSetting dos : outputSettings) {
      final IndexAddressMap.Builder relayMap =
          IndexAddressMap.newBuilder()
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

  private static Message createResumeScheduleResponse() {
    return Oslp.Message.newBuilder()
        .setResumeScheduleResponse(
            Oslp.ResumeScheduleResponse.newBuilder().setStatus(Oslp.Status.OK))
        .build();
  }

  private static Message createSetRebootResponse() {
    return Oslp.Message.newBuilder()
        .setSetRebootResponse(Oslp.SetRebootResponse.newBuilder().setStatus(Oslp.Status.OK))
        .build();
  }

  private static Message createSetTransitionResponse() {
    return Oslp.Message.newBuilder()
        .setSetTransitionResponse(Oslp.SetTransitionResponse.newBuilder().setStatus(Oslp.Status.OK))
        .build();
  }

  /**
   * Get an OutOfSequenceEvent for given device id. The OutOfSequenceEvent instance will be removed
   * from the list, before the instance is returned.
   *
   * @param deviceId The id of the device.
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

  public Bootstrap getBootstrap() {
    return this.bootstrap;
  }

  public void setBootstrap(final Bootstrap bootstrap) {
    this.bootstrap = bootstrap;
  }

  @Override
  public void channelRead0(final ChannelHandlerContext ctx, final OslpEnvelope message)
      throws Exception {

    final String deviceUid = Base64.encodeBase64String(message.getDeviceId());
    if (this.isRebooting(message.getDeviceId())) {
      LOGGER.warn("Disconnect device {}, simulating unavailability while rebooting", deviceUid);
      ctx.disconnect();
      return;
    } else {
      LOGGER.info("Process envelope for device {}, not rebooting", deviceUid);
    }

    this.oslpLogItemRepository.save(
        new OslpLogItem(
            message.getDeviceId(),
            this.getDeviceIdentificationFromMessage(message.getPayloadMessage()),
            true,
            message.getPayloadMessage()));

    if (message.isValid()) {
      if (this.isOslpResponse(message)) {
        this.channelRead0OslpReponse(ctx, message);
      } else {
        this.channelRead0OslpRequest(ctx, message);
      }
    } else {
      LOGGER.warn("Received message wasn't properly secured.");
    }
  }

  private void channelRead0OslpReponse(
      final ChannelHandlerContext ctx, final OslpEnvelope message) {
    LOGGER.info("Received OSLP Response (before callback): {}", message.getPayloadMessage());

    // Lookup correct callback and call handle method
    final String channelId = ctx.channel().id().asLongText();
    final Callback callback = this.callbacks.remove(channelId);
    if (callback == null) {
      LOGGER.warn("Callback for channel {} does not longer exist, dropping response.", channelId);
      return;
    }

    callback.handle(message);
  }

  private void channelRead0OslpRequest(final ChannelHandlerContext ctx, final OslpEnvelope message)
      throws DeviceSimulatorException {
    final String oslpRequest = message.getPayloadMessage().toString().split(" ")[0];
    LOGGER.info("Received OSLP Request: {}", oslpRequest);

    // Sequence number logic
    byte[] sequenceNumber = message.getSequenceNumber();
    int number = -1;
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
            number,
            number + 1);
        number += 1;
      }

      // Convert integer back to byte array
      sequenceNumber = this.convertIntegerToByteArray(number);
    }

    final byte[] deviceId = message.getDeviceId();

    // Build the OslpEnvelope with the incremented sequence number.
    final OslpEnvelope.Builder responseBuilder =
        new OslpEnvelope.Builder()
            .withSignature(this.oslpSignature)
            .withProvider(this.oslpSignatureProvider)
            .withPrimaryKey(this.privateKey)
            .withDeviceId(deviceId)
            .withSequenceNumber(sequenceNumber);

    // Pass the incremented sequence number to the handleRequest()
    // function for checking.
    responseBuilder.withPayloadMessage(this.handleRequest(message, number));
    final OslpEnvelope response = responseBuilder.build();

    this.oslpLogItemRepository.save(
        new OslpLogItem(
            response.getDeviceId(),
            this.getDeviceIdentificationFromMessage(response.getPayloadMessage()),
            false,
            response.getPayloadMessage()));

    LOGGER.info(
        "sending OSLP response with sequence number: {}",
        this.convertByteArrayToInteger(response.getSequenceNumber()));
    ctx.channel().writeAndFlush(response);
    final String oslpResponse = response.getPayloadMessage().toString().split(" ")[0];
    LOGGER.info("Sent OSLP Response: {}", oslpResponse);
  }

  @Override
  public void channelActive(final ChannelHandlerContext ctx) throws Exception {
    LOGGER.info("Channel {} active.", ctx.channel().id());
    super.channelActive(ctx);
  }

  @Override
  public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
    LOGGER.info("Channel {} inactive.", ctx.channel().id());
    super.channelInactive(ctx);
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
    if (this.isConnectionReset(cause)) {
      LOGGER.info("Connection was (as expected) reset by the device.");
    } else {
      LOGGER.warn("Unexpected exception from downstream.", cause);
    }

    ctx.channel().close();
  }

  private boolean isConnectionReset(final Throwable e) {
    return e instanceof IOException
        && e.getMessage() != null
        && e.getMessage().contains("Connection reset by peer");
  }

  public OslpEnvelope send(
      final InetSocketAddress address,
      final OslpEnvelope request,
      final String deviceIdentification)
      throws IOException, DeviceSimulatorException {
    LOGGER.info("Sending OSLP request: {}", request.getPayloadMessage());

    final Callback callback = new Callback(this.connectionTimeout);

    this.lock.lock();

    // Open connection and send message
    ChannelFuture channelFuture;
    try {
      channelFuture = this.bootstrap.connect(address);
      channelFuture.awaitUninterruptibly(this.connectionTimeout, TimeUnit.MILLISECONDS);
      if (channelFuture.channel() != null && channelFuture.channel().isActive()) {
        LOGGER.info("Connection established to: {}", address);
      } else {
        LOGGER.info(
            "The connnection to OSGP from device {} is not successful", deviceIdentification);
        LOGGER.warn("Unable to connect to: {}", address);
        throw new IOException("Unable to connect");
      }

      this.callbacks.put(channelFuture.channel().id().asLongText(), callback);
      channelFuture.channel().writeAndFlush(request);
    } finally {
      this.lock.unlock();
    }

    // wait for response and close connection
    try {
      final OslpEnvelope response = callback.get(deviceIdentification);
      LOGGER.info("Received OSLP response (after callback): {}", response.getPayloadMessage());

      /*
       * Devices expect the channel to be closed if - and only if - the
       * platform initiated the conversation. If the device initiated the
       * conversation it needs to close the channel itself.
       */
      channelFuture.channel().close();

      return response;
    } catch (final IOException | DeviceSimulatorException e) {
      // Remove callback when exception has occurred
      this.callbacks.remove(channelFuture.channel().id().asLongText());
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
      throws DeviceSimulatorException {
    final Oslp.Message request = message.getPayloadMessage();

    // Create response message
    final Oslp.Message response;
    final String deviceIdString = Base64.encodeBase64String(message.getDeviceId());

    LOGGER.info("request received, sequenceNumber: {}", sequenceNumber);
    LOGGER.info(
        "manufacturerId byte[0]: {} byte[1]: {}",
        message.getDeviceId()[0],
        message.getDeviceId()[1]);
    LOGGER.info("deviceId as BASE 64 STRING: {}", deviceIdString);

    // lookup correct device.
    final Device device = this.deviceManagementService.findDevice(deviceIdString);
    if (device == null) {
      throw new DeviceSimulatorException("device with id: " + deviceIdString + " is unknown");
    }

    // Calculate expected sequence number
    final int expectedSequenceNumber = device.doGetNextSequence();

    // Check sequence number
    if (Math.abs(expectedSequenceNumber - sequenceNumber) > this.sequenceNumberWindow) {
      this.outOfSequenceList.add(
          new OutOfSequenceEvent(
              device.getId(), message.getPayloadMessage().toString(), DateTime.now()));

      throw new DeviceSimulatorException(
          "SequenceNumber incorrect for device: "
              + device.getDeviceIdentification()
              + " Expected: "
              + (expectedSequenceNumber == 0
                  ? this.sequenceNumberMaximum
                  : expectedSequenceNumber - 1)
              + " Actual: "
              + (sequenceNumber == 0 ? this.sequenceNumberMaximum : sequenceNumber - 1)
              + " SequenceNumberWindow: "
              + this.sequenceNumberWindow
              + " Request: "
              + message.getPayloadMessage().toString());
    }

    // If responseDelayTime (and optional responseDelayRandomRange) are set,
    // sleep for a little while
    if (this.responseDelayTime != null) {
      if (this.reponseDelayRandomRange == null) {
        this.sleep(this.responseDelayTime);
      } else {
        final Long randomDelay = (long) (this.reponseDelayRandomRange * this.random.nextDouble());
        this.sleep(this.responseDelayTime + randomDelay);
      }
    }

    response = this.checkForRequest(request, device);

    // Update device
    device.setSequenceNumber(expectedSequenceNumber);
    this.deviceManagementService.updateDevice(device);

    // Write log entry for response
    LOGGER.debug("Responding: {}", response);

    return response;
  }

  /**
   * The cognitive complexity of this method is larger than 15. Instead of creating a number of
   * classes to implement the test and handling of each request, suppress the SonarQube check for
   * now.
   */
  @SuppressWarnings("squid:S3776")
  private Oslp.Message checkForRequest(final Oslp.Message request, final Device device) {

    Oslp.Message response = null;

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
      this.handleSetLightRequest(device, request.getSetLightRequest());

      response = createSetLightResponse();
    } else if (request.hasSetEventNotificationsRequest()) {
      this.handleSetEventNotificationsRequest(device, request.getSetEventNotificationsRequest());

      response = createSetEventNotificationsResponse();
    } else if (request.hasUpdateFirmwareRequest()) {
      this.handleUpdateFirmwareRequest(device, request.getUpdateFirmwareRequest());

      response = createUpdateFirmwareResponse();
    } else if (request.hasGetFirmwareVersionRequest()) {
      response = createGetFirmwareVersionResponse(device.getFirmwareVersion());
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
    } else if (request.hasGetStatusRequest()) {
      response = this.createGetStatusResponse(device);
    } else if (request.hasResumeScheduleRequest()) {
      response = createResumeScheduleResponse();
    } else if (request.hasSetRebootRequest()) {
      response = createSetRebootResponse();

      this.simulateReboot(device);
    } else if (request.hasSetTransitionRequest()) {
      this.handleSetTransitionRequest(device, request.getSetTransitionRequest());

      response = createSetTransitionResponse();
    } else if (request.hasConfirmRegisterDeviceRequest()) {
      response =
          createConfirmRegisterDeviceResponse(
              request.getConfirmRegisterDeviceRequest().getRandomDevice(),
              request.getConfirmRegisterDeviceRequest().getRandomPlatform());
    } else {
      // Handle errors by logging
      LOGGER.error("Did not expect request, ignoring: {}", request);
    }

    return response;
  }

  private void startRebooting(final String deviceUid) {
    LOGGER.info("Blocking further requests for {} while rebooting", deviceUid);
    REBOOTING_DEVICE_IDS.add(deviceUid);
  }

  private void finishRebooting(final String deviceUid) {
    REBOOTING_DEVICE_IDS.remove(deviceUid);
    LOGGER.info("Accepting new requests for {} after rebooting", deviceUid);
  }

  private boolean isRebooting(final byte[] deviceId) {
    final String deviceUid = Base64.encodeBase64String(deviceId);
    LOGGER.info("Checking whether device {} is rebooting", deviceUid);
    return REBOOTING_DEVICE_IDS.contains(deviceUid);
  }

  private DeviceMessageStatus performDeviceRegistration(final Device device) {
    if (device == null) {
      return DeviceMessageStatus.FAILURE;
    }

    final String deviceIdentification = device.getDeviceIdentification();
    if (!StringUtils.hasText(deviceIdentification)) {
      return DeviceMessageStatus.FAILURE;
    }

    try {
      LOGGER.info("Sending DeviceRegistrationRequest for device: {}", deviceIdentification);

      this.finishRebooting(device.getDeviceUid());
      final DeviceMessageStatus deviceMessageStatus =
          OslpChannelHandler.this.registerDevice.sendRegisterDeviceCommand(device.getId(), true);
      if (DeviceMessageStatus.OK.equals(deviceMessageStatus)) {
        LOGGER.info(
            "Sending ConfirmDeviceRegistrationRequest for device: {}", deviceIdentification);
        return OslpChannelHandler.this.registerDevice.sendConfirmDeviceRegistrationCommand(
            device.getId());
      } else {
        LOGGER.info(
            "Not sending ConfirmDeviceRegistrationRequest for device: {} because DeviceRegistrationRequest ended with status: {}",
            deviceIdentification,
            deviceMessageStatus);
      }
    } catch (final Exception e) {
      LOGGER.error("Exception during registration process of device: {}", deviceIdentification, e);
    }
    return DeviceMessageStatus.FAILURE;
  }

  private CompletableFuture<DeviceMessageStatus> simulateReboot(final Device device) {
    final String deviceUid = device.getDeviceUid();
    this.startRebooting(deviceUid);
    return CompletableFuture.supplyAsync(
        () -> {
          DeviceMessageStatus result = DeviceMessageStatus.FAILURE;
          try {
            result =
                Executors.newSingleThreadScheduledExecutor()
                    .schedule(
                        () -> this.performDeviceRegistration(device),
                        this.deviceManagementService.getRebootDelay(),
                        TimeUnit.SECONDS)
                    .get();
          } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("simulateReboot was interrupted", e);
          } catch (final ExecutionException e) {
            LOGGER.error("simulateReboot threw an Exception", e);
          }
          this.finishRebooting(deviceUid);
          return result;
        });
  }

  private void handleSetScheduleRequest(
      final Device device, final SetScheduleRequest setScheduleRequest) {
    // Not yet implemented.
    LOGGER.info(
        "handleSetScheduleRequest not yet implemented. Device: {}, number of schedule entries: {}",
        device.getDeviceIdentification(),
        setScheduleRequest.getSchedulesCount());
  }

  private Message createSetConfigurationResponse() {
    return Oslp.Message.newBuilder()
        .setSetConfigurationResponse(
            Oslp.SetConfigurationResponse.newBuilder().setStatus(Oslp.Status.OK))
        .build();
  }

  private Message createGetConfigurationResponse(final Device device) {
    final DaliConfiguration.Builder daliConfiguration =
        DaliConfiguration.newBuilder()
            .addAddressMap(
                IndexAddressMap.newBuilder()
                    .setIndex(ByteString.copyFrom(new byte[] {1}))
                    .setAddress(ByteString.copyFrom(new byte[] {1}))
                    .setRelayType(RelayType.RT_NOT_SET))
            .setNumberOfLights(ByteString.copyFrom(new byte[] {1}));

    final Oslp.GetConfigurationResponse.Builder configuration =
        Oslp.GetConfigurationResponse.newBuilder();
    try {
      configuration
          .setStatus(Oslp.Status.OK)
          .setPreferredLinkType(
              Enum.valueOf(Oslp.LinkType.class, device.getPreferredLinkType().name()))
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
        configuration.setDeviceFixIpValue(
            ByteString.copyFrom(
                InetAddress.getByName(this.configurationIpConfigFixedIpAddress).getAddress()));
        configuration.setNetMask(
            ByteString.copyFrom(
                InetAddress.getByName(this.configurationIpConfigNetmask).getAddress()));
        configuration.setGateWay(
            ByteString.copyFrom(
                InetAddress.getByName(this.configurationIpConfigGateway).getAddress()));
        configuration.setIsDhcpEnabled(false);
        configuration.setCommunicationTimeout(30);
        configuration.setCommunicationNumberOfRetries(5);
        configuration.setCommunicationPauseTimeBetweenConnectionTrials(120);
        configuration.setOspgIpAddress(
            ByteString.copyFrom(
                InetAddress.getByName(this.configurationOsgpIpAddress).getAddress()));
        configuration.setOsgpPortNumber(this.configurationOsgpPortNumber);
        configuration.setIsTestButtonEnabled(false);
        configuration.setIsAutomaticSummerTimingEnabled(false);
        configuration.setAstroGateSunRiseOffset(-900);
        configuration.setAstroGateSunSetOffset(600);
        configuration.addSwitchingDelay(1);
        configuration.addSwitchingDelay(2);
        configuration.addSwitchingDelay(3);
        configuration.addSwitchingDelay(4);
        configuration.addRelayLinking(
            Oslp.RelayMatrix.newBuilder()
                .setMasterRelayIndex(ByteString.copyFrom(new byte[] {2}))
                .setMasterRelayOn(false)
                .setIndicesOfControlledRelaysOn(ByteString.copyFrom(new byte[] {3, 4}))
                .setIndicesOfControlledRelaysOff(ByteString.copyFrom(new byte[] {3, 4})));
        configuration
            .setRelayRefreshing(false)
            .setSummerTimeDetails("0360100")
            .setWinterTimeDetails("1060200");
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

  private Message createGetStatusResponse(final Device device) {

    final List<LightValue> outputValues = new ArrayList<>();
    for (final DeviceOutputSetting dos : device.getOutputSettings()) {
      final Builder lightValue = this.getLightValueForDeviceOutputSetting(device, dos);
      outputValues.add(lightValue.build());
    }

    // Fallback in case output settings are not yet defined.
    if (outputValues.isEmpty()) {
      final Builder lightValue = this.getDefaultLightValue(device);

      outputValues.add(lightValue.build());
    }

    final Oslp.GetStatusResponse.Builder builder = GetStatusResponse.newBuilder();

    builder.setStatus(Oslp.Status.OK);
    builder.addAllValue(outputValues);
    builder.setPreferredLinktype(
        Enum.valueOf(Oslp.LinkType.class, device.getPreferredLinkType().name()));
    builder.setActualLinktype(Enum.valueOf(Oslp.LinkType.class, device.getActualLinkType().name()));
    builder.setLightType(Enum.valueOf(Oslp.LightType.class, device.getLightType().name()));
    builder.setEventNotificationMask(device.getEventNotificationMask());

    LOGGER.info("device.getProtocol(): {}", device.getProtocol());
    LOGGER.info("ProtocolType.OSLP_ELSTER.name(): {}", ProtocolType.OSLP_ELSTER);

    if (device.getProtocol().equals(ProtocolType.OSLP_ELSTER.toString())) {
      builder.setNumberOfOutputs(4);
      builder.setDcOutputVoltageMaximum(24000);
      builder.setDcOutputVoltageCurrent(24000);
      builder.setMaximumOutputPowerOnDcOutput(15000);
      builder.setSerialNumber(
          ByteString.copyFrom(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9}));
      builder.setMacAddress(ByteString.copyFrom(new byte[] {1, 2, 3, 4, 5, 6}));
      builder.setHardwareId("Hardware ID").setInternalFlashMemSize(1024);
      builder.setExternalFlashMemSize(2048).setLastInternalTestResultCode(0).setStartupCounter(42);
      builder.setBootLoaderVersion("1.1.1").setFirmwareVersion("2.8.5");
      builder.setCurrentConfigurationBackUsed(ByteString.copyFrom(new byte[] {0}));
      builder.setName("ELS_DEV-SIM-DEVICE").setCurrentTime("20251231155959");
      builder.setCurrentIp(this.statusInternalIpAddress);
    }

    return Oslp.Message.newBuilder().setGetStatusResponse(builder.build()).build();
  }

  private Builder getLightValueForDeviceOutputSetting(final Device device, final DeviceOutputSetting dos) {
    final Builder lightValue =
        LightValue.newBuilder().setIndex(OslpUtils.integerToByteString(dos.getInternalId()));

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
    return lightValue;
  }

  private Builder getDefaultLightValue(final Device device) {
    final Builder lightValue =
        LightValue.newBuilder()
            .setIndex(OslpUtils.integerToByteString(0))
            .setOn(device.isLightOn());

    if (device.getDimValue() != null) {
      lightValue.setDimValue(OslpUtils.integerToByteString(device.getDimValue()));
    }

    // Real device specifies dimvalue 0 when off.
    if (!device.isLightOn()) {
      lightValue.setDimValue(OslpUtils.integerToByteString(0));
    }
    return lightValue;
  }

  private void handleSetLightRequest(final Device device, final SetLightRequest request) {
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

    final Oslp.Event event =
        device.isLightOn() ? Oslp.Event.LIGHT_EVENTS_LIGHT_ON : Oslp.Event.LIGHT_EVENTS_LIGHT_OFF;
    final String description = "setLightRequest [SET_LIGHT] SCHED[-]";
    this.sendEvent(device, event, description);
  }

  private void handleSetTransitionRequest(
      final Device device, final SetTransitionRequest setTransitionRequest) {
    // Use the transition type to determine if the relay should be switched
    // on or off. Assume that the simulated device is running a 'normal'
    // light schedule, meaning that the light is on during the night and off
    // during the day.
    final TransitionType transitionType = setTransitionRequest.getTransitionType();

    // In case the relay is switched, send an event with this description.
    final String description = "SetTransition Switch [SET_TRANSIT] SCHED[-]";
    final String deviceId = device.getDeviceIdentification();

    if (TransitionType.DAY_NIGHT.equals(transitionType) && !device.isLightOn()) {
      LOGGER.info(
          "Switching relay on for device: {} after receiving transition type: {}",
          deviceId,
          transitionType);
      device.setLightOn(true);
      final Oslp.Event event = Oslp.Event.LIGHT_EVENTS_LIGHT_ON;
      this.sendEvent(device, event, description);
    } else if (TransitionType.NIGHT_DAY.equals(transitionType) && device.isLightOn()) {
      LOGGER.info(
          "Switching relay off for device: {} after receiving transition type: {}",
          deviceId,
          transitionType);
      device.setLightOn(false);
      final Oslp.Event event = Oslp.Event.LIGHT_EVENTS_LIGHT_OFF;
      this.sendEvent(device, event, description);
    } else {
      LOGGER.info(
          "Not switching relay for device: {}. Relay state: {}, transition type: {}.",
          deviceId,
          device.isLightOn(),
          transitionType);
    }
  }

  /**
   * See the file 'event-examples.txt in the resources folder for descriptions for particular event
   * types.'
   */
  private void sendEvent(final Device device, final Oslp.Event event, final String description) {
    this.sendEventWithCustomDelay(device, event, description, 3000);
  }

  private void sendEventWithCustomDelay(
      final Device device, final Oslp.Event event, final String description, final int delay) {
    // Send an event.
    final Timer timer = new Timer();
    timer.schedule(
        new TimerTask() {

          @Override
          public void run() {
            OslpChannelHandler.this.registerDevice.sendEventNotificationCommand(
                device.getId(), event.getNumber(), description, null);
          }
        },
        delay);
  }

  private void handleSetEventNotificationsRequest(
      final Device device, final SetEventNotificationsRequest request) {
    device.setEventNotifications(request.getNotificationMask());
  }

  private void handleUpdateFirmwareRequest(
      final Device device, final UpdateFirmwareRequest request) {
    LOGGER.debug(
        "handle UpdateFirmwareRequest for device: {}, with serialized size of {}",
        device.getDeviceIdentification(),
        request.getSerializedSize());

    this.simulateReboot(device)
        .thenAccept(
            deviceMessageStatus -> {
              if (DeviceMessageStatus.NOT_FOUND == deviceMessageStatus) {
                LOGGER.error(
                    "Device {} not found handling update firmware request",
                    device.getDeviceIdentification());
                return;
              }
              final Oslp.Event event;
              final String description;
              if (DeviceMessageStatus.OK == deviceMessageStatus) {
                event = Oslp.Event.FIRMWARE_EVENTS_ACTIVATING;
                description = "A new firmware is activated";
              } else {
                LOGGER.warn(
                    "Device {} has issues registering after reboot handling update firmware request",
                    device.getDeviceIdentification());
                event = Oslp.Event.FIRMWARE_EVENTS_DOWNLOAD_FAILED;
                description =
                    "Failure rebooting and registrating device during firmware activation";
              }
              this.sendEventWithCustomDelay(
                  device,
                  event,
                  description,
                  Math.max(1, 7 - this.deviceManagementService.getRebootDelay()));
            });
  }

  private void handleSetConfigurationRequest(
      final Device device, final Oslp.SetConfigurationRequest setConfigurationRequest) {
    if (setConfigurationRequest.hasPreferredLinkType()) {
      device.setPreferredLinkType(
          Enum.valueOf(LinkType.class, setConfigurationRequest.getPreferredLinkType().name()));
    }
    if (setConfigurationRequest.hasLightType()) {
      device.setLightType(
          Enum.valueOf(LightType.class, setConfigurationRequest.getLightType().name()));
    }
    if (setConfigurationRequest.hasRelayConfiguration()) {
      final List<DeviceOutputSetting> outputSettings = new ArrayList<>();
      for (final IndexAddressMap iam :
          setConfigurationRequest.getRelayConfiguration().getAddressMapList()) {
        final int index = iam.getIndex().byteAt(0);
        final int address = iam.getAddress().byteAt(0);
        final OutputType outputType = OutputType.valueOf(iam.getRelayType().name());

        outputSettings.add(new DeviceOutputSetting(index, address, outputType));
      }
      device.setOutputSettings(outputSettings);
    }
  }

  private void handleGetConfigurationRequest(
      final Device device, final Oslp.GetConfigurationRequest request) {
    LOGGER.debug(
        "handle GetConfigurationRequest for device: {}, with serialized size of {}",
        device.getDeviceIdentification(),
        request.getSerializedSize());
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
    bytes[1] = value.byteValue();
    LOGGER.info(
        "web-device-simulator.OslpChannelHandler.convertIntegerToByteArray() byte[0]: {} byte[1]: {} Integer value: {}",
        bytes[0],
        bytes[1],
        value);
    return bytes;
  }

  private Integer convertByteArrayToInteger(final byte[] array) {
    // See: platform.service.SequenceNumberUtils
    final Integer value = (array[0] & 0xFF) << 8 | (array[1] & 0xFF);
    LOGGER.info(
        "web-device-simulator.OslpChannelHandler.convertByteArrayToInteger() byte[0]: {} byte[1]: {} Integer value: {}",
        array[0],
        array[1],
        value);
    return value;
  }

  private static class Callback {

    private final CountDownLatch latch = new CountDownLatch(1);
    private final int connectionTimeout;
    private OslpEnvelope response;

    Callback(final int connectionTimeout) {
      this.connectionTimeout = connectionTimeout;
    }

    OslpEnvelope get(final String deviceIdentification)
        throws IOException, DeviceSimulatorException {
      try {
        if (!this.latch.await(this.connectionTimeout, TimeUnit.MILLISECONDS)) {
          LOGGER.warn(
              "Failed to receive response from device {} within timelimit {} ms",
              deviceIdentification,
              this.connectionTimeout);
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
}
