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
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.dlms.services.Protocol;
import org.springframework.core.io.ClassPathResource;

@Slf4j
class DlmsProfileTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void init() {
    this.objectMapper = new ObjectMapper();
  }

  @Test
  void loadJsonFile() throws IOException {

    final DlmsProfile dlmsProfile =
        this.objectMapper.readValue(
            new ClassPathResource("/dlmsprofiles/dlmsprofile-smr500.json").getFile(),
            DlmsProfile.class);

    Assertions.assertNotNull(dlmsProfile);
    AssertionsForInterfaceTypes.assertThat(dlmsProfile.getObjects())
        .hasSize(Protocol.SMR_5_0_0.getNrOfCosemObjects());
  }
}
