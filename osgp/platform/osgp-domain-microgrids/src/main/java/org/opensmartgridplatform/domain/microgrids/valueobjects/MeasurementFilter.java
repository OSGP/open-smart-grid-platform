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

public class MeasurementFilter extends NodeIdentifier implements Serializable {

  private static final long serialVersionUID = -5169545289993816729L;

  private final boolean all;

  public MeasurementFilter(final int id, final String node, final boolean all) {
    super(id, node);
    this.all = all;
  }

  public boolean isAll() {
    return this.all;
  }
}
