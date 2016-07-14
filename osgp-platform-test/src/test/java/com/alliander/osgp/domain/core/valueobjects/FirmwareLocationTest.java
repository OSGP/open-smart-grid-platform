/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class FirmwareLocationTest {
    @Test
    public void getDomainTest() {
        final FirmwareLocation subject = new FirmwareLocation("flexovltest.cloudapp.net", "firmware");

        assertThat(subject.getDomain(), equalTo("flexovltest.cloudapp.net"));
    }

    @Test
    public void getFullPathTest() {
        final FirmwareLocation subject = new FirmwareLocation("flexovltest.cloudapp.net", "firmware");

        assertThat(subject.getFullPath("ame-v1.0.zip"), equalTo("/firmware/ame-v1.0.zip"));
    }

    @Test
    public void getFullPathWithSlashEndingPartialPathTest() {
        final FirmwareLocation subject = new FirmwareLocation("flexovltest.cloudapp.net", "firmware/");

        assertThat(subject.getFullPath("ame-v1.0.zip"), equalTo("/firmware/ame-v1.0.zip"));
    }

    @Test
    public void getFullPathWithSlashStartingPartialPathTest() {
        final FirmwareLocation subject = new FirmwareLocation("flexovltest.cloudapp.net", "/firmware");

        assertThat(subject.getFullPath("ame-v1.0.zip"), equalTo("/firmware/ame-v1.0.zip"));
    }

    @Test
    public void getFullPathWithDotStartingExtension() {
        final FirmwareLocation subject = new FirmwareLocation("flexovltest.cloudapp.net", "/firmware");

        assertThat(subject.getFullPath("ame-v1.0.zip"), equalTo("/firmware/ame-v1.0.zip"));
    }

    @Test
    public void getFullPathWithEmptyPartialPath() {
        final FirmwareLocation subject = new FirmwareLocation("flexovltest.cloudapp.net", "");

        assertThat(subject.getFullPath("ame-v1.0.zip"), equalTo("/ame-v1.0.zip"));
    }

    @Test
    public void getFullPathWithEmptyFileExtension() {
        final FirmwareLocation subject = new FirmwareLocation("flexovltest.cloudapp.net", "/firmware");

        assertThat(subject.getFullPath("ame-v1.0"), equalTo("/firmware/ame-v1.0"));
    }

    @Test
    public void getDomainWithTrailingSlash() {
        final FirmwareLocation subject = new FirmwareLocation("flexovltest.cloudapp.net/", "firmware");

        assertThat(subject.getDomain(), equalTo("flexovltest.cloudapp.net"));
    }

    @Test
    public void getDomainWithLeadingProtocol() {
        final FirmwareLocation subject = new FirmwareLocation("http://flexovltest.cloudapp.net", "firmware");

        assertThat(subject.getDomain(), equalTo("flexovltest.cloudapp.net"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createFirmwareLocationWithEmptyDomain() {
        new FirmwareLocation("", "/firmware");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createFirmwareLocationWithBlankDomain() {
        new FirmwareLocation("       ", "/firmware");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createFirmwareLocationWithNullDomain() {
        new FirmwareLocation(null, "/firmware");
    }
}
