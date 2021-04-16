/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ScanMbusChannelsResponseData extends ActionResponse implements Serializable {

  private static final long serialVersionUID = -4198341767681440770L;

  private final List<MbusChannelShortEquipmentIdentifier> channelShortIds = new ArrayList<>();

  public ScanMbusChannelsResponseData(
      final List<MbusChannelShortEquipmentIdentifier> channelShortIds) {
    if (channelShortIds != null) {
      this.channelShortIds.addAll(channelShortIds);
    }
  }

  @Override
  public String toString() {
    return String.format("ScanMbusChannelsResponseData[channelShortIds=%s]", this.channelShortIds);
  }

  public List<MbusChannelShortEquipmentIdentifier> getChannelShortIds() {
    return new ArrayList<>(this.channelShortIds);
  }
}
