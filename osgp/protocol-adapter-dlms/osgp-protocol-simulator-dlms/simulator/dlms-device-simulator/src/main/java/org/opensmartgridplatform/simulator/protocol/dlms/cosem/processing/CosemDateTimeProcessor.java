/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing;

import java.util.Calendar;
import java.util.List;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;

public class CosemDateTimeProcessor implements DataProcessor {

  @Override
  public DataObject create(final Object data) {
    if (!(data instanceof Calendar)) {
      throw new IllegalArgumentException(
          this.getClass().getSimpleName()
              + " can not create expected DataObject from type "
              + data.getClass().getSimpleName());
    }

    final Calendar cal = (Calendar) data;
    final CosemDateTime dateTime =
        new CosemDateTime(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH),
            0xff,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            cal.get(Calendar.SECOND),
            0,
            0);

    return DataObject.newDateTimeData(dateTime);
  }

  @Override
  public RangeDescriptorFilter provide(final List<DataObject> rangeDescriptor) {
    return new CosemDateTimeFilter(rangeDescriptor);
  }
}
