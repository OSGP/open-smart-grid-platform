/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class FirmwareFileCachingRepository implements CachingRepository<String, byte[]> {

  private Map<String, byte[]> cache = new ConcurrentHashMap<>();

  public FirmwareFileCachingRepository() {
    // Public constructor
  }

  protected FirmwareFileCachingRepository(final Map<String, byte[]> cache) {
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
  public byte[] retrieve(final String key) {
    return this.cache.get(key);
  }

  @Override
  public void store(final String key, final byte[] value) {
    this.cache.put(key, value);
  }
}
