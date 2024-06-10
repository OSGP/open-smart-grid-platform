// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFactory;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.repositories.DeviceAuthorizationRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceModelRepository;
import org.opensmartgridplatform.domain.core.repositories.ManufacturerRepository;
import org.opensmartgridplatform.domain.core.repositories.OrganisationRepository;
import org.opensmartgridplatform.domain.core.repositories.ProtocolInfoRepository;
import org.opensmartgridplatform.domain.core.repositories.SmartMeterRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AddSmartMeterRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service(value = "domainSmartMeteringSmartMeterService")
@Transactional(value = "transactionManager")
public class SmartMeterService {

  @Autowired private SmartMeterRepository smartMeterRepository;
  @Autowired private ManufacturerRepository manufacturerRepository;

  @Autowired private DeviceModelRepository deviceModelRepository;

  @Autowired private ProtocolInfoRepository protocolInfoRepository;

  @Autowired private OrganisationRepository organisationRepository;

  @Autowired private DeviceAuthorizationRepository deviceAuthorizationRepository;

  @Autowired private MapperFactory mapperFactory;

  public void storeMeter(
      final String organisationIdentification,
      final AddSmartMeterRequest addSmartMeterRequest,
      SmartMeter smartMeter)
      throws FunctionalException {
    final SmartMeteringDevice smartMeteringDevice = addSmartMeterRequest.getDevice();
    smartMeter.updateProtocol(this.getProtocolInfo(smartMeteringDevice));
    smartMeter.setDeviceModel(this.getDeviceModel(addSmartMeterRequest.getDeviceModel()));
    smartMeter = this.smartMeterRepository.save(smartMeter);
    this.storeAuthorization(organisationIdentification, smartMeter);
  }

  public void updateMeter(
      final AddSmartMeterRequest addSmartMeterRequest, final SmartMeter smartMeter)
      throws FunctionalException {
    final SmartMeteringDevice smartMeteringDevice = addSmartMeterRequest.getDevice();
    smartMeter.updateProtocol(this.getProtocolInfo(smartMeteringDevice));
    smartMeter.setDeviceModel(this.getDeviceModel(addSmartMeterRequest.getDeviceModel()));

    log.info(
        "UPDATE SmartMeter !! start updating smart meter with device identification: {}",
        smartMeteringDevice.getDeviceIdentification());
    this.smartMeterRepository.updateSmartMeter(
        smartMeter.getId(),
        smartMeter.getSupplier(),
        smartMeter.getChannel(),
        smartMeter.getMbusIdentificationNumber(),
        smartMeter.getMbusManufacturerIdentification(),
        smartMeter.getMbusVersion(),
        smartMeter.getMbusDeviceTypeIdentification(),
        smartMeter.getMbusPrimaryAddress());
  }

  public void removeMeter(final MessageMetadata messageMetadata) {

    final SmartMeter device =
        this.smartMeterRepository.findByDeviceIdentification(
            messageMetadata.getDeviceIdentification());

    this.deviceAuthorizationRepository.deleteAll(device.getAuthorizations());
    this.smartMeterRepository.delete(device);
  }

  public Optional<SmartMeter> validateSmartMeterDoesNotExist(
      final String deviceIdentification, final boolean overwrite) throws FunctionalException {
    final Optional<SmartMeter> existingSmartMeter =
        Optional.ofNullable(
            this.smartMeterRepository.findByDeviceIdentification(deviceIdentification));
    if (existingSmartMeter.isPresent() && !overwrite) {
      throw new FunctionalException(
          FunctionalExceptionType.EXISTING_DEVICE, ComponentType.DOMAIN_SMART_METERING);
    }
    return existingSmartMeter;
  }

  public SmartMeter convertSmartMeter(final SmartMeteringDevice smartMeteringDevice) {
    return this.mapperFactory.getMapperFacade().map(smartMeteringDevice, SmartMeter.class);
  }

  public SmartMeter convertToExistingSmartMeter(
      final SmartMeter newSmartMeter, final SmartMeter existingSmartMeter) {
    existingSmartMeter.setChannel(newSmartMeter.getChannel());
    existingSmartMeter.setDeviceType(newSmartMeter.getDeviceType());
    existingSmartMeter.setSupplier(newSmartMeter.getSupplier());
    existingSmartMeter.setMbusIdentificationNumber(newSmartMeter.getMbusIdentificationNumber());
    existingSmartMeter.setMbusDeviceTypeIdentification(
        newSmartMeter.getMbusDeviceTypeIdentification());
    existingSmartMeter.setMbusManufacturerIdentification(
        newSmartMeter.getMbusManufacturerIdentification());
    existingSmartMeter.setMbusVersion(newSmartMeter.getMbusVersion());
    existingSmartMeter.setMbusPrimaryAddress(newSmartMeter.getMbusPrimaryAddress());
    existingSmartMeter.setActivated(newSmartMeter.isActivated());
    existingSmartMeter.setAlias(newSmartMeter.getAlias());
    existingSmartMeter.setBtsId(newSmartMeter.getBtsId());
    existingSmartMeter.setCellId(newSmartMeter.getCellId());
    existingSmartMeter.setDeviceLifecycleStatus(newSmartMeter.getDeviceLifecycleStatus());
    existingSmartMeter.setVersion(newSmartMeter.getVersion());
    existingSmartMeter.setTechnicalInstallationDate(newSmartMeter.getTechnicalInstallationDate());
    existingSmartMeter.setNetworkAddress(newSmartMeter.getNetworkAddress());
    existingSmartMeter.setLastSuccessfulConnectionTimestamp(
        newSmartMeter.getLastSuccessfulConnectionTimestamp());

    return existingSmartMeter;
  }

  private ProtocolInfo getProtocolInfo(final SmartMeteringDevice smartMeteringDevice)
      throws FunctionalException {

    ProtocolInfo protocolInfo =
        this.protocolInfoRepository.findByProtocolAndProtocolVersionAndProtocolVariant(
            smartMeteringDevice.getProtocolName(),
            smartMeteringDevice.getProtocolVersion(),
            smartMeteringDevice.getProtocolVariant());

    if (protocolInfo == null) {
      protocolInfo =
          this.protocolInfoRepository.findByProtocolAndProtocolVersionAndProtocolVariant(
              smartMeteringDevice.getProtocolName(),
              smartMeteringDevice.getProtocolVersion(),
              null);
    }
    if (protocolInfo == null) {
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_PROTOCOL_NAME_OR_VERSION_OR_VARIANT,
          ComponentType.DOMAIN_SMART_METERING);
    }

    return protocolInfo;
  }

  private DeviceModel getDeviceModel(
      final org.opensmartgridplatform.domain.core.valueobjects.DeviceModel deviceModel) {
    final Manufacturer manufacturer =
        this.manufacturerRepository.findByCode(deviceModel.getManufacturer());
    return this.deviceModelRepository.findByManufacturerAndModelCode(
        manufacturer, deviceModel.getModelCode());
  }

  private void storeAuthorization(
      final String organisationIdentification, final SmartMeter smartMeter) {
    final Organisation organisation =
        this.organisationRepository.findByOrganisationIdentification(organisationIdentification);
    final DeviceAuthorization authorization =
        smartMeter.addAuthorization(organisation, DeviceFunctionGroup.OWNER);
    this.deviceAuthorizationRepository.save(authorization);
  }
}
