DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   protocol_info
    WHERE  protocol = 'SMR'
    AND    protocol_version  = '5.5') THEN

INSERT INTO protocol_info(
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
VALUES (
    '2022-10-01 00:00:00',
    '2022-10-01 00:00:00',
    0,
    'SMR',
    '5.5',
    'jms.protocol.dlms.outgoing.requests',
    'jms.protocol.dlms.incoming.responses',
    'jms.protocol.dlms.incoming.requests',
    'jms.protocol.dlms.outgoing.responses',
    true);

END IF;

END;
$$
