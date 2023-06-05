// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class PowerQualityObjectDto implements Serializable {

  private static final long serialVersionUID = 2123390296585369208L;

  private final String name;
  private final String unit;

  public PowerQualityObjectDto(final String name, final String unit) {
    this.name = name;
    this.unit = unit;
  }
}
