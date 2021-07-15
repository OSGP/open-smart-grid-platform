/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting;

import java.util.Collections;
import java.util.List;
import org.opensmartgridplatform.adapter.protocol.iec61850.application.config.BeanUtil;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.ReadOnlyNodeContainer;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.Iec61850PvCommandFactory;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataSystemIdentifierDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementDto;

public class Iec61850PvReportHandler implements Iec61850ReportHandler {

  private static final String SYSTEM_TYPE = "PV";

  private static final Iec61850ReportNodeHelper NODE_HELPER =
      new Iec61850ReportNodeHelper(Collections.emptySet());

  private final int systemId;
  private final Iec61850PvCommandFactory iec61850PvCommandFactory;

  public Iec61850PvReportHandler(final int systemId) {
    this.systemId = systemId;
    this.iec61850PvCommandFactory = BeanUtil.getBean(Iec61850PvCommandFactory.class);
  }

  @Override
  public GetDataSystemIdentifierDto createResult(final List<MeasurementDto> measurements) {
    return new GetDataSystemIdentifierDto(this.systemId, SYSTEM_TYPE, measurements);
  }

  @Override
  public List<MeasurementDto> handleMember(final ReadOnlyNodeContainer member) {
    return NODE_HELPER.getMeasurements(member, this.iec61850PvCommandFactory);
  }
}
