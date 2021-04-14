/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import org.hibernate.validator.constraints.Range;

// Class to pass objects in a Resume schedule asynchronous message
public class ResumeScheduleData implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -225776178716745555L;

  private static final int MIN_INDEX = 0;
  private static final int MAX_INDEX = 6;

  @Range(min = MIN_INDEX, max = MAX_INDEX)
  private int index;

  private Boolean isImmediate;

  public int getIndex() {
    return this.index;
  }

  public void setIndex(final int newValue) {
    this.index = newValue;
  }

  public Boolean getIsImmediate() {
    return this.isImmediate;
  }

  public void setIsImmediate(final Boolean newValue) {
    this.isImmediate = newValue;
  }
}
