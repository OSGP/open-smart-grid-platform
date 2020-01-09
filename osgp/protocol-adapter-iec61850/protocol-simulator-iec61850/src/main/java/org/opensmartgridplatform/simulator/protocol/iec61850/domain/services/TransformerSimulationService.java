package org.opensmartgridplatform.simulator.protocol.iec61850.domain.services;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.openmuc.openiec61850.ServerSap;
import org.opensmartgridplatform.simulator.protocol.iec61850.domain.valueobjects.FloatMeasurement;
import org.opensmartgridplatform.simulator.protocol.iec61850.infra.files.CsvFloatMeasurementValueProvider;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.RtuSimulator;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.Transformer;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.tasks.UpdateTransformerPowerValuesTask;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.tasks.UpdateTransformerTemperatureValuesTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class TransformerSimulationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformerSimulationService.class);

    @Value("${rtu.transformer.simulation.power.update.interval:10000}")
    private long transformerSimulationPowerUpdateInterval;

    @Value("${rtu.transformer.simulation.temperature.update.interval:60000}")
    private long transformerSimulationTemperatureUpdateInterval;

    @Value("${rtu.transformer.simulation.speedup.factor:1}")
    private long transformerSimulationSpeedupFactor;

    @Value("${rtu.transformer.1.power.file:transformer-p.csv}")
    private String transformer1PowerValuesFileName;

    @Value("${rtu.transformer.1.temperature.file:transformer-t.csv}")
    private String transformer1TemperatureValuesFileName;

    private ThreadPoolTaskScheduler taskScheduler;

    public void initialize(final RtuSimulator simulator) {
        LOGGER.info("Initializing transformer simulation");
        final ServerSap serverSap = simulator.getServer();

        final List<Transformer> transformers = simulator.getLogicalDevices()
                .stream()
                .filter(ld -> (ld instanceof Transformer))
                .map(ld -> (Transformer) ld)
                .collect(Collectors.toList());

        this.initializeScheduler(serverSap, transformers);
    }

    public void stop() {
        if (this.taskScheduler != null) {
            this.taskScheduler.destroy();
        }
    }

    public ValueProvider<FloatMeasurement> transformer1PowerValueProvider() {
        LOGGER.info("Initializing transformer1PowerValueProvider bean with file: {}.",
                this.transformer1PowerValuesFileName);
        return new CsvFloatMeasurementValueProvider(this.transformer1PowerValuesFileName);
    }

    public ValueProvider<FloatMeasurement> transformer1TemperatureValueProvider() {
        LOGGER.info("Initializing transformer1TemperatureValueProvider bean with file: {}.",
                this.transformer1TemperatureValuesFileName);
        return new CsvFloatMeasurementValueProvider(this.transformer1TemperatureValuesFileName);
    }

    private void initializeScheduler(final ServerSap serverSap, final List<Transformer> transformers) {
        final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("transformerSubstationTaskScheduler");

        final Runnable updatePowerValuesTask = new UpdateTransformerPowerValuesTask(serverSap, transformers.get(0),
                this.transformer1PowerValueProvider());
        final Duration powerInterval = this.periodInMillis(this.transformerSimulationPowerUpdateInterval);
        scheduler.scheduleAtFixedRate(updatePowerValuesTask, powerInterval);

        final Runnable updateTemperatureValuesTask = new UpdateTransformerTemperatureValuesTask(serverSap,
                transformers.get(0), this.transformer1TemperatureValueProvider());
        final Duration temperatureInterval = this.periodInMillis(this.transformerSimulationTemperatureUpdateInterval);
        scheduler.scheduleAtFixedRate(updateTemperatureValuesTask, temperatureInterval);

        this.taskScheduler = scheduler;
    }

    private Duration periodInMillis(final long interval) {
        long millis;
        if (this.transformerSimulationSpeedupFactor > 0) {
            millis = interval / this.transformerSimulationSpeedupFactor;
        } else {
            millis = interval;
        }
        return Duration.ofMillis(millis);
    }
}
