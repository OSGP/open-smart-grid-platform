/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
