/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.dto.valueobjects.LinkType;
import com.alliander.osgp.oslp.Oslp;

public class LinkTypeConverter extends BidirectionalConverter<LinkType, Oslp.LinkType> {

    @Override
    public com.alliander.osgp.oslp.Oslp.LinkType convertTo(final LinkType source,
            final Type<com.alliander.osgp.oslp.Oslp.LinkType> destinationType) {
        if (source == null) {
            return null;
        }

        return Oslp.LinkType.valueOf(source.toString());
    }

    @Override
    public LinkType convertFrom(final com.alliander.osgp.oslp.Oslp.LinkType source, final Type<LinkType> destinationType) {
        if (source == null || source == Oslp.LinkType.LINK_NOT_SET) {
            return null;
        }

        return LinkType.valueOf(source.toString());
    }

}
