// Copyright 2020 Alliander N.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;

public class LightSensorStatus implements Status, Serializable {

  private static final long serialVersionUID = -6385082207732463078L;

  private final LightSensorStatusType status;

  public LightSensorStatus(final LightSensorStatusType status) {
    this.status = status;
  }

  public LightSensorStatusType getStatus() {
    return this.status;
  }
}
