/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HibernateNamingStrategyTest {

  @Mock private JdbcEnvironment context;

  private HibernateNamingStrategy hibernateNamingStrategy;

  @BeforeEach
  public void setup() {
    this.hibernateNamingStrategy = HibernateNamingStrategy.INSTANCE;
  }

  @Test
  void testToPhysicalTableName() {
    assertTableName(Identifier.toIdentifier("user", false), Identifier.toIdentifier("user", false));
    assertTableName(
        Identifier.toIdentifier("userId", false), Identifier.toIdentifier("user_id", false));
    assertTableName(Identifier.toIdentifier("user", true), Identifier.toIdentifier("user", true));
    assertTableName(
        Identifier.toIdentifier("userId", true), Identifier.toIdentifier("user_id", true));
  }

  @Test
  void testToPhysicalColumnName() {
    assertColumnName(
        Identifier.toIdentifier("user", false), Identifier.toIdentifier("user", false));
    assertColumnName(
        Identifier.toIdentifier("userId", false), Identifier.toIdentifier("user_id", false));
    assertColumnName(Identifier.toIdentifier("user", true), Identifier.toIdentifier("user", true));
    assertColumnName(
        Identifier.toIdentifier("userId", true), Identifier.toIdentifier("user_id", true));
  }

  private void assertTableName(Identifier source, Identifier expectedIdentifier) {
    Identifier result = hibernateNamingStrategy.toPhysicalTableName(source, context);
    assertThat(result.getText()).isEqualTo(expectedIdentifier.getText());
    assertThat(result.isQuoted()).isEqualTo(expectedIdentifier.isQuoted());
  }

  private void assertColumnName(Identifier source, Identifier expectedIdentifier) {
    Identifier result = hibernateNamingStrategy.toPhysicalColumnName(source, context);
    assertThat(result.getText()).isEqualTo(expectedIdentifier.getText());
    assertThat(result.isQuoted()).isEqualTo(expectedIdentifier.isQuoted());
  }
}
