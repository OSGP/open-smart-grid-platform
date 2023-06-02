//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing;

import org.openmuc.jdlms.datatypes.DataObject;

/** Creates a DataObject out of an Object. */
public interface DataObjectCreator {
  DataObject create(Object data);
}
