// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.admin.infra.jms;

import jakarta.jms.ObjectMessage;
import org.opensmartgridplatform.shared.infra.jms.BaseResponseMessageFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/** Class for retrieving response messages from the admin responses queue by correlation UID. */
@Component(value = "wsAdminResponseMessageFinder")
public final class AdminResponseMessageFinder extends BaseResponseMessageFinder {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdminResponseMessageFinder.class);

  @Autowired
  @Qualifier("wsAdminInboundDomainResponsesJmsTemplate")
  private JmsTemplate jmsTemplate;

  @Override
  protected ObjectMessage receiveObjectMessage(final String correlationUid) {
    LOGGER.info("Trying to find message with correlationUID: {}", correlationUid);

    return (ObjectMessage)
        this.jmsTemplate.receiveSelected(this.getJmsCorrelationId(correlationUid));
  }
}
