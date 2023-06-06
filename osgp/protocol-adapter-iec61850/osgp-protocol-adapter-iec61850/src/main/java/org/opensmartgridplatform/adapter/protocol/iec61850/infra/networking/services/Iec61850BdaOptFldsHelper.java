// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services;

import com.beanit.openiec61850.BdaOptFlds;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class Iec61850BdaOptFldsHelper {

  final List<String> fields = new ArrayList<>();
  final BdaOptFlds optFlds;

  public Iec61850BdaOptFldsHelper(final BdaOptFlds optFlds) {
    this.optFlds = optFlds;
    this.init();
  }

  public String getInfo() {
    if (this.optFlds == null) {
      return "null";
    }
    return StringUtils.join(this.fields, ", ");
  }

  private void init() {
    this.checkBufferOverflow();
    this.checkConfigRevision();
    this.checkDataReference();
    this.checkDataSetName();
    this.checkEntryId();
    this.checkReasonForInclusion();
    this.checkReportTimestamp();
    this.checkSegmentation();
    this.checkSequenceNumber();
  }

  private void checkBufferOverflow() {
    if (this.optFlds.isBufferOverflow()) {
      this.fields.add("BufferOverflow");
    }
  }

  private void checkConfigRevision() {
    if (this.optFlds.isConfigRevision()) {
      this.fields.add("ConfigRevision");
    }
  }

  private void checkDataReference() {
    if (this.optFlds.isDataReference()) {
      this.fields.add("DataReference");
    }
  }

  private void checkDataSetName() {
    if (this.optFlds.isDataSetName()) {
      this.fields.add("DataSetName");
    }
  }

  private void checkEntryId() {
    if (this.optFlds.isEntryId()) {
      this.fields.add("EntryId");
    }
  }

  private void checkReasonForInclusion() {
    if (this.optFlds.isReasonForInclusion()) {
      this.fields.add("ReasonForInclusion");
    }
  }

  private void checkReportTimestamp() {
    if (this.optFlds.isReportTimestamp()) {
      this.fields.add("ReportTimestamp");
    }
  }

  private void checkSegmentation() {
    if (this.optFlds.isSegmentation()) {
      this.fields.add("Segmentation");
    }
  }

  private void checkSequenceNumber() {
    if (this.optFlds.isSequenceNumber()) {
      this.fields.add("SequenceNumber");
    }
  }
}
