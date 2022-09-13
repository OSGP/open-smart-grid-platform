/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;
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
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

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

    assertThat(mapped.getScheduleTime()).hasToString("Sun Sep 05 13:30:00 UTC 2088");
    assertThat(mapped.getAlarmType()).hasToString("PARTIAL_POWER_OUTAGE");
  }
}
