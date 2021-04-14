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

public class LightValueDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 3783788109559927722L;

  private final Integer index;

  private boolean on;

  private final Integer dimValue;

  public LightValueDto(final Integer index, final boolean on, final Integer dimValue) {
    this.index = index;
    this.on = on;
    this.dimValue = dimValue;
  }

  public Integer getIndex() {
    return this.index;
  }

  public boolean isOn() {
    return this.on;
  }

  public Integer getDimValue() {
    return this.dimValue;
  }

  public void invertIsOn() {
    this.on = !this.on;
  }
}
