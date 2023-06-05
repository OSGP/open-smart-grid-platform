// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class DomainInfoTest {
  private static final String DOMAIN_DISTRIBUTION_AUTOMATION = "DISTRIBUTION_AUTOMATION";
  private static final String DOMAIN_PUBLIC_LIGHTING = "PUBLIC_LIGHTING";
  private static final String DOMAIN_VERSION = "1.0";
  private static final DomainInfo DOMAIN_INFO_DISTRIBUTION_AUTOMATION =
      new DomainInfo(DOMAIN_DISTRIBUTION_AUTOMATION, DOMAIN_VERSION);
  private static final DomainInfo DOMAIN_INFO_PUBLIC_LIGHTING =
      new DomainInfo(DOMAIN_PUBLIC_LIGHTING, DOMAIN_VERSION);

  @Test
  public void testGetDomain() {
    // Arrange
    final String expected = DOMAIN_DISTRIBUTION_AUTOMATION;
    final DomainInfo domainInfo = new DomainInfo(DOMAIN_DISTRIBUTION_AUTOMATION, DOMAIN_VERSION);
    // Act
    final String actual = domainInfo.getDomain();
    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testGetDomainVersion() {
    // Arrange
    final String expected = DOMAIN_VERSION;
    final DomainInfo domainInfo = new DomainInfo(DOMAIN_DISTRIBUTION_AUTOMATION, DOMAIN_VERSION);
    // Act
    final String actual = domainInfo.getDomainVersion();
    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testEqualsReturnsTrueForSameObjects() {
    // Arrange
    final DomainInfo thisDomainInfo = DOMAIN_INFO_DISTRIBUTION_AUTOMATION;
    final DomainInfo otherDomainInfo = DOMAIN_INFO_DISTRIBUTION_AUTOMATION;
    // Act
    final boolean actual = thisDomainInfo.equals(otherDomainInfo);
    // Assert
    assertThat(actual).isTrue();
  }

  @Test
  public void testEqualsReturnsTrueForObjectsWithSameValues() {
    // Arrange
    final DomainInfo thisDomainInfo =
        new DomainInfo(DOMAIN_DISTRIBUTION_AUTOMATION, DOMAIN_VERSION);
    final DomainInfo otherDomainInfo =
        new DomainInfo(DOMAIN_DISTRIBUTION_AUTOMATION, DOMAIN_VERSION);
    // Act
    final boolean actual = thisDomainInfo.equals(otherDomainInfo);
    // Assert
    assertThat(actual).isTrue();
  }

  @Test
  public void testEqualsReturnsFalseForObjectsWithDifferentDomainValues() {
    // Arrange
    final DomainInfo thisDomainInfo =
        new DomainInfo(DOMAIN_DISTRIBUTION_AUTOMATION, DOMAIN_VERSION);
    final DomainInfo otherDomainInfo = new DomainInfo(DOMAIN_PUBLIC_LIGHTING, DOMAIN_VERSION);
    // Act
    final boolean actual = thisDomainInfo.equals(otherDomainInfo);
    // Assert
    assertThat(actual).isFalse();
  }

  @Test
  public void testEqualsReturnsFalseForObjectsWithDifferentDomainVersionValues() {
    // Arrange
    final DomainInfo thisDomainInfo = new DomainInfo(DOMAIN_DISTRIBUTION_AUTOMATION, "1.0");
    final DomainInfo otherDomainInfo = new DomainInfo(DOMAIN_DISTRIBUTION_AUTOMATION, "2.0");
    // Act
    final boolean actual = thisDomainInfo.equals(otherDomainInfo);
    // Assert
    assertThat(actual).isFalse();
  }

  @Test
  public void testHashCode() {
    // Arrange
    final DomainInfo domainInfo = new DomainInfo(DOMAIN_PUBLIC_LIGHTING, DOMAIN_VERSION);
    final int expected = DOMAIN_INFO_PUBLIC_LIGHTING.hashCode();
    // Act
    final int actual = domainInfo.hashCode();
    // Assert
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testToString() {
    // Arrange
    final String expected = "DomainInfo [domain=PUBLIC_LIGHTING, domainVersion=1.0]";
    // Act
    final String actual = DOMAIN_INFO_PUBLIC_LIGHTING.toString();
    // Assert
    assertThat(actual).isEqualTo(expected);
  }
}
