/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.builders;

import java.text.ParseException;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetActualPowerQualityRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class GetActualPowerQualityRequestBuilder {

  private String profileType;

  public GetActualPowerQualityRequestBuilder fromParameterMap(
      final Map<String, String> parameterMap) throws ParseException {

    this.profileType = parameterMap.get(PlatformSmartmeteringKeys.KEY_POWER_QUALITY_PROFILE_TYPE);

    return this;
  }

  public GetActualPowerQualityRequestBuilder withProfileType(final String profileType) {
    this.profileType = profileType;
    return this;
  }

  public GetActualPowerQualityRequest build() {
    final GetActualPowerQualityRequest request = new GetActualPowerQualityRequest();
    request.setProfileType(this.profileType);
    return request;
  }
}
