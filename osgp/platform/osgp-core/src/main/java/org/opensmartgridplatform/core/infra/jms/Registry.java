/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Registry<T> {

  private final Map<String, T> internalMap = new HashMap<>();

  public Set<String> getKeys() {
    return new HashSet<>(this.internalMap.keySet());
  }

  public List<T> getValues() {
    return new ArrayList<>(this.internalMap.values());
  }

  public T getValue(final String key) {
    return this.internalMap.get(key);
  }

  public void register(final String key, final T value) {
    this.internalMap.put(key, value);
  }

  public void unregisterAll() {
    this.preUnregisterAll();
    this.internalMap.clear();
  }

  /*
   * Override when further actions are needed before unregistering all objects
   */
  protected void preUnregisterAll() {
    // Do nothing here
  }

  public void unregister(final String key) {
    this.preUnregister(key);
    this.internalMap.remove(key);
  }

  /*
   * Override this method when further actions are needed before unregistering
   * the specific object
   */
  protected void preUnregister(final String key) {
    // Do nothing here
  }
}
