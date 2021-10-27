/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing;

import java.util.List;
import org.openmuc.jdlms.datatypes.DataObject;

/** Abstract definition of a RangeDescriptorFilter. */
public abstract class RangeDescriptorFilter {

  public RangeDescriptorFilter(final List<DataObject> rangeDescriptor) {
    // Empty constructor, to be implmented by subclasses.
  }

  /**
   * Check if passed object matches the RangeDescriptor.
   *
   * @param match Object to match.
   * @return true if the value of match is between the from and to values of the RangeDescriptor.
   */
  public abstract boolean match(Object match);
}
