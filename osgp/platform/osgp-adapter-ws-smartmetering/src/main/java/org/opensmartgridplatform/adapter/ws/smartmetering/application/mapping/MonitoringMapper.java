// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import org.springframework.stereotype.Component;

@Component(value = "monitoringMapper")
public class MonitoringMapper extends ConfigurableMapper {

  private static final String SELECTED_VALUES = "selectedValues";
  private static final String SELECTED_VALUES_CAPTURE_OBJECT = "selectedValues.captureObject";

  @Override
  public void configure(final MapperFactory mapperFactory) {

    mapperFactory
        .classMap(
            org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
                .AmrProfileStatusCode.class,
            AmrProfileStatusCode.class)
        .field("amrProfileStatusCodeFlag", "amrProfileStatusCodeFlags")
        .byDefault()
        .register();

    // Converter is needed because of instanceOf check to set boolean
    // mbusDevice for a PeriodicMeterReadsQuery object
    mapperFactory.getConverterFactory().registerConverter(new PeriodicMeterReadsRequestConverter());

    // This converter is needed because
    // RetrievePushNotificationAlarmResponse
    // doesn't have a List<AlarmType>, but an AlarmRegister. The
    // List<AlarmType> is in that class.
    mapperFactory.getConverterFactory().registerConverter(new PushNotificationsAlarmConverter());

    // This converter is needed because of the M_3 in the OsgpUnitType enum
    // and the M3 in the OsgpUnit enum.
    mapperFactory.getConverterFactory().registerConverter(new MeterValueConverter());

    // This converter is needed to ensure correct mapping of dates and
    // times.
    mapperFactory.getConverterFactory().registerConverter(new XsdDateTimeToLongConverter());

    // This converter is needed because it contains logic.
    mapperFactory
        .getConverterFactory()
        .registerConverter(new PeriodicReadsRequestGasQueryConverter());

    /*
     * The XML construct with the selected values is a bit more complex than
     * the one in the rest of the code. The "selectedValues.captureObject"
     * in XML is the actual list of the objects to be mapped to the
     * "selectedValues" in the rest of the code and vice versa.
     */
    mapperFactory
        .classMap(
            org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring
                .GetPowerQualityProfileRequest.class,
            org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                .GetPowerQualityProfileRequest.class)
        .fieldAToB(SELECTED_VALUES_CAPTURE_OBJECT, SELECTED_VALUES)
        .fieldBToA(SELECTED_VALUES, SELECTED_VALUES_CAPTURE_OBJECT)
        .byDefault()
        .register();

    mapperFactory
        .classMap(
            org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle
                .GetPowerQualityProfileRequest.class,
            org.opensmartgridplatform.domain.core.valueobjects.smartmetering
                .GetPowerQualityProfileRequest.class)
        .fieldAToB(SELECTED_VALUES_CAPTURE_OBJECT, SELECTED_VALUES)
        .fieldBToA(SELECTED_VALUES, SELECTED_VALUES_CAPTURE_OBJECT)
        .byDefault()
        .register();

    mapperFactory.getConverterFactory().registerConverter(new ObisCodeValuesConverter());
    mapperFactory.getConverterFactory().registerConverter(new ProfileEntryValueConverter());
    mapperFactory
        .getConverterFactory()
        .registerConverter(new PowerQualityProfileResponseDataConverter());
    mapperFactory
        .getConverterFactory()
        .registerConverter(new GetPowerQualityProfileRequestDataConverter());
    mapperFactory
        .getConverterFactory()
        .registerConverter(new ActualPowerQualityResponseDataConverter());
    mapperFactory.getConverterFactory().registerConverter(new PowerQualityValueConverter());
    mapperFactory.getConverterFactory().registerConverter(new ThdFingerprintBundleConverter());
    mapperFactory.getConverterFactory().registerConverter(new ThdFingerprintConverter());
  }
}
