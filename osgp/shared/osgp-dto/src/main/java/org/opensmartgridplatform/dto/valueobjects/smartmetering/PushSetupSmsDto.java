// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
