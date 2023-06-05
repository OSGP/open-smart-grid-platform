// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
