/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.io.Serializable;

public class SystemIdentifierDto implements Serializable {

  private static final long serialVersionUID = -8592667499461927077L;

  private int id;
  private String systemType;

  public SystemIdentifierDto(final int id, final String systemType) {
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
