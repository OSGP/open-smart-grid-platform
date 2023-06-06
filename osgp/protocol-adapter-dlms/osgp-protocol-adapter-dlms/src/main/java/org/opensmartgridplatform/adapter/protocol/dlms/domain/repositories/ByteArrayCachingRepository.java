// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ByteArrayCachingRepository implements CachingRepository<String, byte[]> {

  private Map<String, byte[]> cache = new ConcurrentHashMap<>();

  public ByteArrayCachingRepository() {
    // Public constructor
  }

  protected ByteArrayCachingRepository(final Map<String, byte[]> cache) {
    // Protected constructor for testing
    this.cache = cache;
  }

  protected Map<String, byte[]> getCache() {
    return Collections.unmodifiableMap(this.cache);
  }

  @Override
  public boolean isAvailable(final String key) {
    return this.cache.containsKey(key);
  }

  @Override
  // Sonar warning is suppressed, because when the key is not found, this function should return
  // null and not an empty array (which could be a valid array stored in the cache).
  @SuppressWarnings("java:S1168")
  public byte[] retrieve(final String key) {
    if (this.cache.containsKey(key)) {
      // To make sure the byte array in the cache is not changed accidentally, return a copy
      return this.cache.get(key).clone();
    } else {
      return null;
    }
  }

  @Override
  public void store(final String key, final byte[] value) {
    if (value == null) {
      this.remove(key);
    } else {
      this.cache.put(key, value);
    }
  }

  @Override
  public void remove(final String key) {
    this.cache.remove(key);
  }
}
