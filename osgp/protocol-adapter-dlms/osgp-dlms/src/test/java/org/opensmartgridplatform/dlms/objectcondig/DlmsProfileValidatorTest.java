// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectcondig;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
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
            new ClassPathResource("/dlmsprofile-smr50.json").getFile(), DlmsProfile.class);
    final DlmsProfile dlmsProfile51 =
        objectMapper.readValue(
            new ClassPathResource("/dlmsprofile-smr51.json").getFile(), DlmsProfile.class);

    DlmsProfileValidator.validate(Arrays.asList(dlmsProfile50, dlmsProfile51));
  }

  @Test
  void testProfileWithMissingUnit() throws IOException {

    final ObjectMapper objectMapper = new ObjectMapper();
    final DlmsProfile dlmsProfile =
        objectMapper.readValue(
            new ClassPathResource("/dlmsprofile-smr50-missingUnit.json").getFile(),
            DlmsProfile.class);

    try {
      DlmsProfileValidator.validate(Collections.singletonList(dlmsProfile));
    } catch (final ObjectConfigException e) {
      assertThat(e.getMessage())
          .isEqualTo(
              "DlmsProfile SMR 5.0.0 register validation error: Register(s) without scaler_unit: AVERAGE_ACTIVE_POWER_IMPORT_L2");
    }
  }

  @Test
  void testProfileWithPQProfileErrors() throws IOException {

    final ObjectMapper objectMapper = new ObjectMapper();
    final DlmsProfile dlmsProfile =
        objectMapper.readValue(
            new ClassPathResource("/dlmsprofile-smr50-PQProfileErrors.json").getFile(),
            DlmsProfile.class);

    try {
      DlmsProfileValidator.validate(Collections.singletonList(dlmsProfile));
    } catch (final ObjectConfigException e) {
      assertThat(e.getMessage())
          .isEqualTo(
              "DlmsProfile SMR 5.0.0 PQ validation error: AVERAGE_ACTIVE_POWER_IMPORT_L1 doesn't contain PQ Profile, PQ Profile POWER_QUALITY_PROFILE_2 has no selectable objects");
    }
  }
}
