// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetPushSetupSmsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupSms;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class SetPushSetupSmsRequestBuilder {

  private static final String DEFAULT_HOST = "localhost";
  private static final int DEFAULT_PORT = 9598;

  private String host;
  private BigInteger port;

  public SetPushSetupSmsRequestBuilder withDefaults() {
    return this.fromParameterMap(Collections.emptyMap());
  }

  public SetPushSetupSmsRequestBuilder fromParameterMap(final Map<String, String> parameters) {
    this.host = this.getHost(parameters);
    this.port = this.getPort(parameters);
    return this;
  }

  public SetPushSetupSmsRequest build() {
    final SetPushSetupSmsRequest request = new SetPushSetupSmsRequest();
    final PushSetupSms pushSetupSms = new PushSetupSms();
    pushSetupSms.setHost(this.host);
    pushSetupSms.setPort(this.port);

    request.setPushSetupSms(pushSetupSms);
    return request;
  }

  private String getHost(final Map<String, String> parameters) {
    return getString(parameters, PlatformSmartmeteringKeys.HOSTNAME, DEFAULT_HOST);
  }

  private BigInteger getPort(final Map<String, String> parameters) {
    return BigInteger.valueOf(getInteger(parameters, PlatformSmartmeteringKeys.PORT, DEFAULT_PORT));
  }
}
