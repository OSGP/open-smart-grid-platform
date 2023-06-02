//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.hooks;

import io.cucumber.java.Before;
import org.opensmartgridplatform.cucumber.platform.publiclighting.mocks.oslpdevice.MockOslpServer;
import org.springframework.beans.factory.annotation.Autowired;

public class OslpMockServerHooks {

  @Autowired private MockOslpServer mockServer;

  @Before("@OslpMockServer")
  public void resetServer() {
    if (this.mockServer != null) {
      this.mockServer.resetServer();
    }
  }
}
