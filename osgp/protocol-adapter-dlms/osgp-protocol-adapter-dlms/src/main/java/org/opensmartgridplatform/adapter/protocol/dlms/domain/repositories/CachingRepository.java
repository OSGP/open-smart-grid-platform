//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories;

public interface CachingRepository<S, T> {

  boolean isAvailable(S key);

  T retrieve(S key);

  void store(S key, T value);

  void remove(S key);
}
