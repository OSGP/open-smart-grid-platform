//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects;

import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaDevice;

public class CdmaRun {

  private SortedMap<String, CdmaMastSegment> mastSegments;

  public CdmaRun() {
    this.mastSegments = new TreeMap<>();
  }

  public void add(final CdmaDevice cdmaDevice) {
    final CdmaBatchDevice cdmaBatchDevice =
        new CdmaBatchDevice(cdmaDevice.getDeviceIdentification(), cdmaDevice.getNetworkAddress());

    final String mastSegmentName =
        cdmaDevice.getMastSegmentName() == null
            ? CdmaMastSegment.DEFAULT_MASTSEGMENT
            : cdmaDevice.getMastSegmentName();

    CdmaMastSegment mastSegment = this.mastSegments.get(mastSegmentName);
    if (mastSegment == null) {
      mastSegment = new CdmaMastSegment(mastSegmentName);
      this.mastSegments.put(mastSegmentName, mastSegment);
    }
    mastSegment.addCdmaBatchDevice(cdmaDevice.getBatchNumber(), cdmaBatchDevice);
  }

  public Iterator<CdmaMastSegment> getMastSegmentIterator() {
    return this.mastSegments.values().iterator();
  }

  @Override
  public String toString() {
    return "CdmaRun [mastSegments=" + this.mastSegments.keySet() + "]";
  }
}
