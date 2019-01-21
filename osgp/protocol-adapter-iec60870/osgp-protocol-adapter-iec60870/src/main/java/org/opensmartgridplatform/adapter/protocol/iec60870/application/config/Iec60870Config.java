/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.config;

import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement()
@PropertySources({ @PropertySource("classpath:osgp-adapter-protocol-iec60870.properties"),
        @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
        @PropertySource(value = "file:${osgp/AdapterProtocolIec60870/config}", ignoreResourceNotFound = true), })
public class Iec60870Config extends AbstractConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870Config.class);

    private static final String PROPERTY_NAME_IEC60870_TIMEOUT_CONNECT = "iec60870.timeout.connect";
    private static final String PROPERTY_NAME_IEC60870_PORT_CLIENT = "iec60870.port.client";
    private static final String PROPERTY_NAME_IEC60870_PORT_CLIENTLOCAL = "iec60870.port.clientlocal";
    private static final String PROPERTY_NAME_IEC60870_SSLD_PORT_SERVER = "iec60870.ssld.port.server";
    private static final String PROPERTY_NAME_IEC60870_RTU_PORT_SERVER = "iec60870.rtu.port.server";
    private static final String PROPERTY_NAME_IEC60870_PORT_LISTENER = "iec60870.port.listener";

    public Iec60870Config() {
        // TODO: Is onderstaande regel nodig? Zo ja, waarvoor? Voor logging door
        // netty?
        // InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

    @Bean
    public int connectionTimeout() {
        return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_IEC60870_TIMEOUT_CONNECT));
    }

    @Bean
    public int iec60870PortClient() {
        return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_IEC60870_PORT_CLIENT));
    }

    @Bean
    public int iec60870PortClientLocal() {
        return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_IEC60870_PORT_CLIENTLOCAL));
    }

    @Bean
    public int iec60870SsldPortServer() {
        return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_IEC60870_SSLD_PORT_SERVER));
    }

    @Bean
    public int iec60870RtuPortServer() {
        return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_IEC60870_RTU_PORT_SERVER));
    }

    /**
     * Returns a ServerBootstrap setting up a server pipeline listening for
     * incoming IEC60870 register device requests.
     *
     * @return an IEC60870 server bootstrap.
     */
    /* @formatter:off
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

        pipeline.addLast("iec60870RegisterDeviceRequestDecoder", new RegisterDeviceRequestDecoder());

        pipeline.addLast("iec60870ChannelHandler", handler);

        return pipeline;
    }
   @formatter:on
*/

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
    /* @formatter:off
    @Bean
    public Iec60870ChannelHandlerServer iec60870ChannelHandlerServer() {
        return new Iec60870ChannelHandlerServer();
    }
    @formatter:on
    */

}
