// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
