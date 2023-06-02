//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.device.lmd;

import javax.jms.JMSException;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponseHandler;

public interface LmdDeviceService {

  /**
   * Reads the light sensor status from the device.
   *
   * <p>Returns a {@link GetLightSensorStatusResponse} via the deviceResponseHandler's callback.
   */
  void getLightSensorStatus(
      DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler)
      throws JMSException;
}
