//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.core;

import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class CorrelationUidHelper {

  /**
   * Check the correlationUid in the response and save it in the current scenarioContext.
   *
   * @param organizationIdentification The organizationIdentifier used; if null or empty, the
   *     default test-org will be used.
   */
  public static void saveCorrelationUidInScenarioContext(
      final String correlationUid, String organizationIdentification) {
    if (StringUtils.isEmpty(organizationIdentification)) {
      organizationIdentification = PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION;
    }

    final String message =
        String.format(
            "Correlation UID [%s] is expected to start with organisation identification [%s].",
            correlationUid, organizationIdentification);

    // Validate the correlation-id starts with correct organization
    Assert.isTrue(correlationUid.startsWith(organizationIdentification), message);
    ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, correlationUid);
  }
}
