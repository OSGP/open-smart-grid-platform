package org.opensmartgridplatform.simulator.protocol.iec61850.domain.services;

import org.opensmartgridplatform.simulator.protocol.iec61850.domain.exceptions.EndOfSimulationException;

@FunctionalInterface
public interface ValueProvider<T> {

    T getValue() throws EndOfSimulationException;
}
