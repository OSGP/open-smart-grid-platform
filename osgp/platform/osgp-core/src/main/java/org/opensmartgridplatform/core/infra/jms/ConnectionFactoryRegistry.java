/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms;

import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class ConnectionFactoryRegistry extends Registry<PooledConnectionFactory>
    implements DisposableBean {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionFactoryRegistry.class);

  @Override
  protected void preUnregisterAll() {
    this.getValues().forEach(PooledConnectionFactory::stop);
  }

  @Override
  protected void preUnregister(final String key) {
    final PooledConnectionFactory connectionFactory = this.getValue(key);
    if (connectionFactory != null) {
      LOGGER.info("Stopping ConnectionFactory {}", key);
      connectionFactory.stop();
    }
  }

  @Override
  public void destroy() {
    this.unregisterAll();
  }
}
