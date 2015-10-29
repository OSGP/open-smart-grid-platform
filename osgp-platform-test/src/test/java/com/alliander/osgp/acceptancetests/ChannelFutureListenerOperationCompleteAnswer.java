/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
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
