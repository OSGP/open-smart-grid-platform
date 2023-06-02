//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.hibernate;

public abstract class ImmutableUserType extends CustomUserType {

  @Override
  public final boolean isMutable() {
    return false;
  }

  @Override
  public Object deepCopy(final Object value) {
    // for immutable objects, a reference to the original is fine
    return value;
  }
}
