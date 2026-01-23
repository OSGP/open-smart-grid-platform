/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.simulator.protocol.dlms.server.profile;

import java.util.Calendar;
import java.util.List;
import java.util.stream.IntStream;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityThdEventLog;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ThdCurrentOverLimitCounter;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ThdInstantaneousCurrent;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ThdInstantaneousCurrentFingerprint;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ThdMinDurationNormalToOver;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ThdMinDurationOverToNormal;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ThdTimeThreshold;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ThdValueHysteresis;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ThdValueThreshold;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("smr52c")
public class Smr52cProfile {

  @Value("${thd.configuration.minDuration.normaltoover}")
  private long thdMinDurationNormalToOver;

  @Value("${thd.configuration.minDuration.overtonormal}")
  private long thdMinDurationOverToNormal;

  @Value("${thd.configuration.time.threshold}")
  private long thdTimeThreshold;

  @Value("${thd.configuration.value.hysteresis}")
  private int thdValueHysteresis;

  @Value("${thd.configuration.value.threshold}")
  private int thdValueThreshold;

  @Bean
  public PowerQualityThdEventLog powerQualityThdEventLog(final Calendar cal) {
    return new PowerQualityThdEventLog(cal);
  }

  @Bean
  public ThdMinDurationNormalToOver thdMinDurationNormalToOver() {
    return new ThdMinDurationNormalToOver(this.thdMinDurationNormalToOver);
  }

  @Bean
  public ThdMinDurationOverToNormal thdMinDurationOverToNormal() {
    return new ThdMinDurationOverToNormal(this.thdMinDurationOverToNormal);
  }

  @Bean
  public ThdTimeThreshold thdTimeThreshold() {
    return new ThdTimeThreshold(this.thdTimeThreshold);
  }

  @Bean
  public ThdValueHysteresis thdValueHysteresis() {
    return new ThdValueHysteresis(this.thdValueHysteresis);
  }

  @Bean
  public ThdValueThreshold thdValueThreshold() {
    return new ThdValueThreshold(this.thdValueThreshold);
  }

  @Bean
  public ThdInstantaneousCurrent thdInstantaneousCurrentL1() {
    return new ThdInstantaneousCurrent(317, "1.0.31.7.124.255");
  }

  @Bean
  public ThdInstantaneousCurrent thdInstantaneousCurrentL2() {
    return new ThdInstantaneousCurrent(517, "1.0.51.7.124.255");
  }

  @Bean
  public ThdInstantaneousCurrent thdInstantaneousCurrentL3() {
    return new ThdInstantaneousCurrent(717, "1.0.71.7.124.255");
  }

  @Bean
  public ThdInstantaneousCurrentFingerprint thdInstantaneousCurrentFingerprintL1() {
    final List<DataObject> values =
        IntStream.rangeClosed(101, 115).boxed().map(DataObject::newUInteger16Data).toList();
    return new ThdInstantaneousCurrentFingerprint(values, "0.1.94.31.24.255");
  }

  @Bean
  public ThdInstantaneousCurrentFingerprint thdInstantaneousCurrentFingerprintL2() {
    final List<DataObject> values =
        IntStream.rangeClosed(201, 215).boxed().map(DataObject::newUInteger16Data).toList();
    return new ThdInstantaneousCurrentFingerprint(values, "0.1.94.31.25.255");
  }

  @Bean
  public ThdInstantaneousCurrentFingerprint thdInstantaneousCurrentFingerprintL3() {
    final List<DataObject> values =
        IntStream.rangeClosed(301, 315).boxed().map(DataObject::newUInteger16Data).toList();
    return new ThdInstantaneousCurrentFingerprint(values, "0.1.94.31.26.255");
  }

  @Bean
  public ThdCurrentOverLimitCounter thdCurrentOverLimitCounterL1() {
    return new ThdCurrentOverLimitCounter(3136, "1.0.31.36.124.255");
  }

  @Bean
  public ThdCurrentOverLimitCounter thdCurrentOverLimitCounterL2() {
    return new ThdCurrentOverLimitCounter(5136, "1.0.51.36.124.255");
  }

  @Bean
  public ThdCurrentOverLimitCounter thdCurrentOverLimitCounterL3() {
    return new ThdCurrentOverLimitCounter(7136, "1.0.71.36.124.255");
  }
}
