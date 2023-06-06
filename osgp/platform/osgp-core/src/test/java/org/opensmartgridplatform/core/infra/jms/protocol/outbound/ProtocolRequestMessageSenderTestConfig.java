// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.infra.jms.protocol.outbound;

import static org.mockito.Mockito.mock;

import org.opensmartgridplatform.core.infra.jms.protocol.DefaultProtocolJmsConfiguration;
import org.opensmartgridplatform.core.infra.messaging.CoreLogItemRequestMessageSender;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;

public class ProtocolRequestMessageSenderTestConfig {

  @Bean
  public int messageGroupCacheSize() {
    return 1024;
  }

  @Bean
  public DefaultProtocolJmsConfiguration defaultProtocolJmsConfiguration() {
    return new DefaultProtocolJmsConfiguration();
  }

  @Bean
  public ProtocolRequestMessageSender messageSender() {
    return new ProtocolRequestMessageSender();
  }

  @Bean
  public CoreLogItemRequestMessageSender coreLogItemRequestMessageSender() {
    return mock(CoreLogItemRequestMessageSender.class);
  }

  @Bean
  public JmsTemplate coreLogItemRequestsJmsTemplate() {
    return mock(JmsTemplate.class);
  }

  @Bean
  public ProtocolRequestMessageJmsTemplateFactory protocolRequestMessageJmsTemplateFactory() {
    return mock(ProtocolRequestMessageJmsTemplateFactory.class);
  }
}
