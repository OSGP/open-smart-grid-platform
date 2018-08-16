/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.opensmartgridplatform.domain.core.valueobjects.CdmaDevice;

public class CdmaBatch implements Comparable<CdmaBatch> {

    private Short batchNumber;
    private List<CdmaBatchDevice> cdmaBatchDevices;

    public CdmaBatch(final CdmaDevice cdmaDevice) {
        this.batchNumber = cdmaDevice.getBatchNumber();
        if (this.batchNumber == null) {
            throw new IllegalArgumentException("batchNumber is a mandory field, value null is not allowed");
        }

        final CdmaBatchDevice cdmaBatchDevice = new CdmaBatchDevice(cdmaDevice);
        this.cdmaBatchDevices = new ArrayList<>();
        this.cdmaBatchDevices.add(cdmaBatchDevice);
    }

    public void addCdmaDevice(final CdmaDevice cdmaDevice) {
        final CdmaBatchDevice cdmaBatchDevice = new CdmaBatchDevice(cdmaDevice);
        this.cdmaBatchDevices.add(cdmaBatchDevice);
    }

    public Short getBatchNumber() {
        return this.batchNumber;
    }

    public List<CdmaBatchDevice> getCdmaBatchDevices() {
        return this.cdmaBatchDevices;
    }

    @Override
    public int hashCode() {
        return this.batchNumber.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof CdmaBatch)) {
            return false;
        }

        final CdmaBatch other = (CdmaBatch) obj;

        return Objects.equals(this.batchNumber, other.batchNumber);
    }

    @Override
    public int compareTo(final CdmaBatch other) {
        return this.batchNumber.compareTo(other.batchNumber);
    }

    /* @formatter:off
    *
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
     * @formatter:on
     */
}
