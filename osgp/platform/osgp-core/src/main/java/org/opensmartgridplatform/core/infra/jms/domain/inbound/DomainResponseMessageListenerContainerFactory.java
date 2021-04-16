/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.domain.inbound;

import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLException;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.opensmartgridplatform.core.domain.model.protocol.ProtocolResponseService;
import org.opensmartgridplatform.core.infra.jms.ConnectionFactoryRegistry;
import org.opensmartgridplatform.core.infra.jms.MessageListenerContainerRegistry;
import org.opensmartgridplatform.core.infra.jms.domain.DefaultDomainJmsConfiguration;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

public class DomainResponseMessageListenerContainerFactory
    implements InitializingBean, DisposableBean {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DomainResponseMessageListenerContainerFactory.class);

  @Autowired private ProtocolResponseService protocolResponseService;

  @Autowired private DefaultDomainJmsConfiguration defaultDomainJmsConfiguration;

  private Environment environment;
  private final List<DomainInfo> domainInfos;
  private List<ProtocolInfo> protocolInfos;

  private ConnectionFactoryRegistry connectionFactoryRegistry = new ConnectionFactoryRegistry();
  private MessageListenerContainerRegistry messageListenerRegistry =
      new MessageListenerContainerRegistry();

  public DomainResponseMessageListenerContainerFactory(
      final Environment environment,
      final List<DomainInfo> domainInfos,
      final List<ProtocolInfo> protocolInfos) {
    this.environment = environment;
    this.domainInfos = new ArrayList<>(domainInfos);
    this.protocolInfos = new ArrayList<>(protocolInfos);
  }

  public DefaultMessageListenerContainer getMessageListenerContainer(final String key) {
    return this.messageListenerRegistry.getValue(key);
  }

  @Override
  public void afterPropertiesSet() throws SSLException {
    for (final DomainInfo domainInfo : this.domainInfos) {
      LOGGER.info("Initializing DomainResponseMessageListenerContainer {}", domainInfo.getKey());

      this.init(domainInfo);
    }
  }

  private void init(final DomainInfo domainInfo) throws SSLException {
    final JmsConfigurationFactory jmsConfigurationFactory =
        new JmsConfigurationFactory(
            this.environment,
            this.defaultDomainJmsConfiguration,
            domainInfo.getIncomingResponsesPropertyPrefix());

    final PooledConnectionFactory connectionFactory =
        jmsConfigurationFactory.getPooledConnectionFactory();
    this.connectionFactoryRegistry.register(domainInfo.getKey(), connectionFactory);
    connectionFactory.start();

    final DomainResponseMessageListener messageListener =
        new DomainResponseMessageListener(this.protocolResponseService, this.protocolInfos);
    final DefaultMessageListenerContainer messageListenerContainer =
        jmsConfigurationFactory.initMessageListenerContainer(messageListener);
    this.messageListenerRegistry.register(domainInfo.getKey(), messageListenerContainer);
    messageListenerContainer.afterPropertiesSet();
    messageListenerContainer.start();
  }

  @Override
  public void destroy() {
    this.messageListenerRegistry.destroy();
    this.connectionFactoryRegistry.destroy();
  }
}
