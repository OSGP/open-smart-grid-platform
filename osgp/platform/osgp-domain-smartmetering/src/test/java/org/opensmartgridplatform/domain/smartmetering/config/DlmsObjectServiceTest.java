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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.opensmartgridplatform.domain.smartmetering.service.DlmsObjectService;
import org.opensmartgridplatform.domain.smartmetering.service.DlmsObjectType;
import org.springframework.core.io.ClassPathResource;

@Slf4j
class DlmsObjectServiceTest {

  List<MeterConfig> meterConfigList;
  @InjectMocks private DlmsObjectService dlmsObjectService;

  @BeforeEach
  void setUp() throws IOException {
    this.meterConfigList = this.getMeterConfigList();
    this.dlmsObjectService = new DlmsObjectService(this.meterConfigList);
  }

  @Test
  void testGetCosemObject() {
    final String protocolVersion52 = "5.2";
    final String protocolVersion50 = "5.0";
    final String protocolName = "SMR";

    final Map<DlmsObjectType, CosemObject> cosemObjects52 =
        this.dlmsObjectService.getCosemObjects(protocolName, protocolVersion52);

    assertNotNull(cosemObjects52);
    assertEquals(8, cosemObjects52.size());
    assertNotNull(cosemObjects52.get(DlmsObjectType.PUSH_SCHEDULER));

    final Map<DlmsObjectType, CosemObject> cosemObjects50 =
        this.dlmsObjectService.getCosemObjects(protocolName, protocolVersion50);

    assertNotNull(cosemObjects50);
    assertEquals(7, cosemObjects50.size());
    assertNull(cosemObjects50.get(DlmsObjectType.PUSH_SCHEDULER));
  }

  @Test
  void testNoCosemObjectFound() {
    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.dlmsObjectService.getCosemObjects("ABC", "12");

    assertNull(cosemObjects);
  }

  private List<MeterConfig> getMeterConfigList() throws IOException {
    final ObjectMapper objectMapper = new ObjectMapper();
    final List<MeterConfig> meterConfigList = new ArrayList<>();
    final MeterConfig meterConfig50 =
        objectMapper.readValue(
            new ClassPathResource("/meter-profile-config-SMR-5.0-GetActualMeterReads.json")
                .getFile(),
            MeterConfig.class);
    final MeterConfig meterConfig52 =
        objectMapper.readValue(
            new ClassPathResource("/meter-profile-config-SMR-5.2-GetActualMeterReads.json")
                .getFile(),
            MeterConfig.class);
    meterConfigList.add(meterConfig50);
    meterConfigList.add(meterConfig52);
    return meterConfigList;
  }
}
