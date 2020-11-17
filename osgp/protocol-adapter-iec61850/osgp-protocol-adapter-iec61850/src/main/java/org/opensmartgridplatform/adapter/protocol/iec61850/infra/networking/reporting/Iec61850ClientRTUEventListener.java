/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.protocol.iec61850.application.config.BeanUtil;
import org.opensmartgridplatform.adapter.protocol.iec61850.application.services.DeviceManagementService;
import org.opensmartgridplatform.adapter.protocol.iec61850.application.services.ReportingService;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.entities.Iec61850Device;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.repositories.Iec61850DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.ReadOnlyNodeContainer;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataResponseDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataSystemIdentifierDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.ReportDto;
import org.springframework.util.CollectionUtils;

import com.beanit.openiec61850.FcModelNode;
import com.beanit.openiec61850.Report;

public class Iec61850ClientRTUEventListener extends Iec61850ClientBaseEventListener {

    private static final String NODE_NAMES = "(RTU|PV|BATTERY|ENGINE|LOAD|CHP|HEAT_BUFFER|GAS_FURNACE|HEAT_PUMP|BOILER|WIND|PQ)";

    private static final Pattern REPORT_PATTERN = Pattern
            .compile("\\A(.*)" + NODE_NAMES + "([1-9]\\d*+)/LLN0\\.(Status|Measurements|Heartbeat)\\Z");

    private static final Map<String, Class<? extends Iec61850ReportHandler>> REPORT_HANDLERS_MAP = new HashMap<>();

    private ReportingService reportingService;

    static {
        REPORT_HANDLERS_MAP.put("RTU", Iec61850RtuReportHandler.class);
        REPORT_HANDLERS_MAP.put("PV", Iec61850PvReportHandler.class);
        REPORT_HANDLERS_MAP.put("BATTERY", Iec61850BatteryReportHandler.class);
        REPORT_HANDLERS_MAP.put("ENGINE", Iec61850EngineReportHandler.class);
        REPORT_HANDLERS_MAP.put("LOAD", Iec61850LoadReportHandler.class);
        REPORT_HANDLERS_MAP.put("LOAD_COMBINED", Iec61850CombinedLoadReportHandler.class);
        REPORT_HANDLERS_MAP.put("CHP", Iec61850ChpReportHandler.class);
        REPORT_HANDLERS_MAP.put("HEAT_BUFFER", Iec61850HeatBufferReportHandler.class);
        REPORT_HANDLERS_MAP.put("GAS_FURNACE", Iec61850GasFurnaceReportHandler.class);
        REPORT_HANDLERS_MAP.put("HEAT_PUMP", Iec61850HeatPumpReportHandler.class);
        REPORT_HANDLERS_MAP.put("BOILER", Iec61850BoilerReportHandler.class);
        REPORT_HANDLERS_MAP.put("WIND", Iec61850WindReportHandler.class);
        REPORT_HANDLERS_MAP.put("PQ", Iec61850PqReportHandler.class);
    }

    public Iec61850ClientRTUEventListener(final String deviceIdentification,
            final DeviceManagementService deviceManagementService, final ReportingService reportingService) {
        super(deviceIdentification, deviceManagementService, Iec61850ClientRTUEventListener.class);
        this.reportingService = reportingService;
    }

    private Iec61850ReportHandler getReportHandler(final String dataSetRef) {
        final Matcher reportMatcher = REPORT_PATTERN.matcher(dataSetRef);
        if (reportMatcher.matches()) {
            String node = reportMatcher.group(2);

            if ("LOAD".equals(node) && this.useCombinedLoad()) {
                node += "_COMBINED";
            }

            final int systemId = Integer.parseInt(reportMatcher.group(3));
            final Class<?> clazz = REPORT_HANDLERS_MAP.get(node);
            try {
                final Constructor<?> ctor = clazz.getConstructor(int.class);
                return (Iec61850ReportHandler) ctor.newInstance(systemId);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException ex) {
                this.logger.error("Unable to instantiate Iec61850ReportHandler ", ex);
            }
        }
        return null;
    }

    private boolean useCombinedLoad() {
        final Iec61850DeviceRepository repository = BeanUtil.getBean(Iec61850DeviceRepository.class);
        final Iec61850Device device = repository.findByDeviceIdentification(this.deviceIdentification);
        if (device != null) {
            return device.isUseCombinedLoad();
        }
        return BeanUtil.getBeanByName("defaultUseCombinedLoad", Boolean.class);
    }

    @Override
    public void newReport(final Report report) {

        final DateTime timeOfEntry = report.getTimeOfEntry() == null ? null
                : new DateTime(report.getTimeOfEntry().getTimestampValue());

        final String reportDescription = this.getReportDescription(report, timeOfEntry);

        this.logger.info("newReport for {}", reportDescription);

        if (Boolean.TRUE.equals(report.getBufOvfl())) {
            this.logger.warn("Buffer Overflow reported for {} - entries within the buffer may have been lost.",
                    reportDescription);
        }

        final Iec61850ReportHandler reportHandler = this.getReportHandler(report.getDataSetRef());
        if (reportHandler == null) {
            this.logger.warn("Skipping report because dataset is not supported {}", report.getDataSetRef());
            return;
        }

        this.logReportDetails(report);
        try {
            this.processReport(report, reportDescription, reportHandler);
        } catch (final ProtocolAdapterException e) {
            this.logger.warn("Unable to process report, discarding report", e);
        } catch (final Exception e) {
            this.logger.error("Exception while processing report", e);
        }

    }

    private String getReportDescription(final Report report, final DateTime timeOfEntry) {
        return String.format("device: %s, reportId: %s, timeOfEntry: %s, sqNum: %s%s%s", this.deviceIdentification,
                report.getRptId(), timeOfEntry == null ? "-" : timeOfEntry, report.getSqNum(),
                report.getSubSqNum() == null ? "" : " subSqNum: " + report.getSubSqNum(),
                report.isMoreSegmentsFollow() ? " (more segments follow for this sqNum)" : "");
    }

    private void processReport(final Report report, final String reportDescription,
            final Iec61850ReportHandler reportHandler) throws ProtocolAdapterException {
        final List<FcModelNode> dataSetMembers = report.getValues();
        if (CollectionUtils.isEmpty(dataSetMembers)) {
            this.logger.warn("No dataSet members available for {}", reportDescription);
            return;
        }

        final List<MeasurementDto> measurements = this.processMeasurements(reportHandler, reportDescription,
                dataSetMembers);

        final GetDataSystemIdentifierDto systemResult = reportHandler.createResult(measurements);
        final List<GetDataSystemIdentifierDto> systems = new ArrayList<>();
        systems.add(systemResult);

        final ReportDto reportDto = new ReportDto(report.getSqNum(),
                new DateTime(report.getTimeOfEntry().getTimestampValue()), report.getRptId());

        this.deviceManagementService.sendMeasurements(this.deviceIdentification,
                new GetDataResponseDto(systems, reportDto));

        this.reportingService.storeLastReportEntry(report, this.deviceIdentification);
    }

    private List<MeasurementDto> processMeasurements(final Iec61850ReportHandler reportHandler,
            final String reportDescription, final List<FcModelNode> members) {
        final List<MeasurementDto> measurements = new ArrayList<>();
        for (final FcModelNode member : members) {
            if (member == null) {
                this.logger.warn("Member == null in DataSet for {}", reportDescription);
                continue;
            }

            this.logger.info("Handle member {} for {}", member.getReference(), reportDescription);
            try {
                final List<MeasurementDto> memberMeasurements = reportHandler
                        .handleMember(new ReadOnlyNodeContainer(this.deviceIdentification, member));

                if (memberMeasurements.isEmpty()) {
                    this.logger.warn("Unsupported member {}, skipping", member.getName());
                } else {
                    measurements.addAll(memberMeasurements);
                }
            } catch (final Exception e) {
                this.logger.error("Error adding measurement for member {} from {}", member.getReference(),
                        reportDescription, e);
            }
        }
        return measurements;
    }

    private void logReportDetails(final Report report) {
        final StringBuilder sb = new StringBuilder("Report details for device ").append(this.deviceIdentification)
                .append(System.lineSeparator());
        this.logDefaultReportDetails(sb, report);

        final List<FcModelNode> dataSetMembers = report.getValues();
        this.logDataSetMembersDetails(report, dataSetMembers, sb);

        this.logger.info(sb.append(System.lineSeparator()).toString());
    }

    private void logDataSetMembersDetails(final Report report, final List<FcModelNode> dataSetMembers,
            final StringBuilder sb) {
        if (dataSetMembers == null) {
            sb.append("\t           DataSet:\tnull").append(System.lineSeparator());
        } else {
            sb.append("\t           DataSet:\t").append(report.getDataSetRef()).append(System.lineSeparator());
            if (!dataSetMembers.isEmpty()) {
                sb.append("\t   DataSet members:\t").append(dataSetMembers.size()).append(System.lineSeparator());
                for (final FcModelNode member : dataSetMembers) {
                    sb.append("\t            member:\t").append(member.getReference()).append(System.lineSeparator());
                }
            }
        }
    }

    @Override
    public void associationClosed(final IOException e) {
        this.logger.info("associationClosed for device: {}, {}", this.deviceIdentification,
                e == null ? "no IOException" : "IOException: " + e.getMessage());
    }

}
