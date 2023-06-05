// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.entities;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
public class DeviceFunctionMapping extends AbstractEntity {

  /** Serial Version UID. */
  private static final long serialVersionUID = 988621596635222266L;

  @Enumerated(EnumType.STRING)
  private DeviceFunctionGroup functionGroup;

  @Enumerated(EnumType.STRING)
  private DeviceFunction function;

  public DeviceFunctionMapping() {
    // Default constructor
  }

  public DeviceFunctionMapping(
      final DeviceFunctionGroup functionGroup, final DeviceFunction function) {
    this.functionGroup = functionGroup;
    this.function = function;
  }

  public DeviceFunctionGroup getFunctionGroup() {
    return this.functionGroup;
  }

  public DeviceFunction getFunction() {
    return this.function;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DeviceFunctionMapping)) {
      return false;
    }
    final DeviceFunctionMapping mapping = (DeviceFunctionMapping) o;
    final boolean isDeviceFunctionGroupEqual =
        Objects.equals(this.functionGroup, mapping.functionGroup);
    final boolean isDeviceFunctionEqual = Objects.equals(this.function, mapping.function);

    return isDeviceFunctionGroupEqual && isDeviceFunctionEqual;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.functionGroup, this.function);
  }
}
