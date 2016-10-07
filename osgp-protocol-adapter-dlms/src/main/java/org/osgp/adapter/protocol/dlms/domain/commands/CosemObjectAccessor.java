package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.MethodParameter;
import org.openmuc.jdlms.MethodResult;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

class CosemObjectAccessor {

    private static final String EXCEPTION_MSG_ACCESS_RESULT_NOT_SUCCESS = "Access result not success: %s";
    private static final String EXCEPTION_MSG_WRITING_ATTRIBUTE = "An exception occurred while writing attribute %s";
    private static final String EXCEPTION_MSG_NO_METHOD_RESULT = "No MethodResult received.";
    private static final String EXCEPTION_MSG_NO_GET_RESULT = "No GetResult received while retrieving attribute %s.";
    private final DlmsConnection conn;
    private final ObisCode obisCode;
    private final int classId;

    public CosemObjectAccessor(final DlmsConnection conn, final ObisCode obisCode, final int classId) {
        this.conn = conn;
        this.obisCode = obisCode;
        this.classId = classId;
    }

    private AttributeAddress createAttributeAddress(final int attributeId) {
        return new AttributeAddress(this.classId, this.obisCode, attributeId);
    }

    private MethodParameter createMethodParameter(final int methodId, final DataObject dataObject) {
        return new MethodParameter(this.classId, this.obisCode, methodId, dataObject);
    }

    private MethodParameter createMethodParameter(final int methodId) {
        return new MethodParameter(this.classId, this.obisCode, methodId);
    }

    public DataObject readAttribute(final int attributeId) throws ProtocolAdapterException {
        GetResult getResult = null;
        try {
            getResult = this.conn.get(this.createAttributeAddress(attributeId));
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }

        if (getResult == null) {
            throw new ProtocolAdapterException(String.format(EXCEPTION_MSG_NO_GET_RESULT, attributeId));
        }

        return getResult.getResultData();
    }

    public void writeAttribute(final int attributeId, final DataObject data) throws ProtocolAdapterException {
        final AttributeAddress attributeAddress = new AttributeAddress(this.classId, this.obisCode, attributeId);
        final SetParameter setParameter = new SetParameter(attributeAddress, data);

        AccessResultCode accessResultCode = null;
        try {
            accessResultCode = this.conn.set(setParameter);
        } catch (final IOException e) {
            throw new ProtocolAdapterException(String.format(EXCEPTION_MSG_WRITING_ATTRIBUTE, attributeId));
        }

        if (accessResultCode != AccessResultCode.SUCCESS) {
            throw new ProtocolAdapterException(String.format(EXCEPTION_MSG_ACCESS_RESULT_NOT_SUCCESS,
                    accessResultCode.name()));
        }
    }

    public MethodResultCode callMethod(final int methodId) throws ProtocolAdapterException {
        final MethodParameter methodParameter = this.createMethodParameter(methodId);
        return this.handleMethod(methodParameter);
    }

    public MethodResultCode callMethod(final int methodId, final DataObject dataObject) throws ProtocolAdapterException {
        final MethodParameter methodParameter = this.createMethodParameter(methodId, dataObject);
        return this.handleMethod(methodParameter);
    }

    private MethodResultCode handleMethod(final MethodParameter methodParameter) throws ProtocolAdapterException {
        MethodResult result = null;
        try {
            result = this.conn.action(methodParameter);
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }

        if (result == null) {
            throw new ProtocolAdapterException(EXCEPTION_MSG_NO_METHOD_RESULT);
        }

        return result.getResultCode();
    }
}
