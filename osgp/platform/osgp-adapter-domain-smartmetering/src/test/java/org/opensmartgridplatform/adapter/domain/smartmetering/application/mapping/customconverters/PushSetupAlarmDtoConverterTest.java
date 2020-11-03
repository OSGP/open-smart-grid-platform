package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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

import ma.glasnost.orika.MappingContext;

@ExtendWith(MockitoExtension.class)
public class PushSetupAlarmDtoConverterTest {
    @Mock
    private ConfigurationMapper configurationMapper;
    @Mock
    private PushSetupAlarmDto pushSetupAlarmDto;
    @Mock
    private MappingContext mappingContext;
    @Mock
    private PushSetupAlarm pushSetupAlarm;
    private PushSetupAlarmDtoConverter pushSetupAlarmDtoConverter;

    @Test
    public void convertTest() {
        this.pushSetupAlarmDtoConverter = new PushSetupAlarmDtoConverter(this.configurationMapper);

        final List<WindowElement> testList = new ArrayList<WindowElement>();
        testList.add(new WindowElement(new CosemDateTime(), new CosemDateTime()));
        when(this.pushSetupAlarm.getCommunicationWindow()).thenReturn(testList);

        final List<CosemObjectDefinition> pushObjectList = new ArrayList<CosemObjectDefinition>();
        final CosemObisCode code = new CosemObisCode(1, 2, 3, 4, 5, 6);
        pushObjectList.add(new CosemObjectDefinition(1, code, 2));
        when(this.pushSetupAlarm.getPushObjectList()).thenReturn(pushObjectList);

        final PushSetupAlarmDto result = this.pushSetupAlarmDtoConverter.convert(this.pushSetupAlarm, null,
                this.mappingContext);

        assertNotNull(result.getCommunicationWindow());
        assertNotNull(result.getPushObjectList());
    }

    @Test
    public void convertTestWithEmptyLists() {
        this.pushSetupAlarmDtoConverter = new PushSetupAlarmDtoConverter(this.configurationMapper);

        when(this.pushSetupAlarm.getCommunicationWindow()).thenReturn(new ArrayList<>());
        when(this.pushSetupAlarm.getPushObjectList()).thenReturn(new ArrayList<>());

        final PushSetupAlarmDto result = this.pushSetupAlarmDtoConverter.convert(this.pushSetupAlarm, null,
                this.mappingContext);

        assertNull(result.getCommunicationWindow());
        assertNull(result.getPushObjectList());
    }
}
