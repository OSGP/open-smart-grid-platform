/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */

package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

public enum AlarmRegister {
  ALARM_REGISTER_1(PushSetupType.TCP),
  ALARM_REGISTER_2(PushSetupType.TCP),
  ALARM_REGISTER_3(PushSetupType.UDP);

  private final PushSetupType pushSetupType;

  AlarmRegister(final PushSetupType pushSetupType) {
    this.pushSetupType = pushSetupType;
  }
}
