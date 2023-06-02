//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
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

  @BeforeAll
  static void init() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }

  @Test
  void testAlarmSchedulerRequestData() {
    final TestAlarmSchedulerRequestData testAlarmSchedulerRequestData =
        new TestAlarmSchedulerRequestData();
    testAlarmSchedulerRequestData.setAlarmType(TestAlarmType.PARTIAL_POWER_OUTAGE);
    testAlarmSchedulerRequestData.setScheduleTime(this.createCalendar(2088, 9, 5, 13, 30, 0));

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

  private XMLGregorianCalendar createCalendar(
      final int year,
      final int month,
      final int date,
      final int hourOfDay,
      final int minute,
      final int second) {
    final GregorianCalendar cal = new GregorianCalendar();
    cal.set(year, month - 1, date, hourOfDay, minute, second);
    try {
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
    } catch (final DatatypeConfigurationException e) {
      throw new RuntimeException(e);
    }
  }
}
