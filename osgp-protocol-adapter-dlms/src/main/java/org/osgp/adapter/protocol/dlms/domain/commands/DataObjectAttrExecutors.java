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
import java.util.List;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.SetParameter;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains all information to be used when setting a {@link SetParameter} to
 * the device. Further the execute method, which needs a live
 * {@link DlmsConnection}, will do the actual call to the device.
 */
public class DataObjectAttrExecutors {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataObjectAttrExecutors.class);

    private final List<DataObjectAttrExecutor> dataObjectAttrExecutorList;
    private String errString;
    private boolean containsError = false;

    public DataObjectAttrExecutors() {

        this.dataObjectAttrExecutorList = new ArrayList<DataObjectAttrExecutor>();

    }

    /**
     * @param conn
     *            : the active {@link DlmsConnection} to send the
     *            {@link SetParameter} to.
     * @throws IOException
     *             is thrown when an error occurs with the connection to the
     *             dlms device
     */
    public void execute(DlmsConnection conn) throws IOException {

        try {
            for (final DataObjectAttrExecutor dataObjectAttrExecutor : this.dataObjectAttrExecutorList) {
                if (AccessResultCode.SUCCESS != dataObjectAttrExecutor.executeSet(conn)) {
                    this.errString += dataObjectAttrExecutor.createRequestAndResultCodeInfo();
                    this.containsError = true;
                }
            }
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }
    }

    public void addExecutor(DataObjectAttrExecutor executor) {
        this.dataObjectAttrExecutorList.add(executor);
    }

    public List<DataObjectAttrExecutor> getDataObjectAttrExecutorList() {
        return this.dataObjectAttrExecutorList;
    }

    public String getErrString() {
        return this.errString;
    }

    public boolean isContainsError() {
        return this.containsError;
    }

}
