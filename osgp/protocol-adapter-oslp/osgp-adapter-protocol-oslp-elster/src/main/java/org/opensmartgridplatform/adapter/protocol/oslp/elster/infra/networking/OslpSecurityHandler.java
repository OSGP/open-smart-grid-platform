/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking;

import java.security.PublicKey;

import org.apache.commons.codec.binary.Base64;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.application.services.oslp.OslpDeviceSettingsService;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.domain.entities.OslpDevice;
import org.opensmartgridplatform.oslp.OslpEnvelope;
import org.opensmartgridplatform.shared.security.CertificateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
public class OslpSecurityHandler extends SimpleChannelInboundHandler<OslpEnvelope> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OslpSecurityHandler.class);

    @Autowired
    private String oslpSignatureProvider;

    @Autowired
    private String oslpKeyType;

    @Autowired
    private OslpDeviceSettingsService oslpDeviceSettingsService;

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final OslpEnvelope message) throws Exception {

        LOGGER.info("Entering method: channelRead0 for channel {}", ctx.channel().id().asLongText());

        // Upon first registration, a deviceUid is unknown within the platform.
        // Search based on deviceIdentification in this case.
        OslpDevice oslpDevice;

        if (message.getPayloadMessage().hasRegisterDeviceRequest()) {
            final String deviceIdentification = message.getPayloadMessage()
                    .getRegisterDeviceRequest()
                    .getDeviceIdentification();

            oslpDevice = this.oslpDeviceSettingsService.getDeviceByDeviceIdentification(deviceIdentification);
        } else {
            oslpDevice = this.oslpDeviceSettingsService
                    .getDeviceByUid(Base64.encodeBase64String(message.getDeviceId()));
        }

        if (oslpDevice == null) {
            LOGGER.warn("Received message from unknown device.");
        } else if (oslpDevice.getPublicKey() == null) {
            LOGGER.warn("Received message from device without public key: {}", oslpDevice.getDeviceIdentification());
        }

        // When device is unknown or publickey is not available, the message is
        // not valid.
        if (oslpDevice != null && oslpDevice.getPublicKey() != null) {
            final PublicKey publicKey = CertificateHelper.createPublicKeyFromBase64(oslpDevice.getPublicKey(),
                    this.oslpKeyType, this.oslpSignatureProvider);

            message.validate(publicKey);
        }
        ctx.fireChannelRead(message);
    }
}
