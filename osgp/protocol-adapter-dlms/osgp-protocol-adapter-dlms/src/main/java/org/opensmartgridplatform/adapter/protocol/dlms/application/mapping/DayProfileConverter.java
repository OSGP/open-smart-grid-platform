// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DayProfileActionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DayProfileDto;

public class DayProfileConverter extends CustomConverter<DayProfileDto, DataObject> {

  private List<DataObject> getDayObjectElements(final DayProfileDto dayProfile) {
    final List<DataObject> dayObjectElements = new ArrayList<>();

    final DataObject dayId = DataObject.newUInteger8Data(dayProfile.getDayId().shortValue());
    final DataObject dayActionObjectList =
        DataObject.newArrayData(this.getDayActionObjectList(dayProfile.getDayProfileActionList()));
    dayObjectElements.addAll(Arrays.asList(dayId, dayActionObjectList));

    return dayObjectElements;
  }

  private List<DataObject> getDayActionObjectList(
      final List<DayProfileActionDto> dayProfileActionList) {
    final List<DataObject> dayActionObjectList = new ArrayList<>();
    for (final DayProfileActionDto dayProfileAction : dayProfileActionList) {

      final DataObject dayObject =
          DataObject.newStructureData(this.getDayActionObjectElements(dayProfileAction));
      dayActionObjectList.add(dayObject);
    }
    return dayActionObjectList;
  }

  private List<DataObject> getDayActionObjectElements(final DayProfileActionDto dayProfileAction) {
    final List<DataObject> dayActionObjectElements = new ArrayList<>();

    final DataObject startTimeObject =
        this.timeAsDataObjectOctetString(dayProfileAction.getStartTime());

    // See "DSMR P3 v4.2.2 Final P3.pdf" Tariffication Script Table (Class
    // ID: 9). Value: 0-0:10.0.100.255
    final DataObject nameObject =
        DataObject.newOctetStringData(new byte[] {0, 0, 10, 0, 100, (byte) 255});
    final DataObject scriptSelectorObject =
        DataObject.newUInteger16Data(dayProfileAction.getScriptSelector());

    dayActionObjectElements.addAll(
        Arrays.asList(startTimeObject, nameObject, scriptSelectorObject));
    return dayActionObjectElements;
  }

  public DataObject timeAsDataObjectOctetString(final CosemTimeDto time) {

    final Integer h = time.getHour();
    final Integer m = time.getMinute();
    final Integer s = time.getSecond();
    final Integer hu = time.getHundredths();

    final byte[] ba = new byte[] {h.byteValue(), m.byteValue(), s.byteValue(), hu.byteValue()};
    return DataObject.newOctetStringData(ba);
  }

  @Override
  public DataObject convert(
      final DayProfileDto source,
      final Type<? extends DataObject> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    return DataObject.newStructureData(this.getDayObjectElements(source));
  }
}
