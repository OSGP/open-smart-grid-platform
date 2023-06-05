// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.testutils.builders;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.InformationObject;

public class AsduBuilder {

  private final ASduType asduType;
  private CauseOfTransmission causeOfTransmission = CauseOfTransmission.SPONTANEOUS;
  private int commonAddress = 0;
  private int originatorAddress = 0;
  private InformationObject[] informationObjects;

  public AsduBuilder(final ASduType asduType) {
    this.asduType = asduType;
  }

  public static AsduBuilder ofType(final ASduType asduType) {
    return new AsduBuilder(asduType);
  }

  public AsduBuilder withCauseOfTransmission(final CauseOfTransmission causeOfTransmission) {
    this.causeOfTransmission = causeOfTransmission;
    return this;
  }

  public AsduBuilder withCommonAddress(final int commonAddress) {
    this.commonAddress = commonAddress;
    return this;
  }

  public AsduBuilder withOriginatorAddress(final int originatorAddress) {
    this.originatorAddress = originatorAddress;
    return this;
  }

  public AsduBuilder withInformationObjects(final InformationObject... informationObjects) {
    this.informationObjects = informationObjects;
    return this;
  }

  public ASdu build() {
    return new ASdu(
        this.asduType,
        false,
        this.causeOfTransmission,
        false,
        false,
        this.originatorAddress,
        this.commonAddress,
        this.informationObjects);
  }
}
