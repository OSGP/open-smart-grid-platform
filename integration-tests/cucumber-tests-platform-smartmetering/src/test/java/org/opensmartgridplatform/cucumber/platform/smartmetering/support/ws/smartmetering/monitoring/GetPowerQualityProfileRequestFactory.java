// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getDate;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;
import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObjectDefinitions;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.GetPowerQualityProfileAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.GetPowerQualityProfileRequest;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.DateConverter;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class GetPowerQualityProfileRequestFactory {

  private GetPowerQualityProfileRequestFactory() {
    // Private constructor for utility class
  }

  public static GetPowerQualityProfileRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final GetPowerQualityProfileRequest getPowerQualityProfileRequestData =
        new GetPowerQualityProfileRequest();
    final DateTime beginDate = dateFromParameterMap(requestParameters, PlatformKeys.KEY_BEGIN_DATE);
    final DateTime endDate = dateFromParameterMap(requestParameters, PlatformKeys.KEY_END_DATE);
    final String profileType =
        getString(requestParameters, PlatformKeys.KEY_POWER_QUALITY_PROFILE_TYPE);
    final CaptureObjectDefinitions captureObjecDefinitions =
        CaptureObjectDefinitionsFactory.fromParameterMap(requestParameters);

    getPowerQualityProfileRequestData.setDeviceIdentification(
        requestParameters.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    getPowerQualityProfileRequestData.setBeginDate(
        DateConverter.createXMLGregorianCalendar(beginDate.toDate()));
    getPowerQualityProfileRequestData.setEndDate(
        DateConverter.createXMLGregorianCalendar(endDate.toDate()));
    getPowerQualityProfileRequestData.setProfileType(profileType);
    getPowerQualityProfileRequestData.setSelectedValues(captureObjecDefinitions);

    return getPowerQualityProfileRequestData;
  }

  private static DateTime dateFromParameterMap(
      final Map<String, String> requestParameters, final String key) {
    return getDate(requestParameters, key, new DateTime());
  }

  public static GetPowerQualityProfileAsyncRequest fromScenarioContext() {
    final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
    final String deviceIdentification =
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext();
    final GetPowerQualityProfileAsyncRequest getPowerQualityProfileAsyncRequest =
        new GetPowerQualityProfileAsyncRequest();
    getPowerQualityProfileAsyncRequest.setCorrelationUid(correlationUid);
    getPowerQualityProfileAsyncRequest.setDeviceIdentification(deviceIdentification);
    return getPowerQualityProfileAsyncRequest;
  }
}
