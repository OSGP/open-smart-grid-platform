/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.RequestMetadata;

public class RequestMetadataFactory {

  private static final String DEFAULT_IP_ADDRESS = "localhost";

  public static RequestMetadata forDevice(final String deviceIdentification) {
    return RequestMetadata.newBuilder()
        .deviceIdentification(deviceIdentification)
        .ipAddress(DEFAULT_IP_ADDRESS)
        .build();
  }
}
