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

public class UpdateTransformerTemperatureValuesTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateTransformerTemperatureValuesTask.class);

    private final Transformer transformer;
    private final ServerSap serverSap;
    private final ValueProvider<FloatMeasurement> temperatureValueProvider;

    public UpdateTransformerTemperatureValuesTask(final ServerSap serverSap, final Transformer transformer,
            final ValueProvider<FloatMeasurement> powerValueProvider) {
        this.transformer = transformer;
        this.serverSap = serverSap;
        this.temperatureValueProvider = powerValueProvider;
    }

    @Override
    public void run() {
        LOGGER.info("Running update power values task.");
        try {
            final FloatMeasurement measurement = this.temperatureValueProvider.getValue();
            final List<BasicDataAttribute> temperatureValues = this.transformer.updateTemperatureValues(measurement);
            this.serverSap.setValues(temperatureValues);
        } catch (final EndOfSimulationException e) {
            LOGGER.warn("=== NO MORE POWER VALUES AVAILABLE ===");
        }
    }

}
