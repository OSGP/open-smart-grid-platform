package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetThdFingerprintResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ThdFingerprint;

class ThdFingerprintConverterTest {

  private final MonitoringMapper mapper = new MonitoringMapper();

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

  @Test
  void convertBundle() {

    final GetThdFingerprintResponse source = this.createResponse();

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetThdFingerprintResponse
        response =
            this.mapper.map(
                source,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle
                    .GetThdFingerprintResponse.class);

    assertThat(response).isNotNull();

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ThdFingerprint
        thdFingerprint = response.getThdFingerprint();

    this.assertFingerprint(thdFingerprint);
  }

  @Test
  void convertBundleWhenResponseNull() {

    final GetThdFingerprintResponse source = null;

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetThdFingerprintResponse
        response =
            this.mapper.map(
                source,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle
                    .GetThdFingerprintResponse.class);

    assertThat(response).isNull();
  }

  @Test
  void convertBundleWhenFingerprintNull() {

    final GetThdFingerprintResponse source = this.createResponseWithoutFingerprint();

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetThdFingerprintResponse
        response =
            this.mapper.map(
                source,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle
                    .GetThdFingerprintResponse.class);

    assertThat(response).isNotNull();
    assertThat(response.getThdFingerprint()).isNull();
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void convertSingle(final boolean polyphase) {

    final GetThdFingerprintResponse source = this.createResponse();

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .GetThdFingerprintResponse
        response =
            this.mapper.map(
                source,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
                    .GetThdFingerprintResponse.class);

    assertThat(response).isNotNull();

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ThdFingerprint
        thdFingerprint = response.getThdFingerprint();

    this.assertFingerprint(thdFingerprint);
  }

  @Test
  void convertSingleWhenResponseNull() {

    final GetThdFingerprintResponse source = null;

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .GetThdFingerprintResponse
        response =
            this.mapper.map(
                source,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
                    .GetThdFingerprintResponse.class);

    assertThat(response).isNull();
  }

  @Test
  void convertSingleWhenFingerprintNull() {

    final GetThdFingerprintResponse source = this.createResponseWithoutFingerprint();

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .GetThdFingerprintResponse
        response =
            this.mapper.map(
                source,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
                    .GetThdFingerprintResponse.class);

    assertThat(response).isNotNull();
    assertThat(response.getThdFingerprint()).isNull();
  }

  private GetThdFingerprintResponse createResponse() {
    final GetThdFingerprintResponse source =
        new GetThdFingerprintResponse(
            new ThdFingerprint(
                this.currentL1,
                this.currentL2,
                this.currentL3,
                this.fingerprintL1,
                this.fingerprintL2,
                this.fingerprintL3,
                this.counterL1,
                this.counterL2,
                this.counterL3));
    return source;
  }

  private GetThdFingerprintResponse createResponseWithoutFingerprint() {
    final GetThdFingerprintResponse source = new GetThdFingerprintResponse((ThdFingerprint) null);
    return source;
  }

  private void assertFingerprint(
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ThdFingerprint
          thdFingerprint) {
    assertThat(thdFingerprint.getThdInstantaneousCurrentL1()).isEqualTo(this.currentL1);
    assertThat(thdFingerprint.getThdInstantaneousCurrentL2()).isEqualTo(this.currentL2);
    assertThat(thdFingerprint.getThdInstantaneousCurrentL3()).isEqualTo(this.currentL3);
    assertThat(thdFingerprint.getThdInstantaneousCurrentFingerprintL1().getFingerprintValue())
        .isEqualTo(this.fingerprintL1);
    assertThat(thdFingerprint.getThdInstantaneousCurrentFingerprintL2().getFingerprintValue())
        .isEqualTo(this.fingerprintL2);
    assertThat(thdFingerprint.getThdInstantaneousCurrentFingerprintL3().getFingerprintValue())
        .isEqualTo(this.fingerprintL3);
    assertThat(thdFingerprint.getThdCurrentOverLimitCounterL1()).isEqualTo(this.counterL1);
    assertThat(thdFingerprint.getThdCurrentOverLimitCounterL2()).isEqualTo(this.counterL2);
    assertThat(thdFingerprint.getThdCurrentOverLimitCounterL3()).isEqualTo(this.counterL3);
  }
}
