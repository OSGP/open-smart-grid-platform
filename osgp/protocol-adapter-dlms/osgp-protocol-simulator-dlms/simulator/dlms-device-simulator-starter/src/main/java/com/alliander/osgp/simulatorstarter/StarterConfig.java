/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.simulatorstarter;

import com.alliander.osgp.simulatorstarter.database.CoreDatabaseRepository;
import com.alliander.osgp.simulatorstarter.database.DatabaseHelper;
import com.alliander.osgp.simulatorstarter.database.ProtocolAdapterDlmsDatabaseRepository;
import com.alliander.osgp.simulatorstarter.database.SharedDatabaseRepository;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ComponentScan()
public class StarterConfig {
  public Starter starter(
      final DatabaseHelper databaseHelper, final ApplicationArguments applicationArguments)
      throws IOException {
    return new Starter(databaseHelper, applicationArguments);
  }

  public DatabaseHelper databaseHelper(
      final CoreDatabaseRepository coreDatabaseRepository,
      final ProtocolAdapterDlmsDatabaseRepository protocolAdapterDlmsDatabaseRepository,
      final SharedDatabaseRepository sharedDatabaseRepository) {
    return new DatabaseHelper(
        coreDatabaseRepository, protocolAdapterDlmsDatabaseRepository, sharedDatabaseRepository);
  }

  public CoreDatabaseRepository coreDatabaseRepository(
      @Qualifier("coreDb") final JdbcTemplate jdbcTemplate) {
    return new CoreDatabaseRepository(jdbcTemplate);
  }
}
