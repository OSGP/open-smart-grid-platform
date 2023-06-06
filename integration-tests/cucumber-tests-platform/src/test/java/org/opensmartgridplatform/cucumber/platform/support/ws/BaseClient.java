// Copyright 2012-2017 Smart Society Services B.V.
// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.support.ws;

import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;

/** Base client. */
public abstract class BaseClient {

  protected String getOrganizationIdentification() {
    return (String)
        ScenarioContext.current()
            .get(
                PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
                PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION);
  }

  protected String getUserName() {
    return (String)
        ScenarioContext.current()
            .get(PlatformKeys.KEY_USER_NAME, PlatformDefaults.DEFAULT_USER_NAME);
  }
}
