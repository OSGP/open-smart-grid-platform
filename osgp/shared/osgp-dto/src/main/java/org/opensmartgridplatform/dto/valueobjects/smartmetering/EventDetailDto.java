// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class EventDetailDto implements Serializable {

  private static final long serialVersionUID = 6866270221990320428L;

  private final String name;
  private final String value;

  public EventDetailDto(final String name, final String value) {
    this.name = name;
    this.value = value;
  }
}
