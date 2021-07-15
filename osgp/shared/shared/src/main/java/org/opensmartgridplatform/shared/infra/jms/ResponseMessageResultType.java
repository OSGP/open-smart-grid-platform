/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
