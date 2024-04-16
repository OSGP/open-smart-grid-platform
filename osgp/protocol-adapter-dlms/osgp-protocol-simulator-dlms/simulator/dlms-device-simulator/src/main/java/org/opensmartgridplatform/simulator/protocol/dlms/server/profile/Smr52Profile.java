// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.server.profile;

import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.AlarmObject.ALARM_OBJECT_2;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.CosemDateTime.ClockStatus;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DataAttribute;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.AlarmFilter;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.AlarmObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.GsmDiagnostic;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.GsmDiagnostic.AdjacentCellInfo;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.GsmDiagnostic.CellInfo;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.MinDurationCurrentTHDOverlimitNormalToOver;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.MinDurationCurrentTHDOverlimitOverToNormal;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityExtendedEventLog;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityThdEventLog;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.TimeThresholdCurrentTHDOverlimit;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ValueHysteresisCurrentTHDOverlimit;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ValueThresholdCurrentTHDOverlimit;
import org.opensmartgridplatform.simulator.protocol.dlms.util.DynamicValues;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("smr52")
public class Smr52Profile {
  @Value("${alarmobject.register2.value}")
  private int alarmRegister2Value;

  @Value("${alarmfilter2.value}")
  private int alarmFilter2Value;

  @Value("${ltediagnostic.operator}")
  private String lteDiagnosticOperator;

  @Value("${ltediagnostic.status}")
  private int lteDiagnosticStatus;

  @Value("${ltediagnostic.csattachment}")
  private int lteDiagnosticCsAttachment;

  @Value("${ltediagnostic.psstatus}")
  private int lteDiagnosticPsStatus;

  @Value("${ltediagnostic.cellinfo.cellid}")
  private int lteDiagnosticCellInfoCellId;

  @Value("${ltediagnostic.cellinfo.locationid}")
  private int lteDiagnosticCellInfoLocationId;

  @Value("${ltediagnostic.cellinfo.signalquality}")
  private short lteDiagnosticCellInfoSignalQuality;

  @Value("${ltediagnostic.cellinfo.ber}")
  private short lteDiagnosticCellInfoBer;

  @Value("${ltediagnostic.cellinfo.mcc}")
  private int lteDiagnosticCellInfoMcc;

  @Value("${ltediagnostic.cellinfo.mnc}")
  private int lteDiagnosticCellInfoMnc;

  @Value("${ltediagnostic.cellinfo.channelnumber}")
  private int lteDiagnosticCellInfoChannelNumber;

  @Value("#{'${ltediagnostic.adjacentcells.cellids}'.split(',')}")
  private List<Integer> lteDiagnosticAdjacentCellsCellIds;

  @Value("#{'${ltediagnostic.adjacentcells.signalqualities}'.split(',')}")
  private List<Short> lteDiagnosticAdjacentCellsSignalQualities;

  @Value("${ltediagnostic.capturetime.year}")
  private int lteDiagnosticYear;

  @Value("${ltediagnostic.capturetime.month}")
  private int lteDiagnosticMonth;

  @Value("${ltediagnostic.capturetime.dayOfMonth}")
  private int lteDiagnosticDayOfMonth;

  @Value("${ltediagnostic.capturetime.dayOfWeek}")
  private int lteDiagnosticDayOfWeek;

  @Value("${ltediagnostic.capturetime.hour}")
  private int lteDiagnosticHour;

  @Value("${ltediagnostic.capturetime.minute}")
  private int lteDiagnosticMinute;

  @Value("${ltediagnostic.capturetime.second}")
  private int lteDiagnosticSecond;

  @Value("${ltediagnostic.capturetime.hundredths}")
  private int lteDiagnosticHundredths;

  @Value("${ltediagnostic.capturetime.deviation}")
  private int lteDiagnosticDeviation;

  @Value("${ltediagnostic.capturetime.clockstatus}")
  private byte lteDiagnosticClockStatus;

  @Value("${thd.configuration.minDuration.normaltoover}")
  private long minDurationCurrentTHDOverlimitNormalToOver;

  @Value("${thd.configuration.minDuration.overtonormal}")
  private long minDurationCurrentTHDOverlimitOverToNormal;

  @Value("${thd.configuration.time.threshold}")
  private long timeThresholdCurrentTHDOverlimit;

  @Value("${thd.configuration.value.hysteresis}")
  private int valueHysteresisCurrentTHDOverlimit;

  @Value("${thd.configuration.value.threshold}")
  private int valueThresholdCurrentTHDOverlimit;

  @Bean
  public AlarmObject alarmObject2(final DynamicValues dynamicValues) {
    dynamicValues.setDefaultAttributeValue(
        InterfaceClass.DATA.id(),
        new ObisCode(0, 0, 97, 98, 1, 255),
        DataAttribute.VALUE.attributeId(),
        DataObject.newUInteger32Data(this.alarmRegister2Value));
    return new AlarmObject(ALARM_OBJECT_2);
  }

  @Bean
  public AlarmFilter alarmFilter2() {
    return new AlarmFilter("0.0.97.98.11.255", this.alarmFilter2Value);
  }

  @Bean
  public PowerQualityExtendedEventLog powerQualityExtendedEventLog(final Calendar cal) {
    return new PowerQualityExtendedEventLog(cal);
  }

  @Bean
  public PowerQualityThdEventLog powerQualityThdEventLog(final Calendar cal) {
    return new PowerQualityThdEventLog(cal);
  }

  @Bean
  public MinDurationCurrentTHDOverlimitNormalToOver minDurationCurrentTHDOverlimitNormalToOver() {
    return new MinDurationCurrentTHDOverlimitNormalToOver(
        this.minDurationCurrentTHDOverlimitNormalToOver);
  }

  @Bean
  public MinDurationCurrentTHDOverlimitOverToNormal minDurationCurrentTHDOverlimitOverToNormal() {
    return new MinDurationCurrentTHDOverlimitOverToNormal(
        this.minDurationCurrentTHDOverlimitOverToNormal);
  }

  @Bean
  public TimeThresholdCurrentTHDOverlimit timeThresholdCurrentTHDOverlimit() {
    return new TimeThresholdCurrentTHDOverlimit(this.timeThresholdCurrentTHDOverlimit);
  }

  @Bean
  public ValueHysteresisCurrentTHDOverlimit valueHysteresisCurrentTHDOverlimit() {
    return new ValueHysteresisCurrentTHDOverlimit(this.valueHysteresisCurrentTHDOverlimit);
  }

  @Bean
  public ValueThresholdCurrentTHDOverlimit valueThresholdCurrentTHDOverlimit() {
    return new ValueThresholdCurrentTHDOverlimit(this.valueThresholdCurrentTHDOverlimit);
  }

  @Bean
  public GsmDiagnostic gsmLteDiagnostic() {
    final CellInfo cellInfo =
        new CellInfo(
            this.lteDiagnosticCellInfoCellId,
            this.lteDiagnosticCellInfoLocationId,
            this.lteDiagnosticCellInfoSignalQuality,
            this.lteDiagnosticCellInfoBer,
            this.lteDiagnosticCellInfoMcc,
            this.lteDiagnosticCellInfoMnc,
            this.lteDiagnosticCellInfoChannelNumber);

    final List<AdjacentCellInfo> adjacentCellInfos =
        IntStream.range(0, this.lteDiagnosticAdjacentCellsCellIds.size())
            .mapToObj(
                i ->
                    new AdjacentCellInfo(
                        this.lteDiagnosticAdjacentCellsCellIds.get(i),
                        this.lteDiagnosticAdjacentCellsSignalQualities.get(i)))
            .collect(Collectors.toList());

    final CosemDateTime captureTime =
        new CosemDateTime(
            this.lteDiagnosticYear,
            this.lteDiagnosticMonth,
            this.lteDiagnosticDayOfMonth,
            this.lteDiagnosticDayOfWeek,
            this.lteDiagnosticHour,
            this.lteDiagnosticMinute,
            this.lteDiagnosticSecond,
            this.lteDiagnosticHundredths,
            this.lteDiagnosticDeviation,
            ClockStatus.clockStatusFrom(this.lteDiagnosticClockStatus).toArray(new ClockStatus[0]));

    return new GsmDiagnostic(
        "0.2.25.6.0.255",
        this.lteDiagnosticOperator,
        this.lteDiagnosticStatus,
        this.lteDiagnosticCsAttachment,
        this.lteDiagnosticPsStatus,
        cellInfo,
        adjacentCellInfos,
        captureTime);
  }
}
