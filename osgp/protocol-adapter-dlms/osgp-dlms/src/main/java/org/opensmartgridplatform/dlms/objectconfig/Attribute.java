// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig;

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
  private ValueBasedOnModel valuebasedonmodel;
  private AccessType access;

  public Attribute(
      final int id,
      final String description,
      final String note,
      final DlmsDataType datatype,
      final ValueType valuetype,
      final String value,
      final ValueBasedOnModel valuebasedonmodel,
      final AccessType access) {
    this.id = id;
    this.description = description;
    this.note = note;
    this.datatype = datatype;
    this.valuetype = valuetype;
    this.value = value;
    this.valuebasedonmodel = valuebasedonmodel;
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
        this.valuebasedonmodel == null ? null : this.valuebasedonmodel.copy(),
        this.access);
  }

  public Attribute copyWithNewValue(final String newValue) {
    final Attribute newAttribute = this.copy();
    newAttribute.value = newValue;
    return newAttribute;
  }

  public Attribute copyWithNewValueAndType(final String newValue, final ValueType newValueType) {
    final Attribute newAttribute = this.copy();
    newAttribute.value = newValue;
    newAttribute.valuetype = newValueType;
    return newAttribute;
  }
}
