/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class FindEventsRequestDataList implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -1633881200321783021L;

  private List<FindEventsRequestData> findEventsQueryList;

  public FindEventsRequestDataList(final List<FindEventsRequestData> findEventsQueryList) {
    this.findEventsQueryList = findEventsQueryList;
  }

  public List<FindEventsRequestData> getFindEventsQueryList() {
    return this.findEventsQueryList;
  }
}
