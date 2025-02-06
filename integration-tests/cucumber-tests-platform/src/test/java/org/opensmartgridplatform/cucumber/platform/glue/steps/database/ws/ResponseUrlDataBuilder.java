// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.glue.steps.database.ws;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseUrlData;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.springframework.beans.factory.annotation.Qualifier;

public class ResponseUrlDataBuilder {

  private String correlationUid = "test-org|||TEST1024000000001|||20170101000000000";

  @Qualifier("responseUrl")
  private String responseUrl;

  public ResponseUrlData build() {
    return new ResponseUrlData(this.correlationUid, this.responseUrl);
  }

  public ResponseUrlDataBuilder fromSettings(final Map<String, String> settings) {
    if (settings.containsKey(PlatformKeys.KEY_CORRELATION_UID)) {
      this.withCorrelationUid(settings.get(PlatformKeys.KEY_CORRELATION_UID));
    }
    if (settings.containsKey(PlatformKeys.KEY_RESPONSE_URL)) {
      this.withResponseUrl(settings.get(PlatformKeys.KEY_RESPONSE_URL));
    }
    return this;
  }

  private void withCorrelationUid(final String correlationUid) {
    this.correlationUid = correlationUid;
  }

  private void withResponseUrl(final String responseUrl) {
    this.responseUrl = responseUrl;
  }
}
