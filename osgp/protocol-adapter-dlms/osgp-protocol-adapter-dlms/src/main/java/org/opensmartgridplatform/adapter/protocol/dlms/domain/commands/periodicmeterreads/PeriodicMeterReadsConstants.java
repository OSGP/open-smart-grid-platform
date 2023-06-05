// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ObisCode;

public class PeriodicMeterReadsConstants {
  static final int CLASS_ID_PROFILE_GENERIC = 7;
  static final ObisCode OBIS_CODE_INTERVAL_BILLING = new ObisCode("1.0.99.1.0.255");
  static final ObisCode OBIS_CODE_DAILY_BILLING = new ObisCode("1.0.99.2.0.255");
  static final ObisCode OBIS_CODE_MONTHLY_BILLING = new ObisCode("0.0.98.1.0.255");
  static final byte ATTRIBUTE_ID_BUFFER = 2;
  static final byte ATTRIBUTE_ID_SCALER_UNIT = 3;
  static final ObisCode OBIS_CODE_INTERVAL_IMPORT_SCALER_UNIT = new ObisCode("1.0.1.8.0.255");
  static final ObisCode OBIS_CODE_INTERVAL_EXPORT_SCALER_UNIT = new ObisCode("1.0.2.8.0.255");
  static final ObisCode OBIS_CODE_MONTHLY_DAILY_IMPORT_RATE_1 = new ObisCode("1.0.1.8.1.255");
  static final ObisCode OBIS_CODE_MONTHLY_DAILY_IMPORT_RATE_2 = new ObisCode("1.0.1.8.2.255");
  static final ObisCode OBIS_CODE_MONTHLY_DAILY_EXPORT_RATE_1 = new ObisCode("1.0.2.8.1.255");
  static final ObisCode OBIS_CODE_MONTHLY_DAILY_EXPORT_RATE_2 = new ObisCode("1.0.2.8.2.255");

  static final int CLASS_ID_REGISTER = 3;

  static final byte[] OBIS_BYTES_ACTIVE_ENERGY_IMPORT_RATE_1 =
      OBIS_CODE_MONTHLY_DAILY_IMPORT_RATE_1.bytes();
  static final byte[] OBIS_BYTES_ACTIVE_ENERGY_IMPORT_RATE_2 =
      OBIS_CODE_MONTHLY_DAILY_IMPORT_RATE_2.bytes();
  static final byte[] OBIS_BYTES_ACTIVE_ENERGY_EXPORT_RATE_1 =
      OBIS_CODE_MONTHLY_DAILY_EXPORT_RATE_1.bytes();
  static final byte[] OBIS_BYTES_ACTIVE_ENERGY_EXPORT_RATE_2 =
      OBIS_CODE_MONTHLY_DAILY_EXPORT_RATE_2.bytes();
  static final byte ATTRIBUTE_ID_VALUE = 2;

  static final int ACCESS_SELECTOR_RANGE_DESCRIPTOR = 1;

  static final AttributeAddress ATTRIBUTE_INTERVAL_IMPORT_SCALER_UNIT =
      new AttributeAddress(
          CLASS_ID_REGISTER, OBIS_CODE_INTERVAL_IMPORT_SCALER_UNIT, ATTRIBUTE_ID_SCALER_UNIT);
  static final AttributeAddress ATTRIBUTE_INTERVAL_EXPORT_SCALER_UNIT =
      new AttributeAddress(
          CLASS_ID_REGISTER, OBIS_CODE_INTERVAL_EXPORT_SCALER_UNIT, ATTRIBUTE_ID_SCALER_UNIT);

  static final AttributeAddress ATTRIBUTE_DAILY_OR_MONTHLY_IMPORT_RATE_1_SCALER_UNIT =
      new AttributeAddress(
          CLASS_ID_REGISTER, OBIS_CODE_MONTHLY_DAILY_IMPORT_RATE_1, ATTRIBUTE_ID_SCALER_UNIT);
  static final AttributeAddress ATTRIBUTE_DAILY_OR_MONTHLY_IMPORT_RATE_2_SCALER_UNIT =
      new AttributeAddress(
          CLASS_ID_REGISTER, OBIS_CODE_MONTHLY_DAILY_IMPORT_RATE_2, ATTRIBUTE_ID_SCALER_UNIT);
  static final AttributeAddress ATTRIBUTE_DAILY_OR_MONTHLY_EXPORT_RATE_1_SCALER_UNIT =
      new AttributeAddress(
          CLASS_ID_REGISTER, OBIS_CODE_MONTHLY_DAILY_EXPORT_RATE_1, ATTRIBUTE_ID_SCALER_UNIT);
  static final AttributeAddress ATTRIBUTE_DAILY_OR_MONTHLY_EXPORT_RATE_2_SCALER_UNIT =
      new AttributeAddress(
          CLASS_ID_REGISTER, OBIS_CODE_MONTHLY_DAILY_EXPORT_RATE_2, ATTRIBUTE_ID_SCALER_UNIT);

  private PeriodicMeterReadsConstants() {
    throw new IllegalStateException("Utility class");
  }
}
