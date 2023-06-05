// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.microgrids.infra.jms;

import java.io.Serializable;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

public class MicrogridsRequestMessage extends RequestMessage {

  private static final long serialVersionUID = 7694973447737322410L;

  private final MessageType messageType;

  public MicrogridsRequestMessage(
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
