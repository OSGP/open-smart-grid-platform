//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting;

import com.beanit.openiec61850.Brcb;
import com.beanit.openiec61850.ClientAssociation;
import com.beanit.openiec61850.Rcb;
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServiceError;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.entities.Iec61850Device;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.entities.Iec61850DeviceReportGroup;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.entities.Iec61850Report;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.entities.Iec61850ReportEntry;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.entities.Iec61850ReportGroup;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.repositories.Iec61850DeviceReportGroupRepository;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.repositories.Iec61850DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.repositories.Iec61850ReportEntryRepository;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeReadException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeWriteException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.IED;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Iec61850RtuDeviceReportingService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Iec61850RtuDeviceReportingService.class);

  @Autowired private Iec61850DeviceRepository iec61850DeviceRepository;

  @Autowired private Iec61850DeviceReportGroupRepository iec61850DeviceReportRepository;

  @Autowired private Iec61850ReportEntryRepository iec61850ReportEntryRepository;

  @Autowired private Iec61850Client client;

  public void enableReportingForDevice(
      final DeviceConnection connection,
      final String deviceIdentification,
      final String serverName) {
    if (connection.getConnection().getIed() != null
        && IED.FLEX_OVL.equals(connection.getConnection().getIed())) {
      // We don't need 'enableReportingForDevice()' logic for FLEX_OVL
      // devices.
      return;
    }

    try {
      final Iec61850Device device =
          this.iec61850DeviceRepository.findByDeviceIdentification(deviceIdentification);

      if (device.isEnableAllReportsOnConnect()) {
        this.enableAllReports(connection, deviceIdentification);
      } else {
        this.enableSpecificReports(connection, deviceIdentification, serverName);
      }
    } catch (final NullPointerException npe) {
      LOGGER.error(
          "Caught null pointer exception, is Iec61850Device.enableAllReportsOnConnect not set in database?",
          npe);
    } catch (final Exception e) {
      LOGGER.error("Caught unexpected exception", e);
    }
  }

  private void enableAllReports(
      final DeviceConnection connection, final String deviceIdentification) {
    final ServerModel serverModel = connection.getConnection().getServerModel();

    this.enableReports(connection, deviceIdentification, serverModel.getBrcbs());
    this.enableReports(connection, deviceIdentification, serverModel.getUrcbs());
  }

  private void enableReports(
      final DeviceConnection connection,
      final String deviceIdentification,
      final Collection<? extends Rcb> reports) {
    for (final Rcb report : reports) {
      final String reportReference = report.getReference().toString();
      try {
        LOGGER.info(
            "Enable reporting for report {} on device {}.", reportReference, deviceIdentification);

        if (report instanceof Brcb) {
          this.resyncBufferedReport(connection, deviceIdentification, (Brcb) report);
        }

        final NodeContainer node = new NodeContainer(connection, report);

        node.writeBoolean(SubDataAttribute.ENABLE_REPORTING, true);
      } catch (final NullPointerException e) {
        LOGGER.debug("NullPointerException", e);
        LOGGER.warn(
            "Skip enable reporting for report {} on device {}.",
            reportReference,
            deviceIdentification);
      } catch (final NodeWriteException e) {
        LOGGER.debug("NodeWriteException", e);
        LOGGER.error(
            "Enable reporting for report {} on device {} failed with exception: {}",
            reportReference,
            deviceIdentification,
            e.getMessage());
      }
    }
  }

  private void resyncBufferedReport(
      final DeviceConnection connection, final String deviceIdentification, final Brcb brcb) {

    final NodeContainer node = new NodeContainer(connection, brcb);

    try {
      this.client.readNodeDataValues(
          connection.getConnection().getClientAssociation(), node.getFcmodelNode());
    } catch (final NodeReadException e) {
      LOGGER.debug("NodeReadException", e);
      LOGGER.error(
          "Resync reporting failed, could not read report id from device {}, exception: {}",
          deviceIdentification,
          e.getMessage());
      return;
    }
    final String reportId = node.getString(SubDataAttribute.REPORT_ID);

    LOGGER.debug("Resync reporting for report {} on device {}", reportId, deviceIdentification);

    final Iec61850ReportEntry reportEntry =
        this.iec61850ReportEntryRepository.findByDeviceIdentificationAndReportId(
            deviceIdentification, reportId);
    if (reportEntry == null) {
      LOGGER.info(
          "Resync reporting for report {} on device {} not possible, no last report entry found",
          reportId,
          deviceIdentification);
    } else {
      LOGGER.info(
          "Resync reporting for report {} on device {} with last report entry: {}",
          reportId,
          deviceIdentification,
          reportEntry);
      try {
        node.writeOctetString(SubDataAttribute.ENTRY_ID, reportEntry.getEntryId());
      } catch (final NodeWriteException e) {
        LOGGER.debug("NodeWriteException", e);
        LOGGER.error(
            "Resync reporting for report {} on device {} failed with exception: {}",
            reportId,
            deviceIdentification,
            e.getMessage());
      }
    }
  }

  private void enableSpecificReports(
      final DeviceConnection connection,
      final String deviceIdentification,
      final String serverName) {

    final ServerModel serverModel = connection.getConnection().getServerModel();
    final ClientAssociation clientAssociation = connection.getConnection().getClientAssociation();

    final List<Iec61850DeviceReportGroup> deviceReportGroups =
        this.iec61850DeviceReportRepository.findByDeviceIdentificationAndEnabled(
            deviceIdentification, true);
    for (final Iec61850DeviceReportGroup deviceReportGroup : deviceReportGroups) {
      this.enableReportGroup(
          serverName,
          deviceIdentification,
          deviceReportGroup.getIec61850ReportGroup(),
          serverModel,
          clientAssociation);
    }
  }

  private void enableReportGroup(
      final String serverName,
      final String deviceIdentification,
      final Iec61850ReportGroup reportGroup,
      final ServerModel serverModel,
      final ClientAssociation clientAssociation) {
    for (final Iec61850Report iec61850Report : reportGroup.getIec61850Reports()) {
      this.enableReport(
          serverName, deviceIdentification, iec61850Report, serverModel, clientAssociation);
    }
  }

  private void enableReport(
      final String serverName,
      final String deviceIdentification,
      final Iec61850Report iec61850Report,
      final ServerModel serverModel,
      final ClientAssociation clientAssociation) {
    int i = 1;
    Rcb rcb =
        this.getRcb(
            serverModel,
            this.getReportNode(
                serverName, iec61850Report.getLogicalDevice(), i, iec61850Report.getLogicalNode()));
    while (rcb != null) {
      this.enableRcb(deviceIdentification, clientAssociation, rcb);
      i += 1;
      rcb =
          this.getRcb(
              serverModel,
              this.getReportNode(
                  serverName,
                  iec61850Report.getLogicalDevice(),
                  i,
                  iec61850Report.getLogicalNode()));
    }
  }

  private String getReportNode(
      final String serverName,
      final String logicalDevice,
      final int index,
      final String reportNode) {
    return serverName + logicalDevice + index + "/" + reportNode;
  }

  private Rcb getRcb(final ServerModel serverModel, final String node) {
    Rcb rcb = serverModel.getBrcb(node);
    if (rcb == null) {
      rcb = serverModel.getUrcb(node);
    }
    return rcb;
  }

  private void enableRcb(
      final String deviceIdentification, final ClientAssociation clientAssociation, final Rcb rcb) {
    try {
      clientAssociation.enableReporting(rcb);
    } catch (final IOException e) {
      LOGGER.error(
          "IOException: unable to enable reporting for deviceIdentification "
              + deviceIdentification,
          e);
    } catch (final ServiceError e) {
      LOGGER.error(
          "ServiceError: unable to enable reporting for deviceIdentification "
              + deviceIdentification,
          e);
    }
  }
}
