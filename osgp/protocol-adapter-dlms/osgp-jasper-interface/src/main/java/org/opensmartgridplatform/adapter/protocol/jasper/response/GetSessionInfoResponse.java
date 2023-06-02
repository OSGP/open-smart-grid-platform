//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.jasper.response;

import java.util.Date;
import lombok.Getter;

@Getter
public class GetSessionInfoResponse {

  private String iccid;
  private String ipAddress;
  private String ipV6Address;
  private Date dateSessionStarted;
  private Date dateSessionEnded;

  public GetSessionInfoResponse() {}

  public GetSessionInfoResponse(
      final String iccid,
      final String ipAddress,
      final String ipV6Address,
      final Date dateSessionStarted,
      final Date dateSessionEnded) {
    this.iccid = iccid;
    this.ipAddress = ipAddress;
    this.ipV6Address = ipV6Address;
    this.dateSessionStarted = dateSessionStarted;
    this.dateSessionEnded = dateSessionEnded;
  }
}
