/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AccessRight;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AccessSelectorList;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AssociationLnListElement;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AssociationLnListType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AttributeAccessDescriptor;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AttributeAccessItem;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AttributeAccessModeType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemObisCode;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MethodAccessDescriptor;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MethodAccessItem;
import com.alliander.osgp.dto.valueobjects.smartmetering.AccessRightDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AccessSelectorListDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AssociationLnListElementDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AssociationLnListTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AttributeAccessDescriptorDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AttributeAccessItemDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AttributeAccessModeTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCodeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MethodAccessDescriptorDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MethodAccessItemDto;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

public class AssociationLnListTypeMappingTest {

    private ConfigurationMapper configurationMapper = new ConfigurationMapper();

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();;

    @Before
    public void setup() {
        this.configurationMapper.configure(this.mapperFactory);
    }

    // To test if a AssociationLnListElement can be mapped if instance variables are
    // initialized and lists are empty.
    @Test
    public void testAssociationLnListTypeMappingWithEmptyLists() {

        // build test data
        final List<AssociationLnListElement> associationLnListElement = new ArrayList<>();
        final AssociationLnListType associationLnListType = new AssociationLnListTypeBuilder()
                .withEmptyLists(associationLnListElement).build();

        // actual mapping
        final AssociationLnListTypeDto associationLnListTypeDto = this.mapperFactory.getMapperFacade()
                .map(associationLnListType, AssociationLnListTypeDto.class);

        // check values
        this.checkAssociationLnListElementMapping(associationLnListType.getAssociationLnListElement(),
                associationLnListTypeDto.getAssociationLnListElement());

        assertNotNull(associationLnListTypeDto);
    }

    // To test if a AssociationLnListElementDto can be mapped if instance variables
    // are initialized and lists are empty.
    @Test
    public void testAssociationLnListTypeDtoMappingWithEmptyLists() {

        // build test data
        final List<AssociationLnListElementDto> associationLnListElementDto = new ArrayList<>();
        final AssociationLnListTypeDto associationLnListTypeDto = new AssociationLnListTypeDto(
                associationLnListElementDto);

        // actual mapping
        final AssociationLnListType associationLnListType = this.mapperFactory.getMapperFacade()
                .map(associationLnListTypeDto, AssociationLnListType.class);

        // check values
        this.checkAssociationLnListElementMapping(associationLnListType.getAssociationLnListElement(),
                associationLnListTypeDto.getAssociationLnListElement());

        assertNotNull(associationLnListType);
    }

    // To test if a AssociationLnListElement can be mapped if instance variables are
    // initialized and lists are empty.
    @Test
    public void testAssociationLnListTypeMappingWithNonEmptyLists() {

        // build test data
        final List<AttributeAccessItem> listAttributeAccessItem = new ArrayList<>();
        final AttributeAccessModeType accessMode = AttributeAccessModeType.NO_ACCESS;
        final List<Integer> listInteger = new ArrayList<>();
        final AccessSelectorList accessSelectors = new AccessSelectorList(listInteger);
        listAttributeAccessItem.add(new AttributeAccessItem(1, accessMode, accessSelectors));
        final AttributeAccessDescriptor attributeAccessDescriptor = new AttributeAccessDescriptor(
                listAttributeAccessItem);

        final List<MethodAccessItem> listMethodAccessItem = new ArrayList<>();
        final MethodAccessDescriptor methodAccessDescriptor = new MethodAccessDescriptor(listMethodAccessItem);

        final AssociationLnListElement associationLnElement = new AssociationLnListElement(72, 2,
                new CosemObisCode(new int[] { 1, 1, 1, 1, 1, 1 }),
                new AccessRight(attributeAccessDescriptor, methodAccessDescriptor));

        final AssociationLnListType associationLnListType = new AssociationLnListTypeBuilder()
                .withFilledLists(associationLnElement).build();

        // actual mapping
        final AssociationLnListTypeDto associationLnListTypeDto = this.mapperFactory.getMapperFacade()
                .map(associationLnListType, AssociationLnListTypeDto.class);

        // check values
        assertNotNull(associationLnListTypeDto);
        this.checkAssociationLnListElementMapping(associationLnListType.getAssociationLnListElement(),
                associationLnListTypeDto.getAssociationLnListElement());
    }

    // To test if a AssociationLnListElementDto can be mapped if instance variables
    // are initialized and lists are empty.
    @Test
    public void testAssociationLnListTypeDtoMappingWithNonEmptyLists() {
        // build test data
        final List<AttributeAccessItemDto> listAttributeAccessItemDto = new ArrayList<>();
        final AttributeAccessModeTypeDto accessMode = AttributeAccessModeTypeDto.NO_ACCESS;
        final List<Integer> listInteger = new ArrayList<>();
        final AccessSelectorListDto accessSelectors = new AccessSelectorListDto(listInteger);
        listAttributeAccessItemDto.add(new AttributeAccessItemDto(1, accessMode, accessSelectors));
        final AttributeAccessDescriptorDto attributeAccessDescriptorDto = new AttributeAccessDescriptorDto(
                listAttributeAccessItemDto);

        final List<MethodAccessItemDto> listMethodAccessItemDto = new ArrayList<>();
        final MethodAccessDescriptorDto methodAccessDescriptorDto = new MethodAccessDescriptorDto(
                listMethodAccessItemDto);

        final AssociationLnListElementDto associationLnElementDto = new AssociationLnListElementDto(72, 2,
                new CosemObisCodeDto(new int[] { 1, 1, 1, 1, 1, 1 }),
                new AccessRightDto(attributeAccessDescriptorDto, methodAccessDescriptorDto));

        final AssociationLnListTypeDto associationLnListTypeDto = new AssociationLnListTypeDtoBuilder()
                .withFilledArguments(associationLnElementDto).build();

        // actual mapping
        final AssociationLnListType associationLnListType = this.mapperFactory.getMapperFacade()
                .map(associationLnListTypeDto, AssociationLnListType.class);

        // check values
        assertNotNull(associationLnListType);
        this.checkAssociationLnListElementMapping(associationLnListType.getAssociationLnListElement(),
                associationLnListTypeDto.getAssociationLnListElement());
    }

    // method to test AssociationLnListElement object mapping
    private void checkAssociationLnListElementMapping(final List<AssociationLnListElement> associationLnListElements,
            final List<AssociationLnListElementDto> associationLnListElementDtos) {

        // make sure neither is null
        assertNotNull(associationLnListElements);
        assertNotNull(associationLnListElementDtos);

        // make sure all instance variables are equal
        assertEquals(associationLnListElements.size(), associationLnListElementDtos.size());
        for (int i = 0; i < associationLnListElements.size(); i++) {
            this.checkElement(associationLnListElements.get(i), associationLnListElementDtos.get(i));
        }
    }

    private void checkElement(final AssociationLnListElement associationLnListElement,
            final AssociationLnListElementDto associationLnListElementDto) {

        assertEquals(associationLnListElement.getClassId(), associationLnListElementDto.getClassId());
        assertEquals(associationLnListElement.getVersion(), associationLnListElementDto.getVersion());
        this.checkAccessRights(associationLnListElement.getAccessRights(),
                associationLnListElementDto.getAccessRights());
        this.checkCosemObisCode(associationLnListElement.getLogicalName(),
                associationLnListElementDto.getLogicalName());
    }

    private void checkAccessRights(final AccessRight accessRight, final AccessRightDto accessRightDto) {
        this.checkAttributeAccess(accessRight.getAttributeAccess(), accessRightDto.getAttributeAccess());
        this.checkMethodAccess(accessRight.getMethodAccess(), accessRightDto.getMethodAccess());
    }

    private void checkAttributeAccess(final AttributeAccessDescriptor attributeAccess,
            final AttributeAccessDescriptorDto attributeAccessDto) {
        this.checkAttributeAccessItem(attributeAccess.getAttributeAccessItem(),
                attributeAccessDto.getAttributeAccessItem());
    }

    private void checkAttributeAccessItem(final List<AttributeAccessItem> attributeAccessItem,
            final List<AttributeAccessItemDto> attributeAccessItemDto) {
        assertEquals(attributeAccessItem.size(), attributeAccessItemDto.size());
    }

    private void checkMethodAccess(final MethodAccessDescriptor methodAccess,
            final MethodAccessDescriptorDto methodAccessDto) {
        this.checkMethodAccessItem(methodAccess.getMethodAccessItem(), methodAccessDto.getMethodAccessItem());
    }

    private void checkMethodAccessItem(final List<MethodAccessItem> methodAccessItem,
            final List<MethodAccessItemDto> methodAccessItemDto) {
        assertEquals(methodAccessItem.size(), methodAccessItemDto.size());
    }

    private void checkCosemObisCode(final CosemObisCode logicalName, final CosemObisCodeDto logicalNameDto) {
        assertEquals(logicalName.getA(), logicalNameDto.getA());
        assertEquals(logicalName.getB(), logicalNameDto.getB());
        assertEquals(logicalName.getC(), logicalNameDto.getC());
        assertEquals(logicalName.getD(), logicalNameDto.getD());
        assertEquals(logicalName.getE(), logicalNameDto.getE());
        assertEquals(logicalName.getF(), logicalNameDto.getF());
    }
}
