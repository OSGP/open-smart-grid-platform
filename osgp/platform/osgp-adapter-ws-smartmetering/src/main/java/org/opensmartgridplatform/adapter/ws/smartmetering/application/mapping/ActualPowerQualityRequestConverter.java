/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class ActualPowerQualityRequestConverter extends
        CustomConverter<org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityRequest, org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityRequest> {

    @Override
    public boolean canConvert(final Type<?> sourceType, final Type<?> destinationType) {
        return this.sourceType.isAssignableFrom(sourceType) && this.destinationType.equals(destinationType);
    }

    @Override
    public org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityRequest convert(final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.ActualPowerQualityRequest source,
            final Type<? extends org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityRequest> destinationType, final MappingContext context) {
        return new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ActualPowerQualityRequest(
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ConfidentialityType
                        .valueOf(source.getProfileType()));
    }

}
