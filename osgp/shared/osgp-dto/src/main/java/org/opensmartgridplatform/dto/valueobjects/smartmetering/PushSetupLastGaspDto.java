/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class PushSetupLastGaspDto extends AbstractPushSetupDto {

  private static final long serialVersionUID = 4394302740772086009L;

  public static class Builder extends AbstractPushSetupDto.AbstractBuilder<Builder> {

    @Override
    protected Builder self() {
      return this;
    }

    @Override
    public PushSetupLastGaspDto build() {
      return new PushSetupLastGaspDto(this);
    }
  }

  private PushSetupLastGaspDto(final Builder builder) {
    super(builder);
  }
}
