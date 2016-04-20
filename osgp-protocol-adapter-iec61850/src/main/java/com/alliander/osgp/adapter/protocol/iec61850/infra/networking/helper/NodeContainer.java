package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper;

import java.io.IOException;
import java.util.Date;

import org.openmuc.openiec61850.BdaTimestamp;
import org.openmuc.openiec61850.BdaVisibleString;
import org.openmuc.openiec61850.FcModelNode;
import org.openmuc.openiec61850.ServiceError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeContainer.class);

    private final DeviceConnection connection;
    private final FcModelNode parent;

    public NodeContainer(final DeviceConnection connection, final FcModelNode fcmodelNode) {
        this.connection = connection;
        this.parent = fcmodelNode;
    }

    /**
     * Returns a String for {@link BdaVisibleString} values
     */
    public String getString(final SubDataAttribute child) {

        // TODO check to see if it's a BdaVisibleString
        final BdaVisibleString bdaString = (BdaVisibleString) this.parent.getChild(child.getDescription());

        if (bdaString == null) {
            // TODO exceptionHandling, null probably means the node doesn't
            // exist
        }

        LOGGER.info("device: {}, {} has value {}", this.connection.getDeviceIdentification(), child.getDescription(),
                bdaString.getStringValue());

        return bdaString.getStringValue();
    }

    /**
     * Writes a String value to the given child on the device
     */
    public void writeString(final SubDataAttribute child, final String value) {

        final BdaVisibleString stringNode = (BdaVisibleString) this.parent.getChild(child.getDescription());

        LOGGER.info("device: {}, writing {} to {}", this.connection.getDeviceIdentification(), value,
                child.getDescription());

        stringNode.setValue(value);

        this.writeNode(stringNode);

    }

    /**
     * Returns a {@link Date} for {@link BdaTimestamp} values
     */
    public Date getDate(final SubDataAttribute child) {

        // TODO check to see if it's a BdaVisibleString
        final BdaTimestamp dBdaTimestamp = (BdaTimestamp) this.parent.getChild(child.getDescription());

        if (dBdaTimestamp == null) {
            // TODO exceptionHandling, null probably means the node doesn't
            // exist
        }

        LOGGER.info("device: {}, {} has value {}", this.connection.getDeviceIdentification(), child.getDescription(),
                dBdaTimestamp.getDate());

        return dBdaTimestamp.getDate();

    }

    /**
     * Writes a Date value to the given child on the device
     */
    public void writeDate(final SubDataAttribute child, final Date value) {

        final BdaTimestamp dBdaTimestamp = (BdaTimestamp) this.parent.getChild(child.getDescription());

        LOGGER.info("device: {}, writing {} to {}", this.connection.getDeviceIdentification(), value,
                child.getDescription());

        dBdaTimestamp.setDate(value);

        this.writeNode(dBdaTimestamp);

    }

    // TODO Int, Boolean, Date

    /*
     * Writes the new data of the node to the device
     */
    private void writeNode(final FcModelNode node) {
        // TODO move retry mechanism here

        try {
            this.connection.getConnection().getClientAssociation().setDataValues(node);
        } catch (final ServiceError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {

            // "if a fatal association error occurs. The association object will be closed and can no longer be used after this exception is thrown."

            // so reconnect?

            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // GETTERS AND SETTERS
    public FcModelNode getFcmodelNode() {
        return this.parent;
    }

}
