/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;

public enum SecurityLevel {
  HLS5,
  HLS4,
  HLS3,
  LLS1,
  LLS0;

  public static SecurityLevel forDevice(final DlmsDevice device) {
    if (device.isHls5Active()) {
      return HLS5;
    }
    if (device.isHls4Active()) {
      return HLS4;
    }
    if (device.isHls3Active()) {
      return HLS3;
    }
    if (device.isLls1Active()) {
      return LLS1;
    }
    return LLS0;
  }
}
