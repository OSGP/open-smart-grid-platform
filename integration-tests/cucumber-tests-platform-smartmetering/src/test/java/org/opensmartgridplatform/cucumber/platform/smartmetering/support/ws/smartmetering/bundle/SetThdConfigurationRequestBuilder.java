// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getLong;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.THD_MIN_DURATION_NORMAL_TO_OVER;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.THD_MIN_DURATION_OVER_TO_NORMAL;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.THD_TIME_THRESHOLD;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.THD_VALUE_HYSTERESIS;
import static org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys.THD_VALUE_THRESHOLD;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetThdConfigurationRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ThdConfiguration;

public class SetThdConfigurationRequestBuilder {

  private long thdMinDurationNormalToOver;
  private long thdMinDurationOverToNormal;
  private long thdTimeThreshold;
  private int thdValueThreshold;
  private int thdValueHysteresis;

  public SetThdConfigurationRequestBuilder fromParameterMap(final Map<String, String> parameters) {
    this.thdMinDurationNormalToOver =
        this.getLongValue(parameters, THD_MIN_DURATION_NORMAL_TO_OVER);
    this.thdMinDurationOverToNormal =
        this.getLongValue(parameters, THD_MIN_DURATION_OVER_TO_NORMAL);
    this.thdTimeThreshold = this.getLongValue(parameters, THD_TIME_THRESHOLD);
    this.thdValueHysteresis = this.getIntValue(parameters, THD_VALUE_HYSTERESIS);
    this.thdValueThreshold = this.getIntValue(parameters, THD_VALUE_THRESHOLD);
    return this;
  }

  public SetThdConfigurationRequest build() {
    final SetThdConfigurationRequest request = new SetThdConfigurationRequest();
    final ThdConfiguration config = new ThdConfiguration();
    config.setThdMinDurationNormalToOver(this.thdMinDurationNormalToOver);
    config.setThdMinDurationOverToNormal(this.thdMinDurationOverToNormal);
    config.setThdTimeThreshold(this.thdTimeThreshold);
    config.setThdValueThreshold(this.thdValueThreshold);
    config.setThdValueHysteresis(this.thdValueHysteresis);
    request.setThdConfiguration(config);
    return request;
  }

  private long getLongValue(final Map<String, String> parameters, final String key) {
    return getLong(parameters, key);
  }

  private int getIntValue(final Map<String, String> parameters, final String key) {
    return getInteger(parameters, key);
  }
}
