/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.microgrids.glue.steps.mocks;

import java.util.List;

public interface RtuSteps {
  void anRtuReturning(final List<List<String>> mockValues) throws Throwable;

  void theRtuShouldContain(final List<List<String>> mockValues) throws Throwable;
}
