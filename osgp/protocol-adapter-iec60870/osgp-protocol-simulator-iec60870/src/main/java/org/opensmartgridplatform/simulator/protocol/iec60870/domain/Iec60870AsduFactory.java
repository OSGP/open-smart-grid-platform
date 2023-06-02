//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.iec60870.domain;

import java.util.Map;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.IeQualifierOfInterrogation;
import org.openmuc.j60870.ie.InformationElement;
import org.openmuc.j60870.ie.InformationObject;
import org.opensmartgridplatform.iec60870.Iec60870Server;

public interface Iec60870AsduFactory {

  default void initialize() {}

  default IeQualifierOfInterrogation defaultIeQualifierOfInterrogation() {
    final int stationInterrogation = 20;
    return new IeQualifierOfInterrogation(stationInterrogation);
  }

  default ASdu createInterrogationCommandAsdu() {
    return new Iec60870AsduBuilder()
        .withAsduType(ASduType.C_IC_NA_1)
        .withCauseOfTransmission(CauseOfTransmission.ACTIVATION_CON)
        .withInformationObjects(
            new InformationObject[] {
              new InformationObject(
                  0, new InformationElement[][] {{this.defaultIeQualifierOfInterrogation()}})
            })
        .build();
  }

  ASdu createInterrogationCommandResponseAsdu();

  default ASdu createActivationTerminationResponseAsdu() {
    return new Iec60870AsduBuilder()
        .withAsduType(ASduType.C_IC_NA_1)
        .withSequenceOfElements(false)
        .withCauseOfTransmission(CauseOfTransmission.ACTIVATION_TERMINATION)
        .withInformationObjects(
            new InformationObject[] {
              new InformationObject(
                  0, new InformationElement[][] {{this.defaultIeQualifierOfInterrogation()}})
            })
        .build();
  }

  default InformationObject[] processImageToArray(final Map<Integer, InformationElement[][]> map) {
    return map.entrySet().stream()
        .map(entry -> new InformationObject(entry.getKey(), entry.getValue()))
        .toArray(InformationObject[]::new);
  }

  void setIec60870Server(Iec60870Server iec60870Server);
}
