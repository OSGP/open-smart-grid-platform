/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */

package org.opensmartgridplatform.dlms.objectcondig;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.dlms.objectconfig.DlmsProfile;
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
            new ClassPathResource("/dlmsprofile-smr50.json").getFile(), DlmsProfile.class);

    assertNotNull(dlmsProfile);
    assertThat(dlmsProfile.objects).hasSize(13);
  }
}
