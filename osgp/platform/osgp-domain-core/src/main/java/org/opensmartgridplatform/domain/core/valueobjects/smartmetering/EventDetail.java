// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class EventDetail implements Serializable {

  private static final long serialVersionUID = -8568689422907058058L;
  private final String name;
  private final String value;

  public EventDetail(final String name, final String value) {
    this.name = name;
    this.value = value;
  }
}
