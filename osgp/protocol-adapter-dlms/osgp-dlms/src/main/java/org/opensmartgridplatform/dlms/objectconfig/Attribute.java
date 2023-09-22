// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig;

import java.util.Map;
import lombok.Data;

@Data
public class Attribute {
  private int id;
  private String description;
  private String note;
  private DlmsDataType datatype;
  private ValueType valuetype;
  private String value;
  private Map<Object, Object> values;
  private AccessType access;
}
