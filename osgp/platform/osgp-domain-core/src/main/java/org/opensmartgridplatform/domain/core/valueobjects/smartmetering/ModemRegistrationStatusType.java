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

public enum ModemRegistrationStatusType {
  NOT_REGISTERED,
  REGISTERED_HOME_NETWORK,
  NOT_REGISTERED_CURRENT_SEARCH_OPERATOR_TO_REGISTER_TO,
  REGISTRATION_DENIED,
  UNKNOWN,
  REGISTERED_ROAMING,
  RESERVED;

  public String value() {
    return this.name();
  }

  public static ModemRegistrationStatusType fromValue(final String v) {
    return valueOf(v);
  }
}
