// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
