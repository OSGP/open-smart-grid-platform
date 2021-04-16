/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;
import java.util.List;

public class LightValueMessageDataContainerDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 4725254533964342905L;

  private List<LightValueDto> lightValues;

  public LightValueMessageDataContainerDto(final List<LightValueDto> lightValues) {
    this.lightValues = lightValues;
  }

  public List<LightValueDto> getLightValues() {
    return this.lightValues;
  }
}
