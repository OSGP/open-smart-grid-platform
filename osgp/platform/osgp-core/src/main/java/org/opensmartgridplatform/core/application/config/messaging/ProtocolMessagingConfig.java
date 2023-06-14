// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.application.config.messaging;

import java.util.ArrayList;
import java.util.List;
import org.opensmartgridplatform.core.infra.jms.protocol.DefaultProtocolJmsConfiguration;
import org.opensmartgridplatform.core.infra.jms.protocol.inbound.ProtocolRequestMessageListenerContainerFactory;
import org.opensmartgridplatform.core.infra.jms.protocol.inbound.ProtocolResponseMessageListenerContainerFactory;
import org.opensmartgridplatform.core.infra.jms.protocol.outbound.ProtocolRequestMessageJmsTemplateFactory;
import org.opensmartgridplatform.core.infra.jms.protocol.outbound.ProtocolResponseMessageJmsTemplateFactory;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.domain.core.repositories.ProtocolInfoRepository;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProtocolMessagingConfig extends AbstractConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolMessagingConfig.class);

  private static final int DEFAULT_MESSAGE_GROUP_CACHE_SIZE = 1024;

  @Value("${max.retry.count:3}")
  private int maxRetryCount;

  @Value("${jms.protocol.activemq.messagegroup.cachesize:1024}")
  private int messageGroupCacheSize;

  // JMS Settings: SSL settings for the protocol requests and responses
  @Value("${jms.protocol.activemq.broker.client.key.store:/etc/osp/activemq/client.ks}")
  private String clientKeyStore;

  @Value("${jms.protocol.activemq.broker.client.key.store.pwd:password}")
  private String clientKeyStorePwd;

  @Value("${jms.protocol.activemq.broker.client.trust.store:/etc/osp/activemq/client.ts}")
  private String trustKeyStore;

  @Value("${jms.protocol.activemq.broker.client.trust.store.pwd:password}")
  private String trustKeyStorePwd;

  private List<DomainInfo> domainInfos;
  private List<ProtocolInfo> protocolInfos;

  public ProtocolMessagingConfig(
      final DomainInfoRepository domainInfoRepository,
      final ProtocolInfoRepository protocolInfoRepository) {
    this.domainInfos = new ArrayList<>(domainInfoRepository.findAll());
    this.protocolInfos = new ArrayList<>(protocolInfoRepository.findAll());
  }

  @Bean
  public DefaultProtocolJmsConfiguration defaultProtocolJmsConfiguration() {
    return new DefaultProtocolJmsConfiguration();
  }

  @Bean
  @Qualifier("osgpCoreIncomingProtocolRequestMessageProcessorMap")
  public MessageProcessorMap protocolRequestMessageProcessorMap() {
    return new BaseMessageProcessorMap("ProtocolRequestMessageProcessorMap");
  }

  @Bean
  public Integer messageGroupCacheSize() {
    LOGGER.debug("Creating bean: messageGroupCacheSize");
    if (this.messageGroupCacheSize <= 0) {
      LOGGER.warn(
          "Invalid message group cache size {}, using default value {}",
          this.messageGroupCacheSize,
          DEFAULT_MESSAGE_GROUP_CACHE_SIZE);
      return DEFAULT_MESSAGE_GROUP_CACHE_SIZE;
    } else {
      return this.messageGroupCacheSize;
    }
  }

  // bean used for sending protocol request messages
  // (outbound protocol requests)
  @Bean
  public ProtocolRequestMessageJmsTemplateFactory protocolRequestsJmsTemplate() {
    return new ProtocolRequestMessageJmsTemplateFactory(this.environment, this.protocolInfos);
  }

  // bean used for receiving protocol response messages
  // (inbound protocol responses)
  @Bean
  public ProtocolResponseMessageListenerContainerFactory
      protocolResponseMessageListenerContainer() {
    return new ProtocolResponseMessageListenerContainerFactory(
        this.environment, this.protocolInfos);
  }

  // bean used for receiving incoming protocol request messages
  // (inbound protocol requests)
  @Bean
  public ProtocolRequestMessageListenerContainerFactory protocolRequestMessageListenerContainer(
      @Qualifier("osgpCoreIncomingProtocolRequestMessageProcessorMap")
          final MessageProcessorMap messageProcessorMap) {
    return new ProtocolRequestMessageListenerContainerFactory(
        this.environment, this.protocolInfos, this.domainInfos, messageProcessorMap);
  }

  // bean used for sending protocol response messages
  // (outbound protocol responses)
  @Bean
  public ProtocolResponseMessageJmsTemplateFactory protocolResponseJmsTemplateFactory() {
    return new ProtocolResponseMessageJmsTemplateFactory(this.environment, this.protocolInfos);
  }

  // The Max count to retry a failed response

  @Bean
  public int getMaxRetryCount() {
    return this.maxRetryCount;
  }
}
