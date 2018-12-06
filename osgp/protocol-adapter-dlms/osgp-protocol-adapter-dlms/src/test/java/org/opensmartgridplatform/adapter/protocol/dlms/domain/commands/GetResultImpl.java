/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
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
