// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmonitoring;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.FingerprintValues;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ThdFingerprint;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle.BaseBundleSteps;

public class AbstractThdFingerprintSteps extends BaseBundleSteps {

  protected void assertFingerprint(
      final Map<String, String> expectedValues, final ThdFingerprint thdFingerprint) {
    assertValue(expectedValues, "THD_CURRENT_L1", thdFingerprint.getThdInstantaneousCurrentL1());
    assertValue(expectedValues, "THD_CURRENT_L2", thdFingerprint.getThdInstantaneousCurrentL2());
    assertValue(expectedValues, "THD_CURRENT_L3", thdFingerprint.getThdInstantaneousCurrentL3());

    assertSize(
        expectedValues,
        "THD_FINGERPRINT_L1",
        thdFingerprint.getThdInstantaneousCurrentFingerprintL1());
    assertSize(
        expectedValues,
        "THD_FINGERPRINT_L2",
        thdFingerprint.getThdInstantaneousCurrentFingerprintL2());
    assertSize(
        expectedValues,
        "THD_FINGERPRINT_L3",
        thdFingerprint.getThdInstantaneousCurrentFingerprintL3());

    assertValue(expectedValues, "THD_COUNTER_L1", thdFingerprint.getThdCurrentOverLimitCounterL1());
    assertValue(expectedValues, "THD_COUNTER_L2", thdFingerprint.getThdCurrentOverLimitCounterL2());
    assertValue(expectedValues, "THD_COUNTER_L3", thdFingerprint.getThdCurrentOverLimitCounterL3());
  }

  private static void assertValue(
      final Map<String, String> expectedValues, final String key, final Integer value) {
    if (expectedValues.containsKey(key)) {
      assertThat(value).isEqualTo(Integer.valueOf(expectedValues.get(key)));
    } else {
      assertThat(value).isNull();
    }
  }

  private static void assertSize(
      final Map<String, String> expectedValues, final String key, final FingerprintValues value) {
    if (expectedValues.containsKey(key)) {
      assertThat(value.getFingerprintValue()).hasSize(Integer.parseInt(expectedValues.get(key)));
    } else {
      assertThat(value).isNull();
    }
  }
}
