// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.server.profile;

import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ProfileGenericAttribute;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.CaptureObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile;
import org.opensmartgridplatform.simulator.protocol.dlms.util.DynamicValues;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dsmr422")
public class Dsmr422Profile {
  private static final List<CaptureObject> DEFAULT_CAPTURE_OBJECTS =
      Arrays.asList(
          CLOCK_TIME,
          INSTANTANEOUS_VOLTAGE_L1_VALUE,
          INSTANTANEOUS_VOLTAGE_L2_VALUE,
          INSTANTANEOUS_VOLTAGE_L3_VALUE,
          AVERAGE_VOLTAGE_L1_VALUE,
          AVERAGE_VOLTAGE_L2_VALUE,
          AVERAGE_VOLTAGE_L3_VALUE,
          INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1_VALUE,
          INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1_VALUE,
          AVERAGE_CURRENT_L1_VALUE,
          AVERAGE_ACTIVE_POWER_IMPORT_L1_VALUE,
          AVERAGE_ACTIVE_POWER_IMPORT_L2_VALUE,
          AVERAGE_ACTIVE_POWER_IMPORT_L3_VALUE,
          AVERAGE_ACTIVE_POWER_EXPORT_L1_VALUE,
          AVERAGE_ACTIVE_POWER_EXPORT_L2_VALUE,
          AVERAGE_ACTIVE_POWER_EXPORT_L3_VALUE,
          AVERAGE_REACTIVE_POWER_IMPORT_L1_VALUE,
          AVERAGE_REACTIVE_POWER_IMPORT_L2_VALUE,
          AVERAGE_REACTIVE_POWER_IMPORT_L3_VALUE,
          AVERAGE_REACTIVE_POWER_EXPORT_L1_VALUE,
          AVERAGE_REACTIVE_POWER_EXPORT_L2_VALUE,
          AVERAGE_REACTIVE_POWER_EXPORT_L3_VALUE,
          NUMBER_OF_LONG_POWER_FAILURES_IN_ANY_PHASE_VALUE,
          NUMBER_OF_POWER_FAILURES_IN_ANY_PHASE_VALUE,
          NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L1_VALUE,
          NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L1_VALUE,
          NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L2_VALUE,
          NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L2_VALUE,
          NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L3_VALUE,
          NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L3_VALUE,
          INSTANTANEOUS_CURRENT_L1_VALUE,
          INSTANTANEOUS_CURRENT_VALUE);

  @Bean
  @Profile("!smr43")
  public DefinableLoadProfile definableLoadProfile(
      final Calendar cal, final DynamicValues dynamicValues) {
    final Integer classId = InterfaceClass.PROFILE_GENERIC.id();
    final ObisCode obisCode = new ObisCode(0, 1, 94, 31, 6, 255);
    dynamicValues.setDefaultAttributeValue(
        classId,
        obisCode,
        ProfileGenericAttribute.CAPTURE_PERIOD.attributeId(),
        DataObject.newUInteger32Data(300));
    dynamicValues.setDefaultAttributeValue(
        classId,
        obisCode,
        ProfileGenericAttribute.PROFILE_ENTRIES.attributeId(),
        DataObject.newUInteger32Data(960));

    return new DefinableLoadProfile(dynamicValues, cal, null, DEFAULT_CAPTURE_OBJECTS);
  }
}
