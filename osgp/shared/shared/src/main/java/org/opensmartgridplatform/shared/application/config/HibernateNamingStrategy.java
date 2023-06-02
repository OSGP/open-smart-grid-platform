//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.application.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class HibernateNamingStrategy extends PhysicalNamingStrategyStandardImpl {

  /** Default serial version UID is required. */
  private static final long serialVersionUID = 1L;

  /** Singleton instance of this class. */
  public static final HibernateNamingStrategy INSTANCE = new HibernateNamingStrategy();

  @Override
  public Identifier toPhysicalTableName(final Identifier name, final JdbcEnvironment context) {
    return this.convertToSnakeCase(name);
  }

  @Override
  public Identifier toPhysicalColumnName(final Identifier name, final JdbcEnvironment context) {
    return this.convertToSnakeCase(name);
  }

  private Identifier convertToSnakeCase(final Identifier identifier) {
    final String regex = "([a-z])([A-Z])";
    final String replacement = "$1_$2";
    final String newName = identifier.getText().replaceAll(regex, replacement).toLowerCase();
    return Identifier.toIdentifier(newName, identifier.isQuoted());
  }
}
