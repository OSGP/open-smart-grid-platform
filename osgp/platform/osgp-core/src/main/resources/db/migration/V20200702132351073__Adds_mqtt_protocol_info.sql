DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   protocol_info
    WHERE  protocol = 'MQTT'
    AND    protocol_version  = '3.1.1') THEN

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
    '2020-07-02 00:00:00',
    '2020-07-02 00:00:00',
    0,
    'MQTT',
    '3.1.1',
    'jms.protocol.mqtt.outgoing.requests',
    'jms.protocol.mqtt.incoming.responses',
    'jms.protocol.mqtt.incoming.requests',
    'jms.protocol.mqtt.outgoing.responses',
    true);

END IF;

END;
$$
