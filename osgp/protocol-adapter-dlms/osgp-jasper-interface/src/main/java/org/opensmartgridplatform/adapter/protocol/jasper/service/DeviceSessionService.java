// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

@Slf4j
public class DeviceSessionService {
  private static final String IP_ADDRESS_CHANNEL = "ip_address";
  private final DataSource dataSource;
  private final int maxWaitInMs;

  public DeviceSessionService(final DataSource dataSource, final int maxWaitInMs) {
    this.dataSource = dataSource;
    this.maxWaitInMs = maxWaitInMs;
  }

  public Optional<String> waitForIpAddress(final String deviceIdentification) {
    String ipAddress = null;
    try {
      final PGConnection pgConnection =
          this.getListenerConnection(this.dataSource.getConnection(), deviceIdentification);

      final PGNotification[] notifications = pgConnection.getNotifications(this.maxWaitInMs);
      if (notifications == null) {
        log.info(
            "Received no notifications for device {} within {} ms",
            deviceIdentification,
            this.maxWaitInMs);
        return Optional.empty();
      }

      for (final PGNotification notification : notifications) {
        ipAddress = notification.getParameter();
        log.info(
            "Received notification with ipAddress {} for device {}",
            ipAddress,
            deviceIdentification);
      }
    } catch (final SQLException sqle) {
      log.error(
          "SQLException occurred while listening for notification for device: "
              + deviceIdentification,
          sqle);
    }

    return Optional.ofNullable(ipAddress);
  }

  public void notifyIpAddress(final String deviceIdentification, final String ipAddress) {
    try {
      final Connection connection = this.dataSource.getConnection();

      final String sqlStatement =
          String.format("NOTIFY %s, '%s'", this.getChannelName(deviceIdentification), ipAddress);
      try (final PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
        preparedStatement.executeUpdate();
      }
    } catch (final SQLException sqle) {
      log.error("SQLException occurred while notify for device: " + deviceIdentification, sqle);
    }
  }

  private PGConnection getListenerConnection(
      final Connection connection, final String deviceIdentification) throws SQLException {

    final PGConnection pgConnection = connection.unwrap(PGConnection.class);
    final String sqlStatement =
        String.format("LISTEN %s", this.getChannelName(deviceIdentification));
    try (final PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
      preparedStatement.executeUpdate();
    }

    return pgConnection;
  }

  private String getChannelName(final String deviceIdentification) {
    return IP_ADDRESS_CHANNEL + "_" + deviceIdentification;
  }
}
