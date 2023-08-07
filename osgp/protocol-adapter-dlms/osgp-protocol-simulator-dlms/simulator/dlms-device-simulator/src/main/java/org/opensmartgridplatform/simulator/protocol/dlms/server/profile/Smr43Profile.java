// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.server.profile;

import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.AVERAGE_ACTIVE_POWER_EXPORT_L1_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.AVERAGE_ACTIVE_POWER_EXPORT_L2_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.AVERAGE_ACTIVE_POWER_EXPORT_L3_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.AVERAGE_ACTIVE_POWER_IMPORT_L1_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.AVERAGE_ACTIVE_POWER_IMPORT_L2_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.AVERAGE_ACTIVE_POWER_IMPORT_L3_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.AVERAGE_CURRENT_L1_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.AVERAGE_REACTIVE_POWER_EXPORT_L1_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.AVERAGE_REACTIVE_POWER_EXPORT_L2_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.AVERAGE_REACTIVE_POWER_EXPORT_L3_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.AVERAGE_REACTIVE_POWER_IMPORT_L1_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.AVERAGE_REACTIVE_POWER_IMPORT_L2_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.AVERAGE_REACTIVE_POWER_IMPORT_L3_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.AVERAGE_VOLTAGE_L1_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.AVERAGE_VOLTAGE_L2_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.AVERAGE_VOLTAGE_L3_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.CLOCK_TIME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.INSTANTANEOUS_CURRENT_L1_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.INSTANTANEOUS_CURRENT_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.INSTANTANEOUS_VOLTAGE_L1_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.INSTANTANEOUS_VOLTAGE_L2_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.INSTANTANEOUS_VOLTAGE_L3_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.NUMBER_OF_LONG_POWER_FAILURES_IN_ANY_PHASE_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.NUMBER_OF_POWER_FAILURES_IN_ANY_PHASE_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L1_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L2_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L3_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L1_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L2_VALUE;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L3_VALUE;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.CosemDateTime.ClockStatus;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ProfileGenericAttribute;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.AuxiliaryEventLog;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.CaptureObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.GsmDiagnostic;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.GsmDiagnostic.AdjacentCellInfo;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.GsmDiagnostic.CellInfo;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.MBusClearStatusMask;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.MBusReadStatus;
import org.opensmartgridplatform.simulator.protocol.dlms.util.DynamicValues;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("smr43")
public class Smr43Profile {
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

  @Value("${gsmdiagnostic.operator}")
  private String gsmDiagnosticOperator;

  @Value("${gsmdiagnostic.status}")
  private int gsmDiagnosticStatus;

  @Value("${gsmdiagnostic.csattachment}")
  private int gsmDiagnosticCsAttachment;

  @Value("${gsmdiagnostic.psstatus}")
  private int gsmDiagnosticPsStatus;

  @Value("${gsmdiagnostic.cellinfo.cellid}")
  private int gsmDiagnosticCellInfoCellId;

  @Value("${gsmdiagnostic.cellinfo.locationid}")
  private int gsmDiagnosticCellInfoLocationId;

  @Value("${gsmdiagnostic.cellinfo.signalquality}")
  private short gsmDiagnosticCellInfoSignalQuality;

  @Value("${gsmdiagnostic.cellinfo.ber}")
  private short gsmDiagnosticCellInfoBer;

  @Value("${gsmdiagnostic.cellinfo.mcc}")
  private int gsmDiagnosticCellInfoMcc;

  @Value("${gsmdiagnostic.cellinfo.mnc}")
  private int gsmDiagnosticCellInfoMnc;

  @Value("${gsmdiagnostic.cellinfo.channelnumber}")
  private int gsmDiagnosticCellInfoChannelNumber;

  @Value("#{'${gsmdiagnostic.adjacentcells.cellids}'.split(',')}")
  private List<Integer> gsmDiagnosticAdjacentCellsCellIds;

  @Value("#{'${gsmdiagnostic.adjacentcells.signalqualities}'.split(',')}")
  private List<Short> gsmDiagnosticAdjacentCellsSignalQualities;

  @Value("${gsmdiagnostic.capturetime.year}")
  private int gsmDiagnosticYear;

  @Value("${gsmdiagnostic.capturetime.month}")
  private int gsmDiagnosticMonth;

  @Value("${gsmdiagnostic.capturetime.dayOfMonth}")
  private int gsmDiagnosticDayOfMonth;

  @Value("${gsmdiagnostic.capturetime.dayOfWeek}")
  private int gsmDiagnosticDayOfWeek;

  @Value("${gsmdiagnostic.capturetime.hour}")
  private int gsmDiagnosticHour;

  @Value("${gsmdiagnostic.capturetime.minute}")
  private int gsmDiagnosticMinute;

  @Value("${gsmdiagnostic.capturetime.second}")
  private int gsmDiagnosticSecond;

  @Value("${gsmdiagnostic.capturetime.hundredths}")
  private int gsmDiagnosticHundredths;

  @Value("${gsmdiagnostic.capturetime.deviation}")
  private int gsmDiagnosticDeviation;

  @Value("${gsmdiagnostic.capturetime.clockstatus}")
  private byte gsmDiagnosticClockStatus;

  @Value("${clear.status.mask.mbus1}")
  private long clearStatusMaskMBus1;

  @Value("${clear.status.mask.mbus2}")
  private long clearStatusMaskMBus2;

  @Value("${clear.status.mask.mbus3}")
  private long clearStatusMaskMBus3;

  @Value("${clear.status.mask.mbus4}")
  private long clearStatusMaskMBus4;

  @Bean
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

  @Bean
  public GsmDiagnostic gsmCdmaDiagnostic() {
    final CellInfo cellInfo =
        new CellInfo(
            this.gsmDiagnosticCellInfoCellId,
            this.gsmDiagnosticCellInfoLocationId,
            this.gsmDiagnosticCellInfoSignalQuality,
            this.gsmDiagnosticCellInfoBer,
            this.gsmDiagnosticCellInfoMcc,
            this.gsmDiagnosticCellInfoMnc,
            this.gsmDiagnosticCellInfoChannelNumber);

    final List<AdjacentCellInfo> adjacentCellInfos =
        IntStream.range(0, this.gsmDiagnosticAdjacentCellsCellIds.size())
            .mapToObj(
                i ->
                    new AdjacentCellInfo(
                        this.gsmDiagnosticAdjacentCellsCellIds.get(i),
                        this.gsmDiagnosticAdjacentCellsSignalQualities.get(i)))
            .collect(Collectors.toList());

    final CosemDateTime captureTime =
        new CosemDateTime(
            this.gsmDiagnosticYear,
            this.gsmDiagnosticMonth,
            this.gsmDiagnosticDayOfMonth,
            this.gsmDiagnosticDayOfWeek,
            this.gsmDiagnosticHour,
            this.gsmDiagnosticMinute,
            this.gsmDiagnosticSecond,
            this.gsmDiagnosticHundredths,
            this.gsmDiagnosticDeviation,
            ClockStatus.clockStatusFrom(this.gsmDiagnosticClockStatus).toArray(new ClockStatus[0]));
    return new GsmDiagnostic(
        "0.1.25.6.0.255",
        this.gsmDiagnosticOperator,
        this.gsmDiagnosticStatus,
        this.gsmDiagnosticCsAttachment,
        this.gsmDiagnosticPsStatus,
        cellInfo,
        adjacentCellInfos,
        captureTime);
  }

  @Bean
  public AuxiliaryEventLog auxiliaryEventLog(final Calendar cal) {
    return new AuxiliaryEventLog(cal);
  }

  @Bean
  public MBusClearStatusMask mBusClearStatusMask1() {
    return new MBusClearStatusMask(1, this.clearStatusMaskMBus1);
  }

  @Bean
  public MBusClearStatusMask mBusClearStatusMask2() {
    return new MBusClearStatusMask(2, this.clearStatusMaskMBus2);
  }

  @Bean
  public MBusClearStatusMask mBusClearStatusMask3() {
    return new MBusClearStatusMask(3, this.clearStatusMaskMBus3);
  }

  @Bean
  public MBusClearStatusMask mBusClearStatusMask4() {
    return new MBusClearStatusMask(4, this.clearStatusMaskMBus4);
  }

  @Bean
  public MBusReadStatus mBusReadStatusChannel1() {
    return new MBusReadStatus(1);
  }

  @Bean
  public MBusReadStatus mBusReadStatusChannel2() {
    return new MBusReadStatus(2);
  }

  @Bean
  public MBusReadStatus mBusReadStatusChannel3() {
    return new MBusReadStatus(3);
  }

  @Bean
  public MBusReadStatus mBusReadStatusChannel4() {
    return new MBusReadStatus(4);
  }
}
