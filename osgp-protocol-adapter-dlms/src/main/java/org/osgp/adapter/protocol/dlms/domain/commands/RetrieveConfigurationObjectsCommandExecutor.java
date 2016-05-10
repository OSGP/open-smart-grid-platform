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

@Component
public class RetrieveConfigurationObjectsCommandExecutor implements CommandExecutor<DataObject, String> {

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

    private static final Logger LOGGER = LoggerFactory.getLogger(RetrieveConfigurationObjectsCommandExecutor.class);

    @Override
    public String execute(final ClientConnection conn, final DlmsDevice device, final DataObject object)
            throws ProtocolAdapterException {

        final AttributeAddress attributeAddress = new AttributeAddress(CLASS_ID, OBIS_CODE, ATTRIBUTE_ID);

        LOGGER.debug("Retrieving configuration objects for class id: {}, obis code: {}, attribute id: {}", CLASS_ID,
                OBIS_CODE, ATTRIBUTE_ID);

        final List<GetResult> getResultList = this.dlmsHelper.getAndCheck(conn, device,
                "Retrieving configuration objects for class", attributeAddress);

        final DataObject resultData = getResultList.get(0).resultData();
        if (!resultData.isComplex()) {
            this.throwUnexpectedTypeProtocolAdapterException();
        }
        // The check here above "!resultData.isComplex()" garantees that can be
        // cast to a List.
        @SuppressWarnings("unchecked")
        final List<DataObject> resultDataValue = (List<DataObject>) getResultList.get(0).resultData().value();

        final List<ClassIdObisAttr> allObisCodes = this.getAllObisCodes(resultDataValue);
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
            LOGGER.debug("{}/{} {} #attr{}", index++, allObisCodes.size(), obisAttr.getObisCode().value(),
                    obisAttr.getNoAttr());
        }
    }

    private String createOutput(final ClientConnection conn, final List<ClassIdObisAttr> allObisCodes)
            throws ProtocolAdapterException, IOException, TimeoutException {
        String output = "";
        int index = 1;
        for (final ClassIdObisAttr obisAttr : allObisCodes) {
            LOGGER.debug("Creating output for {} {}/{}", obisAttr.getObisCode().value(), index++, allObisCodes.size());
            output += this.getAllDataFromObisCode(conn, obisAttr);
            LOGGER.debug("Length of output is now: {}", output.length());
        }
        return output;
    }

    private String getAllDataFromObisCode(final ClientConnection conn, final ClassIdObisAttr obisAttr)
            throws ProtocolAdapterException, IOException, TimeoutException {
        String output = "";

        final int noOfAttr = obisAttr.getNoAttr();
        for (int attributeValue = 1; attributeValue <= noOfAttr; attributeValue++) {
            LOGGER.debug("Creating output for {} attr: {}/{}", obisAttr.getObisCode().value(), attributeValue, noOfAttr);
            output += this.getAllDataFromAttribute(conn, obisAttr.getClassNumber(), obisAttr.getObisCode(),
                    attributeValue);
        }
        return output;
    }

    private String getAllDataFromAttribute(final ClientConnection conn, final int classNumber,
            final DataObject obisCode, final int attributeValue) throws ProtocolAdapterException, IOException,
            TimeoutException {

        if (!obisCode.isByteArray()) {
            this.throwUnexpectedTypeProtocolAdapterException();
        }

        final byte[] obisCodeByteArray = obisCode.value();
        if (obisCodeByteArray.length != OBIS_CODE_BYTE_ARRAY_LENGTH) {
            this.throwUnexpectedTypeProtocolAdapterException();
        }
        final AttributeAddress attributeAddress = new AttributeAddress(classNumber,
                this.createObisCode(obisCodeByteArray), attributeValue);

        LOGGER.debug("Retrieving configuration objects data for class id: {}, obis code: {}, attribute id: {}",
                classNumber, obisCodeByteArray, attributeValue);
        final List<GetResult> getResultList = conn.get(attributeAddress);

        final GetResult getResult = getResultList.get(0);
        LOGGER.debug("ResultCode: {}", getResult.resultCode());

        return this.dlmsHelper.getDebugInfo(getResult.resultData());
    }

    private ObisCode createObisCode(final byte[] obisCodeByteArray) {
        return new ObisCode(obisCodeByteArray[0], obisCodeByteArray[1], obisCodeByteArray[2], obisCodeByteArray[3],
                obisCodeByteArray[4], obisCodeByteArray[5]);
    }

    private List<ClassIdObisAttr> getAllObisCodes(final List<DataObject> obisCodeMetaDataTree)
            throws ProtocolAdapterException {
        final List<ClassIdObisAttr> allObisCodes = new ArrayList<>();

        for (final DataObject obisCodeMetaData : obisCodeMetaDataTree) {
            final List<DataObject> obisCodeMetaDataList = (List<DataObject>) obisCodeMetaData.value();
            final ClassIdObisAttr classIdObisAttr = new ClassIdObisAttr(this.getClassNumber(obisCodeMetaDataList
                    .get(CLASS_ID_INDEX)), obisCodeMetaDataList.get(OBIS_CODE_INDEX),
                    this.getNoOffAttributes(obisCodeMetaDataList));

            allObisCodes.add(classIdObisAttr);
        }
        return allObisCodes;
    }

    private int getClassNumber(final DataObject dataObject) {
        // is long unsi
        return (int) dataObject.value();
    }

    private void throwUnexpectedTypeProtocolAdapterException() throws ProtocolAdapterException {
        throw new ProtocolAdapterException("Unexpected type of element");
    }

    private int getNoOffAttributes(final List<DataObject> obisCodeMetaDataList) throws ProtocolAdapterException {
        final DataObject element3 = obisCodeMetaDataList.get(ATTR_INDEX);
        if (!element3.isComplex()) {
            this.throwUnexpectedTypeProtocolAdapterException();
        }
        final List<DataObject> attributesList = (List) element3.value();
        final DataObject attributes = attributesList.get(0);
        if (!attributes.isComplex()) {
            this.throwUnexpectedTypeProtocolAdapterException();
        }
        final List<DataObject> listValue = attributes.value();
        return listValue.size();
    }

    private class ClassIdObisAttr {
        private int classNumber;
        private DataObject obisCode;
        private int noAttr;

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
