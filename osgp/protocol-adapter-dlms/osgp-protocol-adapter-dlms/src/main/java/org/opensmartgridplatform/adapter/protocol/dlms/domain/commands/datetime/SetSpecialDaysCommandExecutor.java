// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.datetime;

import static org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType.SPECIAL_DAYS_TABLE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecialDayDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecialDaysRequestDataDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Component;

@Component()
public class SetSpecialDaysCommandExecutor
    extends AbstractCommandExecutor<List<SpecialDayDto>, AccessResultCode> {

  private final DlmsHelper dlmsHelper;
  private final ObjectConfigServiceHelper objectConfigServiceHelper;

  public SetSpecialDaysCommandExecutor(
      final ObjectConfigServiceHelper objectConfigServiceHelper, final DlmsHelper dlmsHelper) {
    super(SpecialDaysRequestDataDto.class);
    this.objectConfigServiceHelper = objectConfigServiceHelper;
    this.dlmsHelper = dlmsHelper;
  }

  @Override
  public List<SpecialDayDto> fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);
    final SpecialDaysRequestDataDto specialDaysRequestDataDto =
        (SpecialDaysRequestDataDto) bundleInput;

    return specialDaysRequestDataDto.getSpecialDays();
  }

  @Override
  public ActionResponseDto asBundleResponse(final AccessResultCode executionResult)
      throws ProtocolAdapterException {

    this.checkAccessResultCode(executionResult);

    return new ActionResponseDto("Set special days was successful");
  }

  @Override
  public AccessResultCode execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final List<SpecialDayDto> specialDays,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final StringBuilder specialDayData = new StringBuilder();

    final List<DataObject> specialDayEntries = new ArrayList<>();
    int i = 0;
    for (final SpecialDayDto specialDay : specialDays) {

      specialDayData
          .append(", ")
          .append(specialDay.getDayId())
          .append(" => ")
          .append(specialDay.getSpecialDayDate());

      final List<DataObject> specDayEntry = new ArrayList<>();
      specDayEntry.add(DataObject.newUInteger16Data(i));
      specDayEntry.add(this.dlmsHelper.asDataObject(specialDay.getSpecialDayDate()));
      specDayEntry.add(DataObject.newUInteger8Data((short) specialDay.getDayId()));

      final DataObject dayStruct = DataObject.newStructureData(specDayEntry);
      specialDayEntries.add(dayStruct);
      i += 1;
    }

    final AttributeAddress specialDaysTableEntries =
        this.objectConfigServiceHelper
            .findOptionalDefaultAttributeAddress(Protocol.forDevice(device), SPECIAL_DAYS_TABLE)
            .orElseThrow(
                () ->
                    new NotSupportedByProtocolException(
                        String.format(
                            "No address found for %s in protocol %s",
                            SPECIAL_DAYS_TABLE.name(), Protocol.forDevice(device).getName())));
    final DataObject entries = DataObject.newArrayData(specialDayEntries);

    final SetParameter request = new SetParameter(specialDaysTableEntries, entries);

    final String specialDayValues;
    if (specialDayData.isEmpty()) {
      specialDayValues = "";
    } else {
      specialDayValues = ", values [" + specialDayData.substring(2) + "]";
    }
    conn.getDlmsMessageListener()
        .setDescription(
            "SetSpecialDays"
                + specialDayValues
                + ", set attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(specialDaysTableEntries));

    try {
      return conn.getConnection().set(request);
    } catch (final IOException e) {
      throw new ConnectionException(e);
    }
  }
}
