/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

public enum SignalQualityType {
  MINUS_113_DBM_OR_LESS,
  MINUS_111_DBM,
  MINUS_109_DBM,
  MINUS_107_DBM,
  MINUS_105_DBM,
  MINUS_103_DBM,
  MINUS_101_DBM,
  MINUS_99_DBM,
  MINUS_97_DBM,
  MINUS_95_DBM,
  MINUS_93_DBM,
  MINUS_91_DBM,
  MINUS_89_DBM,
  MINUS_87_DBM,
  MINUS_85_DBM,
  MINUS_83_DBM,
  MINUS_81_DBM,
  MINUS_79_DBM,
  MINUS_77_DBM,
  MINUS_75_DBM,
  MINUS_73_DBM,
  MINUS_71_DBM,
  MINUS_69_DBM,
  MINUS_67_DBM,
  MINUS_65_DBM,
  MINUS_63_DBM,
  MINUS_61_DBM,
  MINUS_59_DBM,
  MINUS_57_DBM,
  MINUS_55_DBM,
  MINUS_53_DBM,
  MINUS_51_DBM_OR_GREATER,
  NOT_KNOWN_OR_NOT_DETECTABLE;

  public String value() {
    return this.name();
  }

  public static SignalQualityType fromValue(final String v) {
    return valueOf(v);
  }
}
