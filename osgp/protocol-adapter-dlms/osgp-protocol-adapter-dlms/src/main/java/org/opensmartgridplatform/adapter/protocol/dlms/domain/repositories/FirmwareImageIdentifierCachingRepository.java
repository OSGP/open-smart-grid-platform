/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class FirmwareImageIdentifierCachingRepository implements CachingRepository<String, String> {

  private Map<String, String> cache = new ConcurrentHashMap<>();

  public FirmwareImageIdentifierCachingRepository() {
    // Public constructor
  }

  protected FirmwareImageIdentifierCachingRepository(final Map<String, String> cache) {
    // Protected constructor for testing
    this.cache = cache;
  }

  protected Map<String, String> getCache() {
    return Collections.unmodifiableMap(this.cache);
  }

  @Override
  public boolean isAvailable(final String key) {
    return this.cache.containsKey(key);
  }

  @Override
  public String retrieve(final String key) {
    return this.cache.get(key);
  }

  @Override
  public void store(final String key, final String value) {
    this.cache.put(key, value);
  }
}
