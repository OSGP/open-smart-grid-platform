// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.hibernate;

import java.io.Serializable;
import java.util.Objects;
import org.hibernate.type.SerializationException;
import org.hibernate.usertype.UserType;

public abstract class CustomUserType implements UserType {

  // This Sonar critical cannot be avoided. The function equals() overrides a
  // function from Hibernate. There is no way to change the library to get rid
  // of this warning.
  // Methods named "equals" should override Object.equals(Object) : Either
  // override Object.equals(Object), or totally rename the method to prevent
  // any confusion.
  //
  // Therefore, this warning is suppressed.
  @SuppressWarnings("all")
  @Override
  public boolean equals(final Object x, final Object y) {
    return Objects.equals(x, y);
  }

  @Override
  public int hashCode(final Object x) {
    return Objects.hash(x);
  }

  @Override
  public Object assemble(final Serializable cached, final Object owner) {
    return this.deepCopy(cached);
  }

  @Override
  public Serializable disassemble(final Object value) {
    final Object deepCopy = this.deepCopy(value);

    if (!(deepCopy instanceof Serializable)) {
      throw new SerializationException(
          String.format("deepCopy of %s is not serializable", value), null);
    }

    return (Serializable) deepCopy;
  }

  @Override
  public Object replace(final Object original, final Object target, final Object owner) {
    return this.deepCopy(original);
  }
}
