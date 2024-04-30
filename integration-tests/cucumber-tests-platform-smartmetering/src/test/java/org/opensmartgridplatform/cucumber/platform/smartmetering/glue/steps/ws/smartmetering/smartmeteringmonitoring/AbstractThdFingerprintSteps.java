// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmonitoring;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ThdFingerprint;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle.BaseBundleSteps;

public class AbstractThdFingerprintSteps extends BaseBundleSteps {

  protected void assertFingerprint(
      final Map<String, String> expectedValues, final ThdFingerprint thdFingerprint) {
    assertThat(thdFingerprint.getThdInstantaneousCurrentL1())
        .isEqualTo(Integer.valueOf(expectedValues.get("THD_CURRENT_L1")));
    assertThat(thdFingerprint.getThdInstantaneousCurrentL2())
        .isEqualTo(Integer.valueOf(expectedValues.get("THD_CURRENT_L2")));
    assertThat(thdFingerprint.getThdInstantaneousCurrentL3())
        .isEqualTo(Integer.valueOf(expectedValues.get("THD_CURRENT_L3")));

    assertThat(thdFingerprint.getThdInstantaneousCurrentFingerprintL1().getFingerprintValue())
        .hasSize(Integer.valueOf(expectedValues.get("THD_FINGERPRINT_L1")));
    assertThat(thdFingerprint.getThdInstantaneousCurrentFingerprintL2().getFingerprintValue())
        .hasSize(Integer.valueOf(expectedValues.get("THD_FINGERPRINT_L2")));
    assertThat(thdFingerprint.getThdInstantaneousCurrentFingerprintL3().getFingerprintValue())
        .hasSize(Integer.valueOf(expectedValues.get("THD_FINGERPRINT_L3")));

    assertThat(thdFingerprint.getThdCurrentOverLimitCounterL1())
        .isEqualTo(Integer.valueOf(expectedValues.get("THD_COUNTER_L1")));
    assertThat(thdFingerprint.getThdCurrentOverLimitCounterL2())
        .isEqualTo(Integer.valueOf(expectedValues.get("THD_COUNTER_L2")));
    assertThat(thdFingerprint.getThdCurrentOverLimitCounterL3())
        .isEqualTo(Integer.valueOf(expectedValues.get("THD_COUNTER_L3")));
  }
}
