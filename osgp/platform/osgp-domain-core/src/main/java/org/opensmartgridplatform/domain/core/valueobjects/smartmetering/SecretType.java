/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

public enum SecretType {
  E_METER_MASTER_KEY,
  E_METER_AUTHENTICATION_KEY,
  E_METER_ENCRYPTION_KEY_UNICAST,
  E_METER_ENCRYPTION_KEY_BROADCAST,
  G_METER_MASTER_KEY,
  G_METER_ENCRYPTION_KEY,
  G_METER_FIRMWARE_UPDATE_KEY,
  G_METER_OPTICAL_PORT_KEY
}
