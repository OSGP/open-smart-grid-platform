// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.interfaceclass.attribute;

public enum AttributeType {
  // Types defined in DLMS
  ADJACENT_CELLS,
  CAPTURE_OBJECT_DEFINITION,
  CAPTURE_OBJECT_LIST,
  CELL_INFO,
  CIRCUIT_SWITCHED_STATUS,
  DATE_TIME,
  MODEM_REGISTRATION_STATUS,
  PACKET_SWITCHED_STATUS,
  SCALER_UNIT,
  SORT_METHOD,
  SPECIAL_DAYS,

  UNKNOWN,

  // Additional types, defined in profile (e.g. DSMR)
  DSMR_EQUIPMENT_IDENTIFIER,
  SIGNATURE
}
