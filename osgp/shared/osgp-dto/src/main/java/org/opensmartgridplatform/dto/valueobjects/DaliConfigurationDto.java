/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DaliConfigurationDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -3988541249244724989L;

  private final Integer numberOfLights;

  private final Map<Integer, Integer> indexAddressMap;

  public DaliConfigurationDto(
      final Integer numberOfLights, final Map<Integer, Integer> indexAddressMap) {
    this.numberOfLights = numberOfLights;
    // Shallow copy and unmodifiable version of the map to prevent external
    // changes to the map
    this.indexAddressMap = Collections.unmodifiableMap(new HashMap<>(indexAddressMap));
  }

  public Integer getNumberOfLights() {
    return this.numberOfLights;
  }

  public Map<Integer, Integer> getIndexAddressMap() {
    // Returns the property directly as it is already made unmodifiable in
    // the constructor.
    return this.indexAddressMap;
  }
}
