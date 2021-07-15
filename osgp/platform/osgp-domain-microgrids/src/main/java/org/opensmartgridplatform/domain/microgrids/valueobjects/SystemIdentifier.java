/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.microgrids.valueobjects;

import java.io.Serializable;

public class SystemIdentifier implements Serializable {

  /** */
  private static final long serialVersionUID = -3313598698244220718L;

  private final int id;
  private final String systemType;

  public SystemIdentifier(final int id, final String systemType) {
    this.id = id;
    this.systemType = systemType;
  }

  public int getId() {
    return this.id;
  }

  public String getSystemType() {
    return this.systemType;
  }
}
