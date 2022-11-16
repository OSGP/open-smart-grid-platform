/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.jasper.rest.json;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendSMSResponse {

  private long smsMessageId;

  public SendSMSResponse() {}

  public SendSMSResponse(final long smsMessageId) {
    this.smsMessageId = smsMessageId;
  }
}
