//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
