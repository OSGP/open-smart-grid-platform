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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

@Slf4j
class MeterConfigTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void init() {
    this.objectMapper = new ObjectMapper();
  }

  @Test
  void loadJsonFile() throws IOException {

    final MeterConfig meterConfig =
        this.objectMapper.readValue(
            new ClassPathResource("/meter-profile-config-SMR-5.0.json").getFile(),
            MeterConfig.class);

    Assertions.assertNotNull(meterConfig);
    Assertions.assertEquals(
        "MeterConfig(profile=SMR, version=5.0, description=Profile for Smart Meter Requirements 5.0, settings=[Setting(firmwareUpdateType=SMR)], cosemObjects=[CosemObject(tag=CLOCK, description=Clock, classId=8, version=0, obis=0-0:1.0.0.255, group=ABSTRACT, meterTypes=[SP, PP], attributes=[Attribute(id=2, description=time, datatype=octet-string, valuetype=DYNAMIC, value=CURRENT_LOCAL_DATE_AND_TIME, access=RW), Attribute(id=3, description=time_zone, datatype=long, valuetype=FIXED_IN_PROFILE, value=-60, access=RW), Attribute(id=4, description=status, datatype=clock_status, valuetype=DYNAMIC, value=OK, access=R)]), CosemObject(tag=ACTIVE_FIRMWARE_VERSION, description=Active firmware version, classId=1, version=0, obis=1-0:0.2.0.255, group=ABSTRACT, meterTypes=[SP, PP], attributes=[Attribute(id=2, description=value, datatype=octet-string, valuetype=FIXED_IN_METER, value=firmware id 1, access=R)]), CosemObject(tag=ACTIVE_ENERGY_IMPORT, description=Active energy import (+A), classId=3, version=0, obis=1-0:1.8.0.255, group=ELECTRICITY, meterTypes=[SP, PP], attributes=[Attribute(id=2, description=value, datatype=double-long-unsigned, valuetype=DYNAMIC, value=100, access=R), Attribute(id=3, description=scaler_unit, datatype=scal_unit_type, valuetype=FIXED_IN_PROFILE, value=0, Wh, access=R)]), CosemObject(tag=ACTIVE_ENERGY_EXPORT, description=Active energy export (-A), classId=3, version=0, obis=1-0:2.8.0.255, group=ELECTRICITY, meterTypes=[SP, PP], attributes=[Attribute(id=2, description=value, datatype=double-long-unsigned, valuetype=DYNAMIC, value=RANDOM(0,1000), access=R), Attribute(id=3, description=scaler_unit, datatype=scal_unit_type, valuetype=FIXED_IN_PROFILE, value=0, Wh, access=R)]), CosemObject(tag=MBUS_CLIENT_SETUP, description=M-Bus client setup, classId=72, version=1, obis=0-c:24.1.0.255, group=GAS, meterTypes=[SP, PP], attributes=[Attribute(id=2, description=mbus_port_reference, datatype=octet-string, valuetype=FIXED_IN_PROFILE, value=0-0:24.6.0.255, access=R), Attribute(id=3, description=capture_definition, datatype=array, valuetype=DYNAMIC, value=EMPTY_ARRAY, access=R), Attribute(id=4, description=capture_period, datatype=double-long-unsigned, valuetype=FIXED_IN_PROFILE, value=0, access=RW), Attribute(id=5, description=primary_address, datatype=unsigned, valuetype=FIXED_IN_PROFILE, value=0, access=RW)])])",
        meterConfig.toString());
    log.debug("meterConfig=[{}]", meterConfig);
  }

  @Test
  void loadJsonFileAndGetActualMeterReads() throws IOException {

    final MeterConfig meterConfig =
        this.objectMapper.readValue(
            new ClassPathResource("/meter-profile-config-SMR-5.0-GetActualMeterReads.json")
                .getFile(),
            MeterConfig.class);

    Assertions.assertNotNull(meterConfig);
    Assertions.assertEquals(
        "MeterConfig(profile=SMR, version=5.0, description=Profile for Smart Meter Requirements 5.0 nieuw, settings=[Setting(firmwareUpdateType=SMR)], cosemObjects=[CosemObject(tag=CLOCK, description=Clock, classId=8, version=0, obis=0-0:1.0.0.255, group=ABSTRACT, meterTypes=[SP, PP], attributes=[Attribute(id=2, description=time, datatype=octet-string, valuetype=DYNAMIC, value=CURRENT_LOCAL_DATE_AND_TIME, access=RW), Attribute(id=3, description=time_zone, datatype=long, valuetype=FIXED_IN_PROFILE, value=-60, access=RW), Attribute(id=4, description=status, datatype=clock_status, valuetype=DYNAMIC, value=OK, access=R)]), CosemObject(tag=ACTIVE_ENERGY_IMPORT, description=Active energy import (+A), classId=3, version=0, obis=1-0:1.8.0.255, group=ELECTRICITY, meterTypes=[SP, PP], attributes=[Attribute(id=2, description=value, datatype=double-long-unsigned, valuetype=DYNAMIC, value=10001, access=R), Attribute(id=3, description=scaler_unit, datatype=scal_unit_type, valuetype=FIXED_IN_PROFILE, value=0, Wh, access=R)]), CosemObject(tag=ACTIVE_ENERGY_EXPORT, description=Active energy export (-A), classId=3, version=0, obis=1-0:2.8.0.255, group=ELECTRICITY, meterTypes=[SP, PP], attributes=[Attribute(id=2, description=value, datatype=double-long-unsigned, valuetype=DYNAMIC, value=20001, access=R), Attribute(id=3, description=scaler_unit, datatype=scal_unit_type, valuetype=FIXED_IN_PROFILE, value=0, Wh, access=R)]), CosemObject(tag=ACTIVE_ENERGY_IMPORT_RATE_1, description=Active energy import (+A) rate 1, classId=3, version=0, obis=1-0:1.8.1.255, group=ELECTRICITY, meterTypes=[SP, PP], attributes=[Attribute(id=2, description=value, datatype=double-long-unsigned, valuetype=DYNAMIC, value=10002, access=R), Attribute(id=3, description=scaler_unit, datatype=scal_unit_type, valuetype=FIXED_IN_PROFILE, value=0, Wh, access=R)]), CosemObject(tag=ACTIVE_ENERGY_IMPORT_RATE_2, description=Active energy import (+A) rate 2, classId=3, version=0, obis=1-0:1.8.2.255, group=ELECTRICITY, meterTypes=[SP, PP], attributes=[Attribute(id=2, description=value, datatype=double-long-unsigned, valuetype=DYNAMIC, value=10003, access=R), Attribute(id=3, description=scaler_unit, datatype=scal_unit_type, valuetype=FIXED_IN_PROFILE, value=0, Wh, access=R)]), CosemObject(tag=ACTIVE_ENERGY_EXPORT_RATE_1, description=Active energy export (+A) rate 1, classId=3, version=0, obis=1-0:2.8.1.255, group=ELECTRICITY, meterTypes=[SP, PP], attributes=[Attribute(id=2, description=value, datatype=double-long-unsigned, valuetype=DYNAMIC, value=20002, access=R), Attribute(id=3, description=scaler_unit, datatype=scal_unit_type, valuetype=FIXED_IN_PROFILE, value=0, Wh, access=R)]), CosemObject(tag=ACTIVE_ENERGY_EXPORT_RATE_2, description=Active energy export (+A) rate 2, classId=3, version=0, obis=1-0:2.8.2.255, group=ELECTRICITY, meterTypes=[SP, PP], attributes=[Attribute(id=2, description=value, datatype=double-long-unsigned, valuetype=DYNAMIC, value=20003, access=R), Attribute(id=3, description=scaler_unit, datatype=scal_unit_type, valuetype=FIXED_IN_PROFILE, value=0, Wh, access=R)])])",
        meterConfig.toString());
    log.debug("meterConfig=[{}]", meterConfig);
  }
}
