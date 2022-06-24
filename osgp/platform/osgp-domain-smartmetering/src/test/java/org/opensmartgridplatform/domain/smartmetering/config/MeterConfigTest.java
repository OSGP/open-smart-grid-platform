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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

public class MeterConfigTest {

  @Test
  void loadJsonFile() throws JsonProcessingException {

    final ObjectMapper objectMapper = new ObjectMapper();

    objectMapper.readValue("meter-profile-config-SMR-5.0.json", MeterConfig.class);
  }
}
