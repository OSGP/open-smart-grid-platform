/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.elster.application.mapping;

import com.alliander.osgp.dto.valueobjects.LinkTypeDto;
import com.alliander.osgp.oslp.Oslp;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class LinkTypeConverter extends BidirectionalConverter<LinkTypeDto, Oslp.LinkType> {

    @Override
    public com.alliander.osgp.oslp.Oslp.LinkType convertTo(final LinkTypeDto source,
            final Type<com.alliander.osgp.oslp.Oslp.LinkType> destinationType, final MappingContext context) {
        if (source == null) {
            return null;
        }

        return Oslp.LinkType.valueOf(source.toString());
    }

    @Override
    public LinkTypeDto convertFrom(final com.alliander.osgp.oslp.Oslp.LinkType source,
            final Type<LinkTypeDto> destinationType, final MappingContext context) {
        if (source == null || source == Oslp.LinkType.LINK_NOT_SET) {
            return null;
        }

        return LinkTypeDto.valueOf(source.toString());
    }

}
