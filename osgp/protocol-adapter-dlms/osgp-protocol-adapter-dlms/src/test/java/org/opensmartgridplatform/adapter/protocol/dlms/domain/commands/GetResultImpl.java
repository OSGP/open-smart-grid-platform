package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;

/** Basic GetResult implementation, for testing purposes only! */
class GetResultImpl implements GetResult {
    private DataObject resultData;

    public GetResultImpl(DataObject resultData) {
        this.resultData = resultData;
    }

    @Override
    public DataObject getResultData() {
        return this.resultData;
    }

    @Override
    public AccessResultCode getResultCode() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean requestSuccessful() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
