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
public class Attribute {
  private int id;
  private String description;
  private String note;
  private DlmsDataType datatype;
  private ValueType valuetype;
  private String value;
  private List<TypeBasedValue> values;
  private AccessType access;

  public Attribute(
      final int id,
      final String description,
      final String note,
      final DlmsDataType datatype,
      final ValueType valuetype,
      final String value,
      final List<TypeBasedValue> values,
      final AccessType access) {
    this.id = id;
    this.description = description;
    this.note = note;
    this.datatype = datatype;
    this.valuetype = valuetype;
    this.value = value;
    this.values = values;
    this.access = access;
  }

  public Attribute copy() {
    return new Attribute(
        this.id,
        this.description,
        this.note,
        this.datatype,
        this.valuetype,
        this.value,
        this.getValues() == null ? null : new ArrayList<>(this.getValues()),
        this.access);
  }

  public Attribute copyWithNewValue(final String newValue) {
    final Attribute newAttribute = this.copy();
    newAttribute.value = newValue;
    return newAttribute;
  }
}
