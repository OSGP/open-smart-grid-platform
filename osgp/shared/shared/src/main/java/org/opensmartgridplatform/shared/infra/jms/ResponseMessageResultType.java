// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.jms;

public enum ResponseMessageResultType {
  OK("OK"),
  NOT_FOUND("NOT FOUND"),
  NOT_OK("NOT OK");

  private String value;

  private ResponseMessageResultType(final String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
