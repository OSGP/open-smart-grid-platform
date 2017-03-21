package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.ReadOnlyNodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850BoilerCommandFactory;
import com.alliander.osgp.dto.valueobjects.microgrids.GetDataSystemIdentifierDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;

public class Iec61850BoilerReportHandler implements Iec61850ReportHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850BoilerReportHandler.class);

    private static final String SYSTEM_TYPE = "BOILER";
    private static final List<String> NODES_USING_ID_LIST = new ArrayList<>();

    static {
        intializeNodesUsingIdList();
    }

    private final int systemId;

    public Iec61850BoilerReportHandler(final int systemId) {
        this.systemId = systemId;
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
    public MeasurementDto handleMember(final ReadOnlyNodeContainer member) {

        final RtuReadCommand<MeasurementDto> command = Iec61850BoilerCommandFactory.getInstance()
                .getCommand(this.getCommandName(member));

        if (command == null) {
            LOGGER.warn("No command found for node {}", member.getFcmodelNode().getName());
            return null;
        } else {
            return command.translate(member);
        }
    }

    private static void intializeNodesUsingIdList() {
        NODES_USING_ID_LIST.add(DataAttribute.TEMPERATURE.getDescription());
        NODES_USING_ID_LIST.add(DataAttribute.MATERIAL_FLOW.getDescription());
        NODES_USING_ID_LIST.add(DataAttribute.MATERIAL_STATUS.getDescription());
        NODES_USING_ID_LIST.add(DataAttribute.MATERIAL_TYPE.getDescription());
        NODES_USING_ID_LIST.add(DataAttribute.SCHEDULE_ID.getDescription());
        NODES_USING_ID_LIST.add(DataAttribute.SCHEDULE_CAT.getDescription());
        NODES_USING_ID_LIST.add(DataAttribute.SCHEDULE_CAT_RTU.getDescription());
        NODES_USING_ID_LIST.add(DataAttribute.SCHEDULE_TYPE.getDescription());
    }

    private static boolean useId(final String nodeName) {
        return NODES_USING_ID_LIST.contains(nodeName);
    }

    private String getCommandName(final ReadOnlyNodeContainer member) {
        final String nodeName = member.getFcmodelNode().getName();
        if (useId(nodeName)) {
            final String refName = member.getFcmodelNode().getReference().toString();
            final int startIndex = refName.length() - nodeName.length() - 2;
            return nodeName + refName.substring(startIndex, startIndex + 1);
        } else {
            return nodeName;
        }
    }

}
