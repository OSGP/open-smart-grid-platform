// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.rest.json;

import lombok.Data;

@Data
public class SendSMSRequest {

  private String messageText;
  private Short tpvp;
}
