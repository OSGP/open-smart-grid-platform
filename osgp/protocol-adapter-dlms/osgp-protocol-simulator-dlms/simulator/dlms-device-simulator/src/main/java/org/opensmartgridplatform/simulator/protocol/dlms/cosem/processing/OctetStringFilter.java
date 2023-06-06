// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing;

import java.util.List;
import org.openmuc.jdlms.datatypes.DataObject;

public class OctetStringFilter extends RangeDescriptorFilter {

  private byte[] from;
  private byte[] to;

  public OctetStringFilter(final List<DataObject> rangeDescriptor) {
    super(rangeDescriptor);
    this.from = rangeDescriptor.get(1).getValue();
    this.to = rangeDescriptor.get(2).getValue();
  }

  @Override
  public boolean match(final Object match) {
    if (!(match instanceof byte[])) {
      throw new IllegalArgumentException(
          this.getClass().getSimpleName()
              + " does not support matching of type "
              + match.getClass().getSimpleName());
    }

    final byte[] value = (byte[]) match;

    if (value.length != this.from.length) {
      throw new IllegalArgumentException("array length differs from " + this.from.length);
    }

    for (int i = 0; i < this.from.length; i++) {
      if (this.from[i] > value[i] || value[i] > this.to[i]) {
        return false;
      }
    }

    return true;
  }
}
