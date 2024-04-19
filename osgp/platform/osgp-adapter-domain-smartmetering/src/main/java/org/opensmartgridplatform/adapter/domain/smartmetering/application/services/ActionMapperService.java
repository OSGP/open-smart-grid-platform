// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.CommonMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ManagementMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.ActualMeterReadsRequestGasRequestDataConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.CustomValueToDtoConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.GetFirmwareVersionGasRequestDataConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.PeriodicReadsRequestGasDataConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.SetKeyOnGMeterDataConverter;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.util.FaultResponseFactory;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActionRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActivityCalendarData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualMeterReadsGasRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualMeterReadsRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AdministrativeStatusTypeData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.BundleMessageRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ClearAlarmRegisterData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ClearMBusStatusOnAllChannelsRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CoupleMbusDeviceByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CoupleMbusDeviceRequestData;
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
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetThdFingerprintRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.MbusActionRequest;
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
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetPushSetupUdpRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetRandomisationSettingsRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecialDaysRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecificAttributeValueRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SynchronizeTimeRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.TestAlarmSchedulerRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.UpdateFirmwareRequestData;
import org.opensmartgridplatform.domain.smartmetering.exceptions.GatewayDeviceInvalidForMbusDeviceException;
import org.opensmartgridplatform.domain.smartmetering.exceptions.GatewayDeviceNotSetForMbusDeviceException;
import org.opensmartgridplatform.domain.smartmetering.exceptions.MbusChannelNotFoundException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActivityCalendarDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdministrativeStatusTypeDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BundleMessagesRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClearAlarmRegisterRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClearMBusStatusOnAllChannelsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DecoupleMbusDeviceDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DefinableLoadProfileConfigurationDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseParameterDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FindEventsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GenerateAndReplaceKeysRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetAdministrativeStatusDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetAllAttributeValuesRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetAssociationLnObjectsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetConfigurationObjectRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetFirmwareVersionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetGsmDiagnosticRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetKeysRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetOutagesRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetThdFingerprintRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ReadAlarmRegisterDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ScanMbusChannelsRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetAlarmNotificationsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetClockConfigurationRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetConfigurationObjectRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetKeysRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetMbusUserKeyByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetPushSetupAlarmRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetPushSetupLastGaspRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetPushSetupSmsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetPushSetupUdpRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetRandomisationSettingsRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecialDaysRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecificAttributeValueRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SynchronizeTimeRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.TestAlarmSchedulerRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareRequestDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service(value = "domainSmartMeteringActionMapperService")
@Validated
public class ActionMapperService {

  private static final Map<Class<? extends ActionRequest>, ConfigurableMapper> CLASS_TO_MAPPER_MAP =
      new HashMap<>();

  private static final Map<
          Class<? extends ActionRequest>,
          CustomValueToDtoConverter<? extends ActionRequest, ? extends ActionRequestDto>>
      CUSTOM_CONVERTER_FOR_CLASS = new HashMap<>();

  private static final Map<Class<? extends ActionRequest>, Class<? extends ActionRequestDto>>
      CLASS_MAP = new HashMap<>();

  /** Specifies to which DTO object the core object needs to be mapped. */
  static {
    CLASS_MAP.put(PeriodicMeterReadsRequestData.class, PeriodicMeterReadsRequestDataDto.class);
    CLASS_MAP.put(ActualMeterReadsRequestData.class, ActualMeterReadsDataDto.class);
    CLASS_MAP.put(SpecialDaysRequestData.class, SpecialDaysRequestDataDto.class);
    CLASS_MAP.put(ReadAlarmRegisterData.class, ReadAlarmRegisterDataDto.class);
    CLASS_MAP.put(FindEventsRequestData.class, FindEventsRequestDto.class);
    CLASS_MAP.put(GetAdministrativeStatusData.class, GetAdministrativeStatusDataDto.class);
    CLASS_MAP.put(AdministrativeStatusTypeData.class, AdministrativeStatusTypeDataDto.class);
    CLASS_MAP.put(ActivityCalendarData.class, ActivityCalendarDataDto.class);
    CLASS_MAP.put(SetAlarmNotificationsRequestData.class, SetAlarmNotificationsRequestDto.class);
    CLASS_MAP.put(
        SetConfigurationObjectRequestData.class, SetConfigurationObjectRequestDataDto.class);
    CLASS_MAP.put(SetPushSetupAlarmRequestData.class, SetPushSetupAlarmRequestDto.class);
    CLASS_MAP.put(SetPushSetupLastGaspRequestData.class, SetPushSetupLastGaspRequestDto.class);
    CLASS_MAP.put(SetPushSetupSmsRequestData.class, SetPushSetupSmsRequestDto.class);
    CLASS_MAP.put(SetPushSetupUdpRequestData.class, SetPushSetupUdpRequestDto.class);
    CLASS_MAP.put(SynchronizeTimeRequestData.class, SynchronizeTimeRequestDto.class);
    CLASS_MAP.put(GetAllAttributeValuesRequestData.class, GetAllAttributeValuesRequestDto.class);
    CLASS_MAP.put(GetFirmwareVersionRequestData.class, GetFirmwareVersionRequestDto.class);
    CLASS_MAP.put(UpdateFirmwareRequestData.class, UpdateFirmwareRequestDto.class);
    CLASS_MAP.put(SetKeysRequestData.class, SetKeysRequestDto.class);
    CLASS_MAP.put(SpecificAttributeValueRequestData.class, SpecificAttributeValueRequestDto.class);
    CLASS_MAP.put(
        GetAssociationLnObjectsRequestData.class, GetAssociationLnObjectsRequestDto.class);
    CLASS_MAP.put(CoupleMbusDeviceRequestData.class, GetAssociationLnObjectsRequestDto.class);
    CLASS_MAP.put(SetClockConfigurationRequestData.class, SetClockConfigurationRequestDto.class);
    CLASS_MAP.put(GetThdFingerprintRequestData.class, GetThdFingerprintRequestDataDto.class);
    CLASS_MAP.put(
        GetConfigurationObjectRequestData.class, GetConfigurationObjectRequestDataDto.class);
    CLASS_MAP.put(
        GetPowerQualityProfileRequestData.class, GetPowerQualityProfileRequestDataDto.class);
    CLASS_MAP.put(
        GenerateAndReplaceKeysRequestData.class, GenerateAndReplaceKeysRequestDataDto.class);
    CLASS_MAP.put(
        DefinableLoadProfileConfigurationData.class, DefinableLoadProfileConfigurationDto.class);
    CLASS_MAP.put(
        SetMbusUserKeyByChannelRequestData.class, SetMbusUserKeyByChannelRequestDataDto.class);
    CLASS_MAP.put(
        CoupleMbusDeviceByChannelRequestData.class, CoupleMbusDeviceByChannelRequestDataDto.class);
    CLASS_MAP.put(DecoupleMbusDeviceByChannelRequestData.class, DecoupleMbusDeviceDto.class);
    CLASS_MAP.put(
        GetMbusEncryptionKeyStatusRequestData.class, GetMbusEncryptionKeyStatusRequestDto.class);
    CLASS_MAP.put(ClearAlarmRegisterData.class, ClearAlarmRegisterRequestDto.class);
    CLASS_MAP.put(
        GetMbusEncryptionKeyStatusByChannelRequestData.class,
        GetMbusEncryptionKeyStatusByChannelRequestDataDto.class);
    CLASS_MAP.put(
        SetDeviceLifecycleStatusByChannelRequestData.class,
        SetDeviceLifecycleStatusByChannelRequestDataDto.class);
    CLASS_MAP.put(ScanMbusChannelsRequestData.class, ScanMbusChannelsRequestDataDto.class);
    CLASS_MAP.put(GetOutagesRequestData.class, GetOutagesRequestDto.class);
    CLASS_MAP.put(ActualPowerQualityRequest.class, ActualPowerQualityRequestDto.class);
    CLASS_MAP.put(
        SetRandomisationSettingsRequestData.class, SetRandomisationSettingsRequestDataDto.class);
    CLASS_MAP.put(GetGsmDiagnosticRequestData.class, GetGsmDiagnosticRequestDto.class);
    CLASS_MAP.put(GetKeysRequestData.class, GetKeysRequestDto.class);
    CLASS_MAP.put(
        ClearMBusStatusOnAllChannelsRequestData.class,
        ClearMBusStatusOnAllChannelsRequestDto.class);
    CLASS_MAP.put(TestAlarmSchedulerRequestData.class, TestAlarmSchedulerRequestDto.class);
  }

  @Autowired
  @Qualifier("configurationMapper")
  private ConfigurationMapper configurationMapper;

  @Autowired private ManagementMapper managementMapper;

  @Autowired private MonitoringMapper monitoringMapper;

  @Autowired private CommonMapper commonMapper;

  @Autowired private PeriodicReadsRequestGasDataConverter periodicReadsRequestGasDataConverter;

  @Autowired
  private ActualMeterReadsRequestGasRequestDataConverter actualReadsRequestGasDataConverter;

  @Autowired private SetKeyOnGMeterDataConverter setKeyOnGMeterDataConverter;

  @Autowired
  private GetFirmwareVersionGasRequestDataConverter getFirmwareVersionGasRequestDataConverter;

  @Autowired private DomainHelperService domainHelperService;

  private final FaultResponseFactory faultResponseFactory = new FaultResponseFactory();

  /** Specifies which mapper to use for the core class received. */
  @PostConstruct
  private void postConstruct() {

    CUSTOM_CONVERTER_FOR_CLASS.put(
        PeriodicMeterReadsGasRequestData.class, this.periodicReadsRequestGasDataConverter);
    CUSTOM_CONVERTER_FOR_CLASS.put(
        ActualMeterReadsGasRequestData.class, this.actualReadsRequestGasDataConverter);
    CUSTOM_CONVERTER_FOR_CLASS.put(
        SetKeyOnGMeterRequestData.class, this.setKeyOnGMeterDataConverter);
    CUSTOM_CONVERTER_FOR_CLASS.put(
        GetFirmwareVersionGasRequestData.class, this.getFirmwareVersionGasRequestDataConverter);

    CLASS_TO_MAPPER_MAP.put(PeriodicMeterReadsRequestData.class, this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(ActualMeterReadsRequestData.class, this.commonMapper);
    CLASS_TO_MAPPER_MAP.put(SpecialDaysRequestData.class, this.commonMapper);
    CLASS_TO_MAPPER_MAP.put(ReadAlarmRegisterData.class, this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(FindEventsRequestData.class, this.managementMapper);
    CLASS_TO_MAPPER_MAP.put(GetAdministrativeStatusData.class, this.commonMapper);
    CLASS_TO_MAPPER_MAP.put(AdministrativeStatusTypeData.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(ActivityCalendarData.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SetConfigurationObjectRequestData.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SetAlarmNotificationsRequestData.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SetPushSetupAlarmRequestData.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SetPushSetupLastGaspRequestData.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SetPushSetupSmsRequestData.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SetPushSetupUdpRequestData.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SynchronizeTimeRequestData.class, this.commonMapper);
    CLASS_TO_MAPPER_MAP.put(GetAllAttributeValuesRequestData.class, this.commonMapper);
    CLASS_TO_MAPPER_MAP.put(GetFirmwareVersionRequestData.class, this.commonMapper);
    CLASS_TO_MAPPER_MAP.put(UpdateFirmwareRequestData.class, this.commonMapper);
    CLASS_TO_MAPPER_MAP.put(SetKeysRequestData.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SpecificAttributeValueRequestData.class, this.commonMapper);
    CLASS_TO_MAPPER_MAP.put(GetAssociationLnObjectsRequestData.class, this.commonMapper);
    CLASS_TO_MAPPER_MAP.put(SetClockConfigurationRequestData.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(GetThdFingerprintRequestData.class, this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(GetConfigurationObjectRequestData.class, this.commonMapper);
    CLASS_TO_MAPPER_MAP.put(GetPowerQualityProfileRequestData.class, this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(GenerateAndReplaceKeysRequestData.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(DefinableLoadProfileConfigurationData.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(SetMbusUserKeyByChannelRequestData.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(CoupleMbusDeviceByChannelRequestData.class, this.commonMapper);
    CLASS_TO_MAPPER_MAP.put(DecoupleMbusDeviceByChannelRequestData.class, this.commonMapper);
    CLASS_TO_MAPPER_MAP.put(GetMbusEncryptionKeyStatusRequestData.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(ClearAlarmRegisterData.class, this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(
        GetMbusEncryptionKeyStatusByChannelRequestData.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(
        SetDeviceLifecycleStatusByChannelRequestData.class, this.managementMapper);
    CLASS_TO_MAPPER_MAP.put(ScanMbusChannelsRequestData.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(GetOutagesRequestData.class, this.managementMapper);
    CLASS_TO_MAPPER_MAP.put(ActualPowerQualityRequest.class, this.monitoringMapper);
    CLASS_TO_MAPPER_MAP.put(SetRandomisationSettingsRequestData.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(GetGsmDiagnosticRequestData.class, this.managementMapper);
    CLASS_TO_MAPPER_MAP.put(GetKeysRequestData.class, this.configurationMapper);
    CLASS_TO_MAPPER_MAP.put(ClearMBusStatusOnAllChannelsRequestData.class, this.managementMapper);
  }

  public BundleMessagesRequestDto mapAllActions(
      final BundleMessageRequest bundleMessageRequest, final SmartMeter smartMeter) {

    final List<ActionDto> actionValueObjectDtoList = new ArrayList<>();

    for (final ActionRequest action : bundleMessageRequest.getBundleList()) {
      try {
        actionValueObjectDtoList.add(this.mapActionWithMapper(smartMeter, action));
      } catch (final FunctionalException functionalException) {
        log.warn(
            "FunctionalException occurred: " + this.getMessage(functionalException),
            functionalException);
        final ActionDto actionDto = new ActionDto(null);

        final List<FaultResponseParameterDto> parameterList = new ArrayList<>();
        final FaultResponseParameterDto deviceIdentificationParameter =
            new FaultResponseParameterDto(
                "deviceIdentification", smartMeter.getDeviceIdentification());
        parameterList.add(deviceIdentificationParameter);

        final FaultResponseDto faultResponseDto =
            this.faultResponseFactory.nonRetryablefaultResponseForException(
                functionalException, parameterList, "Exception while handling request");
        actionDto.setResponse(faultResponseDto);
        actionValueObjectDtoList.add(actionDto);
      }
    }
    return new BundleMessagesRequestDto(actionValueObjectDtoList);
  }

  private String getMessage(final FunctionalException functionalException) {
    if (functionalException.getCause() != null) {
      return functionalException.getCause().getMessage();
    }
    return functionalException.getMessage();
  }

  private ActionDto mapActionWithMapper(final SmartMeter smartMeter, final ActionRequest action)
      throws FunctionalException {
    @SuppressWarnings("unchecked")
    // TODO: fix this
    final CustomValueToDtoConverter<ActionRequest, ActionRequestDto> customValueToDtoConverter =
        (CustomValueToDtoConverter<ActionRequest, ActionRequestDto>)
            CUSTOM_CONVERTER_FOR_CLASS.get(action.getClass());

    if (customValueToDtoConverter != null) {
      return new ActionDto(customValueToDtoConverter.convert(action, smartMeter));
    } else {
      return this.mapActionWithoutConverter(smartMeter, action);
    }
  }

  private ActionDto mapActionWithoutConverter(
      final SmartMeter smartMeter, final ActionRequest action) throws FunctionalException {
    final Class<? extends ActionRequestDto> clazz = CLASS_MAP.get(action.getClass());
    final ConfigurableMapper mapper = CLASS_TO_MAPPER_MAP.get(action.getClass());
    if (mapper != null) {
      return this.mapActionWithMapper(smartMeter, action, clazz, mapper);
    } else {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.DOMAIN_SMART_METERING,
          new AssertionError(String.format("No mapper defined for class: %s", clazz.getName())));
    }
  }

  private ActionDto mapActionWithMapper(
      final SmartMeter smartMeter,
      final ActionRequest action,
      final Class<? extends ActionRequestDto> clazz,
      final ConfigurableMapper mapper)
      throws FunctionalException {
    if (action instanceof MbusActionRequest) {
      this.verifyAndFindChannelForMbusRequest((MbusActionRequest) action, smartMeter);
    }
    return new ActionDto(this.performDefaultMapping(action, mapper, clazz));
  }

  private ActionRequestDto performDefaultMapping(
      final ActionRequest action,
      final ConfigurableMapper mapper,
      final Class<? extends ActionRequestDto> clazz)
      throws FunctionalException {
    final ActionRequestDto actionValueObjectDto = mapper.map(action, clazz);

    if (actionValueObjectDto == null) {
      throw new FunctionalException(
          FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
          ComponentType.DOMAIN_SMART_METERING,
          new RuntimeException(
              String.format(
                  "Object: %s could not be converted to %s",
                  action.getClass().getName(), clazz.getName())));
    }
    return actionValueObjectDto;
  }

  private void verifyAndFindChannelForMbusRequest(
      final MbusActionRequest action, final SmartMeter smartMeter) throws FunctionalException {

    final SmartMeter mbusDevice =
        this.domainHelperService.findSmartMeter(action.getMbusDeviceIdentification());

    this.verifyMbusDeviceHasChannel(mbusDevice);
    this.verifyMbusDeviceHasGatewayDevice(mbusDevice);
    this.verifyMbusDeviceHasCorrectGatewayDevice(mbusDevice, smartMeter);

    action.setChannel(mbusDevice.getChannel());
  }

  private void verifyMbusDeviceHasChannel(final SmartMeter mbusDevice) throws FunctionalException {
    if (mbusDevice.getChannel() == null) {
      throw new FunctionalException(
          FunctionalExceptionType.NO_MBUS_DEVICE_CHANNEL_FOUND,
          ComponentType.DOMAIN_SMART_METERING,
          new MbusChannelNotFoundException("M-Bus device should have a channel configured."));
    }
  }

  private void verifyMbusDeviceHasGatewayDevice(final SmartMeter mbusDevice)
      throws FunctionalException {
    if (mbusDevice.getGatewayDevice() == null) {
      throw new FunctionalException(
          FunctionalExceptionType.GATEWAY_DEVICE_NOT_SET_FOR_MBUS_DEVICE,
          ComponentType.DOMAIN_SMART_METERING,
          new GatewayDeviceNotSetForMbusDeviceException());
    }
  }

  private void verifyMbusDeviceHasCorrectGatewayDevice(
      final SmartMeter mbusDevice, final SmartMeter smartMeter) throws FunctionalException {
    if (!smartMeter
        .getDeviceIdentification()
        .equals(mbusDevice.getGatewayDevice().getDeviceIdentification())) {
      throw new FunctionalException(
          FunctionalExceptionType.GATEWAY_DEVICE_INVALID_FOR_MBUS_DEVICE,
          ComponentType.DOMAIN_SMART_METERING,
          new GatewayDeviceInvalidForMbusDeviceException());
    }
  }
}
