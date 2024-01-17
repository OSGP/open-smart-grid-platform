// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.hibernate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Hibernate {@link UserType} for {@link InetAddress} */
public class InetAddressUserType extends ImmutableUserType {

  private static final Logger LOGGER = LoggerFactory.getLogger(InetAddressUserType.class);

  @Override
  public int getSqlType() {
    return Types.VARCHAR;
  }

  @Override
  public Class<?> returnedClass() {
    return InetAddress.class;
  }

  @Override
  public Object nullSafeGet(
      final ResultSet rs,
      final int position,
      final SharedSessionContractImplementor session,
      final Object owner)
      throws SQLException {
    try {
      final String value = rs.getString(position);
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
      st.setString(index, address.getHostAddress());
    } else {
      st.setNull(index, Types.VARCHAR);
    }
  }
}
