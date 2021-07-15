/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.builders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import javax.xml.datatype.XMLGregorianCalendar;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetPowerQualityProfileRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.CaptureObjectDefinitions;
import org.opensmartgridplatform.cucumber.platform.helpers.DateConverter;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class GetPowerQualityProfileRequestBuilder {

  private static String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

  private String profileType;
  private XMLGregorianCalendar beginDate;
  private XMLGregorianCalendar endDate;
  private CaptureObjectDefinitions selectedValues;

  public GetPowerQualityProfileRequestBuilder fromParameterMap(
      final Map<String, String> parameterMap) throws ParseException {

    final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    this.beginDate =
        DateConverter.createXMLGregorianCalendar(
            sdf.parse(parameterMap.get(PlatformSmartmeteringKeys.KEY_BEGIN_DATE)));
    this.endDate =
        DateConverter.createXMLGregorianCalendar(
            sdf.parse(parameterMap.get(PlatformSmartmeteringKeys.KEY_END_DATE)));
    this.profileType = parameterMap.get(PlatformSmartmeteringKeys.KEY_POWER_QUALITY_PROFILE_TYPE);

    return this;
  }

  public GetPowerQualityProfileRequestBuilder withProfileType(final String profileType) {
    this.profileType = profileType;
    return this;
  }

  public GetPowerQualityProfileRequestBuilder withBeginDate(final XMLGregorianCalendar beginDate) {
    this.beginDate = beginDate;
    return this;
  }

  public GetPowerQualityProfileRequestBuilder withEndDate(final XMLGregorianCalendar endDate) {
    this.endDate = endDate;
    return this;
  }

  public GetPowerQualityProfileRequestBuilder withSelectedValues(
      final CaptureObjectDefinitions selectedValues) {
    this.selectedValues = selectedValues;
    return this;
  }

  public GetPowerQualityProfileRequest build() {
    final GetPowerQualityProfileRequest request = new GetPowerQualityProfileRequest();
    request.setProfileType(this.profileType);
    request.setBeginDate(this.beginDate);
    request.setEndDate(this.endDate);
    request.setSelectedValues(this.selectedValues);
    return request;
  }
}
