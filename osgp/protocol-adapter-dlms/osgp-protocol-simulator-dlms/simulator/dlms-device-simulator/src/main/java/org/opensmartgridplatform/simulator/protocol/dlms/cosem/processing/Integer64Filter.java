// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing;

import java.util.List;
import org.openmuc.jdlms.datatypes.DataObject;

class Integer64Filter extends RangeDescriptorFilter {

  private final Long from;
  private final Long to;

  Integer64Filter(final List<DataObject> rangeDescriptor) {
    super(rangeDescriptor);
    this.from = rangeDescriptor.get(1).getValue();
    this.to = rangeDescriptor.get(2).getValue();
  }

  @Override
  public boolean match(final Object match) {
    final Long value;

    if (match instanceof Long longMatch) {
      value = longMatch;
    } else if (match instanceof Integer integerMatch) {
      value = Long.valueOf(integerMatch);
    } else {
      throw new IllegalArgumentException(
          this.getClass().getSimpleName()
              + " does not support matching of type "
              + match.getClass().getSimpleName());
    }

    return value >= this.from && value <= this.to;
  }
}
