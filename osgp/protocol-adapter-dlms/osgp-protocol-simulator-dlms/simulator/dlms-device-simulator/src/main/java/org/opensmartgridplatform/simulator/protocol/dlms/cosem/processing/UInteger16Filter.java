// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing;

import java.util.List;
import org.openmuc.jdlms.datatypes.DataObject;

class UInteger16Filter extends RangeDescriptorFilter {

  private final Integer from;
  private final Integer to;

  UInteger16Filter(final List<DataObject> rangeDescriptor) {
    super(rangeDescriptor);
    this.from = rangeDescriptor.get(1).getValue();
    this.to = rangeDescriptor.get(2).getValue();
  }

  @Override
  public boolean match(final Object match) {
    if (!(match instanceof Integer)) {
      throw new IllegalArgumentException(
          this.getClass().getSimpleName()
              + " does not support matching of type "
              + match.getClass().getSimpleName());
    }

    final Integer value = (Integer) match;
    return value >= this.from && value <= this.to;
  }
}
