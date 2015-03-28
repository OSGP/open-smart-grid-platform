package com.alliander.osgp.acceptancetests;

import static org.mockito.Mockito.when;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.alliander.osgp.adapter.protocol.oslp.infra.networking.OslpChannelHandler;
import com.alliander.osgp.oslp.OslpEnvelope;

public class OslpResponseAnswer implements Answer<ChannelFuture> {

    private final OslpChannelHandler oslpChannelHandler;
    private final OslpEnvelope oslpResponse;
    private final Channel channel;

    public OslpResponseAnswer(final OslpChannelHandler oslpChannelHandler, final OslpEnvelope oslpResponse,
            final Channel channel) {
        this.oslpChannelHandler = oslpChannelHandler;
        this.oslpResponse = oslpResponse;
        this.channel = channel;
    }

    @Override
    public ChannelFuture answer(final InvocationOnMock invocation) throws Throwable {
        final ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        final MessageEvent messageEvent = Mockito.mock(MessageEvent.class);
        when(messageEvent.getMessage()).thenReturn(this.oslpResponse);
        when(messageEvent.getChannel()).thenReturn(this.channel);

        this.oslpChannelHandler.messageReceived(channelHandlerContext, messageEvent);

        return Mockito.mock(ChannelFuture.class);
    }
}
