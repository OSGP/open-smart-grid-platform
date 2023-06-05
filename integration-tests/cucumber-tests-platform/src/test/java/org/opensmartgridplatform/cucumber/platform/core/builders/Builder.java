// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.core.builders;

/**
 * Simple builder interface.
 *
 * @param <T> The type of objects the builder will build.
 */
public interface Builder<T> {
  T build();
}
