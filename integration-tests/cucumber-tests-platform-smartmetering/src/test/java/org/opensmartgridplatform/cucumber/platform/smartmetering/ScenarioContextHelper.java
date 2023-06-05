// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.AsyncResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;

public class ScenarioContextHelper {
  /**
   * Store the correlationUid and deviceIdentification in the ScenarioContext, given the
   * AsyncResponse
   *
   * @param asyncResponse The AsyncResponse used to retrieve the values (mentioned above) to store
   *     in the ScenarioContext
   * @throws Throwable
   */
  public static void saveAsyncResponse(final AsyncResponse asyncResponse) throws Throwable {
    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    ScenarioContext.current()
        .put(PlatformKeys.KEY_DEVICE_IDENTIFICATION, asyncResponse.getDeviceIdentification());
  }
}
