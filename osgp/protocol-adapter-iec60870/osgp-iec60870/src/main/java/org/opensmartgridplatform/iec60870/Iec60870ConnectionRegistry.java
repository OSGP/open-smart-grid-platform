package org.opensmartgridplatform.iec60870;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openmuc.j60870.Connection;
import org.springframework.stereotype.Component;

@Component
public class Iec60870ConnectionRegistry {
    private Map<Integer, Connection> connections = new ConcurrentHashMap<>();

    public List<Connection> getAllConnections() {
        return new ArrayList<>(this.connections.values());
    }

    public Connection getConnection(final int id) {
        return this.connections.get(id);
    }

    public void registerConnection(final int id, final Connection connection) {
        this.connections.put(id, connection);
    }

    public void unregisterConnection(final int id) {
        this.connections.remove(id);
    }
}
