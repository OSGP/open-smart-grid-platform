package org.opensmartgridplatform.simulator.protocol.iec61850.domain.services;

@FunctionalInterface
public interface ValueProvider<T> {

    T getValue();
}
