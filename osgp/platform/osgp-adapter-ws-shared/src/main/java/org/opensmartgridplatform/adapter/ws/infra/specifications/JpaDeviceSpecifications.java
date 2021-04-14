/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.infra.specifications;

import java.util.Date;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.DeviceCurrentFirmwareModuleVersion;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.exceptions.ArgumentNullOrEmptyException;
import org.opensmartgridplatform.domain.core.specifications.DeviceSpecifications;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.domain.core.valueobjects.FirmwareModuleFilterType;
import org.springframework.data.jpa.domain.Specification;

public class JpaDeviceSpecifications implements DeviceSpecifications {

  private static final String ALIAS = "alias";
  private static final String DEVICE = "device";
  private static final String DEVICE_IDENTIFICATION = "deviceIdentification";
  private static final String DEVICE_IDENTIFICATIONS = "deviceIdentifications";
  private static final String DEVICE_MODEL = "deviceModel";
  private static final String DEVICE_TYPE = "deviceType";
  private static final String ID = "id";
  private static final String IN_MAINTENANCE = "inMaintenance";
  private static final String MANUFACTURER = "manufacturer";
  private static final String NAME = "name";
  private static final String ORGANISATION = "organisation";

  @Override
  public Specification<Device> hasTechnicalInstallationDate() throws ArgumentNullOrEmptyException {

    return ((deviceRoot, query, cb) ->
        cb.isNotNull(deviceRoot.<Date>get("technicalInstallationDate")));
  }

  @Override
  public Specification<Device> forOrganisation(final Organisation organisation)
      throws ArgumentNullOrEmptyException {
    if (organisation == null) {
      throw new ArgumentNullOrEmptyException(ORGANISATION);
    }

    return ((deviceRoot, query, cb) ->
        this.createPredicateForOrganisation(deviceRoot, query, cb, organisation));
  }

  private Predicate createPredicateForOrganisation(
      final Root<Device> deviceRoot,
      final CriteriaQuery<?> query,
      final CriteriaBuilder cb,
      final Organisation organisation) {

    final Subquery<Long> subquery = query.subquery(Long.class);
    final Root<DeviceAuthorization> deviceAuthorizationRoot =
        subquery.from(DeviceAuthorization.class);
    subquery.select(deviceAuthorizationRoot.get(DEVICE).get(ID).as(Long.class));
    subquery.where(cb.equal(deviceAuthorizationRoot.get(ORGANISATION), organisation.getId()));

    return cb.in(deviceRoot.get(ID)).value(subquery);
  }

  @Override
  public Specification<Device> hasDeviceIdentification(
      final String deviceIdentification, final boolean exactMatch)
      throws ArgumentNullOrEmptyException {
    if (StringUtils.isEmpty(deviceIdentification)) {
      throw new ArgumentNullOrEmptyException(DEVICE_IDENTIFICATION);
    }

    return ((deviceRoot, query, cb) ->
        this.createPredicateForDeviceIdentification(
            deviceRoot, cb, deviceIdentification, exactMatch));
  }

  private Predicate createPredicateForDeviceIdentification(
      final Root<Device> deviceRoot,
      final CriteriaBuilder cb,
      final String deviceIdentification,
      final boolean exactMatch) {

    if (exactMatch) {
      return cb.equal(deviceRoot.<String>get(DEVICE_IDENTIFICATION), deviceIdentification);
    } else {
      return cb.like(
          cb.upper(deviceRoot.<String>get(DEVICE_IDENTIFICATION)),
          deviceIdentification.toUpperCase());
    }
  }

  @Override
  public Specification<Device> hasCity(final String city) throws ArgumentNullOrEmptyException {
    if (StringUtils.isEmpty(city)) {
      throw new ArgumentNullOrEmptyException("city");
    }

    return ((deviceRoot, query, cb) ->
        cb.like(cb.upper(deviceRoot.<String>get("containerCity")), city.toUpperCase()));
  }

  @Override
  public Specification<Device> hasPostalCode(final String postalCode)
      throws ArgumentNullOrEmptyException {
    if (StringUtils.isEmpty(postalCode)) {
      throw new ArgumentNullOrEmptyException("postalCode");
    }

    return ((deviceRoot, query, cb) ->
        cb.like(cb.upper(deviceRoot.<String>get("containerPostalCode")), postalCode.toUpperCase()));
  }

  @Override
  public Specification<Device> hasStreet(final String street) throws ArgumentNullOrEmptyException {
    if (StringUtils.isEmpty(street)) {
      throw new ArgumentNullOrEmptyException("street");
    }

    return ((deviceRoot, query, cb) ->
        cb.like(cb.upper(deviceRoot.<String>get("containerStreet")), street.toUpperCase()));
  }

  @Override
  public Specification<Device> hasNumber(final String number) throws ArgumentNullOrEmptyException {
    if (StringUtils.isEmpty(number)) {
      throw new ArgumentNullOrEmptyException("number");
    }

    return ((deviceRoot, query, cb) ->
        cb.like(cb.upper(deviceRoot.<String>get("containerNumber")), number.toUpperCase()));
  }

  @Override
  public Specification<Device> hasMunicipality(final String municipality)
      throws ArgumentNullOrEmptyException {
    if (StringUtils.isEmpty(municipality)) {
      throw new ArgumentNullOrEmptyException("municipality");
    }

    return ((deviceRoot, query, cb) ->
        cb.like(
            cb.upper(deviceRoot.<String>get("containerMunicipality")), municipality.toUpperCase()));
  }

  @Override
  public Specification<Device> hasAlias(final String alias) throws ArgumentNullOrEmptyException {
    if (StringUtils.isEmpty(alias)) {
      throw new ArgumentNullOrEmptyException(ALIAS);
    }

    return ((deviceRoot, query, cb) ->
        cb.like(cb.upper(deviceRoot.<String>get(ALIAS)), alias.toUpperCase()));
  }

  @Override
  public Specification<Device> isManagedExternally(final Boolean isManagedExternally)
      throws ArgumentNullOrEmptyException {
    if (isManagedExternally == null) {
      throw new ArgumentNullOrEmptyException("isManagedExternally");
    }

    return ((deviceRoot, query, cb) ->
        this.createPredicateForIsManagedExternally(deviceRoot, query, cb, isManagedExternally));
  }

  private Predicate createPredicateForIsManagedExternally(
      final Root<Device> deviceRoot,
      final CriteriaQuery<?> query,
      final CriteriaBuilder cb,
      final boolean isManagedExternally) {

    final Subquery<Long> subquery = query.subquery(Long.class);
    final Root<DeviceAuthorization> deviceAuthorizationRoot =
        subquery.from(DeviceAuthorization.class);
    subquery.select(cb.countDistinct(deviceAuthorizationRoot));
    subquery.where(cb.equal(deviceAuthorizationRoot.get(DEVICE), deviceRoot.<Long>get(ID)));

    if (isManagedExternally) {
      return cb.greaterThan(subquery, Long.valueOf(1));
    } else {
      return cb.lessThanOrEqualTo(subquery, Long.valueOf(1));
    }
  }

  @Override
  public Specification<Device> isActived(final Boolean activated)
      throws ArgumentNullOrEmptyException {
    if (activated == null) {
      throw new ArgumentNullOrEmptyException("activated");
    }

    return ((deviceRoot, query, cb) -> cb.equal(deviceRoot.<Boolean>get("isActivated"), activated));
  }

  @Override
  public Specification<Device> isInMaintenance(final Boolean inMaintenance)
      throws ArgumentNullOrEmptyException {
    if (inMaintenance == null) {
      throw new ArgumentNullOrEmptyException(IN_MAINTENANCE);
    }

    return ((deviceRoot, query, cb) ->
        cb.equal(deviceRoot.<Boolean>get(IN_MAINTENANCE), inMaintenance));
  }

  @Override
  public Specification<Device> forOwner(final String organisation)
      throws ArgumentNullOrEmptyException {
    if (organisation == null) {
      throw new ArgumentNullOrEmptyException("owner");
    }

    return ((deviceRoot, query, cb) ->
        this.createPredicateForOwnerOrganisation(deviceRoot, query, cb, organisation));
  }

  private Predicate createPredicateForOwnerOrganisation(
      final Root<Device> deviceRoot,
      final CriteriaQuery<?> query,
      final CriteriaBuilder cb,
      final String organisation) {

    final Subquery<Long> subquery = query.subquery(Long.class);
    final Root<DeviceAuthorization> deviceAuthorizationRoot =
        subquery.from(DeviceAuthorization.class);
    subquery.select(deviceAuthorizationRoot.get(DEVICE).get(ID).as(Long.class));
    subquery.where(
        cb.and(
            cb.like(
                cb.upper(deviceAuthorizationRoot.get(ORGANISATION).<String>get(NAME)),
                organisation.toUpperCase()),
            cb.equal(
                deviceAuthorizationRoot.get("functionGroup"),
                DeviceFunctionGroup.OWNER.ordinal())));

    return cb.in(deviceRoot.get(ID)).value(subquery);
  }

  @Override
  public Specification<Device> forDeviceType(final String deviceType)
      throws ArgumentNullOrEmptyException {
    if (deviceType == null) {
      throw new ArgumentNullOrEmptyException(DEVICE_TYPE);
    }

    return ((deviceRoot, query, cb) ->
        cb.like(cb.upper(deviceRoot.<String>get(DEVICE_TYPE)), deviceType.toUpperCase()));
  }

  @Override
  public Specification<Device> forDeviceModel(final String deviceModel)
      throws ArgumentNullOrEmptyException {
    if (deviceModel == null) {
      throw new ArgumentNullOrEmptyException(DEVICE_MODEL);
    }

    return ((deviceRoot, query, cb) ->
        cb.like(
            cb.upper(deviceRoot.<String>get(DEVICE_MODEL).get("modelCode").as(String.class)),
            deviceModel.toUpperCase()));
  }

  @Override
  public Specification<Device> forManufacturer(final Manufacturer manufacturer)
      throws ArgumentNullOrEmptyException {
    if (manufacturer == null) {
      throw new ArgumentNullOrEmptyException(MANUFACTURER);
    }

    return ((deviceRoot, query, cb) ->
        this.createPredicateForManufacturer(deviceRoot, query, cb, manufacturer));
  }

  private Predicate createPredicateForManufacturer(
      final Root<Device> deviceRoot,
      final CriteriaQuery<?> query,
      final CriteriaBuilder cb,
      final Manufacturer manufacturer) {

    final Subquery<Long> subquery = query.subquery(Long.class);
    final Root<DeviceModel> deviceModelRoot = subquery.from(DeviceModel.class);
    subquery.select(deviceModelRoot.get(ID).as(Long.class));
    subquery.where(
        cb.equal(
            cb.upper(deviceModelRoot.get(MANUFACTURER).<String>get(NAME)),
            manufacturer.getName().toUpperCase()));

    return cb.in(deviceRoot.get(DEVICE_MODEL).get(ID).as(Long.class)).value(subquery);
  }

  @Override
  public Specification<Device> forFirmwareModuleVersion(
      final FirmwareModuleFilterType firmwareModuleFilterType, final String firmwareModuleVersion)
      throws ArgumentNullOrEmptyException {
    if (StringUtils.isEmpty(firmwareModuleVersion)) {
      throw new ArgumentNullOrEmptyException("firmwareModuleVersion");
    }
    if (firmwareModuleFilterType == null) {
      throw new ArgumentNullOrEmptyException("firmwareModuleType");
    }

    return ((deviceRoot, query, cb) ->
        this.createPredicateForFirmwareModuleVersion(
            deviceRoot, query, cb, firmwareModuleFilterType, firmwareModuleVersion));
  }

  private Predicate createPredicateForFirmwareModuleVersion(
      final Root<Device> deviceRoot,
      final CriteriaQuery<?> query,
      final CriteriaBuilder cb,
      final FirmwareModuleFilterType firmwareModuleFilterType,
      final String firmwareModuleVersion) {

    final String moduleDescription = firmwareModuleFilterType.getDescription();

    final Subquery<Long> subquery = query.subquery(Long.class);
    final Root<DeviceCurrentFirmwareModuleVersion> moduleVersionRoot =
        subquery.from(DeviceCurrentFirmwareModuleVersion.class);
    subquery.select(moduleVersionRoot.get("deviceId").as(Long.class));
    final Predicate moduleTypePredicate =
        cb.equal(moduleVersionRoot.get("moduleDescription").as(String.class), moduleDescription);
    final Predicate moduleVersionPredicate =
        cb.like(moduleVersionRoot.get("moduleVersion").as(String.class), firmwareModuleVersion);
    subquery.where(cb.and(moduleTypePredicate, moduleVersionPredicate));

    return cb.in(deviceRoot.get(ID).as(Long.class)).value(subquery);
  }

  @Override
  public final Specification<Device> existsInDeviceIdentificationList(
      final List<String> deviceIdentifications) throws ArgumentNullOrEmptyException {
    if (deviceIdentifications == null) {
      throw new ArgumentNullOrEmptyException(DEVICE_IDENTIFICATIONS);
    }

    return ((deviceRoot, query, cb) ->
        cb.in(deviceRoot.get(DEVICE_IDENTIFICATION)).value(deviceIdentifications));
  }

  @Override
  public final Specification<Device> excludeDeviceIdentificationList(
      final List<String> deviceIdentifications) throws ArgumentNullOrEmptyException {
    if (deviceIdentifications == null) {
      throw new ArgumentNullOrEmptyException(DEVICE_IDENTIFICATIONS);
    }

    return ((deviceRoot, query, cb) ->
        cb.not(deviceRoot.get(DEVICE_IDENTIFICATION).in(deviceIdentifications)));
  }
}
