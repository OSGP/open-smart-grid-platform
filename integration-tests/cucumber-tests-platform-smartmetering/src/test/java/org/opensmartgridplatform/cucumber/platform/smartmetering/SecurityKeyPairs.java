/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering;

import java.util.HashMap;
import java.util.Map;

public class SecurityKeyPairs {

  private Map<String, String[]> securityKeyPairs;

  public SecurityKeyPairs() {
    this.securityKeyPairs = new HashMap<>();
  }

  public void addSecurityKeyPair(
      final String name, final String keyInDb, final String keyInRequest) {
    this.securityKeyPairs.put(name, new String[] {keyInDb, keyInRequest});
  }

  public String getDbKey(final String name) {
    return this.getKey(name, 0);
  }

  public String getSoapKey(final String name) {
    return this.getKey(name, 1);
  }

  private String getKey(final String name, final int index) {
    final String[] keyPair = this.securityKeyPairs.get(name);
    if (keyPair == null) {
      throw new IllegalArgumentException("No key pair (DB/SOAP) provided for name " + name);
    }
    return keyPair[index];
  }
}
