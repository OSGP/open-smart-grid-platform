/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.specifications;

import java.util.List;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.exceptions.ArgumentNullOrEmptyException;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleFilterType;
import org.springframework.data.jpa.domain.Specification;

public interface DeviceSpecifications {
  Specification<Device> forOrganisation(final Organisation organisation)
      throws ArgumentNullOrEmptyException;

  Specification<Device> hasDeviceIdentification(
      final String deviceIdentification, final boolean exactMatch)
      throws ArgumentNullOrEmptyException;

  Specification<Device> hasAlias(final String alias) throws ArgumentNullOrEmptyException;

  Specification<Device> hasCity(final String city) throws ArgumentNullOrEmptyException;

  Specification<Device> hasPostalCode(final String postalCode) throws ArgumentNullOrEmptyException;

  Specification<Device> hasStreet(final String street) throws ArgumentNullOrEmptyException;

  Specification<Device> hasNumber(final String number) throws ArgumentNullOrEmptyException;

  Specification<Device> hasMunicipality(final String municipality)
      throws ArgumentNullOrEmptyException;

  Specification<Device> isManagedExternally(final Boolean isManagedExternally)
      throws ArgumentNullOrEmptyException;

  Specification<Device> isActived(final Boolean activated) throws ArgumentNullOrEmptyException;

  Specification<Device> isInMaintenance(final Boolean inMaintenance)
      throws ArgumentNullOrEmptyException;

  Specification<Device> hasTechnicalInstallationDate() throws ArgumentNullOrEmptyException;

  Specification<Device> forOwner(final String organisation) throws ArgumentNullOrEmptyException;

  Specification<Device> forDeviceType(final String deviceType) throws ArgumentNullOrEmptyException;

  Specification<Device> forDeviceModel(final String deviceModel)
      throws ArgumentNullOrEmptyException;

  Specification<Device> forManufacturer(final Manufacturer manufacturer)
      throws ArgumentNullOrEmptyException;

  Specification<Device> forFirmwareModuleVersion(
      FirmwareModuleFilterType firmwareModuleFilterType, String firmwareModuleVersion)
      throws ArgumentNullOrEmptyException;

  Specification<Device> existsInDeviceIdentificationList(List<String> deviceIdentifications)
      throws ArgumentNullOrEmptyException;

  Specification<Device> excludeDeviceIdentificationList(List<String> deviceIdentifications)
      throws ArgumentNullOrEmptyException;
}
