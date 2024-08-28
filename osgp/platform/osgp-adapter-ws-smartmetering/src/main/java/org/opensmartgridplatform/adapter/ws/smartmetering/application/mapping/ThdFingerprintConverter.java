// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.FingerprintValues;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ThdFingerprint;

@Slf4j
public class ThdFingerprintConverter {

  private ThdFingerprintConverter() {
    //    hide the implicit public constructor for this utility class
  }

  public static ThdFingerprint getThdFingerprint(
      final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ThdFingerprint
          sourceThdFingerprint) {
    if (sourceThdFingerprint == null) {
      return null;
    }
    final ThdFingerprint dest = new ThdFingerprint();
    dest.setThdInstantaneousCurrentL1(sourceThdFingerprint.getThdInstantaneousCurrentL1());
    dest.setThdInstantaneousCurrentL2(sourceThdFingerprint.getThdInstantaneousCurrentL2());
    dest.setThdInstantaneousCurrentL3(sourceThdFingerprint.getThdInstantaneousCurrentL3());
    final FingerprintValues fingerprintValuesL1 = new FingerprintValues();
    fingerprintValuesL1
        .getFingerprintValue()
        .addAll(sourceThdFingerprint.getThdInstantaneousCurrentFingerprintL1());
    dest.setThdInstantaneousCurrentFingerprintL1(fingerprintValuesL1);
    if (sourceThdFingerprint.getThdInstantaneousCurrentFingerprintL2() != null) {
      final FingerprintValues fingerprintValuesL2 = new FingerprintValues();
      fingerprintValuesL2
          .getFingerprintValue()
          .addAll(sourceThdFingerprint.getThdInstantaneousCurrentFingerprintL2());
      dest.setThdInstantaneousCurrentFingerprintL2(fingerprintValuesL2);
    }
    if (sourceThdFingerprint.getThdInstantaneousCurrentFingerprintL3() != null) {
      final FingerprintValues fingerprintValuesL3 = new FingerprintValues();
      fingerprintValuesL3
          .getFingerprintValue()
          .addAll(sourceThdFingerprint.getThdInstantaneousCurrentFingerprintL3());
      dest.setThdInstantaneousCurrentFingerprintL3(fingerprintValuesL3);
    }
    dest.setThdCurrentOverLimitCounterL1(sourceThdFingerprint.getThdCurrentOverLimitCounterL1());
    dest.setThdCurrentOverLimitCounterL2(sourceThdFingerprint.getThdCurrentOverLimitCounterL2());
    dest.setThdCurrentOverLimitCounterL3(sourceThdFingerprint.getThdCurrentOverLimitCounterL3());
    return dest;
  }
}
