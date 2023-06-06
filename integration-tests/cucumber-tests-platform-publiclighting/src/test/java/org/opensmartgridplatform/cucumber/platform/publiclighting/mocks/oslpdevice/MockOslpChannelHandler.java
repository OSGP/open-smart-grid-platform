// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.mocks.oslpdevice;

import static org.opensmartgridplatform.oslp.Oslp.RelayType.LIGHT;
import static org.opensmartgridplatform.oslp.Oslp.RelayType.TARIFF;

import com.google.protobuf.ByteString;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.PrivateKey;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.opensmartgridplatform.cucumber.platform.publiclighting.PlatformPubliclightingDefaults;
import org.opensmartgridplatform.oslp.Oslp;
import org.opensmartgridplatform.oslp.Oslp.Message;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class MockOslpChannelHandler extends SimpleChannelInboundHandler<OslpEnvelope> {

  private static final Logger LOGGER = LoggerFactory.getLogger(MockOslpChannelHandler.class);
  private final ConcurrentMap<String, Callback> callbacks = new ConcurrentHashMap<>();
  private final Bootstrap clientBootstrap;
  private final int connectionTimeout;
  private final Lock lock = new ReentrantLock();
  private final DevicesContext devicesContext;
  private final String oslpSignature;
  private final String oslpSignatureProvider;
  private final List<OutOfSequenceEvent> outOfSequenceList = new ArrayList<>();
  private final PrivateKey privateKey;
  private final Random random = new Random();
  private final List<Message> receivedResponses;
  private final Long reponseDelayRandomRange;
  private final Long responseDelayTime;
  // Device settings
  private final Integer sequenceNumberMaximum;

  public MockOslpChannelHandler(
      final String oslpSignature,
      final String oslpSignatureProvider,
      final int connectionTimeout,
      final Integer sequenceNumberMaximum,
      final Long responseDelayTime,
      final Long reponseDelayRandomRange,
      final PrivateKey privateKey,
      final Bootstrap clientBootstrap,
      final DevicesContext devicesContext,
      final List<Message> receivedResponses) {
    this.oslpSignature = oslpSignature;
    this.oslpSignatureProvider = oslpSignatureProvider;
    this.connectionTimeout = connectionTimeout;
    this.sequenceNumberMaximum = sequenceNumberMaximum;
    this.responseDelayTime = responseDelayTime;
    this.reponseDelayRandomRange = reponseDelayRandomRange;
    this.privateKey = privateKey;
    this.clientBootstrap = clientBootstrap;
    this.devicesContext = devicesContext;
    this.receivedResponses = receivedResponses;
  }

  private static byte[] convertIntegerToByteArray(final Integer value) {
    // See: platform.service.SequenceNumberUtils
    final byte[] bytes = new byte[2];
    bytes[0] = (byte) (value >>> 8);
    bytes[1] = value.byteValue();
    return bytes;
  }

  private static Integer convertByteArrayToInteger(final byte[] array) {
    // See: platform.service.SequenceNumberUtils
    return (array[0] & 0xFF) << 8 | (array[1] & 0xFF);
  }

  private static String getDeviceUid(final OslpEnvelope message) {
    return Base64.encodeBase64String(message.getDeviceId());
  }

  @Override
  public void channelActive(final ChannelHandlerContext ctx) throws Exception {
    LOGGER.debug("Channel {} active", ctx.channel().id().asLongText());
    super.channelActive(ctx);
  }

  @Override
  public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
    LOGGER.debug("Channel {} inactive", ctx.channel().id().asLongText());
    super.channelInactive(ctx);
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
    if (this.isConnectionReset(cause)) {
      LOGGER.debug("Connection was (as expected) reset by the device.");
    } else {
      LOGGER.warn("Unexpected exception from downstream.", cause);
    }

    ctx.channel().close();
  }

  public Integer getSequenceNumberMaximum() {
    return this.sequenceNumberMaximum;
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

  private boolean isConnectionReset(final Throwable e) {
    return e instanceof IOException
        && e.getMessage() != null
        && e.getMessage().contains("Connection reset by peer");
  }

  private boolean isOslpResponse(final OslpEnvelope envelope) {
    return envelope.getPayloadMessage().hasRegisterDeviceResponse()
        || envelope.getPayloadMessage().hasConfirmRegisterDeviceResponse()
        || envelope.getPayloadMessage().hasEventNotificationResponse();
  }

  @Override
  public void channelRead0(final ChannelHandlerContext ctx, final OslpEnvelope message)
      throws Exception {

    if (message.isValid()) {
      final String deviceUid = MockOslpChannelHandler.getDeviceUid(message);
      final DeviceState deviceState = this.devicesContext.getDeviceState(deviceUid);

      LOGGER.debug(
          "Device {} received a message with sequence number {}",
          deviceUid,
          message.getSequenceNumber());

      if (this.isOslpResponse(message)) {
        LOGGER.debug(
            "Device {} received an OSLP Response (before callback): {}",
            MockOslpChannelHandler.getDeviceUid(message),
            message.getPayloadMessage());

        // Lookup correct callback and call handle method.
        final String channelId = ctx.channel().id().asLongText();
        final Callback callback = this.callbacks.remove(channelId);
        if (callback == null) {
          LOGGER.warn(
              "Callback for channel {} does not longer exist, dropping response.", channelId);
          return;
        }

        callback.handle(message);
      } else {
        final MessageType messageType = this.getMessageType(message.getPayloadMessage());

        LOGGER.debug("Device {} received an OSLP Request of type {}", deviceUid, messageType);

        if (deviceState.hasMockedResponses(messageType)) {

          // Build the OslpEnvelope.
          final OslpEnvelope.Builder responseBuilder =
              new OslpEnvelope.Builder()
                  .withSignature(this.oslpSignature)
                  .withProvider(this.oslpSignatureProvider)
                  .withPrimaryKey(this.privateKey)
                  .withDeviceId(message.getDeviceId())
                  .withPayloadMessage(this.handleRequest(message));

          // Add the new sequence number to the OslpEnvelope.
          responseBuilder.withSequenceNumber(
              convertIntegerToByteArray(deviceState.getSequenceNumber()));
          final OslpEnvelope response = responseBuilder.build();

          LOGGER.debug(
              "Device {} is sending an OSLP response with sequence number {}",
              MockOslpChannelHandler.getDeviceUid(response),
              convertByteArrayToInteger(response.getSequenceNumber()));

          // wait for the response to actually be written. This
          // improves stability of the tests
          final ChannelFuture future = ctx.channel().writeAndFlush(response);

          final InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
          final InetSocketAddress clientAddress =
              new InetSocketAddress(
                  remoteAddress.getAddress(),
                  PlatformPubliclightingDefaults.OSLP_ELSTER_SERVER_PORT);

          future.addListener(
              (ChannelFutureListener)
                  ChannelFutureListener -> {
                    Executors.newSingleThreadScheduledExecutor()
                        .schedule(
                            () -> {
                              try {
                                MockOslpChannelHandler.this.sendNotifications(
                                    clientAddress, message, deviceUid, deviceState);
                              } catch (final DeviceSimulatorException | IOException e) {
                                LOGGER.info("Unable to send notifications", e);
                              }
                            },
                            1000,
                            TimeUnit.MILLISECONDS)
                        .get();

                    if (!ChannelFutureListener.isSuccess()) {
                      ChannelFutureListener.channel().close();
                    }
                  });

          future.await();

          LOGGER.debug(
              "Sent OSLP response: {}", response.getPayloadMessage().toString().split(" ")[0]);
        } else {
          LOGGER.error(
              "Device {} received a message of type {}, but no mocks are available, this test will fail!",
              deviceUid,
              messageType);
        }
      }
    } else {
      LOGGER.warn("Received message wasn't properly secured.");
    }
  }

  private void sendNotifications(
      final InetSocketAddress inetAddress,
      final OslpEnvelope message,
      final String deviceUid,
      final DeviceState deviceState)
      throws DeviceSimulatorException, IOException {

    final MessageType messageType = this.getMessageType(message.getPayloadMessage());

    if (MessageType.SET_LIGHT == messageType) {
      final List<Oslp.LightValue> lightValueList =
          message.getPayloadMessage().getSetLightRequest().getValuesList();
      for (final Oslp.LightValue lightValue : lightValueList) {
        final Oslp.Event event =
            lightValue.getOn()
                ? Oslp.Event.LIGHT_EVENTS_LIGHT_ON
                : Oslp.Event.LIGHT_EVENTS_LIGHT_OFF;
        final OslpEnvelope notification =
            this.buildNotification(message, deviceUid, deviceState, event, lightValue.getIndex());
        this.send(inetAddress, notification, deviceUid);
      }
    }
  }

  private OslpEnvelope buildNotification(
      final OslpEnvelope message,
      final String deviceUid,
      final DeviceState deviceState,
      final Oslp.Event event,
      final ByteString index) {
    this.devicesContext.getDeviceState(deviceUid).incrementSequenceNumber();

    final String timestamp =
        DateTimeFormatter.ofPattern("YYYYMMddHHmmss").format(LocalDateTime.now());

    final Oslp.EventNotification.Builder eventNotificationBuilder =
        Oslp.EventNotification.newBuilder()
            .setEvent(event)
            .setDescription(event.name())
            .setTimestamp(timestamp)
            .setIndex(index);

    final OslpEnvelope.Builder notificationBuilder =
        new OslpEnvelope.Builder()
            .withSignature(this.oslpSignature)
            .withProvider(this.oslpSignatureProvider)
            .withPrimaryKey(this.privateKey)
            .withDeviceId(message.getDeviceId())
            .withSequenceNumber(convertIntegerToByteArray(deviceState.getSequenceNumber()))
            .withPayloadMessage(
                Message.newBuilder()
                    .setEventNotificationRequest(
                        Oslp.EventNotificationRequest.newBuilder()
                            .addNotifications(eventNotificationBuilder.build()))
                    .build());

    return notificationBuilder.build();
  }

  public OslpEnvelope send(
      final InetSocketAddress address,
      final OslpEnvelope request,
      final String deviceIdentification)
      throws IOException, DeviceSimulatorException {
    LOGGER.debug("Sending OSLP request: {}", request.getPayloadMessage());

    final Callback callback = new Callback(this.connectionTimeout);

    this.lock.lock();

    // Open connection and send message
    ChannelFuture channelFuture = null;
    try {
      channelFuture = this.clientBootstrap.connect(address);
      channelFuture.awaitUninterruptibly(this.connectionTimeout, TimeUnit.MILLISECONDS);
      if (channelFuture.channel() != null && channelFuture.channel().isActive()) {
        LOGGER.debug("Connection established to: {}", address);
      } else {
        LOGGER.debug("The connection to the device {} is not successful", deviceIdentification);
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
      LOGGER.debug("Received OSLP response (after callback): {}", response.getPayloadMessage());

      /*
       * Devices expect the channel to be closed if (and only if) the
       * platform initiated the conversation. If the device initiated the
       * conversation it needs to close the channel itself.
       */
      channelFuture.channel().close();

      this.receivedResponses.add(response.getPayloadMessage());

      return response;
    } catch (final IOException | DeviceSimulatorException e) {
      LOGGER.error("send exception", e);
      // Remove callback when exception has occurred
      this.callbacks.remove(channelFuture.channel().id().asLongText());
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
  // WITH a sequence number
  public Oslp.Message handleRequest(final OslpEnvelope message, final int sequenceNumber)
      throws DeviceSimulatorException, IOException, ParseException {

    message.setSequenceNumber(convertIntegerToByteArray(sequenceNumber));
    return this.handleRequest(message);
  }

  public Oslp.Message handleRequest(final OslpEnvelope requestMessage)
      throws DeviceSimulatorException {
    final Oslp.Message request = requestMessage.getPayloadMessage();

    final String deviceUid = MockOslpChannelHandler.getDeviceUid(requestMessage);
    final MessageType messageType = this.getMessageType(requestMessage.getPayloadMessage());
    final DeviceState deviceState = this.devicesContext.getDeviceState(deviceUid);

    LOGGER.info(
        "Device {} received [{}], sequence number [{}]",
        deviceUid,
        request,
        requestMessage.getSequenceNumber());

    // Calculate expected sequence number
    deviceState.incrementSequenceNumber();

    // If responseDelayTime (and optional responseDelayRandomRange) are set,
    // sleep for a little while
    if (this.responseDelayTime != null && this.reponseDelayRandomRange == null) {
      this.sleep(this.responseDelayTime);
    } else if (this.responseDelayTime != null) {
      final Long randomDelay = (long) (this.reponseDelayRandomRange * this.random.nextDouble());
      this.sleep(this.responseDelayTime + randomDelay);
    }

    deviceState.addReceivedRequest(messageType, request);

    final Oslp.Message response = deviceState.pollMockedResponse(messageType);

    LOGGER.info("Device {} mocked response: [{}]", deviceState.getDeviceUid(), response);

    return response;
  }

  public void reset() {}

  /**
   * TODO: Isn't there somewhere a (better) method like this?
   *
   * @param request
   * @return
   */
  private MessageType getMessageType(final Oslp.Message request) throws DeviceSimulatorException {
    if (request.hasGetFirmwareVersionRequest()) {
      return MessageType.GET_FIRMWARE_VERSION;
    }
    if (request.hasUpdateFirmwareRequest()) {
      return MessageType.UPDATE_FIRMWARE;
    }
    if (request.hasSetLightRequest()) {
      return MessageType.SET_LIGHT;
    }
    if (request.hasSetEventNotificationsRequest()) {
      return MessageType.SET_EVENT_NOTIFICATIONS;
    }
    if (request.hasStartSelfTestRequest()) {
      return MessageType.START_SELF_TEST;
    }
    if (request.hasStopSelfTestRequest()) {
      return MessageType.STOP_SELF_TEST;
    }
    if (request.hasGetStatusRequest()) {
      return MessageType.GET_STATUS;
    }
    if (request.hasResumeScheduleRequest()) {
      return MessageType.RESUME_SCHEDULE;
    }
    if (request.hasSetRebootRequest()) {
      return MessageType.SET_REBOOT;
    }
    if (request.hasSetTransitionRequest()) {
      return MessageType.SET_TRANSITION;
    }
    if (request.hasSetDeviceVerificationKeyRequest()) {
      return MessageType.UPDATE_KEY;
    }
    if (request.hasGetConfigurationRequest()) {
      return MessageType.GET_CONFIGURATION;
    }
    if (request.hasSetConfigurationRequest()) {
      return MessageType.SET_CONFIGURATION;
    }
    if (request.hasSetScheduleRequest()) {
      final Oslp.RelayType relayType = request.getSetScheduleRequest().getScheduleType();
      if (TARIFF == relayType) {
        return MessageType.SET_TARIFF_SCHEDULE;
      } else if (LIGHT == relayType) {
        return MessageType.SET_LIGHT_SCHEDULE;
      } else {
        throw new DeviceSimulatorException(
            String.format(
                "Received an unimplemented RelayType in setScheduleRequest of type %s", relayType));
      }
    }

    throw new DeviceSimulatorException("Received an unimplemented message type");
  }

  private static class Callback {

    private final int connectionTimeout;

    private final CountDownLatch latch = new CountDownLatch(1);

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
}
