/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.mocks.oslpdevice;

import java.security.PublicKey;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.alliander.osgp.oslp.OslpEnvelope;

public class OslpSecurityHandler extends SimpleChannelHandler {

    private PublicKey publicKey;

    public OslpSecurityHandler(final PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent evt) throws Exception {
        final OslpEnvelope message = (OslpEnvelope) evt.getMessage();
        message.validate(this.publicKey);

        ctx.sendUpstream(evt);
    }
}
