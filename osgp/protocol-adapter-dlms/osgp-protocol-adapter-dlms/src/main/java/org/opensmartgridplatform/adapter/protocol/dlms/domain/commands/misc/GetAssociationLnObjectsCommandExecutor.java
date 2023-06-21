// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.AbstractCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.JdlmsObjectToStringUtil;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AccessRightDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AccessSelectorListDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActionResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AssociationLnListElementDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AssociationLnListTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AssociationLnObjectsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AttributeAccessDescriptorDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AttributeAccessItemDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AttributeAccessModeTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetAssociationLnObjectsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MethodAccessDescriptorDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MethodAccessItemDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MethodAccessModeTypeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetAssociationLnObjectsCommandExecutor
    extends AbstractCommandExecutor<Void, AssociationLnListTypeDto> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GetAssociationLnObjectsCommandExecutor.class);

  private static final int CLASS_ID = 15;
  private static final ObisCode OBIS_CODE = new ObisCode("0.0.40.0.0.255");
  private static final int ATTRIBUTE_ID = 2;

  private static final int CLASS_ID_INDEX = 0;
  private static final int VERSION_INDEX = 1;
  private static final int OBIS_CODE_INDEX = 2;
  private static final int ACCESS_RIGHTS_INDEX = 3;

  private static final int ACCESS_RIGHTS_ATTRIBUTE_ACCESS_INDEX = 0;
  private static final int ACCESS_RIGHTS_ATTRIBUTE_ACCESS_ATTRIBUTE_ID_INDEX = 0;
  private static final int ACCESS_RIGHTS_ATTRIBUTE_ACCESS_ACCESS_MODE_INDEX = 1;
  private static final int ACCESS_RIGHTS_ATTRIBUTE_ACCESS_ACCESS_SELECTORS_INDEX = 2;

  private static final int ACCESS_RIGHTS_METHOD_ACCESS_INDEX = 1;
  private static final int ACCESS_RIGHTS_METHOD_ACCESS_METHOD_ID_INDEX = 0;
  private static final int ACCESS_RIGHTS_METHOD_ACCESS_ACCESS_MODE_INDEX = 1;

  @Autowired private DlmsHelper dlmsHelper;

  public GetAssociationLnObjectsCommandExecutor() {
    super(GetAssociationLnObjectsRequestDto.class);
  }

  @Override
  public Void fromBundleRequestInput(final ActionRequestDto bundleInput)
      throws ProtocolAdapterException {

    this.checkActionRequestType(bundleInput);

    return null;
  }

  @Override
  public ActionResponseDto asBundleResponse(final AssociationLnListTypeDto executionResult)
      throws ProtocolAdapterException {

    return new AssociationLnObjectsResponseDto(executionResult);
  }

  @Override
  public AssociationLnListTypeDto execute(
      final DlmsConnectionManager conn,
      final DlmsDevice device,
      final Void object,
      final MessageMetadata messageMetadata)
      throws ProtocolAdapterException {

    final AttributeAddress attributeAddress =
        new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

    conn.getDlmsMessageListener()
        .setDescription(
            "GetAssociationLnObjects, retrieve attribute: "
                + JdlmsObjectToStringUtil.describeAttributes(attributeAddress));

    LOGGER.debug(
        "Retrieving Association LN objects for class id: {}, obis code: {}, attribute id: {}",
        CLASS_ID,
        OBIS_CODE,
        ATTRIBUTE_ID);

    final List<GetResult> getResultList =
        this.dlmsHelper.getAndCheck(conn, device, "Association LN Objects", attributeAddress);

    final DataObject resultData = getResultList.get(0).getResultData();
    if (!resultData.isComplex()) {
      throw new ProtocolAdapterException("Unexpected type of element");
    }

    final List<DataObject> associationLnListObjects = resultData.getValue();
    final List<AssociationLnListElementDto> elements =
        this.convertAssociationLnList(associationLnListObjects);

    return new AssociationLnListTypeDto(elements);
  }

  private List<AssociationLnListElementDto> convertAssociationLnList(
      final List<DataObject> resultDataValue) throws ProtocolAdapterException {
    final List<AssociationLnListElementDto> elements = new ArrayList<>();

    for (final DataObject obisCodeMetaData : resultDataValue) {
      final List<DataObject> obisCodeMetaDataList = obisCodeMetaData.getValue();
      final short version = obisCodeMetaDataList.get(VERSION_INDEX).getValue();
      final AssociationLnListElementDto element =
          new AssociationLnListElementDto(
              this.dlmsHelper.readLong(obisCodeMetaDataList.get(CLASS_ID_INDEX), "classId"),
              version,
              this.dlmsHelper.readLogicalName(
                  obisCodeMetaDataList.get(OBIS_CODE_INDEX), "AssociationLN Element"),
              this.convertAccessRights(obisCodeMetaDataList.get(ACCESS_RIGHTS_INDEX)));

      elements.add(element);
    }

    return elements;
  }

  private AccessRightDto convertAccessRights(final DataObject dataObject)
      throws ProtocolAdapterException {
    if (!dataObject.isComplex()) {
      return null;
    }

    final List<DataObject> accessRights = dataObject.getValue();

    final List<DataObject> attributeAccessDescriptorObjects =
        accessRights.get(ACCESS_RIGHTS_ATTRIBUTE_ACCESS_INDEX).getValue();
    final AttributeAccessDescriptorDto attributeAccessDescriptor =
        this.convertAttributeAccessDescriptor(attributeAccessDescriptorObjects);

    final List<DataObject> methodAccessDescriptorObjects =
        accessRights.get(ACCESS_RIGHTS_METHOD_ACCESS_INDEX).getValue();
    final MethodAccessDescriptorDto methodAccessDescriptor =
        this.convertMethodAccessDescriptor(methodAccessDescriptorObjects);

    return new AccessRightDto(attributeAccessDescriptor, methodAccessDescriptor);
  }

  private AttributeAccessDescriptorDto convertAttributeAccessDescriptor(
      final List<DataObject> attributeAccessDescriptor) throws ProtocolAdapterException {

    final List<AttributeAccessItemDto> attributeAccessItems = new ArrayList<>();

    for (final DataObject attributeAccessItemRaw : attributeAccessDescriptor) {
      final List<DataObject> attributeAccessItem = attributeAccessItemRaw.getValue();

      final AccessSelectorListDto asl;
      if (attributeAccessItem.get(ACCESS_RIGHTS_ATTRIBUTE_ACCESS_ACCESS_SELECTORS_INDEX).isNull()) {
        asl = new AccessSelectorListDto(Collections.emptyList());
      } else {
        final List<DataObject> accessSelectorsObjects =
            attributeAccessItem
                .get(ACCESS_RIGHTS_ATTRIBUTE_ACCESS_ACCESS_SELECTORS_INDEX)
                .getValue();
        asl = new AccessSelectorListDto(this.convertAccessSelectors(accessSelectorsObjects));
      }

      attributeAccessItems.add(
          new AttributeAccessItemDto(
              this.dlmsHelper
                  .readLong(
                      attributeAccessItem.get(ACCESS_RIGHTS_ATTRIBUTE_ACCESS_ATTRIBUTE_ID_INDEX),
                      "")
                  .intValue(),
              AttributeAccessModeTypeDto.values()[
                  this.dlmsHelper
                      .readLong(
                          attributeAccessItem.get(ACCESS_RIGHTS_ATTRIBUTE_ACCESS_ACCESS_MODE_INDEX),
                          "")
                      .intValue()],
              asl));
    }

    return new AttributeAccessDescriptorDto(attributeAccessItems);
  }

  private List<Integer> convertAccessSelectors(final List<DataObject> accessSelectors)
      throws ProtocolAdapterException {
    final List<Integer> convertedAccessSelectors = new ArrayList<>();
    for (final DataObject accessSelectorRaw : accessSelectors) {
      convertedAccessSelectors.add(this.dlmsHelper.readLong(accessSelectorRaw, "").intValue());
    }
    return convertedAccessSelectors;
  }

  private MethodAccessDescriptorDto convertMethodAccessDescriptor(
      final List<DataObject> methodAccessDescriptor) throws ProtocolAdapterException {
    final List<MethodAccessItemDto> methodAccessItems = new ArrayList<>();

    for (final DataObject methodAccessItemRaw : methodAccessDescriptor) {
      final List<DataObject> methodAccessItem = methodAccessItemRaw.getValue();
      methodAccessItems.add(
          new MethodAccessItemDto(
              this.dlmsHelper
                  .readLong(methodAccessItem.get(ACCESS_RIGHTS_METHOD_ACCESS_METHOD_ID_INDEX), "")
                  .intValue(),
              MethodAccessModeTypeDto.values()[
                  this.dlmsHelper
                      .readLong(
                          methodAccessItem.get(ACCESS_RIGHTS_METHOD_ACCESS_ACCESS_MODE_INDEX), "")
                      .intValue()]));
    }

    return new MethodAccessDescriptorDto(methodAccessItems);
  }
}
