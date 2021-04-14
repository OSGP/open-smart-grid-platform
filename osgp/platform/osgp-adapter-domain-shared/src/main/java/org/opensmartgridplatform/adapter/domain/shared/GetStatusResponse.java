/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.shared;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatusMapped;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

public class GetStatusResponse {
  ResponseMessageResultType result;
  OsgpException osgpException;
  DeviceStatusMapped deviceStatusMapped;

  public GetStatusResponse() {
    // Empty constructor.
  }

  public ResponseMessageResultType getResult() {
    return this.result;
  }

  public void setResult(final ResponseMessageResultType result) {
    this.result = result;
  }

  public OsgpException getOsgpException() {
    return this.osgpException;
  }

  public void setOsgpException(final OsgpException osgpException) {
    this.osgpException = osgpException;
  }

  public DeviceStatusMapped getDeviceStatusMapped() {
    return this.deviceStatusMapped;
  }

  public void setDeviceStatusMapped(final DeviceStatusMapped deviceStatusMapped) {
    this.deviceStatusMapped = deviceStatusMapped;
  }
}
