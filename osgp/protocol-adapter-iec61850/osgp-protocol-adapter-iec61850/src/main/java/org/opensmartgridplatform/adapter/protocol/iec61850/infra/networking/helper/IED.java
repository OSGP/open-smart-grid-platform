//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper;

/** Contains the name of the IED. */
public enum IED {
  FLEX_OVL("SWDeviceGeneric"),
  ABB_RTU("AA1TH01"),
  ZOWN_RTU("WAGO61850Server"),
  DA_RTU("WAGO61850Server");

  private String description;

  private IED(final String description) {
    this.description = description;
  }

  public String getDescription() {
    return this.description;
  }
}
