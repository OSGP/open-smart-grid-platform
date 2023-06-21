// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.infra.jms.domain.inbound;

import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLException;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.opensmartgridplatform.core.application.services.DeviceRequestMessageService;
import org.opensmartgridplatform.core.infra.jms.ConnectionFactoryRegistry;
import org.opensmartgridplatform.core.infra.jms.MessageListenerContainerRegistry;
import org.opensmartgridplatform.core.infra.jms.domain.DefaultDomainJmsConfiguration;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.repositories.ScheduledTaskRepository;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

public class DomainRequestMessageListenerContainerFactory
    implements InitializingBean, DisposableBean {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DomainRequestMessageListenerContainerFactory.class);

  @Autowired private DeviceRequestMessageService deviceRequestMessageService;

  @Autowired private ScheduledTaskRepository scheduledTaskRepository;

  @Autowired private DefaultDomainJmsConfiguration defaultDomainJmsConfiguration;

  private final Environment environment;
  private final List<DomainInfo> domainInfos;

  private final ConnectionFactoryRegistry connectionFactoryRegistry =
      new ConnectionFactoryRegistry();
  private final MessageListenerContainerRegistry messageListenerContainerRegistry =
      new MessageListenerContainerRegistry();

  public DomainRequestMessageListenerContainerFactory(
      final Environment environment, final List<DomainInfo> domainInfos) {
    this.environment = environment;
    this.domainInfos = new ArrayList<>(domainInfos);
  }

  public DefaultMessageListenerContainer getMessageListenerContainer(final String key) {
    return this.messageListenerContainerRegistry.getValue(key);
  }

  @Override
  public void afterPropertiesSet() throws SSLException {
    for (final DomainInfo domainInfo : this.domainInfos) {
      LOGGER.info("Initializing DomainRequestMessageListenerContainer {}", domainInfo.getKey());

      this.init(domainInfo);
    }
  }

  private void init(final DomainInfo domainInfo) throws SSLException {
    final JmsConfigurationFactory jmsConfigurationFactory =
        new JmsConfigurationFactory(
            this.environment,
            this.defaultDomainJmsConfiguration,
            domainInfo.getIncomingRequestsPropertyPrefix());

    final PooledConnectionFactory connectionFactory =
        jmsConfigurationFactory.getPooledConnectionFactory();
    this.connectionFactoryRegistry.register(domainInfo.getKey(), connectionFactory);
    connectionFactory.start();

    final DomainRequestMessageListener messageListener =
        new DomainRequestMessageListener(
            domainInfo, this.deviceRequestMessageService, this.scheduledTaskRepository);
    final DefaultMessageListenerContainer messageListenerContainer =
        jmsConfigurationFactory.initMessageListenerContainer(messageListener);
    this.messageListenerContainerRegistry.register(domainInfo.getKey(), messageListenerContainer);
    messageListenerContainer.afterPropertiesSet();
    messageListenerContainer.start();
  }

  @Override
  public void destroy() {
    this.messageListenerContainerRegistry.destroy();
    this.connectionFactoryRegistry.destroy();
  }
}
