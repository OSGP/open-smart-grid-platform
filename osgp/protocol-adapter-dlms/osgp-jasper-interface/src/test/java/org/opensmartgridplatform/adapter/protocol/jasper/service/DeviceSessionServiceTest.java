// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
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
public class DeviceSessionServiceTest {
  private static final String IP_ADDRESS_CHANNEL = "ip_address";

  private static final String DEVICE_IDENTIFICATION = "device-identification";
  private static final String ICC_ID = "icc-id";
  private static final String IP_ADDRESS = "1.2.3.4";
  @Mock private DataSource dataSource;
  private final int maxWaitInMs = 654321;
  private DeviceSessionService deviceSessionService;

  @BeforeEach
  void setUp() {
    this.deviceSessionService = new DeviceSessionService(this.dataSource, this.maxWaitInMs);
  }

  @Test
  void testWaitForIpAddressNoNotification() throws SQLException {
    final PGConnection pgConnection = this.setupPgConnectionListen();

    when(pgConnection.getNotifications(this.maxWaitInMs)).thenReturn(null);

    final Optional<String> ipAddress =
        this.deviceSessionService.waitForIpAddress(DEVICE_IDENTIFICATION);

    assertThat(ipAddress).isEmpty();
  }

  @Test
  void testWaitForIpAddress() throws SQLException {
    final PGConnection pgConnection = this.setupPgConnectionListen();
    final PGNotification pgNotification =
        this.newPGNotification(this.getChannelName(DEVICE_IDENTIFICATION), IP_ADDRESS);

    when(pgConnection.getNotifications(this.maxWaitInMs)).thenReturn(Arrays.array(pgNotification));

    final Optional<String> ipAddress =
        this.deviceSessionService.waitForIpAddress(DEVICE_IDENTIFICATION);

    assertThat(ipAddress).isEqualTo(Optional.of(IP_ADDRESS));
  }

  @Test
  void testWaitForIpAddressThrowSqlException() throws SQLException {
    final PGConnection pgConnection = this.setupPgConnectionListen();

    when(pgConnection.getNotifications(this.maxWaitInMs)).thenThrow(new SQLException());

    final Optional<String> ipAddress =
        this.deviceSessionService.waitForIpAddress(DEVICE_IDENTIFICATION);

    assertThat(ipAddress).isEqualTo(Optional.empty());
  }

  @Test
  void testNotifyIpAddress() throws SQLException {
    this.setupPgConnectionNotify();

    this.deviceSessionService.notifyIpAddress(DEVICE_IDENTIFICATION, IP_ADDRESS);
  }

  @Test
  void testNotifyIpAddressThrowSqlException() throws SQLException {
    final Connection connection = mock(Connection.class);
    when(this.dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(
            String.format(
                "NOTIFY %s, '%s'", this.getChannelName(DEVICE_IDENTIFICATION), IP_ADDRESS)))
        .thenThrow(new SQLException());

    this.deviceSessionService.notifyIpAddress(DEVICE_IDENTIFICATION, IP_ADDRESS);
  }

  private void setupPgConnectionNotify() throws SQLException {
    final Connection connection = mock(Connection.class);
    final PreparedStatement preparedStatement = mock(PreparedStatement.class);

    when(this.dataSource.getConnection()).thenReturn(connection);
    when(connection.prepareStatement(
            String.format(
                "NOTIFY %s, '%s'", this.getChannelName(DEVICE_IDENTIFICATION), IP_ADDRESS)))
        .thenReturn(preparedStatement);
    when(preparedStatement.executeUpdate()).thenReturn(0);
  }

  private PGConnection setupPgConnectionListen() throws SQLException {
    final Connection connection = mock(Connection.class);
    final PGConnection pgConnection = mock(PGConnection.class);
    final PreparedStatement preparedStatement = mock(PreparedStatement.class);

    when(this.dataSource.getConnection()).thenReturn(connection);
    when(connection.unwrap(PGConnection.class)).thenReturn(pgConnection);
    when(connection.prepareStatement("LISTEN " + this.getChannelName(DEVICE_IDENTIFICATION)))
        .thenReturn(preparedStatement);
    when(preparedStatement.executeUpdate()).thenReturn(0);

    return pgConnection;
  }

  private String getChannelName(final String deviceIdentification) {
    return IP_ADDRESS_CHANNEL + "_" + deviceIdentification;
  }

  private PGNotification newPGNotification(final String name, final String parameter) {
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
        return parameter;
      }
    };
  }
}
