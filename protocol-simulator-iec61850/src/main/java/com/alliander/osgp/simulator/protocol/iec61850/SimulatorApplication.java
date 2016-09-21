package com.alliander.osgp.simulator.protocol.iec61850;

import java.io.IOException;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SimulatorApplication {

    public static void main(final String[] args) throws NumberFormatException, IOException {
        // Force UTC timezone
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        SpringApplication.run(SimulatorApplication.class, args);
    }
}
