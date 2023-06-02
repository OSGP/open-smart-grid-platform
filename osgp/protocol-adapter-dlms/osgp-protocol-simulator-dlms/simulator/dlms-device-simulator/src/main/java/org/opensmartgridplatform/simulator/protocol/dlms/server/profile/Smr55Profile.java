//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.dlms.server.profile;

import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.AlarmObject.UDP_PUSH_OBJECT;

import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DataAttribute;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.AlarmFilter;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.AlarmObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.PushSetupObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.SingleActionScheduler;
import org.opensmartgridplatform.simulator.protocol.dlms.util.DynamicValues;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("smr55")
public class Smr55Profile {

  @Value("${alarmobject.register3.value}")
  private int alarmRegister3Value;

  @Value("${alarmfilter3.value}")
  private int alarmFilter3Value;

  @Bean
  public AlarmObject alarmObject3(final DynamicValues dynamicValues) {
    dynamicValues.setDefaultAttributeValue(
        InterfaceClass.DATA.id(),
        new ObisCode(0, 0, 97, 98, 2, 255),
        DataAttribute.VALUE.attributeId(),
        DataObject.newUInteger32Data(this.alarmRegister3Value));
    return new AlarmObject(UDP_PUSH_OBJECT);
  }

  @Bean
  public AlarmFilter alarmFilter3() {
    return new AlarmFilter("0.0.97.98.12.255", this.alarmFilter3Value);
  }

  @Bean
  SingleActionScheduler lastGaspTestScheduler() {
    return new SingleActionScheduler("0.0.15.2.4.255");
  }

  @Bean
  public PushSetupObject pushSetupLastGasp() {
    return new PushSetupObject("0.3.25.9.0.255");
  }
}
