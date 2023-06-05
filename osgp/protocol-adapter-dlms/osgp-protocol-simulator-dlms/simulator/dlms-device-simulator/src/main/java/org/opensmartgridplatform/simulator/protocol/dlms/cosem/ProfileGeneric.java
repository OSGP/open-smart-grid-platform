// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import java.util.List;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.openmuc.jdlms.CosemSnInterfaceObject;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing.CaptureObjectDefinitionCollection;

abstract class ProfileGeneric extends CosemSnInterfaceObject {

  protected CircularFifoQueue<List<?>> bufferData;

  /**
   * GenericProfile for LN
   *
   * @param instanceId the OBIS code
   */
  public ProfileGeneric(final String instanceId) {
    super(0, instanceId);
  }

  public ProfileGeneric(final int objectName, final String instanceId) {
    super(objectName, instanceId);
  }

  /**
   * Provides access to statically initialized CaptureObjectDefinitionCollection.
   *
   * @return CaptureObjectDefinitionCollection
   */
  protected abstract CaptureObjectDefinitionCollection getCaptureObjectDefinitionCollection();

  /**
   * Provides the number of records in the bufferData.
   *
   * @return the number of records in the bufferData.
   */
  public DataObject getEntriesInUse() {
    return DataObject.newInteger32Data(this.bufferData.size());
  }

  public DataObject getCaptureObjects() {
    return this.getCaptureObjectDefinitionCollection().captureObjectsAsDataObject();
  }

  /**
   * Filters bufferData according to selectiveAccessDescription and converts it to DataObject.
   *
   * @param selectiveAccessDescription selective access description.
   * @return Filtered and converted data.
   */
  public DataObject getBuffer(final SelectiveAccessDescription selectiveAccessDescription) {
    if (selectiveAccessDescription != null) {
      if (selectiveAccessDescription.getAccessSelector() == 1) {
        final List<DataObject> rangeDescriptor =
            selectiveAccessDescription.getAccessParameter().getValue();

        return this.getCaptureObjectDefinitionCollection()
            .filterAndConvertBufferData(rangeDescriptor, this.bufferData);
      } else if (selectiveAccessDescription.getAccessSelector() == 2) {
        throw new UnsupportedOperationException(
            "Selective access entry descriptor (2) is not supported.");
      }
    }

    // Unfiltered data.
    return this.getCaptureObjectDefinitionCollection().convertBufferData(this.bufferData);
  }

  public void setBuffer(
      final DataObject buffer, final SelectiveAccessDescription selectiveAccessDescription) {
    // Unused method, needed by jDLMS implementation.
  }
}
