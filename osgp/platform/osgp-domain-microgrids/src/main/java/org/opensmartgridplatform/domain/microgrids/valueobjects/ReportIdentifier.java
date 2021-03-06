/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.microgrids.valueobjects;

import java.io.Serializable;

public class ReportIdentifier implements Serializable {

  private static final long serialVersionUID = -3960060997429091933L;

  private final String id;

  public ReportIdentifier(final String id) {
    this.id = id;
  }

  public String getId() {
    return this.id;
  }
}
