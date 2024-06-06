// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
@Table(name = "iec61850_last_report_entry")
public class Iec61850ReportEntry extends AbstractEntity {

  private static final long serialVersionUID = -4203294559891991816L;

  @Column(nullable = false, length = 40)
  private String deviceIdentification;

  @Column(nullable = false)
  private String reportId;

  @Column(nullable = false)
  private byte[] entryId;

  @Column(nullable = false)
  private Date timeOfEntry;

  public Iec61850ReportEntry() {
    // Default constructor for hibernate
  }

  public Iec61850ReportEntry(
      final String deviceIdentification,
      final String reportId,
      final byte[] entryId,
      final Date timeOfEntry) {
    this.deviceIdentification = deviceIdentification;
    this.reportId = reportId;
    this.entryId = entryId;
    this.timeOfEntry = timeOfEntry;
  }

  public String getReportId() {
    return this.reportId;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public byte[] getEntryId() {
    return this.entryId;
  }

  public Date getTimeOfEntry() {
    return this.timeOfEntry;
  }

  public void updateLastReportEntry(final byte[] entryId, final Date timeOfEntry) {
    this.entryId = entryId;
    this.timeOfEntry = timeOfEntry;
  }

  @Override
  public String toString() {
    return String.format(
        "Iec61850BufferedReport [deviceIdentification=%s, reportId=%s, entryId=%s (%s), timeOfEntry=%s]",
        this.deviceIdentification,
        this.reportId,
        Arrays.toString(this.entryId),
        new String(this.entryId, Charset.forName("UTF-8")),
        this.timeOfEntry);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Iec61850ReportEntry)) {
      return false;
    }

    final Iec61850ReportEntry report = (Iec61850ReportEntry) obj;

    return this.deviceIdentification.equals(report.deviceIdentification)
        && this.reportId.equals(report.reportId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.deviceIdentification, this.reportId);
  }
}
