// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
