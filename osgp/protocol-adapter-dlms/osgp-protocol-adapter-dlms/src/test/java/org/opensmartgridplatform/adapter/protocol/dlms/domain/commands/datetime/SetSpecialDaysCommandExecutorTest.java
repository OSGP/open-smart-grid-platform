/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.datetime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.CosemDate;
import org.openmuc.jdlms.datatypes.CosemDateFormat.Field;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.interfaceclass.InterfaceClass;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.ClockAttribute;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SpecialDayDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@Slf4j
class SetSpecialDaysCommandExecutorTest {

  @Mock private DlmsConnectionManager dlmsConnectionManager;
  @Mock private DlmsDevice device;
  @Mock private MessageMetadata messageMetadata;

  private final DlmsHelper dlmsHelper = new DlmsHelper();

  @Mock private DlmsMessageListener dlmsMessageListener;

  @Mock private DlmsConnection dlmsConnection;
  @InjectMocks private SetSpecialDaysCommandExecutor setSpecialDaysCommandExecutor;

  @Captor ArgumentCaptor<SetParameter> setParameterArgumentCaptor;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(this.setSpecialDaysCommandExecutor, "dlmsHelper", this.dlmsHelper);

    when(this.dlmsConnectionManager.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.dlmsConnectionManager.getConnection()).thenReturn(this.dlmsConnection);
  }

  @Test
  void execute() throws ProtocolAdapterException, IOException {

    final SpecialDayDto specialDayDto = new SpecialDayDto(new CosemDateDto(2016, 1, 1), 1);
    final List<SpecialDayDto> specialDayDtoList = Collections.singletonList(specialDayDto);

    this.setSpecialDaysCommandExecutor.execute(
        this.dlmsConnectionManager, this.device, specialDayDtoList, this.messageMetadata);
    verify(this.dlmsConnection, times(1)).set(this.setParameterArgumentCaptor.capture());

    final List<SetParameter> setParameters = this.setParameterArgumentCaptor.getAllValues();
    assertThat(setParameters).hasSize(1);

    final SetParameter setParameter = setParameters.get(0);
    final AttributeAddress attributeAddress = setParameter.getAttributeAddress();
    assertThat(attributeAddress.getClassId()).isEqualTo(InterfaceClass.SPECIAL_DAYS_TABLE.id());
    assertThat(attributeAddress.getId()).isEqualTo(ClockAttribute.TIME.attributeId());

    assertThat(setParameter.getData().getType().name()).isEqualTo("ARRAY");
    final List<DataObject> array = setParameter.getData().getValue();

    final DataObject structure = (DataObject) array.get(0);
    final List<DataObject> structureData = structure.getValue();
    log.debug(structureData.toString());

    final DataObject longUnsigned = (DataObject) structureData.get(0);
    final DataObject date = (DataObject) structureData.get(1);
    final DataObject specialDay = (DataObject) structureData.get(2);

    final CosemDate receivedDate = (CosemDate) date.getRawValue();
    final CosemDate expectedDate = new CosemDate(2016, 1, 1);

    assertThat(longUnsigned.getRawValue()).isEqualTo(0);
    assertThat(receivedDate.get(Field.YEAR)).isEqualTo(expectedDate.get(Field.YEAR));
    assertThat(receivedDate.get(Field.MONTH)).isEqualTo(expectedDate.get(Field.MONTH));
    assertThat(receivedDate.get(Field.DAY_OF_MONTH))
        .isEqualTo(expectedDate.get(Field.DAY_OF_MONTH));
    assertThat(specialDay.getRawValue()).isEqualTo((short) 1);
  }
}
