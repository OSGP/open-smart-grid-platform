// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.server.profile;

import java.util.Calendar;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.AuxiliaryEventLog;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.MBusClearStatusMask;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.MBusReadStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("smr51")
public class Smr51Profile {

  @Value("${clear.status.mask.mbus1}")
  private long clearStatusMaskMBus1;

  @Value("${clear.status.mask.mbus2}")
  private long clearStatusMaskMBus2;

  @Value("${clear.status.mask.mbus3}")
  private long clearStatusMaskMBus3;

  @Value("${clear.status.mask.mbus4}")
  private long clearStatusMaskMBus4;

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
