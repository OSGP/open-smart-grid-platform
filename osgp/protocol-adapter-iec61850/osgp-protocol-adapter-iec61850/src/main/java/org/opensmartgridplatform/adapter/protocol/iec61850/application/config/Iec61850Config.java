// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.application.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.net.InetSocketAddress;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.entities.Iec61850Device;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850ChannelHandlerServer;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.RegisterDeviceRequestDecoder;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.infra.networking.DisposableNioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:osgp-adapter-protocol-iec61850.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(
    value = "file:${osgp/AdapterProtocolIec61850/config}",
    ignoreResourceNotFound = true)
public class Iec61850Config extends AbstractConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850Config.class);

  private static final String DEFAULT_PROPERTY_MESSAGE = "Using default value {} for property {}";
  private static final String PROPERTY_IS_VALUE = "{}={}";

  private static final String PROPERTY_NAME_IEC61850_TIMEOUT_CONNECT = "iec61850.timeout.connect";
  private static final String PROPERTY_NAME_IEC61850_PORT_CLIENT = "iec61850.port.client";
  private static final String PROPERTY_NAME_IEC61850_PORT_CLIENTLOCAL = "iec61850.port.clientlocal";
  private static final String PROPERTY_NAME_IEC61850_SSLD_PORT_SERVER = "iec61850.ssld.port.server";
  private static final String PROPERTY_NAME_IEC61850_RTU_PORT_SERVER = "iec61850.rtu.port.server";
  private static final String PROPERTY_NAME_IEC61850_PORT_LISTENER = "iec61850.port.listener";

  private static final String PROPERTY_NAME_IEC61850_DELAY_AFTER_DEVICE_REGISTRATION =
      "iec61850.delay.after.device.registration";
  private static final String
      PROPERTY_NAME_IEC61850_IS_REPORTING_AFTER_DEVICE_REGISTRATION_ENABLED =
          "iec61850.is.reporting.after.device.registration.enabled";
  private static final String PROPERTY_NAME_IEC61850_DISCONNECT_DELAY = "iec61850.disconnect.delay";

  private static final String PROPERTY_NAME_IEC61850_ICD_FILE_PATH = "iec61850.icd.file.path";
  private static final String PROPERTY_NAME_IEC61850_ICD_FILE_USE = "iec61850.icd.file.use";

  private static final String PROPERTY_NAME_IEC61850_ICD_FILES_FOLDER = "iec61850.icd.files.folder";

  private static final String PROPERTY_NAME_IEC61850_IS_BUFFERED_REPORTING_ENABLED =
      "iec61850.is.buffered.reporting.enabled";

  private static final String PROPERTY_NAME_OSLP_DEFAULT_LATITUDE = "iec61850.default.latitude";
  private static final String PROPERTY_NAME_OSLP_DEFAULT_LONGITUDE = "iec61850.default.longitude";

  @Bean
  public int connectionTimeout() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(PROPERTY_NAME_IEC61850_TIMEOUT_CONNECT));
  }

  @Bean
  public int iec61850PortClient() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(PROPERTY_NAME_IEC61850_PORT_CLIENT));
  }

  @Bean
  public int iec61850PortClientLocal() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(PROPERTY_NAME_IEC61850_PORT_CLIENTLOCAL));
  }

  @Bean
  public int iec61850SsldPortServer() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(PROPERTY_NAME_IEC61850_SSLD_PORT_SERVER));
  }

  @Bean
  public int iec61850RtuPortServer() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(PROPERTY_NAME_IEC61850_RTU_PORT_SERVER));
  }

  @Bean(name = "protocolAdapterIec61850NettyServerBossGroup")
  public DisposableNioEventLoopGroup serverBossGroup() {
    return new DisposableNioEventLoopGroup();
  }

  @Bean(name = "protocolAdapterIec61850NettyServerWorkerGroup")
  public DisposableNioEventLoopGroup serverWorkerGroup() {
    return new DisposableNioEventLoopGroup();
  }

  /**
   * Returns a ServerBootstrap setting up a server pipeline listening for incoming IEC61850 register
   * device requests.
   *
   * @return an IEC61850 server bootstrap.
   */
  @Bean()
  public ServerBootstrap serverBootstrap() {
    LOGGER.info("Initializing serverBootstrap bean.");

    final ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(this.serverBossGroup(), this.serverWorkerGroup());
    bootstrap.channel(NioServerSocketChannel.class);
    bootstrap.childHandler(
        new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(final SocketChannel ch) throws Exception {
            Iec61850Config.this.createChannelPipeline(
                ch, Iec61850Config.this.iec61850ChannelHandlerServer());
            LOGGER.info("Created server new pipeline");
          }
        });

    bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
    bootstrap.childOption(ChannelOption.SO_KEEPALIVE, false);

    bootstrap.bind(new InetSocketAddress(this.iec61850PortListener()));

    return bootstrap;
  }

  private ChannelPipeline createChannelPipeline(
      final SocketChannel channel, final ChannelHandler handler) {
    final ChannelPipeline pipeline = channel.pipeline();
    pipeline.addLast("loggingHandler", new LoggingHandler(LogLevel.INFO));
    pipeline.addLast("iec61850RegisterDeviceRequestDecoder", new RegisterDeviceRequestDecoder());
    pipeline.addLast("iec61850ChannelHandler", handler);
    return pipeline;
  }

  /**
   * Returns the port the server is listening on for incoming IEC61850 requests.
   *
   * @return the port number of the IEC61850 listener endpoint.
   */
  @Bean
  public int iec61850PortListener() {
    return Integer.parseInt(
        this.environment.getRequiredProperty(PROPERTY_NAME_IEC61850_PORT_LISTENER));
  }

  /**
   * @return a new {@link Iec61850ChannelHandlerServer}.
   */
  @Bean
  public Iec61850ChannelHandlerServer iec61850ChannelHandlerServer() {
    return new Iec61850ChannelHandlerServer();
  }

  /**
   * Used to configure how long (in milliseconds) the connection is kept open to allow the device to
   * send reports. If this property is not set, the default value of 5000 milliseconds is used.
   */
  @Bean
  public int delayAfterDeviceRegistration() {
    final String property =
        this.environment.getProperty(PROPERTY_NAME_IEC61850_DELAY_AFTER_DEVICE_REGISTRATION);
    int milliSeconds;
    if (StringUtils.isEmpty(property)) {
      milliSeconds = 5000;
      LOGGER.info(
          DEFAULT_PROPERTY_MESSAGE,
          milliSeconds,
          PROPERTY_NAME_IEC61850_DELAY_AFTER_DEVICE_REGISTRATION);
    } else {
      milliSeconds = Integer.parseInt(property);
      LOGGER.info(
          PROPERTY_IS_VALUE, PROPERTY_NAME_IEC61850_DELAY_AFTER_DEVICE_REGISTRATION, milliSeconds);
    }

    return milliSeconds;
  }

  /**
   * Used to configure if the reporting is enabled after a device has registered. If this property
   * is not set, the default value of false is used.
   */
  @Bean
  public boolean isReportingAfterDeviceRegistrationEnabled() {
    final String property =
        this.environment.getProperty(
            PROPERTY_NAME_IEC61850_IS_REPORTING_AFTER_DEVICE_REGISTRATION_ENABLED);
    boolean isEnabled;
    if (StringUtils.isEmpty(property)) {
      isEnabled = false;
      LOGGER.info(
          DEFAULT_PROPERTY_MESSAGE,
          isEnabled,
          PROPERTY_NAME_IEC61850_IS_REPORTING_AFTER_DEVICE_REGISTRATION_ENABLED);
    } else {
      isEnabled = Boolean.parseBoolean(property);
      LOGGER.info(
          PROPERTY_IS_VALUE,
          PROPERTY_NAME_IEC61850_IS_REPORTING_AFTER_DEVICE_REGISTRATION_ENABLED,
          isEnabled);
    }
    return isEnabled;
  }

  /**
   * Used to configure how long (in milliseconds) the connection is kept open to allow the device to
   * send reports. If this property is not set, the default value of 5000 milliseconds is used.
   */
  @Bean
  public int disconnectDelay() {
    final String property = this.environment.getProperty(PROPERTY_NAME_IEC61850_DISCONNECT_DELAY);
    int milliSeconds;
    if (StringUtils.isEmpty(property)) {
      milliSeconds = 5000;
      LOGGER.info(DEFAULT_PROPERTY_MESSAGE, milliSeconds, PROPERTY_NAME_IEC61850_DISCONNECT_DELAY);
    } else {
      milliSeconds = Integer.parseInt(property);
      LOGGER.info(PROPERTY_IS_VALUE, PROPERTY_NAME_IEC61850_DISCONNECT_DELAY, milliSeconds);
    }
    return milliSeconds;
  }

  @Bean
  public boolean isIcdFileUsed() {
    return Boolean.parseBoolean(
        this.environment.getRequiredProperty(PROPERTY_NAME_IEC61850_ICD_FILE_USE));
  }

  /** File path for SCL / ICD file which describes the ServerModel of an IED. */
  @Bean
  public String icdFilePath() {
    if (this.isIcdFileUsed()) {
      final String filePath =
          this.environment.getRequiredProperty(PROPERTY_NAME_IEC61850_ICD_FILE_PATH);
      LOGGER.info("Using ICD file with file path: {}", filePath);
      return filePath;
    }
    return null;
  }

  /**
   * File path for a directory containing SCL / ICD files which describe the ServerModel of an IED.
   *
   * <p>The file name for files in this directory is configured for devices it applies to in the
   * IEC61850 protocol database (see {@link Iec61850Device#getIcdFilename()}).
   */
  @Bean
  public String icdFilesFolder() {
    final String filesFolder =
        this.environment.getProperty(PROPERTY_NAME_IEC61850_ICD_FILES_FOLDER);
    LOGGER.info("Using ICD files folder: {}", filesFolder);
    return filesFolder;
  }

  @Bean
  public Boolean isBufferedReportingEnabled() {
    final Boolean isBufferedReportingEnabled =
        Boolean.parseBoolean(
            this.environment.getRequiredProperty(
                PROPERTY_NAME_IEC61850_IS_BUFFERED_REPORTING_ENABLED));
    LOGGER.info(
        "{} = {}",
        PROPERTY_NAME_IEC61850_IS_BUFFERED_REPORTING_ENABLED,
        isBufferedReportingEnabled);
    return isBufferedReportingEnabled;
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
