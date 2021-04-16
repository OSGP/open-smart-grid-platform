/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.core.builders;

import java.util.Map;

/**
 * Builder interface adapted for Cucumber. It supports setting multiple properties directly from the
 * input Map provided by Cucumber.
 *
 * @param <T> The type of Builder class that is returned.
 */
public interface CucumberBuilder<T> extends Builder<T> {
  CucumberBuilder<T> withSettings(final Map<String, String> inputSettings);
}
