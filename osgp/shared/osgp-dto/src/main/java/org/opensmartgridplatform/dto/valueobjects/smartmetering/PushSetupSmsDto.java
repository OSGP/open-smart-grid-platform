/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class PushSetupSmsDto extends AbstractPushSetupDto {

  private static final long serialVersionUID = -3541154908239512383L;

  public static class Builder extends AbstractPushSetupDto.AbstractBuilder<Builder> {

    @Override
    protected Builder self() {
      return this;
    }

    @Override
    public PushSetupSmsDto build() {
      return new PushSetupSmsDto(this);
    }
  }

  private PushSetupSmsDto(final Builder builder) {
    super(builder);
  }
}
