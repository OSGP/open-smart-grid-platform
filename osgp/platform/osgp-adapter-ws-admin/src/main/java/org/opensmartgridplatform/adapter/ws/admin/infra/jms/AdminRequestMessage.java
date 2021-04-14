/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.admin.infra.jms;

import java.io.Serializable;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

public class AdminRequestMessage extends RequestMessage {

  /** Serial Version UID. */
  private static final long serialVersionUID = -6355169640460823281L;

  private final MessageType messageType;

  public AdminRequestMessage(
      final MessageType messageType,
      final String correlationUid,
      final String organisationIdentification,
      final String deviceIdentification,
      final Serializable ovlRequest) {
    super(correlationUid, organisationIdentification, deviceIdentification, ovlRequest);

    this.messageType = messageType;
  }

  public MessageType getMessageType() {
    return this.messageType;
  }
}
