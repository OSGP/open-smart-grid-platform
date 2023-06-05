// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils;

import java.util.Arrays;
import org.openmuc.jdlms.datatypes.DataObject;

/**
 * This class encapsulates the objects that are used by more than one class
 *
 * @author Hannek Toebast
 */
public class DataObjectDefinitions {

  private static final int CLASS_ID_CLOCK = 8;
  private static final byte[] OBIS_BYTES_CLOCK = new byte[] {0, 0, 1, 0, 0, (byte) 255};
  private static final byte ATTRIBUTE_ID_TIME = 2;

  private static final int CLASS_ID_DATA = 1;
  private static final byte[] OBIS_BYTES_AMR_PROFILE_STATUS =
      new byte[] {0, 0, 96, 10, 2, (byte) 255};
  private static final byte ATTRIBUTE_ID_VALUE = 2;

  private DataObjectDefinitions() {
    // empty private constructor
  }

  public static DataObject getClockDefinition() {
    // {8,0-0:1.0.0.255,2,0} - Clock
    return DataObject.newStructureData(
        Arrays.asList(
            DataObject.newUInteger16Data(CLASS_ID_CLOCK),
            DataObject.newOctetStringData(OBIS_BYTES_CLOCK),
            DataObject.newInteger8Data(ATTRIBUTE_ID_TIME),
            DataObject.newUInteger16Data(0)));
  }

  public static DataObject getAMRProfileDefinition() {
    // {1,0-0:96.10.2.255,2,0} - AMR profile status
    return DataObject.newStructureData(
        Arrays.asList(
            DataObject.newUInteger16Data(CLASS_ID_DATA),
            DataObject.newOctetStringData(OBIS_BYTES_AMR_PROFILE_STATUS),
            DataObject.newInteger8Data(ATTRIBUTE_ID_VALUE),
            DataObject.newUInteger16Data(0)));
  }
}
