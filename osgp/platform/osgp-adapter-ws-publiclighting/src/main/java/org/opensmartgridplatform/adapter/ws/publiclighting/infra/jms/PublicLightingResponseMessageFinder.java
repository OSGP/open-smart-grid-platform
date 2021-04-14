/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.publiclighting.infra.jms;

import javax.jms.ObjectMessage;
import org.opensmartgridplatform.shared.infra.jms.BaseResponseMessageFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Class for retrieving response messages from the public lighting responses queue by correlation
 * UID.
 */
@Component(value = "wsPublicLightingInboundDomainResponsesMessageFinder")
public class PublicLightingResponseMessageFinder extends BaseResponseMessageFinder {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(PublicLightingResponseMessageFinder.class);

  @Autowired
  @Qualifier("wsPublicLightingInboundDomainResponsesJmsTemplate")
  private JmsTemplate jmsTemplate;

  @Override
  protected ObjectMessage receiveObjectMessage(final String correlationUid) {
    LOGGER.info("Trying to find message with correlationUID: {}", correlationUid);

    return (ObjectMessage)
        this.jmsTemplate.receiveSelected(this.getJmsCorrelationId(correlationUid));
  }
}
