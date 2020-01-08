/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.config;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Provider;

import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.threads.RecoverKeyProcess;
import org.opensmartgridplatform.adapter.protocol.dlms.application.threads.RecoverKeyProcessInitiator;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsDeviceAssociation;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.Hls5Connector;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.Lls0Connector;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.Lls1Connector;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.networking.DlmsChannelHandlerServer;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.networking.DlmsPushNotificationDecoder;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.infra.networking.DisposableNioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0
 */
@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:osgp-adapter-protocol-dlms.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolDlms/config}", ignoreResourceNotFound = true)
public class DlmsConfig extends AbstractConfig {
    private static final String PROPERTY_NAME_DLMS_PORT_SERVER = "dlms.port.server";

    private static final Logger LOGGER = LoggerFactory.getLogger(DlmsConfig.class);

    @Bean(name = "protocolAdapterDlmsNettyServerBossGroup")
    public DisposableNioEventLoopGroup serverBossGroup() {
        return new DisposableNioEventLoopGroup();
    }

    @Bean(name = "protocolAdapterDlmsNettyServerWorkerGroup")
    public DisposableNioEventLoopGroup serverWorkerGroup() {
        return new DisposableNioEventLoopGroup();
    }

    /**
     * Returns a ServerBootstrap setting up a server pipeline listening for
     * incoming DLMS alarm notifications.
     *
     * @return a DLMS alarm server bootstrap.
     */
    @Bean()
    public ServerBootstrap serverBootstrap() {
        LOGGER.info("Initializing serverBootstrap bean.");

        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(this.serverBossGroup(), this.serverWorkerGroup());
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(final SocketChannel ch) throws Exception {
                DlmsConfig.this.createChannelPipeline(ch, DlmsConfig.this.dlmsChannelHandlerServer());
                LOGGER.info("Created new DLMS handler pipeline for server");
            }
        });

        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, false);

        bootstrap.bind(new InetSocketAddress(this.dlmsPortServer()));

        return bootstrap;
    }

    private ChannelPipeline createChannelPipeline(final SocketChannel channel, final ChannelHandler handler) {
        final ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast("loggingHandler", new LoggingHandler(LogLevel.INFO));
        pipeline.addLast("dlmsPushNotificationDecoder", new DlmsPushNotificationDecoder());
        pipeline.addLast("dlmsChannelHandler", handler);

        return pipeline;
    }

    /**
     * Returns the port the DLMS server is listening on.
     *
     * @return the port number of the DLMS server endpoint.
     */
    @Bean
    public int dlmsPortServer() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_DLMS_PORT_SERVER));
    }

    /**
     * @return a new {@link DlmsChannelHandlerServer}.
     */
    @Bean
    public DlmsChannelHandlerServer dlmsChannelHandlerServer() {
        return new DlmsChannelHandlerServer();
    }

    @Bean
    public Hls5Connector hls5Connector(final RecoverKeyProcessInitiator recoverKeyProcessInitiator,
            @Value("${jdlms.response_timeout}") final int responseTimeout,
            @Value("${jdlms.logical_device_address}") final int logicalDeviceAddress) {
        return new Hls5Connector(recoverKeyProcessInitiator, responseTimeout, logicalDeviceAddress,
                DlmsDeviceAssociation.MANAGEMENT_CLIENT);
    }

    @Bean
    public Lls1Connector lls1Connector(@Value("${jdlms.lls1.response.timeout}") final int responseTimeout,
            @Value("${jdlms.logical_device_address}") final int logicalDeviceAddress) {
        return new Lls1Connector(responseTimeout, logicalDeviceAddress, DlmsDeviceAssociation.MANAGEMENT_CLIENT);
    }

    @Bean
    public Lls0Connector lls0Connector(@Value("${jdlms.response_timeout}") final int responseTimeout,
            @Value("${jdlms.logical_device_address}") final int logicalDeviceAddress) {
        return new Lls0Connector(responseTimeout, logicalDeviceAddress, DlmsDeviceAssociation.PUBLIC_CLIENT);
    }

    @Bean
    @Scope("prototype")
    public RecoverKeyProcess recoverKeyProcess(final DomainHelperService domainHelperService,
            final DlmsDeviceRepository dlmsDeviceRepository,
            @Value("${jdlms.response_timeout}") final int responseTimeout,
            @Value("${jdlms.logical_device_address}") final int logicalDeviceAddress) {
        return new RecoverKeyProcess(domainHelperService, dlmsDeviceRepository, responseTimeout, logicalDeviceAddress,
                DlmsDeviceAssociation.MANAGEMENT_CLIENT);
    }

    @Bean
    public RecoverKeyProcessInitiator recoverKeyProcesInitiator(
            @Qualifier("protocolAdapterDlmsScheduleExecutorService") final ScheduledExecutorService executorService,
            final Provider<RecoverKeyProcess> recoverKeyProcessProvider,
            @Value("${key.recovery.delay}") final int recoverKeyDelay) {
        return new RecoverKeyProcessInitiator(executorService, recoverKeyProcessProvider, recoverKeyDelay);
    }

    @Bean(name = "protocolAdapterDlmsScheduleExecutorService")
    public ScheduledExecutorService
            scheduledExecutorService(@Value("${executor.scheduled.poolsize}") final int poolsize) {
        return Executors.newScheduledThreadPool(poolsize);
    }
}
