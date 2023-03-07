/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import lombok.Getter;

@Getter
public class DecoupleMbusDeviceAdministrativeResponse extends ActionResponse
    implements Serializable {

  private final String mbusDeviceIdentification;

  public DecoupleMbusDeviceAdministrativeResponse(final String mbusDeviceIdentification) {
    super(OsgpResultType.OK, null, "Decouple Mbus Device Administrative was successful");
    this.mbusDeviceIdentification = mbusDeviceIdentification;
  }

  @Override
  public String toString() {
    return "DecoupleMbusDeviceAdministrativeResponse [mbusDeviceIdentification="
        + this.mbusDeviceIdentification
        + "]";
  }
}
