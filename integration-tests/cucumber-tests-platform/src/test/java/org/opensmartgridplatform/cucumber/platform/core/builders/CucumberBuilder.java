//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
