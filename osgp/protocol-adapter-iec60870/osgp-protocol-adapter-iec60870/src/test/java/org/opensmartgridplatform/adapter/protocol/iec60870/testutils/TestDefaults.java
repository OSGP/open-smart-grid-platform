/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.testutils;

public final class TestDefaults {
  public static final String DEFAULT_DEVICE_IDENTIFICATION = "TEST-DEVICE-1";
  public static final String DEFAULT_MESSAGE_TYPE = "CONNECT";
  public static final String DEFAULT_ORGANISATION_IDENTIFICATION = "TEST-ORGANISATION";
  public static final String DEFAULT_CORRELATION_UID = "TEST-CORR-1";
  public static final String DEFAULT_DOMAIN = "DISTRIBUTION_AUTOMATION";
  public static final String DEFAULT_DOMAIN_VERSION = "1.0";

  private TestDefaults() {
    // private constructor to prevent instance creation
  }
}
