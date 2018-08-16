/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.services.transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaDevice;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaMastSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CdmaBatch {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdmaBatch.class);
    private List<CdmaMastSegment> cdmaMastSegments;
    private List<CdmaDeviceList> allCdmaDeviceSeries;

    public CdmaBatch(final List<CdmaDevice> devices) {
        LOGGER.info("Create CDMA batch for " + devices.size() + " devices.");
        this.initAllCdmaDeviceSeries(devices);
    }

    private void initAllCdmaDeviceSeries(final List<CdmaDevice> devices) {
        final Stream<CdmaDevice> formattedDevices = devices.stream().map(CdmaDevice::mapEmptyFields);

        final Map<Boolean, TreeMap<String, TreeMap<Short, List<CdmaDevice>>>> cdmaDevices = formattedDevices
                .collect(this.partitionMastSegmentCollector());

        final TreeMap<String, TreeMap<Short, List<CdmaDevice>>> rawMastSegments = cdmaDevices.get(Boolean.TRUE);
        this.cdmaMastSegments = null;

        final TreeMap<String, TreeMap<Short, List<CdmaDevice>>> rawNoMastSegment = cdmaDevices.get(Boolean.FALSE);

        this.allCdmaDeviceSeries = new ArrayList<>();
        int iteration = 0;

        while (rawMastSegments != null || rawNoMastSegment != null) {
            if (rawMastSegments != null) {
                for (final Map.Entry<String, TreeMap<Short, List<CdmaDevice>>> batches : rawMastSegments.entrySet()) {
                    for (final List<CdmaDevice> batchDevices : batches.getValue().values()) {
                        final CdmaDeviceList cdmaDeviceList = new CdmaDeviceList(batchDevices, iteration);
                        this.allCdmaDeviceSeries.add(cdmaDeviceList);
                    }
                    rawMastSegments.remove(batches);
                }
            }

            if (rawNoMastSegment != null) {

            }

            iteration++; // aan einde van de loop
        }
    }

    private void initAllCdmaDeviceSeries_alternative(final List<CdmaDevice> devices) {
        this.allCdmaDeviceSeries = new ArrayList<>();

        if (devices == null || devices.isEmpty()) {
            return;
        }

        int iteration = 0;

        final List<CdmaDevice> newBatch = new ArrayList<>();
        final String currentMastSegment = devices.get(0).getMastSegment();
        String newMastSegment = devices.get(0).getMastSegment();
        for (final CdmaDevice device : devices) {
            newMastSegment = device.getMastSegment();
            if (currentMastSegment.equals(newMastSegment)) {
                newBatch.add(device);
            } else {
                final CdmaDeviceList cdmaDeviceList = new CdmaDeviceList(newBatch, iteration);
                iteration++;
            }
        }
        for (final CdmaDevice device : devices) {
            if (!device.getMastSegment().equals(currentMastSegment)) {

            }
            // final List<CdmaDevice> newBatch = new ArrayList<>();

            final CdmaDeviceList cdmaDeviceList = new CdmaDeviceList(newBatch, iteration);
            this.allCdmaDeviceSeries.add(cdmaDeviceList);

            iteration++;
        }

        // aan einde: sorteren
    }

    public boolean hasNext() {
        return true;
    }

    public CdmaDeviceList next() {
        return null;
    }

    /*
     * Returns a collector which can create a map by dividing a stream of CDMA
     * mast segments over items having the default CDMA mast segment and items
     * having another CDMA mast segment.
     *
     * Example: (Boolean.TRUE, [(DEVICE-WITHOUT-MAST-SEGMENT, [(1, [cd6,
     * cd1])])], Boolean.FALSE, [(2500/1, [(1, [cd6, cd1]), (2, [cd24, cd21])]),
     * ....])
     */
    private Collector<CdmaDevice, ?, Map<Boolean, TreeMap<String, TreeMap<Short, List<CdmaDevice>>>>> partitionMastSegmentCollector() {
        return Collectors.partitioningBy(CdmaDevice::hasDefaultMastSegment, this.mastSegmentCollector());
    }

    /*
     * Returns a collector which can create a map by grouping a stream of CDMA
     * device batches by their mast segment. The created map is ordered by mast
     * segment. The batches within a mast segment are ordered by batch number.
     *
     * Example: (2500/1, [(1, [cd6, cd1]), (2, [cd24, cd21])]), (2500/2, [(1,
     * [cd12, cd13]), (3, [cd20])]), ...
     */
    private Collector<CdmaDevice, ?, TreeMap<String, TreeMap<Short, List<CdmaDevice>>>> mastSegmentCollector() {
        return Collectors.groupingBy(CdmaDevice::getMastSegment, TreeMap<String, TreeMap<Short, List<CdmaDevice>>>::new,
                this.batchNumberCollector());
    }

    /*
     * Returns a collector which can create a map by grouping a stream of CDMA
     * devices by their batch number. The created map is ordered by batch
     * number.
     *
     * Example: (1, [CdmaDevice6, CdmaDevice1]), (2, [CdmaDevice24,
     * CdmaDevice21]), (5, [CdmaDevice12, CdmaDevice15]), ...
     */
    private Collector<CdmaDevice, ?, TreeMap<Short, List<CdmaDevice>>> batchNumberCollector() {
        return Collectors.groupingBy(CdmaDevice::getBatchNumber, TreeMap<Short, List<CdmaDevice>>::new,
                Collectors.toList());
    }

}
