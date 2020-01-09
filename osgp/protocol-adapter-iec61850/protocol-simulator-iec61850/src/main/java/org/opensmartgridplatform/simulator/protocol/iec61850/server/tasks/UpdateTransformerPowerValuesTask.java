package org.opensmartgridplatform.simulator.protocol.iec61850.server.tasks;

import java.util.List;

import org.openmuc.openiec61850.BasicDataAttribute;
import org.openmuc.openiec61850.ServerSap;
import org.opensmartgridplatform.simulator.protocol.iec61850.domain.exceptions.EndOfSimulationException;
import org.opensmartgridplatform.simulator.protocol.iec61850.domain.services.ValueProvider;
import org.opensmartgridplatform.simulator.protocol.iec61850.domain.valueobjects.FloatMeasurement;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateTransformerPowerValuesTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateTransformerPowerValuesTask.class);

    private final Transformer transformer;
    private final ServerSap serverSap;
    private final ValueProvider<FloatMeasurement> powerValueProvider;

    public UpdateTransformerPowerValuesTask(final ServerSap serverSap, final Transformer transformer,
            final ValueProvider<FloatMeasurement> powerValueProvider) {
        this.transformer = transformer;
        this.serverSap = serverSap;
        this.powerValueProvider = powerValueProvider;
    }

    @Override
    public void run() {
        LOGGER.info("Running update power values task.");
        try {
            final FloatMeasurement measurement = this.powerValueProvider.getValue();
            final List<BasicDataAttribute> powerValues = this.transformer.updatePowerValues(measurement);
            this.serverSap.setValues(powerValues);
        } catch (final EndOfSimulationException e) {
            LOGGER.warn("=== NO MORE POWER VALUES AVAILABLE ===");
        }
    }

}
