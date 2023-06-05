// Copyright 2017 Smart Society Services B.V.
// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.da.infra.jms;

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
