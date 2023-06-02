//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import lombok.Getter;

@Getter
public class GetFirmwareVersionGasRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = -5274875125208726393L;

  private final ChannelDto channel;

  private final String mbusDeviceIdentification;

  public GetFirmwareVersionGasRequestDto(
      final ChannelDto channel, final String mbusDeviceIdentification) {
    this.channel = channel;
    this.mbusDeviceIdentification = mbusDeviceIdentification;
  }
}
