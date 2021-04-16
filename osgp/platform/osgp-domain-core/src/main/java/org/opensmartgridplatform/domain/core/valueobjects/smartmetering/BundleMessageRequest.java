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

public class BundleMessageRequest implements Serializable {
  /** Serial Version UID. */
  private static final long serialVersionUID = -1865461707073500L;

  private List<ActionRequest> actionList;

  public BundleMessageRequest(final List<ActionRequest> actionList) {
    this.actionList = actionList;
  }

  public List<ActionRequest> getBundleList() {
    return this.actionList;
  }
}
