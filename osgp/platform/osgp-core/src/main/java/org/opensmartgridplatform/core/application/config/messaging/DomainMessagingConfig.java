// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.application.config.messaging;

import java.util.List;
import org.opensmartgridplatform.core.infra.jms.domain.DefaultDomainJmsConfiguration;
import org.opensmartgridplatform.core.infra.jms.domain.inbound.DomainRequestMessageListenerContainerFactory;
import org.opensmartgridplatform.core.infra.jms.domain.inbound.DomainResponseMessageListenerContainerFactory;
import org.opensmartgridplatform.core.infra.jms.domain.outbound.DomainRequestMessageJmsTemplateFactory;
import org.opensmartgridplatform.core.infra.jms.domain.outbound.DomainResponseMessageJmsTemplateFactory;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.domain.core.repositories.ProtocolInfoRepository;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainMessagingConfig extends AbstractConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(DomainMessagingConfig.class);

  @Value("${netmanagement.organisation:test-org}")
  private String netmanagementOrganisation;

  // JMS Settings: SSL settings for the domain requests and responses
  @Value("${jms.domain.activemq.broker.client.key.store:/etc/osp/activemq/client.ks}")
  private String clientKeyStore;

  @Value("${jms.domain.activemq.broker.client.key.store.pwd:password}")
  private String clientKeyStorePwd;

  @Value("${jms.domain.activemq.broker.client.trust.store:/etc/osp/activemq/client.ts}")
  private String trustKeyStore;

  @Value("${jms.domain.activemq.broker.client.trust.store.pwd:password}")
  private String trustKeyStorePwd;

  private List<DomainInfo> domainInfos;
  private List<ProtocolInfo> protocolInfos;

  public DomainMessagingConfig(
      final DomainInfoRepository domainInfoRepository,
      final ProtocolInfoRepository protocolInfoRepository) {

    this.domainInfos = domainInfoRepository.findAll();
    this.protocolInfos = protocolInfoRepository.findAll();
  }

  @Bean
  public DefaultDomainJmsConfiguration defaultDomainJmsConfiguration() {
    return new DefaultDomainJmsConfiguration();
  }

  // bean used for sending domain response messages
  // (outbound domain responses)
  @Bean
  public DomainResponseMessageJmsTemplateFactory domainResponseJmsTemplateFactory() {
    LOGGER.debug("Creating bean: domainResponseJmsTemplateFactory");

    return new DomainResponseMessageJmsTemplateFactory(this.environment, this.domainInfos);
  }

  // bean used for receiving domain request messages
  // (inbound domain requests)
  @Bean
  public DomainRequestMessageListenerContainerFactory
      domainRequestMessageListenerContainerFactory() {
    LOGGER.debug("Creating bean: domainResponseMessageListenerContainerFactory");

    return new DomainRequestMessageListenerContainerFactory(this.environment, this.domainInfos);
  }

  // bean used for sending domain request messages
  // (outbound domain requests)
  @Bean
  public DomainRequestMessageJmsTemplateFactory domainRequestMessageJmsTemplateFactory() {

    return new DomainRequestMessageJmsTemplateFactory(this.environment, this.domainInfos);
  }

  // bean used for receiving domain response messages
  // (inbound domain responses)
  @Bean
  public DomainResponseMessageListenerContainerFactory domainResponseMessageListenerContainer() {
    return new DomainResponseMessageListenerContainerFactory(
        this.environment, this.domainInfos, this.protocolInfos);
  }

  @Bean
  public String netmanagementOrganisation() {
    return this.netmanagementOrganisation;
  }
}
