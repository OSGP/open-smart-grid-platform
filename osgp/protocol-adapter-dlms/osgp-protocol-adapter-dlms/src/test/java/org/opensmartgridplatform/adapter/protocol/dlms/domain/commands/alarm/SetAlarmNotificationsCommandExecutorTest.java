package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import org.junit.Before;
import org.junit.Test;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.CommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigConfiguration;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionManagerStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmNotificationDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmNotificationsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class SetAlarmNotificationsCommandExecutorTest {
    private DlmsDevice device;
    private CommandExecutor<AlarmNotificationsDto, AccessResultCode> executor;

    @Before
    public void setUp() {
        final DlmsObjectConfigConfiguration dlmsObjectConfigConfiguration = new DlmsObjectConfigConfiguration();
        final DlmsObjectConfigService dlmsObjectConfigService =
                new DlmsObjectConfigService(new DlmsHelper(), dlmsObjectConfigConfiguration.getDlmsObjectConfigs());

        this.device = new DlmsDevice("SuperAwesomeHeroicRockstarDevice");
        this.executor = new SetAlarmNotificationsCommandExecutor(dlmsObjectConfigService);
    }

    @Test
    public void testSetAlarmNotificationsCommandIsSent() throws OsgpException {
        final List<SetParameter> setParametersReceived = new ArrayList<>();
        final DlmsConnectionStub conn = new DlmsConnectionStub() {
            @Override
            public AccessResultCode set(final SetParameter setParameter) {
                setParametersReceived.add(setParameter);
                return AccessResultCode.SUCCESS;
            }
        };
        // Set the return value to 10 (0b1010):
        // REPLACE_BATTERY enabled, AUXILIARY_EVENT enabled.
        conn.addReturnValue(
                new AttributeAddress(1, "0.0.97.98.10.255", 2),
                DataObject.newInteger32Data(10));
        final DlmsConnectionManager connMgr = new DlmsConnectionManagerStub(conn);

        // Setting notifications that are not different from what is on the meter already,
        // should always be successful.
        AccessResultCode res1 = this.execute(connMgr,
                new AlarmNotificationDto(AlarmTypeDto.REPLACE_BATTERY, true));
        assertEquals(AccessResultCode.SUCCESS, res1);
        // Since nothing changed, not a single message should have been sent to the meter.
        assertEquals(0, setParametersReceived.size());

        // Now we enable something: CLOCK_INVALID to enabled.
        AccessResultCode res2 = this.execute(connMgr,
                new AlarmNotificationDto(AlarmTypeDto.CLOCK_INVALID, true));
        assertEquals(AccessResultCode.SUCCESS, res2);
        assertEquals(1, setParametersReceived.size());
        // Expecting 11 (0b1011).
        assertEquals(11, (long)setParametersReceived.get(0).getData().getValue());
        setParametersReceived.clear();

        // Now we enable and disable something: CLOCK_INVALID to enabled and REPLACE_BATTERY to disabled.
        AccessResultCode res3 = this.execute(connMgr,
                new AlarmNotificationDto(AlarmTypeDto.CLOCK_INVALID, true),
                new AlarmNotificationDto(AlarmTypeDto.REPLACE_BATTERY, false));
        assertEquals(AccessResultCode.SUCCESS, res2);
        assertEquals(1, setParametersReceived.size());
        // Expecting 9 (0b1001).
        assertEquals(9, (long)setParametersReceived.get(0).getData().getValue());
    }

    private AccessResultCode execute(final DlmsConnectionManager connMgr, AlarmNotificationDto... alarmNotificationDtos)
            throws OsgpException {
        final Set<AlarmNotificationDto> alarmNotificationDtoSet = new HashSet<>(Arrays.asList(alarmNotificationDtos));
        final AlarmNotificationsDto alarmNotificationsDto = new AlarmNotificationsDto(alarmNotificationDtoSet);
        return executor.execute(connMgr, this.device, alarmNotificationsDto);
    }
}
