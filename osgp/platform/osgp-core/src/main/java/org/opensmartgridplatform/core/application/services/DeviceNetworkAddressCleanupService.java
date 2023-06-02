//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.core.application.services;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class DeviceNetworkAddressCleanupService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DeviceNetworkAddressCleanupService.class);

  private static final InetAddress LOCALHOST = getLocalHost();

  private final boolean allowMultipleDevicesPerNetworkAddress;
  private final List<String> ipRangesAllowingMultipleDevicesPerAddress;
  private final Predicate<InetAddress> duplicatesAllowedByConfiguration;
  private final DeviceRepository deviceRepository;

  @Autowired
  public DeviceNetworkAddressCleanupService(
      final DeviceRepository deviceRepository,
      @Value("${device.network.address.cleanup.never}")
          final boolean allowMultipleDevicesPerNetworkAddress,
      @Value("#{'${device.network.address.cleanup.duplicates.allowed}'.split(',')}")
          final List<String> ipRangesAllowingMultipleDevicesPerAddress) {
    this.deviceRepository = deviceRepository;
    this.allowMultipleDevicesPerNetworkAddress = allowMultipleDevicesPerNetworkAddress;

    if (ipRangesAllowingMultipleDevicesPerAddress == null) {
      this.ipRangesAllowingMultipleDevicesPerAddress = Collections.emptyList();
    } else {
      this.ipRangesAllowingMultipleDevicesPerAddress =
          Collections.unmodifiableList(
              ipRangesAllowingMultipleDevicesPerAddress.stream()
                  .filter(StringUtils::isNotBlank)
                  .collect(Collectors.toList()));
    }
    this.duplicatesAllowedByConfiguration =
        this.duplicatesAllowed(this.ipRangesAllowingMultipleDevicesPerAddress);

    if (this.allowMultipleDevicesPerNetworkAddress) {
      LOGGER.info(
          "DeviceNetworkAddressCleanupService initialized, network addresses will never be cleaned, as device.network.address.cleanup.never=true");
    } else if (this.ipRangesAllowingMultipleDevicesPerAddress.isEmpty()) {
      LOGGER.info(
          "DeviceNetworkAddressCleanupService initialized, duplicated network addresses will be cleaned, except for 127.0.0.1");
    } else {
      LOGGER.info(
          "DeviceNetworkAddressCleanupService initialized, duplicated network addresses will be cleaned, except for 127.0.0.1, or addresses configured as device.network.address.cleanup.duplicates.allowed: {}",
          this.ipRangesAllowingMultipleDevicesPerAddress);
    }
  }

  public static InetAddress getLocalHost() {
    final byte[] ipv4Localhost = new byte[] {127, 0, 0, 1};
    try {
      return InetAddress.getByAddress("localhost", ipv4Localhost);
    } catch (final UnknownHostException e) {
      throw new AssertionError(
          "Should not happen as IP address has a valid value  ("
              + Arrays.toString(ipv4Localhost)
              + ") and length ("
              + ipv4Localhost.length
              + ")",
          e);
    }
  }

  private Predicate<InetAddress> duplicatesAllowed(final List<String> configuredRanges) {
    if (CollectionUtils.isEmpty(configuredRanges)) {
      return inetAddress -> false;
    }

    final List<Predicate<BigInteger>> rangePredicates =
        configuredRanges.stream().map(this::rangePredicate).collect(Collectors.toList());

    return inetAddress -> {
      if (inetAddress == null) {
        return true;
      }
      final BigInteger bigIntegerValue = this.asBigInteger(inetAddress);
      return rangePredicates.stream().anyMatch(p -> p.test(bigIntegerValue));
    };
  }

  private BigInteger asBigInteger(final InetAddress inetAddress) {
    if (inetAddress == null) {
      return null;
    }
    final byte[] address = inetAddress.getAddress();
    final byte[] addressWithLeadingZero = new byte[address.length + 1];
    System.arraycopy(address, 0, addressWithLeadingZero, 1, address.length);
    return new BigInteger(addressWithLeadingZero);
  }

  private InetAddress asInetAddress(final String host) {
    if (StringUtils.isBlank(host)) {
      throw new IllegalArgumentException("InetAddress must not be blank");
    }
    try {
      return InetAddress.getByName(host);
    } catch (final UnknownHostException e) {
      throw new IllegalArgumentException("Invalid InetAddress: \"" + host + "\"", e);
    }
  }

  private Predicate<BigInteger> rangePredicate(final String configuredRange) {
    if (!configuredRange.contains("-")) {
      final InetAddress configuredInetAddress = this.asInetAddress(configuredRange);
      final BigInteger bigIntegerValue = this.asBigInteger(configuredInetAddress);
      return bigInteger ->
          bigInteger != null
              && bigIntegerValue != null
              && bigInteger.compareTo(bigIntegerValue) == 0;
    }

    final String[] fromTo = this.fromTo(configuredRange);

    final InetAddress configuredInetAddressFrom = this.asInetAddress(fromTo[0]);
    final InetAddress configuredInetAddressTo = this.asInetAddress(fromTo[1]);

    final byte[] fromBytes = configuredInetAddressFrom.getAddress();
    final byte[] toBytes = configuredInetAddressTo.getAddress();
    if (fromBytes.length != toBytes.length) {
      throw new IllegalArgumentException(
          "Ivalid range configuration: \""
              + configuredRange
              + "\", from ("
              + fromBytes.length
              + ") and to ("
              + toBytes.length
              + ") have different lengths");
    }

    final BigInteger fromValue = this.asBigInteger(configuredInetAddressFrom);
    final BigInteger toValue = this.asBigInteger(configuredInetAddressTo);
    if (fromValue.compareTo(toValue) > 0) {
      throw new IllegalArgumentException(
          "Incorrect range configuration (from > to): \"" + configuredRange + "\"");
    }

    return bigInteger ->
        bigInteger != null
            && bigInteger.compareTo(fromValue) >= 0
            && bigInteger.compareTo(toValue) <= 0;
  }

  private String[] fromTo(final String configuredRange) {
    final String[] fromTo = configuredRange.split("\\s*+-\\s*+");
    if (fromTo.length != 2) {
      throw new IllegalArgumentException(
          "Incorrect range configuration: \""
              + configuredRange
              + "\", expected 2 parts, found: "
              + fromTo.length);
    }
    return fromTo;
  }

  /**
   * Make sure that no other device than the one identified by {@code deviceIdentification} has the
   * given {@code address} as network address, by removing the network address with other devices
   * where it is stored.
   *
   * @param deviceIdentification identification of the only device that is allowed to have the given
   *     {@code address} as its network address
   * @param host the network address that may not remain stored with other devices than the one
   *     identified by the given {@code deviceIdentification}
   * @throws UnknownHostException
   */
  public void clearDuplicateAddresses(final String deviceIdentification, final String host)
      throws UnknownHostException {

    this.clearDuplicateAddresses(deviceIdentification, InetAddress.getByName(host));
  }

  /**
   * Make sure that no other device than the one identified by {@code deviceIdentification} has the
   * given {@code address} as network address, by removing the network address with other devices
   * where it is stored.
   *
   * @param deviceIdentification identification of the only device that is allowed to have the given
   *     {@code address} as its network address
   * @param inetAddress the network address that may not remain stored with other devices than the
   *     one identified by the given {@code deviceIdentification}
   */
  public void clearDuplicateAddresses(
      final String deviceIdentification, final InetAddress inetAddress) {

    if (this.allowDuplicateEntries(inetAddress)) {
      if (inetAddress != null) {
        if (this.allowMultipleDevicesPerNetworkAddress) {
          LOGGER.info(
              "Not clearing duplicate network addresses, as these are allowed by configuration.");
        } else {
          LOGGER.info(
              "Not clearing duplicate network addresses, as {} is part of a configured exception in: {}",
              inetAddress.getHostAddress(),
              this.ipRangesAllowingMultipleDevicesPerAddress);
        }
      }
      return;
    }

    this.deviceRepository.findByNetworkAddress(inetAddress).stream()
        .filter(this.clearAddressForDevice(deviceIdentification))
        .forEach(this::clearNetworkAddress);
  }

  public boolean allowDuplicateEntries(final InetAddress inetAddress) {
    return this.allowMultipleDevicesPerNetworkAddress
        || inetAddress == null
        || LOCALHOST.equals(inetAddress)
        || this.duplicatesAllowedByConfiguration.test(inetAddress);
  }

  private Predicate<Device> clearAddressForDevice(final String exceptForThisIdentification) {
    return device -> !device.getDeviceIdentification().equals(exceptForThisIdentification);
  }

  private void clearNetworkAddress(final Device device) {
    LOGGER.info(
        "Clearing duplicate network address {} with device {}",
        device.getIpAddress(),
        device.getDeviceIdentification());
    device.clearNetworkAddress();
    this.deviceRepository.save(device);
  }
}
