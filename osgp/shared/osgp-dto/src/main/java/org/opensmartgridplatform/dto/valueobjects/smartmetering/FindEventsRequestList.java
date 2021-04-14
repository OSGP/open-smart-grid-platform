/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class FindEventsRequestList implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 1917863566442592894L;

  private List<FindEventsRequestDto> findEventsQueryList;

  public FindEventsRequestList(final List<FindEventsRequestDto> findEventsQueryList) {
    this.findEventsQueryList = findEventsQueryList;
  }

  public List<FindEventsRequestDto> getFindEventsQueryList() {
    return this.findEventsQueryList;
  }
}
