// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

@Slf4j
public class PermitReleasedNotifier {
  private static final String PERMIT_RELEASED_CHANNEL = "permit_released";
  private final DataSource dataSource;

  public PermitReleasedNotifier(final DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public boolean waitForAvailablePermit(
      final int baseTransceiverStationId, final int cellId, final int maxWaitInMs) {
    Connection connection = null;
    try {
      connection = this.dataSource.getConnection();
      final PGConnection pgConnection =
          this.startListening(connection, baseTransceiverStationId, cellId);

      final PGNotification[] notifications = pgConnection.getNotifications(maxWaitInMs);
      if (notifications == null) {
        log.info(
            "Received no notifications for btsId: {}, cellId: {} within {} ms",
            baseTransceiverStationId,
            cellId,
            maxWaitInMs);
        return false;
      }

      log.debug(
          "Received notification for btsId: {}, cellId: {}", baseTransceiverStationId, cellId);
    } catch (final SQLException sqle) {
      log.error(
          "SQLException occurred while listening for notification for btsId: {}, cellId: {}",
          baseTransceiverStationId,
          cellId,
          sqle);
      return false;
    } finally {
      this.closeConnection(connection, baseTransceiverStationId, cellId);
    }

    return true;
  }

  public void notifyPermitReleased(final int baseTransceiverStationId, final int cellId) {
    Connection connection = null;
    try {
      connection = this.dataSource.getConnection();

      final String sqlStatement =
          String.format("NOTIFY %s", this.getChannelName(baseTransceiverStationId, cellId));
      try (final PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
        preparedStatement.executeUpdate();
      }
    } catch (final SQLException sqle) {
      log.error(
          "SQLException occurred while notify for btsId: {}, cellId: {}",
          baseTransceiverStationId,
          cellId,
          sqle);
    } finally {
      this.closeConnection(connection, baseTransceiverStationId, cellId);
    }
  }

  private PGConnection startListening(
      final Connection connection, final int baseTransceiverStationId, final int cellId)
      throws SQLException {

    final PGConnection pgConnection = connection.unwrap(PGConnection.class);
    final String sqlStatement =
        String.format("LISTEN %s", this.getChannelName(baseTransceiverStationId, cellId));
    try (final PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
      preparedStatement.executeUpdate();
    }

    return pgConnection;
  }

  private void closeConnection(
      final Connection connection, final int baseTransceiverStationId, final int cellId) {
    try {
      if (connection != null) {
        connection.close();
      }
    } catch (final SQLException e) {
      log.error(
          "SQLException occurred while listening for notification for btsId: {}, cellId: {}",
          baseTransceiverStationId,
          cellId,
          e);
    }
  }

  private String getChannelName(final int baseTransceiverStationId, final int cellId) {
    return (PERMIT_RELEASED_CHANNEL + "_" + baseTransceiverStationId + "_" + cellId)
        .replace("-", "minus");
  }
}
