//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class ActualMeterReadsDataGasDto implements ActionRequestDto {

  private static final long serialVersionUID = -3285918794296226542L;

  private final ChannelDto channel;

  public ActualMeterReadsDataGasDto(final ChannelDto channel) {
    this.channel = channel;
  }

  public ChannelDto getChannel() {
    return this.channel;
  }
}
