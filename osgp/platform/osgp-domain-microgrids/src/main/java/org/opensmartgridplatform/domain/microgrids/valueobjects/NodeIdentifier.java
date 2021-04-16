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

public class NodeIdentifier implements Serializable {

  private static final long serialVersionUID = 3933967809366650885L;

  private final int id;
  private final String node;

  public NodeIdentifier(final int id, final String node) {
    super();
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
