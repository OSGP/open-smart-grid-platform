// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import java.math.BigDecimal;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.OsgpUnit;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsMeterValueDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DlmsUnitTypeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calculate a osgp meter value:
 *
 * <pre>
 * - determine the osgp unit
 * - determine the multiplier for the conversion of DlmsUnit to OsgpUnit
 * - apply the multiplier
 * </pre>
 */
public class DlmsMeterValueConverter extends CustomConverter<DlmsMeterValueDto, OsgpMeterValue> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DlmsMeterValueConverter.class);

  @Override
  public OsgpMeterValue convert(
      final DlmsMeterValueDto source,
      final Type<? extends OsgpMeterValue> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }
    final BigDecimal multiplier =
        this.getMultiplierToOsgpUnit(
            source.getDlmsUnit(), this.toStandardUnit(source.getDlmsUnit()));
    final BigDecimal calculated = source.getValue().multiply(multiplier);
    LOGGER.debug(String.format("calculated %s from %s", calculated, source));
    return new OsgpMeterValue(calculated, this.toStandardUnit(source.getDlmsUnit()));
  }

  /**
   * return the multiplier to get from a dlms unit to a osgp unit
   *
   * @throws IllegalArgumentException when no multiplier is found
   * @param dlmsUnit
   * @return
   */
  private BigDecimal getMultiplierToOsgpUnit(
      final DlmsUnitTypeDto dlmsUnit, final OsgpUnit osgpUnit) {

    switch (dlmsUnit) {
      case KWH:
        return BigDecimal.valueOf(0.001d);
      case M3: // intentional fallthrough.
      case M3_CORR: // intentional fallthrough.
      case UNDEFINED:
        return BigDecimal.valueOf(1d);
      default:
        break;
    }

    throw new IllegalArgumentException(
        String.format(
            "calculating %s from %s not supported yet", osgpUnit.name(), dlmsUnit.name()));
  }

  /**
   * return the osgp unit that corresponds to a dlms unit
   *
   * @throws IllegalArgumentException when no osgp unit is found
   */
  private OsgpUnit toStandardUnit(final DlmsUnitTypeDto dlmsUnit) {
    // this is needed because the xsd generates M_3 from the M3 tag!
    if ("M_3".equals(dlmsUnit.getUnit())) {
      return OsgpUnit.M3;
    } else {
      for (final OsgpUnit osgpUnit : OsgpUnit.values()) {
        if (osgpUnit.name().equals(dlmsUnit.getUnit())) {
          return osgpUnit;
        }
      }
    }

    return OsgpUnit.UNDEFINED;
  }
}
