/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class PushSetupLastGasp extends AbstractPushSetup implements Serializable {

  private static final long serialVersionUID = 3473113538889345222L;

  public static class Builder extends AbstractPushSetup.AbstractBuilder<Builder> {

    @Override
    protected Builder self() {
      return this;
    }

    @Override
    public PushSetupLastGasp build() {
      return new PushSetupLastGasp(this);
    }
  }

  public PushSetupLastGasp(final Builder builder) {
    super(builder);
  }
}
