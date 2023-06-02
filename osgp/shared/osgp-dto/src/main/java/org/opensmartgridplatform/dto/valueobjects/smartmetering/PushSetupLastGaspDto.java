//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
