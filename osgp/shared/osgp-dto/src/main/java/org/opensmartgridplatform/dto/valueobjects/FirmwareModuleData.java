/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;

public class FirmwareModuleData implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -2914554640938849434L;

  private final String moduleVersionComm;
  private final String moduleVersionFunc;
  private final String moduleVersionMa;
  private final String moduleVersionMbus;
  private final String moduleVersionSec;
  private final String moduleVersionMBusDriverActive;

  public FirmwareModuleData(
      final String moduleVersionComm,
      final String moduleVersionFunc,
      final String moduleVersionMa,
      final String moduleVersionMbus,
      final String moduleVersionSec,
      final String moduleVersionMBusDriverActive) {
    this.moduleVersionComm = moduleVersionComm;
    this.moduleVersionFunc = moduleVersionFunc;
    this.moduleVersionMa = moduleVersionMa;
    this.moduleVersionMbus = moduleVersionMbus;
    this.moduleVersionSec = moduleVersionSec;
    this.moduleVersionMBusDriverActive = moduleVersionMBusDriverActive;
  }

  public String getModuleVersionComm() {
    return this.moduleVersionComm;
  }

  public String getModuleVersionFunc() {
    return this.moduleVersionFunc;
  }

  public String getModuleVersionMa() {
    return this.moduleVersionMa;
  }

  public String getModuleVersionMbus() {
    return this.moduleVersionMbus;
  }

  public String getModuleVersionSec() {
    return this.moduleVersionSec;
  }

  public String getModuleVersionMBusDriverActive() {
    return this.moduleVersionMBusDriverActive;
  }

  public int countNumberOfModules() {
    int count = 0;
    if (StringUtils.isNotEmpty(this.moduleVersionComm)) {
      count++;
    }
    if (StringUtils.isNotEmpty(this.moduleVersionFunc)) {
      count++;
    }
    if (StringUtils.isNotEmpty(this.moduleVersionMa)) {
      count++;
    }
    if (StringUtils.isNotEmpty(this.moduleVersionMbus)) {
      count++;
    }
    if (StringUtils.isNotEmpty(this.moduleVersionSec)) {
      count++;
    }
    if (StringUtils.isNotEmpty(this.moduleVersionMBusDriverActive)) {
      count++;
    }
    return count;
  }
}
