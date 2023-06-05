// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
