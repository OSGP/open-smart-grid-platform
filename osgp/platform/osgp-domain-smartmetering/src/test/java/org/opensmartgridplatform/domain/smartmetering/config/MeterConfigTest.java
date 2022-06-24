/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.domain.smartmetering.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MeterConfigTest {

  @Test
  void loadJsonFile() throws IOException {

    final ObjectMapper objectMapper = new ObjectMapper();

    final MeterConfig meterConfig =
        objectMapper.readValue(
            Paths.get("meter-profile-config-SMR-5.0.json").toFile(), MeterConfig.class);

    Assertions.assertNotNull(meterConfig);
  }
}
