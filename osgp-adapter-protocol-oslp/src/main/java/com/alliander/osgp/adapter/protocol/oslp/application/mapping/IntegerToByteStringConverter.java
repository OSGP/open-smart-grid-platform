package com.alliander.osgp.adapter.protocol.oslp.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.oslp.OslpUtils;
import com.google.protobuf.ByteString;

public class IntegerToByteStringConverter extends BidirectionalConverter<Integer, ByteString> {

    @Override
    public Integer convertFrom(final ByteString source, final Type<Integer> destinationType) {
        return OslpUtils.byteStringToInteger(source);
    }

    @Override
    public ByteString convertTo(final Integer source, final Type<ByteString> destinationType) {
        return OslpUtils.integerToByteString(source);
    }
}
