//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

public abstract class MbusActionRequest implements ActionRequest {

  private static final long serialVersionUID = 2320272681064473304L;

  private String mbusDeviceIdentification;
  private Short channel;

  public MbusActionRequest(final String mbusDeviceIdentification) {
    this.mbusDeviceIdentification = mbusDeviceIdentification;
  }

  public String getMbusDeviceIdentification() {
    return this.mbusDeviceIdentification;
  }

  public Short getChannel() {
    return this.channel;
  }

  public void setChannel(final Short channel) {
    this.channel = channel;
  }
}
