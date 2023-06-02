//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.CommandExecutorMap;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActivityCalendarDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsDataGasDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdministrativeStatusTypeDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FindEventsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GMeterInfoDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetAdministrativeStatusDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetAllAttributeValuesRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetAssociationLnObjectsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetFirmwareVersionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsGasRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ReadAlarmRegisterDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetAlarmNotificationsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetConfigurationObjectRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetPushSetupAlarmRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetPushSetupLastGaspRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetPushSetupSmsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecialDaysRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SynchronizeTimeRequestDto;

public class CommandExecutorMapStub extends CommandExecutorMap {

  public CommandExecutorMapStub() {

    this.addCommandExecutor(
        FindEventsRequestDto.class, new RetrieveEventsBundleCommandExecutorStub());
    this.addCommandExecutor(
        ActualMeterReadsDataDto.class, new GetActualMeterReadsBundleCommandExecutorStub());
    this.addCommandExecutor(
        ActualPowerQualityRequestDto.class, new GetActualPowerQualityBundleCommandExecutorStub());
    this.addCommandExecutor(
        ActualMeterReadsDataGasDto.class, new GetActualMeterReadsBundleGasCommandExecutorStub());
    this.addCommandExecutor(
        SpecialDaysRequestDataDto.class, new SetSpecialDaysBundleCommandExecutorStub());
    this.addCommandExecutor(
        ReadAlarmRegisterDataDto.class, new ReadAlarmRegisterBundleCommandExecutorStub());
    this.addCommandExecutor(
        GetAdministrativeStatusDataDto.class,
        new GetAdministrativeStatusBundleCommandExecutorStub());
    this.addCommandExecutor(
        PeriodicMeterReadsRequestDataDto.class,
        new GetPeriodicMeterReadsBundleCommandExecutorStub());
    this.addCommandExecutor(
        PeriodicMeterReadsGasRequestDto.class,
        new GetPeriodicMeterReadsGasBundleCommandExecutorStub());
    this.addCommandExecutor(
        AdministrativeStatusTypeDataDto.class,
        new SetAdministrativeStatusBundleCommandExecutorStub());
    this.addCommandExecutor(
        ActivityCalendarDataDto.class, new SetActivityCalendarBundleCommandExecutorStub());
    this.addCommandExecutor(GMeterInfoDto.class, new SetKeyOnGMeterBundleCommandExecutorStub());
    this.addCommandExecutor(
        SetAlarmNotificationsRequestDto.class,
        new SetAlarmNotificationsBundleCommandExecutorStub());
    this.addCommandExecutor(
        SetConfigurationObjectRequestDataDto.class,
        new SetConfigurationObjectBundleCommandExecutorStub());
    this.addCommandExecutor(
        SetPushSetupAlarmRequestDto.class, new SetPushSetupAlarmBundleCommandExecutorStub());
    this.addCommandExecutor(
        SetPushSetupLastGaspRequestDto.class, new SetPushSetupLastGaspBundleCommandExecutorStub());
    this.addCommandExecutor(
        SetPushSetupSmsRequestDto.class, new SetPushSetupSmsBundleCommandExecutorStub());
    this.addCommandExecutor(
        SynchronizeTimeRequestDto.class, new SynchronizeTimeBundleCommandExecutorStub());
    this.addCommandExecutor(
        GetAllAttributeValuesRequestDto.class,
        new RetrieveAttributeValuesBundleCommandExecutorStub());
    this.addCommandExecutor(
        GetFirmwareVersionRequestDto.class, new GetFirmwareVersionsBundleCommandExecutorStub());
    this.addCommandExecutor(
        GetAssociationLnObjectsRequestDto.class,
        new GetAssociationLnObjectsBundleCommandExecutorStub());
  }
}
