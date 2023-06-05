// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ClearAlarmRegisterRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ClearMBusStatusOnAllChannelsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ConfigureDefinableLoadProfileRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.CoupleMbusDeviceByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.DecoupleMbusDeviceByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.FindEventsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GenerateAndReplaceKeysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetActualMeterReadsGasRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetActualMeterReadsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetActualPowerQualityRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetAdministrativeStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetAllAttributeValuesRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetAssociationLnObjectsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetConfigurationObjectRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetFirmwareVersionGasRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetFirmwareVersionRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetGsmDiagnosticRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetMbusEncryptionKeyStatusByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetMbusEncryptionKeyStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetOutagesRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetPeriodicMeterReadsGasRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetPeriodicMeterReadsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetPowerQualityProfileRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetSpecificAttributeValueRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ReadAlarmRegisterRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ScanMbusChannelsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetActivityCalendarRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetAdministrativeStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetAlarmNotificationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetClockConfigurationRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetConfigurationObjectRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetDeviceLifecycleStatusByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetKeyOnGMeterRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetKeysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetMbusUserKeyByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetPushSetupAlarmRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetPushSetupLastGaspRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetPushSetupSmsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetRandomisationSettingsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetSpecialDaysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SynchronizeTimeRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.UpdateFirmwareRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Action;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetKeysRequest;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping.AdhocMapper;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping.InstallationMapper;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping.ManagementMapper;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping.MonitoringMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActionRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActivityCalendarData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualMeterReadsGasRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualMeterReadsRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AdministrativeStatusTypeData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ClearMBusStatusOnAllChannelsRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CoupleMbusDeviceByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DefinableLoadProfileConfigurationData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FindEventsRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GenerateAndReplaceKeysRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetAdministrativeStatusData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetAllAttributeValuesRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetAssociationLnObjectsRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetConfigurationObjectRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetFirmwareVersionGasRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetFirmwareVersionRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetGsmDiagnosticRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetKeysRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetMbusEncryptionKeyStatusRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetOutagesRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetPowerQualityProfileRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGasRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ReadAlarmRegisterData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ScanMbusChannelsRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetAlarmNotificationsRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetClockConfigurationRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetKeyOnGMeterRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetKeysRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetMbusUserKeyByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetPushSetupAlarmRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetPushSetupLastGaspRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetPushSetupSmsRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetRandomisationSettingsRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecialDaysRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecificAttributeValueRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SynchronizeTimeRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.UpdateFirmwareRequestData;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service(value = "wsSmartMeteringActionMapperService")
@Validated
public class ActionMapperService {

  private static final Map<Class<?>, ConfigurableMapper> CLASS_TO_MAPPER_MAP = new HashMap<>();
  private static final Map<Class<?>, Class<? extends ActionRequest>> CLASS_MAP = new HashMap<>();

  /** Specifies to which core object the ws object needs to be mapped. */
  static {
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SpecialDaysRequestData.class,
        SpecialDaysRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterData
            .class,
        ReadAlarmRegisterData.class);
    CLASS_MAP.put(ReadAlarmRegisterRequest.class, ReadAlarmRegisterData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindEventsRequestData
            .class,
        FindEventsRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .GetAdministrativeStatusData.class,
        GetAdministrativeStatusData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .PeriodicMeterReadsRequestData.class,
        PeriodicMeterReadsRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .PeriodicMeterReadsGasRequestData.class,
        PeriodicMeterReadsGasRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsData
            .class,
        ActualMeterReadsRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasData
            .class,
        ActualMeterReadsGasRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .AdministrativeStatusTypeData.class,
        AdministrativeStatusTypeData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SetActivityCalendarRequestData.class,
        ActivityCalendarData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SetKeyOnGMeterRequestData.class,
        SetKeyOnGMeterRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SetMbusUserKeyByChannelRequestData.class,
        SetMbusUserKeyByChannelRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SetAlarmNotificationsRequestData.class,
        SetAlarmNotificationsRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SetConfigurationObjectRequestData.class,
        SetConfigurationObjectRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SetPushSetupAlarmRequestData.class,
        SetPushSetupAlarmRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SetPushSetupLastGaspRequestData.class,
        SetPushSetupLastGaspRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SetPushSetupSmsRequestData.class,
        SetPushSetupSmsRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeRequestData
            .class,
        SynchronizeTimeRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc
            .GetAllAttributeValuesRequestData.class,
        GetAllAttributeValuesRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .GetFirmwareVersionRequestData.class,
        GetFirmwareVersionRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .GetFirmwareVersionGasRequestData.class,
        GetFirmwareVersionGasRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .UpdateFirmwareRequestData.class,
        UpdateFirmwareRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetKeysRequestData
            .class,
        SetKeysRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc
            .GetAssociationLnObjectsRequestData.class,
        GetAssociationLnObjectsRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc
            .GetSpecificAttributeValueRequestData.class,
        SpecificAttributeValueRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SetClockConfigurationRequestData.class,
        SetClockConfigurationRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .GetConfigurationObjectRequestData.class,
        GetConfigurationObjectRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .DefinableLoadProfileConfigurationData.class,
        DefinableLoadProfileConfigurationData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation
            .CoupleMbusDeviceByChannelRequestData.class,
        CoupleMbusDeviceByChannelRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation
            .DecoupleMbusDeviceByChannelRequestData.class,
        DecoupleMbusDeviceByChannelRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.ScanMbusChannelsRequestData
            .class,
        ScanMbusChannelsRequestData.class);

    CLASS_MAP.put(SetSpecialDaysRequest.class, SpecialDaysRequestData.class);
    CLASS_MAP.put(ReadAlarmRegisterRequest.class, ReadAlarmRegisterData.class);
    CLASS_MAP.put(FindEventsRequest.class, FindEventsRequestData.class);
    CLASS_MAP.put(GetAdministrativeStatusRequest.class, GetAdministrativeStatusData.class);
    CLASS_MAP.put(GetPeriodicMeterReadsRequest.class, PeriodicMeterReadsRequestData.class);
    CLASS_MAP.put(GetPeriodicMeterReadsGasRequest.class, PeriodicMeterReadsGasRequestData.class);
    CLASS_MAP.put(GetActualMeterReadsRequest.class, ActualMeterReadsRequestData.class);
    CLASS_MAP.put(GetActualMeterReadsGasRequest.class, ActualMeterReadsGasRequestData.class);
    CLASS_MAP.put(SetAdministrativeStatusRequest.class, AdministrativeStatusTypeData.class);
    CLASS_MAP.put(SetActivityCalendarRequest.class, ActivityCalendarData.class);
    CLASS_MAP.put(SetKeyOnGMeterRequest.class, SetKeyOnGMeterRequestData.class);
    CLASS_MAP.put(SetMbusUserKeyByChannelRequest.class, SetMbusUserKeyByChannelRequestData.class);
    CLASS_MAP.put(SetAlarmNotificationsRequest.class, SetAlarmNotificationsRequestData.class);
    CLASS_MAP.put(SetConfigurationObjectRequest.class, SetConfigurationObjectRequestData.class);
    CLASS_MAP.put(SetPushSetupAlarmRequest.class, SetPushSetupAlarmRequestData.class);
    CLASS_MAP.put(SetPushSetupLastGaspRequest.class, SetPushSetupLastGaspRequestData.class);
    CLASS_MAP.put(SetPushSetupSmsRequest.class, SetPushSetupSmsRequestData.class);
    CLASS_MAP.put(SynchronizeTimeRequest.class, SynchronizeTimeRequestData.class);
    CLASS_MAP.put(GetAllAttributeValuesRequest.class, GetAllAttributeValuesRequestData.class);
    CLASS_MAP.put(GetFirmwareVersionRequest.class, GetFirmwareVersionRequestData.class);
    CLASS_MAP.put(GetFirmwareVersionGasRequest.class, GetFirmwareVersionGasRequestData.class);
    CLASS_MAP.put(UpdateFirmwareRequest.class, UpdateFirmwareRequestData.class);
    CLASS_MAP.put(SetKeysRequest.class, SetKeysRequestData.class);
    CLASS_MAP.put(GetAssociationLnObjectsRequest.class, GetAssociationLnObjectsRequestData.class);
    CLASS_MAP.put(GetSpecificAttributeValueRequest.class, SpecificAttributeValueRequestData.class);
    CLASS_MAP.put(SetClockConfigurationRequest.class, SetClockConfigurationRequestData.class);
    CLASS_MAP.put(GetConfigurationObjectRequest.class, GetConfigurationObjectRequestData.class);
    CLASS_MAP.put(GetPowerQualityProfileRequest.class, GetPowerQualityProfileRequestData.class);
    CLASS_MAP.put(GenerateAndReplaceKeysRequest.class, GenerateAndReplaceKeysRequestData.class);
    CLASS_MAP.put(
        ConfigureDefinableLoadProfileRequest.class, DefinableLoadProfileConfigurationData.class);
    CLASS_MAP.put(
        CoupleMbusDeviceByChannelRequest.class, CoupleMbusDeviceByChannelRequestData.class);
    CLASS_MAP.put(
        DecoupleMbusDeviceByChannelRequest.class, DecoupleMbusDeviceByChannelRequestData.class);
    CLASS_MAP.put(
        GetMbusEncryptionKeyStatusRequest.class, GetMbusEncryptionKeyStatusRequestData.class);
    CLASS_MAP.put(
        SetDeviceLifecycleStatusByChannelRequest.class,
        SetDeviceLifecycleStatusByChannelRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.management
            .SetDeviceLifecycleStatusByChannelRequestData.class,
        SetDeviceLifecycleStatusByChannelRequestData.class);
    CLASS_MAP.put(
        ClearAlarmRegisterRequest.class,
        org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ClearAlarmRegisterData
            .class);
    CLASS_MAP.put(
        GetMbusEncryptionKeyStatusByChannelRequest.class,
        GetMbusEncryptionKeyStatusByChannelRequestData.class);
    CLASS_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .GetMbusEncryptionKeyStatusByChannelRequestData.class,
        GetMbusEncryptionKeyStatusByChannelRequestData.class);
    CLASS_MAP.put(ScanMbusChannelsRequest.class, ScanMbusChannelsRequestData.class);
    CLASS_MAP.put(SetRandomisationSettingsRequest.class, SetRandomisationSettingsRequestData.class);
    CLASS_MAP.put(GetOutagesRequest.class, GetOutagesRequestData.class);
    CLASS_MAP.put(GetActualPowerQualityRequest.class, ActualPowerQualityRequest.class);
    CLASS_MAP.put(GetGsmDiagnosticRequest.class, GetGsmDiagnosticRequestData.class);
    CLASS_MAP.put(GetKeysRequest.class, GetKeysRequestData.class);
    CLASS_MAP.put(
        ClearMBusStatusOnAllChannelsRequest.class, ClearMBusStatusOnAllChannelsRequestData.class);
  }

  @Autowired private ManagementMapper managementMapper;
  @Autowired private AdhocMapper adhocMapper;
  @Autowired private ConfigurationMapper configurationMapper;
  @Autowired private MonitoringMapper monitoringMapper;
  @Autowired private InstallationMapper installationMapper;

  /** Specifies which mapper to use for the ws class received. */
  @PostConstruct
  private void postConstruct() {
    this.mapAdHocRequestData();
    this.mapConfigurationRequestData();
    this.mapInstallationRequestData();
    this.mapManagementRequestData();
    this.mapMonitoringRequestData();
  }

  private void mapInstallationRequestData() {
    CLASS_TO_MAPPER_MAP.put(CoupleMbusDeviceByChannelRequest.class, this.installationMapper);
    CLASS_TO_MAPPER_MAP.put(DecoupleMbusDeviceByChannelRequest.class, this.installationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation
            .CoupleMbusDeviceByChannelRequestData.class,
        this.installationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation
            .DecoupleMbusDeviceByChannelRequestData.class,
        this.installationMapper);
  }

  private void mapAdHocRequestData() {
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeRequestData
            .class,
        this.adhocMapper);
    CLASS_TO_MAPPER_MAP.put(SynchronizeTimeRequest.class, this.adhocMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc
            .GetAssociationLnObjectsRequestData.class,
        this.adhocMapper);
    CLASS_TO_MAPPER_MAP.put(GetAssociationLnObjectsRequest.class, this.adhocMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc
            .GetSpecificAttributeValueRequestData.class,
        this.adhocMapper);
    CLASS_TO_MAPPER_MAP.put(GetSpecificAttributeValueRequest.class, this.adhocMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.ScanMbusChannelsRequestData
            .class,
        this.adhocMapper);
    CLASS_TO_MAPPER_MAP.put(ScanMbusChannelsRequest.class, this.adhocMapper);
  }

  private void mapManagementRequestData() {
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindEventsRequestData
            .class,
        this.managementMapper);
    CLASS_TO_MAPPER_MAP.put(FindEventsRequest.class, this.managementMapper);
    CLASS_TO_MAPPER_MAP.put(SetDeviceLifecycleStatusByChannelRequest.class, this.managementMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.management
            .SetDeviceLifecycleStatusByChannelRequestData.class,
        this.managementMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.GetOutagesRequestData
            .class,
        this.managementMapper);
    CLASS_TO_MAPPER_MAP.put(GetOutagesRequest.class, this.managementMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.management
            .GetGsmDiagnosticRequestData.class,
        this.managementMapper);
    CLASS_TO_MAPPER_MAP.put(GetGsmDiagnosticRequest.class, this.managementMapper);
    CLASS_TO_MAPPER_MAP.put(ClearMBusStatusOnAllChannelsRequest.class, this.managementMapper);
  }

  private void mapMonitoringRequestData() {
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterData
            .class,
        this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(ReadAlarmRegisterRequest.class, this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .PeriodicReadsRequestData.class,
        this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(GetPeriodicMeterReadsRequest.class, this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .PeriodicMeterReadsGasRequestData.class,
        this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(GetPeriodicMeterReadsGasRequest.class, this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsData
            .class,
        this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(GetActualMeterReadsRequest.class, this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasData
            .class,
        this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(GetActualMeterReadsGasRequest.class, this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(GetPowerQualityProfileRequest.class, this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .GetPowerQualityProfileRequest.class,
        this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(GetActualPowerQualityRequest.class, this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
            .ActualPowerQualityRequest.class,
        this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ClearAlarmRegisterData
            .class,
        this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(ClearAlarmRegisterRequest.class, this.monitoringMapper);
  }

  private void mapConfigurationRequestData() {
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SpecialDaysRequestData.class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SetSpecialDaysRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .GetAdministrativeStatusData.class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(GetAdministrativeStatusRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .AdministrativeStatusTypeData.class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SetAdministrativeStatusRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SetActivityCalendarRequestData.class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SetActivityCalendarRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SetKeyOnGMeterRequestData.class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SetKeyOnGMeterRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SetMbusUserKeyByChannelRequestData.class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SetMbusUserKeyByChannelRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SetAlarmNotificationsRequestData.class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SetAlarmNotificationsRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SetConfigurationObjectRequestData.class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SetConfigurationObjectRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SetPushSetupAlarmRequestData.class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SetPushSetupAlarmRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SetPushSetupLastGaspRequestData.class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SetPushSetupLastGaspRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SetPushSetupSmsRequestData.class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SetPushSetupSmsRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc
            .GetAllAttributeValuesRequestData.class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(GetAllAttributeValuesRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .GetFirmwareVersionRequestData.class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(GetFirmwareVersionRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .GetFirmwareVersionGasRequestData.class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(GetFirmwareVersionGasRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(UpdateFirmwareRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetKeysRequestData
            .class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SetKeysRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SetClockConfigurationRequestData.class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SetClockConfigurationRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .GetConfigurationObjectRequest.class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(GetConfigurationObjectRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(GenerateAndReplaceKeysRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .DefinableLoadProfileConfigurationData.class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(ConfigureDefinableLoadProfileRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(GetMbusEncryptionKeyStatusRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        GetMbusEncryptionKeyStatusByChannelRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .GetMbusEncryptionKeyStatusByChannelRequestData.class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration
            .SetRandomisationSettingsRequestData.class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SetRandomisationSettingsRequest.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetKeysRequestData
            .class,
        this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(GetKeysRequest.class, this.configurationMapper);
  }

  public List<ActionRequest> mapAllActions(final List<? extends Action> actionList)
      throws FunctionalException {
    final List<ActionRequest> actionRequestList = new ArrayList<>();

    for (final Action action : actionList) {

      final ConfigurableMapper mapper = CLASS_TO_MAPPER_MAP.get(action.getClass());
      final Class<? extends ActionRequest> clazz = CLASS_MAP.get(action.getClass());
      if (mapper != null) {
        actionRequestList.add(this.getActionRequestWithDefaultMapper(action, mapper, clazz));
      } else {
        throw new FunctionalException(
            FunctionalExceptionType.VALIDATION_ERROR,
            ComponentType.DOMAIN_SMART_METERING,
            new AssertionError("No mapper defined for class: " + action.getClass().getName()));
      }
    }

    return actionRequestList;
  }

  private ActionRequest getActionRequestWithDefaultMapper(
      final Action action,
      final ConfigurableMapper mapper,
      final Class<? extends ActionRequest> clazz)
      throws FunctionalException {
    final ActionRequest actionRequest = mapper.map(action, clazz);

    if (actionRequest == null) {
      throw new FunctionalException(
          FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
          ComponentType.WS_SMART_METERING,
          new RuntimeException(
              "No Value Object for Action of class: " + action.getClass().getName()));
    }
    return actionRequest;
  }
}
