/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking;

import java.net.InetAddress;
import java.util.UUID;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.OsgpRequestMessageSender;
import com.alliander.osgp.core.db.api.iec61850.entities.Ssld;
import com.alliander.osgp.dto.valueobjects.DeviceFunction;
import com.alliander.osgp.dto.valueobjects.DeviceRegistrationData;
import com.alliander.osgp.iec61850.RegisterDeviceRequest;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

public class Iec61850ChannelHandlerServer extends Iec61850ChannelHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850ChannelHandlerServer.class);

    @Autowired
    private OsgpRequestMessageSender osgpRequestMessageSender;

    @Autowired
    private Iec61850Client iec61850Client;

    public Iec61850ChannelHandlerServer() {
        super(LOGGER);
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {

        final RegisterDeviceRequest message = (RegisterDeviceRequest) e.getMessage();

        final String correlationId = UUID.randomUUID().toString().replace("-", "");

        this.processRegistrationMessage(message, correlationId);
    }

    private void processRegistrationMessage(final RegisterDeviceRequest message, final String correlationId) {

        this.logMessage(message);

        final String deviceIdentification = message.getDeviceIdentification();
        final String deviceType = Ssld.SSLD_TYPE;
        final String ipAddress = message.getIpAddress();

        final DeviceRegistrationData deviceRegistrationData = new DeviceRegistrationData(ipAddress, deviceType, true);

        final RequestMessage requestMessage = new RequestMessage(correlationId, "no-organisation",
                deviceIdentification, ipAddress, deviceRegistrationData);

        LOGGER.info("Sending register device request to OSGP with correlation ID: " + correlationId);
        this.osgpRequestMessageSender.send(requestMessage, DeviceFunction.REGISTER_DEVICE.name());

        try {
            this.iec61850Client.disableRegistration(deviceIdentification, InetAddress.getByName(ipAddress));
            LOGGER.info("Disabled registration for device: {}, at IP address: {}", deviceIdentification, ipAddress);
        } catch (final Exception e) {
            LOGGER.error("Failed to disable registration for device: {}, at IP address: {}", deviceIdentification,
                    ipAddress, e);
        }
    }
}
