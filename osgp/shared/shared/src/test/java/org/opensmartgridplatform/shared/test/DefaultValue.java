//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.test;

/**
 * Default value that is either set or not set, for use by builder classes (which usually generate
 * values on the fly when no default is set).
 */
public class DefaultValue<T> {
  private enum Status {
    SET,
    NOT_SET
  }

  private final DefaultValue.Status status;
  private final T value;

  private DefaultValue(final DefaultValue.Status status, final T value) {
    this.status = status;
    this.value = value;
  }

  public static <T> DefaultValue<T> notSet() {
    return new DefaultValue<>(Status.NOT_SET, null);
  }

  public static <T> DefaultValue<T> setTo(final T value) {
    return new DefaultValue<>(Status.SET, value);
  }

  /** Returns default value when set, or the given value when default value is not set. */
  public T orElse(final T elseValue) {
    if (this.status == Status.SET) {
      return this.value;
    }
    return elseValue;
  }
}
