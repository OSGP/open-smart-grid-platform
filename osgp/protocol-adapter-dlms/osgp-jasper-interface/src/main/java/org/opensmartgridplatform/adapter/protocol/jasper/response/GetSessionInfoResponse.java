/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
