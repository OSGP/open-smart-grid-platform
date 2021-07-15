/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.device;

import javax.jms.JMSException;

public interface DeviceResponseHandler {

  void handleResponse(DeviceResponse deviceResponse);

  void handleConnectionFailure(Throwable t, DeviceResponse deviceResponse) throws JMSException;

  void handleException(Throwable t, DeviceResponse deviceResponse);
}
