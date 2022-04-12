/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.server.profile;

import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.AlarmObject.ALARM_OBJECT_1;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile1.AVERAGE_ACTIVE_POWER_EXPORT_L1_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile1.AVERAGE_ACTIVE_POWER_EXPORT_L2_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile1.AVERAGE_ACTIVE_POWER_EXPORT_L3_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile1.AVERAGE_ACTIVE_POWER_IMPORT_L1_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile1.AVERAGE_ACTIVE_POWER_IMPORT_L2_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile1.AVERAGE_ACTIVE_POWER_IMPORT_L3_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile1.AVERAGE_REACTIVE_POWER_EXPORT_L1_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile1.AVERAGE_REACTIVE_POWER_EXPORT_L2_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile1.AVERAGE_REACTIVE_POWER_EXPORT_L3_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile1.AVERAGE_REACTIVE_POWER_IMPORT_L1_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile1.AVERAGE_REACTIVE_POWER_IMPORT_L2_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile1.AVERAGE_REACTIVE_POWER_IMPORT_L3_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2.AVERAGE_CURRENT_L1_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2.AVERAGE_CURRENT_L2_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2.AVERAGE_CURRENT_L3_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2.AVERAGE_VOLTAGE_L1_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2.AVERAGE_VOLTAGE_L2_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2.AVERAGE_VOLTAGE_L3_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2.INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2.INSTANTANEOUS_ACTIVE_POWER_EXPORT_L2_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2.INSTANTANEOUS_ACTIVE_POWER_EXPORT_L3_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2.INSTANTANEOUS_ACTIVE_POWER_EXPORT_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2.INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2.INSTANTANEOUS_ACTIVE_POWER_IMPORT_L2_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2.INSTANTANEOUS_ACTIVE_POWER_IMPORT_L3_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2.INSTANTANEOUS_ACTIVE_POWER_IMPORT_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2.INSTANTANEOUS_CURRENT_L1_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2.INSTANTANEOUS_CURRENT_L2_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2.INSTANTANEOUS_CURRENT_L3_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2.INSTANTANEOUS_VOLTAGE_L1_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2.INSTANTANEOUS_VOLTAGE_L2_LOGICAL_NAME;
import static org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerQualityProfile2.INSTANTANEOUS_VOLTAGE_L3_LOGICAL_NAME;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.util.encoders.Hex;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.BitString;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.CosemDateTime.ClockStatus;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.DataAttribute;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ProfileGenericAttribute;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ActiveFirmwareIdentifier;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ActiveFirmwareSignature;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ActivityCalendar;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.AdministrativeInOut;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.AdministrativeStatusType;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.AlarmFilter;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.AlarmObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.AmrProfileStatusCodeEMeter;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.Clock;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.CommunicationModuleActiveFirmwareIdentifier;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.CommunicationModuleFirmwareSignature;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.CommunicationSessionLog;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ConfigurationObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.CurrentlyActiveTariff;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.DataOfBillingPeriod1;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.DefinableLoadProfile;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.DoubleLongUnsignedExtendedRegister;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.DoubleLongUnsignedRegister;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ErrorObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.FraudDetectionLog;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ImageTransfer;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.LoadProfileWithPeriod1;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.LoadProfileWithPeriod2;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.LongUnsignedData;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.LongUnsignedRegister;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.MBus1MasterLoadProfilePeriod1;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.MBus2MasterLoadProfilePeriod1;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.MBus3MasterLoadProfilePeriod1;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.MBus4MasterLoadProfilePeriod1;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.MBusClientSetup;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.MBusEventLog;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ModuleActiveFirmwareIdentifier;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.ModuleFirmwareSignature;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.OctetStringData;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.P1PortDsmrVersion;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.PowerOutages;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.PushSetupAlarm;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.PushSetupSms;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.SetRandomisationSettings;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.SpecialDaysTable;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.StandardEventLog;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.UnitType;
import org.opensmartgridplatform.simulator.protocol.dlms.interceptor.OsgpServerConnectionListener;
import org.opensmartgridplatform.simulator.protocol.dlms.rest.client.DlmsAttributeValuesClient;
import org.opensmartgridplatform.simulator.protocol.dlms.util.DynamicValues;
import org.opensmartgridplatform.simulator.protocol.dlms.util.KeyPathProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

@Configuration
@Profile("default")
public class DefaultDeviceProfile {

  private static final String NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L1_LOGICAL_NAME = "1.0.32.32.0.255";
  private static final String NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L1_LOGICAL_NAME = "1.0.32.36.0.255";
  private static final String NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L2_LOGICAL_NAME = "1.0.52.32.0.255";
  private static final String NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L2_LOGICAL_NAME = "1.0.52.36.0.255";
  private static final String NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L3_LOGICAL_NAME = "1.0.72.32.0.255";
  private static final String NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L3_LOGICAL_NAME = "1.0.72.36.0.255";

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDeviceProfile.class);

  @Value("${command.periodicmeterreads.amrProfileStatusCodeEMeter}")
  private short amrProfileStatusCodeEMeter;

  @Value("${command.actualmeterreads.importValue}")
  private long importValue;

  @Value("${command.actualmeterreads.importRate1Value}")
  private long importRate1Value;

  @Value("${command.actualmeterreads.importRate2Value}")
  private long importRate2Value;

  @Value("${command.actualmeterreads.exportValue}")
  private long exportValue;

  @Value("${command.actualmeterreads.exportRate1Value}")
  private long exportRate1Value;

  @Value("${command.actualmeterreads.exportRate2Value}")
  private long exportRate2Value;

  @Value("${command.actualmeterreads.scaler}")
  private int scaler;

  @Value("${command.actualmeterreads.unit}")
  private UnitType unit;

  @Value("${command.actualmeterreads.clock.year}")
  private int clockYear;

  @Value("${command.actualmeterreads.clock.month}")
  private int clockMonth;

  @Value("${command.actualmeterreads.clock.dayOfMonth}")
  private int clockDayOfMonth;

  @Value("${command.actualmeterreads.clock.dayOfWeek}")
  private int clockDayOfWeek;

  @Value("${command.actualmeterreads.clock.hour}")
  private int clockHour;

  @Value("${command.actualmeterreads.clock.minute}")
  private int clockMinute;

  @Value("${command.actualmeterreads.clock.second}")
  private int clockSecond;

  @Value("${command.actualmeterreads.clock.hundredths}")
  private int clockHundredths;

  @Value("${command.actualmeterreads.clock.deviation}")
  private int clockDeviation;

  @Value("${command.actualmeterreads.clock.clockstatus}")
  private String clockStatus;

  @Value("${command.actualmeterreads.mbus1.value}")
  private long mBus1Value;

  @Value("${command.actualmeterreads.mbus1.scaler}")
  private byte mBus1Scaler;

  @Value("${command.actualmeterreads.mbus1.unit}")
  private UnitType mBus1Unit;

  @Value("${command.actualmeterreads.mbus1.capturetime.year}")
  private int mBus1CaptureTimeYear;

  @Value("${command.actualmeterreads.mbus1.capturetime.month}")
  private int mBus1CaptureTimeMonth;

  @Value("${command.actualmeterreads.mbus1.capturetime.dayOfMonth}")
  private int mBus1CaptureTimeDayOfMonth;

  @Value("${command.actualmeterreads.mbus1.capturetime.dayOfWeek}")
  private int mBus1CaptureTimeDayOfWeek;

  @Value("${command.actualmeterreads.mbus1.capturetime.hour}")
  private int mBus1CaptureTimeHour;

  @Value("${command.actualmeterreads.mbus1.capturetime.minute}")
  private int mBus1CaptureTimeMinute;

  @Value("${command.actualmeterreads.mbus1.capturetime.second}")
  private int mBus1CaptureTimeSecond;

  @Value("${command.actualmeterreads.mbus1.capturetime.hundredths}")
  private int mBus1CaptureTimeHundredths;

  @Value("${command.actualmeterreads.mbus1.capturetime.deviation}")
  private int mBus1CaptureTimeDeviation;

  @Value("${command.actualmeterreads.mbus1.capturetime.clockstatus}")
  private byte mBus1CaptureTimeStatus;

  @Value("${command.actualmeterreads.mbus2.value}")
  private long mBus2Value;

  @Value("${command.actualmeterreads.mbus2.scaler}")
  private byte mBus2Scaler;

  @Value("${command.actualmeterreads.mbus2.unit}")
  private UnitType mBus2Unit;

  @Value("${command.actualmeterreads.mbus2.capturetime.year}")
  private int mBus2CaptureTimeYear;

  @Value("${command.actualmeterreads.mbus2.capturetime.month}")
  private int mBus2CaptureTimeMonth;

  @Value("${command.actualmeterreads.mbus2.capturetime.dayOfMonth}")
  private int mBus2CaptureTimeDayOfMonth;

  @Value("${command.actualmeterreads.mbus2.capturetime.dayOfWeek}")
  private int mBus2CaptureTimeDayOfWeek;

  @Value("${command.actualmeterreads.mbus2.capturetime.hour}")
  private int mBus2CaptureTimeHour;

  @Value("${command.actualmeterreads.mbus2.capturetime.minute}")
  private int mBus2CaptureTimeMinute;

  @Value("${command.actualmeterreads.mbus2.capturetime.second}")
  private int mBus2CaptureTimeSecond;

  @Value("${command.actualmeterreads.mbus2.capturetime.hundredths}")
  private int mBus2CaptureTimeHundredths;

  @Value("${command.actualmeterreads.mbus2.capturetime.deviation}")
  private int mBus2CaptureTimeDeviation;

  @Value("${command.actualmeterreads.mbus2.capturetime.clockstatus}")
  private byte mBus2CaptureTimeStatus;

  @Value("${command.actualmeterreads.mbus3.value}")
  private long mBus3Value;

  @Value("${command.actualmeterreads.mbus3.scaler}")
  private byte mBus3Scaler;

  @Value("${command.actualmeterreads.mbus3.unit}")
  private UnitType mBus3Unit;

  @Value("${command.actualmeterreads.mbus3.capturetime.year}")
  private int mBus3CaptureTimeYear;

  @Value("${command.actualmeterreads.mbus3.capturetime.month}")
  private int mBus3CaptureTimeMonth;

  @Value("${command.actualmeterreads.mbus3.capturetime.dayOfMonth}")
  private int mBus3CaptureTimeDayOfMonth;

  @Value("${command.actualmeterreads.mbus3.capturetime.dayOfWeek}")
  private int mBus3CaptureTimeDayOfWeek;

  @Value("${command.actualmeterreads.mbus3.capturetime.hour}")
  private int mBus3CaptureTimeHour;

  @Value("${command.actualmeterreads.mbus3.capturetime.minute}")
  private int mBus3CaptureTimeMinute;

  @Value("${command.actualmeterreads.mbus3.capturetime.second}")
  private int mBus3CaptureTimeSecond;

  @Value("${command.actualmeterreads.mbus3.capturetime.hundredths}")
  private int mBus3CaptureTimeHundredths;

  @Value("${command.actualmeterreads.mbus3.capturetime.deviation}")
  private int mBus3CaptureTimeDeviation;

  @Value("${command.actualmeterreads.mbus3.capturetime.clockstatus}")
  private byte mBus3CaptureTimeStatus;

  @Value("${command.actualmeterreads.mbus4.value}")
  private long mBus4Value;

  @Value("${command.actualmeterreads.mbus4.scaler}")
  private byte mBus4Scaler;

  @Value("${command.actualmeterreads.mbus4.unit}")
  private UnitType mBus4Unit;

  @Value("${command.actualmeterreads.mbus4.capturetime.year}")
  private int mBus4CaptureTimeYear;

  @Value("${command.actualmeterreads.mbus4.capturetime.month}")
  private int mBus4CaptureTimeMonth;

  @Value("${command.actualmeterreads.mbus4.capturetime.dayOfMonth}")
  private int mBus4CaptureTimeDayOfMonth;

  @Value("${command.actualmeterreads.mbus4.capturetime.dayOfWeek}")
  private int mBus4CaptureTimeDayOfWeek;

  @Value("${command.actualmeterreads.mbus4.capturetime.hour}")
  private int mBus4CaptureTimeHour;

  @Value("${command.actualmeterreads.mbus4.capturetime.minute}")
  private int mBus4CaptureTimeMinute;

  @Value("${command.actualmeterreads.mbus4.capturetime.second}")
  private int mBus4CaptureTimeSecond;

  @Value("${command.actualmeterreads.mbus4.capturetime.hundredths}")
  private int mBus4CaptureTimeHundredths;

  @Value("${command.actualmeterreads.mbus4.capturetime.deviation}")
  private int mBus4CaptureTimeDeviation;

  @Value("${command.actualmeterreads.mbus4.capturetime.clockstatus}")
  private byte mBus4CaptureTimeStatus;

  @Value("${deviceidentification.kemacode}")
  private String kemaCode;

  @Value("${deviceidentification.productionyear}")
  private int productionYear;

  @Value("${deviceidentification.serialnumber}")
  private int serialNumber;

  @Value("${alarmobject.register1.value}")
  private int alarmValue;

  @Value("${errorobject.value}")
  private int errorValue;

  @Value("${alarmfilter.value}")
  private int alarmFilterValue;

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

  @Value("${firmware.active.identifier}")
  private String activeFirmwareIdentifier;

  @Value("${firmware.active.signature}")
  private String activeFirmwareSignature;

  @Value("${firmware.module.active.identifier}")
  private String moduleActiveFirmwareIdentifier;

  @Value("${firmware.module.signature}")
  private String moduleFirmwareSignature;

  @Value("${firmware.communicationmodule.active.identifier}")
  private String communicationModuleActiveFirmwareIdentifier;

  @Value("${firmware.communicationmodule.signature}")
  private String communicationModuleFirmwareSignature;

  @Value("${firmware.p1port.dmsrversion}")
  private String p1PortDsmrVersion;

  @Value("${simulator.keys.authentication.path:null}")
  private String authenticationKey;

  @Value("${simulator.keys.encryption.path:null}")
  private String encryptionKey;

  @Value("${connection.open.delay.min:0}")
  private int connectionSetupDelayMin;

  @Value("${connection.open.delay.max:0}")
  private int connectionSetupDelayMax;

  @Value("${simulator.keys.master.path:null}")
  private String masterKey;

  @Value("${configurationobject.gprsoperationmode}")
  private int gprsOperationMode;

  @Value("#{'${configurationobject.flags}'.split(',')}")
  private List<Byte> configurationObjectFlags;

  @Value("${firmware.imagetransfer.blocksize}")
  private int imageTransferBlockSize;

  @Value("${firmware.imagetransfer.failureChance}")
  private double imageTransferFailureChance;

  @Value("${firmware.imagetransfer.activationStatusChangeDelay}")
  private int activationStatusChangeDelay;

  @Value("${mbus.primary.address}")
  private short mbusPrimaryAdress;

  @Value("${mbus.identification.number}")
  private long mbusIdentificationNumber;

  @Value("${mbus.manufacturer.id}")
  private int mbusManufacturerId;

  @Value("${mbus.version}")
  private int mbusVersion;

  @Value("${mbus.devicetype}")
  private int mbusDeviceType;

  @Value("${mbus.encryption.key.status}")
  private int mbusEncryptionKeyStatus;

  @Value("${dlms.attribute.values.service.base.address}")
  private String dlmsAttributeValuesServiceBaseAddress;

  @Bean
  public OsgpServerConnectionListener osgpServerConnectionListener() {

    LOGGER.debug("connectionSetupDelay min is {}", this.connectionSetupDelayMin);
    LOGGER.debug("connectionSetupDelay max is {}", this.connectionSetupDelayMax);

    return new OsgpServerConnectionListener(
        this.connectionSetupDelayMin, this.connectionSetupDelayMax);
  }

  @Bean
  public String authenticationKeyPath() {
    return this.authenticationKey;
  }

  @Bean
  public String encryptionKeyPath() {
    return this.encryptionKey;
  }

  @Bean
  public String masterKeyPath() {
    return this.masterKey;
  }

  @Bean
  public KeyPathProvider keyPathProvider() {
    return new KeyPathProvider(
        this.authenticationKeyPath(), this.encryptionKeyPath(), this.masterKeyPath());
  }

  @Bean
  @Scope("prototype")
  public Map<String, CosemInterfaceObject> cosemClasses(
      final org.springframework.context.ApplicationContext applicationContext) {
    return applicationContext.getBeansOfType(CosemInterfaceObject.class);
  }

  @Bean
  @Scope("prototype")
  public Clock clock() {
    return new Clock(LocalDateTime.now());
  }

  @Bean
  public DoubleLongUnsignedRegister importValue() {
    return new DoubleLongUnsignedRegister(
        "1.0.1.8.0.255", this.importValue, this.scaler, this.unit);
  }

  @Bean
  public DoubleLongUnsignedRegister importRate1Value() {
    return new DoubleLongUnsignedRegister(
        "1.0.1.8.1.255", this.importRate1Value, this.scaler, this.unit);
  }

  @Bean
  public DoubleLongUnsignedRegister importRate2Value() {
    return new DoubleLongUnsignedRegister(
        "1.0.1.8.2.255", this.importRate2Value, this.scaler, this.unit);
  }

  @Bean
  public DoubleLongUnsignedRegister exportValue() {
    return new DoubleLongUnsignedRegister(
        "1.0.2.8.0.255", this.exportValue, this.scaler, this.unit);
  }

  @Bean
  public DoubleLongUnsignedRegister exportRate1Value() {
    return new DoubleLongUnsignedRegister(
        "1.0.2.8.1.255", this.exportRate1Value, this.scaler, this.unit);
  }

  @Bean
  public DoubleLongUnsignedRegister exportRate2Value() {
    return new DoubleLongUnsignedRegister(
        "1.0.2.8.2.255", this.exportRate2Value, this.scaler, this.unit);
  }

  @Bean
  public DoubleLongUnsignedExtendedRegister mBusMasterValue1() {
    return new DoubleLongUnsignedExtendedRegister(
        "0.1.24.2.1.255",
        this.mBus1Value,
        this.mBus1Scaler,
        this.mBus1Unit,
        new CosemDateTime(
            this.mBus1CaptureTimeYear,
            this.mBus1CaptureTimeMonth,
            this.mBus1CaptureTimeDayOfMonth,
            this.mBus1CaptureTimeDayOfWeek,
            this.mBus1CaptureTimeHour,
            this.mBus1CaptureTimeMinute,
            this.mBus1CaptureTimeSecond,
            this.mBus1CaptureTimeHundredths,
            this.mBus1CaptureTimeDeviation,
            ClockStatus.clockStatusFrom(this.mBus1CaptureTimeStatus).toArray(new ClockStatus[0])));
  }

  @Bean
  public DoubleLongUnsignedExtendedRegister mBusMasterValue2() {
    return new DoubleLongUnsignedExtendedRegister(
        "0.2.24.2.1.255",
        this.mBus2Value,
        this.mBus2Scaler,
        this.mBus2Unit,
        new CosemDateTime(
            this.mBus2CaptureTimeYear,
            this.mBus2CaptureTimeMonth,
            this.mBus2CaptureTimeDayOfMonth,
            this.mBus2CaptureTimeDayOfWeek,
            this.mBus2CaptureTimeHour,
            this.mBus2CaptureTimeMinute,
            this.mBus2CaptureTimeSecond,
            this.mBus2CaptureTimeHundredths,
            this.mBus2CaptureTimeDeviation,
            ClockStatus.clockStatusFrom(this.mBus2CaptureTimeStatus).toArray(new ClockStatus[0])));
  }

  @Bean
  public DoubleLongUnsignedExtendedRegister mBusMasterValue3() {
    return new DoubleLongUnsignedExtendedRegister(
        "0.3.24.2.1.255",
        this.mBus3Value,
        this.mBus3Scaler,
        this.mBus3Unit,
        new CosemDateTime(
            this.mBus3CaptureTimeYear,
            this.mBus3CaptureTimeMonth,
            this.mBus3CaptureTimeDayOfMonth,
            this.mBus3CaptureTimeDayOfWeek,
            this.mBus3CaptureTimeHour,
            this.mBus3CaptureTimeMinute,
            this.mBus3CaptureTimeSecond,
            this.mBus3CaptureTimeHundredths,
            this.mBus3CaptureTimeDeviation,
            ClockStatus.clockStatusFrom(this.mBus3CaptureTimeStatus).toArray(new ClockStatus[0])));
  }

  @Bean
  public DoubleLongUnsignedExtendedRegister mBusMasterValue4() {
    return new DoubleLongUnsignedExtendedRegister(
        "0.4.24.2.1.255",
        this.mBus4Value,
        this.mBus4Scaler,
        this.mBus4Unit,
        new CosemDateTime(
            this.mBus4CaptureTimeYear,
            this.mBus4CaptureTimeMonth,
            this.mBus4CaptureTimeDayOfMonth,
            this.mBus4CaptureTimeDayOfWeek,
            this.mBus4CaptureTimeHour,
            this.mBus4CaptureTimeMinute,
            this.mBus4CaptureTimeSecond,
            this.mBus4CaptureTimeHundredths,
            this.mBus4CaptureTimeDeviation,
            ClockStatus.clockStatusFrom(this.mBus4CaptureTimeStatus).toArray(new ClockStatus[0])));
  }

  @Bean
  public OctetStringData deviceId1() {
    final String obisCode = "0.0.96.1.0.255";
    this.dynamicValues()
        .setDefaultAttributeValue(
            InterfaceClass.DATA.id(),
            new ObisCode(obisCode),
            OctetStringData.ATTRIBUTE_ID_VALUE,
            DataObject.newOctetStringData(
                String.valueOf(this.serialNumber).getBytes(StandardCharsets.US_ASCII)));
    return new OctetStringData(obisCode);
  }

  @Bean
  public OctetStringData deviceId2() {
    final String obisCode = "0.0.96.1.1.255";
    final String deviceId2 =
        String.format(
            "%s%010d%2d",
            String.format("%5s", this.kemaCode).substring(0, 5),
            this.serialNumber,
            this.productionYear);
    LOGGER.info("deviceId2 {}", deviceId2);
    this.dynamicValues()
        .setDefaultAttributeValue(
            InterfaceClass.DATA.id(),
            new ObisCode(obisCode),
            OctetStringData.ATTRIBUTE_ID_VALUE,
            DataObject.newOctetStringData(deviceId2.getBytes(StandardCharsets.US_ASCII)));
    return new OctetStringData(obisCode);
  }

  @Bean
  public OctetStringData deviceId4() {
    final String obisCode = "0.0.96.1.3.255";
    this.dynamicValues()
        .setDefaultAttributeValue(
            InterfaceClass.DATA.id(),
            new ObisCode(obisCode),
            OctetStringData.ATTRIBUTE_ID_VALUE,
            DataObject.newOctetStringData(new byte[0]));
    return new OctetStringData(obisCode);
  }

  @Bean
  public OctetStringData deviceId5() {
    final String obisCode = "0.0.96.1.4.255";
    this.dynamicValues()
        .setDefaultAttributeValue(
            InterfaceClass.DATA.id(),
            new ObisCode(obisCode),
            OctetStringData.ATTRIBUTE_ID_VALUE,
            DataObject.newOctetStringData(new byte[0]));
    return new OctetStringData(obisCode);
  }

  @Bean
  public OctetStringData deviceId8() {
    final String obisCode = "0.0.96.1.7.255";
    this.dynamicValues()
        .setDefaultAttributeValue(
            InterfaceClass.DATA.id(),
            new ObisCode(obisCode),
            OctetStringData.ATTRIBUTE_ID_VALUE,
            DataObject.newOctetStringData(new byte[0]));
    return new OctetStringData(obisCode);
  }

  @Bean
  public OctetStringData deviceId9() {
    final String obisCode = "0.0.96.1.8.255";
    this.dynamicValues()
        .setDefaultAttributeValue(
            InterfaceClass.DATA.id(),
            new ObisCode(obisCode),
            OctetStringData.ATTRIBUTE_ID_VALUE,
            DataObject.newOctetStringData(new byte[0]));
    return new OctetStringData(obisCode);
  }

  @Scope("prototype")
  @Bean
  public AlarmObject alarmObject() {
    this.dynamicValues()
        .setDefaultAttributeValue(
            InterfaceClass.DATA.id(),
            new ObisCode(0, 0, 97, 98, 0, 255),
            DataAttribute.VALUE.attributeId(),
            DataObject.newUInteger32Data(this.alarmValue));
    return new AlarmObject(ALARM_OBJECT_1);
  }

  @Scope("prototype")
  @Bean
  public ErrorObject errorObject() {
    return new ErrorObject(this.errorValue);
  }

  @Scope("prototype")
  @Bean
  public PushSetupAlarm pushSetupAlarm() {
    return new PushSetupAlarm();
  }

  @Scope("prototype")
  @Bean
  public PushSetupSms pushSetupSms() {
    return new PushSetupSms();
  }

  @Scope("prototype")
  @Bean
  public AlarmFilter alarmFilter() {
    return new AlarmFilter("0.0.97.98.10.255", this.alarmFilterValue);
  }

  @Bean
  @Scope("prototype")
  public SpecialDaysTable specialDaysTable() {
    return new SpecialDaysTable();
  }

  @Bean
  @Scope("prototype")
  public ActivityCalendar activityCalendar() {
    return new ActivityCalendar();
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
  public DataOfBillingPeriod1 dataOfBillingPeriod1(final Calendar cal) {
    return new DataOfBillingPeriod1(cal);
  }

  @Bean
  public FraudDetectionLog fraudDetectionLog(final Calendar cal) {
    return new FraudDetectionLog(cal);
  }

  @Bean
  public CommunicationSessionLog communicationSessionLog(final Calendar cal) {
    return new CommunicationSessionLog(cal);
  }

  @Bean
  public StandardEventLog standardEventLog(final Calendar cal) {
    return new StandardEventLog(cal);
  }

  @Bean
  public PowerOutages powerFailureEventLog(final Calendar cal) {
    return new PowerOutages(cal);
  }

  @Bean
  public ActiveFirmwareIdentifier activeFirmwareIdentifier() {
    return new ActiveFirmwareIdentifier(this.activeFirmwareIdentifier.getBytes());
  }

  @Bean
  public ActiveFirmwareSignature activeFirmwareSignature() {
    return new ActiveFirmwareSignature(Hex.decode(this.activeFirmwareSignature.getBytes()));
  }

  @Bean
  public ModuleActiveFirmwareIdentifier moduleActiveFirmwareIdentifier() {
    return new ModuleActiveFirmwareIdentifier(this.moduleActiveFirmwareIdentifier.getBytes());
  }

  @Bean
  public ModuleFirmwareSignature moduleFirmwareSignature() {
    return new ModuleFirmwareSignature(Hex.decode(this.moduleFirmwareSignature.getBytes()));
  }

  @Bean
  public CommunicationModuleActiveFirmwareIdentifier communicationModuleActiveFirmwareIdentifier() {
    return new CommunicationModuleActiveFirmwareIdentifier(
        this.communicationModuleActiveFirmwareIdentifier.getBytes());
  }

  @Bean
  public CommunicationModuleFirmwareSignature communicationModuleFirmwareSignature() {
    return new CommunicationModuleFirmwareSignature(
        Hex.decode(this.communicationModuleFirmwareSignature.getBytes()));
  }

  @Bean
  public P1PortDsmrVersion p1PortDsmrVersion() {
    return new P1PortDsmrVersion(this.p1PortDsmrVersion.getBytes());
  }

  @Bean
  @Scope("prototype")
  public AdministrativeInOut administrativeInOut() {
    return new AdministrativeInOut(AdministrativeStatusType.ON);
  }

  @Bean
  public LoadProfileWithPeriod1 loadProfile1(final Calendar cal) {
    return new LoadProfileWithPeriod1(cal);
  }

  @Bean
  public LoadProfileWithPeriod2 loadProfile2(final Calendar cal) {
    return new LoadProfileWithPeriod2(cal);
  }

  @Bean
  public DefinableLoadProfile definableLoadProfile(final Calendar cal) {
    final Integer classId = InterfaceClass.PROFILE_GENERIC.id();
    final ObisCode obisCode = new ObisCode(0, 1, 94, 31, 6, 255);
    this.dynamicValues()
        .setDefaultAttributeValue(
            classId,
            obisCode,
            ProfileGenericAttribute.CAPTURE_PERIOD.attributeId(),
            DataObject.newUInteger32Data(300));
    this.dynamicValues()
        .setDefaultAttributeValue(
            classId,
            obisCode,
            ProfileGenericAttribute.PROFILE_ENTRIES.attributeId(),
            DataObject.newUInteger32Data(960));

    return new DefinableLoadProfile(cal);
  }

  @Bean
  public AmrProfileStatusCodeEMeter amrProfileStatusCodeEMeter() {
    return new AmrProfileStatusCodeEMeter(this.amrProfileStatusCodeEMeter);
  }

  @Bean
  public CurrentlyActiveTariff currentlyActiveTariff() {
    return new CurrentlyActiveTariff();
  }

  @Bean
  public DataObject configurationObjectDataObjectHolder() {
    final Byte[] bytes = new Byte[this.configurationObjectFlags.size()];
    this.configurationObjectFlags.toArray(bytes);

    return DataObject.newStructureData(
        DataObject.newEnumerateData(this.gprsOperationMode),
        DataObject.newBitStringData(new BitString(ArrayUtils.toPrimitive(bytes), 16)));
  }

  @Bean
  public ConfigurationObject configurationObject(
      final DataObject configurationObjectDataObjectHolder) {
    this.dynamicValues()
        .setDefaultAttributeValue(
            InterfaceClass.DATA.id(),
            new ObisCode(0, 1, 94, 31, 3, 255),
            ConfigurationObject.ATTRIBUTE_ID_VALUE,
            configurationObjectDataObjectHolder);

    return new ConfigurationObject();
  }

  @Bean
  public MBus1MasterLoadProfilePeriod1 mBus1MasterLoadProfilePeriod1(final Calendar cal) {
    return new MBus1MasterLoadProfilePeriod1(cal);
  }

  @Bean
  public MBus2MasterLoadProfilePeriod1 mBus2MasterLoadProfilePeriod1(final Calendar cal) {
    return new MBus2MasterLoadProfilePeriod1(cal);
  }

  @Bean
  public MBus3MasterLoadProfilePeriod1 mBus3MasterLoadProfilePeriod1(final Calendar cal) {
    return new MBus3MasterLoadProfilePeriod1(cal);
  }

  @Bean
  public MBus4MasterLoadProfilePeriod1 mBus4MasterLoadProfilePeriod1(final Calendar cal) {
    return new MBus4MasterLoadProfilePeriod1(cal);
  }

  @Bean
  public MBusEventLog mBusEventLog(final Calendar cal) {
    return new MBusEventLog(cal);
  }

  @Bean
  public ImageTransfer imageTransfer() {
    return new ImageTransfer(
        this.imageTransferBlockSize,
        this.activationStatusChangeDelay,
        this.imageTransferFailureChance);
  }

  @Bean
  public Long mbusIdentificationNumberHolder() {
    return this.mbusIdentificationNumber;
  }

  /*
   * The mbus clients are setup (in the default profile) in such a way, that
   * on channel 1 a match can be found.
   */
  @Bean
  public MBusClientSetup mbusClientSetup1(final Long mbusIdentificationNumberHolder) {
    this.setMbusClientSetupDefaults(
        1,
        this.mbusPrimaryAdress,
        mbusIdentificationNumberHolder,
        this.mbusManufacturerId,
        this.mbusVersion,
        this.mbusDeviceType,
        this.mbusEncryptionKeyStatus);

    return new MBusClientSetup("0.1.24.1.0.255");
  }

  @Bean
  public MBusClientSetup mbusClientSetup2() {
    this.setMbusClientSetupDefaults(2, 0, 0, 0, 0, 0, 0);
    return new MBusClientSetup("0.2.24.1.0.255");
  }

  @Bean
  public MBusClientSetup mbusClientSetup3() {
    this.setMbusClientSetupDefaults(3, 0, 0, 0, 0, 0, 0);
    return new MBusClientSetup("0.3.24.1.0.255");
  }

  @Bean
  public MBusClientSetup mbusClientSetup4() {
    this.setMbusClientSetupDefaults(4, 0, 0, 0, 0, 0, 0);
    return new MBusClientSetup("0.4.24.1.0.255");
  }

  private void setMbusClientSetupDefaults(
      final int channel,
      final int primaryAddress,
      final long identificationNumber,
      final int manufacturerId,
      final int version,
      final int deviceType,
      final int encryptionKeyStatus) {

    final Integer classId = MBusClientSetup.MBUS_CLIENT_CLASS_ID;
    final ObisCode obisCode = new ObisCode(0, channel, 24, 1, 0, 255);

    this.dynamicValues()
        .setDefaultAttributeValue(
            classId,
            obisCode,
            MBusClientSetup.ATTRIBUTE_ID_PRIMARY_ADDRESS,
            DataObject.newUInteger8Data((short) primaryAddress));
    this.dynamicValues()
        .setDefaultAttributeValue(
            classId,
            obisCode,
            MBusClientSetup.ATTRIBUTE_ID_IDENTIFICATION_NUMBER,
            DataObject.newUInteger32Data(identificationNumber));
    this.dynamicValues()
        .setDefaultAttributeValue(
            classId,
            obisCode,
            MBusClientSetup.ATTRIBUTE_ID_MANUFACTURER_ID,
            DataObject.newUInteger16Data(manufacturerId));
    this.dynamicValues()
        .setDefaultAttributeValue(
            classId,
            obisCode,
            MBusClientSetup.ATTRIBUTE_ID_VERSION,
            DataObject.newUInteger8Data((short) version));
    this.dynamicValues()
        .setDefaultAttributeValue(
            classId,
            obisCode,
            MBusClientSetup.ATTRIBUTE_ID_DEVICE_TYPE,
            DataObject.newUInteger8Data((short) deviceType));
    this.dynamicValues()
        .setDefaultAttributeValue(
            classId,
            obisCode,
            MBusClientSetup.ATTRIBUTE_ID_ENCRYPTION_KEY_STATUS,
            DataObject.newEnumerateData(encryptionKeyStatus));
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
  public LongUnsignedRegister instantaneousVoltageL1() {
    return new LongUnsignedRegister(INSTANTANEOUS_VOLTAGE_L1_LOGICAL_NAME, 1, 0, UnitType.VOLT);
  }

  @Bean
  public LongUnsignedRegister instantaneousVoltageL2() {
    return new LongUnsignedRegister(INSTANTANEOUS_VOLTAGE_L2_LOGICAL_NAME, 1, 0, UnitType.VOLT);
  }

  @Bean
  public LongUnsignedRegister instantaneousVoltageL3() {
    return new LongUnsignedRegister(INSTANTANEOUS_VOLTAGE_L3_LOGICAL_NAME, 1, 0, UnitType.VOLT);
  }

  @Bean
  public LongUnsignedRegister averageVoltageL1() {
    return new LongUnsignedRegister(AVERAGE_VOLTAGE_L1_LOGICAL_NAME, 1, 0, UnitType.VOLT);
  }

  @Bean
  public LongUnsignedRegister averageVoltageL2() {
    return new LongUnsignedRegister(AVERAGE_VOLTAGE_L2_LOGICAL_NAME, 1, 0, UnitType.VOLT);
  }

  @Bean
  public LongUnsignedRegister averageVoltageL3() {
    return new LongUnsignedRegister(AVERAGE_VOLTAGE_L3_LOGICAL_NAME, 1, 0, UnitType.VOLT);
  }

  @Bean
  public LongUnsignedRegister instantaneousActivePowerImportL1() {
    return new LongUnsignedRegister(
        INSTANTANEOUS_ACTIVE_POWER_IMPORT_L1_LOGICAL_NAME, 1, 0, UnitType.WATT);
  }

  @Bean
  public LongUnsignedRegister instantaneousActivePowerImportL2() {
    return new LongUnsignedRegister(
        INSTANTANEOUS_ACTIVE_POWER_IMPORT_L2_LOGICAL_NAME, 1, 0, UnitType.WATT);
  }

  @Bean
  public LongUnsignedRegister instantaneousActivePowerImportL3() {
    return new LongUnsignedRegister(
        INSTANTANEOUS_ACTIVE_POWER_IMPORT_L3_LOGICAL_NAME, 1, 0, UnitType.WATT);
  }

  @Bean
  public LongUnsignedRegister instantaneousActivePowerExportL1() {
    return new LongUnsignedRegister(
        INSTANTANEOUS_ACTIVE_POWER_EXPORT_L1_LOGICAL_NAME, 1, 0, UnitType.WATT);
  }

  @Bean
  public LongUnsignedRegister instantaneousActivePowerExportL2() {
    return new LongUnsignedRegister(
        INSTANTANEOUS_ACTIVE_POWER_EXPORT_L2_LOGICAL_NAME, 1, 0, UnitType.WATT);
  }

  @Bean
  public LongUnsignedRegister instantaneousActivePowerExportL3() {
    return new LongUnsignedRegister(
        INSTANTANEOUS_ACTIVE_POWER_EXPORT_L3_LOGICAL_NAME, 1, 0, UnitType.WATT);
  }

  @Bean
  public LongUnsignedRegister instantaneousActivePowerExport() {
    return new LongUnsignedRegister(
        INSTANTANEOUS_ACTIVE_POWER_EXPORT_LOGICAL_NAME, 1, 0, UnitType.WATT);
  }

  @Bean
  public LongUnsignedRegister instantaneousActivePowerImport() {
    return new LongUnsignedRegister(
        INSTANTANEOUS_ACTIVE_POWER_IMPORT_LOGICAL_NAME, 1, 0, UnitType.WATT);
  }

  @Bean
  public LongUnsignedRegister averageCurrentL1() {
    return new LongUnsignedRegister(AVERAGE_CURRENT_L1_LOGICAL_NAME, 1, 0, UnitType.AMPERE);
  }

  @Bean
  public LongUnsignedRegister averageCurrentL2() {
    return new LongUnsignedRegister(AVERAGE_CURRENT_L2_LOGICAL_NAME, 1, 0, UnitType.AMPERE);
  }

  @Bean
  public LongUnsignedRegister averageCurrentL3() {
    return new LongUnsignedRegister(AVERAGE_CURRENT_L3_LOGICAL_NAME, 1, 0, UnitType.AMPERE);
  }

  @Bean
  public LongUnsignedRegister instantaneousCurrentL1() {
    return new LongUnsignedRegister(INSTANTANEOUS_CURRENT_L1_LOGICAL_NAME, 1, 0, UnitType.AMPERE);
  }

  @Bean
  public LongUnsignedRegister instantaneousCurrentL2() {
    return new LongUnsignedRegister(INSTANTANEOUS_CURRENT_L2_LOGICAL_NAME, 1, 0, UnitType.AMPERE);
  }

  @Bean
  public LongUnsignedRegister instantaneousCurrentL3() {
    return new LongUnsignedRegister(INSTANTANEOUS_CURRENT_L3_LOGICAL_NAME, 1, 0, UnitType.AMPERE);
  }

  @Bean
  public LongUnsignedRegister averageActivePowerImportL1() {
    return new LongUnsignedRegister("1.0.21.24.0.255", 1, 0, UnitType.WATT);
  }

  @Bean
  public LongUnsignedRegister averageActivePowerImportL2() {
    return new LongUnsignedRegister("1.0.41.24.0.255", 1, 0, UnitType.WATT);
  }

  @Bean
  public LongUnsignedRegister averageActivePowerImportL3() {
    return new LongUnsignedRegister("1.0.61.24.0.255", 1, 0, UnitType.WATT);
  }

  @Bean
  public LongUnsignedRegister averageActivePowerExportL1() {
    return new LongUnsignedRegister("1.0.22.24.0.255", 1, 0, UnitType.WATT);
  }

  @Bean
  public LongUnsignedRegister averageActivePowerExportL2() {
    return new LongUnsignedRegister("1.0.42.24.0.255", 1, 0, UnitType.WATT);
  }

  @Bean
  public LongUnsignedRegister averageActivePowerExportL3() {
    return new LongUnsignedRegister("1.0.62.24.0.255", 1, 0, UnitType.WATT);
  }

  @Bean
  public LongUnsignedRegister averageReactivePowerImportL1() {
    return new LongUnsignedRegister("1.0.23.24.0.255", 1, 0, UnitType.VAR);
  }

  @Bean
  public LongUnsignedRegister averageReactivePowerImportL2() {
    return new LongUnsignedRegister("1.0.43.24.0.255", 1, 0, UnitType.VAR);
  }

  @Bean
  public LongUnsignedRegister averageReactivePowerImportL3() {
    return new LongUnsignedRegister("1.0.63.24.0.255", 1, 0, UnitType.VAR);
  }

  @Bean
  public LongUnsignedRegister averageReactivePowerExportL1() {
    return new LongUnsignedRegister("1.0.24.24.0.255", 1, 0, UnitType.VAR);
  }

  @Bean
  public LongUnsignedRegister averageReactivePowerExportL2() {
    return new LongUnsignedRegister("1.0.44.24.0.255", 1, 0, UnitType.VAR);
  }

  @Bean
  public LongUnsignedRegister averageReactivePowerExportL3() {
    return new LongUnsignedRegister("1.0.64.24.0.255", 1, 0, UnitType.VAR);
  }

  @Bean
  public LongUnsignedData numberOfLongPowerFailuresInAnyPhases() {
    return new LongUnsignedData("0.0.96.7.9.255", 1);
  }

  @Bean
  public LongUnsignedData numberOfPowerFailuresInAnyPhases() {
    return new LongUnsignedData("0.0.96.7.21.255", 1);
  }

  @Bean
  public LongUnsignedData numberOfVoltageSagsInPhaseL1() {
    return new LongUnsignedData(NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L1_LOGICAL_NAME, 1);
  }

  @Bean
  public LongUnsignedData numberOfVoltageSwellsInPhaseL1() {
    return new LongUnsignedData(NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L1_LOGICAL_NAME, 1);
  }

  @Bean
  public LongUnsignedData numberOfVoltageSagsInPhaseL2() {
    return new LongUnsignedData(NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L2_LOGICAL_NAME, 1);
  }

  @Bean
  public LongUnsignedData numberOfVoltageSwellsInPhaseL2() {
    return new LongUnsignedData(NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L2_LOGICAL_NAME, 1);
  }

  @Bean
  public LongUnsignedData numberOfVoltageSagsInPhaseL3() {
    return new LongUnsignedData(NUMBER_OF_VOLTAGE_SAGS_IN_PHASE_L3_LOGICAL_NAME, 1);
  }

  @Bean
  public LongUnsignedData numberOfVoltageSwellsInPhaseL3() {
    return new LongUnsignedData(NUMBER_OF_VOLTAGE_SWELLS_IN_PHASE_L3_LOGICAL_NAME, 1);
  }

  @Bean
  public LongUnsignedRegister instantaneousCurrentSumOfAllPhases() {
    return new LongUnsignedRegister("1.0.90.7.0.255", 1, 0, UnitType.AMPERE);
  }

  @Bean
  public LongUnsignedRegister averageActivePowerImportL1Value() {
    return new LongUnsignedRegister(
        AVERAGE_ACTIVE_POWER_IMPORT_L1_LOGICAL_NAME, 1, 0, UnitType.WATT);
  }

  @Bean
  public LongUnsignedRegister averageActivePowerImportL2Value() {
    return new LongUnsignedRegister(
        AVERAGE_ACTIVE_POWER_IMPORT_L2_LOGICAL_NAME, 1, 0, UnitType.WATT);
  }

  @Bean
  public LongUnsignedRegister averageActivePowerImportL3Value() {
    return new LongUnsignedRegister(
        AVERAGE_ACTIVE_POWER_IMPORT_L3_LOGICAL_NAME, 1, 0, UnitType.WATT);
  }

  @Bean
  public LongUnsignedRegister averageActivePowerExportL1Value() {
    return new LongUnsignedRegister(
        AVERAGE_ACTIVE_POWER_EXPORT_L1_LOGICAL_NAME, 1, 0, UnitType.WATT);
  }

  @Bean
  public LongUnsignedRegister averageActivePowerExportL2Value() {
    return new LongUnsignedRegister(
        AVERAGE_ACTIVE_POWER_EXPORT_L2_LOGICAL_NAME, 1, 0, UnitType.WATT);
  }

  @Bean
  public LongUnsignedRegister averageActivePowerExportL3Value() {
    return new LongUnsignedRegister(
        AVERAGE_ACTIVE_POWER_EXPORT_L3_LOGICAL_NAME, 1, 0, UnitType.WATT);
  }

  @Bean
  public LongUnsignedRegister averageReactivePowerImportL1Value() {
    return new LongUnsignedRegister(
        AVERAGE_REACTIVE_POWER_IMPORT_L1_LOGICAL_NAME, 1, 0, UnitType.VAR);
  }

  @Bean
  public LongUnsignedRegister averageReactivePowerImportL2Value() {
    return new LongUnsignedRegister(
        AVERAGE_REACTIVE_POWER_IMPORT_L2_LOGICAL_NAME, 1, 0, UnitType.VAR);
  }

  @Bean
  public LongUnsignedRegister averageReactivePowerImportL3Value() {
    return new LongUnsignedRegister(
        AVERAGE_REACTIVE_POWER_IMPORT_L3_LOGICAL_NAME, 1, 0, UnitType.VAR);
  }

  @Bean
  public LongUnsignedRegister averageReactivePowerExportL1Value() {
    return new LongUnsignedRegister(
        AVERAGE_REACTIVE_POWER_EXPORT_L1_LOGICAL_NAME, 1, 0, UnitType.VAR);
  }

  @Bean
  public LongUnsignedRegister averageReactivePowerExportL2Value() {
    return new LongUnsignedRegister(
        AVERAGE_REACTIVE_POWER_EXPORT_L2_LOGICAL_NAME, 1, 0, UnitType.VAR);
  }

  @Bean
  public LongUnsignedRegister averageReactivePowerExportL3Value() {
    return new LongUnsignedRegister(
        AVERAGE_REACTIVE_POWER_EXPORT_L3_LOGICAL_NAME, 1, 0, UnitType.VAR);
  }

  @Bean
  public SetRandomisationSettings setRandomisationSettings() {
    return new SetRandomisationSettings();
  }
}
