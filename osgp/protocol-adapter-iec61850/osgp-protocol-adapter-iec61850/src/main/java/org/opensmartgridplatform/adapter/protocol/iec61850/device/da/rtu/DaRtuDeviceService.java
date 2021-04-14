/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.device.da.rtu;

import javax.jms.JMSException;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.DaRtuDeviceRequestMessageProcessor;

public interface DaRtuDeviceService {
  /**
   * Executes the function in messageProcessor to fill the ResponseDTO, returns a Response DTO via
   * the message processor callback function
   */
  void getData(
      DaDeviceRequest deviceRequest,
      DeviceResponseHandler deviceResponseHandler,
      DaRtuDeviceRequestMessageProcessor messageProcessor)
      throws JMSException;
}
