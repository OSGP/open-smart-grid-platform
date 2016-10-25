/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

class CosemObjectAccessor {

    private static final String EXCEPTION_MSG_ACCESS_RESULT_NOT_SUCCESS = "Access result not success but '%s'  while writing attribute %s, classId %s, obisCode %s.";
    private static final String EXCEPTION_MSG_WRITING_ATTRIBUTE = "An exception occurred while writing attribute %s, classId %s, obisCode %s.";
    private static final String EXCEPTION_MSG_NO_METHOD_RESULT = "No MethodResult received.";
    private static final String EXCEPTION_MSG_NO_GET_RESULT = "No GetResult received while retrieving attribute %s, classId %s, obisCode %s.";

    private final DlmsConnectionHolder connector;
    private final ObisCode obisCode;
    private final int classId;

    public CosemObjectAccessor(final DlmsConnectionHolder connector, final ObisCode obisCode, final int classId) {
        this.connector = connector;
        this.obisCode = obisCode;
        this.classId = classId;
    }

    public DataObject readAttribute(final CosemObjectAttribute attributeId) throws ProtocolAdapterException {
        GetResult getResult = null;
        try {
            getResult = this.connector.getConnection().get(this.createAttributeAddress(attributeId));
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }

        if (getResult == null) {
            throw new ProtocolAdapterException(String.format(EXCEPTION_MSG_NO_GET_RESULT, attributeId, this.classId,
                    this.obisCode));
        }

        return getResult.getResultData();
    }

    public void writeAttribute(final CosemObjectAttribute attributeId, final DataObject data)
            throws ProtocolAdapterException {
        final AttributeAddress attributeAddress = this.createAttributeAddress(attributeId);
        final SetParameter setParameter = new SetParameter(attributeAddress, data);

        AccessResultCode accessResultCode = null;
        try {
            accessResultCode = this.connector.getConnection().set(setParameter);
        } catch (final IOException e) {
            throw new ProtocolAdapterException(String.format(EXCEPTION_MSG_WRITING_ATTRIBUTE, attributeId,
                    this.classId, this.obisCode), e);
        }

        if (accessResultCode != AccessResultCode.SUCCESS) {
            throw new ProtocolAdapterException(String.format(EXCEPTION_MSG_ACCESS_RESULT_NOT_SUCCESS,
                    accessResultCode.name(), attributeId, this.classId, this.obisCode));
        }
    }

    public MethodResultCode callMethod(final CosemObjectMethod methodId) throws ProtocolAdapterException {
        final MethodParameter methodParameter = this.createMethodParameter(methodId.getValue());
        return this.handleMethod(methodParameter);
    }

    public MethodResultCode callMethod(final CosemObjectMethod methodId, final DataObject dataObject)
            throws ProtocolAdapterException {
        final MethodParameter methodParameter = this.createMethodParameter(methodId.getValue(), dataObject);
        return this.handleMethod(methodParameter);
    }

    AttributeAddress createAttributeAddress(final CosemObjectAttribute attributeId) {
        return new AttributeAddress(this.classId, this.obisCode, attributeId.getValue());
    }

    private MethodParameter createMethodParameter(final int methodId, final DataObject dataObject) {
        return new MethodParameter(this.classId, this.obisCode, methodId, dataObject);
    }

    private MethodParameter createMethodParameter(final int methodId) {
        return new MethodParameter(this.classId, this.obisCode, methodId);
    }

    private MethodResultCode handleMethod(final MethodParameter methodParameter) throws ProtocolAdapterException {
        MethodResult result = null;
        try {
            result = this.connector.getConnection().action(methodParameter);
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }

        if (result == null) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_NO_METHOD_RESULT);
        }

        return result.getResultCode();
    }
}
