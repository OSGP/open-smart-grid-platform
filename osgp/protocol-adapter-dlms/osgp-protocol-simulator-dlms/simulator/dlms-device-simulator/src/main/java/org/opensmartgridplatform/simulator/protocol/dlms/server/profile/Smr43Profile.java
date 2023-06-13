// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.server.profile;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.CosemDateTime.ClockStatus;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.AuxiliaryEventLog;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.GsmDiagnostic;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.GsmDiagnostic.AdjacentCellInfo;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.GsmDiagnostic.CellInfo;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.MBusClearStatusMask;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.MBusReadStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("smr43")
public class Smr43Profile {
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
