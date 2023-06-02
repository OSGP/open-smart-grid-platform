//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0

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
