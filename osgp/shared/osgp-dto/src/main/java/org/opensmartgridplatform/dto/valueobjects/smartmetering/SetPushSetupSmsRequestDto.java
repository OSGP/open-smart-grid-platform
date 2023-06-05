// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class SetPushSetupSmsRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = -7127981780182253661L;

  private PushSetupSmsDto pushSetupSms;

  public SetPushSetupSmsRequestDto(final PushSetupSmsDto pushSetupSms) {
    this.pushSetupSms = pushSetupSms;
  }

  public PushSetupSmsDto getPushSetupSms() {
    return this.pushSetupSms;
  }
}
