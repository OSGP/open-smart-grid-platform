//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec60870.application.services;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.LoggingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.LogItem;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.LogItemRequestMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Iec60870LoggingService implements LoggingService {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870LoggingService.class);

  @Autowired private LogItemRequestMessageSender logItemRequestMessageSender;

  @Override
  public void log(final LogItem logItem) {
    LOGGER.info(
        "Sending LogItem request message for device: {}", logItem.getDeviceIdentification());
    this.logItemRequestMessageSender.send(logItem);
  }
}
