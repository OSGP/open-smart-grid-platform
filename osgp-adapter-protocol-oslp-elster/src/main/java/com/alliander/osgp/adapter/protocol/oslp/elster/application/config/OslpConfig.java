/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.elster.application.config;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.logging.InternalLogLevel;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alliander.osgp.adapter.protocol.oslp.elster.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.oslp.elster.infra.networking.OslpChannelHandlerClient;
import com.alliander.osgp.adapter.protocol.oslp.elster.infra.networking.OslpChannelHandlerServer;
import com.alliander.osgp.adapter.protocol.oslp.elster.infra.networking.OslpSecurityHandler;
import com.alliander.osgp.oslp.OslpDecoder;
import com.alliander.osgp.oslp.OslpEncoder;
import com.alliander.osgp.oslp.OslpUtils;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0
 */
@Configuration
@EnableTransactionManagement()
@PropertySource("file:${osp/osgpAdapterProtocolOslpElster/config}")
public class OslpConfig {
    private static final String PROPERTY_NAME_OSLP_TIMEOUT_CONNECT = "oslp.timeout.connect";
    private static final String PROPERTY_NAME_OSLP_PORT_CLIENT = "oslp.port.client";
    private static final String PROPERTY_NAME_OSLP_PORT_CLIENTLOCAL = "oslp.port.clientlocal";

    private static final String PROPERTY_NAME_OSLP_PORT_SERVER = "oslp.port.server";

    private static final String PROPERTY_NAME_OSLP_SECURITY_KEYTYPE = "oslp.security.keytype";
    private static final String PROPERTY_NAME_OSLP_SECURITY_SIGNATURE = "oslp.security.signature";
    private static final String PROPERTY_NAME_OSLP_SECURITY_PROVIDER = "oslp.security.provider";
    private static final String PROPERTY_NAME_OSLP_SEQUENCE_NUMBER_WINDOW = "oslp.sequence.number.window";
    private static final String PROPERTY_NAME_OSLP_SEQUENCE_NUMBER_MAXIMUM = "oslp.sequence.number.maximum";

    private static final String PROPERTY_NAME_OSLP_EXECUTE_RESUME_SCHEDULE_AFTER_SET_LIGHT = "oslp.execute.resume.schedule.after.set.light";
    private static final String PROPERTY_NAME_OSLP_DELAY_BEFORE_EXECUTE_RESUME_SCHEDULE_AFTER_SET_LIGHT = "oslp.delay.before.execute.resume.schedule.after.set.light";

    private static final Logger LOGGER = LoggerFactory.getLogger(OslpConfig.class);

    @Resource
    private Environment environment;

    public OslpConfig() {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

    @Bean(destroyMethod = "releaseExternalResources")
    public ClientBootstrap clientBootstrap() {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
        final ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        final ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws ProtocolAdapterException {
                final ChannelPipeline pipeline = OslpConfig.this.createChannelPipeline(OslpConfig.this
                        .oslpChannelHandlerClient());

                LOGGER.info("Created client new pipeline");

                return pipeline;
            }
        };

        final ClientBootstrap bootstrap = new ClientBootstrap(factory);

        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", false);
        bootstrap.setOption("connectTimeoutMillis", this.connectionTimeout());

        bootstrap.setPipelineFactory(pipelineFactory);

        return bootstrap;
    }

    @Bean(destroyMethod = "releaseExternalResources")
    public ServerBootstrap serverBootstrap() {
        final ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        final ServerBootstrap bootstrap = new ServerBootstrap(factory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws ProtocolAdapterException {
                final ChannelPipeline pipeline = OslpConfig.this.createChannelPipeline(OslpConfig.this
                        .oslpChannelHandlerServer());

                LOGGER.info("Created server new pipeline");

                return pipeline;
            }
        });

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", false);

        bootstrap.bind(new InetSocketAddress(this.oslpPortServer()));

        return bootstrap;
    }

    private ChannelPipeline createChannelPipeline(final ChannelHandler handler) throws ProtocolAdapterException {
        final ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast("loggingHandler", new LoggingHandler(InternalLogLevel.INFO, false));

        pipeline.addLast("oslpEncoder", new OslpEncoder());
        pipeline.addLast("oslpDecoder", new OslpDecoder(this.oslpSignature(), this.oslpSignatureProvider()));
        pipeline.addLast("oslpSecurity", this.oslpSecurityHandler());

        pipeline.addLast("oslpChannelHandler", handler);

        return pipeline;
    }

    @Bean
    public OslpSecurityHandler oslpSecurityHandler() {
        return new OslpSecurityHandler();
    }

    @Bean
    public OslpDecoder oslpDecoder() throws ProtocolAdapterException {
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
        return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_TIMEOUT_CONNECT));
    }

    @Bean
    public int oslpPortClient() {
        return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_PORT_CLIENT));
    }

    @Bean
    public int oslpPortClientLocal() {
        return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_PORT_CLIENTLOCAL));
    }

    @Bean
    public int oslpPortServer() {
        return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_PORT_SERVER));
    }

    @Bean
    public OslpChannelHandlerServer oslpChannelHandlerServer() {
        return new OslpChannelHandlerServer();
    }

    @Bean
    public OslpChannelHandlerClient oslpChannelHandlerClient() {
        return new OslpChannelHandlerClient();
    }

    /**
     * Why is this class instantiated? The class only offers static functions.
     * SonarQube issue: Classes with only "static" methods should not be
     * instantiated squid:S2440
     * http://54.77.62.182/sonarqube/coding_rules#rule_key=squid%3AS2440
     *
     * @return
     */
    @Bean
    public OslpUtils oslpUtils() {
        return new OslpUtils();
    }

    // === Sequence number config ===

    @Bean
    @Qualifier("sequenceNumberWindow")
    public Integer sequenceNumberWindow() {
        return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_SEQUENCE_NUMBER_WINDOW));
    }

    @Bean
    @Qualifier("sequenceNumberMaximum")
    public Integer sequenceNumberMaximum() {
        return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_SEQUENCE_NUMBER_MAXIMUM));
    }

    @Bean
    public boolean executeResumeScheduleAfterSetLight() {
        final String property = this.environment
                .getProperty(PROPERTY_NAME_OSLP_EXECUTE_RESUME_SCHEDULE_AFTER_SET_LIGHT);
        if (property == null) {
            return false;
        }
        try {
            return Boolean.parseBoolean(property);
        } catch (final Exception e) {
            LOGGER.error(
                    "Bean executeResumeScheduleAfterSetLight could not parse the property: {} with value: {}. valid values are 'true' and 'false'",
                    PROPERTY_NAME_OSLP_EXECUTE_RESUME_SCHEDULE_AFTER_SET_LIGHT, property);
            return false;
        }
    }

    @Bean
    public long delayBeforeExecuteResumeScheduleAfterSetLight() {
        final long defaultValue = 0L;
        final String property = this.environment
                .getProperty(PROPERTY_NAME_OSLP_DELAY_BEFORE_EXECUTE_RESUME_SCHEDULE_AFTER_SET_LIGHT);
        if (property == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(property);
        } catch (final Exception e) {
            LOGGER.error(
                    "Bean delayBeforeExecuteResumeScheduleAfterSetLight could not parse the property: {} with value: {}. valid values should be all numbers",
                    PROPERTY_NAME_OSLP_EXECUTE_RESUME_SCHEDULE_AFTER_SET_LIGHT, property);
            return defaultValue;
        }
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
