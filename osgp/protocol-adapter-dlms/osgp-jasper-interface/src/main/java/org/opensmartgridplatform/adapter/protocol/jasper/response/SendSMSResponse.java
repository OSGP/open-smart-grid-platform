// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.response;

import lombok.Getter;

@Getter
public class SendSMSResponse {

  private long smsMsgId;

  public SendSMSResponse() {}

  public SendSMSResponse(final long smsMsgId) {
    this.smsMsgId = smsMsgId;
  }
}
