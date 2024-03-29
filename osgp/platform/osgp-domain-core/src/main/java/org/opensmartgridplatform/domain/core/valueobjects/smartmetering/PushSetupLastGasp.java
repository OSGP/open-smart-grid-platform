// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
