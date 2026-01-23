DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   protocol_info
    WHERE  protocol = 'SMR'
    AND    protocol_version  = '5.2c') THEN

INSERT INTO protocol_info(
    creation_time,
    modification_time,
    protocol,
    protocol_version,
    outgoing_requests_property_prefix,
    incoming_responses_property_prefix,
    incoming_requests_property_prefix,
    outgoing_responses_property_prefix,
    parallel_requests_allowed)
VALUES (
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'SMR',
    '5.2c',
    'jms.protocol.dlms.outgoing.requests',
    'jms.protocol.dlms.incoming.responses',
    'jms.protocol.dlms.incoming.requests',
    'jms.protocol.dlms.outgoing.responses',
    true);

END IF;

END;
$$
