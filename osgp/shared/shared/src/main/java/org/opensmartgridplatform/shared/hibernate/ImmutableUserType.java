/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
