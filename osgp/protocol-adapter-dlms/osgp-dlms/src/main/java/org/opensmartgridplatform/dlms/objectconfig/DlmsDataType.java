/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */

package org.opensmartgridplatform.dlms.objectconfig;

public enum DlmsDataType {
  // Common DLMS data types, defined in Green Book
  NULL_DATA,
  ARRAY,
  STRUCTURE,
  BOOLEAN,
  BIT_STRING,
  DOUBLE_LONG,
  DOUBLE_LONG_UNSIGNED,
  OCTET_STRING,
  VISIBLE_STRING,
  UTF8_STRING,
  BCD,
  INTEGER,
  LONG,
  LONG_UNSIGNED,
  COMPACT_ARRAY,
  LONG64,
  LONG64_UNSIGNED,
  ENUM,
  FLOAT32,
  FLOAT64,
  DATE_TIME,
  DATE,
  TIME,
  DONT_CARE,

  // Data types for specific classes

  // Class-id 3: Register
  SCAL_UNIT_TYPE,

  // Class-id 7: Profile
  OBJECT_DEFINITION,

  // Class-id 8: Clock
  CLOCK_STATUS,

  // Class-id 47: GSM diagnostic
  CELL_INFO_TYPE
}
