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
import java.util.List;

public class BundleMessagesResponse implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -3617762659993165221L;

  private List<ActionResponse> actionResponseList;

  public BundleMessagesResponse(final List<ActionResponse> actionResponseList) {
    this.actionResponseList = actionResponseList;
  }

  public List<ActionResponse> getBundleList() {
    return this.actionResponseList;
  }
}
