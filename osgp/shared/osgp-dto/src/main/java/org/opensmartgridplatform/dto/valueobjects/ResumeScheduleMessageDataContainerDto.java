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

public class ResumeScheduleMessageDataContainerDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 4989992501170383172L;

  Integer index;
  boolean isImmediate;

  public ResumeScheduleMessageDataContainerDto(final Integer index, final boolean isImmediate) {
    this.index = index;
    this.isImmediate = isImmediate;
  }

  public Integer getIndex() {
    return this.index;
  }

  public boolean isImmediate() {
    return this.isImmediate;
  }
}
