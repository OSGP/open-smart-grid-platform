// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import ma.glasnost.orika.MappingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemDateTime;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemObisCode;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemObjectDefinition;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupAlarm;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.WindowElement;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupAlarmDto;

@ExtendWith(MockitoExtension.class)
class PushSetupAlarmDtoConverterTest {
  @Mock private ConfigurationMapper configurationMapper;
  @Mock private MappingContext mappingContext;
  @Mock private PushSetupAlarm pushSetupAlarm;
  private PushSetupAlarmDtoConverter pushSetupAlarmDtoConverter;

  @BeforeEach
  public void setup() {
    this.pushSetupAlarmDtoConverter = new PushSetupAlarmDtoConverter(this.configurationMapper);
  }

  @Test
  void convertTest() {
    final List<WindowElement> testList =
        Collections.singletonList(new WindowElement(new CosemDateTime(), new CosemDateTime()));
    when(this.pushSetupAlarm.getCommunicationWindow()).thenReturn(testList);

    final CosemObisCode code = new CosemObisCode(1, 2, 3, 4, 5, 6);
    when(this.pushSetupAlarm.getPushObjectList())
        .thenReturn(Collections.singletonList(new CosemObjectDefinition(1, code, 2)));

    final PushSetupAlarmDto result =
        this.pushSetupAlarmDtoConverter.convert(this.pushSetupAlarm, null, this.mappingContext);

    assertThat(result.getCommunicationWindow()).isNotNull();
    assertThat(result.getPushObjectList()).isNotNull();
  }

  @Test
  void convertTestWithEmptyLists() {
    when(this.pushSetupAlarm.getCommunicationWindow()).thenReturn(null);
    when(this.pushSetupAlarm.getPushObjectList()).thenReturn(null);

    final PushSetupAlarmDto result =
        this.pushSetupAlarmDtoConverter.convert(this.pushSetupAlarm, null, this.mappingContext);

    assertThat(result.getCommunicationWindow()).isNull();
    assertThat(result.getPushObjectList()).isNull();
  }
}
