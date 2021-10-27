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

/**
 * Interface for objects that are able to provide a filter when given a RangeDescriptor (see Blue
 * Book Ed. 10, page 48)
 */
public interface FilterProvider {
  RangeDescriptorFilter provide(final List<DataObject> rangeDescriptor);
}
