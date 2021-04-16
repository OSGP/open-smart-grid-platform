/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class MbusChannelShortEquipmentIdentifierDto implements Serializable {

  private static final long serialVersionUID = -5219063019275081622L;

  private final short channel;
  private final MbusShortEquipmentIdentifierDto shortId;

  public MbusChannelShortEquipmentIdentifierDto(
      final short channel, final MbusShortEquipmentIdentifierDto shortId) {
    this.channel = channel;
    this.shortId = shortId;
  }

  @Override
  public String toString() {
    return String.format(
        "MbusChannelShortEquipmentIdentifierDto[channel=%d, shortId=%s]",
        this.channel, this.shortId);
  }

  public short getChannel() {
    return this.channel;
  }

  public MbusShortEquipmentIdentifierDto getShortId() {
    return this.shortId;
  }
}
