package com.alliander.osgp.acceptancetests;

import static org.mockito.Mockito.when;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class SimpleChannelHandlerAnswer implements Answer<Boolean> {

    private final SimpleChannelHandler simpleChannelHandler;

    private final Channel channel;

    public SimpleChannelHandlerAnswer(final SimpleChannelHandler simpleChannelHandler, final Channel channel) {
        this.simpleChannelHandler = simpleChannelHandler;
        this.channel = channel;
    }

    @Override
    public Boolean answer(final InvocationOnMock invocation) throws Throwable {
        final ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);

        final ChannelStateEvent channelStateEvent = Mockito.mock(ChannelStateEvent.class);
        when(channelStateEvent.getChannel()).thenReturn(this.channel);
        when(this.channel.isConnected()).thenReturn(true);

        this.simpleChannelHandler.channelOpen(channelHandlerContext, channelStateEvent);

        return true;
    }
}
