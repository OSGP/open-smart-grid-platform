/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
