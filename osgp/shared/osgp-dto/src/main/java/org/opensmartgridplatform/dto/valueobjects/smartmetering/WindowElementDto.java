// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Objects;

public class WindowElementDto implements Serializable {

  private static final long serialVersionUID = 2516893643452886984L;

  private final CosemDateTimeDto startTime;
  private final CosemDateTimeDto endTime;

  public WindowElementDto(final CosemDateTimeDto startTime, final CosemDateTimeDto endTime) {
    Objects.requireNonNull(startTime, "startTime must not be null");
    Objects.requireNonNull(endTime, "endTime must not be null");
    this.startTime = startTime;
    this.endTime = endTime;
  }

  @Override
  public String toString() {
    return "WindowElement[start=" + this.startTime + ", end=" + this.endTime + "]";
  }

  public CosemDateTimeDto getStartTime() {
    return this.startTime;
  }

  public CosemDateTimeDto getEndTime() {
    return this.endTime;
  }
}
