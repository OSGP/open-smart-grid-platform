/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
