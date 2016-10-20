/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetAttributeValuesRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetAttributeValuesResponseDto;

@Component
public class RetrieveAllAttributeValuesCommandExecutor extends AbstractCommandExecutor<DataObject, String> {

    private static final int OBIS_CODE_BYTE_ARRAY_LENGTH = 6;
    /* 0 is the index of the class number */
    private static final int CLASS_ID_INDEX = 0;
    /* 2 is index of the obis code */
    private static final int OBIS_CODE_INDEX = 2;
    /* 3 is the index of the attributes */
    private static final int ATTR_INDEX = 3;
    private static final int CLASS_ID = 15;
    private static final ObisCode OBIS_CODE = new ObisCode("0.0.40.0.0.255");
    private static final int ATTRIBUTE_ID = 2;

    @Autowired
    private DlmsHelperService dlmsHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(RetrieveAllAttributeValuesCommandExecutor.class);

    public RetrieveAllAttributeValuesCommandExecutor() {
        super(GetAttributeValuesRequestDto.class);
    }

    @Override
    public DataObject fromBundleRequestInput(final ActionRequestDto bundleInput) throws ProtocolAdapterException {
        /*
         * GetAllAttributeValuesRequestDto does not contain any values to pass
         * on, and the RetrieveAllAttributeValuesCommandExecutor takes a
         * DataObject as input that is ignored.
         */
        return null;
    }

    @Override
    public ActionResponseDto asBundleResponse(final String executionResult) throws ProtocolAdapterException {
        return new GetAttributeValuesResponseDto(executionResult);
    }

    @Override
    public String execute(final DlmsConnectionHolder conn, final DlmsDevice device, final DataObject object)
            throws ProtocolAdapterException {

        final AttributeAddress attributeAddress = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

        conn.getDlmsMessageListener().setDescription(
                "RetrieveAllAttributeValues, retrieve attribute: " + this.describeAttributes(attributeAddress));

        LOGGER.debug("Retrieving all attribute values for class id: {}, obis code: {}, attribute id: {}", CLASS_ID,
                OBIS_CODE, ATTRIBUTE_ID);

        final DataObject objectList = this.dlmsHelper.getAttributeValue(conn, attributeAddress);

        if (!objectList.isComplex()) {
            this.throwUnexpectedTypeProtocolAdapterException();
        }
        final List<DataObject> objectListElements = objectList.getValue();

        final List<ClassIdObisAttr> allObisCodes = this.getAllObisCodes(objectListElements);
        this.logAllObisCodes(allObisCodes);

        try {
            final String output = this.createOutput(conn, allObisCodes);

            LOGGER.debug("Total output is: {}", output);

            return output;
        } catch (final IOException | TimeoutException e) {
            throw new ConnectionException(e);
        }
    }

    private void logAllObisCodes(final List<ClassIdObisAttr> allObisCodes) {
        int index = 1;
        LOGGER.debug("List of all ObisCodes:");
        for (final ClassIdObisAttr obisAttr : allObisCodes) {
            LOGGER.debug("{}/{} {} #attr{}", index++, allObisCodes.size(), obisAttr.getObisCode().getValue(),
                    obisAttr.getNoAttr());
        }
    }

    private String createOutput(final DlmsConnectionHolder conn, final List<ClassIdObisAttr> allObisCodes)
            throws ProtocolAdapterException, IOException, TimeoutException {
        String output = "";
        int index = 1;
        for (final ClassIdObisAttr obisAttr : allObisCodes) {
            LOGGER.debug("Creating output for {} {}/{}", obisAttr.getObisCode().getValue(), index++,
                    allObisCodes.size());
            output += this.getAllDataFromObisCode(conn, obisAttr);
            LOGGER.debug("Length of output is now: {}", output.length());
        }
        return output;
    }

    private String getAllDataFromObisCode(final DlmsConnectionHolder conn, final ClassIdObisAttr obisAttr)
            throws ProtocolAdapterException, IOException, TimeoutException {
        String output = "";

        final int noOfAttr = obisAttr.getNoAttr();
        for (int attributeValue = 1; attributeValue <= noOfAttr; attributeValue++) {
            LOGGER.debug("Creating output for {} attr: {}/{}", obisAttr.getObisCode().getValue(), attributeValue,
                    noOfAttr);
            output += this.getAllDataFromAttribute(conn, obisAttr.getClassNumber(), obisAttr.getObisCode(),
                    attributeValue);
        }
        return output;
    }

    private String getAllDataFromAttribute(final DlmsConnectionHolder conn, final int classNumber,
            final DataObject obisCode, final int attributeValue)
                    throws ProtocolAdapterException, IOException, TimeoutException {

        if (!obisCode.isByteArray()) {
            this.throwUnexpectedTypeProtocolAdapterException();
        }

        final byte[] obisCodeByteArray = obisCode.getValue();
        if (obisCodeByteArray.length != OBIS_CODE_BYTE_ARRAY_LENGTH) {
            this.throwUnexpectedTypeProtocolAdapterException();
        }
        final AttributeAddress attributeAddress = new AttributeAddress(classNumber, new ObisCode(obisCodeByteArray),
                attributeValue);

        conn.getDlmsMessageListener().setDescription(
                "RetrieveAllAttributeValues, retrieve attribute: " + this.describeAttributes(attributeAddress));

        LOGGER.debug("Retrieving configuration objects data for class id: {}, obis code: {}, attribute id: {}",
                classNumber, obisCodeByteArray, attributeValue);
        final GetResult getResult = conn.getConnection().get(attributeAddress);

        LOGGER.debug("ResultCode: {}", getResult.getResultCode());

        return this.dlmsHelper.getDebugInfo(getResult.getResultData());
    }

    private List<ClassIdObisAttr> getAllObisCodes(final List<DataObject> objectListElements)
            throws ProtocolAdapterException {
        final List<ClassIdObisAttr> allObisCodes = new ArrayList<>();

        for (final DataObject objectListElement : objectListElements) {
            final List<DataObject> objectListElementValues = objectListElement.getValue();
            final ClassIdObisAttr classIdObisAttr = new ClassIdObisAttr(this.getClassId(objectListElementValues
                    .get(CLASS_ID_INDEX)), objectListElementValues.get(OBIS_CODE_INDEX),
                    this.getNoOffAttributes(objectListElementValues));

            allObisCodes.add(classIdObisAttr);
        }
        return allObisCodes;
    }

    private int getClassId(final DataObject dataObject) throws ProtocolAdapterException {
        if (DataObject.Type.LONG_UNSIGNED != dataObject.getType()) {
            this.throwUnexpectedTypeProtocolAdapterException();
        }
        return ((Number) dataObject.getValue()).intValue();
    }

    private void throwUnexpectedTypeProtocolAdapterException() throws ProtocolAdapterException {
        throw new ProtocolAdapterException("Unexpected type of element");
    }

    private int getNoOffAttributes(final List<DataObject> objectListElementValues) throws ProtocolAdapterException {
        final DataObject accessRights = objectListElementValues.get(ATTR_INDEX);
        if (!accessRights.isComplex()) {
            this.throwUnexpectedTypeProtocolAdapterException();
        }
        final List<DataObject> accessRightsValues = accessRights.getValue();
        final DataObject attributeAccess = accessRightsValues.get(0);
        if (!attributeAccess.isComplex()) {
            this.throwUnexpectedTypeProtocolAdapterException();
        }
        final List<DataObject> attributeAccessDescriptors = attributeAccess.getValue();
        return attributeAccessDescriptors.size();
    }

    private class ClassIdObisAttr {
        private final int classNumber;
        private final DataObject obisCode;
        private final int noAttr;

        public ClassIdObisAttr(final int classNumber, final DataObject obisCode, final int noAttr) {
            this.classNumber = classNumber;
            this.obisCode = obisCode;
            this.noAttr = noAttr;
        }

        public int getClassNumber() {
            return this.classNumber;
        }

        public DataObject getObisCode() {
            return this.obisCode;
        }

        public int getNoAttr() {
            return this.noAttr;
        }
    }
}
