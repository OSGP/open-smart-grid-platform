// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DlmsDataType {
  // Common DLMS data types, defined in Green Book
  @JsonProperty("null-data")
  NULL_DATA,
  @JsonProperty("array")
  ARRAY,
  @JsonProperty("structure")
  STRUCTURE,
  @JsonProperty("boolean")
  BOOLEAN,
  @JsonProperty("bit-string")
  BIT_STRING,
  @JsonProperty("double-long")
  DOUBLE_LONG,
  @JsonProperty("double-long-unsigned")
  DOUBLE_LONG_UNSIGNED,
  @JsonProperty("octet-string")
  OCTET_STRING,
  @JsonProperty("visible-string")
  VISIBLE_STRING,
  @JsonProperty("utf8-string")
  UTF8_STRING,
  @JsonProperty("bcd")
  BCD,
  @JsonProperty("integer")
  INTEGER,
  @JsonProperty("long")
  LONG,
  @JsonProperty("unsigned")
  UNSIGNED,
  @JsonProperty("long-unsigned")
  LONG_UNSIGNED,
  @JsonProperty("compact-array")
  COMPACT_ARRAY,
  @JsonProperty("long64")
  LONG64,
  @JsonProperty("long64-unsigned")
  LONG64_UNSIGNED,
  @JsonProperty("enum")
  ENUM,
  @JsonProperty("float32")
  FLOAT32,
  @JsonProperty("float64")
  FLOAT64,
  @JsonProperty("date-time")
  DATE_TIME,
  @JsonProperty("date")
  DATE,
  @JsonProperty("time")
  TIME,
  @JsonProperty("dont-care")
  DONT_CARE,

  // Data types for specific classes

  // Class-id 3: Register
  @JsonProperty("scal_unit_type")
  SCAL_UNIT_TYPE,

  // Class-id 7: Profile
  @JsonProperty("object_definition")
  OBJECT_DEFINITION,

  // Class-id 8: Clock
  @JsonProperty("clock_status")
  CLOCK_STATUS,

  // Class-id 47: GSM diagnostic
  @JsonProperty("cell_info_type")
  CELL_INFO_TYPE
}
