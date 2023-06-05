// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;

public class DeviceModel implements Serializable {

  private static final long serialVersionUID = 2817683210984491986L;

  private String manufacturer;

  private String modelCode;

  private String description;

  public DeviceModel() {
    // Default constructor
  }

  public DeviceModel(final String manufacturer, final String modelCode, final String description) {
    this.manufacturer = manufacturer;
    this.modelCode = modelCode;
    this.description = description;
  }

  public String getManufacturer() {
    return this.manufacturer;
  }

  public void setManufacturer(final String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public String getModelCode() {
    return this.modelCode;
  }

  public void setModelCode(final String modelCode) {
    this.modelCode = modelCode;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }
}
