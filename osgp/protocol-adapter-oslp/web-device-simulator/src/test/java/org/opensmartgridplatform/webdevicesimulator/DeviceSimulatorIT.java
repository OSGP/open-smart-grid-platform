/*
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.webdevicesimulator.Assertions.assertThat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.ByteBufFormat;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.oslp.Oslp;
import org.opensmartgridplatform.oslp.OslpDecoder;
import org.opensmartgridplatform.oslp.OslpEncoder;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.shared.infra.networking.DisposableNioEventLoopGroup;
import org.opensmartgridplatform.webdevicesimulator.application.config.OslpConfig;
import org.opensmartgridplatform.webdevicesimulator.application.services.DeviceManagementService;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.Device;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.DeviceMessageStatus;
import org.opensmartgridplatform.webdevicesimulator.domain.entities.OslpLogItem;
import org.opensmartgridplatform.webdevicesimulator.domain.repositories.OslpLogItemRepository;
import org.opensmartgridplatform.webdevicesimulator.service.OslpSecurityHandler;
import org.opensmartgridplatform.webdevicesimulator.service.RegisterDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@ContextConfiguration(classes = {TestConfig.class, OslpConfig.class})
class DeviceSimulatorIT {

  private static final AtomicLong DEVICE_ID = new AtomicLong(0);

  private static final int REBOOT_DELAY_IN_SECONDS = 1;

  @Autowired private OslpConfig oslpConfig;

  @Autowired private TestConfig testConfig;

  @Autowired private DeviceManagementService deviceManagementService;

  @Autowired private RegisterDevice registerDevice;

  @Autowired private OslpLogItemRepository oslpLogItemRepository;

  private final Random random = new SecureRandom();

  @Mock private Consumer<OslpEnvelope> oslpEnvelopeConsumer;

  @Captor private ArgumentCaptor<OslpEnvelope> oslpEnvelopeCaptor;

  @Captor private ArgumentCaptor<OslpLogItem> oslpLogItemCaptor;

  @BeforeEach
  void setUp() {
    when(this.deviceManagementService.getRebootDelay()).thenReturn(REBOOT_DELAY_IN_SECONDS);
  }

  @AfterEach
  void tearDown() {
    clearInvocations(this.oslpLogItemRepository, this.registerDevice);
  }

  @Test
  void savesOslpLogItemsForRequestAndResponse() throws Exception {
    final long id = DEVICE_ID.incrementAndGet();
    final int sequenceNumber = this.aSequenceNumber();
    final byte[] deviceUid = this.aDeviceUid();
    final Device device =
        this.aDevice(id, String.format("TST-%03d", id), deviceUid, sequenceNumber);
    when(this.deviceManagementService.findDevice(device.getDeviceUid())).thenReturn(device);
    final Channel channel = this.activeChannelToSimulator();
    channel.writeAndFlush(this.setRebootEnvelope(deviceUid, sequenceNumber));
    channel.closeFuture().awaitUninterruptibly(1000 * (REBOOT_DELAY_IN_SECONDS + 1));
    verify(this.oslpLogItemRepository, times(2)).save(this.oslpLogItemCaptor.capture());
    final List<OslpLogItem> savedOslpLogItems = this.oslpLogItemCaptor.getAllValues();
    savedOslpLogItems.forEach(
        savedOslpLogItem -> {
          assertThat(savedOslpLogItem.getDeviceUid()).isEqualTo(device.getDeviceUid());
        });
    assertThat(savedOslpLogItems.get(0).isIncoming()).isTrue();
    assertThat(savedOslpLogItems.get(1).isIncoming()).isFalse();
  }

  @Test
  void sendsRegisterDeviceCommandOnRebootRequest() throws Exception {
    final long id = DEVICE_ID.incrementAndGet();
    final int sequenceNumber = this.aSequenceNumber();
    final byte[] deviceUid = this.aDeviceUid();
    final Device device =
        this.aDevice(id, String.format("TST-%03d", id), deviceUid, sequenceNumber);
    when(this.deviceManagementService.findDevice(device.getDeviceUid())).thenReturn(device);
    when(this.registerDevice.sendRegisterDeviceCommand(id, true))
        .thenReturn(DeviceMessageStatus.FAILURE);

    final Channel channel = this.activeChannelToSimulator();
    channel.writeAndFlush(this.setRebootEnvelope(deviceUid, sequenceNumber));
    channel.closeFuture().awaitUninterruptibly(1000 * (REBOOT_DELAY_IN_SECONDS + 1));
    verify(this.registerDevice).sendRegisterDeviceCommand(id, true);
  }

  @Test
  void returnsASetRebootResponseOnRebootRequest() throws Exception {
    final long id = DEVICE_ID.incrementAndGet();
    final int sequenceNumber = this.aSequenceNumber();
    final byte[] deviceUid = this.aDeviceUid();
    final Device device =
        this.aDevice(id, String.format("TST-%03d", id), deviceUid, sequenceNumber);
    when(this.deviceManagementService.findDevice(device.getDeviceUid())).thenReturn(device);
    when(this.registerDevice.sendRegisterDeviceCommand(id, true))
        .thenReturn(DeviceMessageStatus.FAILURE);

    final Channel channel = this.activeChannelToSimulator();
    channel.writeAndFlush(this.setRebootEnvelope(deviceUid, sequenceNumber));
    channel.closeFuture().awaitUninterruptibly(1000 * (REBOOT_DELAY_IN_SECONDS + 1));
    verify(this.oslpEnvelopeConsumer).accept(this.oslpEnvelopeCaptor.capture());
    final OslpEnvelope responseEnvelope = this.oslpEnvelopeCaptor.getValue();
    assertThat(responseEnvelope).hasDeviceId(deviceUid).hasMessageWithName("setRebootResponse");
  }

  @Test
  void doesNotProcessRequestsDuringRebootDelay() throws Exception {
    this.whenUsingARebootDelayOf(60);
    final long id = DEVICE_ID.incrementAndGet();
    final int sequenceNumber = this.aSequenceNumber();
    final byte[] deviceUid = this.aDeviceUid();
    final Device device =
        this.aDevice(id, String.format("TST-%03d", id), deviceUid, sequenceNumber);
    when(this.deviceManagementService.findDevice(device.getDeviceUid())).thenReturn(device);
    when(this.registerDevice.sendRegisterDeviceCommand(id, true))
        .thenReturn(DeviceMessageStatus.FAILURE);
    Channel channel = this.activeChannelToSimulator();
    channel.writeAndFlush(this.setRebootEnvelope(deviceUid, sequenceNumber));
    channel.closeFuture().awaitUninterruptibly(1000);
    channel = this.activeChannelToSimulator();
    channel.writeAndFlush(this.setRebootEnvelope(deviceUid, sequenceNumber));
    channel.closeFuture().awaitUninterruptibly(1000);
    verify(this.oslpLogItemRepository, times(2)).save(any(OslpLogItem.class));
    verify(this.oslpEnvelopeConsumer).accept(any(OslpEnvelope.class));
  }

  @Test
  void processesRequestAfterRebootDelay() throws Exception {
    final long id = DEVICE_ID.incrementAndGet();
    final int sequenceNumber = this.aSequenceNumber();
    final byte[] deviceUid = this.aDeviceUid();
    final Device device =
        this.aDevice(id, String.format("TST-%03d", id), deviceUid, sequenceNumber);
    when(this.deviceManagementService.findDevice(device.getDeviceUid())).thenReturn(device);
    when(this.registerDevice.sendRegisterDeviceCommand(id, true))
        .thenReturn(DeviceMessageStatus.FAILURE);
    Channel channel = this.activeChannelToSimulator();
    channel.writeAndFlush(this.setRebootEnvelope(deviceUid, sequenceNumber));
    channel.closeFuture().awaitUninterruptibly(1000 * (REBOOT_DELAY_IN_SECONDS + 1));
    channel = this.activeChannelToSimulator();
    channel.writeAndFlush(this.setRebootEnvelope(deviceUid, sequenceNumber));
    channel.closeFuture().awaitUninterruptibly(1000);
    verify(this.oslpLogItemRepository, times(4)).save(any(OslpLogItem.class));
    verify(this.oslpEnvelopeConsumer, times(2)).accept(any(OslpEnvelope.class));
  }

  @Test
  void allowsRequestForOtherDeviceDuringRebootDelay() throws Exception {
    this.whenUsingARebootDelayOf(60);
    final long id1 = DEVICE_ID.incrementAndGet();
    final int sequenceNumber1 = this.aSequenceNumber();
    final byte[] deviceUid1 = this.aDeviceUid();
    final Device device1 =
        this.aDevice(id1, String.format("TST-%03d", id1), deviceUid1, sequenceNumber1);
    when(this.deviceManagementService.findDevice(device1.getDeviceUid())).thenReturn(device1);
    when(this.registerDevice.sendRegisterDeviceCommand(id1, true))
        .thenReturn(DeviceMessageStatus.FAILURE);
    final long id2 = DEVICE_ID.incrementAndGet();
    final int sequenceNumber2 = this.aSequenceNumber();
    final byte[] deviceUid2 = this.aDeviceUid();
    final Device device2 =
        this.aDevice(id2, String.format("TST-%03d", id2), deviceUid2, sequenceNumber2);
    when(this.deviceManagementService.findDevice(device2.getDeviceUid())).thenReturn(device2);
    when(this.registerDevice.sendRegisterDeviceCommand(id2, true))
        .thenReturn(DeviceMessageStatus.FAILURE);
    Channel channel = this.activeChannelToSimulator();
    channel.writeAndFlush(this.setRebootEnvelope(deviceUid1, sequenceNumber1));
    channel.closeFuture().awaitUninterruptibly(1000);
    channel = this.activeChannelToSimulator();
    channel.writeAndFlush(this.setRebootEnvelope(deviceUid2, sequenceNumber2));
    channel.closeFuture().awaitUninterruptibly(1000);
    verify(this.oslpLogItemRepository, times(4)).save(any(OslpLogItem.class));
    verify(this.oslpEnvelopeConsumer, times(2)).accept(this.oslpEnvelopeCaptor.capture());
    final List<OslpEnvelope> responseEnvelopes = this.oslpEnvelopeCaptor.getAllValues();
    assertThat(responseEnvelopes.get(0))
        .hasDeviceId(deviceUid1)
        .hasMessageWithName("setRebootResponse");
    assertThat(responseEnvelopes.get(1))
        .hasDeviceId(deviceUid2)
        .hasMessageWithName("setRebootResponse");
  }

  private int aSequenceNumber() {
    return this.random.nextInt(-2 * Short.MIN_VALUE);
  }

  private byte[] aDeviceUid() {
    final byte[] bytes = new byte[12];
    this.random.nextBytes(bytes);
    return bytes;
  }

  private Device aDevice(
      final Long id,
      final String deviceIdentification,
      final byte[] deviceUid,
      final int sequenceNumber) {
    final Device device = new Device();
    ReflectionTestUtils.setField(device, "id", id);
    device.setDeviceIdentification(deviceIdentification);
    device.setDeviceUid(deviceUid);
    device.setSequenceNumber(sequenceNumber);
    return device;
  }

  private Channel activeChannelToSimulator() throws IOException {
    final ChannelFuture channelFuture =
        this.testClientBootstrap()
            .connect(this.oslpConfig.oslpAddressServer(), this.oslpConfig.oslpElsterPortServer());
    if (!channelFuture.awaitUninterruptibly(
        this.oslpConfig.connectionTimeout(), TimeUnit.MILLISECONDS)) {
      throw new IOException("Unable to connect");
    }
    channelFuture.addListener(
        new ChannelFutureListener() {
          @Override
          public void operationComplete(final ChannelFuture future) throws IOException {
            final boolean success = future.isSuccess();
            final boolean done = future.isDone();
            final boolean cancelled = future.isCancelled();
            System.out.printf(
                "%nChannel operationComplete - success %b, done %b, cancelled %b%n%n",
                success, done, cancelled);
          }
        });
    return channelFuture.channel();
  }

  private OslpEnvelope setRebootEnvelope(final byte[] deviceId, final int sequenceNumber)
      throws Exception {
    return new OslpEnvelope.Builder()
        .withProvider(this.oslpConfig.oslpSignatureProvider())
        .withSignature(this.oslpConfig.oslpSignature())
        .withPrimaryKey(this.testConfig.privateKeySigningServer())
        .withSequenceNumber(ByteBuffer.allocate(2).putShort((short) sequenceNumber).array())
        .withDeviceId(deviceId)
        .withPayloadMessage(
            Oslp.Message.newBuilder()
                .setSetRebootRequest(Oslp.SetRebootRequest.newBuilder())
                .build())
        .build();
  }

  private Bootstrap testClientBootstrap() {

    final Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(new DisposableNioEventLoopGroup());
    bootstrap.channel(NioSocketChannel.class);
    bootstrap.handler(
        new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(final SocketChannel ch) throws Exception {
            final ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(
                "loggingHandler", new LoggingHandler(LogLevel.INFO, ByteBufFormat.HEX_DUMP));
            pipeline.addLast("oslpEncoder", new OslpEncoder());
            pipeline.addLast(
                "oslpDecoder",
                new OslpDecoder(
                    DeviceSimulatorIT.this.oslpConfig.oslpSignature(),
                    DeviceSimulatorIT.this.oslpConfig.oslpSignatureProvider()));
            final OslpSecurityHandler oslpSecurityHandler = new OslpSecurityHandler();
            ReflectionTestUtils.setField(
                oslpSecurityHandler,
                "publicKey",
                DeviceSimulatorIT.this.testConfig.publicKeySimulator());
            pipeline.addLast("oslpSecurity", oslpSecurityHandler);
            pipeline.addLast(new OslpTestHandler(DeviceSimulatorIT.this.oslpEnvelopeConsumer));
          }
        });

    bootstrap.option(ChannelOption.TCP_NODELAY, true);
    bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
    bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.oslpConfig.connectionTimeout());

    return bootstrap;
  }

  private void whenUsingARebootDelayOf(final int rebootDelayInSeconds) {
    reset(this.deviceManagementService);
    when(this.deviceManagementService.getRebootDelay()).thenReturn(rebootDelayInSeconds);
  }
}
