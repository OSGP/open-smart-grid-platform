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
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.EXTERNAL_TRIGGER_CSD;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.EXTERNAL_TRIGGER_SMS;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.INTERNAL_TRIGGER_ALARM;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.INTERVAL_VALUES;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.MBUS_MASTER_VALUE;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.MONTHLY_BILLING_VALUES;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.PUSH_SCHEDULER;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.PUSH_SETUP_ALARM;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.PUSH_SETUP_SCHEDULER;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.RANDOMISATION_SETTINGS;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.Medium.COMBINED;
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
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsMessageHandler;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsProfile;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsPushSetup;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsRegister;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsRegisterMonitor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model.DlmsSingleActionSchedule;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;

public class DlmsObjectConfigDsmr422 extends DlmsObjectConfig {

    @Override
    List<Protocol> initProtocols() {
        return Arrays.asList(Protocol.DSMR_4_2_2, Protocol.OTHER_PROTOCOL);
    }

    @Override
    List<DlmsObject> initObjects() {
        final List<DlmsObject> objectList = new ArrayList<>();

        // @formatter:off

        // Abstract objects
        final DlmsObject clock = new DlmsClock("0.0.1.0.0.255");
        final DlmsObject amrStatus = new DlmsData(AMR_STATUS, "0.0.96.10.2.255");
        final DlmsObject amrStatusMbus = new DlmsData(AMR_STATUS, "0.<c>.96.10.2.255");
        final DlmsObject alarmFilter = new DlmsData(ALARM_FILTER, "0.0.97.98.10.255");
        final DlmsObject randomisationSettings = new DlmsData(RANDOMISATION_SETTINGS, "0.1.94.31.12.255");

        objectList.addAll(Arrays.asList(clock, amrStatus, amrStatusMbus, alarmFilter, randomisationSettings));

        final DlmsObject pushScheduler = new DlmsSingleActionSchedule(PUSH_SCHEDULER, "0.0.15.0.4.255");
        final DlmsObject pushSetupScheduler = new DlmsPushSetup(PUSH_SETUP_SCHEDULER, "0.0.25.9.0.255");
        final DlmsObject externalTriggerSms = new DlmsMessageHandler(EXTERNAL_TRIGGER_SMS, "0.0.2.3.0.255");
        final DlmsObject externalTriggerCsd = new DlmsAutoAnswer(EXTERNAL_TRIGGER_CSD, "0.0.2.2.0.255");
        final DlmsObject internalTriggerAlarm = new DlmsRegisterMonitor(INTERNAL_TRIGGER_ALARM, "0.0.16.1.0.255");
        final DlmsObject pushSetupAlarm = new DlmsPushSetup(PUSH_SETUP_ALARM, "0.1.25.9.0.255");

        objectList.addAll(Arrays.asList(pushScheduler, pushSetupScheduler, externalTriggerSms, externalTriggerCsd,
                internalTriggerAlarm, pushSetupAlarm));

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
                new DlmsExtendedRegister(MBUS_MASTER_VALUE, "0.<c>.24.2.1.255", 0, M3, GAS);
        objectList.add(mbusMasterValue);

        // Profiles
        final List<DlmsCaptureObject> captureObjectsIntervalE = Arrays.asList(
                DlmsCaptureObject.create(clock),
                DlmsCaptureObject.create(amrStatus),
                DlmsCaptureObject.create(activeEnergyImport),
                DlmsCaptureObject.create(activeEnergyExport));
        objectList.add(new DlmsProfile(INTERVAL_VALUES, "1.0.99.1.0.255", captureObjectsIntervalE, QUARTER_HOUR,
                ELECTRICITY));

        final List<DlmsCaptureObject> captureObjectsIntervalG = Arrays.asList(
                DlmsCaptureObject.create(clock),
                DlmsCaptureObject.create(amrStatusMbus),
                DlmsCaptureObject.create(mbusMasterValue),
                DlmsCaptureObject.create(mbusMasterValue, 5));
        objectList.add(new DlmsProfile(INTERVAL_VALUES, "0.<c>.24.3.0.255", captureObjectsIntervalG, HOUR,
                GAS));

        final List<DlmsCaptureObject> captureObjectsDaily = Arrays.asList(
                DlmsCaptureObject.create(clock),
                DlmsCaptureObject.create(amrStatus),
                DlmsCaptureObject.create(activeEnergyImportRate1),
                DlmsCaptureObject.create(activeEnergyImportRate2),
                DlmsCaptureObject.create(activeEnergyExportRate1),
                DlmsCaptureObject.create(activeEnergyExportRate2),
                DlmsCaptureObject.createWithChannel(mbusMasterValue, 1),
                DlmsCaptureObject.createWithChannel(mbusMasterValue, 1, 5),
                DlmsCaptureObject.createWithChannel(mbusMasterValue, 2),
                DlmsCaptureObject.createWithChannel(mbusMasterValue, 2, 5),
                DlmsCaptureObject.createWithChannel(mbusMasterValue, 3),
                DlmsCaptureObject.createWithChannel(mbusMasterValue, 3, 5),
                DlmsCaptureObject.createWithChannel(mbusMasterValue, 4),
                DlmsCaptureObject.createWithChannel(mbusMasterValue, 4, 5));
        objectList.add(new DlmsProfile(DAILY_LOAD_PROFILE, "1.0.99.2.0.255", captureObjectsDaily, DAY, COMBINED));

        final List<DlmsCaptureObject> captureObjectsMonthly = Arrays.asList(
                DlmsCaptureObject.create(clock),
                DlmsCaptureObject.create(activeEnergyImportRate1),
                DlmsCaptureObject.create(activeEnergyImportRate2),
                DlmsCaptureObject.create(activeEnergyExportRate1),
                DlmsCaptureObject.create(activeEnergyExportRate2),
                DlmsCaptureObject.createWithChannel(mbusMasterValue, 1),
                DlmsCaptureObject.createWithChannel(mbusMasterValue, 1, 5),
                DlmsCaptureObject.createWithChannel(mbusMasterValue, 2),
                DlmsCaptureObject.createWithChannel(mbusMasterValue, 2, 5),
                DlmsCaptureObject.createWithChannel(mbusMasterValue, 3),
                DlmsCaptureObject.createWithChannel(mbusMasterValue, 3, 5),
                DlmsCaptureObject.createWithChannel(mbusMasterValue, 4),
                DlmsCaptureObject.createWithChannel(mbusMasterValue, 4, 5));
        objectList.add(new DlmsProfile(MONTHLY_BILLING_VALUES, "0.0.98.1.0.255", captureObjectsMonthly, MONTH,
                COMBINED));

        return objectList;
    }
}
