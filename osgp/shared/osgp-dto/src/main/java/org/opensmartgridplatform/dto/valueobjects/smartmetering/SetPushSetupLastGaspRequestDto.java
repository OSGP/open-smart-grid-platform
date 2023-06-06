// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class SetPushSetupLastGaspRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = 8727328348461809018L;

  private PushSetupLastGaspDto pushSetupLastGasp;

  public SetPushSetupLastGaspRequestDto(final PushSetupLastGaspDto pushSetupLastGasp) {
    this.pushSetupLastGasp = pushSetupLastGasp;
  }

  public PushSetupLastGaspDto getPushSetupLastGasp() {
    return this.pushSetupLastGasp;
  }
}
