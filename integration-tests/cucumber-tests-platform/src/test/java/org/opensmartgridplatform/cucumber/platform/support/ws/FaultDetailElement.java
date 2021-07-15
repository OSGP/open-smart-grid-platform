/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
