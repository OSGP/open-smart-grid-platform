/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
