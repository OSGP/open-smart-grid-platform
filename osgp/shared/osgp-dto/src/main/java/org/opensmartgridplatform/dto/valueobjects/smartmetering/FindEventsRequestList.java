// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
