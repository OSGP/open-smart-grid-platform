/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.server.profile;

import java.util.Calendar;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.util.encoders.Hex;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.CosemDateTime.ClockStatus;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ConfigurationObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.DoubleLongUnsignedExtendedRegister;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.EMonthlyBillingValuesPeriod1SMR5;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.MBusDailyBillingValuesPeriod1SMR5;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.MBusDriverActiveFirmwareIdentifier;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.MBusDriverActiveFirmwareSignature;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.MBusMasterLoadProfilePeriod1SMR5;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.MBusMonthlyBillingValuesPeriod1SMR5;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.OctetStringExtendedRegister;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityEventLog;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile1;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.SingleActionScheduler;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.UnitType;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.smr5.InvocationCounter;
import org.opensmartgridplatform.simulator.protocol.dlms.rest.client.DlmsAttributeValuesClient;
import org.opensmartgridplatform.simulator.protocol.dlms.util.DynamicValues;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("smr5")
public class Smr5Profile {
  @Value("${firmware.mbusdriver.active.identifier}")
  private String mBusDriverActiveFirmwareIdentifier;

  @Value("${firmware.mbusdriver.active.signature}")
  private String mBusDriverActiveFirmwareSignature;

  @Value("${invocation.counter}")
  private long invocationCounter;

  @Value("${simple.version.info.mbus1.hexvalue}")
  private String simpleVersionInfoMbus1HexValue;

  @Value("${simple.version.info.mbus1.scaler}")
  private int simpleVersionInfoMbus1Scaler;

  @Value("${simple.version.info.mbus1.unit}")
  private UnitType simpeVersionInfoMbus1Unit;

  @Value("${simple.version.info.mbus2.hexvalue}")
  private String simpleVersionInfoMbus2HexValue;

  @Value("${simple.version.info.mbus2.scaler}")
  private int simpleVersionInfoMbus2Scaler;

  @Value("${simple.version.info.mbus2.unit}")
  private UnitType simpeVersionInfoMbus2Unit;

  @Value("${simple.version.info.mbus3.hexvalue}")
  private String simpleVersionInfoMbus3HexValue;

  @Value("${simple.version.info.mbus3.scaler}")
  private int simpleVersionInfoMbus3Scaler;

  @Value("${simple.version.info.mbus3.unit}")
  private UnitType simpeVersionInfoMbus3Unit;

  @Value("${simple.version.info.mbus4.hexvalue}")
  private String simpleVersionInfoMbus4HexValue;

  @Value("${simple.version.info.mbus4.scaler}")
  private int simpleVersionInfoMbus4Scaler;

  @Value("${simple.version.info.mbus4.unit}")
  private UnitType simpeVersionInfoMbus4Unit;

  @Value("${dlms.attribute.values.service.base.address}")
  private String dlmsAttributeValuesServiceBaseAddress;

  @Value("#{'${configurationobject.flags}'.split(',')}")
  private List<Byte> configurationObjectFlags;

  @Value("${mbus.identification.number}")
  private long mbusIdentificationNumber;

  @Value("${command.hourlymeterreads.mbus.value}")
  private long mBusValue;

  @Value("${command.hourlymeterreads.mbus.scaler}")
  private byte mBusScaler;

  @Value("${command.hourlymeterreads.mbus.unit}")
  private UnitType mBusUnit;

  @Value("${command.hourlymeterreads.mbus.capturetime.year}")
  private int mBusCaptureTimeYear;

  @Value("${command.hourlymeterreads.mbus.capturetime.month}")
  private int mBusCaptureTimeMonth;

  @Value("${command.hourlymeterreads.mbus.capturetime.dayOfMonth}")
  private int mBusCaptureTimeDayOfMonth;

  @Value("${command.hourlymeterreads.mbus.capturetime.dayOfWeek}")
  private int mBusCaptureTimeDayOfWeek;

  @Value("${command.hourlymeterreads.mbus.capturetime.hour}")
  private int mBusCaptureTimeHour;

  @Value("${command.hourlymeterreads.mbus.capturetime.minute}")
  private int mBusCaptureTimeMinute;

  @Value("${command.hourlymeterreads.mbus.capturetime.second}")
  private int mBusCaptureTimeSecond;

  @Value("${command.hourlymeterreads.mbus.capturetime.hundredths}")
  private int mBusCaptureTimeHundredths;

  @Value("${command.hourlymeterreads.mbus.capturetime.deviation}")
  private int mBusCaptureTimeDeviation;

  @Value("${command.hourlymeterreads.mbus.capturetime.clockstatus}")
  private byte mBusCaptureTimeStatus;

  @Bean
  public InvocationCounter invocationCounter() {
    return new InvocationCounter(this.invocationCounter);
  }

  @Bean
  public MBusDriverActiveFirmwareIdentifier mBusDriverActiveFirmwareIdentifier() {
    return new MBusDriverActiveFirmwareIdentifier(
        this.mBusDriverActiveFirmwareIdentifier.getBytes());
  }

  @Bean
  public MBusDriverActiveFirmwareSignature mBusDriverActiveFirmwareSignature() {
    return new MBusDriverActiveFirmwareSignature(
        Hex.decode(this.mBusDriverActiveFirmwareSignature.getBytes()));
  }

  @Bean
  public PowerQualityEventLog powerQualityEventLog(final Calendar cal) {
    return new PowerQualityEventLog(cal);
  }

  @Bean
  public PowerQualityProfile1 powerQualityProfile1(final Calendar cal) {
    return new PowerQualityProfile1(cal);
  }

  @Bean
  public PowerQualityProfile2 powerQualityProfile2(final Calendar cal) {
    return new PowerQualityProfile2(cal);
  }

  @Bean
  public OctetStringExtendedRegister simpleVersionInfoIdentifier1() {
    final String obisCode = "0.1.24.2.11.255";

    return new OctetStringExtendedRegister(
        obisCode,
        Hex.decode(this.simpleVersionInfoMbus1HexValue),
        this.simpleVersionInfoMbus1Scaler,
        this.simpeVersionInfoMbus1Unit,
        this.getSimpleVersionInfoCaptureTime());
  }

  @Bean
  public OctetStringExtendedRegister simpleVersionInfoIdentifier2() {
    final String obisCode = "0.2.24.2.11.255";

    return new OctetStringExtendedRegister(
        obisCode,
        Hex.decode(this.simpleVersionInfoMbus2HexValue),
        this.simpleVersionInfoMbus2Scaler,
        this.simpeVersionInfoMbus2Unit,
        this.getSimpleVersionInfoCaptureTime());
  }

  @Bean
  public OctetStringExtendedRegister simpleVersionInfoIdentifier3() {
    final String obisCode = "0.3.24.2.11.255";

    return new OctetStringExtendedRegister(
        obisCode,
        Hex.decode(this.simpleVersionInfoMbus3HexValue),
        this.simpleVersionInfoMbus3Scaler,
        this.simpeVersionInfoMbus3Unit,
        this.getSimpleVersionInfoCaptureTime());
  }

  @Bean
  public OctetStringExtendedRegister simpleVersionInfoIdentifier4() {
    final String obisCode = "0.4.24.2.11.255";

    return new OctetStringExtendedRegister(
        obisCode,
        Hex.decode(this.simpleVersionInfoMbus4HexValue),
        this.simpleVersionInfoMbus4Scaler,
        this.simpeVersionInfoMbus4Unit,
        this.getSimpleVersionInfoCaptureTime());
  }

  private CosemDateTime getSimpleVersionInfoCaptureTime() {
    return new CosemDateTime(
        2021,
        3,
        15,
        2,
        23,
        0,
        0,
        0,
        -60,
        CosemDateTime.ClockStatus.clockStatusFrom((byte) 0)
            .toArray(new CosemDateTime.ClockStatus[0]));
  }

  @Bean
  public DynamicValues dynamicValues() {
    return new DynamicValues(this.dlmsAttributeValuesClient());
  }

  @Bean
  public DlmsAttributeValuesClient dlmsAttributeValuesClient() {
    return new DlmsAttributeValuesClient(this.dlmsAttributeValuesServiceBaseAddress);
  }

  @Bean
  public ConfigurationObject configurationObject() {
    final Byte[] bytes = new Byte[this.configurationObjectFlags.size()];
    this.configurationObjectFlags.toArray(bytes);

    this.dynamicValues()
        .setDefaultAttributeValue(
            InterfaceClass.DATA.id(),
            new ObisCode(0, 1, 94, 31, 3, 255),
            ConfigurationObject.ATTRIBUTE_ID_VALUE,
            DataObject.newBitStringData(new BitString(ArrayUtils.toPrimitive(bytes), 16)));

    return new ConfigurationObject();
  }

  @Bean
  public Long mbusIdentificationNumberHolder() {
    return this.mbusIdentificationNumber;
  }

  @Bean
  public DoubleLongUnsignedExtendedRegister mBusVolumeHourlyChannel1() {
    return this.defaultVolumeHourly("0.1.24.2.2.255");
  }

  @Bean
  public DoubleLongUnsignedExtendedRegister mBusVolumeHourlyChannel2() {
    return this.defaultVolumeHourly("0.2.24.2.2.255");
  }

  @Bean
  public DoubleLongUnsignedExtendedRegister mBusVolumeHourlyChannel3() {
    return this.defaultVolumeHourly("0.3.24.2.2.255");
  }

  @Bean
  public DoubleLongUnsignedExtendedRegister mBusVolumeHourlyChannel4() {
    return this.defaultVolumeHourly("0.4.24.2.2.255");
  }

  private DoubleLongUnsignedExtendedRegister defaultVolumeHourly(final String logicalName) {
    return new DoubleLongUnsignedExtendedRegister(
        logicalName,
        this.mBusValue,
        this.mBusScaler,
        this.mBusUnit,
        new CosemDateTime(
            this.mBusCaptureTimeYear,
            this.mBusCaptureTimeMonth,
            this.mBusCaptureTimeDayOfMonth,
            this.mBusCaptureTimeDayOfWeek,
            this.mBusCaptureTimeHour,
            this.mBusCaptureTimeMinute,
            this.mBusCaptureTimeSecond,
            this.mBusCaptureTimeHundredths,
            this.mBusCaptureTimeDeviation,
            ClockStatus.clockStatusFrom(this.mBusCaptureTimeStatus).toArray(new ClockStatus[0])));
  }

  @Bean
  public MBusMasterLoadProfilePeriod1SMR5 mBus1MasterLoadProfilePeriod1(final Calendar cal) {
    return new MBusMasterLoadProfilePeriod1SMR5(cal, 1);
  }

  @Bean
  public MBusMasterLoadProfilePeriod1SMR5 mBus2MasterLoadProfilePeriod1(final Calendar cal) {
    return new MBusMasterLoadProfilePeriod1SMR5(cal, 2);
  }

  @Bean
  public MBusMasterLoadProfilePeriod1SMR5 mBus3MasterLoadProfilePeriod1(final Calendar cal) {
    return new MBusMasterLoadProfilePeriod1SMR5(cal, 3);
  }

  @Bean
  public MBusMasterLoadProfilePeriod1SMR5 mBus4MasterLoadProfilePeriod1(final Calendar cal) {
    return new MBusMasterLoadProfilePeriod1SMR5(cal, 4);
  }

  @Bean
  public MBusDailyBillingValuesPeriod1SMR5 mBus1DailyBillingValuesPeriod1(final Calendar cal) {
    return new MBusDailyBillingValuesPeriod1SMR5(cal, 1);
  }

  @Bean
  public MBusDailyBillingValuesPeriod1SMR5 mBus2DailyBillingValuesPeriod1(final Calendar cal) {
    return new MBusDailyBillingValuesPeriod1SMR5(cal, 2);
  }

  @Bean
  public MBusDailyBillingValuesPeriod1SMR5 mBus3DailyBillingValuesPeriod1(final Calendar cal) {
    return new MBusDailyBillingValuesPeriod1SMR5(cal, 3);
  }

  @Bean
  public MBusDailyBillingValuesPeriod1SMR5 mBus4DailyBillingValuesPeriod1(final Calendar cal) {
    return new MBusDailyBillingValuesPeriod1SMR5(cal, 4);
  }

  @Bean
  public MBusMonthlyBillingValuesPeriod1SMR5 mBus1MonthlyBillingValuesPeriod1(final Calendar cal) {
    return new MBusMonthlyBillingValuesPeriod1SMR5(cal, 1);
  }

  @Bean
  public MBusMonthlyBillingValuesPeriod1SMR5 mBus2MonthlyBillingValuesPeriod1(final Calendar cal) {
    return new MBusMonthlyBillingValuesPeriod1SMR5(cal, 2);
  }

  @Bean
  public MBusMonthlyBillingValuesPeriod1SMR5 mBus3MonthlyBillingValuesPeriod1(final Calendar cal) {
    return new MBusMonthlyBillingValuesPeriod1SMR5(cal, 3);
  }

  @Bean
  public MBusMonthlyBillingValuesPeriod1SMR5 mBus4MonthlyBillingValuesPeriod1(final Calendar cal) {
    return new MBusMonthlyBillingValuesPeriod1SMR5(cal, 4);
  }

  @Bean
  public EMonthlyBillingValuesPeriod1SMR5 eMonthlyBillingValuesPeriod1SMR5(final Calendar cal) {
    return new EMonthlyBillingValuesPeriod1SMR5(cal);
  }

  @Bean
  SingleActionScheduler phaseOutageTestScheduler() {
    return new SingleActionScheduler("0.0.15.1.4.255");
  }
}
