//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects;

public class OsgpSystemCorrelationUid {

  /**
   * Correlation UID used by the system. When sending system messages, this UID is used. Responses
   * for this UID are not sent to the other layer(s) of the system.
   */
  public static final String CORRELATION_UID = "osgp-system-correlation-uid";

  private OsgpSystemCorrelationUid() {
    // Prevent instantiating this class.
  }
}
