/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Objects;

public class WindowElement implements Serializable {

  private static final long serialVersionUID = 2516893643452886984L;

  private final CosemDateTime startTime;
  private final CosemDateTime endTime;

  public WindowElement(final CosemDateTime startTime, final CosemDateTime endTime) {
    Objects.requireNonNull(startTime, "startTime must not be null");
    Objects.requireNonNull(endTime, "endTime must not be null");
    this.startTime = startTime;
    this.endTime = endTime;
  }

  @Override
  public String toString() {
    return "WindowElement[start=" + this.startTime + ", end=" + this.endTime + "]";
  }

  public CosemDateTime getStartTime() {
    return this.startTime;
  }

  public CosemDateTime getEndTime() {
    return this.endTime;
  }
}
