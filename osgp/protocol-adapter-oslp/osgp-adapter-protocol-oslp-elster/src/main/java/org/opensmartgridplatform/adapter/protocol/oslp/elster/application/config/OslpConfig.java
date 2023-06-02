//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.ByteBufFormat;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking.ChannelCache;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking.OslpChannelHandlerClient;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking.OslpChannelHandlerServer;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking.OslpSecurityHandler;
import org.opensmartgridplatform.oslp.OslpDecoder;
import org.opensmartgridplatform.oslp.OslpEncoder;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.infra.networking.DisposableNioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** An application context Java configuration class. */
@Configuration
public class OslpConfig extends AbstractConfig {
  private static final String PROPERTY_NAME_OSLP_TIMEOUT_CONNECT = "oslp.timeout.connect";
  private static final String PROPERTY_NAME_OSLP_CONCURRENT_CLIENT_CONNECTIONS_LIMIT_ACTIVE =
      "oslp.concurrent.client.connections.limit.active";
  private static final String PROPERTY_NAME_OSLP_CONCURRENT_CLIENT_CONNECTIONS_MAXIMUM =
      "oslp.concurrent.client.connections.maximum";

  private static final String PROPERTY_NAME_OSLP_CHANNEL_CACHE_EXPIRATION_MILLIS =
      "oslp.channel.cache.expiration.millis";

  private static final String PROPERTY_NAME_OSLP_PORT_CLIENT = "oslp.port.client";
  private static final String PROPERTY_NAME_OSLP_PORT_CLIENTLOCAL = "oslp.port.clientlocal";

  private static final String PROPERTY_NAME_OSLP_PORT_SERVER = "oslp.port.server";

  private static final String PROPERTY_NAME_OSLP_SECURITY_KEYTYPE = "oslp.security.keytype";
  private static final String PROPERTY_NAME_OSLP_SECURITY_SIGNATURE = "oslp.security.signature";
  private static final String PROPERTY_NAME_OSLP_SECURITY_PROVIDER = "oslp.security.provider";
  private static final String PROPERTY_NAME_OSLP_SEQUENCE_NUMBER_WINDOW =
      "oslp.sequence.number.window";
  private static final String PROPERTY_NAME_OSLP_SEQUENCE_NUMBER_MAXIMUM =
      "oslp.sequence.number.maximum";

  private static final String PROPERTY_NAME_OSLP_EXECUTE_RESUME_SCHEDULE_AFTER_SET_LIGHT =
      "oslp.execute.resume.schedule.after.set.light";

  private static final String PROPERTY_NAME_OSLP_EXECUTE_REBOOT_AFTER_SET_CONFIGURATION =
      "oslp.execute.reboot.after.set.configuration";

  private static final String PROPERTY_NAME_OSLP_DEFAULT_LATITUDE = "oslp.default.latitude";
  private static final String PROPERTY_NAME_OSLP_DEFAULT_LONGITUDE = "oslp.default.longitude";

  private static final String PROPERTY_NAME_OSLP_NETTY_LOG_LEVEL = "oslp.netty.log.level";
  private static final String PROPERTY_NAME_OSLP_NETTY_BYTE_BUF_FORMAT =
      "oslp.netty.byte.buf.format";

  private static final Logger LOGGER = LoggerFactory.getLogger(OslpConfig.class);

  @Bean(name = "protocolAdapterOslpNettyServerBossGroup")
  public DisposableNioEventLoopGroup serverBossGroup() {
    return new DisposableNioEventLoopGroup();
  }

  @Bean(name = "protocolAdapterOslpNettyServerWorkerGroup")
  public DisposableNioEventLoopGroup serverWorkerGroup() {
    return new DisposableNioEventLoopGroup();
  }

  @Bean(name = "protocolAdapterOslpNettyClientWorkerGroup")
  public DisposableNioEventLoopGroup clientWorkerGroup() {
    return new DisposableNioEventLoopGroup();
  }

  @Bean
  public Bootstrap clientBootstrap() {

    LOGGER.info("Initializing clientBootstrap bean.");

    final Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(this.clientWorkerGroup());
    bootstrap.channel(NioSocketChannel.class);
    bootstrap.handler(
        new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(final SocketChannel ch) throws Exception {
            OslpConfig.this.createChannelPipeline(ch, OslpConfig.this.oslpChannelHandlerClient());
          }
        });

    bootstrap.option(ChannelOption.TCP_NODELAY, true);
    bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
    bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.connectionTimeout());

    return bootstrap;
  }

  @Bean
  public ServerBootstrap serverBootstrap() {

    LOGGER.info("Initializing serverBootstrap bean.");

    final ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(this.serverBossGroup(), this.serverWorkerGroup());
    bootstrap.channel(NioServerSocketChannel.class);
    bootstrap.childHandler(
        new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(final SocketChannel ch) throws Exception {
            OslpConfig.this.createChannelPipeline(ch, OslpConfig.this.oslpChannelHandlerServer());
            LOGGER.info("Created server new pipeline");
          }
        });

    bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
    bootstrap.childOption(ChannelOption.SO_KEEPALIVE, false);

    bootstrap.bind(new InetSocketAddress(this.oslpPortServer()));

    return bootstrap;
  }

  private void createChannelPipeline(final SocketChannel channel, final ChannelHandler handler) {
    channel
        .pipeline()
        .addLast("loggingHandler", this.loggingHandler(this.nettyLogLevel(), this.byteBufFormat()));
    channel.pipeline().addLast("oslpEncoder", new OslpEncoder());
    channel
        .pipeline()
        .addLast(
            "oslpDecoder", new OslpDecoder(this.oslpSignature(), this.oslpSignatureProvider()));
    channel.pipeline().addLast("oslpSecurity", this.oslpSecurityHandler());
    channel.pipeline().addLast("oslpChannelHandler", handler);
  }

  @Bean
  public LoggingHandler loggingHandler(
      final LogLevel nettyLogLevel, final ByteBufFormat byteBufFormat) {
    return new LoggingHandler(nettyLogLevel, byteBufFormat);
  }

  @Bean
  public LogLevel nettyLogLevel() {
    final LogLevel defaultLogLevel = LogLevel.INFO;
    final String logLevelName =
        this.environment.getProperty(PROPERTY_NAME_OSLP_NETTY_LOG_LEVEL, defaultLogLevel.name());
    try {
      return LogLevel.valueOf(logLevelName);
    } catch (final IllegalArgumentException e) {
      LOGGER.error(
          "Configured value for {}: \"{}\" is not a known LogLevel name; using default: {}",
          PROPERTY_NAME_OSLP_NETTY_LOG_LEVEL,
          logLevelName,
          defaultLogLevel,
          e);
    }
    return defaultLogLevel;
  }

  @Bean
  public ByteBufFormat byteBufFormat() {
    final ByteBufFormat defaultByteBufFormat = ByteBufFormat.SIMPLE;
    final String byteBufFormatName =
        this.environment.getProperty(
            PROPERTY_NAME_OSLP_NETTY_BYTE_BUF_FORMAT, defaultByteBufFormat.name());
    try {
      return ByteBufFormat.valueOf(byteBufFormatName);
    } catch (final IllegalArgumentException e) {
      LOGGER.error(
          "Configured value for {}: \"{}\" is not a known ByteBufFormat name; using default: {}",
          PROPERTY_NAME_OSLP_NETTY_BYTE_BUF_FORMAT,
          byteBufFormatName,
          defaultByteBufFormat,
          e);
    }
    return defaultByteBufFormat;
  }

  @Bean
  public OslpSecurityHandler oslpSecurityHandler() {
    return new OslpSecurityHandler();
  }

  @Bean
  public OslpDecoder oslpDecoder() {
    return new OslpDecoder(this.oslpSignature(), this.oslpSignatureProvider());
  }

  @Bean
  public String oslpKeyType() {
    return this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_SECURITY_KEYTYPE);
  }

  @Bean
  public String oslpSignatureProvider() {
    return this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_SECURITY_PROVIDER);
  }

  @Bean
  public String oslpSignature() {
    return this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_SECURITY_SIGNATURE);
  }

  @Bean
  public int connectionTimeout() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_TIMEOUT_CONNECT));
  }

  @Bean
  public int oslpPortClient() {
    return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_PORT_CLIENT));
  }

  @Bean
  public int oslpPortClientLocal() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_PORT_CLIENTLOCAL));
  }

  @Bean
  public int oslpPortServer() {
    return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_PORT_SERVER));
  }

  @Bean
  public ChannelCache channelCache() {
    final long oneDayInMillis = TimeUnit.DAYS.toMillis(1);
    final String millisText =
        this.environment.getProperty(PROPERTY_NAME_OSLP_CHANNEL_CACHE_EXPIRATION_MILLIS);
    if (StringUtils.isBlank(millisText)) {
      LOGGER.info(
          "No configured value found for {}, ChannelCache expiration is set to 1 day",
          PROPERTY_NAME_OSLP_CHANNEL_CACHE_EXPIRATION_MILLIS);
      return new ChannelCache(oneDayInMillis);
    }
    try {
      final long expirationMillis = Long.parseLong(millisText);
      final long fiveMinutesInMillis = TimeUnit.MINUTES.toMillis(5);
      if (expirationMillis < fiveMinutesInMillis) {
        LOGGER.warn(
            "Configured value for {} ({}) is less than five minutes, ChannelCache expiration is set to 5 minutes",
            PROPERTY_NAME_OSLP_CHANNEL_CACHE_EXPIRATION_MILLIS,
            millisText);
      } else {
        LOGGER.info(
            "ChannelCache configured with {} milliseconds before cached channels expire",
            millisText);
      }
      return new ChannelCache(Long.max(expirationMillis, fiveMinutesInMillis));
    } catch (final NumberFormatException e) {
      LOGGER.error(
          "Configured value for {} ({}) is not a long value, ChannelCache expiration is set to 1 day",
          PROPERTY_NAME_OSLP_CHANNEL_CACHE_EXPIRATION_MILLIS,
          millisText);
      return new ChannelCache(oneDayInMillis);
    }
  }

  @Bean
  public OslpChannelHandlerServer oslpChannelHandlerServer() {
    return this.oslpConcurrentClientConnectionsLimitActive()
        ? new OslpChannelHandlerServer(this.oslpConcurrentClientConnectionsMaximum())
        : new OslpChannelHandlerServer();
  }

  @Bean
  public OslpChannelHandlerClient oslpChannelHandlerClient() {
    return new OslpChannelHandlerClient();
  }

  private boolean oslpConcurrentClientConnectionsLimitActive() {
    return Boolean.parseBoolean(
        this.environment.getRequiredProperty(
            PROPERTY_NAME_OSLP_CONCURRENT_CLIENT_CONNECTIONS_LIMIT_ACTIVE));
  }

  private int oslpConcurrentClientConnectionsMaximum() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(
            PROPERTY_NAME_OSLP_CONCURRENT_CLIENT_CONNECTIONS_MAXIMUM));
  }

  // === Sequence number config ===

  @Bean
  @Qualifier("sequenceNumberWindow")
  public Integer sequenceNumberWindow() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_SEQUENCE_NUMBER_WINDOW));
  }

  @Bean
  @Qualifier("sequenceNumberMaximum")
  public Integer sequenceNumberMaximum() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_SEQUENCE_NUMBER_MAXIMUM));
  }

  @Bean
  public boolean executeResumeScheduleAfterSetLight() {
    return Boolean.parseBoolean(
        this.environment.getRequiredProperty(
            PROPERTY_NAME_OSLP_EXECUTE_RESUME_SCHEDULE_AFTER_SET_LIGHT));
  }

  @Bean
  public boolean executeRebootAfterSetConfiguration() {
    return Boolean.parseBoolean(
        this.environment.getRequiredProperty(
            PROPERTY_NAME_OSLP_EXECUTE_REBOOT_AFTER_SET_CONFIGURATION));
  }

  @Bean
  public Float defaultLatitude() {
    return Float.parseFloat(
        this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_DEFAULT_LATITUDE));
  }

  @Bean
  public Float defaultLongitude() {
    return Float.parseFloat(
        this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_DEFAULT_LONGITUDE));
  }
}
