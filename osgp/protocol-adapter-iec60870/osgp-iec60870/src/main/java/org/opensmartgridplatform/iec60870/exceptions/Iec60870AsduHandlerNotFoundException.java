//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
