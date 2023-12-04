// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.objectconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.opensmartgridplatform.dlms.services.Protocol;
import org.springframework.core.io.ClassPathResource;

@Slf4j
class DlmsProfileTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void init() {
    this.objectMapper = new ObjectMapper();
  }

  @ParameterizedTest
  @CsvSource({
    "DSMR_2_2,dlmsprofile-dsmr22.json",
    "DSMR_4_2_2,dlmsprofile-dsmr422.json",
    "SMR_5_0_0,dlmsprofile-smr500.json",
  })
  void loadJsonFile(final String enumName, final String fileName) throws IOException {

    final DlmsProfile dlmsProfile =
        this.objectMapper.readValue(
            new ClassPathResource("/dlmsprofiles/" + fileName).getFile(), DlmsProfile.class);

    Assertions.assertNotNull(dlmsProfile);
    AssertionsForInterfaceTypes.assertThat(dlmsProfile.getObjects())
        .hasSize(Protocol.valueOf(enumName).getNrOfCosemObjects());
  }
}
