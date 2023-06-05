// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.ServerModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.QualityType;

public class Engine extends LogicalDevice {

  public Engine(
      final String physicalDeviceName,
      final String logicalDeviceName,
      final ServerModel serverModel) {
    super(physicalDeviceName, logicalDeviceName, serverModel);
  }

  @Override
  public List<BasicDataAttribute> getAttributesAndSetValues(final Date timestamp) {
    final List<BasicDataAttribute> values = new ArrayList<>();

    values.add(this.setRandomByte(LogicalDeviceNode.LLN0_HEALTH_STVAL, 1, 2));
    values.add(this.setQuality(LogicalDeviceNode.LLN0_HEALTH_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.LLN0_HEALTH_T, timestamp));

    values.add(this.setRandomByte(LogicalDeviceNode.LLN0_BEH_STVAL, 1, 2));
    values.add(this.setQuality(LogicalDeviceNode.LLN0_BEH_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.LLN0_BEH_T, timestamp));

    values.add(this.setRandomByte(LogicalDeviceNode.LLN0_MOD_STVAL, 1, 2));
    values.add(this.setQuality(LogicalDeviceNode.LLN0_MOD_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.LLN0_MOD_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU1_MAXWPHS_MAG_F, 500, 1000));
    values.add(this.setQuality(LogicalDeviceNode.MMXU1_MAXWPHS_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU1_MAXWPHS_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU1_MINWPHS_MAG_F, 0, 500));
    values.add(this.setQuality(LogicalDeviceNode.MMXU1_MINWPHS_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU1_MINWPHS_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.MMXU1_TOTW_MAG_F, 0, 1000));
    values.add(this.setQuality(LogicalDeviceNode.MMXU1_TOTW_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.MMXU1_TOTW_T, timestamp));

    values.add(this.setRandomFloat(LogicalDeviceNode.DRCC1_OUTWSET_SUBVAL_F, 0, 1000));
    values.add(this.setQuality(LogicalDeviceNode.DRCC1_OUTWSET_SUBQ, QualityType.VALIDITY_GOOD));

    values.add(this.setRandomFloat(LogicalDeviceNode.DGEN1_TOTWH_MAG_F, 0, 1000));
    values.add(this.setQuality(LogicalDeviceNode.DGEN1_TOTWH_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.DGEN1_TOTWH_T, timestamp));

    values.add(this.setRandomByte(LogicalDeviceNode.DGEN1_GNOPST_STVAL, 1, 2));
    values.add(this.setQuality(LogicalDeviceNode.DGEN1_GNOPST_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.DGEN1_GNOPST_T, timestamp));

    values.add(this.incrementInt(LogicalDeviceNode.DGEN1_OPTMSRS_STVAL));
    values.add(this.setQuality(LogicalDeviceNode.DGEN1_OPTMSRS_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.DGEN1_OPTMSRS_T, timestamp));

    values.add(this.setBoolean(LogicalDeviceNode.GGIO1_ALM1_STVAL, false));
    values.add(this.setQuality(LogicalDeviceNode.GGIO1_ALM1_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.GGIO1_ALM1_T, timestamp));

    values.add(this.setBoolean(LogicalDeviceNode.GGIO1_ALM2_STVAL, false));
    values.add(this.setQuality(LogicalDeviceNode.GGIO1_ALM2_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.GGIO1_ALM2_T, timestamp));

    values.add(this.setBoolean(LogicalDeviceNode.GGIO1_ALM3_STVAL, false));
    values.add(this.setQuality(LogicalDeviceNode.GGIO1_ALM3_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.GGIO1_ALM3_T, timestamp));

    values.add(this.setBoolean(LogicalDeviceNode.GGIO1_ALM4_STVAL, false));
    values.add(this.setQuality(LogicalDeviceNode.GGIO1_ALM4_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.GGIO1_ALM4_T, timestamp));

    values.add(this.setRandomInt(LogicalDeviceNode.GGIO1_INTIN1_STVAL, 1, 100));
    values.add(this.setQuality(LogicalDeviceNode.GGIO1_INTIN1_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.GGIO1_INTIN1_T, timestamp));

    values.add(this.setBoolean(LogicalDeviceNode.GGIO1_WRN1_STVAL, false));
    values.add(this.setQuality(LogicalDeviceNode.GGIO1_WRN1_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.GGIO1_WRN1_T, timestamp));

    values.add(this.setBoolean(LogicalDeviceNode.GGIO1_WRN2_STVAL, false));
    values.add(this.setQuality(LogicalDeviceNode.GGIO1_WRN2_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.GGIO1_WRN2_T, timestamp));

    values.add(this.setBoolean(LogicalDeviceNode.GGIO1_WRN3_STVAL, false));
    values.add(this.setQuality(LogicalDeviceNode.GGIO1_WRN3_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.GGIO1_WRN3_T, timestamp));

    values.add(this.setBoolean(LogicalDeviceNode.GGIO1_WRN4_STVAL, false));
    values.add(this.setQuality(LogicalDeviceNode.GGIO1_WRN4_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.GGIO1_WRN4_T, timestamp));

    values.add(this.setRandomInt(LogicalDeviceNode.GGIO1_INTIN2_STVAL, 1, 100));
    values.add(this.setQuality(LogicalDeviceNode.GGIO1_INTIN2_Q, QualityType.VALIDITY_GOOD));
    values.add(this.setTime(LogicalDeviceNode.GGIO1_INTIN2_T, timestamp));

    values.add(this.setRandomInt(LogicalDeviceNode.DSCH1_SCHDID_SETVAL, 1, 100));
    values.add(this.setRandomInt(LogicalDeviceNode.DSCH1_SCHDTYP_SETVAL, 1, 100));
    values.add(this.setRandomInt(LogicalDeviceNode.DSCH1_SCHDCAT_SETVAL, 1, 100));
    values.add(this.setRandomFloat(LogicalDeviceNode.DSCH1_SCHDABSTM_VAL_0, 0, 1000));
    values.add(this.setTime(LogicalDeviceNode.DSCH1_SCHDABSTM_TIME_0, timestamp));
    values.add(this.setRandomFloat(LogicalDeviceNode.DSCH1_SCHDABSTM_VAL_1, 0, 1000));
    values.add(this.setTime(LogicalDeviceNode.DSCH1_SCHDABSTM_TIME_1, timestamp));
    values.add(this.setRandomFloat(LogicalDeviceNode.DSCH1_SCHDABSTM_VAL_2, 0, 1000));
    values.add(this.setTime(LogicalDeviceNode.DSCH1_SCHDABSTM_TIME_2, timestamp));
    values.add(this.setRandomFloat(LogicalDeviceNode.DSCH1_SCHDABSTM_VAL_3, 0, 1000));
    values.add(this.setTime(LogicalDeviceNode.DSCH1_SCHDABSTM_TIME_3, timestamp));

    values.add(this.setRandomInt(LogicalDeviceNode.DSCH2_SCHDID_SETVAL, 1, 100));
    values.add(this.setRandomInt(LogicalDeviceNode.DSCH2_SCHDTYP_SETVAL, 1, 100));
    values.add(this.setRandomInt(LogicalDeviceNode.DSCH2_SCHDCAT_SETVAL, 1, 100));
    values.add(this.setRandomFloat(LogicalDeviceNode.DSCH2_SCHDABSTM_VAL_0, 0, 1000));
    values.add(this.setTime(LogicalDeviceNode.DSCH2_SCHDABSTM_TIME_0, timestamp));
    values.add(this.setRandomFloat(LogicalDeviceNode.DSCH2_SCHDABSTM_VAL_1, 0, 1000));
    values.add(this.setTime(LogicalDeviceNode.DSCH2_SCHDABSTM_TIME_1, timestamp));
    values.add(this.setRandomFloat(LogicalDeviceNode.DSCH2_SCHDABSTM_VAL_2, 0, 1000));
    values.add(this.setTime(LogicalDeviceNode.DSCH2_SCHDABSTM_TIME_2, timestamp));
    values.add(this.setRandomFloat(LogicalDeviceNode.DSCH2_SCHDABSTM_VAL_3, 0, 1000));
    values.add(this.setTime(LogicalDeviceNode.DSCH2_SCHDABSTM_TIME_3, timestamp));

    values.add(this.setRandomInt(LogicalDeviceNode.DSCH3_SCHDID_SETVAL, 1, 100));
    values.add(this.setRandomInt(LogicalDeviceNode.DSCH3_SCHDTYP_SETVAL, 1, 100));
    values.add(this.setRandomInt(LogicalDeviceNode.DSCH3_SCHDCAT_SETVAL, 1, 100));
    values.add(this.setRandomFloat(LogicalDeviceNode.DSCH3_SCHDABSTM_VAL_0, 0, 1000));
    values.add(this.setTime(LogicalDeviceNode.DSCH3_SCHDABSTM_TIME_0, timestamp));
    values.add(this.setRandomFloat(LogicalDeviceNode.DSCH3_SCHDABSTM_VAL_1, 0, 1000));
    values.add(this.setTime(LogicalDeviceNode.DSCH3_SCHDABSTM_TIME_1, timestamp));
    values.add(this.setRandomFloat(LogicalDeviceNode.DSCH3_SCHDABSTM_VAL_2, 0, 1000));
    values.add(this.setTime(LogicalDeviceNode.DSCH3_SCHDABSTM_TIME_2, timestamp));
    values.add(this.setRandomFloat(LogicalDeviceNode.DSCH3_SCHDABSTM_VAL_3, 0, 1000));
    values.add(this.setTime(LogicalDeviceNode.DSCH3_SCHDABSTM_TIME_3, timestamp));

    values.add(this.setRandomInt(LogicalDeviceNode.DSCH4_SCHDID_SETVAL, 1, 100));
    values.add(this.setRandomInt(LogicalDeviceNode.DSCH4_SCHDTYP_SETVAL, 1, 100));
    values.add(this.setRandomInt(LogicalDeviceNode.DSCH4_SCHDCAT_SETVAL, 1, 100));
    values.add(this.setRandomFloat(LogicalDeviceNode.DSCH4_SCHDABSTM_VAL_0, 0, 1000));
    values.add(this.setTime(LogicalDeviceNode.DSCH4_SCHDABSTM_TIME_0, timestamp));
    values.add(this.setRandomFloat(LogicalDeviceNode.DSCH4_SCHDABSTM_VAL_1, 0, 1000));
    values.add(this.setTime(LogicalDeviceNode.DSCH4_SCHDABSTM_TIME_1, timestamp));
    values.add(this.setRandomFloat(LogicalDeviceNode.DSCH4_SCHDABSTM_VAL_2, 0, 1000));
    values.add(this.setTime(LogicalDeviceNode.DSCH4_SCHDABSTM_TIME_2, timestamp));
    values.add(this.setRandomFloat(LogicalDeviceNode.DSCH4_SCHDABSTM_VAL_3, 0, 1000));
    values.add(this.setTime(LogicalDeviceNode.DSCH4_SCHDABSTM_TIME_3, timestamp));

    return values;
  }
}
