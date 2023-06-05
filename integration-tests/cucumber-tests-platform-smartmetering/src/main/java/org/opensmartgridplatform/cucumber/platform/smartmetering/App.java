// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering;

import org.opensmartgridplatform.cucumber.execution.AppBase;

public class App extends AppBase {

  public static void main(final String[] args) throws Throwable {

    final String[] testClasses = {
      "org.opensmartgridplatform.cucumber.platform.smartmetering.AcceptanceTests"
    };
    final App app = new App();
    System.exit(AppBase.run(app, testClasses, args));
  }
}
