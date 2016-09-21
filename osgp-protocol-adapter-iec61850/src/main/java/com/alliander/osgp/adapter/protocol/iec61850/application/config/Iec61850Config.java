/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.application.config;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.logging.InternalLogLevel;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850ChannelHandlerServer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.RegisterDeviceRequestDecoder;

@Configuration
@EnableTransactionManagement()
@PropertySource("file:${osp/osgpAdapterProtocolIec61850/config}")
public class Iec61850Config {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850Config.class);

    private static final String PROPERTY_NAME_IEC61850_TIMEOUT_CONNECT = "iec61850.timeout.connect";
    private static final String PROPERTY_NAME_IEC61850_PORT_CLIENT = "iec61850.port.client";
    private static final String PROPERTY_NAME_IEC61850_PORT_CLIENTLOCAL = "iec61850.port.clientlocal";
    private static final String PROPERTY_NAME_IEC61850_SSLD_PORT_SERVER = "iec61850.ssld.port.server";
    private static final String PROPERTY_NAME_IEC61850_RTU_PORT_SERVER = "iec61850.rtu.port.server";
    private static final String PROPERTY_NAME_IEC61850_PORT_LISTENER = "iec61850.port.listener";

    @Resource
    private Environment environment;

    public Iec61850Config() {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

    @Bean
    public int connectionTimeout() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_IEC61850_TIMEOUT_CONNECT));
    }

    @Bean
    public int iec61850PortClient() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_IEC61850_PORT_CLIENT));
    }

    @Bean
    public int iec61850PortClientLocal() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_IEC61850_PORT_CLIENTLOCAL));
    }

    @Bean
    public int iec61850SsldPortServer() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_IEC61850_SSLD_PORT_SERVER));
    }

    @Bean
    public int iec61850RtuPortServer() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_IEC61850_RTU_PORT_SERVER));
    }

    /**
     * Returns a ServerBootstrap setting up a server pipeline listening for
     * incoming IEC61850 register device requests.
     *
     * @return an IEC61850 server bootstrap.
     */
    @Bean(destroyMethod = "releaseExternalResources")
    public ServerBootstrap serverBootstrap() {
        final ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        final ServerBootstrap bootstrap = new ServerBootstrap(factory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws ProtocolAdapterException {
                final ChannelPipeline pipeline = Iec61850Config.this.createChannelPipeline(Iec61850Config.this
                        .iec61850ChannelHandlerServer());

                LOGGER.info("Created new IEC61850 handler pipeline for server");

                return pipeline;
            }
        });

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", false);

        bootstrap.bind(new InetSocketAddress(this.iec61850PortListener()));

        return bootstrap;
    }

    private ChannelPipeline createChannelPipeline(final ChannelHandler handler) throws ProtocolAdapterException {
        final ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast("loggingHandler", new LoggingHandler(InternalLogLevel.INFO, true));

        pipeline.addLast("iec61850RegisterDeviceRequestDecoder", new RegisterDeviceRequestDecoder());

        pipeline.addLast("iec61850ChannelHandler", handler);

        return pipeline;
    }

    /**
     * Returns the port the server is listening on for incoming IEC61850
     * requests.
     *
     * @return the port number of the IEC61850 listener endpoint.
     */
    @Bean
    public int iec61850PortListener() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_IEC61850_PORT_LISTENER));
    }

    /**
     * @return a new {@link Iec61850ChannelHandlerServer}.
     */
    @Bean
    public Iec61850ChannelHandlerServer iec61850ChannelHandlerServer() {
        return new Iec61850ChannelHandlerServer();
    }

    @Bean
    public String testDeviceId() {
        final String testDeviceId = this.environment.getProperty("test.device.id");
        LOGGER.info("testDeviceId: {}", testDeviceId);
        return testDeviceId;
    }

    @Bean
    public String testDeviceIp() {
        final String testDeviceIp = this.environment.getProperty("test.device.ip");
        LOGGER.info("testDeviceIp: {}", testDeviceIp);
        return testDeviceIp;
    }
}
