/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.server.profile;

import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.AlarmObject.ALARM_OBJECT_2;

import java.util.Calendar;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DataAttribute;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.AlarmFilter;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.AlarmObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityExtendedEventLog;
import org.opensmartgridplatform.simulator.protocol.dlms.util.DynamicValues;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("smr52")
public class Smr52Profile {
  @Value("${alarmobject.register2.value}")
  private int alarmRegister2Value;

  @Value("${alarmfilter2.value}")
  private int alarmFilter2Value;

  @Bean
  public AlarmObject alarmObject2(final DynamicValues dynamicValues) {
    dynamicValues.setDefaultAttributeValue(
        InterfaceClass.DATA.id(),
        new ObisCode(0, 0, 97, 98, 1, 255),
        DataAttribute.VALUE.attributeId(),
        DataObject.newUInteger32Data(this.alarmRegister2Value));
    return new AlarmObject(ALARM_OBJECT_2);
  }

  @Bean
  public AlarmFilter alarmFilter2() {
    return new AlarmFilter("0.0.97.98.11.255", this.alarmFilter2Value);
  }

  @Bean
  public PowerQualityExtendedEventLog powerQualityExtendedEventLog(final Calendar cal) {
    return new PowerQualityExtendedEventLog(cal);
  }
}
