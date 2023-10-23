// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TypeBasedValue {
  private List<String> types;
  private String value;

  public TypeBasedValue(final List<String> types, final String value) {
    this.types = types;
    this.value = value;
  }
}
