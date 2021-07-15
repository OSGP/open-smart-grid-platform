/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.protocol.iec61850.application.services.DeviceManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beanit.openiec61850.BdaReasonForInclusion;
import com.beanit.openiec61850.ClientEventListener;
import com.beanit.openiec61850.HexConverter;
import com.beanit.openiec61850.Report;

public abstract class Iec61850ClientBaseEventListener implements ClientEventListener {

    protected final Logger logger;

    /**
     * Node names of EvnRpn nodes that occur as members of the report data-set.
     */
    protected final String deviceIdentification;
    protected final DeviceManagementService deviceManagementService;
    protected Integer firstNewSqNum = null;

    public Iec61850ClientBaseEventListener(final String deviceIdentification,
            final DeviceManagementService deviceManagementService, final Class<?> loggerClass) {
        this.deviceManagementService = deviceManagementService;
        this.deviceIdentification = deviceIdentification;
        this.logger = LoggerFactory.getLogger(loggerClass);
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    /**
     * Before enabling reporting on the device, set the SqNum of the buffered
     * report data to be able to check if incoming reports have been received
     * already.
     *
     * @param value
     *            the value of SqNum of a BR node on the device.
     */
    public void setSqNum(final int value) {
        this.logger.info("First new SqNum for report listener for device: {} is: {}", this.deviceIdentification, value);
        this.firstNewSqNum = value;
    }

    /**
     * The logging of the {@link Report} consists of a default part and a custom
     * part. This method is intended for the default part.
     */
    public void logDefaultReportDetails(final StringBuilder sb, final Report report) {
        sb.append("\t             RptId:\t").append(report.getRptId()).append(System.lineSeparator());
        sb.append("\t        DataSetRef:\t").append(report.getDataSetRef()).append(System.lineSeparator());
        sb.append("\t           ConfRev:\t").append(report.getConfRev()).append(System.lineSeparator());
        if (report.getBufOvfl() == null) {
            sb.append("\t           BufOvfl:\tnull").append(System.lineSeparator());
        } else {
            sb.append("\t           BufOvfl:\t").append(report.getBufOvfl()).append(System.lineSeparator());
        }

        sb.append("\t           EntryId:\t").append(report.getEntryId()).append(System.lineSeparator());
        if (report.getEntryId() != null) {
            sb.append("\t                   \t(")
                    .append(new String(report.getEntryId().getValue(), Charset.forName("UTF-8")))
                    .append(")")
                    .append(System.lineSeparator());
        }
        sb.append("\tInclusionBitString:\t")
                .append(Arrays.toString(report.getInclusionBitString()))
                .append(System.lineSeparator());
        sb.append("\tMoreSegmentsFollow:\t").append(report.isMoreSegmentsFollow()).append(System.lineSeparator());
        sb.append("\t             SqNum:\t").append(report.getSqNum()).append(System.lineSeparator());
        sb.append("\t          SubSqNum:\t").append(report.getSubSqNum()).append(System.lineSeparator());
        sb.append("\t       TimeOfEntry:\t").append(report.getTimeOfEntry()).append(System.lineSeparator());
        if (report.getTimeOfEntry() != null) {
            sb.append("\t                   \t(")
                    .append(new DateTime(report.getTimeOfEntry().getTimestampValue()))
                    .append(')')
                    .append(System.lineSeparator());
        }
        final List<BdaReasonForInclusion> reasonCodes = report.getReasonCodes();
        if ((reasonCodes != null) && !reasonCodes.isEmpty()) {
            sb.append("\t       ReasonCodes:").append(System.lineSeparator());
            for (final BdaReasonForInclusion reasonCode : reasonCodes) {
                sb.append("\t                   \t")
                        .append(reasonCode.getReference() == null ? HexConverter.toHexString(reasonCode.getValue())
                                : reasonCode)
                        .append("\t(")
                        .append(new Iec61850BdaReasonForInclusionHelper(reasonCode).getInfo())
                        .append(')')
                        .append(System.lineSeparator());
            }
        }
    }

}
