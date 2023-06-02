//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.hibernate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.TextType;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Hibernate {@link UserType} for {@link InetAddress} */
public class InetAddressUserType extends ImmutableUserType {

  private static final Logger LOGGER = LoggerFactory.getLogger(InetAddressUserType.class);

  @Override
  public int[] sqlTypes() {
    return new int[] {TextType.INSTANCE.sqlType()};
  }

  @Override
  public Class<?> returnedClass() {
    return InetAddress.class;
  }

  @Override
  public Object nullSafeGet(
      final ResultSet rs,
      final String[] names,
      final SharedSessionContractImplementor session,
      final Object owner)
      throws SQLException {
    try {
      final String value = (String) TextType.INSTANCE.nullSafeGet(rs, names, session, owner);
      if (value == null) {
        return null;
      } else {
        return InetAddress.getByName(value);
      }
    } catch (final UnknownHostException e) {
      LOGGER.warn("Exception thrown during nullSafeGet.", e);
      return null;
    }
  }

  @Override
  public void nullSafeSet(
      final PreparedStatement st,
      final Object value,
      final int index,
      final SharedSessionContractImplementor session)
      throws SQLException {
    if (value != null) {
      final InetAddress address = (InetAddress) value;
      TextType.INSTANCE.nullSafeSet(st, address.getHostAddress(), index, session);
    } else {
      TextType.INSTANCE.nullSafeSet(st, null, index, session);
    }
  }
}
