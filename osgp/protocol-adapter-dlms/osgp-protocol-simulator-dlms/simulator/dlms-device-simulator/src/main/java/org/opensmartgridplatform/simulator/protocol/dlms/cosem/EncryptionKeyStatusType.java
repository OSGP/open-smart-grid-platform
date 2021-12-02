/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

public enum EncryptionKeyStatusType {
  NO_ENCRYPTION_KEY(0),
  ENCRYPTION_KEY_SET(1),
  ENCRYPTION_KEY_TRANSFERRED(2),
  ENCRYPTION_KEY_SET_AND_TRANSFERRED(3),
  ENCRYPTION_KEY_IN_USE(4);

  int value;

  EncryptionKeyStatusType(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }
}
