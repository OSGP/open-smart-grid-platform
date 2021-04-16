/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator.application.config;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.annotation.Resource;
import org.opensmartgridplatform.oslp.OslpDecoder;
import org.opensmartgridplatform.oslp.OslpEncoder;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.shared.infra.networking.DisposableNioEventLoopGroup;
import org.opensmartgridplatform.shared.security.CertificateHelper;
import org.opensmartgridplatform.webdevicesimulator.service.OslpChannelHandler;
import org.opensmartgridplatform.webdevicesimulator.service.OslpSecurityHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class OslpConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(OslpConfig.class);

  private static final String PROPERTY_NAME_OSLP_TIMEOUT_CONNECT = "oslp.timeout.connect";

  private static final String PROPERTY_NAME_OSLP_PORT_CLIENT = "oslp.port.client";
  private static final String PROPERTY_NAME_OSLP_ELSTER_PORT_CLIENT = "oslp.elster.port.client";
  private static final String PROPERTY_NAME_OSLP_PORT_SERVER = "oslp.port.server";
  private static final String PROPERTY_NAME_OSLP_ELSTER_PORT_SERVER = "oslp.elster.port.server";
  private static final String PROPERTY_NAME_OSLP_ADDRESS_CLIENT = "oslp.address.client";

  private static final String PROPERTY_NAME_OSLP_SECURITY_SIGNKEY_PATH =
      "oslp.security.signkey.path";
  private static final String PROPERTY_NAME_OSLP_SECURITY_VERIFYKEY_PATH =
      "oslp.security.verifykey.path";
  private static final String PROPERTY_NAME_OSLP_SECURITY_KEYTYPE = "oslp.security.keytype";
  private static final String PROPERTY_NAME_OSLP_SECURITY_SIGNATURE = "oslp.security.signature";
  private static final String PROPERTY_NAME_OSLP_SECURITY_PROVIDER = "oslp.security.provider";
  private static final String PROPERTY_NAME_OSLP_SEQUENCE_NUMBER_WINDOW =
      "oslp.sequence.number.window";
  private static final String PROPERTY_NAME_OSLP_SEQUENCE_NUMBER_MAXIMUM =
      "oslp.sequence.number.maximum";

  @Resource private Environment environment;

  @Bean(name = "deviceSimulatorOslpNettyServerBossGroup")
  public DisposableNioEventLoopGroup serverBossGroup() {
    return new DisposableNioEventLoopGroup();
  }

  @Bean(name = "deviceSimulatorOslpNettyServerWorkerGroup")
  public DisposableNioEventLoopGroup serverWorkerGroup() {
    return new DisposableNioEventLoopGroup();
  }

  @Bean(name = "deviceSimulatorOslpNettyClientWorkerGroup")
  public DisposableNioEventLoopGroup clientWorkerGroup() {
    return new DisposableNioEventLoopGroup();
  }

  @Bean()
  public Bootstrap clientBootstrap() {

    final Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(this.clientWorkerGroup());
    bootstrap.channel(NioSocketChannel.class);
    bootstrap.handler(
        new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(final SocketChannel ch) throws Exception {
            OslpConfig.this.createChannelPipeline(ch);
          }
        });

    bootstrap.option(ChannelOption.TCP_NODELAY, true);
    bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
    bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.connectionTimeout());

    return bootstrap;
  }

  @Bean()
  public ServerBootstrap serverBootstrap() {
    return this.createServerBootstrap(this.oslpPortServer());
  }

  @Bean()
  public ServerBootstrap serverBootstrapElster() {
    return this.createServerBootstrap(this.oslpElsterPortServer());
  }

  private ServerBootstrap createServerBootstrap(final int port) {
    final ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(this.serverBossGroup(), this.serverWorkerGroup());
    bootstrap.channel(NioServerSocketChannel.class);
    bootstrap.childHandler(
        new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(final SocketChannel ch) throws Exception {
            OslpConfig.this.createChannelPipeline(ch);
            LOGGER.info("Created server new pipeline");
          }
        });

    bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
    bootstrap.childOption(ChannelOption.SO_KEEPALIVE, false);

    bootstrap.bind(new InetSocketAddress(port));

    return bootstrap;
  }

  private ChannelPipeline createChannelPipeline(final Channel channel) {
    final ChannelPipeline pipeline = channel.pipeline();

    pipeline.addLast("oslpEncoder", new OslpEncoder());
    pipeline.addLast(
        "oslpDecoder", new OslpDecoder(this.oslpSignature(), this.oslpSignatureProvider()));
    pipeline.addLast("oslpSecurity", this.oslpSecurityHandler());

    pipeline.addLast("oslpChannelHandler", this.oslpChannelHandler());
    return pipeline;
  }

  @Bean
  public SimpleChannelInboundHandler<OslpEnvelope> oslpSecurityHandler() {
    return new OslpSecurityHandler();
  }

  @Bean
  public OslpDecoder oslpDecoder() {
    return new OslpDecoder(this.oslpSignature(), this.oslpSignatureProvider());
  }

  @Bean
  public PublicKey publicKey() throws IOException {
    return CertificateHelper.createPublicKey(
        this.environment.getProperty(PROPERTY_NAME_OSLP_SECURITY_VERIFYKEY_PATH),
        this.environment.getProperty(PROPERTY_NAME_OSLP_SECURITY_KEYTYPE),
        this.environment.getProperty(PROPERTY_NAME_OSLP_SECURITY_PROVIDER));
  }

  @Bean
  public PrivateKey privateKey() throws IOException {
    return CertificateHelper.createPrivateKey(
        this.environment.getProperty(PROPERTY_NAME_OSLP_SECURITY_SIGNKEY_PATH),
        this.environment.getProperty(PROPERTY_NAME_OSLP_SECURITY_KEYTYPE),
        this.environment.getProperty(PROPERTY_NAME_OSLP_SECURITY_PROVIDER));
  }

  @Bean
  public String oslpSignatureProvider() {
    return this.environment.getProperty(PROPERTY_NAME_OSLP_SECURITY_PROVIDER);
  }

  @Bean
  public String oslpSignature() {
    return this.environment.getProperty(PROPERTY_NAME_OSLP_SECURITY_SIGNATURE);
  }

  @Bean
  public String oslpKeyType() {
    return this.environment.getProperty(PROPERTY_NAME_OSLP_SECURITY_KEYTYPE);
  }

  @Bean
  public int connectionTimeout() {
    return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_OSLP_TIMEOUT_CONNECT));
  }

  @Bean
  public int oslpPortClient() {
    return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_OSLP_PORT_CLIENT));
  }

  @Bean
  public int oslpElsterPortClient() {
    return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_OSLP_ELSTER_PORT_CLIENT));
  }

  @Bean
  public int oslpPortServer() {
    return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_OSLP_PORT_SERVER));
  }

  @Bean
  public int oslpElsterPortServer() {
    return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_OSLP_ELSTER_PORT_SERVER));
  }

  @Bean
  public String oslpAddressServer() {
    return this.environment.getProperty(PROPERTY_NAME_OSLP_ADDRESS_CLIENT);
  }

  @Bean
  public OslpChannelHandler oslpChannelHandler() {
    return new OslpChannelHandler();
  }

  @Bean
  public Integer sequenceNumberWindow() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_SEQUENCE_NUMBER_WINDOW));
  }

  @Bean
  public Integer sequenceNumberMaximum() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_SEQUENCE_NUMBER_MAXIMUM));
  }
}
