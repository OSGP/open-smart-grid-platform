// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Collections;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetMbusEncryptionKeyStatusRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class GetMbusEncryptionKeyStatusRequestBuilder {

  private static final String DEFAULT_MBUS_DEVICE_IDENTIFICATION = "TESTG102400000001";

  private String mbusDeviceIdentification;

  public GetMbusEncryptionKeyStatusRequestBuilder withDefaults() {
    return this.fromParameterMap(Collections.emptyMap());
  }

  public GetMbusEncryptionKeyStatusRequestBuilder fromParameterMap(
      final Map<String, String> parameters) {
    this.mbusDeviceIdentification = this.getMbusDeviceIdentification(parameters);
    return this;
  }

  public GetMbusEncryptionKeyStatusRequest build() {
    final GetMbusEncryptionKeyStatusRequest request = new GetMbusEncryptionKeyStatusRequest();
    request.setMbusDeviceIdentification(this.mbusDeviceIdentification);
    return request;
  }

  private String getMbusDeviceIdentification(final Map<String, String> parameters) {
    return getString(
        parameters,
        PlatformSmartmeteringKeys.MBUS_DEVICE_IDENTIFICATION,
        DEFAULT_MBUS_DEVICE_IDENTIFICATION);
  }
}
