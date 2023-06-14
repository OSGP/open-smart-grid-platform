// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class PushSetupAlarmDto extends AbstractPushSetupDto {

  private static final long serialVersionUID = -3541154908239512383L;

  public static class Builder extends AbstractPushSetupDto.AbstractBuilder<Builder> {

    @Override
    protected Builder self() {
      return this;
    }

    @Override
    public PushSetupAlarmDto build() {
      return new PushSetupAlarmDto(this);
    }
  }

  private PushSetupAlarmDto(final Builder builder) {
    super(builder);
  }
}
