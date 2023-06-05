// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.InformationObject;

public class AsduFactory {

  public static ASdu ofType(final ASduType asduType) {
    return new Builder(asduType).build();
  }

  private static class Builder {
    private final ASduType asduType;
    private final boolean isSequenceOfElements = false;
    private final CauseOfTransmission causeOfTransmission = CauseOfTransmission.SPONTANEOUS;
    private final boolean test = false;
    private final boolean negativeConfirm = false;
    private final int originatorAddress = 0;
    private final int commonAddress = 1;
    private final InformationObject[] informationObjects = new InformationObject[] {};

    public Builder(final ASduType asduType) {
      this.asduType = asduType;
    }

    public ASdu build() {
      return new ASdu(
          this.asduType,
          this.isSequenceOfElements,
          this.causeOfTransmission,
          this.test,
          this.negativeConfirm,
          this.originatorAddress,
          this.commonAddress,
          this.informationObjects);
    }
  }
}
