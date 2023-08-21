// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing;

import java.util.List;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.GsmDiagnostic.CellInfo;

public class GsmDiagnosticCellInfoProcessor implements DataProcessor {

  @Override
  public DataObject create(final Object data) {
    if (!(data instanceof final CellInfo cellInfo)) {
      throw new IllegalArgumentException(
          this.getClass().getSimpleName()
              + " can not create expected DataObject from type "
              + data.getClass().getSimpleName());
    }

    return cellInfo.getDataObject();
  }

  @Override
  public RangeDescriptorFilter provide(final List<DataObject> rangeDescriptor) {
    return new RangeDescriptorFilter(rangeDescriptor) {
      @Override
      public boolean match(final Object match) {
        return true;
      }
    };
  }
}
