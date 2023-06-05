// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.application.services;

import com.beanit.openiec61850.Report;
import java.util.Date;
import java.util.Objects;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.entities.Iec61850ReportEntry;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.repositories.Iec61850ReportEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "iec61850ReportingService")
public class ReportingService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReportingService.class);

  @Autowired private Iec61850ReportEntryRepository iec61850ReportEntryRepository;

  @Transactional(value = "transactionManager", readOnly = true)
  public Iec61850ReportEntry retrieveReportEntry(
      final String deviceIdentification, final String reportId) {
    return this.iec61850ReportEntryRepository.findByDeviceIdentificationAndReportId(
        deviceIdentification, reportId);
  }

  @Transactional(value = "transactionManager")
  public void storeLastReportEntry(final Report report, final String deviceIdentification) {
    if (Objects.isNull(report.getEntryId()) || Objects.isNull(report.getTimeOfEntry())) {
      LOGGER.warn(
          "Not all report entry data availabe for report id {} and device identification {}, skip storing last report entry",
          report.getRptId(),
          deviceIdentification);
      return;
    }
    Iec61850ReportEntry reportEntry =
        this.iec61850ReportEntryRepository.findByDeviceIdentificationAndReportId(
            deviceIdentification, report.getRptId());
    if (reportEntry == null) {
      reportEntry =
          new Iec61850ReportEntry(
              deviceIdentification,
              report.getRptId(),
              report.getEntryId().getValue(),
              new Date(report.getTimeOfEntry().getTimestampValue()));
      LOGGER.info("Store new last report entry: {}", reportEntry);
    } else {
      reportEntry.updateLastReportEntry(
          report.getEntryId().getValue(), new Date(report.getTimeOfEntry().getTimestampValue()));
      LOGGER.info("Store updated last report entry: {}", reportEntry);
    }
    try {
      this.iec61850ReportEntryRepository.saveAndFlush(reportEntry);
    } catch (final JpaOptimisticLockingFailureException e) {
      LOGGER.debug("JpaOptimisticLockingFailureException", e);
      LOGGER.warn(
          "JPA optimistic locking failure exception while saving last report entry: {} with id {} and version {}",
          reportEntry,
          reportEntry.getId(),
          reportEntry.getVersion());
    }
  }
}
