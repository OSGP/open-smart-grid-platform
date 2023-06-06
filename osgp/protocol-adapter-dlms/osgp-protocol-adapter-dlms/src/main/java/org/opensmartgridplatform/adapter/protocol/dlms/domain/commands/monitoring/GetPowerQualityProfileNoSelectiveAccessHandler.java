// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.monitoring;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileEntryValueDto;
import org.springframework.stereotype.Component;

@Component
public class GetPowerQualityProfileNoSelectiveAccessHandler
    extends AbstractGetPowerQualityProfileHandler {

  public GetPowerQualityProfileNoSelectiveAccessHandler(
      final DlmsHelper dlmsHelper, final ObjectConfigService objectConfigService) {
    super(dlmsHelper, objectConfigService);
  }

  @Override
  protected List<ProfileEntryValueDto> createProfileEntryValueDto(
      final DataObject profileEntryDataObject,
      final ProfileEntryDto previousProfileEntryDto,
      final LinkedHashMap<Integer, SelectableObject> selectedObjects,
      final int timeInterval) {

    final List<ProfileEntryValueDto> result = new ArrayList<>();
    final List<DataObject> dataObjects = profileEntryDataObject.getValue();

    for (int i = 0; i < dataObjects.size(); i++) {

      // The values are retrieved without selective access, so the meter could have returned more
      // values than we need. Only the values that are present in the selectedObjects should be
      // returned.
      if (selectedObjects.containsKey(i)) {
        final ProfileEntryValueDto currentProfileEntryValueDto =
            this.makeProfileEntryValueDto(
                dataObjects.get(i), selectedObjects.get(i), previousProfileEntryDto, timeInterval);
        result.add(currentProfileEntryValueDto);
      }
    }

    return result;
  }

  @Override
  protected DataObject convertSelectableCaptureObjects(
      final List<SelectableObject> selectableCaptureObjects) {
    return DataObject.newArrayData(new ArrayList<>());
  }
}
