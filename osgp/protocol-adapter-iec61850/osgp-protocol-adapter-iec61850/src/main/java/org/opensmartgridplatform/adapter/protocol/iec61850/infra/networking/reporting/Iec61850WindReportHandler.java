/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.opensmartgridplatform.adapter.protocol.iec61850.application.config.BeanUtil;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.ReadOnlyNodeContainer;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.Iec61850WindCommandFactory;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataSystemIdentifierDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementDto;

public class Iec61850WindReportHandler implements Iec61850ReportHandler {

  private static final String SYSTEM_TYPE = "WIND";

  private static final Set<DataAttribute> NODES_USING_ID =
      EnumSet.of(
          DataAttribute.ACTIVE_POWER_PHASE_A,
          DataAttribute.ACTIVE_POWER_PHASE_B,
          DataAttribute.ACTIVE_POWER_PHASE_C);

  private static final Pattern NODE_PATTERN =
      Pattern.compile("\\A(.*)WIND([1-9]\\d*+)/(LLN0|DRCC|DGEN|MMXU|GGIO)([1-9]\\d*+)?\\.(.*)\\Z");

  private static final Set<String> COMPOSITE_NODES = Collections.singleton("W");

  private static final Iec61850ReportNodeHelper NODE_HELPER =
      new Iec61850ReportNodeHelper(NODES_USING_ID, NODE_PATTERN, 4, COMPOSITE_NODES);

  private final int systemId;
  private final Iec61850WindCommandFactory iec61850WindCommandFactory;

  public Iec61850WindReportHandler(final int systemId) {
    this.systemId = systemId;
    this.iec61850WindCommandFactory = BeanUtil.getBean(Iec61850WindCommandFactory.class);
  }

  @Override
  public GetDataSystemIdentifierDto createResult(final List<MeasurementDto> measurements) {
    return new GetDataSystemIdentifierDto(this.systemId, SYSTEM_TYPE, measurements);
  }

  @Override
  public List<MeasurementDto> handleMember(final ReadOnlyNodeContainer member) {
    return NODE_HELPER.getMeasurements(member, this.iec61850WindCommandFactory);
  }
}
