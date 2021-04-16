/*
 * Copyright 2014 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
