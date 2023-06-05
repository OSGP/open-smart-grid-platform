-- SPDX-FileCopyrightText: 2023 Contributors to the GXF project
-- SPDX-FileCopyrightText: Contributors to the GXF project
--
-- SPDX-License-Identifier: Apache-2.0

DO
$$
begin

if not exists (
    select 1
    from   protocol_info
    where  protocol = 'SMR_CDMA'
    and    protocol_version  = '5.1') then

insert into protocol_info(
    creation_time,
    modification_time,
    version,
    protocol,
    protocol_version,
    outgoing_requests_property_prefix,
    incoming_responses_property_prefix,
    incoming_requests_property_prefix,
    outgoing_responses_property_prefix,
    parallel_requests_allowed)
values (
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    0,
    'SMR_CDMA',
    '5.1',
    'jms.protocol.dlms.outgoing.cdma.requests',
    'jms.protocol.dlms.incoming.responses',
    'jms.protocol.dlms.incoming.requests',
    'jms.protocol.dlms.outgoing.responses',
    true);

end if;

if not exists (
    select 1
    from   protocol_info
    where  protocol = 'SMR_CDMA'
    and    protocol_version  = '5.0.0') then

insert into protocol_info(
    creation_time,
    modification_time,
    version,
    protocol,
    protocol_version,
    outgoing_requests_property_prefix,
    incoming_responses_property_prefix,
    incoming_requests_property_prefix,
    outgoing_responses_property_prefix,
    parallel_requests_allowed)
values (
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    0,
    'SMR_CDMA',
    '5.0.0',
    'jms.protocol.dlms.outgoing.cdma.requests',
    'jms.protocol.dlms.incoming.responses',
    'jms.protocol.dlms.incoming.requests',
    'jms.protocol.dlms.outgoing.responses',
    true);

end if;

if not exists (
    select 1
    from   protocol_info
    where  protocol = 'DSMR_CDMA'
    and    protocol_version  = '4.2.2') then

insert into protocol_info(
    creation_time,
    modification_time,
    version,
    protocol,
    protocol_version,
    outgoing_requests_property_prefix,
    incoming_responses_property_prefix,
    incoming_requests_property_prefix,
    outgoing_responses_property_prefix,
    parallel_requests_allowed)
values (
    '2020-05-12 00:00:00',
    '2020-05-12 00:00:00',
    0,
    'DSMR_CDMA',
    '4.2.2',
    'jms.protocol.dlms.outgoing.cdma.requests',
    'jms.protocol.dlms.incoming.responses',
    'jms.protocol.dlms.incoming.requests',
    'jms.protocol.dlms.outgoing.responses',
    true);

end if;
end;
$$
