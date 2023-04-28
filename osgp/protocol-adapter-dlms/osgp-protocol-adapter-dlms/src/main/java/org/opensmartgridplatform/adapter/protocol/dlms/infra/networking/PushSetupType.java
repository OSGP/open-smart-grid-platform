/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */

package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

public enum PushSetupType {
  TCP(1),
  UDP(3);

  private final int bit;

  PushSetupType(final int bit) {
    this.bit = bit;
  }
}
