package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
            throw new ProtocolAdapterException("No GetResult received while retrieving configuration objects.");
        }

        if (getResultList.size() > 1 || getResultList.get(0) == null) {
            throw new ProtocolAdapterException("Expected 1 GetResult while retrieving configuration objects, got "
                    + getResultList.size());
        }

        final DataObject resultData = getResultList.get(0).resultData();
        if (!resultData.isComplex()) {
            throw new ProtocolAdapterException("Unexpected type of element");
        }

        final List<AssociationLnListElementDto> elements = new ArrayList<>();

        @SuppressWarnings("unchecked")
        final List<DataObject> resultDataValue = (List<DataObject>) getResultList.get(0).resultData().value();
        for (final DataObject obisCodeMetaData : resultDataValue) {
            @SuppressWarnings("unchecked")
            final List<DataObject> obisCodeMetaDataList = (List<DataObject>) obisCodeMetaData.value();
            final AssociationLnListElementDto element = new AssociationLnListElementDto(
                    this.dlmsHelperService.readLong(obisCodeMetaDataList.get(CLASS_ID_INDEX), "classId"), new Integer(
                            (short) obisCodeMetaDataList.get(VERSION_INDEX).value()),
                            this.dlmsHelperService.readLogicalName(obisCodeMetaDataList.get(OBIS_CODE_INDEX),
                            "AssociationLN Element"), this.readAccessRights(obisCodeMetaDataList
                                            .get(ACCESS_RIGHTS_INDEX)));

            elements.add(element);
        }

        return new AssociationLnListTypeDto(elements);
    }

    private AccessRightDto readAccessRights(final DataObject dataObject) throws ProtocolAdapterException {
        if (!dataObject.isComplex()) {
            return null;
        }

        final List<DataObject> accessRights = (List<DataObject>) dataObject.value();

        final List<DataObject> attributeAccessDescriptor = (List<DataObject>) accessRights.get(0).value();
        final List<AttributeAccessItemDto> attributeAccessItems = new ArrayList<>();
        for (final DataObject attributeAccessItemRaw : attributeAccessDescriptor) {
            final List<DataObject> attributeAccessItem = (List<DataObject>) attributeAccessItemRaw.value();

            AccessSelectorListDto asl = null;
            if (attributeAccessItem.get(2).isNull()) {
                asl = new AccessSelectorListDto(Collections.EMPTY_LIST);
            } else {
                // TODO conversion.
                asl = new AccessSelectorListDto(Arrays.asList(1, 2, 3));
            }

            attributeAccessItems.add(new AttributeAccessItemDto(this.dlmsHelperService.readLong(
                    attributeAccessItem.get(0), "").intValue(),
                    AttributeAccessModeTypeDto.values()[this.dlmsHelperService.readLong(attributeAccessItem.get(1), "")
                                                        .intValue()], asl));
        }

        final AttributeAccessDescriptorDto d = new AttributeAccessDescriptorDto(attributeAccessItems);

        final List<DataObject> methodAccessDescriptor = (List<DataObject>) accessRights.get(1).value();
        final List<MethodAccessItemDto> methodAccessItems = new ArrayList<>();
        for (final DataObject methodAccessItemRaw : methodAccessDescriptor) {
            final List<DataObject> methodAccessItem = (List<DataObject>) methodAccessItemRaw.value();
            methodAccessItems.add(new MethodAccessItemDto(this.dlmsHelperService.readLong(methodAccessItem.get(0), "")
                    .intValue(), MethodAccessModeTypeDto.values()[this.dlmsHelperService.readLong(
                            methodAccessItem.get(1), "").intValue()]));
        }

        final MethodAccessDescriptorDto f = new MethodAccessDescriptorDto(methodAccessItems);

        final AccessRightDto g = new AccessRightDto(d, f);

        return g;
    }
}
