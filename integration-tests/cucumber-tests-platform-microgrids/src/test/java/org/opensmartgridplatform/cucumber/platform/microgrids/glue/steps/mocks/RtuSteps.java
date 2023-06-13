// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.microgrids.glue.steps.mocks;

import java.util.List;

public interface RtuSteps {
  void anRtuReturning(final List<List<String>> mockValues) throws Throwable;

  void theRtuShouldContain(final List<List<String>> mockValues) throws Throwable;
}
