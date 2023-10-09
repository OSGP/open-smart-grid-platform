// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Attribute {
  private int id;
  private String description;
  private String note;
  private DlmsDataType datatype;
  private ValueType valuetype;
  private String value;
  private List<TypeBasedValue> values;
  private AccessType access;

  public Attribute copy() {
    return new Attribute(
        this.id,
        this.description,
        this.note,
        this.datatype,
        this.valuetype,
        this.value,
        this.getValues(),
        this.access);
  }

  public Attribute copyWithNewValue(final String newValue) {
    final Attribute newAttribute = this.copy();
    newAttribute.value = newValue;
    return newAttribute;
  }
}
