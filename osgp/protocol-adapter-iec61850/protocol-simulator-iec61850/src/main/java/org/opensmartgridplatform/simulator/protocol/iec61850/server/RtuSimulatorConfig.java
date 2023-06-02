//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.iec61850.server;

import com.beanit.openiec61850.SclParseException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.eventproducers.ServerSapEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class RtuSimulatorConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(RtuSimulatorConfig.class);

  @Bean
  @SuppressWarnings(
      "squid:S00107") // Large number of parameters is preferable over the alternatives.
  public RtuSimulator rtuSimulator(
      @Value("${rtu.icd}") final String icdFilename,
      @Value("${rtu.port}") final Integer port,
      @Value("${rtu.serverName}") final String serverName,
      @Value("${rtu.stopGeneratingValues}") final Boolean stopGeneratingValues,
      @Value("${rtu.updateValuesDelay}") final Long updateValuesDelay,
      @Value("${rtu.updateValuesPeriod}") final Long updateValuesPeriod,
      final ResourceLoader resourceLoader,
      final ServerSapEventProducer serverSapEventProducer)
      throws IOException {
    LOGGER.info(
        "Start simulator with icdFilename={}, port={}, serverName={}, stopGeneratingValues={}, updateValuesDelay={}, updateValuesPeriod={}",
        icdFilename,
        port,
        serverName,
        stopGeneratingValues,
        updateValuesDelay,
        updateValuesPeriod);

    final InputStream icdInputStream;
    final File icdFile = new File(icdFilename);
    if (icdFile.exists()) {
      LOGGER.info("Simulator icd {} found as external file", icdFilename);
      icdInputStream = resourceLoader.getResource("file:" + icdFilename).getInputStream();
    } else {
      LOGGER.info(
          "Simulator icd {} not found as external file, load it from the classpath", icdFilename);
      icdInputStream = resourceLoader.getResource("classpath:" + icdFilename).getInputStream();
    }
    LOGGER.info("Simulator icd file loaded");

    try {
      final RtuSimulator rtuSimulator =
          new RtuSimulator(
              port,
              icdInputStream,
              serverName,
              serverSapEventProducer,
              updateValuesDelay,
              updateValuesPeriod);
      if (Boolean.TRUE.equals(stopGeneratingValues)) {
        rtuSimulator.ensurePeriodicDataGenerationIsStopped();
      }
      rtuSimulator.start();
      return rtuSimulator;
    } catch (final SclParseException e) {
      LOGGER.warn("Error parsing SCL/ICD file", e);
    } finally {
      icdInputStream.close();
    }

    return null;
  }
}
