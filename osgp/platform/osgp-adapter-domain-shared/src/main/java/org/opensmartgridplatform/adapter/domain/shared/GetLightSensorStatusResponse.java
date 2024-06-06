// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.shared;

import org.opensmartgridplatform.domain.core.valueobjects.LightSensorStatus;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

public class GetLightSensorStatusResponse {

  ResponseMessageResultType result;
  OsgpException osgpException;
  LightSensorStatus lightSensorStatus;

  public GetLightSensorStatusResponse() {
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

  public LightSensorStatus getLightSensorStatus() {
    return this.lightSensorStatus;
  }

  public void setLightSensorStatus(final LightSensorStatus lightSensorStatus) {
    this.lightSensorStatus = lightSensorStatus;
  }
}
