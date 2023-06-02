//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.opensmartgridplatform.adapter.protocol.iec61850.application.config.BeanUtil;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.ReadOnlyNodeContainer;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.Iec61850GasFurnaceCommandFactory;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataSystemIdentifierDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementDto;

public class Iec61850GasFurnaceReportHandler implements Iec61850ReportHandler {

  private static final String SYSTEM_TYPE = "GAS_FURNACE";

  private static final Set<DataAttribute> NODES_USING_ID =
      EnumSet.of(
          DataAttribute.TEMPERATURE,
          DataAttribute.MATERIAL_FLOW,
          DataAttribute.MATERIAL_STATUS,
          DataAttribute.MATERIAL_TYPE);

  private static final Iec61850ReportNodeHelper NODE_HELPER =
      new Iec61850ReportNodeHelper(NODES_USING_ID);

  private final int systemId;
  private final Iec61850GasFurnaceCommandFactory iec61850GasFurnaceCommandFactory;

  public Iec61850GasFurnaceReportHandler(final int systemId) {
    this.systemId = systemId;
    this.iec61850GasFurnaceCommandFactory =
        BeanUtil.getBean(Iec61850GasFurnaceCommandFactory.class);
  }

  @Override
  public GetDataSystemIdentifierDto createResult(final List<MeasurementDto> measurements) {
    return new GetDataSystemIdentifierDto(this.systemId, SYSTEM_TYPE, measurements);
  }

  @Override
  public List<MeasurementDto> handleMember(final ReadOnlyNodeContainer member) {
    return NODE_HELPER.getMeasurements(member, this.iec61850GasFurnaceCommandFactory);
  }
}
