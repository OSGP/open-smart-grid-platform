// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.testutils.matchers;

import org.mockito.ArgumentMatcher;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;

public class MeasurementReportTypeMatcher implements ArgumentMatcher<ResponseMessage> {

  private String typeId;

  public MeasurementReportTypeMatcher(final String typeId) {
    this.typeId = typeId;
  }

  @Override
  public boolean matches(final ResponseMessage argument) {
    if (!(argument.getDataObject() instanceof MeasurementReportDto)) {
      return false;
    }
    final MeasurementReportDto dto = (MeasurementReportDto) argument.getDataObject();
    return dto.getReportHeader().getMeasurementType().equals(this.typeId);
  }
}
