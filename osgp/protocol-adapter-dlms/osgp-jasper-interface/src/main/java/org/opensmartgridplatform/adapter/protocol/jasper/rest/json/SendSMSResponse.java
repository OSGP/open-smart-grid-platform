// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
