/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AccessRight;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AccessSelectorList;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AssociationLnListElement;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AssociationLnListType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AttributeAccessDescriptor;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AttributeAccessItem;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AttributeAccessModeType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemObisCode;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.MethodAccessDescriptor;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.MethodAccessItem;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AccessRightDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AccessSelectorListDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AssociationLnListElementDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AssociationLnListTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AttributeAccessDescriptorDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AttributeAccessItemDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AttributeAccessModeTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObisCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MethodAccessDescriptorDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MethodAccessItemDto;

public class AssociationLnListTypeMappingTest {

  private final ConfigurationMapper configurationMapper = new ConfigurationMapper();

  @Test
  public void testAssociationLnListTypeMappingWithEmptyLists() {

    // build test data
    final AssociationLnListType associationLnListType =
        new AssociationLnListTypeBuilder().withEmptyLists().build();

    // actual mapping
    final AssociationLnListTypeDto associationLnListTypeDto =
        this.configurationMapper.map(associationLnListType, AssociationLnListTypeDto.class);

    // check values
    assertThat(associationLnListTypeDto).isNotNull();
    this.checkAssociationLnListElementMapping(
        associationLnListType.getAssociationLnListElement(),
        associationLnListTypeDto.getAssociationLnListElement());
  }

  @Test
  public void testAssociationLnListTypeDtoMappingWithEmptyLists() {

    // build test data
    final AssociationLnListTypeDto associationLnListTypeDto =
        new AssociationLnListTypeDtoBuilder().withEmptyLists().build();

    // actual mapping
    final AssociationLnListType associationLnListType =
        this.configurationMapper.map(associationLnListTypeDto, AssociationLnListType.class);

    // check values
    assertThat(associationLnListType).isNotNull();

    this.checkAssociationLnListElementMapping(
        associationLnListType.getAssociationLnListElement(),
        associationLnListTypeDto.getAssociationLnListElement());
  }

  @Test
  public void testAssociationLnListTypeMappingWithNonEmptyLists() {

    // build test data
    final List<AttributeAccessItem> listAttributeAccessItem = new ArrayList<>();
    final AttributeAccessModeType accessMode = AttributeAccessModeType.NO_ACCESS;
    final List<Integer> listInteger = new ArrayList<>();
    final AccessSelectorList accessSelectors = new AccessSelectorList(listInteger);
    listAttributeAccessItem.add(new AttributeAccessItem(1, accessMode, accessSelectors));
    final AttributeAccessDescriptor attributeAccessDescriptor =
        new AttributeAccessDescriptor(listAttributeAccessItem);

    final List<MethodAccessItem> listMethodAccessItem = new ArrayList<>();
    final MethodAccessDescriptor methodAccessDescriptor =
        new MethodAccessDescriptor(listMethodAccessItem);

    final AssociationLnListElement associationLnElement =
        new AssociationLnListElement(
            72,
            2,
            new CosemObisCode(new int[] {1, 1, 1, 1, 1, 1}),
            new AccessRight(attributeAccessDescriptor, methodAccessDescriptor));

    final AssociationLnListType associationLnListType =
        new AssociationLnListTypeBuilder().withNonEmptyLists(associationLnElement).build();

    // actual mapping
    final AssociationLnListTypeDto associationLnListTypeDto =
        this.configurationMapper.map(associationLnListType, AssociationLnListTypeDto.class);

    // check values
    assertThat(associationLnListTypeDto).isNotNull();

    this.checkAssociationLnListElementMapping(
        associationLnListType.getAssociationLnListElement(),
        associationLnListTypeDto.getAssociationLnListElement());
  }

  @Test
  public void testAssociationLnListTypeDtoMappingWithNonEmptyLists() {

    // build test data
    final List<AttributeAccessItemDto> listAttributeAccessItemDto = new ArrayList<>();
    final AttributeAccessModeTypeDto accessMode = AttributeAccessModeTypeDto.NO_ACCESS;
    final List<Integer> listInteger = new ArrayList<>();
    final AccessSelectorListDto accessSelectors = new AccessSelectorListDto(listInteger);
    listAttributeAccessItemDto.add(new AttributeAccessItemDto(1, accessMode, accessSelectors));
    final AttributeAccessDescriptorDto attributeAccessDescriptorDto =
        new AttributeAccessDescriptorDto(listAttributeAccessItemDto);

    final List<MethodAccessItemDto> listMethodAccessItemDto = new ArrayList<>();
    final MethodAccessDescriptorDto methodAccessDescriptorDto =
        new MethodAccessDescriptorDto(listMethodAccessItemDto);

    final AssociationLnListElementDto associationLnElementDto =
        new AssociationLnListElementDto(
            72,
            2,
            new CosemObisCodeDto(new int[] {1, 1, 1, 1, 1, 1}),
            new AccessRightDto(attributeAccessDescriptorDto, methodAccessDescriptorDto));

    final AssociationLnListTypeDto associationLnListTypeDto =
        new AssociationLnListTypeDtoBuilder().withNonEmptyLists(associationLnElementDto).build();

    // actual mapping
    final AssociationLnListType associationLnListType =
        this.configurationMapper.map(associationLnListTypeDto, AssociationLnListType.class);

    // check values
    assertThat(associationLnListType).isNotNull();

    this.checkAssociationLnListElementMapping(
        associationLnListType.getAssociationLnListElement(),
        associationLnListTypeDto.getAssociationLnListElement());
  }

  private void checkAssociationLnListElementMapping(
      final List<AssociationLnListElement> associationLnListElements,
      final List<AssociationLnListElementDto> associationLnListElementDtos) {

    assertThat(associationLnListElements).isNotNull();
    assertThat(associationLnListElementDtos).isNotNull();

    assertThat(associationLnListElementDtos.size()).isEqualTo(associationLnListElements.size());
    for (int i = 0; i < associationLnListElements.size(); i++) {
      this.checkElement(associationLnListElements.get(i), associationLnListElementDtos.get(i));
    }
  }

  private void checkElement(
      final AssociationLnListElement associationLnListElement,
      final AssociationLnListElementDto associationLnListElementDto) {

    assertThat(associationLnListElementDto.getClassId())
        .isEqualTo(associationLnListElement.getClassId());
    assertThat(associationLnListElementDto.getVersion())
        .isEqualTo(associationLnListElement.getVersion());
    this.checkAccessRights(
        associationLnListElement.getAccessRights(), associationLnListElementDto.getAccessRights());
    this.checkCosemObisCode(
        associationLnListElement.getLogicalName(), associationLnListElementDto.getLogicalName());
  }

  private void checkAccessRights(
      final AccessRight accessRight, final AccessRightDto accessRightDto) {
    this.checkAttributeAccess(
        accessRight.getAttributeAccess(), accessRightDto.getAttributeAccess());
    this.checkMethodAccess(accessRight.getMethodAccess(), accessRightDto.getMethodAccess());
  }

  private void checkAttributeAccess(
      final AttributeAccessDescriptor attributeAccess,
      final AttributeAccessDescriptorDto attributeAccessDto) {
    this.checkAttributeAccessItem(
        attributeAccess.getAttributeAccessItem(), attributeAccessDto.getAttributeAccessItem());
  }

  private void checkAttributeAccessItem(
      final List<AttributeAccessItem> attributeAccessItemList,
      final List<AttributeAccessItemDto> attributeAccessItemDtoList) {
    assertThat(attributeAccessItemDtoList.size()).isEqualTo(attributeAccessItemList.size());

    final Iterator<AttributeAccessItem> attributeAccessItemIterator =
        attributeAccessItemList.iterator();
    final Iterator<AttributeAccessItemDto> attributeAccessItemDtoIterator =
        attributeAccessItemDtoList.iterator();
    while (attributeAccessItemIterator.hasNext() && attributeAccessItemDtoIterator.hasNext()) {
      final AttributeAccessItem attributeAccessItem = attributeAccessItemIterator.next();
      final AttributeAccessItemDto attributeAccessItemDto = attributeAccessItemDtoIterator.next();
      assertThat(attributeAccessItemDto.getAccessMode().name())
          .isEqualTo(attributeAccessItem.getAccessMode().name());
      assertThat(attributeAccessItemDto.getAccessSelectors().getAccessSelector())
          .isEqualTo(attributeAccessItem.getAccessSelectors().getAccessSelector());
      assertThat(attributeAccessItemDto.getAttributeId())
          .isEqualTo(attributeAccessItem.getAttributeId());
    }
  }

  private void checkMethodAccess(
      final MethodAccessDescriptor methodAccess, final MethodAccessDescriptorDto methodAccessDto) {
    this.checkMethodAccessItem(
        methodAccess.getMethodAccessItem(), methodAccessDto.getMethodAccessItem());
  }

  private void checkMethodAccessItem(
      final List<MethodAccessItem> methodAccessItemList,
      final List<MethodAccessItemDto> methodAccessItemDtoList) {
    assertThat(methodAccessItemDtoList.size()).isEqualTo(methodAccessItemList.size());

    final Iterator<MethodAccessItem> methodAccessItemIterator = methodAccessItemList.iterator();
    final Iterator<MethodAccessItemDto> methodAccessItemDtoIterator =
        methodAccessItemDtoList.iterator();
    while (methodAccessItemIterator.hasNext() && methodAccessItemDtoIterator.hasNext()) {
      final MethodAccessItem methodAccessItem = methodAccessItemIterator.next();
      final MethodAccessItemDto methodAccessItemDto = methodAccessItemDtoIterator.next();
      assertThat(methodAccessItemDto.getAccessMode().name())
          .isEqualTo(methodAccessItem.getAccessMode().name());
      assertThat(methodAccessItemDto.getMethodId()).isEqualTo(methodAccessItem.getMethodId());
    }
  }

  private void checkCosemObisCode(
      final CosemObisCode logicalName, final CosemObisCodeDto logicalNameDto) {
    assertThat(logicalNameDto.getA()).isEqualTo(logicalName.getA());
    assertThat(logicalNameDto.getB()).isEqualTo(logicalName.getB());
    assertThat(logicalNameDto.getC()).isEqualTo(logicalName.getC());
    assertThat(logicalNameDto.getD()).isEqualTo(logicalName.getD());
    assertThat(logicalNameDto.getE()).isEqualTo(logicalName.getE());
    assertThat(logicalNameDto.getF()).isEqualTo(logicalName.getF());
  }
}
