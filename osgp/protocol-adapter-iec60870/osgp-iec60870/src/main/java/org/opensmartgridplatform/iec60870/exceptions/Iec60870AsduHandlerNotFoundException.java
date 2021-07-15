/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.iec60870.exceptions;

import org.openmuc.j60870.ASduType;

public class Iec60870AsduHandlerNotFoundException extends Exception {

  private static final long serialVersionUID = 1L;
  private final ASduType asduType;

  public Iec60870AsduHandlerNotFoundException(final ASduType asduType) {
    super();
    this.asduType = asduType;
  }

  public ASduType getAsduType() {
    return this.asduType;
  }
}
