// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.springframework.core.io.ClassPathResource;

@Slf4j
class DlmsProfileValidatorTest {

  @Test
  void testValidProfiles() throws IOException, ObjectConfigException {

    final ObjectConfigService objectConfigService = new ObjectConfigService();
    final DlmsProfile dlmsProfile42 = objectConfigService.getDlmsProfile("DSMR", "4.2.2");
    final DlmsProfile dlmsProfile43 = objectConfigService.getDlmsProfile("SMR", "4.3");
    final DlmsProfile dlmsProfile50 = objectConfigService.getDlmsProfile("SMR", "5.0.0");
    final DlmsProfile dlmsProfile51 = objectConfigService.getDlmsProfile("SMR", "5.1");
    final DlmsProfile dlmsProfile52 = objectConfigService.getDlmsProfile("SMR", "5.2");
    final DlmsProfile dlmsProfile55 = objectConfigService.getDlmsProfile("SMR", "5.5");
    DlmsProfileValidator.validate(
        Arrays.asList(
            dlmsProfile42,
            dlmsProfile43,
            dlmsProfile50,
            dlmsProfile51,
            dlmsProfile52,
            dlmsProfile55));
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

  @Test
  void testAllValueTypesFixedInProfileHaveAValue() throws IOException, ObjectConfigException {

    final ObjectConfigService objectConfigService = new ObjectConfigService();
    final List<DlmsProfile> dlmsProfiles = objectConfigService.dlmsProfiles;

    final List<DlmsProfile> fixedWithoutValueList =
        dlmsProfiles.stream()
            .filter(
                profile ->
                    profile.getObjects().stream()
                        .anyMatch(
                            object ->
                                object.getAttributes().stream()
                                    .anyMatch(
                                        attribute ->
                                            attribute.getValuetype() == ValueType.FIXED_IN_PROFILE
                                                && attribute.getValue() == null)))
            .toList();

    Assertions.assertThat(fixedWithoutValueList).isEmpty();
  }
}
