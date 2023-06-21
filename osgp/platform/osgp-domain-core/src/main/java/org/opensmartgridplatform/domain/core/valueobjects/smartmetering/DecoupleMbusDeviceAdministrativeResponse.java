// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
