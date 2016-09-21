package com.alliander.osgp.simulator.protocol.iec61850.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PreDestroy;

import org.openmuc.openiec61850.BasicDataAttribute;
import org.openmuc.openiec61850.BdaFloat32;
import org.openmuc.openiec61850.BdaInt32;
import org.openmuc.openiec61850.BdaInt64;
import org.openmuc.openiec61850.BdaInt8;
import org.openmuc.openiec61850.BdaTimestamp;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.SclParseException;
import org.openmuc.openiec61850.ServerEventListener;
import org.openmuc.openiec61850.ServerModel;
import org.openmuc.openiec61850.ServerSap;
import org.openmuc.openiec61850.ServiceError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class RtuSimulator implements ServerEventListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(RtuSimulator.class);

    private final ServerSap server;

    private final ServerModel serverModel;

    private boolean isStarted = false;

    public RtuSimulator(final int port, final InputStream sclFile) throws SclParseException {
        final List<ServerSap> serverSaps = ServerSap.getSapsFromSclFile(sclFile);
        this.server = serverSaps.get(0);
        this.server.setPort(port);
        this.serverModel = this.server.getModelCopy();
    }

    public void start() throws IOException {
        if (this.isStarted) {
            throw new IOException("Server is already started");
        }

        this.server.startListening(this);
        this.isStarted = true;
    }

    public void stop() {
        this.server.stop();
        LOGGER.info("Server was stopped.");
    }

    @PreDestroy
    private void destroy() {
        this.stop();
    }

    @Override
    public List<ServiceError> write(final List<BasicDataAttribute> bdas) {
        for (final BasicDataAttribute bda : bdas) {
            LOGGER.info("got a write request: " + bda);
        }

        return null;
    }

    @Override
    public void serverStoppedListening(final ServerSap serverSAP) {
        LOGGER.error("The SAP stopped listening");
    }

    @Scheduled(fixedDelay = 60000)
    public void generateData() {
        final Date timestamp = new Date();

        final List<BasicDataAttribute> values = new ArrayList<BasicDataAttribute>();

        // Logical Device RTU1
        values.add(this.setRandomByte("WAGO61850ServerRTU1/LLN0.Health.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerRTU1/LLN0.Health.t", Fc.ST, timestamp));

        values.add(this.setRandomByte("WAGO61850ServerRTU1/LLN0.Beh.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerRTU1/LLN0.Beh.t", Fc.ST, timestamp));

        values.add(this.setRandomByte("WAGO61850ServerRTU1/LLN0.Mod.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerRTU1/LLN0.Mod.t", Fc.ST, timestamp));

        // Logical Device PV1
        values.add(this.setRandomByte("WAGO61850ServerPV1/LLN0.Health.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerPV1/LLN0.Health.t", Fc.ST, timestamp));

        values.add(this.setRandomByte("WAGO61850ServerPV1/LLN0.Beh.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerPV1/LLN0.Beh.t", Fc.ST, timestamp));

        values.add(this.setRandomByte("WAGO61850ServerPV1/LLN0.Mod.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerPV1/LLN0.Mod.t", Fc.ST, timestamp));

        values.add(this.setRandomFloat("WAGO61850ServerPV1/MMXU1.TotW.mag.f", Fc.MX, 0, 1000));
        values.add(this.setTime("WAGO61850ServerPV1/MMXU1.TotW.t", Fc.MX, timestamp));

        values.add(this.setRandomFloat("WAGO61850ServerPV1/DRCC1.OutWSet.subVal.f", Fc.SV, 0, 1000));

        values.add(this.setRandomFloat("WAGO61850ServerPV1/DGEN1.TotWh.mag.f", Fc.MX, 0, 1000));
        values.add(this.setTime("WAGO61850ServerPV1/DGEN1.TotWh.t", Fc.MX, timestamp));

        values.add(this.setRandomByte("WAGO61850ServerPV1/DGEN1.GnOpSt.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerPV1/DGEN1.GnOpSt.t", Fc.ST, timestamp));

        values.add(this.incrementInt("WAGO61850ServerPV1/DGEN1.OpTmsRs.stVal", Fc.ST));
        values.add(this.setTime("WAGO61850ServerPV1/DGEN1.OpTmsRs.t", Fc.ST, timestamp));

        // Logical Device PV2
        values.add(this.setRandomByte("WAGO61850ServerPV2/LLN0.Health.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerPV2/LLN0.Health.t", Fc.ST, timestamp));

        values.add(this.setRandomByte("WAGO61850ServerPV2/LLN0.Beh.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerPV2/LLN0.Beh.t", Fc.ST, timestamp));

        values.add(this.setRandomByte("WAGO61850ServerPV2/LLN0.Mod.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerPV2/LLN0.Mod.t", Fc.ST, timestamp));

        values.add(this.setRandomFloat("WAGO61850ServerPV2/MMXU1.TotW.mag.f", Fc.MX, 0, 1000));
        values.add(this.setTime("WAGO61850ServerPV2/MMXU1.TotW.t", Fc.MX, timestamp));

        values.add(this.setRandomFloat("WAGO61850ServerPV2/DRCC1.OutWSet.subVal.f", Fc.SV, 0, 1000));

        values.add(this.setRandomFloat("WAGO61850ServerPV2/DGEN1.TotWh.mag.f", Fc.MX, 0, 1000));
        values.add(this.setTime("WAGO61850ServerPV2/DGEN1.TotWh.t", Fc.MX, timestamp));

        values.add(this.setRandomByte("WAGO61850ServerPV2/DGEN1.GnOpSt.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerPV2/DGEN1.GnOpSt.t", Fc.ST, timestamp));

        values.add(this.incrementInt("WAGO61850ServerPV2/DGEN1.OpTmsRs.stVal", Fc.ST));
        values.add(this.setTime("WAGO61850ServerPV2/DGEN1.OpTmsRs.t", Fc.ST, timestamp));

        // Logical Device PV3
        values.add(this.setRandomByte("WAGO61850ServerPV3/LLN0.Health.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerPV3/LLN0.Health.t", Fc.ST, timestamp));

        values.add(this.setRandomByte("WAGO61850ServerPV3/LLN0.Beh.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerPV3/LLN0.Beh.t", Fc.ST, timestamp));

        values.add(this.setRandomByte("WAGO61850ServerPV3/LLN0.Mod.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerPV3/LLN0.Mod.t", Fc.ST, timestamp));

        values.add(this.setRandomFloat("WAGO61850ServerPV3/MMXU1.TotW.mag.f", Fc.MX, 0, 1000));
        values.add(this.setTime("WAGO61850ServerPV3/MMXU1.TotW.t", Fc.MX, timestamp));

        values.add(this.setRandomFloat("WAGO61850ServerPV3/DRCC1.OutWSet.subVal.f", Fc.SV, 0, 1000));

        values.add(this.setRandomFloat("WAGO61850ServerPV3/DGEN1.TotWh.mag.f", Fc.MX, 0, 1000));
        values.add(this.setTime("WAGO61850ServerPV3/DGEN1.TotWh.t", Fc.MX, timestamp));

        values.add(this.setRandomByte("WAGO61850ServerPV3/DGEN1.GnOpSt.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerPV3/DGEN1.GnOpSt.t", Fc.ST, timestamp));

        values.add(this.incrementInt("WAGO61850ServerPV3/DGEN1.OpTmsRs.stVal", Fc.ST));
        values.add(this.setTime("WAGO61850ServerPV3/DGEN1.OpTmsRs.t", Fc.ST, timestamp));

        // Logical Device BATTERY1
        values.add(this.setRandomByte("WAGO61850ServerBATTERY1/LLN0.Health.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerBATTERY1/LLN0.Health.t", Fc.ST, timestamp));

        values.add(this.setRandomByte("WAGO61850ServerBATTERY1/LLN0.Beh.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerBATTERY1/LLN0.Beh.t", Fc.ST, timestamp));

        values.add(this.setRandomByte("WAGO61850ServerBATTERY1/LLN0.Mod.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerBATTERY1/LLN0.Mod.t", Fc.ST, timestamp));

        values.add(this.setRandomFloat("WAGO61850ServerBATTERY1/MMXU1.TotW.mag.f", Fc.MX, 0, 1000));
        values.add(this.setTime("WAGO61850ServerBATTERY1/MMXU1.TotW.t", Fc.MX, timestamp));

        values.add(this.setRandomFloat("WAGO61850ServerBATTERY1/DRCC1.OutWSet.subVal.f", Fc.SV, 0, 1000));

        values.add(this.setRandomFloat("WAGO61850ServerBATTERY1/DGEN1.TotWh.mag.f", Fc.MX, 0, 1000));
        values.add(this.setTime("WAGO61850ServerBATTERY1/DGEN1.TotWh.t", Fc.MX, timestamp));

        values.add(this.setRandomByte("WAGO61850ServerBATTERY1/DGEN1.GnOpSt.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerBATTERY1/DGEN1.GnOpSt.t", Fc.ST, timestamp));

        values.add(this.setRandomFloat("WAGO61850ServerBATTERY1/MMXU1.TotPF.mag.f", Fc.MX, 0, 1000));
        values.add(this.setTime("WAGO61850ServerBATTERY1/MMXU1.TotPF.t", Fc.MX, timestamp));

        values.add(this.incrementInt("WAGO61850ServerBATTERY1/DGEN1.OpTmsRs.stVal", Fc.ST));
        values.add(this.setTime("WAGO61850ServerBATTERY1/DGEN1.OpTmsRs.t", Fc.ST, timestamp));

        // Logical Device BATTERY2
        values.add(this.setRandomByte("WAGO61850ServerBATTERY2/LLN0.Health.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerBATTERY2/LLN0.Health.t", Fc.ST, timestamp));

        values.add(this.setRandomByte("WAGO61850ServerBATTERY2/LLN0.Beh.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerBATTERY2/LLN0.Beh.t", Fc.ST, timestamp));

        values.add(this.setRandomByte("WAGO61850ServerBATTERY2/LLN0.Mod.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerBATTERY2/LLN0.Mod.t", Fc.ST, timestamp));

        values.add(this.setRandomFloat("WAGO61850ServerBATTERY2/MMXU1.TotW.mag.f", Fc.MX, 0, 1000));
        values.add(this.setTime("WAGO61850ServerBATTERY2/MMXU1.TotW.t", Fc.MX, timestamp));

        values.add(this.setRandomFloat("WAGO61850ServerBATTERY2/DRCC1.OutWSet.subVal.f", Fc.SV, 0, 1000));

        values.add(this.setRandomFloat("WAGO61850ServerBATTERY2/DGEN1.TotWh.mag.f", Fc.MX, 0, 1000));
        values.add(this.setTime("WAGO61850ServerBATTERY2/DGEN1.TotWh.t", Fc.MX, timestamp));

        values.add(this.setRandomByte("WAGO61850ServerBATTERY2/DGEN1.GnOpSt.stVal", Fc.ST, 1, 2));
        values.add(this.setTime("WAGO61850ServerBATTERY2/DGEN1.GnOpSt.t", Fc.ST, timestamp));

        values.add(this.setRandomFloat("WAGO61850ServerBATTERY2/MMXU1.TotPF.mag.f", Fc.MX, 0, 1000));
        values.add(this.setTime("WAGO61850ServerBATTERY2/MMXU1.TotPF.t", Fc.MX, timestamp));

        values.add(this.incrementInt("WAGO61850ServerBATTERY2/DGEN1.OpTmsRs.stVal", Fc.ST));
        values.add(this.setTime("WAGO61850ServerBATTERY2/DGEN1.OpTmsRs.t", Fc.ST, timestamp));

        // Logical Device LOAD1
        for (int i = 1; i <= 5; i++) {
            values.add(this.setFixedFloat("WAGO61850ServerLOAD1/MMXU" + i + ".TotW.mag.f", Fc.MX, i));
            values.add(this.setTime("WAGO61850ServerLOAD1/MMXU" + i + ".TotW.t", Fc.MX, timestamp));

            values.add(this.setFixedInt("WAGO61850ServerLOAD1/MMTR" + i + ".TotWh.actVal", Fc.ST, i));
            values.add(this.setTime("WAGO61850ServerLOAD1/MMTR" + i + ".TotWh.t", Fc.ST, timestamp));
        }

        this.server.setValues(values);
        LOGGER.info("Generated values");
    }

    private BasicDataAttribute incrementInt(final String node, final Fc fc) {
        final BdaInt32 value = (BdaInt32) this.serverModel.findModelNode(node, fc);
        value.setValue(value.getValue() + 1);
        return value;
    }

    private BasicDataAttribute setTime(final String node, final Fc fc, final Date date) {
        final BdaTimestamp value = (BdaTimestamp) this.serverModel.findModelNode(node, fc);
        value.setDate(date);
        return value;
    }

    private BasicDataAttribute setRandomFloat(final String node, final Fc fc, final int min, final int max) {
        final BdaFloat32 value = (BdaFloat32) this.serverModel.findModelNode(node, fc);
        value.setFloat((float) ThreadLocalRandom.current().nextInt(min, max));
        return value;
    }

    private BasicDataAttribute setFixedFloat(final String node, final Fc fc, final int val) {
        final BdaFloat32 value = (BdaFloat32) this.serverModel.findModelNode(node, fc);
        value.setFloat((float) val);
        return value;
    }

    private BasicDataAttribute setRandomByte(final String node, final Fc fc, final int min, final int max) {
        final BdaInt8 value = (BdaInt8) this.serverModel.findModelNode(node, fc);
        value.setValue((byte) ThreadLocalRandom.current().nextInt(min, max));
        return value;
    }

    private BasicDataAttribute setFixedInt(final String node, final Fc fc, final int val) {
        final BdaInt64 value = (BdaInt64) this.serverModel.findModelNode(node, fc);
        value.setValue((byte) val);
        return value;
    }

    private BasicDataAttribute setRandomInt(final String node, final Fc fc, final int min, final int max) {
        final BdaInt32 value = (BdaInt32) this.serverModel.findModelNode(node, fc);
        value.setValue(ThreadLocalRandom.current().nextInt(min, max));
        return value;
    }
}
