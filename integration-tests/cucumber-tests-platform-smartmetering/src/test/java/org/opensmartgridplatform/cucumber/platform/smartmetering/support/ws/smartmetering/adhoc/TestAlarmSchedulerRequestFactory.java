//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc;

import java.util.Map;
import javax.xml.datatype.XMLGregorianCalendar;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.TestAlarmSchedulerAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.TestAlarmSchedulerRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.TestAlarmSchedulerRequestData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.TestAlarmType;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.XMLGregorianCalendarHelper;

public final class TestAlarmSchedulerRequestFactory {

  private TestAlarmSchedulerRequestFactory() {
    // Private constructor for utility class
  }

  public static TestAlarmSchedulerRequest fromParameterMap(final Map<String, String> parameters) {
    final TestAlarmSchedulerRequest request = new TestAlarmSchedulerRequest();
    request.setDeviceIdentification(
        parameters.get(PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION));
    final TestAlarmSchedulerRequestData requestData = new TestAlarmSchedulerRequestData();
    requestData.setAlarmType(
        TestAlarmType.valueOf(parameters.get(PlatformSmartmeteringKeys.TEST_ALARM_TYPE)));

    final XMLGregorianCalendar scheduledTime =
        XMLGregorianCalendarHelper.createXMLGregorianCalendar(
            parameters, PlatformSmartmeteringKeys.SCHEDULE_TIME);

    requestData.setScheduleTime(scheduledTime);
    request.setTestAlarmSchedulerRequestData(requestData);
    return request;
  }

  public static TestAlarmSchedulerAsyncRequest fromScenarioContext() {
    final TestAlarmSchedulerAsyncRequest asyncRequest = new TestAlarmSchedulerAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return asyncRequest;
  }
}
