/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmRegister;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmType;

public class AlarmRegisterMappingTest {

  private static final AlarmType ALARMTYPE = AlarmType.CLOCK_INVALID;
  private MonitoringMapper monitoringMapper = new MonitoringMapper();

  /** Test to see if an AlarmRegister object is mapped correctly with an empty Set. */
  @Test
  public void testWithEmptySet() {

    // build test data
    final Set<AlarmType> alarmTypeSet = new TreeSet<>();
    final AlarmRegister original = new AlarmRegister(alarmTypeSet);

    // actual mapping
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.AlarmRegister
        mapped =
            this.monitoringMapper.map(
                original,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.AlarmRegister
                    .class);

    // check mapping
    assertThat(mapped).isNotNull();
    assertThat(mapped.getAlarmTypes()).isNotNull();
    assertThat(mapped.getAlarmTypes().isEmpty()).isTrue();
  }

  /** Test to see if an AlarmRegister object is mapped correctly with a filled Set. */
  @Test
  public void testWithFilledSet() {

    // build test data
    final Set<AlarmType> alarmTypeSet = new TreeSet<>();
    alarmTypeSet.add(ALARMTYPE);
    final AlarmRegister original = new AlarmRegister(alarmTypeSet);

    // actual mapping
    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.AlarmRegister
        mapped =
            this.monitoringMapper.map(
                original,
                org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.AlarmRegister
                    .class);

    // check mapping
    assertThat(mapped).isNotNull();
    assertThat(mapped.getAlarmTypes()).isNotNull();
    assertThat(mapped.getAlarmTypes().get(0)).isNotNull();
    assertThat(mapped.getAlarmTypes().size()).isEqualTo(original.getAlarmTypes().size());
    assertThat(mapped.getAlarmTypes().get(0).name()).isEqualTo(ALARMTYPE.name());
  }
}
