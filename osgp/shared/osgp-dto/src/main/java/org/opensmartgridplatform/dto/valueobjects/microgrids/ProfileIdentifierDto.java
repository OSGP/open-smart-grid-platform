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

public class ProfileIdentifierDto implements Serializable {

  private static final long serialVersionUID = 5587798706867134143L;

  private int id;
  private String node;

  public ProfileIdentifierDto(final int id, final String node) {
    this.id = id;
    this.node = node;
  }

  public int getId() {
    return this.id;
  }

  public String getNode() {
    return this.node;
  }
}
