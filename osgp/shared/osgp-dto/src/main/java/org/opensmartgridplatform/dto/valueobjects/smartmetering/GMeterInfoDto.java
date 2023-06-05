// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

/** Class to pass G-Meter information to the protocol adapter */
public class GMeterInfoDto implements ActionRequestDto {

  private static final long serialVersionUID = -4321438772672309715L;
  private final int channel;
  private final String deviceIdentification;

  public GMeterInfoDto(final int channel, final String deviceIdentification) {
    this.channel = channel;
    this.deviceIdentification = deviceIdentification;
  }

  public int getChannel() {
    return this.channel;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }
}
