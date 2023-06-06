// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class PushSetupSms extends AbstractPushSetup implements Serializable {

  private static final long serialVersionUID = -3541154908239512383L;

  public static class Builder extends AbstractPushSetup.AbstractBuilder<Builder> {

    @Override
    protected Builder self() {
      return this;
    }

    @Override
    public PushSetupSms build() {
      return new PushSetupSms(this);
    }
  }

  public PushSetupSms(final Builder builder) {
    super(builder);
  }
}
