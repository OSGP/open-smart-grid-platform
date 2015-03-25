package com.alliander.osgp.domain.core.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.exceptions.ArgumentNullOrEmptyException;

public interface DeviceSpecifications {
    Specification<Device> forOrganisation(final Organisation organisation) throws ArgumentNullOrEmptyException;

    Specification<Device> hasDeviceIdentification(final String deviceIdentification)
            throws ArgumentNullOrEmptyException;

    Specification<Device> hasCity(final String city) throws ArgumentNullOrEmptyException;

    Specification<Device> hasPostalCode(final String postalCode) throws ArgumentNullOrEmptyException;

    Specification<Device> hasStreet(final String street) throws ArgumentNullOrEmptyException;

    Specification<Device> hasNumber(final String number) throws ArgumentNullOrEmptyException;
}
