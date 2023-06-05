// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.mqtt;

public class Default {
  public static final String BROKER_HOST = "127.0.0.1";
  public static final int BROKER_PORT = 8883;
  public static final Boolean CLEAN_SESSION = true;
  public static final int KEEP_ALIVE = 60;

  private Default() {
    // do not instantiate utility class
  }
}
