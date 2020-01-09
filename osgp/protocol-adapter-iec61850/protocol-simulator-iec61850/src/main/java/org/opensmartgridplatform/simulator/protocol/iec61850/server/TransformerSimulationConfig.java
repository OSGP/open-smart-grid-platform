package org.opensmartgridplatform.simulator.protocol.iec61850.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransformerSimulationConfig {

    @Value("${rtu.transformer.simulation.enabled:false}")
    private boolean transformerSimulationEnabled;

    @Bean
    public boolean transformerSimulationEnabled() {
        return this.transformerSimulationEnabled;
    }
}
