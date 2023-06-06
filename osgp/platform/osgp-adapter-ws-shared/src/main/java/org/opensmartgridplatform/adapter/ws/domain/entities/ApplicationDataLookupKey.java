// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.domain.entities;

import static org.opensmartgridplatform.shared.utils.StacktraceUtils.currentStacktrace;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Embeddable
public final class ApplicationDataLookupKey implements Serializable {
  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationDataLookupKey.class);

  private static final long serialVersionUID = -6367887742782273500L;

  private String organisationIdentification;
  private String applicationName;

  public ApplicationDataLookupKey() {
    /*
     * Public no-argument constructor, required for JPA composite primary
     * key class.
     */
  }

  public ApplicationDataLookupKey(final String organisationIdentification) {
    this(organisationIdentification, "");
  }

  public ApplicationDataLookupKey(
      final String organisationIdentification, final String applicationName) {
    this.organisationIdentification =
        Objects.requireNonNull(
            organisationIdentification, "organisationIdentification must not be null");
    this.applicationName =
        Objects.requireNonNull(applicationName, "applicationName must not be null");
    if (applicationName.isEmpty()) {
      // Log this for troubleshooting. Eventually empty applicationName should not be supported
      // anymore.
      LOGGER.warn("Empty applicationName created with stacktrace: \n{}", currentStacktrace());
    }
  }

  public String getOrganisationIdentification() {
    return this.organisationIdentification;
  }

  public String getApplicationName() {
    return this.applicationName;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ApplicationDataLookupKey)) {
      return false;
    }
    final ApplicationDataLookupKey other = (ApplicationDataLookupKey) obj;
    return Objects.equals(
            this.getOrganisationIdentification(), other.getOrganisationIdentification())
        && Objects.equals(this.getApplicationName(), other.getApplicationName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.getOrganisationIdentification(), this.getApplicationName());
  }

  @Override
  public String toString() {
    return String.format(
        "%s[organisation=%s, application=%s]",
        this.getClass().getSimpleName(),
        this.getOrganisationIdentification(),
        this.getApplicationName());
  }
}
