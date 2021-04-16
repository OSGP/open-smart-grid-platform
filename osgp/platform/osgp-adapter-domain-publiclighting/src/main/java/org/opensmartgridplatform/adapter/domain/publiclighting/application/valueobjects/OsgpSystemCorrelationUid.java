/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
