// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu;

import javax.jms.JMSException;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceMessageStatus;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.requests.GetDataDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.requests.SetDataDeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses.GetDataDeviceResponse;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataRequestDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SetDataRequestDto;

public interface RtuDeviceService {
  /**
   * Reads the {@link GetDataRequestDto} from the device, returns a {@link GetDataDeviceResponse}
   * via the deviceResponseHandler's callback.
   */
  void getData(GetDataDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler)
      throws JMSException;

  /**
   * Writes the {@link SetDataRequestDto} to the device, returns a {@link DeviceMessageStatus} via
   * the deviceResponseHandler's callback.
   *
   * @throws JMSException
   */
  void setData(SetDataDeviceRequest deviceRequest, DeviceResponseHandler deviceResponseHandler)
      throws JMSException;
}
