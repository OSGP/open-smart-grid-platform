/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.testutils.matchers.asdu;

import org.mockito.ArgumentMatcher;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;

public class InterrogationActivationAsduMatcher implements ArgumentMatcher<ASdu> {

  @Override
  public boolean matches(final ASdu asdu) {
    if (ASduType.C_IC_NA_1 != asdu.getTypeIdentification()) {
      return false;
    }
    if (CauseOfTransmission.ACTIVATION != asdu.getCauseOfTransmission()) {
      return false;
    }
    return true;
  }
}
