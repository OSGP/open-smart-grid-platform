package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.iec61850.application.config.BeanUtil;
import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.ReadOnlyNodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850BoilerCommandFactory;
import com.alliander.osgp.dto.valueobjects.microgrids.GetDataSystemIdentifierDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;

public class Iec61850BoilerReportHandler implements Iec61850ReportHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850BoilerReportHandler.class);

    private static final String SYSTEM_TYPE = "BOILER";

    private static final Set<DataAttribute> NODES_USING_ID = EnumSet.of(DataAttribute.TEMPERATURE,
            DataAttribute.MATERIAL_FLOW, DataAttribute.MATERIAL_STATUS, DataAttribute.MATERIAL_TYPE,
            DataAttribute.SCHEDULE_ID, DataAttribute.SCHEDULE_CAT, DataAttribute.SCHEDULE_CAT_RTU,
            DataAttribute.SCHEDULE_TYPE);

    private static final Iec61850ReportNodeHelper NODE_HELPER = new Iec61850ReportNodeHelper(NODES_USING_ID);

    private final int systemId;
    private final Iec61850BoilerCommandFactory iec61850BoilerCommandFactory;

    public Iec61850BoilerReportHandler(final int systemId) {
        this.systemId = systemId;
        this.iec61850BoilerCommandFactory = BeanUtil.getBean(Iec61850BoilerCommandFactory.class);
    }

    @Override
    public GetDataSystemIdentifierDto createResult(final List<MeasurementDto> measurements) {
        final GetDataSystemIdentifierDto systemResult = new GetDataSystemIdentifierDto(this.systemId, SYSTEM_TYPE,
                measurements);
        final List<GetDataSystemIdentifierDto> systems = new ArrayList<>();
        systems.add(systemResult);
        return systemResult;
    }

    @Override
    public List<MeasurementDto> handleMember(final ReadOnlyNodeContainer member) {

        final List<MeasurementDto> measurements = new ArrayList<>();
        final RtuReadCommand<MeasurementDto> command = this.iec61850BoilerCommandFactory
                .getCommand(NODE_HELPER.getCommandName(member));

        if (command == null) {
            LOGGER.warn("No command found for node {}", member.getFcmodelNode().getName());
        } else {
            measurements.add(command.translate(member));
        }
        return measurements;
    }

}
