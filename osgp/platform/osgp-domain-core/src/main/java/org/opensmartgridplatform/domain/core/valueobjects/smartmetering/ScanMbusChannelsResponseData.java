//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
