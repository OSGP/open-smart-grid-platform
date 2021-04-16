/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.da.measurements;

import java.util.Arrays;
import java.util.Optional;

public enum MeasurementType {
  MEASURED_SHORT_FLOAT_WITH_TIME_TAG(36, "M_ME_TF_1");

  private int identifierNumber;
  private String identifierName;

  private MeasurementType(final int identifierNumber, final String identifierName) {
    this.identifierNumber = identifierNumber;
    this.identifierName = identifierName;
  }

  public int getIdentifierNumber() {
    return this.identifierNumber;
  }

  public String getIdentifierName() {
    return this.identifierName;
  }

  public static MeasurementType forIdentifierNumber(final int identifierNumber) {
    final Optional<MeasurementType> measurementType =
        Arrays.asList(values()).stream()
            .filter(m -> m.identifierNumber == identifierNumber)
            .findFirst();
    return measurementType.orElse(null);
  }

  public static MeasurementType forIdentifierName(final String identifierName) {
    final Optional<MeasurementType> measurementType =
        Arrays.asList(values()).stream()
            .filter(m -> m.identifierName.equals(identifierName))
            .findFirst();
    return measurementType.orElse(null);
  }
}
