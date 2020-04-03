/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DomainInfoTest {
    static final String DOMAIN_DISTRIBUTION_AUTOMATION = "DISTRIBUTION_AUTOMATION";
    static final String DOMAIN_PUBLIC_LIGHTING = "PUBLIC_LIGHTING";
    static final String DOMAIN_VERSION = "1.0";
    static final DomainInfo DOMAIN_INFO_DISTRIBUTION_AUTOMATION = new DomainInfo(DOMAIN_DISTRIBUTION_AUTOMATION,
            DOMAIN_VERSION);
    static final DomainInfo DOMAIN_INFO_PUBLIC_LIGHTING = new DomainInfo(DOMAIN_PUBLIC_LIGHTING, DOMAIN_VERSION);

    @Test
    void testDomainInfo() {
        // Arrange
        final DomainInfo expected = DOMAIN_INFO_DISTRIBUTION_AUTOMATION;
        // Act
        final DomainInfo actual = new DomainInfo("DISTRIBUTION_AUTOMATION", "1.0");
        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testGetDomain() {
        // Arrange
        final String expected = DOMAIN_DISTRIBUTION_AUTOMATION;
        // Act
        final String actual = DOMAIN_INFO_DISTRIBUTION_AUTOMATION.getDomain();
        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testGetDomainVersion() {
        // Arrange
        final String expected = DOMAIN_VERSION;
        // Act
        final String actual = DOMAIN_INFO_DISTRIBUTION_AUTOMATION.getDomainVersion();
        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testEqualsReturnsTrueForSameObjects() {
        // Arrange
        final DomainInfo thisDomainInfo = DOMAIN_INFO_DISTRIBUTION_AUTOMATION;
        final DomainInfo otherDomainInfo = DOMAIN_INFO_DISTRIBUTION_AUTOMATION;
        // Act
        final boolean actual = thisDomainInfo.equals(otherDomainInfo);
        // Assert
        assertThat(actual).isTrue();
    }

    @Test
    void testEqualsReturnsTrueForObjectsWithSameValues() {
        // Arrange
        final DomainInfo thisDomainInfo = DOMAIN_INFO_DISTRIBUTION_AUTOMATION;
        final DomainInfo otherDomainInfo = new DomainInfo(DOMAIN_DISTRIBUTION_AUTOMATION, DOMAIN_VERSION);
        // Act
        final boolean actual = thisDomainInfo.equals(otherDomainInfo);
        // Assert
        assertThat(actual).isTrue();
    }

    @Test
    void testEqualsReturnsFalseForObjectsWithDifferentDomainValues() {
        // Arrange
        final DomainInfo thisDomainInfo = new DomainInfo(DOMAIN_DISTRIBUTION_AUTOMATION, DOMAIN_VERSION);
        final DomainInfo otherDomainInfo = new DomainInfo(DOMAIN_PUBLIC_LIGHTING, DOMAIN_VERSION);
        // Act
        final boolean actual = thisDomainInfo.equals(otherDomainInfo);
        // Assert
        assertThat(actual).isFalse();
    }

    @Test
    void testEqualsReturnsFalseForObjectsWithDifferentDomainVersionValues() {
        // Arrange
        final DomainInfo thisDomainInfo = new DomainInfo(DOMAIN_DISTRIBUTION_AUTOMATION, "1.0");
        final DomainInfo otherDomainInfo = new DomainInfo(DOMAIN_DISTRIBUTION_AUTOMATION, "2.0");
        // Act
        final boolean actual = thisDomainInfo.equals(otherDomainInfo);
        // Assert
        assertThat(actual).isFalse();
    }

    @Test
    void testHashCode() {
        // Arrange
        final DomainInfo domainInfo = new DomainInfo(DOMAIN_PUBLIC_LIGHTING, DOMAIN_VERSION);
        final int expected = DOMAIN_INFO_PUBLIC_LIGHTING.hashCode();
        // Act
        final int actual = domainInfo.hashCode();
        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testToString() {
        // Arrange
        final String expected = "DomainInfo [domain=PUBLIC_LIGHTING, domainVersion=1.0]";
        // Act
        final String actual = DOMAIN_INFO_PUBLIC_LIGHTING.toString();
        // Assert
        assertThat(actual).isEqualTo(expected);
    }

}
