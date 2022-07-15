/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.protocol.outbound;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.core.infra.jms.Registry;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.util.ReflectionTestUtils;

class ProtocolRequestMessageJmsTemplateFactoryTest {

  private Registry<JmsTemplate> jmsTemplateRegistry;
  private ProtocolRequestMessageJmsTemplateFactory protocolRequestMessageJmsTemplateFactory;

  @BeforeEach
  void setUp() {
    this.protocolRequestMessageJmsTemplateFactory =
        new ProtocolRequestMessageJmsTemplateFactory(
            mock(Environment.class), Collections.emptyList());

    this.jmsTemplateRegistry = new Registry<>();
    ReflectionTestUtils.setField(
        this.protocolRequestMessageJmsTemplateFactory,
        "jmsTemplateRegistry",
        this.jmsTemplateRegistry);
  }

  @Test
  void testGetJmsTemplateNotExist() {
    final ProtocolInfo protocolInfo = this.newProtocolInfo("SMR", "5.2", null);

    final JmsTemplate foundJmsTemplate =
        this.protocolRequestMessageJmsTemplateFactory.getJmsTemplate(protocolInfo);
    assertThat(foundJmsTemplate).isNull();
  }

  @Test
  void testGetJmsTemplate() {
    final JmsTemplate jmsTemplate = mock(JmsTemplate.class);
    final ProtocolInfo protocolInfo = this.newProtocolInfo("SMR", "5.2", null);

    this.jmsTemplateRegistry.register(protocolInfo.getKey(), jmsTemplate);

    final JmsTemplate foundJmsTemplate =
        this.protocolRequestMessageJmsTemplateFactory.getJmsTemplate(protocolInfo);
    assertThat(foundJmsTemplate).isEqualTo(jmsTemplate);
  }

  @Test
  void testGetJmsTemplateFallback() {
    final JmsTemplate jmsTemplateWithoutVariant = mock(JmsTemplate.class);
    final ProtocolInfo protocolInfoWithoutVariant = this.newProtocolInfo("SMR", "5.2", null);
    this.jmsTemplateRegistry.register(
        protocolInfoWithoutVariant.getKey(), jmsTemplateWithoutVariant);

    final JmsTemplate jmsTemplateWithVariant = mock(JmsTemplate.class);
    final ProtocolInfo protocolInfoWithVariant = this.newProtocolInfo("SMR", "5.2", "CDMA");
    this.jmsTemplateRegistry.register(protocolInfoWithVariant.getKey(), jmsTemplateWithVariant);

    final ProtocolInfo protocolInfo = this.newProtocolInfo("SMR", "5.2", "CDMA");

    final JmsTemplate foundJmsTemplate =
        this.protocolRequestMessageJmsTemplateFactory.getJmsTemplate(protocolInfo);
    assertThat(foundJmsTemplate).isEqualTo(jmsTemplateWithVariant);

    final ProtocolInfo protocolInfo2 = this.newProtocolInfo("SMR", "5.2", "GPRS");
    final JmsTemplate foundJmsTemplate2 =
        this.protocolRequestMessageJmsTemplateFactory.getJmsTemplate(protocolInfo2);
    assertThat(foundJmsTemplate2).isEqualTo(jmsTemplateWithoutVariant);

    final ProtocolInfo protocolInfo3 = this.newProtocolInfo("SMR", "5.2", null);
    final JmsTemplate foundJmsTemplate3 =
        this.protocolRequestMessageJmsTemplateFactory.getJmsTemplate(protocolInfo3);
    assertThat(foundJmsTemplate3).isEqualTo(jmsTemplateWithoutVariant);

    final ProtocolInfo protocolInfo4 = this.newProtocolInfo("SMR", "5.1", null);
    final JmsTemplate foundJmsTemplate4 =
        this.protocolRequestMessageJmsTemplateFactory.getJmsTemplate(protocolInfo4);
    assertThat(foundJmsTemplate4).isNull();
  }

  private ProtocolInfo newProtocolInfo(
      final String protocol, final String protocolVersion, final String protocolVariant) {
    return new ProtocolInfo.Builder()
        .withProtocol(protocol)
        .withProtocolVersion(protocolVersion)
        .withProtocolVariant(protocolVariant)
        .withOutgoingRequestsPropertyPrefix(protocol + "." + protocolVersion + ".")
        .build();
  }
}
