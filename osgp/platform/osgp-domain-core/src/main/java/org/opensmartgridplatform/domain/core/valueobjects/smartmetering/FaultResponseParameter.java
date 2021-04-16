/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class FaultResponseParameter implements Serializable {

  private static final long serialVersionUID = -551244954351120677L;

  private final String key;
  private final String value;

  public FaultResponseParameter(final String key, final String value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public String toString() {
    return "[" + this.key + " => " + this.value + "]";
  }

  public String getKey() {
    return this.key;
  }

  public String getValue() {
    return this.value;
  }
}
