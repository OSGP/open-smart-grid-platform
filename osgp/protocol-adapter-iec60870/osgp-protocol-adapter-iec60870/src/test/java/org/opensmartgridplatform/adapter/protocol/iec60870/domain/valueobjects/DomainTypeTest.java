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

class DomainTypeTest {

    @Test
    void testDomainInfoDistributionAutomation() {
        // Arrange
        final DomainInfo expected = DomainInfoTest.DOMAIN_INFO_DISTRIBUTION_AUTOMATION;
        // Act
        final DomainInfo actual = DomainType.DISTRIBUTION_AUTOMATION.domainInfo();
        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testDomainInfoPublicLighting() {
        // Arrange
        final DomainInfo expected = DomainInfoTest.DOMAIN_INFO_PUBLIC_LIGHTING;
        // Act
        final DomainInfo actual = DomainType.PUBLIC_LIGHTING.domainInfo();
        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
