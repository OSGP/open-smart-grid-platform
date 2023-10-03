// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectcondig;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.DlmsProfile;
import org.opensmartgridplatform.dlms.objectconfig.DlmsProfileValidator;
import org.springframework.core.io.ClassPathResource;

@Slf4j
class DlmsProfileValidatorTest {

  @Test
  void testValidProfiles() throws IOException, ObjectConfigException {

    final ObjectMapper objectMapper = new ObjectMapper();
    final DlmsProfile dlmsProfile50 =
        objectMapper.readValue(
            new ClassPathResource("/dlmsprofiles/dlmsprofile-smr500.json").getFile(),
            DlmsProfile.class);
    final DlmsProfile dlmsProfile51 =
        objectMapper.readValue(
            new ClassPathResource("/dlmsprofiles/dlmsprofile-smr51.json").getFile(),
            DlmsProfile.class);

    DlmsProfileValidator.validate(Arrays.asList(dlmsProfile50, dlmsProfile51));
  }

  static Stream<Arguments> errorCases() {
    final List<Arguments> errorCases =
        List.of(
            Arguments.of(
                "/dlmsprofile-smr50-missingUnit.json",
                "DlmsProfile SMR 5.0.0 register validation error: Register(s) without scaler_unit: AVERAGE_ACTIVE_POWER_IMPORT_L2"),
            Arguments.of(
                "/dlmsprofile-smr50-PQProfileErrors.json",
                "DlmsProfile SMR 5.0.0 PQ validation error: AVERAGE_ACTIVE_POWER_IMPORT_L1 doesn't contain PQ Profile, PQ Profile POWER_QUALITY_PROFILE_2 has no selectable objects"),
            Arguments.of(
                "/dlmsprofile-smr50-missingCaptureObject.json",
                "DlmsProfile SMR 5.0.0 Capture objects validation error: Profile doesn't contain object for MBUS_MASTER_VALUE"));

    return errorCases.stream();
  }

  @ParameterizedTest
  @MethodSource("errorCases")
  void testProfileWithError(final String fileName, final String expectedError) throws IOException {

    final ObjectMapper objectMapper = new ObjectMapper();
    final DlmsProfile dlmsProfile =
        objectMapper.readValue(new ClassPathResource(fileName).getFile(), DlmsProfile.class);

    try {
      DlmsProfileValidator.validate(Collections.singletonList(dlmsProfile));
    } catch (final ObjectConfigException e) {
      assertThat(e.getMessage()).isEqualTo(expectedError);
    }
  }
}
