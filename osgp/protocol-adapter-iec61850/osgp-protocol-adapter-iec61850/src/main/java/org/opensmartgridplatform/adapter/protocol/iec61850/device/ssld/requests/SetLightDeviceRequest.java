//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.LightValueMessageDataContainerDto;

public class SetLightDeviceRequest extends DeviceRequest {

  private LightValueMessageDataContainerDto lightValuesContainer;

  public SetLightDeviceRequest(
      final Builder deviceRequestBuilder,
      final LightValueMessageDataContainerDto lightValuesContainer) {
    super(deviceRequestBuilder);
    this.lightValuesContainer = lightValuesContainer;
  }

  public LightValueMessageDataContainerDto getLightValuesContainer() {
    return this.lightValuesContainer;
  }
}
