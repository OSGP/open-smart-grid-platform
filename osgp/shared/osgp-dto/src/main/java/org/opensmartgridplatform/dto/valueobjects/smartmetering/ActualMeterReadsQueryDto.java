// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

/**
 * request actual reads for E or GAS meters
 *
 * @author dev
 */
public class ActualMeterReadsQueryDto implements Serializable {
  private static final long serialVersionUID = 3751586818507193990L;

  private final ChannelDto channel;

  public ActualMeterReadsQueryDto() {
    this(null);
  }

  public ActualMeterReadsQueryDto(final ChannelDto channel) {
    this.channel = channel;
  }

  public boolean isMbusQuery() {
    return this.channel != null;
  }

  public ChannelDto getChannel() {
    return this.channel;
  }
}
