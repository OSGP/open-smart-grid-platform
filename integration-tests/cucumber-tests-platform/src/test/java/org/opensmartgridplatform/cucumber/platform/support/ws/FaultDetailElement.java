//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.support.ws;

public enum FaultDetailElement {
  CODE("Code"),
  MESSAGE("Message"),
  COMPONENT("Component"),
  INNER_EXCEPTION("InnerException"),
  INNER_MESSAGE("InnerMessage"),
  VALIDATION_ERROR("ValidationError"),
  VALIDATION_ERRORS("ValidationErrors");

  private final String localName;

  FaultDetailElement(final String localName) {
    this.localName = localName;
  }

  public static FaultDetailElement forLocalName(final String localName) {
    for (final FaultDetailElement faultDetailElement : values()) {
      if (faultDetailElement.localName.equals(localName)) {
        return faultDetailElement;
      }
    }
    return null;
  }

  public String getLocalName() {
    return this.localName;
  }
}
