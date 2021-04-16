/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.server;

import org.mockito.ArgumentMatcher;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;

public class AsduTypeArgumentMatcher implements ArgumentMatcher<ASdu> {

  private final ASduType type;

  private CauseOfTransmission causeOfTransmission;

  public AsduTypeArgumentMatcher(final ASduType type) {
    this.type = type;
  }

  public AsduTypeArgumentMatcher(
      final ASduType type, final CauseOfTransmission causeOfTransmission) {
    super();
    this.type = type;
    this.causeOfTransmission = causeOfTransmission;
  }

  @Override
  public boolean matches(final ASdu argument) {
    if (this.causeOfTransmission == null) {
      return argument.getTypeIdentification() == this.type;
    }
    return argument.getTypeIdentification() == this.type
        && argument.getCauseOfTransmission() == this.causeOfTransmission;
  }
}
