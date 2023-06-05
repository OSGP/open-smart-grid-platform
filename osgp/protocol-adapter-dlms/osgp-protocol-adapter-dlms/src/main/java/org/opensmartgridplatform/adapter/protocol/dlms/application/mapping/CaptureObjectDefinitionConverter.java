// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import java.util.ArrayList;
import java.util.List;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CaptureObjectDefinitionDto;

public class CaptureObjectDefinitionConverter
    extends CustomConverter<CaptureObjectDefinitionDto, DataObject> {

  private static final int DATA_INDEX_ENTIRE_ATTRIBUTE = 0;

  @Override
  public DataObject convert(
      final CaptureObjectDefinitionDto source,
      final Type<? extends DataObject> destinationType,
      final MappingContext context) {

    if (source == null) {
      return null;
    }

    final List<DataObject> captureObjectDefinitionElements = new ArrayList<>();
    captureObjectDefinitionElements.add(DataObject.newUInteger16Data(source.getClassId()));
    captureObjectDefinitionElements.add(
        DataObject.newOctetStringData(source.getLogicalName().toByteArray()));
    captureObjectDefinitionElements.add(DataObject.newInteger8Data(source.getAttributeIndex()));
    captureObjectDefinitionElements.add(this.convertDataIndex(source.getDataIndex()));

    return DataObject.newStructureData(captureObjectDefinitionElements);
  }

  private DataObject convertDataIndex(final Integer dataIndex) {
    if (dataIndex == null) {
      return DataObject.newUInteger16Data(DATA_INDEX_ENTIRE_ATTRIBUTE);
    }
    return DataObject.newUInteger16Data(dataIndex);
  }
}
