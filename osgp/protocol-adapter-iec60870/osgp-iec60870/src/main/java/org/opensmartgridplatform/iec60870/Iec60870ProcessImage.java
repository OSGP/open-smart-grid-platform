/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.iec60870;

import java.util.Map;

public class Iec60870ProcessImage {
  private final Map<Integer, Iec60870InformationObject> informationObjects;

  public Iec60870ProcessImage(final Map<Integer, Iec60870InformationObject> informationObjects) {
    this.informationObjects = informationObjects;
  }

  public Map<Integer, Iec60870InformationObject> getInformationObjects() {
    return this.informationObjects;
  }

  public Iec60870InformationObject getInformationObject(final int informationObjectAddress) {
    return this.informationObjects.get(informationObjectAddress);
  }
}
