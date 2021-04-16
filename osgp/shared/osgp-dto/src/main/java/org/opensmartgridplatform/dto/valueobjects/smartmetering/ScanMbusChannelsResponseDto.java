/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.ArrayList;
import java.util.List;

public class ScanMbusChannelsResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = 778017393218321982L;

  private final ArrayList<MbusChannelShortEquipmentIdentifierDto> channelShortIds =
      new ArrayList<>();

  public ScanMbusChannelsResponseDto(
      final List<MbusChannelShortEquipmentIdentifierDto> channelShortIds) {
    if (channelShortIds != null) {
      this.channelShortIds.addAll(channelShortIds);
    }
  }

  @Override
  public String toString() {
    return String.format("ScanMbusChannelsResponseDto[channelShortIds=%s]", this.channelShortIds);
  }

  public List<MbusChannelShortEquipmentIdentifierDto> getChannelShortIds() {
    return new ArrayList<>(this.channelShortIds);
  }
}
