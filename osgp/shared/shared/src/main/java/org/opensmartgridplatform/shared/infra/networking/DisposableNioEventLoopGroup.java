/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.infra.networking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;

public class DisposableNioEventLoopGroup extends NioEventLoopGroup implements BeanNameAware, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisposableNioEventLoopGroup.class);

    private String name = "disposableNioEventLoopGroup";

    public DisposableNioEventLoopGroup() {
        super();
    }

    @Override
    public void destroy() {
        LOGGER.info("Destroying DisposableNioEventLoopGroup Bean: {}", this.name);
        final Future<?> f = this.shutdownGracefully();
        try {
            f.await();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("InterruptedException", e);
        }
    }

    @Override
    public void setBeanName(final String name) {
        this.name = name;
    }
}
