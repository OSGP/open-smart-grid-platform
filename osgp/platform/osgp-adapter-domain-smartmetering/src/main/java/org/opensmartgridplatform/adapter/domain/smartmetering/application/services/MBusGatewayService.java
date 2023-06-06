// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.JmsMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.exceptions.InactiveDeviceException;
import org.opensmartgridplatform.domain.core.repositories.SmartMeterRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CoupleMbusDeviceByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CoupleMbusDeviceRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceAdministrativeRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceByChannelRequestData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.DecoupleMbusDeviceRequestData;
import org.opensmartgridplatform.domain.smartmetering.exceptions.MbusChannelNotFoundException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DecoupleMbusDeviceDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DecoupleMbusDeviceResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelElementsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelElementsResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service(value = "domainSmartMeteringMBusGatewayService")
@Transactional(value = "transactionManager")
public class MBusGatewayService {

  private static final int MAXIMUM_NUMBER_OF_MBUS_CHANNELS = 4;

  @Autowired
  @Qualifier(value = "domainSmartMeteringOutboundOsgpCoreRequestsMessageSender")
  private JmsMessageSender osgpCoreRequestMessageSender;

  @Autowired private SmartMeterRepository smartMeteringDeviceRepository;

  @Autowired private DomainHelperService domainHelperService;

  @Autowired private InstallationService installationService;

  public MBusGatewayService() {
    // Parameterless constructor required for transactions...
  }

  /**
   * @param messageMetadata the metadata of the message, including the correlationUid, the
   *     deviceIdentification and the organization
   * @param requestData the requestData of the message, including the identification of the m-bus
   *     device and the channel
   */
  public void coupleMbusDevice(
      final MessageMetadata messageMetadata, final CoupleMbusDeviceRequestData requestData)
      throws FunctionalException {

    final String deviceIdentification = messageMetadata.getDeviceIdentification();
    final String mbusDeviceIdentification = requestData.getMbusDeviceIdentification();

    log.debug(
        "coupleMbusDevice for organizationIdentification: {} for gateway: {}, m-bus device {} ",
        messageMetadata.getOrganisationIdentification(),
        deviceIdentification,
        mbusDeviceIdentification);

    final SmartMeter gatewayDevice = this.domainHelperService.findSmartMeter(deviceIdentification);
    final SmartMeter mbusDevice = this.domainHelperService.findSmartMeter(mbusDeviceIdentification);

    this.checkAndHandleIfMbusDeviceIsInUse(mbusDevice);
    if (!requestData.isForce()) {
      this.checkAndHandleIfGivenMBusAlreadyCoupled(mbusDevice);
    }

    this.checkAndHandleIfAllMBusChannelsAreAlreadyOccupied(gatewayDevice);
    final MbusChannelElementsDto requestDto = this.makeMbusChannelElementsDto(mbusDevice);

    this.osgpCoreRequestMessageSender.send(
        requestDto,
        messageMetadata
            .builder()
            .withIpAddress(gatewayDevice.getIpAddress())
            .withNetworkSegmentIds(gatewayDevice.getBtsId(), gatewayDevice.getCellId())
            .build());
  }

  public void decoupleMbusDevice(
      final MessageMetadata messageMetadata, final DecoupleMbusDeviceRequestData requestData)
      throws FunctionalException {

    final String deviceIdentification = messageMetadata.getDeviceIdentification();
    final String mbusDeviceIdentification = requestData.getMbusDeviceIdentification();

    log.debug(
        "decoupleMbusDevice for organizationIdentification: {} for gateway: {}, m-bus device {} ",
        messageMetadata.getOrganisationIdentification(),
        deviceIdentification,
        mbusDeviceIdentification);

    final SmartMeter gatewayDevice = this.domainHelperService.findSmartMeter(deviceIdentification);
    final SmartMeter mbusDevice = this.domainHelperService.findSmartMeter(mbusDeviceIdentification);

    this.checkAndHandleInactiveMbusDevice(mbusDevice);

    // If Mbus device is already decoupled, return response OK, otherwise,
    // decouple it.
    if (!this.isMbusDeviceCoupled(mbusDevice)) {
      this.installationService.handleResponse(
          "decoupleMbusDevice", messageMetadata, ResponseMessageResultType.OK, null);
    } else {
      final DecoupleMbusDeviceDto requestDto = new DecoupleMbusDeviceDto(mbusDevice.getChannel());
      this.osgpCoreRequestMessageSender.send(
          requestDto,
          messageMetadata
              .builder()
              .withIpAddress(gatewayDevice.getIpAddress())
              .withNetworkSegmentIds(gatewayDevice.getBtsId(), gatewayDevice.getCellId())
              .build());
    }
  }

  public void decoupleMbusDeviceAdministrative(
      final MessageMetadata messageMetadata,
      final DecoupleMbusDeviceAdministrativeRequestData requestData)
      throws FunctionalException {

    final String mbusDeviceIdentification = requestData.getMbusDeviceIdentification();

    log.debug(
        "decoupleMbusDeviceAdministrative for organizationIdentification: {} m-bus device {} ",
        messageMetadata.getOrganisationIdentification(),
        mbusDeviceIdentification);

    final SmartMeter mbusDevice = this.domainHelperService.findSmartMeter(mbusDeviceIdentification);

    if (mbusDevice != null) {
      mbusDevice.setChannel(null);
      mbusDevice.setMbusPrimaryAddress(null);
      mbusDevice.updateGatewayDevice(null);
      this.smartMeteringDeviceRepository.saveAndFlush(mbusDevice);
    }
  }

  public void decoupleMbusDeviceByChannel(
      final MessageMetadata messageMetadata,
      final DecoupleMbusDeviceByChannelRequestData requestData)
      throws FunctionalException {

    final String deviceIdentification = messageMetadata.getDeviceIdentification();
    final SmartMeter gatewayDevice = this.domainHelperService.findSmartMeter(deviceIdentification);

    log.debug(
        "decoupleMbusDeviceByChannel for organizationIdentification: {} for gateway: {}, channel {} ",
        messageMetadata.getOrganisationIdentification(),
        deviceIdentification,
        requestData.getChannel());

    final DecoupleMbusDeviceDto requestDto = new DecoupleMbusDeviceDto(requestData.getChannel());
    this.osgpCoreRequestMessageSender.send(
        requestDto,
        messageMetadata
            .builder()
            .withIpAddress(gatewayDevice.getIpAddress())
            .withNetworkSegmentIds(gatewayDevice.getBtsId(), gatewayDevice.getCellId())
            .build());
  }

  private Optional<SmartMeter> findByMBusIdentificationNumber(
      final ChannelElementValuesDto channelElementValuesDto) {
    final SmartMeter mbusDevice =
        this.smartMeteringDeviceRepository.findByMBusIdentificationNumber(
            channelElementValuesDto.getIdentificationNumber(),
            channelElementValuesDto.getManufacturerIdentification());

    return Optional.ofNullable(mbusDevice);
  }

  private Optional<SmartMeter> findByGatewayDeviceAndChannel(
      final Device gatewayDevice, final Short channel) {
    final SmartMeter mbusDevice =
        this.smartMeteringDeviceRepository.findByGatewayDeviceAndChannel(gatewayDevice, channel);

    return Optional.ofNullable(mbusDevice);
  }

  private boolean isMbusDeviceCoupled(final SmartMeter mbusDevice) {
    return mbusDevice.getChannel() != null;
  }

  public void handleCoupleMbusDeviceResponse(
      final MessageMetadata messageMetadata,
      final MbusChannelElementsResponseDto mbusChannelElementsResponseDto)
      throws FunctionalException {

    final String deviceIdentification = messageMetadata.getDeviceIdentification();
    final SmartMeter gatewayDevice = this.domainHelperService.findSmartMeter(deviceIdentification);

    this.checkAndHandleIfChannelNotFound(mbusChannelElementsResponseDto);
    this.doCoupleMBusDevice(gatewayDevice, mbusChannelElementsResponseDto);
  }

  public void coupleMbusDeviceByChannel(
      final MessageMetadata messageMetadata, final CoupleMbusDeviceByChannelRequestData requestData)
      throws FunctionalException {

    final String deviceIdentification = messageMetadata.getDeviceIdentification();

    log.debug(
        "getMBusDeviceOnChannel for organizationIdentification: {} for gateway: {}",
        messageMetadata.getOrganisationIdentification(),
        deviceIdentification);

    final CoupleMbusDeviceByChannelRequestDataDto requestDto =
        new CoupleMbusDeviceByChannelRequestDataDto(requestData.getChannel());

    final SmartMeter gatewayDevice = this.domainHelperService.findSmartMeter(deviceIdentification);

    this.osgpCoreRequestMessageSender.send(
        requestDto,
        messageMetadata
            .builder()
            .withIpAddress(gatewayDevice.getIpAddress())
            .withNetworkSegmentIds(gatewayDevice.getBtsId(), gatewayDevice.getCellId())
            .build());
  }

  public void handleCoupleMbusDeviceByChannelResponse(
      final MessageMetadata messageMetadata,
      final CoupleMbusDeviceByChannelResponseDto coupleMbusDeviceByChannelResponseDto)
      throws FunctionalException {

    this.checkAndHandleIfNoChannelElementValuesFound(coupleMbusDeviceByChannelResponseDto);

    final SmartMeter gatewayDevice =
        this.domainHelperService.findSmartMeter(messageMetadata.getDeviceIdentification());
    final SmartMeter mbusDevice =
        this.smartMeteringDeviceRepository.findByMBusIdentificationNumber(
            coupleMbusDeviceByChannelResponseDto
                .getChannelElementValues()
                .getIdentificationNumber(),
            coupleMbusDeviceByChannelResponseDto
                .getChannelElementValues()
                .getManufacturerIdentification());

    this.checkAndHandleIfMbusDeviceNotFound(mbusDevice, coupleMbusDeviceByChannelResponseDto);
    this.checkAndHandleIfMbusDeviceIsInUse(mbusDevice, coupleMbusDeviceByChannelResponseDto);

    final short channel =
        coupleMbusDeviceByChannelResponseDto.getChannelElementValues().getChannel();
    mbusDevice.setChannel(channel);
    mbusDevice.setMbusPrimaryAddress(
        coupleMbusDeviceByChannelResponseDto.getChannelElementValues().getPrimaryAddress());
    mbusDevice.updateGatewayDevice(gatewayDevice);

    this.smartMeteringDeviceRepository.save(mbusDevice);
  }

  /**
   * Updates the M-Bus device identified in the input part of the {@code
   * mbusChannelElementsResponseDto} with respect to persisted attributes related to the coupling
   * with the given {@code gatewayDevice}.
   *
   * @param gatewayDevice
   * @param mbusChannelElementsResponseDto
   * @throws FunctionalException
   */
  private void doCoupleMBusDevice(
      final SmartMeter gatewayDevice,
      final MbusChannelElementsResponseDto mbusChannelElementsResponseDto)
      throws FunctionalException {

    final String mbusDeviceIdentification =
        mbusChannelElementsResponseDto.getMbusChannelElementsDto().getMbusDeviceIdentification();
    final SmartMeter mbusDevice = this.domainHelperService.findSmartMeter(mbusDeviceIdentification);

    /*
     * If the flow of handling the response gets to this point, the channel
     * has already been confirmed not be null, so the following should be
     * safe with regards to NullPointerExceptions.
     */
    final short channel = mbusChannelElementsResponseDto.getChannel();
    mbusDevice.setChannel(channel);
    mbusDevice.setMbusPrimaryAddress(
        this.getPrimaryAddress(mbusChannelElementsResponseDto, channel));

    mbusDevice.updateGatewayDevice(gatewayDevice);
    this.smartMeteringDeviceRepository.save(mbusDevice);
  }

  /**
   * Updates the M-Bus device identified in the input part of the {@code decoupleMbusResponseDto}.
   *
   * @param messageMetadata
   * @param decoupleMbusDeviceResponseDto
   * @throws FunctionalException
   */
  public void handleDecoupleMbusDeviceResponse(
      final MessageMetadata messageMetadata,
      final DecoupleMbusDeviceResponseDto decoupleMbusDeviceResponseDto)
      throws FunctionalException {
    final Optional<SmartMeter> mbusDeviceOnDeviceChannel =
        this.findByMBusIdentificationNumber(
            decoupleMbusDeviceResponseDto.getChannelElementValues());

    if (mbusDeviceOnDeviceChannel.isPresent()) {
      // Add the M-Bus device found on the meter to the response
      final SmartMeter mbusDevice = mbusDeviceOnDeviceChannel.get();
      decoupleMbusDeviceResponseDto.setMbusDeviceIdentification(
          mbusDevice.getDeviceIdentification());
    }

    // Decouple the M-Bus Device found on gateway device and the channel from the database
    final SmartMeter gatewayDevice =
        this.domainHelperService.findSmartMeter(messageMetadata.getDeviceIdentification());
    final Short channelInRequest =
        decoupleMbusDeviceResponseDto.getChannelElementValues().getChannel();

    final Optional<SmartMeter> optionalMbusDevice =
        this.findByGatewayDeviceAndChannel(gatewayDevice, channelInRequest);
    if (!optionalMbusDevice.isPresent()) {
      return;
    }

    final SmartMeter mbusDevice = optionalMbusDevice.get();
    if (mbusDevice.getGatewayDevice() != null) {
      mbusDevice.setChannel(null);
      mbusDevice.setMbusPrimaryAddress(null);
      mbusDevice.updateGatewayDevice(null);
      this.smartMeteringDeviceRepository.saveAndFlush(mbusDevice);
    }
  }

  private MbusChannelElementsDto makeMbusChannelElementsDto(final SmartMeter mbusDevice) {

    final String mbusDeviceIdentification = mbusDevice.getDeviceIdentification();
    final String mbusManufacturerIdentification = mbusDevice.getMbusManufacturerIdentification();
    final Short mbusVersion = mbusDevice.getMbusVersion();
    final Short mbusDeviceTypeIdentification = mbusDevice.getMbusDeviceTypeIdentification();
    Short primaryAddress = null;

    if (mbusDevice.getMbusPrimaryAddress() != null) {
      primaryAddress = mbusDevice.getMbusPrimaryAddress();
    }

    return new MbusChannelElementsDto(
        primaryAddress,
        mbusDeviceIdentification,
        mbusDevice.getMbusIdentificationNumber(),
        mbusManufacturerIdentification,
        mbusVersion,
        mbusDeviceTypeIdentification);
  }

  /**
   * This method checks if a channel was found on the gateway, and if not it will throw a
   * FunctionalException with the NO_MBUS_DEVICE_CHANNEL_FOUND type.
   */
  private void checkAndHandleIfChannelNotFound(
      final MbusChannelElementsResponseDto mbusChannelElementsResponseDto)
      throws FunctionalException {
    if (!mbusChannelElementsResponseDto.isChannelFound()) {
      throw new FunctionalException(
          FunctionalExceptionType.NO_MBUS_DEVICE_CHANNEL_FOUND,
          ComponentType.DOMAIN_SMART_METERING,
          new MbusChannelNotFoundException(
              String.valueOf(mbusChannelElementsResponseDto.getRetrievedChannelElements())));
    }
  }

  private void checkAndHandleIfMbusDeviceNotFound(
      final SmartMeter mbusDevice, final CoupleMbusDeviceByChannelResponseDto responseDto)
      throws FunctionalException {
    if (mbusDevice == null) {
      throw new FunctionalException(
          FunctionalExceptionType.NO_MATCHING_MBUS_DEVICE_FOUND,
          ComponentType.DOMAIN_SMART_METERING,
          new OsgpException(
              ComponentType.DOMAIN_SMART_METERING,
              "No matching mbus device found with mbusIdentificationNumber: "
                  + responseDto.getChannelElementValues().getIdentificationNumber()
                  + " and mbusManufacturerIdentification: "
                  + responseDto.getChannelElementValues().getManufacturerIdentification()));
    }
  }

  private void checkAndHandleIfMbusDeviceIsInUse(final SmartMeter mbusDevice)
      throws FunctionalException {
    if (DeviceLifecycleStatus.IN_USE == mbusDevice.getDeviceLifecycleStatus()) {
      throw new FunctionalException(
          FunctionalExceptionType.MBUS_DEVICE_NOT_MOVED_TO_ANOTHER_EMETER,
          ComponentType.DOMAIN_SMART_METERING,
          new OsgpException(
              ComponentType.DOMAIN_SMART_METERING,
              String.format(
                  "Mbus device %s not moved to another E meter",
                  mbusDevice.getDeviceIdentification())));
    }
  }

  private void checkAndHandleIfMbusDeviceIsInUse(
      final SmartMeter mbusDevice, final CoupleMbusDeviceByChannelResponseDto responseDto)
      throws FunctionalException {
    if (DeviceLifecycleStatus.IN_USE == mbusDevice.getDeviceLifecycleStatus()) {
      throw new FunctionalException(
          FunctionalExceptionType.MBUS_DEVICE_NOT_MOVED_TO_ANOTHER_EMETER,
          ComponentType.DOMAIN_SMART_METERING,
          new OsgpException(
              ComponentType.DOMAIN_SMART_METERING,
              String.format(
                  "Mbus device %s found on channel: %d with mbusIdentificationNumber: %s and mbusManufacturerIdentification: %s not moved to another E meter",
                  mbusDevice.getDeviceIdentification(),
                  responseDto.getChannelElementValues().getChannel(),
                  responseDto.getChannelElementValues().getIdentificationNumber(),
                  responseDto.getChannelElementValues().getManufacturerIdentification())));
    }
  }

  private void checkAndHandleIfNoChannelElementValuesFound(
      final CoupleMbusDeviceByChannelResponseDto responseDto) throws FunctionalException {

    final ChannelElementValuesDto values = responseDto.getChannelElementValues();

    if (!values.hasChannel()
        || !values.hasDeviceTypeIdentification()
        || !values.hasManufacturerIdentification()) {
      throw new FunctionalException(
          FunctionalExceptionType.NO_DEVICE_FOUND_ON_CHANNEL,
          ComponentType.DOMAIN_SMART_METERING,
          new OsgpException(
              ComponentType.DOMAIN_SMART_METERING,
              "No device was found on channel: " + values.getChannel()));
    }
  }

  /**
   * This method checks if the given mbusDevice is already coupled with a gateway. In that case it
   * will throw a FunctionalException.
   */
  private void checkAndHandleIfGivenMBusAlreadyCoupled(final SmartMeter mbusDevice)
      throws FunctionalException {
    if (mbusDevice.getGatewayDevice() != null) {
      log.info(
          "The given M-bus device {} is already coupled to gateway {} on channel {}",
          mbusDevice.getDeviceIdentification(),
          mbusDevice.getGatewayDevice().getDeviceIdentification(),
          mbusDevice.getChannel());

      throw new FunctionalException(
          FunctionalExceptionType.GIVEN_MBUS_DEVICE_ALREADY_COUPLED,
          ComponentType.DOMAIN_SMART_METERING,
          new OsgpException(
              ComponentType.DOMAIN_SMART_METERING,
              mbusDevice.getDeviceIdentification()
                  + " is already coupled to gateway "
                  + mbusDevice.getGatewayDevice().getDeviceIdentification()));
    }
  }

  private void checkAndHandleIfAllMBusChannelsAreAlreadyOccupied(final SmartMeter gatewayDevice)
      throws FunctionalException {
    final List<SmartMeter> mBusDevices =
        this.smartMeteringDeviceRepository.getMbusDevicesForGateway(gatewayDevice.getId());
    if (mBusDevices != null && mBusDevices.size() >= MAXIMUM_NUMBER_OF_MBUS_CHANNELS) {
      throw new FunctionalException(
          FunctionalExceptionType.ALL_MBUS_CHANNELS_OCCUPIED,
          ComponentType.DOMAIN_SMART_METERING,
          new OsgpException(
              ComponentType.DOMAIN_SMART_METERING,
              "All M-Bus channels are already occupied for gateway "
                  + gatewayDevice.getDeviceIdentification()));
    }
  }

  private void checkAndHandleInactiveMbusDevice(final SmartMeter mbusDevice)
      throws FunctionalException {
    if (!mbusDevice.getDeviceLifecycleStatus().equals(DeviceLifecycleStatus.IN_USE)) {
      log.info("The given M-bus device {} is inactive", mbusDevice.getDeviceIdentification());

      throw new FunctionalException(
          FunctionalExceptionType.INACTIVE_DEVICE,
          ComponentType.DOMAIN_SMART_METERING,
          new InactiveDeviceException(mbusDevice.getDeviceIdentification()));
    }
  }

  private Short getPrimaryAddress(
      final MbusChannelElementsResponseDto mbusChannelElementsResponseDto, final short channel) {
    // because the List is 0-based, it is needed to subtract 1 to get the
    // ChannelElements for the desired channel.
    return mbusChannelElementsResponseDto
        .getRetrievedChannelElements()
        .get(channel - 1)
        .getPrimaryAddress();
  }
}
