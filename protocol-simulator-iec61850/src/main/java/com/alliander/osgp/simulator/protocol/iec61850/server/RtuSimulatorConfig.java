package com.alliander.osgp.simulator.protocol.iec61850.server;

import java.io.IOException;
import java.io.InputStream;

import org.openmuc.openiec61850.SclParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RtuSimulatorConfig {

    private final static Logger LOGGER = LoggerFactory.getLogger(RtuSimulatorConfig.class);

    @Bean
    public RtuSimulator rtuSimulator(@Value("${rtu.icd}") final String icdFilename,
            @Value("${rtu.port}") final Integer port) throws IOException {
        final InputStream icdFile = ClassLoader.getSystemResourceAsStream(icdFilename);

        try {
            final RtuSimulator rtuSimulator = new RtuSimulator(port, icdFile);
            rtuSimulator.start();
            return rtuSimulator;
        } catch (final SclParseException e) {
            LOGGER.warn("Error parsing SCL/ICD file {}", e.getMessage());
        }

        return null;
    }
}
