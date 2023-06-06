// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
