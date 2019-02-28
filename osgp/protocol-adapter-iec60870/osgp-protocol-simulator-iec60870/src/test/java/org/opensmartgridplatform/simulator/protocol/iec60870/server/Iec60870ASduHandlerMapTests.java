package org.opensmartgridplatform.simulator.protocol.iec60870.server;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openmuc.j60870.TypeId;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers.Iec60870InterrogationCommandASduHandler;
import org.opensmartgridplatform.simulator.protocol.iec60870.server.handlers.Iec60870SingleCommandASduHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

// SpringBootTest annotation needed here, to resolve autowired fields
@SpringBootTest
public class Iec60870ASduHandlerMapTests {
    // Mock the RTU Simulator to prevent the simulator from starting, which
    // might cause
    // 'address already in use' exceptions while running tests
    @MockBean
    Iec60870RtuSimulator iec60870RtuSimulator;

    @Autowired
    Iec60870ASduHandlerMap iec60870aSduHandlerMap;

    @Autowired
    Iec60870InterrogationCommandASduHandler iec60870InterrogationCommandASduHandler;

    @Autowired
    Iec60870SingleCommandASduHandler iec60870SingleCommandASduHandler;

    @Test
    public void mapShouldReturnInterrogationCommandHandlerForInterrogationCommandTypeId() {
        // arrange
        final TypeId typeId = TypeId.C_IC_NA_1;
        final Iec60870InterrogationCommandASduHandler expected = this.iec60870InterrogationCommandASduHandler;

        // act
        final Iec60870ASduHandler actual = this.iec60870aSduHandlerMap.getHandler(typeId);

        // assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void mapShouldReturnSingleCommandHandlerForSingleCommandTypeId() {
        // arrange
        final TypeId typeId = TypeId.C_SC_NA_1;
        final Iec60870SingleCommandASduHandler expected = this.iec60870SingleCommandASduHandler;

        // act
        final Iec60870ASduHandler actual = this.iec60870aSduHandlerMap.getHandler(typeId);

        // assert
        assertThat(actual).isEqualTo(expected);
    }
}
