DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   protocol_info
    WHERE  protocol = '60870-5-104'
    AND    protocol_version  = '1.0') THEN

INSERT INTO protocol_info(
    creation_time,
    modification_time,
    version,
    protocol,
    protocol_version,
    outgoing_protocol_requests_queue,
    incoming_protocol_responses_queue,
    incoming_protocol_requests_queue,
    outgoing_protocol_responses_queue,
    parallel_requests_allowed)
VALUES (
    '2019-01-16 00:00:00',
    '2019-01-16 00:00:00',
    0,
    '60870-5-104',
    '1.0',
    'protocol-iec60870.1_0.osgp-core.1_0.requests',
    'osgp-core.1_0.protocol-iec60870.1_0.responses',
    'osgp-core.1_0.protocol-iec60870.1_0.requests',
    'protocol-iec60870.1_0.osgp-core.1_0.responses',
    true);

END IF;

END;
$$
