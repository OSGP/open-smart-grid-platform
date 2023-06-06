// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.hibernate;

public abstract class MutableUserType extends CustomUserType {

  @Override
  public boolean isMutable() {
    return true;
  }
}
