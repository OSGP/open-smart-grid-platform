/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking;

import java.util.Map;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class Iec60870ChannelHandlerServer extends Iec60870ChannelHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870ChannelHandlerServer.class);

    /**
     * Convert list in property files to {@code Map}.
     *
     * See the SpEL documentation for more information:
     * https://docs.spring.io/spring/docs/3.0.x/reference/expressions.html
     */
    @Value("#{${test.device.ips}}")
    private Map<String, String> testDeviceIps;

    public Iec60870ChannelHandlerServer() {
        super(LOGGER);
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        LOGGER.info("Handle incoming message");
    }

}
