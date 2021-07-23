/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms;

import java.io.Serializable;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

public class SmartMeteringRequestMessage extends RequestMessage {

  private static final long serialVersionUID = 8978488633831083383L;

  private SmartMeteringRequestMessage(
      final MessageMetadata messageMetadata, final Serializable request) {
    super(messageMetadata, request);
  }

  public static class Builder {
    private MessageMetadata messageMetadata;
    private Serializable request;

    public Builder() {
      // empty constructor
    }

    public Builder messageMetadata(final MessageMetadata messageMetadata) {
      this.messageMetadata = messageMetadata;
      return this;
    }

    public Builder request(final Serializable request) {
      this.request = request;
      return this;
    }

    public SmartMeteringRequestMessage build() {
      return new SmartMeteringRequestMessage(this.messageMetadata, this.request);
    }
  }
}
