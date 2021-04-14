/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.infra.jms;

import java.io.Serializable;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

public class DistributionAutomationRequestMessage extends RequestMessage {

  private static final long serialVersionUID = 8139039766689984959L;

  private final MessageType messageType;

  public DistributionAutomationRequestMessage(
      final MessageType messageType,
      final String correlationUid,
      final String organisationIdentification,
      final String deviceIdentification,
      final Serializable request) {
    super(correlationUid, organisationIdentification, deviceIdentification, request);
    this.messageType = messageType;
  }

  public MessageType getMessageType() {
    return this.messageType;
  }
}
