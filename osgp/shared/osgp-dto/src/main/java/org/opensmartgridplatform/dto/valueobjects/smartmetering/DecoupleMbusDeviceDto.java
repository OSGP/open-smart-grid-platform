/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class DecoupleMbusDeviceDto implements Serializable, ActionRequestDto {

  private static final long serialVersionUID = 5377631203726277889L;

  private final Short channel;

  public DecoupleMbusDeviceDto(final Short channel) {
    this.channel = channel;
  }

  @Override
  public String toString() {
    return "DecoupleMbusDeviceDto [channel=" + this.channel + "]";
  }
}
