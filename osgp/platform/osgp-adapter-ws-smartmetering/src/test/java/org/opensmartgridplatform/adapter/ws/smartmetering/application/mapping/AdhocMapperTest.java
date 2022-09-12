/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.TestAlarmSchedulerRequestData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.TestAlarmType;

@ExtendWith(MockitoExtension.class)
@Slf4j
class AdhocMapperTest {

  @InjectMocks private AdhocMapper adhocMapper;

  @Test
  void testAlarmSchedulerRequestData() {

    final TestAlarmSchedulerRequestData testAlarmSchedulerRequestData =
        new TestAlarmSchedulerRequestData();
    testAlarmSchedulerRequestData.setAlarmType(TestAlarmType.PARTIAL_POWER_OUTAGE);
    testAlarmSchedulerRequestData.setScheduleTime(
        XMLGregorianCalendarImpl.createDateTime(2088, 9, 5, 13, 30, 0));

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering
            .TestAlarmSchedulerRequestData
        mapped =
            this.adhocMapper.map(
                testAlarmSchedulerRequestData,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                    .TestAlarmSchedulerRequestData.class);

    Assertions.assertThat(mapped)
        .hasToString(
            "TestAlarmSchedulerRequestData(scheduleTime=Sun Sep 05 13:30:00 CEST 2088, alarmType=PARTIAL_POWER_OUTAGE)");
    log.debug(mapped.toString());
  }
}
