// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ValueBasedOnModel {
  protected String type;
  protected List<TypeBasedValue> values;

  public ValueBasedOnModel(final String type, final List<TypeBasedValue> values) {
    this.type = type;
    this.values = values;
  }

  public ValueBasedOnModel copy() {
    return new ValueBasedOnModel(
        this.type, this.getValues() == null ? null : new ArrayList<>(this.getValues()));
  }
}
