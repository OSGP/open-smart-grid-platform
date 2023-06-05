// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
