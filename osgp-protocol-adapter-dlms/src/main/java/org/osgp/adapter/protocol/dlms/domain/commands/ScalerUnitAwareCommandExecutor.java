/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.ClientConnection;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ScalerUnit;
import com.alliander.osgp.dto.valueobjects.smartmetering.ScalerUnitResponse;

/**
 * interface for command executors that should provide scaler unit information.
 * Implementations should retrieve scaler unit information by providing multiple
 * (two) AttributeAddresses to
 * {@link ClientConnection#get(org.openmuc.jdlms.AttributeAddress...)}.
 *
 * @param <R>
 *            a response from which scaler unit information can be retrieved
 */
public interface ScalerUnitAwareCommandExecutor<T, R extends ScalerUnitResponse> extends CommandExecutor<T, R> {

    /**
     * the attribute address to scaler unit information on the dlms device
     *
     * @see ClientConnection#get(org.openmuc.jdlms.AttributeAddress...)
     * @return the address of the scaler / unit to be used
     */
    AttributeAddress getScalerUnitAttributeAddress(T t) throws ProtocolAdapterException;

    /**
     * convert a data object holding scaler and unit, the data object argument
     * is the result of the query with {@link #getScalerUnitAttributeAddress()
     * the attribute address. The data object should be the container for 2 data
     * objects, one holding scaler, the other unit info.
     *
     * @see DataObject#isComplex()
     * @param dataObject
     * @return the scaler / unit of the value(s) retrieved
     */
    ScalerUnit convert(DataObject dataObject) throws ProtocolAdapterException;

}
