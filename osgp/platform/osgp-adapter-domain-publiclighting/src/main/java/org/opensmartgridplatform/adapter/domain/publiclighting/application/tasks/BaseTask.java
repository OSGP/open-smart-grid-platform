// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.publiclighting.application.tasks;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.OsgpSystemCorrelationUid;
import org.opensmartgridplatform.adapter.domain.publiclighting.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.repositories.DeviceModelRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.EventRepository;
import org.opensmartgridplatform.domain.core.repositories.LightMeasurementDeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.ManufacturerRepository;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.dto.valueobjects.DomainTypeDto;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

/** Base class for scheduled tasks. */
public class BaseTask {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseTask.class);

  private boolean firstRun = true;

  @Autowired
  @Qualifier("domainPublicLightingOutboundOsgpCoreRequestsMessageSender")
  protected OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

  @Autowired protected DeviceRepository deviceRepository;

  @Autowired protected RtuDeviceRepository rtuDeviceRepository;

  @Autowired protected LightMeasurementDeviceRepository lightMeasurementDeviceRepository;

  @Autowired protected ManufacturerRepository manufacturerRepository;

  @Autowired protected DeviceModelRepository deviceModelRepository;

  @Autowired protected EventRepository eventRepository;

  /** Try to find a manufacturer by name (case sensitive). */
  protected Manufacturer findManufacturer(final String name) {
    LOGGER.info("Trying to find manufacturer for name: {}", name);
    final Manufacturer manufacturer = this.manufacturerRepository.findByName(name);
    if (manufacturer == null) {
      LOGGER.warn("No manufacturer found for name: {}", name);
    } else {
      LOGGER.info("Manufacturer found for name: {}", name);
    }
    return manufacturer;
  }

  /** Try to find all device models for a manufacturer. */
  protected List<DeviceModel> findDeviceModels(final Manufacturer manufacturer) {
    LOGGER.info("Trying to find device models for manufacturer: {}", manufacturer.getName());
    final List<DeviceModel> deviceModels =
        this.deviceModelRepository.findByManufacturer(manufacturer);
    if (deviceModels == null) {
      LOGGER.warn(
          "No device models found for manufacturer with name: {}, deviceModels == null",
          manufacturer.getName());
    } else if (deviceModels.isEmpty()) {
      LOGGER.warn(
          "No device models found for manufacturer with name: {}, deviceModels.isEmpty()",
          manufacturer.getName());
    } else {
      LOGGER.info(
          "{} device models found for manufacturer with name: {}",
          deviceModels.size(),
          manufacturer.getName());
      for (final DeviceModel deviceModel : deviceModels) {
        LOGGER.info(" deviceModel: {}", deviceModel.getModelCode());
      }
    }
    return deviceModels;
  }

  /** Try to find all devices which are not 'in maintenance' for a list of device models. */
  protected List<Device> findDevices(
      final List<DeviceModel> deviceModels, final String deviceType) {
    LOGGER.info("Trying to find devices for device models for manufacturer...");
    final List<Device> devices = new ArrayList<>();
    for (final DeviceModel deviceModel : deviceModels) {

      final List<Device> devsInUse =
          this.deviceRepository
              .findByDeviceModelAndDeviceTypeAndInMaintenanceAndDeviceLifecycleStatus(
                  deviceModel, deviceType, false, DeviceLifecycleStatus.IN_USE);
      final List<Device> devsRegistered =
          this.deviceRepository
              .findByDeviceModelAndDeviceTypeAndInMaintenanceAndDeviceLifecycleStatus(
                  deviceModel, deviceType, false, DeviceLifecycleStatus.REGISTERED);
      devices.addAll(devsInUse);
      devices.addAll(devsRegistered);
    }
    if (devices.isEmpty()) {
      LOGGER.warn("No devices found for device models for manufacturer");
    } else {
      LOGGER.info("{} devices found for device models for manufacturer", devices.size());
      for (final Device device : devices) {
        LOGGER.info(" device: {}", device.getDeviceIdentification());
      }
    }
    return devices;
  }

  /**
   * Try to find all devices which are not 'in maintenance' that are communicated with using the
   * given protocol.
   */
  protected List<LightMeasurementDevice> findLightMeasurementDevicesByProtocol(
      final String protocol) {
    LOGGER.info("Trying to find connectable light measurement devices for protocol {}", protocol);

    final List<LightMeasurementDevice> lightMeasurementDevices =
        this.lightMeasurementDeviceRepository.findByProtocolInfoProtocolAndDeviceLifecycleStatus(
            protocol, DeviceLifecycleStatus.IN_USE);

    if (lightMeasurementDevices.isEmpty()) {
      LOGGER.warn("No connectable light measurement devices found for protocol {}", protocol);
    } else {
      final String identifications = this.deviceIdentifications(lightMeasurementDevices);
      LOGGER.info(
          "{} connectable light measurement device(s) found for protocol {}: {}",
          lightMeasurementDevices.size(),
          protocol,
          identifications);
    }
    return lightMeasurementDevices;
  }

  private String deviceIdentifications(final Collection<? extends Device> devices) {
    if (CollectionUtils.isEmpty(devices)) {
      return "";
    }
    return devices.stream()
        .map(Device::getDeviceIdentification)
        .sorted()
        .collect(Collectors.joining(", "));
  }

  private boolean isFirstRun() {
    final boolean result = this.firstRun;
    this.firstRun = false;
    return result;
  }

  /**
   * Filter a list of given devices to determine which devices should be contacted. The filtering
   * uses the age of the latest event in comparison with 'maximumAllowedAge'.
   */
  protected List<Device> findDevicesToContact(
      final List<Device> devices, final int maximumAllowedAge) {
    if (this.isFirstRun()) {
      // Contact all devices.
      LOGGER.info("This is the first run. Contact all devices, devices.size(): {}", devices.size());
      return devices;
    }

    final List<Object> listOfObjectArrays =
        this.eventRepository.findLatestEventForEveryDevice(devices);
    LOGGER.info("devicesWithEventsList.size(): {}", listOfObjectArrays.size());

    final Date maxAge =
        Date.from(ZonedDateTime.now(ZoneId.of("UTC")).minusHours(maximumAllowedAge).toInstant());
    LOGGER.info("maxAge: {}", maxAge);

    final Map<Long, Date> map = new HashMap<>();
    for (final Object objectArray : listOfObjectArrays) {
      final Object[] array = (Object[]) objectArray;
      final Long eventDeviceId = (Long) array[0];
      final Date timestamp = (Date) array[1];
      LOGGER.info("eventDeviceId: {}, timestamp: {}", eventDeviceId, timestamp);
      if (this.isEventOlderThanMaxInterval(maxAge, timestamp, maximumAllowedAge)) {
        map.put(eventDeviceId, timestamp);
      }
    }

    final List<Device> devicesToContact = this.deviceRepository.findByIdIn(map.keySet());
    LOGGER.info("devicesToContact.size(): {}", devicesToContact.size());
    devicesToContact.sort(
        (a, b) -> a.getDeviceIdentification().compareTo(b.getDeviceIdentification()));

    for (final Device device : devicesToContact) {
      LOGGER.info("device: {}, id: {}", device.getDeviceIdentification(), device.getId());
    }
    return devicesToContact;
  }

  /** Determine if an event is older than X hours as indicated by maxAge. */
  protected boolean isEventOlderThanMaxInterval(
      final Date maxAge, final Date event, final int maximumAllowedAge) {
    if (event == null) {
      // In case the event instance is null, try to contact the device.
      LOGGER.info("Event instance is null");
      return true;
    }
    final boolean result = event.before(maxAge);
    LOGGER.info(
        "event date time: {}, current date time minus {} hours: {}, is event before? : {}",
        event,
        maximumAllowedAge,
        maxAge,
        result);
    return result;
  }

  protected void contactDevices(
      final List<Device> devicesToContact, final DeviceFunction deviceFunction) {
    for (final Device device : devicesToContact) {
      this.sendRequestMessageToDevice(device, deviceFunction);
    }
  }

  protected void sendRequestMessageToDevice(
      final Device device, final DeviceFunction deviceFunction) {
    final String deviceIdentification = device.getDeviceIdentification();
    // Try to use the identification of the owner organization.
    final String organisation =
        device.getOwner() == null ? "" : device.getOwner().getOrganisationIdentification();
    // Creating message with OSGP System CorrelationUID. This way the
    // responses for scheduled tasks can be filtered out.
    final String correlationUid = OsgpSystemCorrelationUid.CORRELATION_UID;
    final String deviceFunctionString = deviceFunction.name();
    final DomainTypeDto domain = DomainTypeDto.PUBLIC_LIGHTING;

    final String networkAddress = device.getNetworkAddress();
    if (networkAddress == null) {
      // In case the device does not have a known IP address, don't send
      // a request message.
      LOGGER.warn(
          "Unable to create protocol request message because the network address is empty for device: {}",
          deviceIdentification);
      return;
    }

    final RequestMessage requestMessage =
        new RequestMessage(correlationUid, organisation, deviceIdentification, domain);

    this.osgpCoreRequestMessageSender.send(
        requestMessage,
        deviceFunctionString,
        MessagePriorityEnum.LOW.getPriority(),
        networkAddress);
  }

  /**
   * Filter a list of given light measurement devices to determine which devices should be
   * connected. The filtering uses the latest communication time in comparison with
   * 'maximumAllowedAge' relative to the current time.
   */
  protected List<LightMeasurementDevice> findLightMeasurementDevicesToConnect(
      final List<LightMeasurementDevice> lightMeasurementDevices, final int maximumAllowedAge) {

    if (CollectionUtils.isEmpty(lightMeasurementDevices)) {
      LOGGER.info("No light measurement devices to connect with");
      return lightMeasurementDevices;
    }

    if (this.isFirstRun()) {
      LOGGER.info(
          "This is the first run. Connect all light measurement devices: {}",
          lightMeasurementDevices.size());
      return lightMeasurementDevices;
    }

    final Instant maxAge = Instant.now().minus(maximumAllowedAge, ChronoUnit.HOURS);
    final long valueForMissingTime = 0;
    final long boundaryTime = getTime(maxAge, valueForMissingTime);

    final Predicate<LightMeasurementDevice> deviceFilter;
    if (boundaryTime < valueForMissingTime) {
      LOGGER.error(
          "Maximum allowed age earlier than {} is not allowed."
              + " Possibly an error in configuration of maximum allowed age in hours: {}.",
          new Date(valueForMissingTime),
          maximumAllowedAge);
      /*
       * Make sure all devices will be connected.
       */
      deviceFilter = lightMeasurementDevice -> true;
    } else {
      LOGGER.info(
          "Connect light measurement devices last communicated with no later than: {}", maxAge);
      deviceFilter = this.noCommunicationSince(boundaryTime, valueForMissingTime);
    }

    return lightMeasurementDevices.stream()
        .filter(deviceFilter)
        .sorted(Comparator.comparing(LightMeasurementDevice::getDeviceIdentification))
        .toList();
  }

  private static long getTime(final Date date, final long valueIfNull) {
    if (date == null) {
      return valueIfNull;
    }
    return date.getTime();
  }

  private static long getTime(final Instant instant, final long valueIfNull) {
    if (instant == null) {
      return valueIfNull;
    }
    return instant.toEpochMilli();
  }

  private Predicate<LightMeasurementDevice> noCommunicationSince(
      final long boundaryTime, final long valueForMissingTime) {

    return lightMeasurementDevice ->
        this.getLastCommunicationTime(lightMeasurementDevice, valueForMissingTime) < boundaryTime;
  }

  /**
   * Determines the last known communication time with the light measurement device, or its gateway.
   * The gateway connection time counts equally, since it is just as relevant with respect to the
   * connection or reconnecting with the light measurement device as the light measurement device
   * itself.
   *
   * @param lightMeasurementDevice
   * @param valueForMissingTime default value if no communication time is available
   * @return the last known communication time (as the number of milliseconds since January 1, 1970,
   *     00:00:00 GMT) with the given {@code lightMeasurementDevice} or its gateway, or {@code
   *     valueForMissingTime} if there is no known communication time.
   */
  private long getLastCommunicationTime(
      final LightMeasurementDevice lightMeasurementDevice, final long valueForMissingTime) {

    final long lastCommunicationTimeLightMeasurementDevice =
        Long.max(
            getTime(lightMeasurementDevice.getLastCommunicationTime(), valueForMissingTime),
            getTime(
                lightMeasurementDevice.getLastSuccessfulConnectionTimestamp(),
                valueForMissingTime));
    final long lastCommunicationTimeGateway =
        this.getGatewayLastCommunicationTime(lightMeasurementDevice, valueForMissingTime);
    return Long.max(lastCommunicationTimeLightMeasurementDevice, lastCommunicationTimeGateway);
  }

  private long getGatewayLastCommunicationTime(
      final LightMeasurementDevice lightMeasurementDevice, final long valueForMissingTime) {

    final Device gatewayDevice = lightMeasurementDevice.getGatewayDevice();
    if (gatewayDevice == null) {
      return valueForMissingTime;
    }

    final long lastSuccessFullConnectionTimestampGateway =
        getTime(gatewayDevice.getLastSuccessfulConnectionTimestamp(), valueForMissingTime);
    final long lastCommunicationTimeRtuGateway =
        this.rtuDeviceRepository
            .findById(gatewayDevice.getId())
            .map(rtu -> getTime(rtu.getLastCommunicationTime(), valueForMissingTime))
            .orElse(valueForMissingTime);
    return Long.max(lastSuccessFullConnectionTimestampGateway, lastCommunicationTimeRtuGateway);
  }

  protected void connectLightMeasurementDevices(
      final List<LightMeasurementDevice> devicesToConnect) {
    if (CollectionUtils.isEmpty(devicesToConnect)) {
      LOGGER.info("No light measurement devices to connect with");
      return;
    }

    final String deviceIdentifications = this.deviceIdentifications(devicesToConnect);
    LOGGER.info(
        "Sending requests to ensure active connections with the following light measurement devices: {}",
        deviceIdentifications);

    final Set<Long> connectedToGatewayIds = new HashSet<>();
    for (final LightMeasurementDevice device : devicesToConnect) {
      if (this.aDeviceWithTheSameGatewayHasNotBeenConnected(device, connectedToGatewayIds)) {
        final String withGateway =
            device.getGatewayDevice() == null
                ? ""
                : String.format(
                    " with gateway: %s", device.getGatewayDevice().getDeviceIdentification());
        LOGGER.info(
            "Send connect request to device {}{}", device.getDeviceIdentification(), withGateway);
        this.sendConnectRequestToDevice(device);
      } else {
        LOGGER.info(
            "Skipping device {} since the connection was already assured for its gateway ({})",
            device.getDeviceIdentification(),
            device.getGatewayDevice().getDeviceIdentification());
      }
    }
  }

  private boolean aDeviceWithTheSameGatewayHasNotBeenConnected(
      final LightMeasurementDevice device, final Set<Long> connectedToGatewayIds) {

    return device.getGatewayDevice() == null
        || connectedToGatewayIds.add(device.getGatewayDevice().getId());
  }

  protected void sendConnectRequestToDevice(final LightMeasurementDevice lightMeasurementDevice) {
    final String deviceIdentification = getDeviceIdentification(lightMeasurementDevice);
    final String organisation = getOwnerOrganisation(lightMeasurementDevice);
    // Creating message with OSGP System CorrelationUID. This way the
    // responses for scheduled tasks can be filtered out.
    final String correlationUid = OsgpSystemCorrelationUid.CORRELATION_UID;
    final String messageType = DeviceFunction.CONNECT.name();
    final DomainTypeDto domain = DomainTypeDto.PUBLIC_LIGHTING;

    final String networkAddress = getNetworkAddress(lightMeasurementDevice);
    if (networkAddress == null) {
      LOGGER.warn(
          "Unable to create connect request because no network address is known for device: {}",
          deviceIdentification);
      return;
    }

    final RequestMessage requestMessage =
        new RequestMessage(correlationUid, organisation, deviceIdentification, domain);
    this.osgpCoreRequestMessageSender.send(
        requestMessage, messageType, MessagePriorityEnum.LOW.getPriority(), networkAddress);
  }

  private static String getDeviceIdentification(final LightMeasurementDevice device) {
    if (device.getGatewayDevice() != null) {
      return device.getGatewayDevice().getDeviceIdentification();
    } else {
      return device.getDeviceIdentification();
    }
  }

  private static String getOwnerOrganisation(final LightMeasurementDevice device) {
    if (device.getOwner() == null) {
      return "";
    } else {
      return device.getOwner().getOrganisationIdentification();
    }
  }

  private static String getNetworkAddress(final Device lightMeasurementDevice) {
    final String gatewayNetworkAddress = getGatewayNetworkAddress(lightMeasurementDevice);
    if (gatewayNetworkAddress != null) {
      return gatewayNetworkAddress;
    }
    return lightMeasurementDevice.getNetworkAddress();
  }

  private static String getGatewayNetworkAddress(final Device lightMeasurementDevice) {
    if (lightMeasurementDevice.getGatewayDevice() == null) {
      return null;
    }
    return lightMeasurementDevice.getGatewayDevice().getNetworkAddress();
  }
}
