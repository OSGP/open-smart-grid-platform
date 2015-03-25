package com.alliander.osgp.acceptancetests;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ChannelFutureListenerOperationCompleteAnswer implements Answer<ChannelFutureListener> {

    private final ChannelFuture channelFuture;
    private final ArgumentCaptor<ChannelFutureListener> argumentCaptor;

    public ChannelFutureListenerOperationCompleteAnswer(final ChannelFuture channelFuture, final ArgumentCaptor<ChannelFutureListener> argumentCaptor) {
        this.channelFuture = channelFuture;
        this.argumentCaptor = argumentCaptor;
    }

    @Override
    public ChannelFutureListener answer(final InvocationOnMock invocation) throws Throwable {

        this.argumentCaptor.getValue().operationComplete(this.channelFuture);

        return null;
    }
}
