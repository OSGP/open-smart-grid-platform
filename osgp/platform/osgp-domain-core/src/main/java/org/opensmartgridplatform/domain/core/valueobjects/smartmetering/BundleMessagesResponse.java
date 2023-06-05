// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
