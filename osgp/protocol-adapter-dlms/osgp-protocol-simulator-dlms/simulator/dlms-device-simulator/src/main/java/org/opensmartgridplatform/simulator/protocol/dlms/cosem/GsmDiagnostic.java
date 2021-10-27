/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import org.openmuc.jdlms.AttributeAccessMode;
import org.openmuc.jdlms.CosemAttribute;
import org.openmuc.jdlms.CosemClass;
import org.openmuc.jdlms.CosemInterfaceObject;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.datatypes.DataObject.Type;

@CosemClass(id = 47)
public class GsmDiagnostic extends CosemInterfaceObject {

  @CosemAttribute(id = 2, type = Type.VISIBLE_STRING, accessMode = AttributeAccessMode.READ_ONLY)
  public DataObject operator;

  @CosemAttribute(id = 3, type = Type.ENUMERATE, accessMode = AttributeAccessMode.READ_ONLY)
  public DataObject status;

  @CosemAttribute(id = 4, type = Type.ENUMERATE, accessMode = AttributeAccessMode.READ_ONLY)
  public DataObject csAttachment;

  @CosemAttribute(id = 5, type = Type.ENUMERATE, accessMode = AttributeAccessMode.READ_ONLY)
  public DataObject psStatus;

  @CosemAttribute(id = 6, type = Type.STRUCTURE, accessMode = AttributeAccessMode.READ_ONLY)
  public DataObject cellInfo;

  @CosemAttribute(id = 7, type = Type.ARRAY, accessMode = AttributeAccessMode.READ_ONLY)
  public DataObject adjacentCells;

  @CosemAttribute(id = 8, type = Type.DATE_TIME, accessMode = AttributeAccessMode.READ_ONLY)
  public DataObject captureTime;

  public GsmDiagnostic(
      final String obisCode,
      final String operator,
      final int status,
      final int csAttachment,
      final int psStatus,
      final CellInfo cellInfo,
      final List<AdjacentCellInfo> adjacentCellInfos,
      final CosemDateTime captureTime) {
    super(obisCode);

    this.operator = DataObject.newVisibleStringData(operator.getBytes(StandardCharsets.US_ASCII));
    this.status = DataObject.newEnumerateData(status);
    this.csAttachment = DataObject.newEnumerateData(csAttachment);
    this.psStatus = DataObject.newEnumerateData(psStatus);

    this.cellInfo = cellInfo.getDataObject();

    this.adjacentCells = this.createAdjacentCellsDataObject(adjacentCellInfos);

    this.captureTime = DataObject.newDateTimeData(captureTime);
  }

  private DataObject createAdjacentCellsDataObject(final List<AdjacentCellInfo> adjacentCellInfos) {
    return DataObject.newArrayData(
        adjacentCellInfos.stream()
            .map(AdjacentCellInfo::getDataObject)
            .collect(Collectors.toList()));
  }

  public static class CellInfo {
    final long cellId;
    final int locationId;
    final short signalQuality;
    final short ber;
    final int mcc;
    final int mnc;
    final long channelNumber;

    public CellInfo(
        final long cellId,
        final int locationId,
        final short signalQuality,
        final short ber,
        final int mcc,
        final int mnc,
        final long channelNumber) {
      this.cellId = cellId;
      this.locationId = locationId;
      this.signalQuality = signalQuality;
      this.ber = ber;
      this.mcc = mcc;
      this.mnc = mnc;
      this.channelNumber = channelNumber;
    }

    public DataObject getDataObject() {
      return DataObject.newStructureData(
          DataObject.newUInteger32Data(this.cellId),
          DataObject.newUInteger16Data(this.locationId),
          DataObject.newUInteger8Data(this.signalQuality),
          DataObject.newUInteger8Data(this.ber),
          DataObject.newUInteger16Data(this.mcc),
          DataObject.newUInteger16Data(this.mnc),
          DataObject.newUInteger32Data(this.channelNumber));
    }
  }

  public static class AdjacentCellInfo {
    final long adjacentCellId;
    final short adjacentCellSignalQuality;

    public AdjacentCellInfo(final long adjacentCellId, final short adjacentCellSignalQuality) {
      this.adjacentCellId = adjacentCellId;
      this.adjacentCellSignalQuality = adjacentCellSignalQuality;
    }

    public DataObject getDataObject() {
      return DataObject.newStructureData(
          DataObject.newUInteger32Data(this.adjacentCellId),
          DataObject.newUInteger8Data(this.adjacentCellSignalQuality));
    }
  }
}
