/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper;

import java.util.Date;

import org.apache.commons.lang3.NotImplementedException;
import com.beanit.openiec61850.FcModelNode;

public final class ReadOnlyNodeContainer extends NodeContainer {

    private static final String NOT_SUPPORTED = "Writing is not supported";

    public ReadOnlyNodeContainer(final String deviceIdentification, final FcModelNode fcmodelNode) {
        super(deviceIdentification, fcmodelNode);
    }

    @Override
    public void write() {
        throw new NotImplementedException(NOT_SUPPORTED);
    }

    @Override
    public void writeBoolean(final SubDataAttribute child, final boolean value) {
        throw new NotImplementedException(NOT_SUPPORTED);
    }

    @Override
    public void writeByte(final SubDataAttribute child, final byte value) {
        throw new NotImplementedException(NOT_SUPPORTED);
    }

    @Override
    public void writeDate(final SubDataAttribute child, final Date value) {
        throw new NotImplementedException(NOT_SUPPORTED);
    }

    @Override
    public void writeFloat(final SubDataAttribute child, final Float value) {
        throw new NotImplementedException(NOT_SUPPORTED);
    }

    @Override
    public void writeInteger(final SubDataAttribute child, final Integer value) {
        throw new NotImplementedException(NOT_SUPPORTED);
    }

    @Override
    public void writeShort(final SubDataAttribute child, final Short value) {
        throw new NotImplementedException(NOT_SUPPORTED);
    }

    @Override
    public void writeString(final SubDataAttribute child, final String value) {
        throw new NotImplementedException(NOT_SUPPORTED);
    }

    @Override
    public void writeUnsignedShort(final SubDataAttribute child, final Integer value) {
        throw new NotImplementedException(NOT_SUPPORTED);
    }

    @Override
    public NodeContainer getChild(final SubDataAttribute child) {
        return new ReadOnlyNodeContainer(this.deviceIdentification,
                (FcModelNode) this.parent.getChild(child.getDescription()));
    }

    @Override
    public NodeContainer getChild(final String child) {
        return new ReadOnlyNodeContainer(this.deviceIdentification, (FcModelNode) this.parent.getChild(child));
    }
}
