//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
