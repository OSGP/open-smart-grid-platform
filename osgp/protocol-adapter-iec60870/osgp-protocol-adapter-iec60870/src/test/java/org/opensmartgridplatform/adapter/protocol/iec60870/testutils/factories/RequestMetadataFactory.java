//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
