//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.infra.networking;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;

public class DisposableNioEventLoopGroup extends NioEventLoopGroup
    implements BeanNameAware, DisposableBean {

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
