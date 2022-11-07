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

package org.opensmartgridplatform.dlms.services;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.objectconfig.CosemObject;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dlms.objectconfig.DlmsProfile;
import org.springframework.core.io.ClassPathResource;

@Slf4j
class ObjectConfigServiceTest {

  private ObjectConfigService objectConfigService;

  @BeforeEach
  void setUp() throws IOException, ObjectConfigException {
    final List<DlmsProfile> dlmsProfileList = this.getDlmsProfileList();
    this.objectConfigService = new ObjectConfigService(dlmsProfileList);
  }

  @Test
  void testGetCosemObjects() throws ObjectConfigException {
    final String protocolName = "SMR";
    final String protocolVersion50 = "5.0";
    final String protocolVersion51 = "5.1";

    final Map<DlmsObjectType, CosemObject> cosemObjects50 =
        this.objectConfigService.getCosemObjects(protocolName, protocolVersion50);

    assertNotNull(cosemObjects50);
    assertThat(cosemObjects50).hasSize(13);
    assertNull(cosemObjects50.get(DlmsObjectType.NUMBER_OF_POWER_FAILURES));

    final Map<DlmsObjectType, CosemObject> cosemObjects51 =
        this.objectConfigService.getCosemObjects(protocolName, protocolVersion51);

    assertNotNull(cosemObjects51);
    assertThat(cosemObjects51).hasSize(14);
    assertNotNull(cosemObjects51.get(DlmsObjectType.NUMBER_OF_POWER_FAILURES));
  }

  @Test
  void testNoCosemObjectsFound() throws ObjectConfigException {
    final Map<DlmsObjectType, CosemObject> cosemObjects =
        this.objectConfigService.getCosemObjects("ABC", "12");

    assertTrue(cosemObjects.isEmpty());
  }

  @Test
  void testGetCosemObject() throws ObjectConfigException {
    final String protocolVersion50 = "5.0";
    final String protocolName = "SMR";

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          this.objectConfigService.getCosemObject(
              protocolName, protocolVersion50, DlmsObjectType.AVERAGE_CURRENT_L1);
        });

    final CosemObject cosemObject =
        this.objectConfigService.getCosemObject(
            protocolName, protocolVersion50, DlmsObjectType.AVERAGE_VOLTAGE_L1);

    assertNotNull(cosemObject);
    assertThat(cosemObject.getTag()).isEqualTo(DlmsObjectType.AVERAGE_VOLTAGE_L1.value());
  }

  private List<DlmsProfile> getDlmsProfileList() throws IOException {
    final ObjectMapper objectMapper = new ObjectMapper();
    final List<DlmsProfile> DlmsProfileList = new ArrayList<>();
    final DlmsProfile dlmsProfile50 =
        objectMapper.readValue(
            new ClassPathResource("/dlmsprofile-smr50.json").getFile(), DlmsProfile.class);
    final DlmsProfile dlmsProfile51 =
        objectMapper.readValue(
            new ClassPathResource("/dlmsprofile-smr51.json").getFile(), DlmsProfile.class);
    DlmsProfileList.add(dlmsProfile50);
    DlmsProfileList.add(dlmsProfile51);
    return DlmsProfileList;
  }
}
