/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories;

import java.util.Arrays;
import org.opensmartgridplatform.dto.da.measurements.MeasurementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementGroupDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportHeaderDto;
import org.opensmartgridplatform.dto.da.measurements.elements.BitmaskMeasurementElementDto;

public class MeasurementReportFactory {
  private static final byte LMD_1_ON = 1;
  private static final byte LMD_2_ON = 0;

  public static MeasurementReportDto getMeasurementReportDto() {
    final MeasurementReportHeaderDto mrh =
        new MeasurementReportHeaderDto("M_SP_NA_1", "INTERROGATED_BY_STATION", 0, 0);
    final MeasurementGroupDto mg1 = getMeasurementGroup(Iec60870DeviceFactory.LMD_1_IOA, LMD_1_ON);
    final MeasurementGroupDto mg2 = getMeasurementGroup(Iec60870DeviceFactory.LMD_2_IOA, LMD_2_ON);
    return new MeasurementReportDto(mrh, Arrays.asList(mg1, mg2));
  }

  private static MeasurementGroupDto getMeasurementGroup(
      final String identification, final byte value) {
    final MeasurementElementDto me = new BitmaskMeasurementElementDto(value);
    final MeasurementDto m = new MeasurementDto(Arrays.asList(me));
    return new MeasurementGroupDto(identification, Arrays.asList(m));
  }
}
