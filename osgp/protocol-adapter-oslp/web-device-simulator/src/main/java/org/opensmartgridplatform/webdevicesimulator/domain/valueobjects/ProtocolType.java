/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator.domain.valueobjects;

public enum ProtocolType {
  OSLP("OSLP"),
  OSLP_ELSTER("OSLP_ELSTER");

  private final String value;

  private ProtocolType(final String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
