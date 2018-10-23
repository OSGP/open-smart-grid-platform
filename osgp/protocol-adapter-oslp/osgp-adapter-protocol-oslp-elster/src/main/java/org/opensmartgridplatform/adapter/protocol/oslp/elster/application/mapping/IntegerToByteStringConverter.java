/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.mapping;

import org.opensmartgridplatform.oslp.OslpUtils;
import com.google.protobuf.ByteString;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class IntegerToByteStringConverter extends BidirectionalConverter<Integer, ByteString> {

    @Override
    public Integer convertFrom(final ByteString source, final Type<Integer> destinationType,
            final MappingContext context) {
        return OslpUtils.byteStringToInteger(source);
    }

    @Override
    public ByteString convertTo(final Integer source, final Type<ByteString> destinationType,
            final MappingContext context) {
        return OslpUtils.integerToByteString(source);
    }
}
