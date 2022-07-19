/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.server.profile;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.CosemSnInterfaceObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.Clock;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.Long64Register;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.UnitType;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.e650.E650DedicatedEventLog;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.e650.E650DiagnosticRegister;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.e650.E650DoubleLongExtendedRegister;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.e650.E650DoubleLongUnsignedDemandRegister;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.e650.E650EventLog;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.e650.E650EventRegister;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.e650.E650LoadProfile1;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.e650.E650LoadProfile2;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.e650.E650OctedStringRegister;
import org.opensmartgridplatform.simulator.protocol.dlms.interceptor.OsgpServerConnectionListener;
import org.opensmartgridplatform.simulator.protocol.dlms.util.KeyPathProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

@Configuration
@Profile("e650")
public class E650Profile {

  private static final Logger LOGGER = LoggerFactory.getLogger(E650Profile.class);

  private static final int SCALER = -1;
  private static final int SCALER_NO_SCALING = 0;

  @Value("${connection.open.delay.min:0}")
  private int connectionSetupDelayMin;

  @Value("${connection.open.delay.max:0}")
  private int connectionSetupDelayMax;

  @Value("${default.clock.year}")
  private int defaultClockYear;

  @Value("${default.clock.month}")
  private int defaultClockMonth;

  @Value("${default.clock.dayOfMonth}")
  private int defaultClockDayOfMonth;

  @Value("${default.clock.hour}")
  private int defaultClockHour;

  @Value("${default.clock.minute}")
  private int defaultClockMinute;

  @Value("${default.clock.second}")
  private int defaultClockSecond;

  @Bean
  public KeyPathProvider keyPathProvider() {
    return new KeyPathProvider(null, null, null);
  }

  @Bean
  public OsgpServerConnectionListener osgpServerConnectionListener() {

    LOGGER.debug("connectionSetupDelay min is {}", this.connectionSetupDelayMin);
    LOGGER.debug("connectionSetupDelay max is {}", this.connectionSetupDelayMax);

    return new OsgpServerConnectionListener(
        this.connectionSetupDelayMin, this.connectionSetupDelayMax);
  }

  @Bean
  public Map<String, CosemInterfaceObject> cosemClasses(
      final org.springframework.context.ApplicationContext applicationContext) {
    final HashMap<String, CosemInterfaceObject> snCosemClasses = new HashMap<>();

    // change the return type of getBeansOfType to Map<String,
    // CosemInterfaceObject>
    for (final Map.Entry<String, CosemSnInterfaceObject> entry :
        applicationContext.getBeansOfType(CosemSnInterfaceObject.class).entrySet()) {
      snCosemClasses.put(entry.getKey(), entry.getValue());
    }
    return snCosemClasses;
  }

  @Bean
  public Clock clock() {
    return new Clock(LocalDateTime.now());
  }

  @Bean
  public E650EventRegister e650eventRegister() {
    return new E650EventRegister();
  }

  @Bean
  public E650OctedStringRegister e650errorRegister() {
    return new E650OctedStringRegister(0x448, "0.0.97.97.0.255", 4, 0, UnitType.CUBIC_METER);
  }

  @Bean
  @Scope("prototype")
  public Calendar defaultCalendar() {
    final Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, this.defaultClockYear);
    cal.set(Calendar.MONTH, this.defaultClockMonth);
    cal.set(Calendar.DAY_OF_MONTH, this.defaultClockDayOfMonth);
    cal.set(Calendar.HOUR_OF_DAY, this.defaultClockHour);
    cal.set(Calendar.MINUTE, this.defaultClockMinute);
    cal.set(Calendar.SECOND, this.defaultClockSecond);

    return cal;
  }

  @Bean
  public E650LoadProfile1 loadProfile1(final Calendar cal) {
    return new E650LoadProfile1(cal);
  }

  @Bean
  public E650LoadProfile2 loadProfile2(final Calendar cal) {

    return new E650LoadProfile2(cal);
  }

  @Bean
  public E650EventLog eventLog(final Calendar cal) {

    return new E650EventLog(cal);
  }

  @Bean
  public E650DedicatedEventLog underVoltageL1(final Calendar cal) {

    return new E650DedicatedEventLog(cal, 0xC858, "1.1.99.98.152.255", "1.1.32.23.0.255");
  }

  @Bean
  public E650DedicatedEventLog underVoltageL2(final Calendar cal) {

    return new E650DedicatedEventLog(cal, 0xC960, "1.1.99.98.153.255", "1.1.52.23.0.255");
  }

  @Bean
  public E650DedicatedEventLog underVoltageL3(final Calendar cal) {

    return new E650DedicatedEventLog(cal, 0xCA68, "1.1.99.98.154.255", "1.1.72.23.0.255");
  }

  @Bean
  public E650DedicatedEventLog overVoltageL1(final Calendar cal) {

    return new E650DedicatedEventLog(cal, 0xCB70, "1.1.99.98.155.255", "1.1.32.26.0.255");
  }

  @Bean
  public E650DedicatedEventLog overVoltageL2(final Calendar cal) {

    return new E650DedicatedEventLog(cal, 0xCC78, "1.1.99.98.156.255", "1.1.52.26.0.255");
  }

  @Bean
  public E650DedicatedEventLog overVoltageL3(final Calendar cal) {

    return new E650DedicatedEventLog(cal, 0xCD80, "1.1.99.98.157.255", "1.1.72.26.0.255");
  }

  @Bean
  public E650DedicatedEventLog phaseFailureL1(final Calendar cal) {

    return new E650DedicatedEventLog(cal, 0xCE88, "1.1.99.98.158.255", null);
  }

  @Bean
  public E650DedicatedEventLog phaseFailureL2(final Calendar cal) {

    return new E650DedicatedEventLog(cal, 0xCF90, "1.1.99.98.159.255", null);
  }

  @Bean
  public E650DedicatedEventLog phaseFailureL3(final Calendar cal) {

    return new E650DedicatedEventLog(cal, 0xD098, "1.1.99.98.160.255", null);
  }

  @Bean
  public Long64Register energyTotalRegisterM1() {
    return new Long64Register(0x1CE0, "1.1.1.8.0.255", 0L, SCALER, UnitType.WATT);
  }

  @Bean
  public Long64Register energyTotalRegisterM4() {
    return new Long64Register(0x1EF8, "1.1.2.8.0.255", 0L, SCALER, UnitType.WATT);
  }

  @Bean
  public E650DoubleLongUnsignedDemandRegister currentAverageActivePowerPlus() {
    return new E650DoubleLongUnsignedDemandRegister(
        0x33E8, "1.1.1.4.0.255", 1, SCALER, UnitType.WATT);
  }

  @Bean
  public E650DoubleLongUnsignedDemandRegister currentAverageActivePowerMinus() {
    return new E650DoubleLongUnsignedDemandRegister(
        0x3878, "1.1.2.4.0.255", 1, SCALER, UnitType.WATT);
  }

  @Bean
  public E650DoubleLongUnsignedDemandRegister currentAverageReactivePowerPlus() {
    return new E650DoubleLongUnsignedDemandRegister(
        0x3480, "1.1.3.4.0.255", 1, SCALER, UnitType.VAR);
  }

  @Bean
  public E650DoubleLongUnsignedDemandRegister currentAverageReactivePowerMinus() {
    return new E650DoubleLongUnsignedDemandRegister(
        0x3648, "1.1.4.4.0.255", 1, SCALER, UnitType.VAR);
  }

  @Bean
  public E650DiagnosticRegister instantPowerFactorL1() {
    return new E650DiagnosticRegister(
        0x9E68, "1.1.33.7.0.255", 1, SCALER_NO_SCALING, UnitType.COUNT);
  }

  @Bean
  public E650DiagnosticRegister instantPowerFactorL2() {
    return new E650DiagnosticRegister(
        0x9F00, "1.1.53.7.0.255", 1, SCALER_NO_SCALING, UnitType.COUNT);
  }

  @Bean
  public E650DiagnosticRegister instantPowerFactorL3() {
    return new E650DiagnosticRegister(
        0x9F98, "1.1.73.7.0.255", 1, SCALER_NO_SCALING, UnitType.COUNT);
  }

  @Bean
  public E650DiagnosticRegister allHarmonicsInstantVoltageL1() {
    return new E650DiagnosticRegister(0xA0C8, "1.0.32.7.126.255", 1, SCALER, UnitType.VOLT);
  }

  @Bean
  public E650DiagnosticRegister allHarmonicsInstantVoltageL2() {
    return new E650DiagnosticRegister(0xA160, "1.0.52.7.126.255", 1, SCALER, UnitType.VOLT);
  }

  @Bean
  public E650DiagnosticRegister allHarmonicsInstantVoltageL3() {
    return new E650DiagnosticRegister(0xA1F8, "1.0.72.7.126.255", 1, SCALER, UnitType.VOLT);
  }

  @Bean
  public E650DiagnosticRegister allHarmonicsInstantCurrentL1() {
    return new E650DiagnosticRegister(0xA328, "1.0.31.7.126.255", 1, SCALER, UnitType.AMPERE);
  }

  @Bean
  public E650DiagnosticRegister allHarmonicsInstantCurrentL2() {
    return new E650DiagnosticRegister(0xA3C0, "1.0.51.7.126.255", 1, SCALER, UnitType.AMPERE);
  }

  @Bean
  public E650DiagnosticRegister allHarmonicsInstantCurrentL3() {
    return new E650DiagnosticRegister(0xA458, "1.0.71.7.126.255", 1, SCALER, UnitType.AMPERE);
  }

  @Bean
  public E650DiagnosticRegister totalInstantVoltageL1() {
    return new E650DiagnosticRegister(0x9450, "1.1.32.7.0.255", 1, SCALER, UnitType.VOLT);
  }

  @Bean
  public E650DiagnosticRegister totalInstantVoltageL2() {
    return new E650DiagnosticRegister(0x94E8, "1.1.52.7.0.255", 1, SCALER, UnitType.VOLT);
  }

  @Bean
  public E650DiagnosticRegister totalInstantVoltageL3() {
    return new E650DiagnosticRegister(0x9580, "1.1.72.7.0.255", 1, SCALER, UnitType.VOLT);
  }

  @Bean
  public E650DiagnosticRegister totalInstantCurrentL1() {
    return new E650DiagnosticRegister(0x9618, "1.1.31.7.0.255", 1, SCALER, UnitType.AMPERE);
  }

  @Bean
  public E650DiagnosticRegister totalInstantCurrentL2() {
    return new E650DiagnosticRegister(0x96B0, "1.1.51.7.0.255", 1, SCALER, UnitType.AMPERE);
  }

  @Bean
  public E650DiagnosticRegister totalInstantCurrentL3() {
    return new E650DiagnosticRegister(0x9748, "1.1.71.7.0.255", 1, SCALER, UnitType.AMPERE);
  }

  @Bean
  public E650DiagnosticRegister totalInstantCurrentNeutral() {
    return new E650DiagnosticRegister(0x97E0, "1.1.91.7.0.255", 1, SCALER, UnitType.AMPERE);
  }

  @Bean
  public E650DiagnosticRegister totalInstantActivePowerL1() {
    return new E650DiagnosticRegister(0xB590, "1.1.36.7.0.255", 1, SCALER, UnitType.WATT);
  }

  @Bean
  public E650DiagnosticRegister totalInstantActivePowerL2() {
    return new E650DiagnosticRegister(0xB628, "1.1.56.7.0.255", 1, SCALER, UnitType.WATT);
  }

  @Bean
  public E650DiagnosticRegister totalInstantActivePowerL3() {
    return new E650DiagnosticRegister(0xB6C0, "1.1.76.7.0.255", 1, SCALER, UnitType.WATT);
  }

  @Bean
  public E650DoubleLongExtendedRegister min3L1Voltage() {
    return new E650DoubleLongExtendedRegister(0x7DE8, "1.1.32.23.0.255", 1, SCALER, UnitType.VOLT);
  }

  @Bean
  public E650DoubleLongExtendedRegister min3L2Voltage() {
    return new E650DoubleLongExtendedRegister(0x7E78, "1.1.52.23.0.255", 1, SCALER, UnitType.VOLT);
  }

  @Bean
  public E650DoubleLongExtendedRegister min3L3Voltage() {
    return new E650DoubleLongExtendedRegister(0x7F08, "1.1.72.23.0.255", 1, SCALER, UnitType.VOLT);
  }

  @Bean
  public E650DoubleLongExtendedRegister max3L1Voltage() {
    return new E650DoubleLongExtendedRegister(0x7F98, "1.1.32.26.0.255", 1, SCALER, UnitType.VOLT);
  }

  @Bean
  public E650DoubleLongExtendedRegister max3L2Voltage() {
    return new E650DoubleLongExtendedRegister(0x8028, "1.1.52.26.0.255", 1, SCALER, UnitType.VOLT);
  }

  @Bean
  public E650DoubleLongExtendedRegister max3L3Voltage() {
    return new E650DoubleLongExtendedRegister(0x80B8, "1.1.72.26.0.255", 1, SCALER, UnitType.VOLT);
  }
}
