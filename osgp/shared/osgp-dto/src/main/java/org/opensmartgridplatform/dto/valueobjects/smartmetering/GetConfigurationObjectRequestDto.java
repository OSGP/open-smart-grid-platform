// Copyright 2014-2017 Smart Society Services B.V.
// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class GetConfigurationObjectRequestDto implements Serializable {

  private static final long serialVersionUID = -8095383777073559173L;

  private final String deviceIdentification;

  public GetConfigurationObjectRequestDto(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }
}
