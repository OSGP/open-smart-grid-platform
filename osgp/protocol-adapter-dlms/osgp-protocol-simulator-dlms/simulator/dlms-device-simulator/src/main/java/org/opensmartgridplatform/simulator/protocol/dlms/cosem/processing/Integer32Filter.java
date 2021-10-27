/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing;

import java.util.List;
import org.openmuc.jdlms.datatypes.DataObject;

class Integer32Filter extends RangeDescriptorFilter {

  private final Integer from;
  private final Integer to;

  Integer32Filter(final List<DataObject> rangeDescriptor) {
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
