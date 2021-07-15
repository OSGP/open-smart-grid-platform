/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
