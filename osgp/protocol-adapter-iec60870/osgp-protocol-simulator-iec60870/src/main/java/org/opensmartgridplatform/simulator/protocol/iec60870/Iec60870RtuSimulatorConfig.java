package org.opensmartgridplatform.simulator.protocol.iec60870;

import org.opensmartgridplatform.simulator.protocol.iec60870.server.Iec60870ASduHandlerMap;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.Iec60870RtuSimulator;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.Iec60870ServerEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = false)
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/SimulatorProtocolIec60870/config}", ignoreResourceNotFound = true)
public class Iec60870RtuSimulatorConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870RtuSimulatorConfig.class);

    @Autowired
    public Iec60870ASduHandlerMap iec60870ASduHandlerMap;

    @Value("${iec60870.simulator.connection.timeout}")
    private int connectionTimeout;

    @Bean
    public Iec60870RtuSimulator iec60870RtuSimulator() {
        LOGGER.debug("Creating IEC60870 RTU Simulator Bean.");
        final Iec60870RtuSimulator simulator = new Iec60870RtuSimulator(this.iec60870ServerEventListener());

        LOGGER.debug("Starting IEC60870 RTU Simulator.");
        simulator.start();

        return simulator;
    }

    @Bean
    public Iec60870ServerEventListener iec60870ServerEventListener() {
        return new Iec60870ServerEventListener(this.iec60870ASduHandlerMap, this.connectionTimeout);
    }
}
