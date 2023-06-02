//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.device;

import javax.jms.JMSException;

public interface DeviceResponseHandler {

  void handleResponse(DeviceResponse deviceResponse);

  void handleConnectionFailure(Throwable t, DeviceResponse deviceResponse) throws JMSException;

  void handleException(Throwable t, DeviceResponse deviceResponse);
}
