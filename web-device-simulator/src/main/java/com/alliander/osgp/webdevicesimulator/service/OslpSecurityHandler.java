package com.alliander.osgp.webdevicesimulator.service;

import java.security.PublicKey;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.oslp.OslpEnvelope;

public class OslpSecurityHandler extends SimpleChannelHandler {

    @Autowired
    private PublicKey publicKey;

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent evt) throws Exception {
        final OslpEnvelope message = (OslpEnvelope) evt.getMessage();
        message.validate(this.publicKey);

        ctx.sendUpstream(evt);
    }
}
