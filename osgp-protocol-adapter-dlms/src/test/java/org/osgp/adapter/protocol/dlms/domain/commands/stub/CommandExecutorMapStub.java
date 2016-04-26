/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands.stub;

import org.osgp.adapter.protocol.dlms.domain.commands.CommandExecutorMap;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendarDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsDataGasDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusTypeDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQueryDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GMeterInfoDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministrativeStatusDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetAssociationLnObjectsRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetConfigurationRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetFirmwareVersionRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGasRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetAlarmNotificationsRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetConfigurationObjectRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetPushSetupAlarmRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetPushSetupSmsRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SynchronizeTimeRequestDataDto;

public class CommandExecutorMapStub extends CommandExecutorMap {

    public CommandExecutorMapStub() {

        this.addCommandExecutor(FindEventsQueryDto.class, new RetrieveEventsBundleCommandExecutorStub());
        this.addCommandExecutor(ActualMeterReadsDataDto.class, new GetActualMeterReadsBundleCommandExecutorStub());
        this.addCommandExecutor(ActualMeterReadsDataGasDto.class, new GetActualMeterReadsBundleGasCommandExecutorStub());
        this.addCommandExecutor(SpecialDaysRequestDataDto.class, new SetSpecialDaysBundleCommandExecutorStub());
        this.addCommandExecutor(ReadAlarmRegisterDataDto.class, new ReadAlarmRegisterBundleCommandExecutorStub());
        this.addCommandExecutor(GetAdministrativeStatusDataDto.class,
                new GetAdministrativeStatusBundleCommandExecutorStub());
        this.addCommandExecutor(PeriodicMeterReadsRequestDataDto.class,
                new GetPeriodicMeterReadsBundleCommandExecutorStub());
        this.addCommandExecutor(PeriodicMeterReadsGasRequestDataDto.class,
                new GetPeriodicMeterReadsGasBundleCommandExecutorStub());
        this.addCommandExecutor(AdministrativeStatusTypeDataDto.class,
                new SetAdministrativeStatusBundleCommandExecutorStub());
        this.addCommandExecutor(ActivityCalendarDataDto.class, new SetActivityCalendarBundleCommandExecutorStub());
        this.addCommandExecutor(GMeterInfoDto.class, new SetEncryptionKeyExchangeOnGMeterBundleCommandExecutorStub());
        this.addCommandExecutor(SetAlarmNotificationsRequestDataDto.class,
                new SetAlarmNotificationsBundleCommandExecutorStub());
        this.addCommandExecutor(SetConfigurationObjectRequestDataDto.class,
                new SetConfigurationObjectBundleCommandExecutorStub());
        this.addCommandExecutor(SetPushSetupAlarmRequestDataDto.class, new SetPushSetupAlarmBundleCommandExecutorStub());
        this.addCommandExecutor(SetPushSetupSmsRequestDataDto.class, new SetPushSetupSmsBundleCommandExecutorStub());
        this.addCommandExecutor(SynchronizeTimeRequestDataDto.class, new SynchronizeTimeBundleCommandExecutorStub());
        this.addCommandExecutor(GetConfigurationRequestDataDto.class,
                new RetrieveConfigurationObjectsBundleCommandExecutorStub());
        this.addCommandExecutor(GetFirmwareVersionRequestDataDto.class,
                new GetFirmwareVersionsBundleCommandExecutorStub());
        this.addCommandExecutor(GetAssociationLnObjectsRequestDataDto.class,
                new GetAssociationLnObjectsBundleCommandExecutorStub());
    }
}
