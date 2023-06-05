// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;

import java.util.Collections;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetAdministrativeStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class SetAdministrativeStatusRequestBuilder {

  private static final AdministrativeStatusType DEFAULT_STATUS_TYPE = AdministrativeStatusType.ON;

  private AdministrativeStatusType statusType;

  public SetAdministrativeStatusRequestBuilder withDefaults() {
    return this.fromParameterMap(Collections.emptyMap());
  }

  public SetAdministrativeStatusRequestBuilder fromParameterMap(
      final Map<String, String> parameters) {
    this.statusType = this.getAdministrativeStatusType(parameters);
    return this;
  }

  public SetAdministrativeStatusRequest build() {
    final SetAdministrativeStatusRequest request = new SetAdministrativeStatusRequest();
    request.setAdministrativeStatusType(this.statusType);
    return request;
  }

  private AdministrativeStatusType getAdministrativeStatusType(
      final Map<String, String> parameters) {
    return getEnum(
        parameters,
        PlatformSmartmeteringKeys.ADMINISTRATIVE_STATUS_TYPE,
        AdministrativeStatusType.class,
        DEFAULT_STATUS_TYPE);
  }
}
