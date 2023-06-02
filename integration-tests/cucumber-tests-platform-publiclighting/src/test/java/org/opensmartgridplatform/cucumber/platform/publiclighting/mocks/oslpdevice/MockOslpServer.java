//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.publiclighting.mocks.oslpdevice;

import com.google.protobuf.ByteString;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.config.CoreDeviceConfiguration;
import org.opensmartgridplatform.oslp.Oslp;
import org.opensmartgridplatform.oslp.Oslp.DaliConfiguration;
import org.opensmartgridplatform.oslp.Oslp.GetConfigurationResponse;
import org.opensmartgridplatform.oslp.Oslp.GetFirmwareVersionResponse;
import org.opensmartgridplatform.oslp.Oslp.GetStatusResponse;
import org.opensmartgridplatform.oslp.Oslp.GetStatusResponse.Builder;
import org.opensmartgridplatform.oslp.Oslp.IndexAddressMap;
import org.opensmartgridplatform.oslp.Oslp.LightType;
import org.opensmartgridplatform.oslp.Oslp.LightValue;
import org.opensmartgridplatform.oslp.Oslp.LinkType;
import org.opensmartgridplatform.oslp.Oslp.Message;
import org.opensmartgridplatform.oslp.Oslp.RelayConfiguration;
import org.opensmartgridplatform.oslp.Oslp.RelayType;
import org.opensmartgridplatform.oslp.Oslp.ResumeScheduleResponse;
import org.opensmartgridplatform.oslp.Oslp.SetConfigurationResponse;
import org.opensmartgridplatform.oslp.Oslp.SetDeviceVerificationKeyResponse;
import org.opensmartgridplatform.oslp.Oslp.SetEventNotificationsResponse;
import org.opensmartgridplatform.oslp.Oslp.SetLightResponse;
import org.opensmartgridplatform.oslp.Oslp.SetRebootResponse;
import org.opensmartgridplatform.oslp.Oslp.SetScheduleResponse;
import org.opensmartgridplatform.oslp.Oslp.SetTransitionResponse;
import org.opensmartgridplatform.oslp.Oslp.StartSelfTestResponse;
import org.opensmartgridplatform.oslp.Oslp.StopSelfTestResponse;
import org.opensmartgridplatform.oslp.Oslp.UpdateFirmwareResponse;
import org.opensmartgridplatform.oslp.OslpDecoder;
import org.opensmartgridplatform.oslp.OslpEncoder;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.oslp.OslpUtils;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.networking.DisposableNioEventLoopGroup;
import org.opensmartgridplatform.shared.security.CertificateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockOslpServer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MockOslpServer.class);

  private static final String MOCKING_MESSAGE_TYPE =
      "Mocking response for device {} of message type: {}";

  private final CoreDeviceConfiguration configuration;

  private final int oslpPortServer;

  private final int oslpElsterPortServer;

  private final String oslpSignature;

  private final String oslpSignatureProvider;

  private final int connectionTimeout;

  private final String signKeyPath;

  private final String verifyKeyPath;

  private final String keytype;

  private final Integer sequenceNumberMaximum;

  private final Long responseDelayTime;

  private final Long responseDelayRandomRange;

  private ServerBootstrap serverOslp;
  private ServerBootstrap serverOslpElster;

  private DisposableNioEventLoopGroup serverBossGroup = new DisposableNioEventLoopGroup();
  private DisposableNioEventLoopGroup serverWorkerGroup = new DisposableNioEventLoopGroup();
  private final DisposableNioEventLoopGroup clientWorkerGroup = new DisposableNioEventLoopGroup();

  // TODO split channel handler in client/server
  private MockOslpChannelHandler channelHandler;

  private final DevicesContext devicesContext = new DevicesContext();
  private final List<Message> receivedResponses = new ArrayList<>();

  public MockOslpServer(
      final CoreDeviceConfiguration configuration,
      final int oslpPortServer,
      final int oslpElsterPortServer,
      final String oslpSignature,
      final String oslpSignatureProvider,
      final int connectionTimeout,
      final String signKeyPath,
      final String verifyKeyPath,
      final String keytype,
      final Integer sequenceNumberMaximum,
      final Long responseDelayTime,
      final Long reponseDelayRandomRange) {
    this.configuration = configuration;
    this.oslpPortServer = oslpPortServer;
    this.oslpElsterPortServer = oslpElsterPortServer;
    this.oslpSignature = oslpSignature;
    this.oslpSignatureProvider = oslpSignatureProvider;
    this.connectionTimeout = connectionTimeout;
    this.signKeyPath = signKeyPath;
    this.verifyKeyPath = verifyKeyPath;
    this.keytype = keytype;
    this.sequenceNumberMaximum = sequenceNumberMaximum;
    this.responseDelayTime = responseDelayTime;
    this.responseDelayRandomRange = reponseDelayRandomRange;
  }

  public Integer getSequenceNumber(final String deviceUid) {
    return this.devicesContext.getDeviceState(deviceUid).getSequenceNumber();
  }

  public void incrementSequenceNumber(final String deviceUid) {
    this.devicesContext.getDeviceState(deviceUid).incrementSequenceNumber();
  }

  public void start() {
    this.channelHandler =
        new MockOslpChannelHandler(
            this.oslpSignature,
            this.oslpSignatureProvider,
            this.connectionTimeout,
            this.sequenceNumberMaximum,
            this.responseDelayTime,
            this.responseDelayRandomRange,
            this.privateKey(),
            this.clientBootstrap(),
            this.devicesContext,
            this.receivedResponses);

    this.serverBossGroup = new DisposableNioEventLoopGroup();
    this.serverWorkerGroup = new DisposableNioEventLoopGroup();

    LOGGER.debug("OSLP Mock server starting on port {}", this.oslpPortServer);
    this.serverOslp = this.serverBootstrap();
    this.serverOslp.bind(new InetSocketAddress(this.oslpPortServer));
    LOGGER.debug("OSLP Elster Mock server starting on port {}", this.oslpElsterPortServer);
    this.serverOslpElster = this.serverBootstrap();
    this.serverOslpElster.bind(new InetSocketAddress(this.oslpElsterPortServer));
    LOGGER.info("OSLP Mock servers started.");
  }

  public void stop() {
    this.serverWorkerGroup.destroy();
    this.serverBossGroup.destroy();
    this.resetServer();
    this.channelHandler = null;
    LOGGER.info("OSLP Mock servers shutdown.");
  }

  public void resetServer() {
    this.receivedResponses.clear();
    this.devicesContext.clear();
    this.channelHandler.reset();
  }

  public Message waitForRequest(final String deviceUid, final MessageType messageType)
      throws DeviceSimulatorException {
    LOGGER.info(
        "Device {} is waiting for request of message type: {}, receivedResponses: {}",
        deviceUid,
        messageType.name(),
        this.receivedResponses.size());

    final DeviceState deviceState = this.devicesContext.getDeviceState(deviceUid);

    int count = 0;
    while (!deviceState.hasReceivedRequests(messageType)) {
      try {
        count++;
        LOGGER.info(
            "Sleeping 1s {} - Device {} is waiting for a request of message type: {}",
            count,
            deviceUid,
            messageType.name());
        Thread.sleep(1000);
      } catch (final InterruptedException e) {
        Assertions.fail("Polling for a request interrupted");
      }

      if (count > this.configuration.getTimeout()) {
        Assertions.fail("Polling for a request failed, no request found");
      }
    }

    LOGGER.info("Device {} received a request of message type: {}", deviceUid, messageType.name());

    return deviceState.pollReceivedRequest(messageType);
  }

  public OslpEnvelope send(
      final InetSocketAddress address,
      final OslpEnvelope request,
      final String deviceIdentification)
      throws IOException, DeviceSimulatorException {
    return this.channelHandler.send(address, request, deviceIdentification);
  }

  public Message sendRequest(final String deviceUid, final Message message)
      throws DeviceSimulatorException, IOException, ParseException {

    final OslpEnvelope envelope = new OslpEnvelope();
    envelope.setPayloadMessage(message);

    return this.channelHandler.handleRequest(envelope, this.getSequenceNumber(deviceUid));
  }

  private ServerBootstrap serverBootstrap() {

    LOGGER.info("Initializing serverBootstrap bean.");

    final ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(this.serverBossGroup, this.serverWorkerGroup);
    bootstrap.channel(NioServerSocketChannel.class);
    bootstrap.childHandler(
        new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(final SocketChannel ch) throws Exception {
            MockOslpServer.this.createPipeLine(ch);
          }
        });

    bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
    bootstrap.childOption(ChannelOption.SO_KEEPALIVE, false);

    return bootstrap;
  }

  private Bootstrap clientBootstrap() {

    LOGGER.info("Initializing clientBootstrap bean.");

    final Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(this.clientWorkerGroup);
    bootstrap.channel(NioSocketChannel.class);
    bootstrap.handler(
        new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(final SocketChannel ch) throws Exception {
            MockOslpServer.this.createPipeLine(ch);
          }
        });

    bootstrap.option(ChannelOption.TCP_NODELAY, true);
    bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
    bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.connectionTimeout);

    return bootstrap;
  }

  private ChannelPipeline createPipeLine(final SocketChannel channel)
      throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException,
          IOException {
    final ChannelPipeline pipeline = channel.pipeline();

    pipeline.addLast("oslpEncoder", new OslpEncoder());
    pipeline.addLast(
        "oslpDecoder", new OslpDecoder(this.oslpSignature, this.oslpSignatureProvider));
    pipeline.addLast("oslpSecurity", new OslpSecurityHandler(this.publicKey()));
    pipeline.addLast("oslpChannelHandler", this.channelHandler);

    return pipeline;
  }

  private PublicKey publicKey() throws IOException {
    return CertificateHelper.createPublicKey(
        this.verifyKeyPath, this.keytype, this.oslpSignatureProvider);
  }

  public PrivateKey privateKey() {
    PrivateKey privateKey = null;

    try {
      privateKey =
          CertificateHelper.createPrivateKey(
              this.signKeyPath, this.keytype, this.oslpSignatureProvider);
    } catch (final Exception ex) {
      //
    }

    return privateKey;
  }

  public void mockGetConfigurationResponse(
      final String deviceUid,
      final Oslp.Status oslpStatus,
      final LightType lightType,
      final String dcLights,
      final String dcMap,
      final String rcMap,
      final LinkType preferredLinkType,
      final String osgpIpAddress,
      final Integer osgpPort)
      throws UnknownHostException {

    final String[] dcMapArray;
    final String[] rcMapArray;
    if (dcMap != null) {
      dcMapArray = dcMap.split(";");
    } else {
      dcMapArray = null;
    }
    if (rcMap != null) {
      rcMapArray = rcMap.split(";");
    } else {
      rcMapArray = null;
    }

    final GetConfigurationResponse.Builder builder = GetConfigurationResponse.newBuilder();

    builder.setStatus(oslpStatus);
    if (lightType != null && lightType != LightType.LT_NOT_SET) {
      builder.setLightType(lightType);
    }

    final ByteString bsDcLights = ByteString.copyFromUtf8(dcLights);
    if (dcMapArray != null && !dcMapArray[0].isEmpty()) {
      final DaliConfiguration.Builder dcBuilder = DaliConfiguration.newBuilder();

      for (int i = 0; i < dcMapArray.length; i++) {
        final String[] dcSubMapArray = dcMapArray[i].split(",");
        if (dcSubMapArray[i] != null && !dcSubMapArray[i].isEmpty()) {
          dcBuilder.addAddressMap(
              IndexAddressMap.newBuilder()
                  .setIndex(OslpUtils.integerToByteString(Integer.parseInt(dcSubMapArray[0])))
                  .setAddress(OslpUtils.integerToByteString(Integer.parseInt(dcSubMapArray[1])))
                  .setRelayType(RelayType.RT_NOT_SET));
        }
      }

      if (!bsDcLights.isEmpty()) {
        dcBuilder.setNumberOfLights(bsDcLights);
      }

      if (!dcLights.isEmpty() && dcBuilder.getAddressMapCount() != Integer.parseInt(dcLights)) {
        builder.setDaliConfiguration(dcBuilder.build());
      }

      if (rcMapArray != null && !rcMapArray[0].isEmpty()) {
        final RelayConfiguration.Builder rcBuilder = RelayConfiguration.newBuilder();

        for (int i = 0; i < rcMapArray.length; i++) {
          final String[] rcSubMapArray = rcMapArray[i].split(",");
          if (rcSubMapArray[i] != null && !rcSubMapArray[i].isEmpty()) {
            final RelayType rcRelayType = RelayType.valueOf(rcSubMapArray[2]);
            rcBuilder.addAddressMap(
                IndexAddressMap.newBuilder()
                    .setIndex(OslpUtils.integerToByteString(Integer.parseInt(rcSubMapArray[0])))
                    .setAddress(OslpUtils.integerToByteString(Integer.parseInt(rcSubMapArray[1])))
                    .setRelayType(rcRelayType));
          }
        }

        builder.setRelayConfiguration(rcBuilder.build());
      }
    }

    if (preferredLinkType != null && preferredLinkType != LinkType.LINK_NOT_SET) {
      builder.setPreferredLinkType(preferredLinkType);
    }

    /*
     * MeterType, ShortTermHistoryIntervalMinutes, LongTermHistoryInterval
     * and LongTermHistoryIntervalType were part of the platform and the
     * protocol adapter, but they have been removed. Mock some values here
     * for OSLP, since they are still part of the OSLP protocol as installed
     * on the actual devices.
     */
    builder.setShortTermHistoryIntervalMinutes(0);
    builder.setLongTermHistoryInterval(0);

    if (StringUtils.isNotEmpty(osgpIpAddress)) {
      builder.setOspgIpAddress(
          ByteString.copyFrom(InetAddress.getByName(osgpIpAddress).getAddress()));
    }

    if (osgpPort != null) {
      builder.setOsgpPortNumber(osgpPort);
    }

    LOGGER.info(MOCKING_MESSAGE_TYPE, deviceUid, MessageType.GET_CONFIGURATION);
    this.devicesContext
        .getDeviceState(deviceUid)
        .addMockedResponse(
            MessageType.GET_CONFIGURATION,
            Oslp.Message.newBuilder().setGetConfigurationResponse(builder).build());
  }

  public void mockSetConfigurationResponse(final String deviceUid, final Oslp.Status oslpStatus) {
    LOGGER.info(MOCKING_MESSAGE_TYPE, deviceUid, MessageType.SET_CONFIGURATION);
    this.devicesContext
        .getDeviceState(deviceUid)
        .addMockedResponse(
            MessageType.SET_CONFIGURATION,
            Oslp.Message.newBuilder()
                .setSetConfigurationResponse(
                    SetConfigurationResponse.newBuilder().setStatus(oslpStatus).build())
                .build());
  }

  public void mockGetFirmwareVersionResponse(final String deviceUid, final String firmwareVersion) {
    LOGGER.info(MOCKING_MESSAGE_TYPE, deviceUid, MessageType.GET_FIRMWARE_VERSION);
    this.devicesContext
        .getDeviceState(deviceUid)
        .addMockedResponse(
            MessageType.GET_FIRMWARE_VERSION,
            Oslp.Message.newBuilder()
                .setGetFirmwareVersionResponse(
                    GetFirmwareVersionResponse.newBuilder().setFirmwareVersion(firmwareVersion))
                .build());
  }

  public void mockUpdateFirmwareResponse(final String deviceUid, final Oslp.Status status) {
    LOGGER.info(MOCKING_MESSAGE_TYPE, deviceUid, MessageType.UPDATE_FIRMWARE);
    this.devicesContext
        .getDeviceState(deviceUid)
        .addMockedResponse(
            MessageType.UPDATE_FIRMWARE,
            Oslp.Message.newBuilder()
                .setUpdateFirmwareResponse(UpdateFirmwareResponse.newBuilder().setStatus(status))
                .build());
  }

  public void mockSetLightResponse(final String deviceUid, final Oslp.Status status) {
    LOGGER.info(MOCKING_MESSAGE_TYPE, deviceUid, MessageType.SET_LIGHT);
    this.devicesContext
        .getDeviceState(deviceUid)
        .addMockedResponse(
            MessageType.SET_LIGHT,
            Oslp.Message.newBuilder()
                .setSetLightResponse(SetLightResponse.newBuilder().setStatus(status))
                .build());
  }

  public void mockSetEventNotificationResponse(final String deviceUid, final Oslp.Status status) {
    LOGGER.info(MOCKING_MESSAGE_TYPE, deviceUid, MessageType.SET_EVENT_NOTIFICATIONS);
    this.devicesContext
        .getDeviceState(deviceUid)
        .addMockedResponse(
            MessageType.SET_EVENT_NOTIFICATIONS,
            Oslp.Message.newBuilder()
                .setSetEventNotificationsResponse(
                    SetEventNotificationsResponse.newBuilder().setStatus(status))
                .build());
  }

  public void mockStartDeviceResponse(final String deviceUid, final Oslp.Status status) {
    LOGGER.info(MOCKING_MESSAGE_TYPE, deviceUid, MessageType.START_SELF_TEST);
    this.devicesContext
        .getDeviceState(deviceUid)
        .addMockedResponse(
            MessageType.START_SELF_TEST,
            Oslp.Message.newBuilder()
                .setStartSelfTestResponse(StartSelfTestResponse.newBuilder().setStatus(status))
                .build());
  }

  public void mockStopDeviceResponse(
      final String deviceUid,
      final com.google.protobuf.ByteString value,
      final Oslp.Status status) {
    LOGGER.info(MOCKING_MESSAGE_TYPE, deviceUid, MessageType.STOP_SELF_TEST);
    this.devicesContext
        .getDeviceState(deviceUid)
        .addMockedResponse(
            MessageType.STOP_SELF_TEST,
            Oslp.Message.newBuilder()
                .setStopSelfTestResponse(
                    StopSelfTestResponse.newBuilder().setSelfTestResult(value).setStatus(status))
                .build());
  }

  public void mockGetStatusResponse(
      final String deviceUid,
      final LinkType preferred,
      final LinkType actual,
      final LightType lightType,
      final int eventNotificationMask,
      final Oslp.Status status,
      final List<LightValue> lightValues,
      final List<LightValue> tariffValues) {

    final Builder response =
        GetStatusResponse.newBuilder()
            .setPreferredLinktype(preferred)
            .setActualLinktype(actual)
            .setLightType(lightType)
            .setEventNotificationMask(eventNotificationMask)
            .setStatus(status);

    for (final LightValue lightValue : lightValues) {
      response.addValue(lightValue);
    }
    for (final LightValue tariffValue : tariffValues) {
      response.addValue(tariffValue);
    }

    LOGGER.info(MOCKING_MESSAGE_TYPE, deviceUid, MessageType.GET_STATUS);
    this.devicesContext
        .getDeviceState(deviceUid)
        .addMockedResponse(
            MessageType.GET_STATUS,
            Oslp.Message.newBuilder().setGetStatusResponse(response).build());
  }

  public void mockResumeScheduleResponse(final String deviceUid, final Oslp.Status status) {
    LOGGER.info(MOCKING_MESSAGE_TYPE, deviceUid, MessageType.RESUME_SCHEDULE);
    this.devicesContext
        .getDeviceState(deviceUid)
        .addMockedResponse(
            MessageType.RESUME_SCHEDULE,
            Oslp.Message.newBuilder()
                .setResumeScheduleResponse(ResumeScheduleResponse.newBuilder().setStatus(status))
                .build());
  }

  public void mockSetRebootResponse(final String deviceUid, final Oslp.Status status) {
    LOGGER.info(MOCKING_MESSAGE_TYPE, deviceUid, MessageType.SET_REBOOT);
    this.devicesContext
        .getDeviceState(deviceUid)
        .addMockedResponse(
            MessageType.SET_REBOOT,
            Oslp.Message.newBuilder()
                .setSetRebootResponse(SetRebootResponse.newBuilder().setStatus(status))
                .build());
  }

  public void mockSetTransitionResponse(final String deviceUid, final Oslp.Status status) {
    LOGGER.info(MOCKING_MESSAGE_TYPE, deviceUid, MessageType.SET_TRANSITION);
    this.devicesContext
        .getDeviceState(deviceUid)
        .addMockedResponse(
            MessageType.SET_TRANSITION,
            Oslp.Message.newBuilder()
                .setSetTransitionResponse(SetTransitionResponse.newBuilder().setStatus(status))
                .build());
  }

  public void mockUpdateKeyResponse(final String deviceUid, final Oslp.Status status) {
    LOGGER.info(MOCKING_MESSAGE_TYPE, deviceUid, MessageType.UPDATE_KEY);
    this.devicesContext
        .getDeviceState(deviceUid)
        .addMockedResponse(
            MessageType.UPDATE_KEY,
            Oslp.Message.newBuilder()
                .setSetDeviceVerificationKeyResponse(
                    SetDeviceVerificationKeyResponse.newBuilder().setStatus(status))
                .build());
  }

  public void mockGetLightStatusResponse(
      final String deviceUid,
      final LinkType preferred,
      final LinkType actual,
      final LightType lightType,
      final int eventNotificationMask,
      final Oslp.Status status,
      final List<LightValue> lightValues) {
    final Builder response =
        GetStatusResponse.newBuilder()
            .setPreferredLinktype(preferred)
            .setActualLinktype(actual)
            .setLightType(lightType)
            .setEventNotificationMask(eventNotificationMask)
            .setStatus(status);

    for (final LightValue lightValue : lightValues) {
      response.addValue(lightValue);
    }

    LOGGER.info(MOCKING_MESSAGE_TYPE, deviceUid, MessageType.GET_LIGHT_STATUS);
    this.devicesContext
        .getDeviceState(deviceUid)
        .addMockedResponse(
            MessageType.GET_LIGHT_STATUS,
            Oslp.Message.newBuilder().setGetStatusResponse(response).build());
  }

  public void mockSetScheduleResponse(
      final String deviceUid, final MessageType type, final Oslp.Status status) {
    LOGGER.info(MOCKING_MESSAGE_TYPE, deviceUid, type.name());
    this.devicesContext
        .getDeviceState(deviceUid)
        .addMockedResponse(
            type,
            Oslp.Message.newBuilder()
                .setSetScheduleResponse(SetScheduleResponse.newBuilder().setStatus(status))
                .build());
  }

  public String getOslpSignature() {
    return this.oslpSignature;
  }

  public String getOslpSignatureProvider() {
    return this.oslpSignatureProvider;
  }

  public Message waitForResponse() {
    return Wait.untilAndReturn(
        () -> {
          if (this.receivedResponses.isEmpty()) {
            throw new Exception("no response yet");
          }

          return this.receivedResponses.get(0);
        });
  }
}
