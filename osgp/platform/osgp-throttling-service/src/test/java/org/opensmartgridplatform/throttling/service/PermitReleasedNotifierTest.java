// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

@ExtendWith(MockitoExtension.class)
class PermitReleasedNotifierTest {
  private static final String PERMIT_RELEASED_CHANNEL = "permit_released";

  private static final int btsId = 1;
  private static final int cellId = 2;
  @Mock private DataSource dataSource;
  private final int maxWaitInMs = 654321;
  private PermitReleasedNotifier permitReleasedNotifier;

  @BeforeEach
  void setUp() {
    this.permitReleasedNotifier = new PermitReleasedNotifier(this.dataSource);
  }

  @Test
  void testWaitForPermitReleasedNoNotification() throws SQLException {
    final PGConnection pgConnection = this.setupPgConnectionListen();

    when(pgConnection.getNotifications(this.maxWaitInMs)).thenReturn(null);

    final boolean result =
        this.permitReleasedNotifier.waitForAvailablePermit(btsId, cellId, this.maxWaitInMs);

    assertThat(result).isFalse();
  }

  @Test
  void testWaitForPermitReleased() throws SQLException {
    final PGConnection pgConnection = this.setupPgConnectionListen();
    final PGNotification pgNotification =
        this.newPGNotification(this.getChannelName(btsId, cellId));

    when(pgConnection.getNotifications(this.maxWaitInMs)).thenReturn(Arrays.array(pgNotification));

    final boolean result =
        this.permitReleasedNotifier.waitForAvailablePermit(btsId, cellId, this.maxWaitInMs);

    assertThat(result).isTrue();
    verify(this.dataSource.getConnection()).close();
  }

  @Test
  void testWaitForPermitReleasedThrowSqlException() throws SQLException {
    final PGConnection pgConnection = this.setupPgConnectionListen();

    when(pgConnection.getNotifications(this.maxWaitInMs)).thenThrow(new SQLException());

    final boolean result =
        this.permitReleasedNotifier.waitForAvailablePermit(btsId, cellId, this.maxWaitInMs);

    assertThat(result).isFalse();
    verify(this.dataSource.getConnection()).close();
  }

  @Test
  void testNotifyPermitReleased() throws SQLException {
    this.setupPgConnectionNotify();

    this.permitReleasedNotifier.notifyPermitReleased(btsId, cellId);
    verify(this.dataSource.getConnection()).close();
  }

  @Test
  void testNotifyPermitReleasedThrowSqlException() throws SQLException {
    final Connection connection = mock(Connection.class);
    when(this.dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(
            String.format("NOTIFY %s", this.getChannelName(btsId, cellId))))
        .thenThrow(new SQLException());

    this.permitReleasedNotifier.notifyPermitReleased(btsId, cellId);
    verify(this.dataSource.getConnection()).close();
  }

  private void setupPgConnectionNotify() throws SQLException {
    final Connection connection = mock(Connection.class);
    final PreparedStatement preparedStatement = mock(PreparedStatement.class);

    when(this.dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(
            String.format("NOTIFY %s", this.getChannelName(btsId, cellId))))
        .thenReturn(preparedStatement);
    when(preparedStatement.executeUpdate()).thenReturn(0);
  }

  private PGConnection setupPgConnectionListen() throws SQLException {
    final Connection connection = mock(Connection.class);
    final PGConnection pgConnection = mock(PGConnection.class);
    final PreparedStatement preparedStatement = mock(PreparedStatement.class);

    when(this.dataSource.getConnection()).thenReturn(connection);
    when(connection.unwrap(PGConnection.class)).thenReturn(pgConnection);
    when(connection.prepareStatement("LISTEN " + this.getChannelName(btsId, cellId)))
        .thenReturn(preparedStatement);
    when(preparedStatement.executeUpdate()).thenReturn(0);

    return pgConnection;
  }

  private String getChannelName(final int btsId, final int cellId) {
    return PERMIT_RELEASED_CHANNEL + "_" + btsId + "_" + cellId;
  }

  private PGNotification newPGNotification(final String name) {
    return new PGNotification() {
      @Override
      public String getName() {
        return name;
      }

      @Override
      public int getPID() {
        return 666;
      }

      @Override
      public String getParameter() {
        return "";
      }
    };
  }
}
