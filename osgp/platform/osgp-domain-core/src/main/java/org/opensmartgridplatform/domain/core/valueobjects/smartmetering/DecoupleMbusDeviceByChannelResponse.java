/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class DecoupleMbusDeviceByChannelResponse extends ActionResponse implements Serializable {

  private static final long serialVersionUID = -7800915379658671321L;

  private final Short channel;
  private final String mbusDeviceIdentification;

  public DecoupleMbusDeviceByChannelResponse(
      final String mbusDeviceIdentification, final Short channel) {
    super(OsgpResultType.OK, null, "Decouple Mbus Device By Channel was successful");
    this.mbusDeviceIdentification = mbusDeviceIdentification;
    this.channel = channel;
  }

  @Override
  public String toString() {
    return "DecoupleMbusDeviceByChannelResponse [channel="
        + this.channel
        + ", mbusDeviceIdentification="
        + this.mbusDeviceIdentification
        + "]";
  }
}
