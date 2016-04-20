/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.AccessRightDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AccessSelectorListDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AssociationLnListElementDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AssociationLnListTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AttributeAccessDescriptorDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AttributeAccessItemDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AttributeAccessModeTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MethodAccessDescriptorDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MethodAccessItemDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MethodAccessModeTypeDto;

@Component
public class GetAssociationLnObjectsCommandExecutor implements CommandExecutor<Void, AssociationLnListTypeDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetAssociationLnObjectsCommandExecutor.class);

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

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public AssociationLnListTypeDto execute(final ClientConnection conn, final DlmsDevice device, final Void object)
            throws ProtocolAdapterException {

        final AttributeAddress attributeAddress = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

        LOGGER.debug("Retrieving Association LN objects for class id: {}, obis code: {}, attribute id: {}", CLASS_ID,
                OBIS_CODE, ATTRIBUTE_ID);

        List<GetResult> getResultList;
        try {
            getResultList = conn.get(attributeAddress);
        } catch (IOException | TimeoutException e) {
            throw new ConnectionException(e);
        }

        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException("No GetResult received while retrieving Association LN objects.");
        }

        if (getResultList.size() > 1 || getResultList.get(0) == null) {
            throw new ProtocolAdapterException("Expected 1 GetResult while retrieving Association LN objects, got "
                    + getResultList.size());
        }

        final DataObject resultData = getResultList.get(0).resultData();
        if (!resultData.isComplex()) {
            throw new ProtocolAdapterException("Unexpected type of element");
        }

        @SuppressWarnings("unchecked")
        final List<AssociationLnListElementDto> elements = this
        .convertAssociationLnList((List<DataObject>) getResultList.get(0).resultData().value());

        return new AssociationLnListTypeDto(elements);
    }

    private List<AssociationLnListElementDto> convertAssociationLnList(final List<DataObject> resultDataValue)
            throws ProtocolAdapterException {
        final List<AssociationLnListElementDto> elements = new ArrayList<>();

        for (final DataObject obisCodeMetaData : resultDataValue) {
            @SuppressWarnings("unchecked")
            final List<DataObject> obisCodeMetaDataList = (List<DataObject>) obisCodeMetaData.value();
            final AssociationLnListElementDto element = new AssociationLnListElementDto(
                    this.dlmsHelperService.readLong(obisCodeMetaDataList.get(CLASS_ID_INDEX), "classId"), new Integer(
                            (short) obisCodeMetaDataList.get(VERSION_INDEX).value()),
                            this.dlmsHelperService.readLogicalName(obisCodeMetaDataList.get(OBIS_CODE_INDEX),
                            "AssociationLN Element"), this.convertAccessRights(obisCodeMetaDataList
                                            .get(ACCESS_RIGHTS_INDEX)));

            elements.add(element);
        }

        return elements;
    }

    private AccessRightDto convertAccessRights(final DataObject dataObject) throws ProtocolAdapterException {
        if (!dataObject.isComplex()) {
            return null;
        }

        @SuppressWarnings("unchecked")
        final List<DataObject> accessRights = (List<DataObject>) dataObject.value();

        @SuppressWarnings("unchecked")
        final AttributeAccessDescriptorDto attributeAccessDescriptor = this
                .convertAttributeAccessDescriptor((List<DataObject>) accessRights.get(
                        ACCESS_RIGHTS_ATTRIBUTE_ACCESS_INDEX).value());

        @SuppressWarnings("unchecked")
        final MethodAccessDescriptorDto methodAccessDescriptor = this
                .convertMethodAttributeAccessDescriptor((List<DataObject>) accessRights.get(
                        ACCESS_RIGHTS_METHOD_ACCESS_INDEX).value());

        return new AccessRightDto(attributeAccessDescriptor, methodAccessDescriptor);
    }

    private AttributeAccessDescriptorDto convertAttributeAccessDescriptor(
            final List<DataObject> attributeAccessDescriptor) throws ProtocolAdapterException {
        final List<AttributeAccessItemDto> attributeAccessItems = new ArrayList<>();

        for (final DataObject attributeAccessItemRaw : attributeAccessDescriptor) {
            @SuppressWarnings("unchecked")
            final List<DataObject> attributeAccessItem = (List<DataObject>) attributeAccessItemRaw.value();

            AccessSelectorListDto asl = null;
            if (attributeAccessItem.get(ACCESS_RIGHTS_ATTRIBUTE_ACCESS_ACCESS_SELECTORS_INDEX).isNull()) {
                asl = new AccessSelectorListDto(Collections.<Integer> emptyList());
            } else {
                final DataObject accessSelectors = attributeAccessItem
                        .get(ACCESS_RIGHTS_ATTRIBUTE_ACCESS_ACCESS_SELECTORS_INDEX);
                asl = new AccessSelectorListDto((List<Integer>) accessSelectors.value());
            }

            attributeAccessItems.add(new AttributeAccessItemDto(this.dlmsHelperService.readLong(
                    attributeAccessItem.get(ACCESS_RIGHTS_ATTRIBUTE_ACCESS_ATTRIBUTE_ID_INDEX), "").intValue(),
                    AttributeAccessModeTypeDto.values()[this.dlmsHelperService.readLong(
                            attributeAccessItem.get(ACCESS_RIGHTS_ATTRIBUTE_ACCESS_ACCESS_MODE_INDEX), "").intValue()],
                    asl));
        }

        return new AttributeAccessDescriptorDto(attributeAccessItems);
    }

    private MethodAccessDescriptorDto convertMethodAttributeAccessDescriptor(
            final List<DataObject> methodAccessDescriptor) throws ProtocolAdapterException {
        final List<MethodAccessItemDto> methodAccessItems = new ArrayList<>();

        for (final DataObject methodAccessItemRaw : methodAccessDescriptor) {
            @SuppressWarnings("unchecked")
            final List<DataObject> methodAccessItem = (List<DataObject>) methodAccessItemRaw.value();
            methodAccessItems.add(new MethodAccessItemDto(this.dlmsHelperService.readLong(
                    methodAccessItem.get(ACCESS_RIGHTS_METHOD_ACCESS_METHOD_ID_INDEX), "").intValue(),
                    MethodAccessModeTypeDto.values()[this.dlmsHelperService.readLong(
                            methodAccessItem.get(ACCESS_RIGHTS_METHOD_ACCESS_ACCESS_MODE_INDEX), "").intValue()]));
        }

        return new MethodAccessDescriptorDto(methodAccessItems);
    }
}
