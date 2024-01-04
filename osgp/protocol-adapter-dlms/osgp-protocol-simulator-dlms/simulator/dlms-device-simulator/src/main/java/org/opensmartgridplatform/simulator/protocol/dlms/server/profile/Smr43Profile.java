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
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile.CDMA_DIAGNOSTIC_SIGNAL_QUALITY;
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
          CDMA_DIAGNOSTIC_SIGNAL_QUALITY,
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

  @Value("${cdmadiagnostic.operator}")
  private String cdmaDiagnosticOperator;

  @Value("${cdmadiagnostic.status}")
  private int cdmaDiagnosticStatus;

  @Value("${cdmadiagnostic.csattachment}")
  private int cdmaDiagnosticCsAttachment;

  @Value("${cdmadiagnostic.psstatus}")
  private int cdmaDiagnosticPsStatus;

  @Value("${cdmadiagnostic.cellinfo.cellid}")
  private int cdmaDiagnosticCellInfoCellId;

  @Value("${cdmadiagnostic.cellinfo.locationid}")
  private int cdmaDiagnosticCellInfoLocationId;

  @Value("${cdmadiagnostic.cellinfo.signalquality}")
  private short cdmaDiagnosticCellInfoSignalQuality;

  @Value("${cdmadiagnostic.cellinfo.ber}")
  private short cdmaDiagnosticCellInfoBer;

  @Value("${cdmadiagnostic.cellinfo.mcc}")
  private int cdmaDiagnosticCellInfoMcc;

  @Value("${cdmadiagnostic.cellinfo.mnc}")
  private int cdmaDiagnosticCellInfoMnc;

  @Value("${cdmadiagnostic.cellinfo.channelnumber}")
  private int cdmaDiagnosticCellInfoChannelNumber;

  @Value("#{'${cdmadiagnostic.adjacentcells.cellids}'.split(',')}")
  private List<Integer> cdmaDiagnosticAdjacentCellsCellIds;

  @Value("#{'${cdmadiagnostic.adjacentcells.signalqualities}'.split(',')}")
  private List<Short> cdmaDiagnosticAdjacentCellsSignalQualities;

  @Value("${cdmadiagnostic.capturetime.year}")
  private int cdmaDiagnosticYear;

  @Value("${cdmadiagnostic.capturetime.month}")
  private int cdmaDiagnosticMonth;

  @Value("${cdmadiagnostic.capturetime.dayOfMonth}")
  private int cdmaDiagnosticDayOfMonth;

  @Value("${cdmadiagnostic.capturetime.dayOfWeek}")
  private int cdmaDiagnosticDayOfWeek;

  @Value("${cdmadiagnostic.capturetime.hour}")
  private int cdmaDiagnosticHour;

  @Value("${cdmadiagnostic.capturetime.minute}")
  private int cdmaDiagnosticMinute;

  @Value("${cdmadiagnostic.capturetime.second}")
  private int cdmaDiagnosticSecond;

  @Value("${cdmadiagnostic.capturetime.hundredths}")
  private int cdmaDiagnosticHundredths;

  @Value("${cdmadiagnostic.capturetime.deviation}")
  private int cdmaDiagnosticDeviation;

  @Value("${cdmadiagnostic.capturetime.clockstatus}")
  private byte cdmaDiagnosticClockStatus;

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
            this.cdmaDiagnosticCellInfoCellId,
            this.cdmaDiagnosticCellInfoLocationId,
            this.cdmaDiagnosticCellInfoSignalQuality,
            this.cdmaDiagnosticCellInfoBer,
            this.cdmaDiagnosticCellInfoMcc,
            this.cdmaDiagnosticCellInfoMnc,
            this.cdmaDiagnosticCellInfoChannelNumber);

    final List<AdjacentCellInfo> adjacentCellInfos =
        IntStream.range(0, this.cdmaDiagnosticAdjacentCellsCellIds.size())
            .mapToObj(
                i ->
                    new AdjacentCellInfo(
                        this.cdmaDiagnosticAdjacentCellsCellIds.get(i),
                        this.cdmaDiagnosticAdjacentCellsSignalQualities.get(i)))
            .collect(Collectors.toList());

    final CosemDateTime captureTime =
        new CosemDateTime(
            this.cdmaDiagnosticYear,
            this.cdmaDiagnosticMonth,
            this.cdmaDiagnosticDayOfMonth,
            this.cdmaDiagnosticDayOfWeek,
            this.cdmaDiagnosticHour,
            this.cdmaDiagnosticMinute,
            this.cdmaDiagnosticSecond,
            this.cdmaDiagnosticHundredths,
            this.cdmaDiagnosticDeviation,
            ClockStatus.clockStatusFrom(this.cdmaDiagnosticClockStatus)
                .toArray(new ClockStatus[0]));
    return new GsmDiagnostic(
        "0.1.25.6.0.255",
        this.cdmaDiagnosticOperator,
        this.cdmaDiagnosticStatus,
        this.cdmaDiagnosticCsAttachment,
        this.cdmaDiagnosticPsStatus,
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
