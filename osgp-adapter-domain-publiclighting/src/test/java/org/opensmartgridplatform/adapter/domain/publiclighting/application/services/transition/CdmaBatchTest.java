package org.opensmartgridplatform.adapter.domain.publiclighting.application.services.transition;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;

/**

 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

import org.junit.Before;
import org.junit.Test;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CdmaBatchTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CdmaBatchTest.class);

    @Before
    public void before() {
    }

    @Test
    public void groupBy() {
        LOGGER.info("Test: groupBy");
        InetAddress host1 = null;
        InetAddress host2 = null;
        InetAddress host3 = null;
        final InetAddress host4 = null;
        try {
            host1 = InetAddress.getLocalHost();
            host2 = InetAddress.getByName("10.20.30.40");
            host3 = InetAddress.getByName("11.22.33.44");
        } catch (final UnknownHostException e) {
            Assert.fail(e.getMessage());
        }
        // final CdmaDevice device1 = new CdmaDevice("device1", host1, "250/1",
        // 1);
        // final CdmaDevice device2 = new CdmaDevice("deviceNr2", host2,
        // "322/2", 1);
        // final CdmaDevice device3 = new CdmaDevice("deviceNo3", host3,
        // "250/1", 1);
        // final CdmaDevice device4 = new CdmaDevice("device4", host4, "322/1",
        // 1);
        final CdmaDevice device1 = new CdmaDevice();
        final CdmaDevice device2 = new CdmaDevice();
        final CdmaDevice device3 = new CdmaDevice();
        final CdmaDevice device4 = new CdmaDevice();
        final CdmaDevice[] devicesArray = { device1, device2, device3, device4 };
        final List<CdmaDevice> devices = Arrays.asList(devicesArray);

        final Stream<CdmaDevice> devicesStream = devices.stream();
        final TreeMap<String, List<CdmaDevice>> myMap = devicesStream
                .collect(Collectors.groupingBy(CdmaDevice::getMastSegment, TreeMap::new, Collectors.toList()));

        System.out.println("Inhoud map: " + myMap);
        // final CdmaBatch batch = new CdmaBatch(1, devices);
        // LOGGER.info("Inhoud batch: " + batch);

        // Assert.assertTrue("Batchnummer fout",
        // Integer.valueOf(1).equals(batch.getBatchNumber()));
    }

}
