/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.iec60870;

import java.util.Arrays;

public enum Iec60870InformationObjectType {
  SHORT_FLOAT("IeShortFloat"),
  SINGLE_POINT_INFORMATION_WITH_QUALITY("IeSinglePointWithQuality"),
  QUALIFIER_OF_INTERROGATION("IeQualifierOfInterrogation"),
  TIME56("IeTime56");

  private String description;

  Iec60870InformationObjectType(final String description) {
    this.description = description;
  }

  public String getDescription() {
    return this.description;
  }

  public static Iec60870InformationObjectType fromString(final String description) {
    return Arrays.stream(Iec60870InformationObjectType.values())
        .filter(iot -> iot.description.equalsIgnoreCase(description))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Iec60870InformationObjectType with description "
                        + description
                        + " not found."));
  }
}
