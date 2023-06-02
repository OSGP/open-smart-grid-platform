//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.steps;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import io.cucumber.java.en.Then;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.LogItemRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec60870.testutils.matchers.LogItemTypeMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class LogItemSteps {
  private static final Logger LOGGER = LoggerFactory.getLogger(LogItemSteps.class);

  @Autowired
  @Qualifier("protocolIec60870OutboundLogItemRequestsMessageSender")
  private LogItemRequestMessageSender logItemRequestMessageSenderMock;

  @Then("I should send a log item with a message containing type {string}")
  public void thenIShouldSendLogItemWithMessageContainingType(final String typeId) {
    LOGGER.debug("Then I should send a log item with a message containing type {}", typeId);

    verify(this.logItemRequestMessageSenderMock).send(argThat(new LogItemTypeMatcher(typeId)));
  }
}
