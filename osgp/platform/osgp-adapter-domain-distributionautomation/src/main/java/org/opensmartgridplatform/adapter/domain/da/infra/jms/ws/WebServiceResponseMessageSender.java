/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.da.infra.jms.ws;

import org.opensmartgridplatform.adapter.domain.da.infra.jms.BaseResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.NotificationResponseMessageSender;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

// Send response message to the web service adapter.
@Component(value = "domainDistributionAutomationOutboundWebServiceResponsesMessageSender")
public class WebServiceResponseMessageSender extends BaseResponseMessageSender
    implements NotificationResponseMessageSender {

  @Autowired
  @Qualifier("domainDistributionAutomationOutboundWebServiceResponsesJmsTemplate")
  private JmsTemplate jmsTemplate;

  @Override
  public void send(final ResponseMessage responseMessage, final String messageType) {
    this.send(this.jmsTemplate, responseMessage, null, messageType);
  }
}
