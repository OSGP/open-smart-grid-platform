// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig;

import lombok.Data;

@Data
public class Attribute {
  private int id;
  private String description;
  private DlmsDataType datatype;
  private ValueType valuetype;
  private String value;
  private AccessType access;
}
