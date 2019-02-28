/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.config;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

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
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.Iec60870ChannelHandlerServer;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.exceptionhandling.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:osgp-adapter-protocol-iec60870.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolIec60870/config}", ignoreResourceNotFound = true)
public class Iec60870Config extends AbstractConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870Config.class);

    private static final String PROPERTY_NAME_CONNECTION_RESPONSE_TIMEOUT = "connection.response.timeout";
    private static final String PROPERTY_NAME_IEC60870_TIMEOUT_CONNECT = "iec60870.timeout.connect";
    private static final String PROPERTY_NAME_IEC60870_PORT_LISTENER = "iec60870.port.listener";

    public Iec60870Config() {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

    /**
     * The amount of time, in milliseconds, the library will wait for a response
     * after sending a request.
     */
    @Bean
    public int responseTimeout() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_CONNECTION_RESPONSE_TIMEOUT));
    }

    @Bean
    public int connectionTimeout() {
        return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_IEC60870_TIMEOUT_CONNECT));
    }

    /**
     * Returns a ServerBootstrap setting up a server pipeline listening for
     * incoming IEC60870 device requests.
     *
     * @return an IEC60870 server bootstrap.
     */
    @Bean(destroyMethod = "releaseExternalResources")
    public ServerBootstrap serverBootstrap() {
        final ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        final ServerBootstrap bootstrap = new ServerBootstrap(factory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws ProtocolAdapterException {
                final ChannelPipeline pipeline = Iec60870Config.this
                        .createChannelPipeline(Iec60870Config.this.iec60870ChannelHandlerServer());

                LOGGER.info("Created new IEC60870 handler pipeline for server");

                return pipeline;
            }
        });

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", false);

        bootstrap.bind(new InetSocketAddress(this.iec60870PortListener()));

        return bootstrap;
    }

    private ChannelPipeline createChannelPipeline(final ChannelHandler handler) {
        final ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast("loggingHandler", new LoggingHandler(InternalLogLevel.INFO, true));

        pipeline.addLast("iec60870ChannelHandler", handler);

        return pipeline;
    }

    /**
     * Returns the port the server is listening on for incoming IEC60870
     * requests.
     *
     * @return the port number of the IEC60870 listener endpoint.
     */
    @Bean
    public int iec60870PortListener() {
        return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_IEC60870_PORT_LISTENER));
    }

    /**
     * @return a new {@link Iec60870ChannelHandlerServer}.
     */
    @Bean
    public Iec60870ChannelHandlerServer iec60870ChannelHandlerServer() {
        return new Iec60870ChannelHandlerServer();
    }

}
