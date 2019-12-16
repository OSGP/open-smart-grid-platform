/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.ACTIVE_ENERGY_EXPORT;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.ACTIVE_ENERGY_EXPORT_RATE_1;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.ACTIVE_ENERGY_EXPORT_RATE_2;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.ACTIVE_ENERGY_IMPORT;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.ACTIVE_ENERGY_IMPORT_RATE_1;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.ACTIVE_ENERGY_IMPORT_RATE_2;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.ALARM_FILTER;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.AMR_STATUS;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.DAILY_LOAD_PROFILE;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.EXTERNAL_TRIGGER;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.INTERNAL_TRIGGER_ALARM;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.INTERVAL_VALUES;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.MBUS_MASTER_VALUE;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.MONTHLY_BILLING_VALUES;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.PUSH_SCHEDULER;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.PUSH_SETUP_ALARM;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.PUSH_SETUP_SCHEDULER;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.RANDOMISATION_SETTINGS;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.Medium.ELECTRICITY;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.Medium.GAS;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.ProfileCaptureTime.DAY;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.ProfileCaptureTime.HOUR;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.ProfileCaptureTime.MONTH;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.ProfileCaptureTime.QUARTER_HOUR;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.RegisterUnit.M3;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.RegisterUnit.WH;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsAutoAnswer;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsClock;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsData;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsExtendedRegister;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsProfile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsPushSetup;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsRegister;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsRegisterMonitor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsSingleActionSchedule;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;

public class DlmsObjectConfigSmr50 extends DlmsObjectConfig {

    @Override
    List<Protocol> initProtocols() {
        return Arrays.asList(Protocol.SMR_5_0, Protocol.SMR_5_1);
    }

    @Override
    List<DlmsObject> initObjects() {
        final List<DlmsObject> objectList = new ArrayList<>();

        // @formatter:off

        // Abstract objects
        final DlmsObject clock = new DlmsClock("0.0.1.0.0.255");
        final DlmsObject amrStatusIntervalE = new DlmsData(AMR_STATUS, "0.0.96.10.2.255");
        final DlmsObject amrStatusIntervalG = new DlmsData(AMR_STATUS, "0.<c>.96.10.3.255");
        final DlmsObject amrStatusDailyE = new DlmsData(AMR_STATUS, "0.0.96.10.4.255");
        final DlmsObject amrStatusDailyG = new DlmsData(AMR_STATUS, "0.<c>.96.10.5.255");
        final DlmsObject amrStatusMonthlyE = new DlmsData(AMR_STATUS, "0.0.96.10.6.255");
        final DlmsObject amrStatusMonthlyG = new DlmsData(AMR_STATUS, "0.<c>.96.10.7.255");
        final DlmsObject alarmFilter = new DlmsData(ALARM_FILTER, "0.0.97.98.10.255");
        final DlmsObject randomisationSettings = new DlmsData(RANDOMISATION_SETTINGS, "0.1.94.31.12.255");

        objectList.addAll(Arrays.asList(clock, amrStatusIntervalE, amrStatusIntervalG, amrStatusDailyE,
                amrStatusDailyG, amrStatusMonthlyE, amrStatusMonthlyG, alarmFilter, randomisationSettings));

        final DlmsObject pushScheduler = new DlmsSingleActionSchedule(PUSH_SCHEDULER, "0.0.15.0.4.255");
        final DlmsObject pushSetupScheduler = new DlmsPushSetup(PUSH_SETUP_SCHEDULER, "0.0.25.9.0.255");
        final DlmsObject externalTriggerSmsOrCsd = new DlmsAutoAnswer(EXTERNAL_TRIGGER, "0.0.2.2.0.255");
        final DlmsObject internalTriggerAlarm = new DlmsRegisterMonitor(INTERNAL_TRIGGER_ALARM, "0.0.16.1.0.255");
        final DlmsObject pushSetupAlarm = new DlmsPushSetup(PUSH_SETUP_ALARM, "0.1.25.9.0.255");

        objectList.addAll(Arrays.asList(pushScheduler, pushSetupScheduler, externalTriggerSmsOrCsd, internalTriggerAlarm,
                pushSetupAlarm));


        // Electricity objects
        final DlmsObject activeEnergyImport =
                new DlmsRegister(ACTIVE_ENERGY_IMPORT, "1.0.1.8.0.255", 0, WH, ELECTRICITY);
        final DlmsObject activeEnergyExport =
                new DlmsRegister(ACTIVE_ENERGY_EXPORT, "1.0.2.8.0.255", 0, WH, ELECTRICITY);
        final DlmsObject activeEnergyImportRate1 =
                new DlmsRegister(ACTIVE_ENERGY_IMPORT_RATE_1, "1.0.1.8.1.255", 0, WH, ELECTRICITY);
        final DlmsObject activeEnergyImportRate2 =
                new DlmsRegister(ACTIVE_ENERGY_IMPORT_RATE_2, "1.0.1.8.2.255", 0, WH, ELECTRICITY);
        final DlmsObject activeEnergyExportRate1 =
                new DlmsRegister(ACTIVE_ENERGY_EXPORT_RATE_1, "1.0.2.8.1.255", 0, WH, ELECTRICITY);
        final DlmsObject activeEnergyExportRate2 =
                new DlmsRegister(ACTIVE_ENERGY_EXPORT_RATE_2, "1.0.2.8.2.255", 0, WH, ELECTRICITY);

        objectList.addAll(Arrays.asList(activeEnergyImport, activeEnergyExport, activeEnergyImportRate1, activeEnergyImportRate2,
                activeEnergyExportRate1, activeEnergyExportRate2));

        // Gas objects
        final DlmsObject mbusMasterValue =
                new DlmsExtendedRegister(MBUS_MASTER_VALUE, "0.<c>.24.2.2.255", 0, M3, GAS);
        objectList.add(mbusMasterValue);

        final List<DlmsCaptureObject> captureObjectsIntervalE = Arrays.asList(
                DlmsCaptureObject.create(clock),
                DlmsCaptureObject.create(amrStatusIntervalE),
                DlmsCaptureObject.create(activeEnergyImport),
                DlmsCaptureObject.create(activeEnergyExport));
        objectList.add(new DlmsProfile(INTERVAL_VALUES, "1.0.99.1.0.255", captureObjectsIntervalE, QUARTER_HOUR,
                ELECTRICITY));

        final List<DlmsCaptureObject> captureObjectsIntervalG = Arrays.asList(
                DlmsCaptureObject.create(clock),
                DlmsCaptureObject.create(amrStatusIntervalG),
                DlmsCaptureObject.create(mbusMasterValue),
                DlmsCaptureObject.create(mbusMasterValue, 5));
        objectList.add(new DlmsProfile(INTERVAL_VALUES, "0.<c>.24.3.0.255", captureObjectsIntervalG, HOUR,
                GAS));

        final List<DlmsCaptureObject> captureObjectsDailyE = Arrays.asList(
                DlmsCaptureObject.create(clock),
                DlmsCaptureObject.create(amrStatusDailyE),
                DlmsCaptureObject.create(activeEnergyImportRate1),
                DlmsCaptureObject.create(activeEnergyImportRate2),
                DlmsCaptureObject.create(activeEnergyExportRate1),
                DlmsCaptureObject.create(activeEnergyExportRate2));
        objectList.add(new DlmsProfile(DAILY_LOAD_PROFILE, "1.0.99.2.0.255", captureObjectsDailyE, DAY, ELECTRICITY));

        final List<DlmsCaptureObject> captureObjectsDailyG = Arrays.asList(
                DlmsCaptureObject.create(clock),
                DlmsCaptureObject.create(amrStatusDailyG),
                DlmsCaptureObject.create(mbusMasterValue),
                DlmsCaptureObject.create(mbusMasterValue, 5));
        objectList.add(new DlmsProfile(DAILY_LOAD_PROFILE, "0.<c>.24.3.1.255", captureObjectsDailyG, DAY, GAS));

        final List<DlmsCaptureObject> captureObjectsMonthly = Arrays.asList(
                DlmsCaptureObject.create(clock),
                DlmsCaptureObject.create(amrStatusMonthlyE),
                DlmsCaptureObject.create(activeEnergyImportRate1),
                DlmsCaptureObject.create(activeEnergyImportRate2),
                DlmsCaptureObject.create(activeEnergyExportRate1),
                DlmsCaptureObject.create(activeEnergyExportRate2));
        objectList.add(new DlmsProfile(MONTHLY_BILLING_VALUES, "1.0.98.1.0.255", captureObjectsMonthly, MONTH,
                ELECTRICITY));

        final List<DlmsCaptureObject> captureObjectsMonthlyG = Arrays.asList(
                DlmsCaptureObject.create(clock),
                DlmsCaptureObject.create(amrStatusMonthlyG),
                DlmsCaptureObject.create(mbusMasterValue),
                DlmsCaptureObject.create(mbusMasterValue, 5));
        objectList.add(new DlmsProfile(MONTHLY_BILLING_VALUES, "0.<c>.24.3.2.255", captureObjectsMonthlyG, MONTH,
                GAS));

        return objectList;
    }
}
