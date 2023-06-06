// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.application.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.repositories.DeviceAuthorizationRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceModelRepository;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.domain.core.repositories.ManufacturerRepository;
import org.opensmartgridplatform.domain.core.repositories.OrganisationRepository;
import org.opensmartgridplatform.domain.core.repositories.ProtocolInfoRepository;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;
import org.opensmartgridplatform.domain.core.valueobjects.AddRtuDeviceRequest;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.domain.core.valueobjects.RtuDevice;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "domainDistributionAutomationRtuDeviceService")
@Transactional(value = "transactionManager")
public class RtuDeviceService {

  @SuppressWarnings("squid:S1313")
  private static final String LOCAL_HOST = "127.0.0.1";

  @Autowired private RtuDeviceRepository rtuDeviceRepository;

  @Autowired private DomainInfoRepository domainInfoRepository;

  @Autowired private ManufacturerRepository manufacturerRepository;

  @Autowired private DeviceModelRepository deviceModelRepository;

  @Autowired private ProtocolInfoRepository protocolInfoRepository;

  @Autowired private OrganisationRepository organisationRepository;

  @Autowired private DeviceAuthorizationRepository deviceAuthorizationRepository;

  public void storeRtuDevice(
      final String organisationIdentification, final AddRtuDeviceRequest addRtuDeviceRequest)
      throws FunctionalException {
    this.throwExceptionOnExistingDevice(addRtuDeviceRequest);
    final RtuDevice rtuDevice = addRtuDeviceRequest.getRtuDevice();
    org.opensmartgridplatform.domain.core.entities.RtuDevice rtuDeviceEntity =
        new org.opensmartgridplatform.domain.core.entities.RtuDevice(
            rtuDevice.getDeviceIdentification());
    rtuDeviceEntity.setDomainInfo(
        this.domainInfoRepository.findByDomainAndDomainVersion("DISTRIBUTION_AUTOMATION", "1.0"));
    this.addProtocolInfo(rtuDevice, rtuDeviceEntity);
    this.addRegistrationData(rtuDevice, rtuDeviceEntity);
    this.addDeviceModel(addRtuDeviceRequest.getDeviceModel(), rtuDeviceEntity);
    rtuDeviceEntity = this.rtuDeviceRepository.save(rtuDeviceEntity);
    this.storeAuthorization(organisationIdentification, rtuDeviceEntity);
  }

  private void throwExceptionOnExistingDevice(final AddRtuDeviceRequest addRtuDeviceRequest)
      throws FunctionalException {

    final String newDeviceIdentification =
        addRtuDeviceRequest.getRtuDevice().getDeviceIdentification();
    if (this.rtuDeviceRepository.findByDeviceIdentification(newDeviceIdentification).isPresent()) {
      throw new FunctionalException(
          FunctionalExceptionType.EXISTING_DEVICE, ComponentType.DOMAIN_DISTRIBUTION_AUTOMATION);
    }
  }

  private void addRegistrationData(
      final RtuDevice rtuDevice,
      final org.opensmartgridplatform.domain.core.entities.RtuDevice rtuDeviceEntity)
      throws FunctionalException {
    final String networkAddress = rtuDevice.getNetworkAddress();
    final InetAddress inetAddress;
    try {
      inetAddress =
          LOCAL_HOST.equals(networkAddress)
              ? InetAddress.getLoopbackAddress()
              : InetAddress.getByName(networkAddress);
    } catch (final UnknownHostException e) {
      throw new FunctionalException(
          FunctionalExceptionType.INVALID_IP_ADDRESS,
          ComponentType.DOMAIN_DISTRIBUTION_AUTOMATION,
          e);
    }
    rtuDeviceEntity.updateRegistrationData(inetAddress, RtuDevice.PSD_TYPE);
  }

  private void addDeviceModel(
      final org.opensmartgridplatform.domain.core.valueobjects.DeviceModel deviceModel,
      final org.opensmartgridplatform.domain.core.entities.RtuDevice rtuDeviceEntity)
      throws FunctionalException {
    final Manufacturer manufacturer =
        this.manufacturerRepository.findByCode(deviceModel.getManufacturer());
    final DeviceModel deviceModelEntity =
        this.deviceModelRepository.findByManufacturerAndModelCode(
            manufacturer, deviceModel.getModelCode());
    if (deviceModelEntity == null) {
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_DEVICEMODEL,
          ComponentType.DOMAIN_DISTRIBUTION_AUTOMATION);
    }
    rtuDeviceEntity.setDeviceModel(deviceModelEntity);
  }

  private void addProtocolInfo(
      final RtuDevice rtuDevice,
      final org.opensmartgridplatform.domain.core.entities.RtuDevice rtuDeviceEntity)
      throws FunctionalException {
    final ProtocolInfo protocolInfo =
        this.protocolInfoRepository.findByProtocolAndProtocolVersion(
            rtuDevice.getProtocolName(), rtuDevice.getProtocolVersion());
    if (protocolInfo == null) {
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_PROTOCOL_NAME_OR_VERSION_OR_VARIANT,
          ComponentType.DOMAIN_DISTRIBUTION_AUTOMATION);
    }
    rtuDeviceEntity.updateProtocol(protocolInfo);
  }

  private void storeAuthorization(
      final String organisationIdentification,
      final org.opensmartgridplatform.domain.core.entities.RtuDevice rtuDevice) {
    final Organisation organisation =
        this.organisationRepository.findByOrganisationIdentification(organisationIdentification);
    final DeviceAuthorization authorization =
        rtuDevice.addAuthorization(organisation, DeviceFunctionGroup.OWNER);
    this.deviceAuthorizationRepository.save(authorization);
  }
}
