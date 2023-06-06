// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
