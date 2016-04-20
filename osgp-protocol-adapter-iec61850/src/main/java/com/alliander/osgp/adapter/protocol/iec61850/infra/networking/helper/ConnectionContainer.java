package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper;

import org.openmuc.openiec61850.ClientAssociation;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.FcModelNode;
import org.openmuc.openiec61850.ObjectReference;
import org.openmuc.openiec61850.ServerModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionContainer.class);

    private final ClientAssociation clientAssociation;
    private final ServerModel serverModel;
    private final String deviceIdentification;

    public static final String LOGICAL_DEVICE_PREFIX = "SWDeviceGenericIO";
    public static final String LOGICAL_NODE_SEPARATOR = "/";
    public static final String DATA_ATTRIBUTE_SEPARATOR = ".";

    public ConnectionContainer(final ClientAssociation clientAssociation, final ServerModel serverModel,
            final String deviceIdentification) {
        this.clientAssociation = clientAssociation;
        this.serverModel = serverModel;
        this.deviceIdentification = deviceIdentification;
    }

    /**
     * Returns a {@link NodeContainer} for the given {@link ObjectReference}
     * data and the Functional constraint.
     */
    public NodeContainer GetFcModelNode(final LogicalNode logicalNode, final DataAttribute dataAttribute, final Fc fc) {

        final FcModelNode fcModelNode = (FcModelNode) this.serverModel.findModelNode(
                this.createObjectReference(logicalNode, dataAttribute), fc);
        if (fcModelNode == null) {
            // TODO exceptionHandling, null probably means the node doesn't
            // exist
        }

        return new NodeContainer(this, fcModelNode);

    }

    /*
     * Creates a correct ObjectReference.
     */
    private ObjectReference createObjectReference(final LogicalNode logicalNode, final DataAttribute dataAttribute) {

        final String objectReference = LOGICAL_DEVICE_PREFIX.concat(LOGICAL_NODE_SEPARATOR)
                .concat(logicalNode.getDescription()).concat(DATA_ATTRIBUTE_SEPARATOR)
                .concat(dataAttribute.getDescription());

        LOGGER.info("Device: {}, ObjectReference: {}", this.deviceIdentification, objectReference);

        return new ObjectReference(objectReference);
    }

    // GETTERS AND SETTERS

    public ClientAssociation getClientAssociation() {
        return this.clientAssociation;
    }

    public ServerModel getServerModel() {
        return this.serverModel;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }
}
