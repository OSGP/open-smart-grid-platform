// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetSpecialDaysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SpecialDay;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class SetSpecialDaysRequestBuilder {

  private static final int DEFAULT_SPECIAL_DAY_COUNT = 1;
  private static final int DEFAULT_SPECIAL_DAY_ID = 1;
  private static final String DEFAULT_SPECIAL_DAY_DATE = "FFFFFFFFFF";

  private List<SpecialDay> specialDays = new ArrayList<>();

  public SetSpecialDaysRequestBuilder withDefaults() {
    return this.fromParameterMap(Collections.emptyMap());
  }

  public SetSpecialDaysRequestBuilder fromParameterMap(final Map<String, String> parameters) {
    this.specialDays = new ArrayList<>();
    final int specialDayCount = this.getSpecialDayCount(parameters);
    for (int i = 1; i <= specialDayCount; i++) {
      this.specialDays.add(this.getSpecialDay(parameters, i));
    }
    return this;
  }

  public SetSpecialDaysRequest build() {
    final SetSpecialDaysRequest request = new SetSpecialDaysRequest();
    request.getSpecialDays().addAll(this.specialDays);
    return request;
  }

  private int getSpecialDayCount(final Map<String, String> parameters) {
    return getInteger(
        parameters, PlatformSmartmeteringKeys.SPECIAL_DAY_COUNT, DEFAULT_SPECIAL_DAY_COUNT);
  }

  private SpecialDay getSpecialDay(final Map<String, String> parameters, final int index) {
    final SpecialDay specialDay = new SpecialDay();
    specialDay.setDayId(this.getSpecialDayId(parameters, index));
    specialDay.setSpecialDayDate(this.getSpecialDayDate(parameters, index));
    return specialDay;
  }

  private int getSpecialDayId(final Map<String, String> parameters, final int index) {
    final String key = SettingsHelper.makeKey(PlatformSmartmeteringKeys.SPECIAL_DAY_ID, index);
    return getInteger(parameters, key, DEFAULT_SPECIAL_DAY_ID);
  }

  private byte[] getSpecialDayDate(final Map<String, String> parameters, final int index) {
    final String key = SettingsHelper.makeKey(PlatformSmartmeteringKeys.SPECIAL_DAY_DATE, index);
    final String value = getString(parameters, key, DEFAULT_SPECIAL_DAY_DATE);
    return DatatypeConverter.parseHexBinary(value);
  }
}
