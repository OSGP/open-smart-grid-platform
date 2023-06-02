//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.iec60870.domain;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.InformationObject;

public class Iec60870AsduBuilder {

  private ASduType asduType = ASduType.M_ME_NB_1;
  private boolean sequenceOfElements = false;
  private CauseOfTransmission causeOfTransmission = CauseOfTransmission.SPONTANEOUS;
  private boolean test = false;
  private boolean negativeConfirm = false;
  private int originatorAddress = 0;
  private int commonAddress = 1;
  private InformationObject[] informationObjects;

  public Iec60870AsduBuilder withAsduType(final ASduType asduType) {
    this.asduType = asduType;
    return this;
  }

  public Iec60870AsduBuilder withSequenceOfElements(final boolean sequenceOfElements) {
    this.sequenceOfElements = sequenceOfElements;
    return this;
  }

  public Iec60870AsduBuilder withCauseOfTransmission(
      final CauseOfTransmission causeOfTransmission) {
    this.causeOfTransmission = causeOfTransmission;
    return this;
  }

  public Iec60870AsduBuilder withTest(final boolean test) {
    this.test = test;
    return this;
  }

  public Iec60870AsduBuilder withNegativeConfirm(final boolean negativeConfirm) {
    this.negativeConfirm = negativeConfirm;
    return this;
  }

  public Iec60870AsduBuilder withOriginatorAddress(final int originatorAddress) {
    this.originatorAddress = originatorAddress;
    return this;
  }

  public Iec60870AsduBuilder withCommonAddress(final int commonAddress) {
    this.commonAddress = commonAddress;
    return this;
  }

  public Iec60870AsduBuilder withInformationObjects(final InformationObject[] informationObjects) {
    this.informationObjects = informationObjects.clone();
    return this;
  }

  public ASdu build() {
    return new ASdu(
        this.asduType,
        this.sequenceOfElements,
        this.causeOfTransmission,
        this.test,
        this.negativeConfirm,
        this.originatorAddress,
        this.commonAddress,
        this.informationObjects);
  }
}
