// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;

public class CaptureObject {

  private final int classId;

  private final ObisCode obisCode;

  private final byte attributeId;

  private final int dataIndex;

  public CaptureObject(
      final int classId, final ObisCode obisCode, final byte attributeId, final int dataIndex) {
    this.classId = classId;
    this.obisCode = obisCode;
    this.attributeId = attributeId;
    this.dataIndex = dataIndex;
  }

  public CaptureObject(
      final int classId, final String obisCodeString, final byte attributeId, final int dataIndex) {
    this.classId = classId;
    this.obisCode = new ObisCode(obisCodeString);
    this.attributeId = attributeId;
    this.dataIndex = dataIndex;
  }

  public static CaptureObject newCaptureObject(final DataObject dataObject) {
    if (!dataObject.isComplex()) {
      throw new IllegalArgumentException(
          "The given data is not a valid capture object definition: " + dataObject);
    }
    final List<DataObject> elements = dataObject.getValue();
    if (!elements.get(0).isNumber()
        || !elements.get(1).isByteArray()
        || !elements.get(2).isNumber()
        || !elements.get(3).isNumber()) {
      throw new IllegalArgumentException(
          "The given data does not contain the elements of a valid capture object definition: "
              + elements);
    }

    final int classId = elements.get(0).getValue();
    final ObisCode obisCode = new ObisCode((byte[]) elements.get(1).getValue());
    final byte attributeId = elements.get(2).getValue();
    final int dataIndex = elements.get(3).getValue();

    return new CaptureObject(classId, obisCode, attributeId, dataIndex);
  }

  public DataObject asDataObject() {
    return DataObject.newStructureData(
        Arrays.asList(
            DataObject.newUInteger16Data(this.classId),
            DataObject.newOctetStringData(this.obisCode.bytes()),
            DataObject.newInteger8Data(this.attributeId),
            DataObject.newUInteger16Data(this.dataIndex)));
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.classId, this.obisCode, this.attributeId, this.dataIndex);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }

    final CaptureObject other = (CaptureObject) obj;
    return this.attributeId == other.attributeId
        && this.classId == other.classId
        && this.dataIndex == other.dataIndex
        && this.obisCode.equals(other.obisCode);
  }

  @Override
  public String toString() {
    return "CaptureObject[classId="
        + this.classId
        + ", obisCode="
        + this.obisCode.asDecimalString()
        + ", attributeId="
        + this.attributeId
        + ", dataIndex="
        + this.dataIndex
        + "]";
  }
}
