// Copyright 2014-2017 Smart Society Services B.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class GetConfigurationObjectRequest implements Serializable {

  private static final long serialVersionUID = 3107247305216273215L;

  private final String deviceIdentification;

  public GetConfigurationObjectRequest(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }
}
