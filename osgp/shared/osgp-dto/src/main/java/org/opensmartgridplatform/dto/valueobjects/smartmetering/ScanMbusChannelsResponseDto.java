//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
