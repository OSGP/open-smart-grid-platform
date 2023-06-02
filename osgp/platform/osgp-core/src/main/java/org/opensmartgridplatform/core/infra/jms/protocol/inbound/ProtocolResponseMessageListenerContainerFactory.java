//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.core.infra.jms.protocol.inbound;

import java.util.List;
import javax.net.ssl.SSLException;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.opensmartgridplatform.core.application.services.DeviceResponseMessageService;
import org.opensmartgridplatform.core.infra.jms.ConnectionFactoryRegistry;
import org.opensmartgridplatform.core.infra.jms.MessageListenerContainerRegistry;
import org.opensmartgridplatform.core.infra.jms.protocol.DefaultProtocolJmsConfiguration;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

public class ProtocolResponseMessageListenerContainerFactory
    implements InitializingBean, DisposableBean {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ProtocolResponseMessageListenerContainerFactory.class);

  @Autowired private DeviceResponseMessageService deviceResponseMessageService;

  @Autowired private DefaultProtocolJmsConfiguration defaultProtocolJmsConfiguration;

  private final Environment environment;
  private final List<ProtocolInfo> protocolInfos;

  private final ConnectionFactoryRegistry connectionFactoryRegistry =
      new ConnectionFactoryRegistry();
  private final MessageListenerContainerRegistry messageListenerContainerRegistry =
      new MessageListenerContainerRegistry();

  public ProtocolResponseMessageListenerContainerFactory(
      final Environment environment, final List<ProtocolInfo> protocolInfos) {
    this.environment = environment;
    this.protocolInfos = protocolInfos;
  }

  public DefaultMessageListenerContainer getMessageListenerContainer(final String key) {
    return this.messageListenerContainerRegistry.getValue(key);
  }

  @Override
  public void afterPropertiesSet() throws SSLException {
    for (final ProtocolInfo protocolInfo : this.protocolInfos) {
      LOGGER.info(
          "Initializing ProtocolResponseMessageListenerContainer {}", protocolInfo.getKey());

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
            protocolInfo.getIncomingResponsesPropertyPrefix());

    LOGGER.debug("Initializing PooledConnectionFactory for protocol {}", key);
    final PooledConnectionFactory connectionFactory =
        jmsConfigurationFactory.getPooledConnectionFactory();
    this.connectionFactoryRegistry.register(key, connectionFactory);
    connectionFactory.start();

    LOGGER.debug("Initializing MesssageListenerContainer for protocol {}", key);
    final ProtocolResponseMessageListener messageListener =
        new ProtocolResponseMessageListener(this.deviceResponseMessageService);
    final DefaultMessageListenerContainer messageListenerContainer =
        jmsConfigurationFactory.initMessageListenerContainer(messageListener);
    this.messageListenerContainerRegistry.register(key, messageListenerContainer);
    messageListenerContainer.afterPropertiesSet();
    messageListenerContainer.start();
  }

  @Override
  public void destroy() {
    LOGGER.debug("Destroying MessageListenerContainerRegistry");
    this.messageListenerContainerRegistry.destroy();

    LOGGER.debug("Destroying ConnectionFactoryRegistry");
    this.connectionFactoryRegistry.destroy();
  }
}
