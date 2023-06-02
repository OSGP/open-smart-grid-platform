//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.CommonMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.InstallationMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ManagementMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActionResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AdministrativeStatusTypeResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmRegister;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AssociationLnObjectsResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.BundleMessagesResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CoupleMbusDeviceByChannelResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceByChannelResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventMessagesResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FaultResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FirmwareVersionGasResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.FirmwareVersionResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetAllAttributeValuesResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetConfigurationObjectResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetGsmDiagnosticResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetKeysResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetMbusEncryptionKeyStatusResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetOutagesResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetPowerQualityProfileResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.MeterReads;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.MeterReadsGas;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainer;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ScanMbusChannelsResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.UpdateFirmwareResponse;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualPowerQualityResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AdministrativeStatusTypeResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmRegisterResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AssociationLnObjectsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.BundleMessagesRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DecoupleMbusDeviceResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventMessageDataResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FaultResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FirmwareVersionGasResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.FirmwareVersionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetAllAttributeValuesResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetConfigurationObjectResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetGsmDiagnosticResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetKeysResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetOutagesResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MeterReadsGasResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MeterReadsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadGasResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ScanMbusChannelsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.UpdateFirmwareResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service(value = "domainSmartMeteringActionMapperResponseService")
@Validated
public class ActionMapperResponseService {

  @Autowired private ManagementMapper managementMapper;

  @Autowired private ConfigurationMapper configurationMapper;

  @Autowired private MonitoringMapper monitoringMapper;

  @Autowired private CommonMapper commonMapper;

  @Autowired private InstallationMapper installationMapper;

  private static final Map<Class<? extends ActionResponseDto>, ConfigurableMapper>
      classToMapperMap = new HashMap<>();
  private static final Map<Class<? extends ActionResponseDto>, Class<? extends ActionResponse>>
      classMap = new HashMap<>();

  /** Specifies to which core value object the DTO object needs to be mapped. */
  static {
    classMap.put(EventMessageDataResponseDto.class, EventMessagesResponse.class);
    classMap.put(MeterReadsResponseDto.class, MeterReads.class);
    classMap.put(MeterReadsGasResponseDto.class, MeterReadsGas.class);
    classMap.put(ActionResponseDto.class, ActionResponse.class);
    classMap.put(FaultResponseDto.class, FaultResponse.class);
    classMap.put(AlarmRegisterResponseDto.class, AlarmRegister.class);
    classMap.put(AdministrativeStatusTypeResponseDto.class, AdministrativeStatusTypeResponse.class);
    classMap.put(PeriodicMeterReadsResponseDto.class, PeriodicMeterReadsContainer.class);
    classMap.put(PeriodicMeterReadGasResponseDto.class, PeriodicMeterReadsContainerGas.class);
    classMap.put(GetAllAttributeValuesResponseDto.class, GetAllAttributeValuesResponse.class);
    classMap.put(FirmwareVersionResponseDto.class, FirmwareVersionResponse.class);
    classMap.put(FirmwareVersionGasResponseDto.class, FirmwareVersionGasResponse.class);
    classMap.put(UpdateFirmwareResponseDto.class, UpdateFirmwareResponse.class);
    classMap.put(AssociationLnObjectsResponseDto.class, AssociationLnObjectsResponseData.class);
    classMap.put(GetConfigurationObjectResponseDto.class, GetConfigurationObjectResponse.class);
    classMap.put(GetPowerQualityProfileResponseDto.class, GetPowerQualityProfileResponse.class);
    classMap.put(ActualPowerQualityResponseDto.class, ActualPowerQualityResponse.class);
    classMap.put(
        CoupleMbusDeviceByChannelResponseDto.class, CoupleMbusDeviceByChannelResponse.class);
    classMap.put(DecoupleMbusDeviceResponseDto.class, DecoupleMbusDeviceByChannelResponse.class);
    classMap.put(
        GetMbusEncryptionKeyStatusResponseDto.class, GetMbusEncryptionKeyStatusResponseData.class);
    classMap.put(
        GetMbusEncryptionKeyStatusByChannelResponseDto.class,
        GetMbusEncryptionKeyStatusByChannelResponseData.class);
    classMap.put(
        SetDeviceLifecycleStatusByChannelResponseDto.class,
        SetDeviceLifecycleStatusByChannelResponseData.class);
    classMap.put(ScanMbusChannelsResponseDto.class, ScanMbusChannelsResponseData.class);
    classMap.put(GetOutagesResponseDto.class, GetOutagesResponseData.class);
    classMap.put(GetGsmDiagnosticResponseDto.class, GetGsmDiagnosticResponseData.class);
    classMap.put(GetKeysResponseDto.class, GetKeysResponseData.class);
  }

  /** Specifies which mapper to use for the DTO class received. */
  @PostConstruct
  private void postConstruct() {
    classToMapperMap.put(EventMessageDataResponseDto.class, this.managementMapper);
    classToMapperMap.put(MeterReadsResponseDto.class, this.monitoringMapper);
    classToMapperMap.put(MeterReadsGasResponseDto.class, this.monitoringMapper);
    classToMapperMap.put(ActionResponseDto.class, this.commonMapper);
    classToMapperMap.put(FaultResponseDto.class, this.commonMapper);
    classToMapperMap.put(AlarmRegisterResponseDto.class, this.commonMapper);
    classToMapperMap.put(AdministrativeStatusTypeResponseDto.class, this.configurationMapper);
    classToMapperMap.put(PeriodicMeterReadsResponseDto.class, this.monitoringMapper);
    classToMapperMap.put(PeriodicMeterReadGasResponseDto.class, this.monitoringMapper);
    classToMapperMap.put(GetAllAttributeValuesResponseDto.class, this.configurationMapper);
    classToMapperMap.put(FirmwareVersionResponseDto.class, this.configurationMapper);
    classToMapperMap.put(FirmwareVersionGasResponseDto.class, this.configurationMapper);
    classToMapperMap.put(UpdateFirmwareResponseDto.class, this.configurationMapper);
    classToMapperMap.put(AssociationLnObjectsResponseDto.class, this.commonMapper);
    classToMapperMap.put(GetConfigurationObjectResponseDto.class, this.configurationMapper);
    classToMapperMap.put(GetPowerQualityProfileResponseDto.class, this.monitoringMapper);
    classToMapperMap.put(ActualPowerQualityResponseDto.class, this.monitoringMapper);
    classToMapperMap.put(CoupleMbusDeviceByChannelResponseDto.class, this.commonMapper);
    classToMapperMap.put(DecoupleMbusDeviceResponseDto.class, this.installationMapper);
    classToMapperMap.put(GetMbusEncryptionKeyStatusResponseDto.class, this.configurationMapper);
    classToMapperMap.put(
        GetMbusEncryptionKeyStatusByChannelResponseDto.class, this.configurationMapper);
    classToMapperMap.put(SetDeviceLifecycleStatusByChannelResponseDto.class, this.managementMapper);
    classToMapperMap.put(ScanMbusChannelsResponseDto.class, this.configurationMapper);
    classToMapperMap.put(GetOutagesResponseDto.class, this.managementMapper);
    classToMapperMap.put(GetGsmDiagnosticResponseDto.class, this.managementMapper);
    classToMapperMap.put(GetKeysResponseDto.class, this.configurationMapper);
  }

  public BundleMessagesResponse mapAllActions(
      final BundleMessagesRequestDto bundleMessageResponseDto) throws FunctionalException {

    final List<ActionResponse> actionResponseList = new ArrayList<>();

    for (final ActionResponseDto action : bundleMessageResponseDto.getAllResponses()) {

      final ConfigurableMapper mapper = this.getMapper(action);
      final Class<? extends ActionResponse> clazz = this.getClazz(action);

      // mapper is monitoring mapper
      final ActionResponse actionValueResponseObject = this.doMap(action, mapper, clazz);

      actionResponseList.add(actionValueResponseObject);
    }

    return new BundleMessagesResponse(actionResponseList);
  }

  private ActionResponse doMap(
      final ActionResponseDto action,
      final ConfigurableMapper mapper,
      final Class<? extends ActionResponse> clazz)
      throws FunctionalException {
    final ActionResponse actionValueResponseObject = mapper.map(action, clazz);

    if (actionValueResponseObject == null) {
      throw new FunctionalException(
          FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
          ComponentType.DOMAIN_SMART_METERING,
          new RuntimeException(
              "No Action Value Response Object for Action Value Response DTO Object of class: "
                  + action.getClass().getName()));
    }

    return actionValueResponseObject;
  }

  private Class<? extends ActionResponse> getClazz(final ActionResponseDto action)
      throws FunctionalException {
    final Class<? extends ActionResponse> clazz = classMap.get(action.getClass());

    if (clazz == null) {
      throw new FunctionalException(
          FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
          ComponentType.DOMAIN_SMART_METERING,
          new RuntimeException(
              "No Action Value Response Object class for Action Value Response DTO Object class: "
                  + action.getClass().getName()));
    }
    return clazz;
  }

  private ConfigurableMapper getMapper(final ActionResponseDto action) throws FunctionalException {
    final ConfigurableMapper mapper = classToMapperMap.get(action.getClass());

    if (mapper == null) {
      throw new FunctionalException(
          FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
          ComponentType.DOMAIN_SMART_METERING,
          new RuntimeException(
              "No mapper for Action Value Response DTO Object class: "
                  + action.getClass().getName()));
    }
    return mapper;
  }
}
