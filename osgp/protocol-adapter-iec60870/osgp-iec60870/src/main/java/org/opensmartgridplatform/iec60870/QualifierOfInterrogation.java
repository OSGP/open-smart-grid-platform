// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.iec60870;

import java.util.HashMap;
import java.util.Map;

public enum QualifierOfInterrogation {
  INTERROGATED_BY_STATION(20),
  INTERROGATED_BY_GROUP_1(21),
  INTERROGATED_BY_GROUP_2(22),
  INTERROGATED_BY_GROUP_3(23),
  INTERROGATED_BY_GROUP_4(24),
  INTERROGATED_BY_GROUP_5(25),
  INTERROGATED_BY_GROUP_6(26),
  INTERROGATED_BY_GROUP_7(27),
  INTERROGATED_BY_GROUP_8(28),
  INTERROGATED_BY_GROUP_9(29),
  INTERROGATED_BY_GROUP_10(30),
  INTERROGATED_BY_GROUP_11(31),
  INTERROGATED_BY_GROUP_12(32),
  INTERROGATED_BY_GROUP_13(33),
  INTERROGATED_BY_GROUP_14(34),
  INTERROGATED_BY_GROUP_15(35),
  INTERROGATED_BY_GROUP_16(36);

  private final int id;

  private static final Map<Integer, QualifierOfInterrogation> idMap = new HashMap<>();

  static {
    for (final QualifierOfInterrogation enumInstance : QualifierOfInterrogation.values()) {
      if (idMap.put(enumInstance.getId(), enumInstance) != null) {
        throw new IllegalArgumentException("duplicate ID: " + enumInstance.getId());
      }
    }
  }

  private QualifierOfInterrogation(final int id) {
    this.id = id;
  }

  /**
   * Returns the ID of this QualifierOfInterrogation.
   *
   * @return the ID.
   */
  public int getId() {
    return this.id;
  }

  /**
   * Returns the QualifierOfInterrogation that corresponds to the given ID. Returns <code>null
   * </code> if no QualifierOfInterrogation with the given ID exists.
   *
   * @param id the ID.
   * @return the QualifierOfInterrogation that corresponds to the given ID.
   */
  public static QualifierOfInterrogation qualifierFor(final int id) {
    return idMap.get(id);
  }
}
