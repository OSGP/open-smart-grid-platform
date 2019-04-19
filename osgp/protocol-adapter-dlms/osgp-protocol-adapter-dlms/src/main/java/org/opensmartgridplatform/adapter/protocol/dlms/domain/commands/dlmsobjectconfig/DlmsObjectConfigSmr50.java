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
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.ACTIVE_ENERGY_EXPORT_RATE_2;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.ACTIVE_ENERGY_IMPORT;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.ACTIVE_ENERGY_IMPORT_RATE_1;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.AMR_STATUS;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.CLOCK;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.DAILY_LOAD_PROFILE;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.INTERVAL_VALUES;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.MBUS_MASTER_VALUE;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType.MONTHLY_BILLING_VALUES;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.Medium.ELECTRICITY;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.Medium.GAS;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.ProfileCaptureTime.DAY;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.ProfileCaptureTime.MONTH;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.ProfileCaptureTime.QUARTER_HOUR;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.RegisterUnit.M3;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.RegisterUnit.WH;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        final DlmsObject clock = new DlmsClock(CLOCK, "0.0.1.0.0.255");
        final DlmsObject amrStatusIntervalE = new DlmsData(AMR_STATUS, "0.0.96.10.2.255");
        final DlmsObject amrStatusIntervalG = new DlmsData(AMR_STATUS, "0.<c>.96.10.3.255");
        final DlmsObject amrStatusDailyE = new DlmsData(AMR_STATUS, "0.0.96.10.4.255");
        final DlmsObject amrStatusDailyG = new DlmsData(AMR_STATUS, "0.<c>.96.10.5.255");
        final DlmsObject amrStatusMonthlyE = new DlmsData(AMR_STATUS, "0.0.96.10.6.255");
        final DlmsObject amrStatusMonthlyG = new DlmsData(AMR_STATUS, "0.<c>.96.10.7.255");

        // Electricity objects
        final DlmsObject activeEnergyImport =
                new DlmsRegister(ACTIVE_ENERGY_IMPORT, "1.0.1.8.0.255", 0, WH, ELECTRICITY);
        final DlmsObject activeEnergyExport =
                new DlmsRegister(ACTIVE_ENERGY_EXPORT, "1.0.2.8.0.255", 0, WH, ELECTRICITY);
        final DlmsObject activeEnergyImportRate1 =
                new DlmsRegister(ACTIVE_ENERGY_IMPORT_RATE_1, "1.0.1.8.1.255", 0, WH, ELECTRICITY);
        final DlmsObject activeEnergyImportRate2 =
                new DlmsRegister(ACTIVE_ENERGY_EXPORT_RATE_2, "1.0.1.8.2.255", 0, WH, ELECTRICITY);
        final DlmsObject activeEnergyExportRate1 =
                new DlmsRegister(ACTIVE_ENERGY_IMPORT_RATE_1, "1.0.2.8.1.255", 0, WH, ELECTRICITY);
        final DlmsObject activeEnergyExportRate2 =
                new DlmsRegister(ACTIVE_ENERGY_EXPORT_RATE_2, "1.0.2.8.2.255", 0, WH, ELECTRICITY);

        // Gas objects
        final DlmsObject mbusMasterValue =
                new DlmsExtendedRegister(MBUS_MASTER_VALUE, "0.<c>.24.2.2.255", 0, M3, GAS);

        addProfiles(objectList, clock, amrStatusIntervalE, activeEnergyImport, activeEnergyExport);

        final List<DlmsCaptureObject> captureObjectsIntervalG = Arrays.asList(
                new DlmsCaptureObject(clock),
                new DlmsCaptureObject(amrStatusIntervalG),
                new DlmsCaptureObject(mbusMasterValue),
                new DlmsCaptureObject(mbusMasterValue, 5));
        objectList.add(new DlmsProfile(INTERVAL_VALUES, "0.<c>.24.3.0.255", captureObjectsIntervalG, QUARTER_HOUR,
                GAS));

        final List<DlmsCaptureObject> captureObjectsDailyE = Arrays.asList(
                new DlmsCaptureObject(clock),
                new DlmsCaptureObject(amrStatusDailyE),
                new DlmsCaptureObject(activeEnergyImportRate1),
                new DlmsCaptureObject(activeEnergyImportRate2),
                new DlmsCaptureObject(activeEnergyExportRate1),
                new DlmsCaptureObject(activeEnergyExportRate2));
        objectList.add(new DlmsProfile(DAILY_LOAD_PROFILE, "1.0.99.2.0.255", captureObjectsDailyE, DAY, ELECTRICITY));

        final List<DlmsCaptureObject> captureObjectsDailyG = Arrays.asList(
                new DlmsCaptureObject(clock),
                new DlmsCaptureObject(amrStatusDailyG),
                new DlmsCaptureObject(mbusMasterValue),
                new DlmsCaptureObject(mbusMasterValue, 5));
        objectList.add(new DlmsProfile(DAILY_LOAD_PROFILE, "0.<c>.24.3.1.255", captureObjectsDailyG, DAY, GAS));

        final List<DlmsCaptureObject> captureObjectsMonthly = Arrays.asList(
                new DlmsCaptureObject(clock),
                new DlmsCaptureObject(amrStatusMonthlyE),
                new DlmsCaptureObject(activeEnergyImportRate1),
                new DlmsCaptureObject(activeEnergyImportRate2),
                new DlmsCaptureObject(activeEnergyExportRate1),
                new DlmsCaptureObject(activeEnergyExportRate2));
        objectList.add(new DlmsProfile(MONTHLY_BILLING_VALUES, "1.0.98.1.0.255", captureObjectsMonthly, MONTH,
                ELECTRICITY));

        final List<DlmsCaptureObject> captureObjectsMonthlyG = Arrays.asList(
                new DlmsCaptureObject(clock),
                new DlmsCaptureObject(amrStatusMonthlyG),
                new DlmsCaptureObject(mbusMasterValue),
                new DlmsCaptureObject(mbusMasterValue, 5));
        objectList.add(new DlmsProfile(MONTHLY_BILLING_VALUES, "0.<c>.24.3.2.255", captureObjectsMonthlyG, MONTH,
                GAS));

        return objectList;
    }private void addProfiles(List<DlmsObject> objectList, DlmsObject clock, DlmsObject amrStatusIntervalE,
            DlmsObject activeEnergyImport, DlmsObject activeEnergyExport) {
    final List<DlmsCaptureObject> captureObjectsIntervalE = Arrays.asList(
            new DlmsCaptureObject(clock),
            new DlmsCaptureObject(amrStatusIntervalE),
            new DlmsCaptureObject(activeEnergyImport),
            new DlmsCaptureObject(activeEnergyExport));
        objectList.add(new DlmsProfile(INTERVAL_VALUES, "1.0.99.1.0.255", captureObjectsIntervalE, QUARTER_HOUR,
                ELECTRICITY));}
}
