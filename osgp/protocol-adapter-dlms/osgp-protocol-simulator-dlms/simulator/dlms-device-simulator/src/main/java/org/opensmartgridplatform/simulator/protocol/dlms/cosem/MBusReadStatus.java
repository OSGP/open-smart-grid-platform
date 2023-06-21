// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import org.openmuc.jdlms.AttributeAccessMode;
import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;

/**
 * M-Bus Master Value (Class ID: 4, Version: 0) Instance specific (4 instances, one per channel)
 *
 * <p>1 logical_name octet-string 0-x:24.2.y.255 (x=channel number(1..4), One channel per M-Bus
 * device, (y=instance number (1..6) R
 *
 * <p>2 value double-long-unsigned Measurement value R
 *
 * <p>3 scaler_unit scal_unit_type set by the E-meter by reading the M-Bus device VIF/DIF
 * combination from the M-Bus device R
 *
 * <p>4 status unsigned status of M-Bus Value 0 = Data Valid 4 = Data Not Valid (DNV) R
 *
 * <p>5 capture_time octet-string time of last successful readout R
 *
 * <p>Specific methods m/o 1 reset (data) o
 */
@CosemClass(id = 4)
public class MBusReadStatus extends CosemInterfaceObject {

  @CosemAttribute(
      id = 2,
      type = Type.DOUBLE_LONG_UNSIGNED,
      accessMode = AttributeAccessMode.READ_ONLY)
  public DataObject value;

  public MBusReadStatus(final int channel) {
    super(String.format("0.%1$d.24.2.6.255", channel));
    this.value = DataObject.newUInteger32Data(1L);
  }

  public DataObject getValue() {
    return this.value;
  }
}
