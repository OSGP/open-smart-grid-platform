package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetThdFingerprintResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ThdFingerprint;

class ThdFingerprintConverterTest {

  private final MonitoringMapper mapper = new MonitoringMapper();

  @Test
  void convert() {

    final int currentL1 = 31;
    final int currentL2 = 32;
    final int currentL3 = 33;
    final List<Integer> fingerprintL1 = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
    final List<Integer> fingerprintL2 =
        List.of(21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35);
    final List<Integer> fingerprintL3 =
        List.of(41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55);
    final int counterL1 = 101;
    final int counterL2 = 102;
    final int counterL3 = 103;

    final GetThdFingerprintResponse source =
        new GetThdFingerprintResponse(
            new ThdFingerprint(
                currentL1,
                currentL2,
                currentL3,
                fingerprintL1,
                fingerprintL2,
                fingerprintL3,
                counterL1,
                counterL2,
                counterL3));

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetThdFingerprintResponse
        response =
            this.mapper.map(
                source,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle
                    .GetThdFingerprintResponse.class);

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ThdFingerprint
        thdFingerprint = response.getThdFingerprint();
    assertThat(thdFingerprint.getThdInstantaneousCurrentL1()).isEqualTo(currentL1);
    assertThat(thdFingerprint.getThdInstantaneousCurrentL2()).isEqualTo(currentL2);
    assertThat(thdFingerprint.getThdInstantaneousCurrentL3()).isEqualTo(currentL3);
    assertThat(thdFingerprint.getThdInstantaneousCurrentFingerprintL1().getFingerprintValue())
        .isEqualTo(fingerprintL1);
    assertThat(thdFingerprint.getThdInstantaneousCurrentFingerprintL2().getFingerprintValue())
        .isEqualTo(fingerprintL2);
    assertThat(thdFingerprint.getThdInstantaneousCurrentFingerprintL3().getFingerprintValue())
        .isEqualTo(fingerprintL3);
    assertThat(thdFingerprint.getThdCurrentOverLimitCounterL1()).isEqualTo(counterL1);
    assertThat(thdFingerprint.getThdCurrentOverLimitCounterL2()).isEqualTo(counterL2);
    assertThat(thdFingerprint.getThdCurrentOverLimitCounterL3()).isEqualTo(counterL3);
  }
}
