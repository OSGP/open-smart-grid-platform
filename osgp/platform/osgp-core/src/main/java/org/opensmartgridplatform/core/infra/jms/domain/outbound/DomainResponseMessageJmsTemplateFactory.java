// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.infra.jms.domain.outbound;

import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLException;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.opensmartgridplatform.core.infra.jms.ConnectionFactoryRegistry;
import org.opensmartgridplatform.core.infra.jms.Registry;
import org.opensmartgridplatform.core.infra.jms.domain.DefaultDomainJmsConfiguration;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;

public class DomainResponseMessageJmsTemplateFactory implements InitializingBean, DisposableBean {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DomainResponseMessageJmsTemplateFactory.class);

  private final ConnectionFactoryRegistry connectionFactoryRegistry =
      new ConnectionFactoryRegistry();
  private final Registry<JmsTemplate> jmsTemplateRegistry = new Registry<>();

  @Autowired private DefaultDomainJmsConfiguration defaultDomainJmsConfiguration;

  private final Environment environment;
  private final List<DomainInfo> domainInfos;

  public DomainResponseMessageJmsTemplateFactory(
      final Environment environment, final List<DomainInfo> domainInfos) {
    this.environment = environment;
    this.domainInfos = new ArrayList<>(domainInfos);
  }

  public JmsTemplate getJmsTemplate(final String key) {
    return this.jmsTemplateRegistry.getValue(key);
  }

  @Override
  public void afterPropertiesSet() throws SSLException {

    for (final DomainInfo domainInfo : this.domainInfos) {

      LOGGER.info("Initializing DomainResponseMessageJmsTemplate {}", domainInfo.getKey());
      this.init(domainInfo);
    }
  }

  private void init(final DomainInfo domainInfo) throws SSLException {

    final JmsConfigurationFactory jmsConfigurationFactory =
        new JmsConfigurationFactory(
            this.environment,
            this.defaultDomainJmsConfiguration,
            domainInfo.getOutgoingResponsesPropertyPrefix());

    final PooledConnectionFactory connectionFactory =
        jmsConfigurationFactory.getPooledConnectionFactory();
    this.connectionFactoryRegistry.register(domainInfo.getKey(), connectionFactory);
    connectionFactory.start();

    final JmsTemplate jmsTemplate = jmsConfigurationFactory.initJmsTemplate();
    this.jmsTemplateRegistry.register(domainInfo.getKey(), jmsTemplate);
    jmsTemplate.afterPropertiesSet();
  }

  @Override
  public void destroy() throws Exception {
    this.jmsTemplateRegistry.unregisterAll();
    this.connectionFactoryRegistry.destroy();
  }
}
