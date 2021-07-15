/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class PushSetupAlarm extends AbstractPushSetup implements Serializable {

  private static final long serialVersionUID = -3541154908239512383L;

  public static class Builder extends AbstractPushSetup.AbstractBuilder<Builder> {

    @Override
    protected Builder self() {
      return this;
    }

    @Override
    public PushSetupAlarm build() {
      return new PushSetupAlarm(this);
    }
  }

  public PushSetupAlarm(final Builder builder) {
    super(builder);
  }
}
