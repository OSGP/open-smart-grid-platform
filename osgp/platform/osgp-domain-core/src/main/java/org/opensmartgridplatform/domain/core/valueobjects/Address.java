// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address implements Serializable {

  private static final long serialVersionUID = -4694887724278796555L;

  @Column(length = 255)
  private String city;

  @Column(length = 10)
  private String postalCode;

  @Column(length = 255)
  private String street;

  @Column() private Integer number;

  @Column(length = 10)
  private String numberAddition;

  @Column(length = 255)
  private String municipality;

  public Address() {
    // Default constructor for hibernate
  }

  public Address(
      final String city,
      final String postalCode,
      final String street,
      final Integer number,
      final String numberAddition,
      final String municipality) {
    this.city = city;
    this.postalCode = postalCode;
    this.street = street;
    this.number = number;
    this.numberAddition = numberAddition;
    this.municipality = municipality;
  }

  public String getCity() {
    return this.city;
  }

  public String getPostalCode() {
    return this.postalCode;
  }

  public String getStreet() {
    return this.street;
  }

  public Integer getNumber() {
    return this.number;
  }

  public String getNumberAddition() {
    return this.numberAddition;
  }

  public String getMunicipality() {
    return this.municipality;
  }

  @Override
  public String toString() {
    return "Address [city="
        + this.city
        + ", postalCode="
        + this.postalCode
        + ", street="
        + this.street
        + ", number="
        + this.number
        + ", numberAddition="
        + this.numberAddition
        + ", municipality="
        + this.municipality
        + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        this.city,
        this.postalCode,
        this.street,
        this.number,
        this.numberAddition,
        this.municipality);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof Address)) {
      return false;
    }

    final Address other = (Address) obj;
    boolean result = Objects.equals(this.city, other.city);
    result &= Objects.equals(this.postalCode, other.postalCode);
    result &= Objects.equals(this.street, other.street);
    result &= Objects.equals(this.number, other.number);
    result &= Objects.equals(this.numberAddition, other.numberAddition);
    result &= Objects.equals(this.municipality, other.municipality);
    return result;
  }
}
