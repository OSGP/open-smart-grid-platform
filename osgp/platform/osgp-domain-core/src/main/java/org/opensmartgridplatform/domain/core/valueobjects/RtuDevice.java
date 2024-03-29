// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;

public class RtuDevice implements Serializable {

  private static final long serialVersionUID = -6133164707489276802L;

  /** Device type indicator for Peak Shaving Device */
  public static final String PSD_TYPE = "PSD";

  protected String deviceIdentification;
  protected String protocolName;
  protected String protocolVersion;
  protected String networkAddress;

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public void setDeviceIdentification(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  public String getProtocolName() {
    return this.protocolName;
  }

  public void setProtocolName(final String protocolName) {
    this.protocolName = protocolName;
  }

  public String getProtocolVersion() {
    return this.protocolVersion;
  }

  public void setProtocolVersion(final String protocolVersion) {
    this.protocolVersion = protocolVersion;
  }

  public String getNetworkAddress() {
    return this.networkAddress;
  }

  public void setNetworkAddress(final String networkAddress) {
    this.networkAddress = networkAddress;
  }
}
