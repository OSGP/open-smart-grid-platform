/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.protocol.outbound;

import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLException;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.opensmartgridplatform.core.infra.jms.ConnectionFactoryRegistry;
import org.opensmartgridplatform.core.infra.jms.Registry;
import org.opensmartgridplatform.core.infra.jms.protocol.DefaultProtocolJmsConfiguration;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;

public class ProtocolResponseMessageJmsTemplateFactory implements InitializingBean, DisposableBean {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ProtocolRequestMessageJmsTemplateFactory.class);

  @Autowired private DefaultProtocolJmsConfiguration defaultProtocolJmsConfiguration;

  private Environment environment;
  private List<ProtocolInfo> protocolInfos;

  private ConnectionFactoryRegistry connectionFactoryRegistry = new ConnectionFactoryRegistry();
  private Registry<JmsTemplate> jmsTemplateRegistry = new Registry<>();

  public ProtocolResponseMessageJmsTemplateFactory(
      final Environment environment, final List<ProtocolInfo> protocolInfos) {
    this.environment = environment;
    this.protocolInfos = new ArrayList<>(protocolInfos);
  }

  public JmsTemplate getJmsTemplate(final String key) {
    return this.jmsTemplateRegistry.getValue(key);
  }

  @Override
  public void afterPropertiesSet() throws SSLException {
    for (final ProtocolInfo protocolInfo : this.protocolInfos) {
      LOGGER.info("Initializing ProtocolResponseMessageJmsTemplate {}", protocolInfo.getKey());
      this.init(protocolInfo);
    }
  }

  private void init(final ProtocolInfo protocolInfo) throws SSLException {
    final String key = protocolInfo.getKey();

    LOGGER.debug("Initializing JmsConfigurationFactory for protocol {}", key);
    final JmsConfigurationFactory jmsConfigurationFactory =
        new JmsConfigurationFactory(
            this.environment,
            this.defaultProtocolJmsConfiguration,
            protocolInfo.getOutgoingResponsesPropertyPrefix());

    LOGGER.debug("Initializing PooledConnectionFactory for protocol {}", key);
    final PooledConnectionFactory connectionFactory =
        jmsConfigurationFactory.getPooledConnectionFactory();
    this.connectionFactoryRegistry.register(key, connectionFactory);
    connectionFactory.start();

    LOGGER.debug("Initializing JmsTemplate for protocol {}", key);
    final JmsTemplate jmsTemplate = jmsConfigurationFactory.initJmsTemplate();
    this.jmsTemplateRegistry.register(key, jmsTemplate);
    jmsTemplate.afterPropertiesSet();
  }

  @Override
  public void destroy() throws Exception {
    LOGGER.debug("Unregistering all from JmsTemplateRegistry");
    this.jmsTemplateRegistry.unregisterAll();

    LOGGER.debug("Destroying ConnectionFactoryRegistry");
    this.connectionFactoryRegistry.destroy();
  }
}
