// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig;

import java.util.List;
import lombok.Data;

@Data
public class Attribute {
  private int id;
  private String description;
  private String note;
  private DlmsDataType datatype;
  private ValueType valuetype;
  private String value;
  private List<TypeBasedValue> values;
  private AccessType access;

  public Attribute() {}

  public Attribute(final Attribute attribute) {
    this.id = attribute.id;
    this.description = attribute.description;
    this.note = attribute.note;
    this.datatype = attribute.datatype;
    this.valuetype = attribute.valuetype;
    this.value = attribute.value;
    this.values = attribute.getValues();
    this.access = attribute.access;
  }
}
