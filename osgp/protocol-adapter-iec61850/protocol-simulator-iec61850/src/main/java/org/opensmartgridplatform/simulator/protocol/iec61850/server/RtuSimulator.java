// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec61850.server;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.Rcb;
import com.beanit.openiec61850.SclParseException;
import com.beanit.openiec61850.SclParser;
import com.beanit.openiec61850.ServerEventListener;
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServerSap;
import com.beanit.openiec61850.ServiceError;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.eventproducers.ServerSapEventProducer;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.Battery;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.Boiler;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.Chp;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.Engine;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.GasFurnace;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.HeatBuffer;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.HeatPump;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.LightMeasurementRtu;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.Load;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.LogicalDevice;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.LogicalDeviceNode;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.Pq;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.Pv;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.Rtu;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.SwitchDevice;
import org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.Wind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class RtuSimulator implements ServerEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(RtuSimulator.class);

  private static final String PHYSICAL_DEVICE = "WAGO61850Server";

  private final List<LogicalDevice> logicalDevices = new ArrayList<>();

  private final ServerSap server;

  private final ServerModel serverModel;

  private final String serverName;

  private final ServerSapEventProducer serverSapEventProducer;

  private final Long updateValuesDelay;

  private final Long updateValuesPeriod;

  private boolean isStarted = false;

  private final AtomicBoolean stopGeneratingValues = new AtomicBoolean(false);

  public RtuSimulator(final int port, final InputStream sclFile, final String serverName)
      throws SclParseException {
    this(port, sclFile, serverName, null, null, null);
  }

  public RtuSimulator(
      final int port,
      final InputStream sclFile,
      final String serverName,
      final ServerSapEventProducer serverSapEventProducer,
      final Long updateValuesDelay,
      final Long updateValuesPeriod)
      throws SclParseException {

    final List<ServerModel> serverModels =
        SclParser.parse(IcdFileConverter.convertReportsForTesting(sclFile));
    this.server = new ServerSap(port, 0, null, serverModels.get(0), null);
    this.serverName = serverName;
    this.serverSapEventProducer = serverSapEventProducer;
    this.updateValuesDelay = updateValuesDelay;
    this.updateValuesPeriod = updateValuesPeriod;

    this.serverModel = this.server.getModelCopy();
    this.addLogicalDevices(this.serverModel);
  }

  public void ensureReportsDisabled() {
    for (final Rcb rcb : this.server.getModelCopy().getBrcbs()) {
      this.ensureReportDisabled(rcb);
    }
    for (final Rcb rcb : this.server.getModelCopy().getUrcbs()) {
      this.ensureReportDisabled(rcb);
    }
  }

  public void assertAllReportsEnabled() {
    for (final Rcb rcb : this.server.getModelCopy().getBrcbs()) {
      this.assertReportEnabled(rcb);
    }
    for (final Rcb rcb : this.server.getModelCopy().getUrcbs()) {
      this.assertReportEnabled(rcb);
    }
  }

  public void assertNoReportsEnabled() {
    for (final Rcb rcb : this.server.getModelCopy().getBrcbs()) {
      this.assertReportNotEnabled(rcb);
    }
    for (final Rcb rcb : this.server.getModelCopy().getUrcbs()) {
      this.assertReportNotEnabled(rcb);
    }
  }

  private void ensureReportDisabled(final Rcb rcb) {
    rcb.getRptEna().setValue(false);
    this.server.setValues(Arrays.asList(rcb.getRptEna()));
  }

  private void assertReportEnabled(final Rcb rcb) {
    if (!rcb.getRptEna().getValue()) {
      throw new AssertionError("Report " + rcb.getReference() + " should be enabled");
    }
  }

  private void assertReportNotEnabled(final Rcb rcb) {
    if (rcb.getRptEna().getValue()) {
      throw new AssertionError("Report " + rcb.getReference() + " should not be enabled");
    }
  }

  private void addLogicalDevices(final ServerModel serverModel) {
    this.addRtuDevices(serverModel);
    this.addPvDevices(serverModel);
    this.addBatteryDevices(serverModel);
    this.addEngineDevices(serverModel);
    this.addLoadDevices(serverModel);
    this.addHeatBufferDevices(serverModel);
    this.addChpDevices(serverModel);
    this.addGasFurnaceDevices(serverModel);
    this.addHeatPumpDevices(serverModel);
    this.addBoilerDevices(serverModel);
    this.addWindDevices(serverModel);
    this.addLightMeasurementDevice(serverModel);
    this.addPqDevices(serverModel);
    this.addSwitchDevices(serverModel);
  }

  private void addRtuDevices(final ServerModel serverModel) {
    final String rtuPrefix = "RTU";
    int i = 1;
    String logicalDeviceName = rtuPrefix + i;
    ModelNode rtuNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    while (rtuNode != null) {
      this.logicalDevices.add(new Rtu(this.getDeviceName(), logicalDeviceName, serverModel));
      i += 1;
      logicalDeviceName = rtuPrefix + i;
      rtuNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    }
  }

  private void addPvDevices(final ServerModel serverModel) {
    final String pvPrefix = "PV";
    int i = 1;
    String logicalDeviceName = pvPrefix + i;
    ModelNode pvNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    while (pvNode != null) {
      this.logicalDevices.add(new Pv(this.getDeviceName(), logicalDeviceName, serverModel));
      i += 1;
      logicalDeviceName = pvPrefix + i;
      pvNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    }
  }

  private void addBatteryDevices(final ServerModel serverModel) {
    final String batteryPrefix = "BATTERY";
    int i = 1;
    String logicalDeviceName = batteryPrefix + i;
    ModelNode batteryNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    while (batteryNode != null) {
      this.logicalDevices.add(new Battery(this.getDeviceName(), logicalDeviceName, serverModel));
      i += 1;
      logicalDeviceName = batteryPrefix + i;
      batteryNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    }
  }

  private void addEngineDevices(final ServerModel serverModel) {
    final String enginePrefix = "ENGINE";
    int i = 1;
    String logicalDeviceName = enginePrefix + i;
    ModelNode engineNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    while (engineNode != null) {
      this.logicalDevices.add(new Engine(this.getDeviceName(), logicalDeviceName, serverModel));
      i += 1;
      logicalDeviceName = enginePrefix + i;
      engineNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    }
  }

  private void addLoadDevices(final ServerModel serverModel) {
    final String loadPrefix = "LOAD";
    int i = 1;
    String logicalDeviceName = loadPrefix + i;
    ModelNode loadNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    while (loadNode != null) {
      this.logicalDevices.add(new Load(this.getDeviceName(), logicalDeviceName, serverModel));
      i += 1;
      logicalDeviceName = loadPrefix + i;
      loadNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    }
  }

  private void addHeatBufferDevices(final ServerModel serverModel) {
    final String heatBufferPrefix = "HEAT_BUFFER";
    int i = 1;
    String logicalDeviceName = heatBufferPrefix + i;
    ModelNode heatBufferNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    while (heatBufferNode != null) {
      this.logicalDevices.add(new HeatBuffer(this.getDeviceName(), logicalDeviceName, serverModel));
      i += 1;
      logicalDeviceName = heatBufferPrefix + i;
      heatBufferNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    }
  }

  private void addChpDevices(final ServerModel serverModel) {
    final String chpPrefix = "CHP";
    int i = 1;
    String logicalDeviceName = chpPrefix + i;
    ModelNode chpNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    while (chpNode != null) {
      this.logicalDevices.add(new Chp(this.getDeviceName(), logicalDeviceName, serverModel));
      i += 1;
      logicalDeviceName = chpPrefix + i;
      chpNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    }
  }

  private void addGasFurnaceDevices(final ServerModel serverModel) {
    final String gasFurnacePrefix = "GAS_FURNACE";
    int i = 1;
    String logicalDeviceName = gasFurnacePrefix + i;
    ModelNode gasFurnaceNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    while (gasFurnaceNode != null) {
      this.logicalDevices.add(new GasFurnace(this.getDeviceName(), logicalDeviceName, serverModel));
      i += 1;
      logicalDeviceName = gasFurnacePrefix + i;
      gasFurnaceNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    }
  }

  private void addHeatPumpDevices(final ServerModel serverModel) {
    final String heatPumpPrefix = "HEAT_PUMP";
    int i = 1;
    String logicalDeviceName = heatPumpPrefix + i;
    ModelNode heatPumpNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    while (heatPumpNode != null) {
      this.logicalDevices.add(new HeatPump(this.getDeviceName(), logicalDeviceName, serverModel));
      i += 1;
      logicalDeviceName = heatPumpPrefix + i;
      heatPumpNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    }
  }

  private void addBoilerDevices(final ServerModel serverModel) {
    final String boilerPrefix = "BOILER";
    int i = 1;
    String logicalDeviceName = boilerPrefix + i;
    ModelNode boilerNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    while (boilerNode != null) {
      this.logicalDevices.add(new Boiler(this.getDeviceName(), logicalDeviceName, serverModel));
      i += 1;
      logicalDeviceName = boilerPrefix + i;
      boilerNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    }
  }

  private void addWindDevices(final ServerModel serverModel) {
    final String windPrefix = "WIND";
    int i = 1;
    String logicalDeviceName = windPrefix + i;
    ModelNode windNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    while (windNode != null) {
      this.logicalDevices.add(new Wind(this.getDeviceName(), logicalDeviceName, serverModel));
      i += 1;
      logicalDeviceName = windPrefix + i;
      windNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    }
  }

  private void addLightMeasurementDevice(final ServerModel serverModel) {
    final String logicalDeviceName = "LD0";
    final ModelNode lightMeasurementRtuNode =
        serverModel.getChild(this.getDeviceName() + logicalDeviceName);

    if (lightMeasurementRtuNode != null) {
      // Light Measurement RTU found in the server model.
      LOGGER.info("Adding lmRtu {}", logicalDeviceName);
      this.logicalDevices.add(
          new LightMeasurementRtu(this.getDeviceName(), logicalDeviceName, serverModel));
    }
  }

  private void addSwitchDevices(final ServerModel serverModel) {
    final String logicalDeviceName = "IO";
    final ModelNode switchDevice = serverModel.getChild(this.getDeviceName() + logicalDeviceName);

    if (switchDevice != null) {
      LOGGER.info("Adding switchDevice {}", this.getDeviceName());
      this.logicalDevices.add(
          new SwitchDevice(this.getDeviceName(), logicalDeviceName, serverModel));
    }
  }

  private void addPqDevices(final ServerModel serverModel) {
    final String pqPrefix = "PQ";
    int i = 1;
    String logicalDeviceName = pqPrefix + i;
    ModelNode pqNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    while (pqNode != null) {
      this.logicalDevices.add(new Pq(this.getDeviceName(), logicalDeviceName, serverModel));
      i += 1;
      logicalDeviceName = pqPrefix + i;
      pqNode = serverModel.getChild(this.getDeviceName() + logicalDeviceName);
    }
  }

  public void start() throws IOException {
    if (this.isStarted) {
      throw new IOException("Server is already started");
    }

    this.server.startListening(this);
    if (this.serverSapEventProducer != null) {
      this.serverSapEventProducer.scheduleAtFixedRate(
          this.server, this.updateValuesDelay, this.updateValuesPeriod);
    }
    this.isStarted = true;
  }

  public void stop() {
    this.server.stop();
    this.isStarted = false;
    LOGGER.info("Server was stopped.");
  }

  @PreDestroy
  private void destroy() {
    this.stop();
  }

  @Override
  public List<ServiceError> write(final List<BasicDataAttribute> bdas) {
    for (final BasicDataAttribute bda : bdas) {
      LOGGER.info("got a write request: {}", bda);
      this.writeValueAndUpdateRelatedAttributes(bda);
    }

    return new ArrayList<>();
  }

  /**
   * Writes an updated value for an attribute to the server model. This attribute update can also
   * trigger updates to other attributes. Those updates are also handled.
   *
   * @param bda The attribute that has been updated.
   */
  private void writeValueAndUpdateRelatedAttributes(final BasicDataAttribute bda) {
    final String logicalNodeSeparator = "/";
    final Pattern pattern =
        Pattern.compile(this.serverName + "(.*?)" + logicalNodeSeparator + "(.*?):");
    final Matcher matcher = pattern.matcher(bda.toString());
    if (matcher.find()) {
      final String logicalDeviceName = matcher.group(1);
      final String node = matcher.group(2);

      final LogicalDevice logicalDevice = this.getLogicalDevice(logicalDeviceName);
      final List<BasicDataAttribute> updatedAttributes =
          logicalDevice.writeValueAndUpdateRelatedAttributes(node, bda);
      this.server.setValues(updatedAttributes);
    }
  }

  @Override
  public void serverStoppedListening(final ServerSap serverSAP) {
    LOGGER.error("The SAP stopped listening");
  }

  public void assertValue(final String logicalDeviceName, final String node, final String value) {
    final LogicalDevice logicalDevice = this.getLogicalDevice(logicalDeviceName);
    // Get a new model copy to see values that have been set on the server.
    logicalDevice.refreshServerModel(this.server.getModelCopy());
    final ModelNode actual =
        logicalDevice.getBasicDataAttribute(LogicalDeviceNode.fromDescription(node));

    final String onLogicalDevice = "\" on logical device \"";

    if (actual == null) {
      throw new AssertionError(
          "RTU Simulator does not have expected node \""
              + node
              + onLogicalDevice
              + logicalDeviceName
              + "\".");
    }
    if (!(actual instanceof BasicDataAttribute)) {
      throw new AssertionError(
          "RTU Simulator value has node \""
              + node
              + onLogicalDevice
              + logicalDeviceName
              + "\", but it is not a BasicDataAttribute: "
              + actual.getClass().getName());
    }
    final BasicDataAttribute expected =
        this.getCopyWithNewValue((BasicDataAttribute) actual, value);
    if (!BasicDataAttributesHelper.attributesEqual(expected, (BasicDataAttribute) actual)) {
      throw new AssertionError(
          "RTU Simulator attribute for node \""
              + node
              + onLogicalDevice
              + logicalDeviceName
              + "\" - expected: ["
              + expected
              + "], actual: ["
              + actual
              + "]");
    }
  }

  private BasicDataAttribute getCopyWithNewValue(
      final BasicDataAttribute original, final String value) {
    final BasicDataAttribute copy = (BasicDataAttribute) original.copy();
    BasicDataAttributesHelper.setValue(copy, value);
    return copy;
  }

  public void mockValue(final String logicalDeviceName, final String node, final String value) {
    if (!this.stopGeneratingValues.get()) {
      /*
       * A mocked value is explicitly set, stop changing values with
       * generateData, because one of those might break the mock value
       * that will be expected.
       */
      this.ensurePeriodicDataGenerationIsStopped();
    }
    final LogicalDevice logicalDevice = this.getLogicalDevice(logicalDeviceName);
    final BasicDataAttribute basicDataAttribute =
        logicalDevice.getAttributeAndSetValue(LogicalDeviceNode.fromDescription(node), value);
    this.server.setValues(Arrays.asList(basicDataAttribute));
  }

  public void ensurePeriodicDataGenerationIsStopped() {
    synchronized (this.stopGeneratingValues) {
      this.stopGeneratingValues.set(true);
    }
  }

  public void resumeGeneratingValues() {
    synchronized (this.stopGeneratingValues) {
      this.stopGeneratingValues.set(false);
    }
  }

  private LogicalDevice getLogicalDevice(final String logicalDeviceName) {
    for (final LogicalDevice ld : this.logicalDevices) {
      if (ld.getLogicalDeviceName().equals(logicalDeviceName)) {
        return ld;
      }
    }
    throw new IllegalArgumentException(
        "A logical device with name \""
            + logicalDeviceName
            + "\" is not registered with simulated RTU device \""
            + this.getDeviceName()
            + "\".");
  }

  @Scheduled(fixedDelay = 60000)
  public void generateData() {
    synchronized (this.stopGeneratingValues) {
      if (!this.stopGeneratingValues.get()) {
        final Date timestamp = new Date();

        final List<BasicDataAttribute> values = new ArrayList<>();

        for (final LogicalDevice ld : this.logicalDevices) {
          try {
            values.addAll(ld.getAttributesAndSetValues(timestamp));
          } catch (final Exception e) {
            LOGGER.info("Exception while generating values.", e);
          }
        }

        this.server.setValues(values);
        LOGGER.info("Generated values");
      }
    }
  }

  private String getDeviceName() {
    if ((this.serverName != null) && !this.serverName.isEmpty()) {
      return this.serverName;
    } else {
      return PHYSICAL_DEVICE;
    }
  }
}
