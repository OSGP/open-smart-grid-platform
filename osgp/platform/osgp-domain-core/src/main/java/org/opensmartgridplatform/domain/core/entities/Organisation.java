// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformDomain;
import org.opensmartgridplatform.domain.core.valueobjects.PlatformFunctionGroup;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;
import org.opensmartgridplatform.shared.validation.Identification;

/** Organisation entity class */
@Entity
public class Organisation extends AbstractEntity {

  /** Serial Version UID. */
  private static final long serialVersionUID = -1097307978466479033L;

  private static final int ORGANISATION_IDENTIFICATION_MAX_LENGTH = 40;
  private static final int NAME_MAX_LENGTH = 255;
  private static final int PREFIX_MAX_LENGTH = 3;
  private static final String SEPARATOR = ";";

  @Column(length = ORGANISATION_IDENTIFICATION_MAX_LENGTH, nullable = false, unique = true)
  @Identification
  private String organisationIdentification;

  @Column(length = NAME_MAX_LENGTH, nullable = false, unique = false)
  @NotEmpty
  @Length(max = NAME_MAX_LENGTH)
  private String name;

  @Column(length = PREFIX_MAX_LENGTH, nullable = false, unique = false)
  @NotEmpty
  @Length(max = PREFIX_MAX_LENGTH)
  private String prefix;

  @OneToMany(mappedBy = "organisation", targetEntity = DeviceAuthorization.class)
  @Cascade(value = {CascadeType.DELETE})
  private final List<DeviceAuthorization> authorizations = new ArrayList<>();

  @Column(nullable = false)
  private PlatformFunctionGroup functionGroup;

  @Column(nullable = false)
  private boolean enabled;

  /** Comma separated list of domains organisation is allowed to access. */
  @Column(nullable = false, length = NAME_MAX_LENGTH)
  @NotEmpty
  @Length(max = NAME_MAX_LENGTH)
  private String domains;

  public Organisation() {
    // Default constructor
  }

  public Organisation(
      final String organisationIdentification,
      final String name,
      final String prefix,
      final PlatformFunctionGroup functionGroup) {
    this.organisationIdentification = organisationIdentification;
    this.name = name;
    this.prefix = prefix;
    this.functionGroup = functionGroup;
  }

  /**
   * Gets the organisations identification
   *
   * @return the identification
   */
  public String getOrganisationIdentification() {
    return this.organisationIdentification;
  }

  /**
   * Gets the organisations name
   *
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Gets the organisations prefix
   *
   * @return the prefix
   */
  public String getPrefix() {
    return this.prefix;
  }

  public List<DeviceAuthorization> getAuthorizations() {
    return this.authorizations;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.organisationIdentification);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Organisation)) {
      return false;
    }
    final Organisation other = (Organisation) o;
    return Objects.equals(this.organisationIdentification, other.organisationIdentification);
  }

  public PlatformFunctionGroup getFunctionGroup() {
    return this.functionGroup;
  }

  public void changeOrganisationData(
      final String organisationName, final PlatformFunctionGroup platformFunctionGroup) {
    this.name = organisationName;
    this.functionGroup = platformFunctionGroup;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setIsEnabled(final boolean value) {
    this.enabled = value;
  }

  public void setDomains(final List<PlatformDomain> domains) {
    this.domains = StringUtils.EMPTY;

    // Check for empty list
    if (domains == null) {
      return;
    }

    for (final PlatformDomain domain : domains) {
      this.domains += domain + SEPARATOR;
    }
  }

  public void addDomain(final PlatformDomain domain) {
    if (this.domains == null) {
      this.domains = StringUtils.EMPTY;
    }

    this.domains += domain + SEPARATOR;
  }

  public List<PlatformDomain> getDomains() {
    final List<PlatformDomain> result = new ArrayList<>();

    if (StringUtils.isEmpty(this.domains)) {
      return result;
    }

    final String[] splits = this.domains.split(SEPARATOR);
    for (final String s : splits) {
      result.add(PlatformDomain.valueOf(s));
    }

    return result;
  }
}
