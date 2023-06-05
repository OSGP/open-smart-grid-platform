// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing;

import java.util.List;
import org.openmuc.jdlms.datatypes.DataObject;

public class BoolFilter extends RangeDescriptorFilter {

  BoolFilter(final List<DataObject> rangeDescriptor) {
    super(rangeDescriptor);
  }

  @Override
  public boolean match(final Object match) {
    if (!(match instanceof Boolean)) {
      throw new IllegalArgumentException(
          this.getClass().getSimpleName()
              + " does not support matching of type "
              + match.getClass().getSimpleName());
    }

    return true;
  }
}
