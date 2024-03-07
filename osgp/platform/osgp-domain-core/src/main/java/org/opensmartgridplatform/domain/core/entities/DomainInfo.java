// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.entities;

import jakarta.persistence.Entity;
import java.util.Objects;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

/**
 * Class containing information about a domain and its messaging configuration by means of property
 * prefixes to be used for custom configuration properties.
 */
@Entity
public class DomainInfo extends AbstractEntity {

  private static final long serialVersionUID = 1L;

  private String domain;
  private String domainVersion;
  private String incomingRequestsPropertyPrefix;
  private String outgoingResponsesPropertyPrefix;
  private String outgoingRequestsPropertyPrefix;
  private String incomingResponsesPropertyPrefix;

  @SuppressWarnings("unused")
  private DomainInfo() {
    // Default constructor for Hibernate
  }

  /**
   * Construct a DomainInfo instance.
   *
   * @param domain The name of the domain.
   * @param version The version of the domain.
   * @param incomingRequestsPropertyPrefix The property prefix for incoming domain requests
   *     configuration.
   * @param outgoingResponsesPropertyPrefix The property prefix for outgoing domain responses
   *     configuration.
   * @param outgoingRequestsPropertyPrefix The property prefix for outgoing domain requests
   *     configuration.
   * @param incomingResponsesPropertyPrefix The property prefix for incoming domain responses
   *     configuration.
   */
  public DomainInfo(
      final String domain,
      final String domainVersion,
      final String incomingRequestsPropertyPrefix,
      final String outgoingResponsesPropertyPrefix,
      final String outgoingRequestsPropertyPrefix,
      final String incomingResponsesPropertyPrefix) {
    this.domain = domain;
    this.domainVersion = domainVersion;
    this.incomingRequestsPropertyPrefix = incomingRequestsPropertyPrefix;
    this.outgoingResponsesPropertyPrefix = outgoingResponsesPropertyPrefix;
    this.outgoingRequestsPropertyPrefix = outgoingRequestsPropertyPrefix;
    this.incomingResponsesPropertyPrefix = incomingResponsesPropertyPrefix;
  }

  public static String getKey(final String domain, final String domainVersion) {
    return createKey(domain, domainVersion);
  }

  public String getKey() {
    return createKey(this.domain, this.domainVersion);
  }

  private static String createKey(final String protocol, final String version) {
    return protocol + "-" + version;
  }

  /**
   * The name of the domain.
   *
   * @return The name of the domain.
   */
  public String getDomain() {
    return this.domain;
  }

  /**
   * The version of the domain.
   *
   * @return The version of the domain.
   */
  public String getDomainVersion() {
    return this.domainVersion;
  }

  /**
   * The property prefix used for configuration of custom properties for receiving domain request
   * messages.
   *
   * @return The property prefix for incoming requests.
   */
  public String getIncomingRequestsPropertyPrefix() {
    return this.incomingRequestsPropertyPrefix;
  }

  /**
   * The property prefix used for configuration of custom properties for sending domain response
   * messages.
   *
   * @return The property prefix for outgoing responses.
   */
  public String getOutgoingResponsesPropertyPrefix() {
    return this.outgoingResponsesPropertyPrefix;
  }

  /**
   * The property prefix used for configuration of custom properties for sending domain request
   * messages.
   *
   * @return The property prefix for outgoing requests.
   */
  public String getOutgoingRequestsPropertyPrefix() {
    return this.outgoingRequestsPropertyPrefix;
  }

  /**
   * The property prefix used for configuration of custom properties for receiving domain response
   * messages.
   *
   * @return The property prefix for incoming responses.
   */
  public String getIncomingResponsesPropertyPrefix() {
    return this.incomingResponsesPropertyPrefix;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DomainInfo)) {
      return false;
    }
    final DomainInfo domainInfo = (DomainInfo) o;
    return Objects.equals(this.getKey(), domainInfo.getKey());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.getKey());
  }

  @Override
  public String toString() {
    return "DomainInfo [domain=" + this.domain + ", domainVersion=" + this.domainVersion + "]";
  }
}
