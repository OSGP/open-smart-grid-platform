DO
$$
BEGIN

IF NOT EXISTS (
    SELECT 1
    FROM   protocol_info
    WHERE  protocol = 'DSMR'
    AND    protocol_version  = '4.2.2') THEN

INSERT INTO protocol_info(
    id,
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
    nextval('protocol_info_id_seq'),
    '2018-12-10 00:00:00',
    '2018-12-10 00:00:00',
    0,
    'SMR',
    '5.1',
    'protocol-dlms.1_0.osgp-core.1_0.requests',
    'osgp-core.1_0.protocol-dlms.1_0.responses',
    'osgp-core.1_0.protocol-dlms.1_0.requests',
    'protocol-dlms.1_0.osgp-core.1_0.responses',
    true);

END IF;

END;
$$
